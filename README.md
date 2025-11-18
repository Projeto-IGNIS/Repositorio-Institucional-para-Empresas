# Repositório Institucional para Empresas (MVP)

Sistema web para armazenamento, gerenciamento e busca de documentos institucionais, com controle de acesso baseado em permissões, hierarquia organizacional e metadados flexíveis.

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

### Ambiente Completo com Docker

```bash
# Clone o repositório
git clone <repo-url>
cd Repositorio-Institucional-para-Empresas

# Configure as variáveis de ambiente
cp .env.example .env

# Inicie todos os serviços
docker-compose up --build

# Ou use o script auxiliar
./up.sh
```

### Acessos

- **Frontend:** http://localhost:4200
- **API Backend:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Airflow UI:** http://localhost:8081 (usuário: admin / senha: admin)
- **Adminer (PostgreSQL):** http://localhost:8082
- **Mongo Express:** http://localhost:8083

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
├── docker-compose.yml # Orquestração de serviços
├── scripts/           # Scripts operacionais
│   ├── up.sh
│   ├── down.sh
│   ├── backup.sh
│   └── seed.sh
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
