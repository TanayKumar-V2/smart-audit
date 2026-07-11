# SmartAudit Platform

SmartAudit is an event-driven microservices platform for auditing Web3 smart contracts using AI. 

## Architecture & Tech Stack

This platform is composed of bleeding-edge technologies split across multiple microservices:
1. **Infrastructure**: PostgreSQL, Redis, and Apache Kafka (KRaft mode).
2. **Backend (Orchestrator)**: Spring Boot (Java 23+) REST API to receive audit requests, persist to Postgres, and publish events to Kafka.
3. **AI Worker**: Python 3.14+ Kafka consumer leveraging LangGraph to process smart contract code and simulate AI audits.
4. **Frontend**: Next.js 15 (App Router) web application featuring a stunning glassmorphic UI built with pure CSS.

## Monorepo Structure

- `/infrastructure`: `docker-compose.yml` for database, cache, and message broker.
- `/backend`: Spring Boot application.
- `/ai-worker`: Python virtual environment and consumer scripts.
- `/frontend`: Next.js web application.

## How to Run

1. **Start Infrastructure**:
   ```bash
   cd infrastructure
   docker compose up -d
   ```

2. **Start Backend**:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

3. **Start AI Worker**:
   ```bash
   cd ai-worker
   source venv/bin/activate
   python main.py
   ```

4. **Start Frontend**:
   ```bash
   cd frontend
   npm run dev
   ```

## Development

The frontend is accessible at `http://localhost:3000` and automatically proxies `/api` requests to the backend running on `http://localhost:8080`.
