#!/bin/bash
set -e

echo "Starting Infrastructure..."
cd infrastructure
docker compose up -d
cd ..

echo "Starting Backend..."
cd backend
export JAVA_HOME="$HOME/.sdkman/candidates/java/current"
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw spring-boot:run &
BACKEND_PID=$!
cd ..

echo "Starting AI Worker..."
cd ai-worker
source venv/bin/activate
python main.py &
WORKER_PID=$!
cd ..

echo "Starting Frontend..."
cd frontend
npm run dev &
FRONTEND_PID=$!
cd ..

echo "All services started! Press Ctrl+C to stop."

cleanup() {
    echo "Stopping services..."
    kill $BACKEND_PID $WORKER_PID $FRONTEND_PID
    cd infrastructure
    docker compose stop
    exit 0
}

trap cleanup SIGINT SIGTERM
wait
