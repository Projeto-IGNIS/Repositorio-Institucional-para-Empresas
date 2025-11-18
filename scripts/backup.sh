#!/bin/bash

# Script to backup databases
BACKUP_DIR="./backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

echo "💾 Starting backup process..."
echo ""

# Create backup directory
mkdir -p $BACKUP_DIR

# Backup PostgreSQL
echo "📦 Backing up PostgreSQL..."
docker exec repositorio-postgres pg_dump -U postgres repositorio_db > "$BACKUP_DIR/postgres_backup_$TIMESTAMP.sql"

# Backup MongoDB
echo "📦 Backing up MongoDB..."
docker exec repositorio-mongodb mongodump --db repositorio_db --archive > "$BACKUP_DIR/mongodb_backup_$TIMESTAMP.archive"

echo ""
echo "✅ Backup completed!"
echo "   PostgreSQL: $BACKUP_DIR/postgres_backup_$TIMESTAMP.sql"
echo "   MongoDB:    $BACKUP_DIR/mongodb_backup_$TIMESTAMP.archive"
