#!/bin/bash
# Script para configurar conexões do Airflow

echo "🔧 Configurando conexões do Airflow..."

# Aguardar Airflow estar pronto
sleep 5

# Adicionar conexão PostgreSQL
docker exec repositorio-airflow-webserver airflow connections add 'repositorio_postgres' \
    --conn-type 'postgres' \
    --conn-login 'postgres' \
    --conn-password 'postgres' \
    --conn-host 'postgres' \
    --conn-port '5432' \
    --conn-schema 'repositorio_db' \
    || echo "⚠️  Conexão já existe ou erro ao criar"

# Adicionar conexão MongoDB
docker exec repositorio-airflow-webserver airflow connections add 'repositorio_mongodb' \
    --conn-type 'mongodb' \
    --conn-host 'mongodb' \
    --conn-port '27017' \
    --conn-schema 'repositorio_db' \
    || echo "⚠️  Conexão já existe ou erro ao criar"

echo "✅ Configuração concluída!"
echo ""
echo "📋 Conexões configuradas:"
docker exec repositorio-airflow-webserver airflow connections list
