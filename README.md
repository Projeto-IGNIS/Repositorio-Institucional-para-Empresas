# Repositório Institucional para Empresas (MVP)

Sistema web para armazenamento, gerenciamento e busca de documentos institucionais, com controle de acesso baseado em permissões, hierarquia organizacional e metadados flexíveis.

## 🔐 IMPORTANTE: Configuração de Variáveis de Ambiente

**Antes de iniciar**, configure os arquivos `.env`:

```bash
# 1. Copie os templates
cp .env.example .env
cp backend/.env.example backend/.env

# 2. Verifique se tudo está configurado corretamente
./scripts/check-env.sh
```
---

## 🚀 Tecnologias

- **Frontend:** Angular 19
- **Backend:** Java Spring Boot
- **Banco de Dados:** PostgreSQL (relacional) + MongoDB (documentos)
- **Cache/Mensageria:** Redis
- **Orquestração de Workflows:** Apache Airflow
- **Containerização:** Docker + Docker Compose
- **Documentação API:** Swagger/OpenAPI

## 📋 Funcionalidades Principais

- ✅ Autenticação JWT com refresh tokens
- ✅ Gerenciamento de usuários, grupos e permissões (RBAC)
- ✅ Hierarquia organizacional de setores
- ✅ Upload e versionamento de documentos
- ✅ Metadados customizáveis (schema JSON)
- ✅ Pesquisa avançada com filtros e facetas
- ✅ Sistema de auditoria completo
- ✅ Processamento assíncrono com Airflow (indexação, transformações, backups)
- ✅ Cache inteligente com Redis

## 🛠️ Pré-requisitos

- Docker (20.10+)
- Docker Compose (2.0+)
- Node.js 18+ (para desenvolvimento local do frontend)
- JDK 17+ (para desenvolvimento local do backend)

## 🏃 Iniciando o Projeto

### Setup Rápido (Novo Desenvolvedor)

```bash
# 1. Clone o repositório
git clone <repo-url>
cd Repositorio-Institucional-para-Empresas

# 2. Configure variáveis de ambiente
cp .env.example .env
cp backend/.env.example backend/.env

# 3. Verifique se tudo está configurado
./scripts/check-env.sh

# 4. Suba todos os serviços (pode demorar um pouco)
./scripts/up.sh

# 5. Aguarde todos os containers ficarem healthy
docker-compose ps

# 6. Teste se está funcionando
curl http://localhost:8080/actuator/health
# Deve retornar: {"status":"UP"}
```

**Pronto!** Todos os serviços estarão rodando. Não precisa de arquivo `.env` para desenvolvimento local.

### Acessos

- **API Backend:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Airflow UI:** http://localhost:8081 (user: `admin` / pass: `admin`)
- **Health Check:** http://localhost:8080/actuator/health

### Credenciais Padrão

**Admin do Sistema:**
- Username: `admin`
- Password: `admin123`

### Comandos Úteis

```bash
# Scripts de gerenciamento
./scripts/up.sh      # Primeira vez / rebuild após mudanças
./scripts/start.sh   # Reiniciar containers existentes (rápido)
./scripts/stop.sh    # Pausar (mantém containers visíveis)
./scripts/down.sh    # Parar e remover containers

# Backup e restore
./scripts/backup.sh  # Criar backup dos bancos
./scripts/restore.sh <postgres_file> <mongo_file>

# Docker direto
docker-compose ps    # Ver status dos containers
docker-compose logs -f backend  # Ver logs do backend
```

### Desenvolvimento Local - Frontend

```bash
cd frontend
npm install
ng serve
```

### Desenvolvimento Local - Backend

```bash
cd backend
./mvnw spring-boot:run
```

## 📁 Estrutura do Projeto

```
├── backend/           # API Spring Boot
├── frontend/          # Aplicação Angular
├── airflow/           # DAGs e configurações do Airflow
├── scripts/           # Scripts operacionais
│   ├── up.sh          # Iniciar (primeira vez/rebuild)
│   ├── start.sh       # Reiniciar containers existentes
│   ├── stop.sh        # Pausar (mantém containers)
│   ├── down.sh        # Parar e remover containers
│   ├── backup.sh      # Backup dos bancos
│   └── restore.sh     # Restaurar backup
├── docker-compose.yml # Orquestração de serviços
└── docs/              # Documentação adicional
```

## 🔐 Segurança

- Senhas com hashing bcrypt/argon2
- TLS/HTTPS obrigatório em produção
- Rate limiting em endpoints críticos
- Validação e sanitização de uploads
- RBAC com princípio do menor privilégio

## 🧪 Testes

```bash
# Testes unitários backend
./mvnw test

# Testes unitários frontend
ng test

# Testes de integração
./mvnw verify
```

## 📦 Backup e Restore

```bash
# Criar backup
./scripts/backup.sh

# Restaurar backup
./scripts/restore.sh <backup-file>
```

## 🔄 Workflows Airflow

O Airflow gerencia processos assíncronos como:

- Indexação de documentos para busca
- Extração de metadados automáticos
- Backups programados
- Processamento de arquivos em lote
- Geração de relatórios periódicos

Acesse o Airflow UI para monitorar e gerenciar os workflows.

## 📚 Documentação

- [Especificação de Requisitos (SRS)](documento_requisitos.md)
- [Roteiro de Implementação](roteiro_de_implementacao.md) - Guia passo a passo para desenvolvimento
- [Guia de Desenvolvimento](docs/DEVELOPMENT.md)
- [API Documentation](http://localhost:8080/swagger-ui.html) (com aplicação rodando)

## 🤝 Contribuindo

1. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
2. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
3. Push para a branch (`git push origin feature/nova-funcionalidade`)
4. Abra um Pull Request

## 📄 Licença

Este projeto tem como objetivo o estudo das tecnologias propostas e o aprendizado compartilhado pela equipe Projeto IGNIS.

## 👥 Autores

- Vinicius Dias e equipe
- Projeto IGNIS - Novembro 2025

---

**Nota:** Este é um MVP (Minimum Viable Product). Funcionalidades como workflows de aprovação, integração LDAP, OCR e multitenancy estão planejadas para versões futuras.
