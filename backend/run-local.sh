#!/bin/bash

# Script para rodar o backend LOCALMENTE (desenvolvimento)

echo "🚀 Iniciando backend em modo DESENVOLVIMENTO LOCAL..."
echo ""

# Verifica se está no diretório correto
if [ ! -f "pom.xml" ]; then
    echo "❌ Erro: Execute este script de dentro do diretório 'backend'"
    exit 1
fi

# Verifica se .env existe
if [ ! -f ".env" ]; then
    echo "⚠️  Arquivo .env não encontrado!"
    echo "📝 Criando .env a partir do .env.example..."
    if [ -f ".env.example" ]; then
        cp .env.example .env
        echo "✅ Arquivo .env criado! Revise as configurações se necessário."
    else
        echo "❌ Erro: .env.example não encontrado!"
        exit 1
    fi
fi

# Verifica se os containers de infra estão rodando
echo "🔍 Verificando containers de infraestrutura..."
if ! docker ps | grep -q "repositorio-postgres"; then
    echo "⚠️  PostgreSQL não está rodando. Iniciando..."
    cd .. && docker-compose up -d postgres && cd backend
fi

if ! docker ps | grep -q "repositorio-mongodb"; then
    echo "⚠️  MongoDB não está rodando. Iniciando..."
    cd .. && docker-compose up -d mongodb && cd backend
fi

if ! docker ps | grep -q "repositorio-redis"; then
    echo "⚠️  Redis não está rodando. Iniciando..."
    cd .. && docker-compose up -d redis && cd backend
fi

echo ""
echo "✅ Containers de infraestrutura prontos!"
echo ""
echo "📦 Compilando e iniciando aplicação..."
echo "🔐 Carregando variáveis do arquivo .env"
echo "🌐 Acesso: http://localhost:8080"
echo "📚 Swagger: http://localhost:8080/swagger-ui.html"
echo ""
echo "💡 Dica: Mudanças no código serão recarregadas automaticamente!"
echo ""
echo "----------------------------------------"
echo ""

# Inicia a aplicação (Maven carrega .env automaticamente via plugin)
mvn spring-boot:run
