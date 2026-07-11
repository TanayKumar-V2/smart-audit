import json
import logging
import sys
from confluent_kafka import Consumer, KafkaError

logging.basicConfig(level=logging.INFO, format='%(asctime)s [%(levelname)s] %(message)s')
logger = logging.getLogger(__name__)

KAFKA_BROKER = 'localhost:9092'
TOPIC = 'contract-uploads'
GROUP_ID = 'ai-worker-group'

def start_worker():
    conf = {
        'bootstrap.servers': KAFKA_BROKER,
        'group.id': GROUP_ID,
        'auto.offset.reset': 'earliest'
    }
    
    consumer = Consumer(conf)
    consumer.subscribe([TOPIC])
    logger.info(f"AI Worker listening on topic: {TOPIC} at {KAFKA_BROKER}")
    
    try:
        while True:
            msg = consumer.poll(timeout=1.0)
            if msg is None:
                continue
                
            if msg.error():
                if msg.error().code() == KafkaError._PARTITION_EOF:
                    # End of partition event
                    continue
                else:
                    logger.error(f"Kafka Error: {msg.error()}")
                    break
                    
            value = msg.value().decode('utf-8')
            logger.info(f"Received message: {value}")
            
            try:
                data = json.loads(value)
                process_audit_request(data)
            except json.JSONDecodeError as e:
                logger.error(f"Failed to decode message: {e}")
                
    except KeyboardInterrupt:
        logger.info("Worker interrupted by user, shutting down.")
    finally:
        consumer.close()

def process_audit_request(data):
    """
    Placeholder for LangGraph-based smart contract audit process.
    """
    logger.info(f"Processing audit for project: {data.get('projectName')}")
    audit_id = data.get('auditId')
    contract_code = data.get('contractCode')
    
    # TODO: Integrate LangGraph workflow here.
    logger.info(f"Simulating LangGraph execution for audit {audit_id}...")
    logger.info("Audit completed successfully.")

if __name__ == '__main__':
    start_worker()
