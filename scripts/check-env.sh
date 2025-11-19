#!/bin/bash

# Script de verificação de ambiente
# Usa este script para verificar se tudo está configurado corretamente

echo "🔍 Verificando configuração do ambiente..."
echo ""

# Cores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

check_passed=0
check_failed=0

# Função para verificação
check() {
    if [ $1 -eq 0 ]; then
        echo -e "${GREEN}✅${NC} $2"
        ((check_passed++))
    else
        echo -e "${RED}❌${NC} $2"
        ((check_failed++))
    fi
}

# 1. Verificar Docker
echo "📦 Docker & Docker Compose"
docker --version > /dev/null 2>&1
check $? "Docker instalado"

docker-compose --version > /dev/null 2>&1
check $? "Docker Compose instalado"

docker ps > /dev/null 2>&1
check $? "Docker rodando"
echo ""

# 2. Verificar arquivos .env
echo "🔐 Arquivos .env"
[ -f .env ]
check $? ".env existe na raiz"

[ -f backend/.env ]
check $? "backend/.env existe"

if [ ! -f .env ]; then
    echo -e "${YELLOW}💡 Dica: cp .env.example .env${NC}"
fi

if [ ! -f backend/.env ]; then
    echo -e "${YELLOW}💡 Dica: cp backend/.env.example backend/.env${NC}"
fi
echo ""

# 3. Verificar se variáveis críticas estão definidas
if [ -f .env ]; then
    echo "🔑 Variáveis críticas (.env raiz)"
    
    grep -q "POSTGRES_PASSWORD" .env
    check $? "POSTGRES_PASSWORD definido"
    
    grep -q "JWT_SECRET" .env
    check $? "JWT_SECRET definido"
    
    # Verificar se JWT_SECRET foi alterado do padrão inseguro
    if grep -q "dev-secret-key-CHANGE-THIS" .env; then
        echo -e "${YELLOW}⚠️  JWT_SECRET ainda usa valor padrão (OK para dev, MUDE em produção!)${NC}"
    fi
    echo ""
fi

if [ -f backend/.env ]; then
    echo "🔑 Variáveis críticas (backend/.env)"
    
    grep -q "SPRING_PROFILES_ACTIVE" backend/.env
    check $? "SPRING_PROFILES_ACTIVE definido"
    
    grep -q "SPRING_DATASOURCE_URL" backend/.env
    check $? "SPRING_DATASOURCE_URL definido"
    echo ""
fi

# 4. Verificar .gitignore
echo "🙈 Segurança Git"
grep -q "^\.env$" .gitignore
check $? ".env no .gitignore"

grep -q "backend/\.env" .gitignore
check $? "backend/.env no .gitignore"
echo ""

# 5. Verificar containers rodando
echo "🐳 Containers (infraestrutura necessária)"
docker ps --format "{{.Names}}" | grep -q "repositorio-postgres"
check $? "PostgreSQL rodando"

docker ps --format "{{.Names}}" | grep -q "repositorio-mongodb"
check $? "MongoDB rodando"

docker ps --format "{{.Names}}" | grep -q "repositorio-redis"
check $? "Redis rodando"

if [ $? -ne 0 ]; then
    echo -e "${YELLOW}💡 Dica: docker-compose up -d postgres mongodb redis${NC}"
fi
echo ""

# 6. Verificar portas disponíveis
echo "🔌 Portas disponíveis"
! lsof -i:8080 > /dev/null 2>&1
check $? "Porta 8080 livre (backend)"

! lsof -i:4200 > /dev/null 2>&1
check $? "Porta 4200 livre (frontend)"

! lsof -i:5432 > /dev/null 2>&1
if [ $? -eq 0 ]; then
    # Verifica se é o container Docker
    if docker ps --format "{{.Names}}" | grep -q "repositorio-postgres"; then
        echo -e "${GREEN}✅${NC} Porta 5432 (PostgreSQL container)"
        ((check_passed++))
    else
        echo -e "${RED}❌${NC} Porta 5432 ocupada (não é o container)"
        ((check_failed++))
    fi
else
    echo -e "${YELLOW}⚠️${NC}  Porta 5432 livre (PostgreSQL não rodando)"
fi
echo ""

# 7. Verificar ferramentas de desenvolvimento
echo "🛠️  Ferramentas de desenvolvimento (opcionais para local)"
java -version > /dev/null 2>&1
check $? "Java instalado (necessário para dev local)"

mvn --version > /dev/null 2>&1
check $? "Maven instalado (necessário para dev local)"

node --version > /dev/null 2>&1
check $? "Node.js instalado (necessário para frontend)"

npm --version > /dev/null 2>&1
check $? "npm instalado (necessário para frontend)"
echo ""

# Resumo
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📊 RESUMO"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "${GREEN}✅ Passou: $check_passed${NC}"
echo -e "${RED}❌ Falhou: $check_failed${NC}"
echo ""

if [ $check_failed -eq 0 ]; then
    echo -e "${GREEN}🎉 Tudo pronto para desenvolvimento!${NC}"
    echo ""
    echo "Próximos passos:"
    echo "  1. Backend local:  cd backend && ./run-local.sh"
    echo "  2. Frontend:       cd frontend/repositorio-institucional && npm install && ng serve"
    echo "  3. Swagger UI:     http://localhost:8080/swagger-ui.html"
    exit 0
else
    echo -e "${YELLOW}⚠️  Alguns checks falharam. Revise acima.${NC}"
    echo ""
    echo "Ajuda rápida:"
    echo "  • Arquivos .env:   cp .env.example .env && cp backend/.env.example backend/.env"
    echo "  • Iniciar infra:   docker-compose up -d postgres mongodb redis"
    echo "  • Instalar Java:   https://adoptium.net/"
    echo "  • Instalar Node:   https://nodejs.org/"
    exit 1
fi
