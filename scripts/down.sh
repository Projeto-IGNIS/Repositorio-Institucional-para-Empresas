#!/bin/bash

# Script to stop and remove all services
echo "🛑 Stopping and removing Repositório Institucional..."
echo ""

docker-compose down

echo ""
echo "✅ All services stopped and removed!"
echo ""
echo "💾 Data volumes are preserved. To remove them, run:"
echo "   docker-compose down -v"
