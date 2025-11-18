#!/bin/bash

# Script to restore databases from backup
if [ -z "$1" ] || [ -z "$2" ]; then
    echo "Usage: ./restore.sh <postgres_backup_file> <mongodb_backup_file>"
    echo "Example: ./restore.sh backups/postgres_backup_20251118.sql backups/mongodb_backup_20251118.archive"
    exit 1
fi

POSTGRES_BACKUP=$1
MONGODB_BACKUP=$2

echo "🔄 Starting restore process..."
echo ""

# Restore PostgreSQL
if [ -f "$POSTGRES_BACKUP" ]; then
    echo "📥 Restoring PostgreSQL from $POSTGRES_BACKUP..."
    docker exec -i repositorio-postgres psql -U postgres -d repositorio_db < "$POSTGRES_BACKUP"
else
    echo "❌ PostgreSQL backup file not found: $POSTGRES_BACKUP"
    exit 1
fi

# Restore MongoDB
if [ -f "$MONGODB_BACKUP" ]; then
    echo "📥 Restoring MongoDB from $MONGODB_BACKUP..."
    docker exec -i repositorio-mongodb mongorestore --db repositorio_db --archive < "$MONGODB_BACKUP"
else
    echo "❌ MongoDB backup file not found: $MONGODB_BACKUP"
    exit 1
fi

echo ""
echo "✅ Restore completed!"
