#!/bin/bash

# Script to start all services
echo "🚀 Starting Repositório Institucional..."
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Build and start services
echo "📦 Building and starting services..."
docker-compose up --build -d

echo ""
echo "⏳ Waiting for services to be healthy..."
sleep 10

# Check service status
echo ""
echo "📊 Service Status:"
docker-compose ps

echo ""
echo "✅ All services started!"
echo ""
echo "🌐 Access URLs:"
echo "   - Backend API:     http://localhost:8080"
echo "   - Swagger UI:      http://localhost:8080/swagger-ui.html"
echo "   - Airflow UI:      http://localhost:8081 (admin/admin)"
echo "   - PostgreSQL:      localhost:5432"
echo "   - MongoDB:         localhost:27017"
echo "   - Redis:           localhost:6379"
echo ""
echo "📝 To view logs: docker-compose logs -f [service-name]"
echo "🛑 To stop: ./down.sh or docker-compose down"
