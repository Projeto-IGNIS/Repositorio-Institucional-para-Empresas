#!/bin/bash

# Script to stop all services (keeps containers in VS Code)
echo "⏸️  Stopping Repositório Institucional..."
echo ""

docker-compose stop

echo ""
echo "✅ All services stopped!"
echo ""
echo "📋 Containers are stopped but still visible in VS Code"
echo "🔄 To start again: docker-compose start or ./up.sh"
echo "🗑️  To remove containers: ./down.sh"