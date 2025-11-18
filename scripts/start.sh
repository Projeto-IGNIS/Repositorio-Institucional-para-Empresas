#!/bin/bash

# Script to start existing containers (fast restart)
echo "▶️  Starting Repositório Institucional..."
echo ""

# Check if containers exist
if [ -z "$(docker-compose ps -q)" ]; then
    echo "❌ No containers found. Run './up.sh' first to create them."
    exit 1
fi

# Start existing containers
docker-compose start

echo ""
echo "⏳ Waiting for services to start..."
sleep 5

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
echo ""
echo "📝 To view logs: docker-compose logs -f [service-name]"
echo "⏸️  To stop: ./stop.sh"