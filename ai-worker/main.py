import json
import logging
import sys
import os
from confluent_kafka import Consumer, Producer, KafkaError
from langchain_cohere import ChatCohere
from langchain_core.messages import HumanMessage, SystemMessage
from langgraph.graph import StateGraph, START, END
from typing import TypedDict, List

logging.basicConfig(level=logging.INFO, format='%(asctime)s [%(levelname)s] %(message)s')
logger = logging.getLogger(__name__)

KAFKA_BROKER = 'localhost:9092'
TOPIC_UPLOADS = 'contract-uploads'
TOPIC_RESULTS = 'audit-results'
GROUP_ID = 'ai-worker-group'

class AuditState(TypedDict):
    contract_code: str
    audit_report: str
    vulnerabilities_found: int

def analyze_contract(state: AuditState):
    logger.info("Analyzing contract using Cohere...")
    contract_code = state["contract_code"]
    
    cohere_api_key = os.environ.get("COHERE_API_KEY")
    if not cohere_api_key:
        logger.warning("No COHERE_API_KEY found! Using fallback mock analysis.")
        return {
            "audit_report": "Mock Report: The contract seems mostly fine, but lacks reentrancy guards on the transfer function.",
            "vulnerabilities_found": 1
        }
    
    try:
        llm = ChatCohere(model="command-r-plus")
        messages = [
            SystemMessage(content="You are an expert Web3 Smart Contract Auditor. Analyze the following Solidity code and provide a short JSON report of vulnerabilities found. Include 'vulnerabilities_found' (integer) and 'audit_report' (string)."),
            HumanMessage(content=f"Code:\n{contract_code}")
        ]
        response = llm.invoke(messages)
        # Assuming the model returns text that we just pass as the report
        return {
            "audit_report": response.content,
            "vulnerabilities_found": 1 # Simplified for now
        }
    except Exception as e:
        logger.error(f"Error calling Cohere: {e}")
        return {
            "audit_report": f"Error during analysis: {str(e)}",
            "vulnerabilities_found": -1
        }

# Build LangGraph
workflow = StateGraph(AuditState)
workflow.add_node("analyze", analyze_contract)
workflow.add_edge(START, "analyze")
workflow.add_edge("analyze", END)
audit_app = workflow.compile()


def start_worker():
    conf_consumer = {
        'bootstrap.servers': KAFKA_BROKER,
        'group.id': GROUP_ID,
        'auto.offset.reset': 'earliest'
    }
    conf_producer = {
        'bootstrap.servers': KAFKA_BROKER
    }
    
    consumer = Consumer(conf_consumer)
    producer = Producer(conf_producer)
    consumer.subscribe([TOPIC_UPLOADS])
    
    logger.info(f"AI Worker (Cohere) listening on topic: {TOPIC_UPLOADS}")
    
    try:
        while True:
            msg = consumer.poll(timeout=1.0)
            if msg is None:
                continue
                
            if msg.error():
                if msg.error().code() == KafkaError._PARTITION_EOF:
                    continue
                else:
                    logger.error(f"Kafka Error: {msg.error()}")
                    break
                    
            value = msg.value().decode('utf-8')
            logger.info(f"Received message: {value}")
            
            try:
                data = json.loads(value)
                process_and_publish(data, producer)
            except json.JSONDecodeError as e:
                logger.error(f"Failed to decode message: {e}")
                
    except KeyboardInterrupt:
        logger.info("Worker interrupted by user, shutting down.")
    finally:
        consumer.close()

def process_and_publish(data, producer):
    audit_id = data.get('auditId', data.get('id'))
    contract_code = data.get('contractCode', '')
    
    logger.info(f"Running LangGraph for audit {audit_id}...")
    
    # Run LangGraph pipeline
    final_state = audit_app.invoke({"contract_code": contract_code})
    
    result_event = {
        "auditId": audit_id,
        "report": final_state.get("audit_report"),
        "vulnerabilities": final_state.get("vulnerabilities_found"),
        "status": "COMPLETED"
    }
    
    producer.produce(TOPIC_RESULTS, json.dumps(result_event).encode('utf-8'))
    producer.flush()
    logger.info(f"Published audit results for {audit_id} to {TOPIC_RESULTS}")

if __name__ == '__main__':
    start_worker()
