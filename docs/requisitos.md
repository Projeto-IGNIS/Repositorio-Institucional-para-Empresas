# Especificação de Requisitos de Software (SRS)

**Nome do projeto:** Repositório Institucional para Empresas — MVP

**Data:** 17 de novembro de 2025

**Visão geral:**
Este documento especifica os requisitos para um MVP de um Repositório Institucional para empresas. O sistema deve permitir armazenar, organizar, pesquisar, controlar acesso e versionar documentos digitais em um ambiente corporativo com hierarquias de usuários, grupos, setores e múltiplos níveis de hierarquia tanto para setores quanto para documentos.

---

## 1. Escopo do produto

O produto será um sistema web (frontend Angular + backend Spring Boot) que possibilita:

* Gerenciamento centralizado de documentos (upload, download, preview, versionamento).
* Controle de acesso baseado em papéis (RBAC) e regras de permissões por documento, grupo e setor.
* Estruturas hierárquicas de setores e de documentos (coleções/pastas e subpastas) com herança de permissões.
* Suporte a metadados ricos e pesquisa avançada (full-text + filtros por metadados).
* Fluxos mínimos de colaboração (comentários, check-in/check-out, histórico de versões).
* Auditoria e logs de acesso/alteração.

**MVP**: foco nas funcionalidades essenciais para uso produtivo em uma empresa pequena/média. Funcionalidades avançadas (workflow de aprovação complexos, OCR em massa, integração SSO corporativo customizado, organização por timelines legais) ficam para fases posteriores.

---

## 2. Stakeholders

* Dono do produto / Sponsor
* Administrador do Sistema (IT)
* Gestor de Documentos / Bibliotecário Corporativo
* Usuários finais (colaboradores por setor)
* Auditores / Compliance
* Equipe de desenvolvimento / DevOps

---

## 3. Requisitos de alto nível

### 3.1 Requisitos funcionais prioritários (MVP)

1. Upload de documentos (um ou múltiplos) com metadados básicos (título, autor, setor, tags, data de validade).
2. Download e visualização prévia (preview) de documentos PDF, imagens e formatos comuns.
3. Versionamento de documentos com histórico e possibilidade de restaurar versões anteriores.
4. Autenticação e autorização (login com usuário/senha; suporte JWT; roles básicas: ADMIN, MANAGER, EDITOR, VIEWER).
5. Criação/gestão de usuários, grupos e setores; atribuição de permissões por usuário, grupo ou setor.
6. Hierarquia de setores (ex.: Empresa > Unidade > Departamento) com herança de políticas de acesso.
7. Estrutura de documentos em coleções/pastas com hierarquia e herança de permissões.
8. Pesquisar por texto completo (full-text) e filtros por metadados (data, setor, tags, autor).
9. Auditoria: registro de ações críticas (upload, download, delete, share, alterar permissões).
10. API RESTful documentada para operações principais (CRUD documentos, usuários, grupos, setores).
11. Cache de metadados e resultados de busca usando Redis.
12. Persistência mista: PostgreSQL para dados relacionais (usuários, permissões, setores, metadados estruturados) e MongoDB para documentos/binaries e/ou metadados flexíveis (ver seção de dados).
13. Dockerização completa (serviços individuais em containers) e orquestração via docker-compose para MVP.

### 3.2 Requisitos não-funcionais (MVP)

* Segurança: criptografia em trânsito (HTTPS/TLS), senhas armazenadas com hashing forte (bcrypt/argon2), proteção contra injeção, XSS e CSRF.
* Performance: tempo de resposta de API < 300 ms para operações de metadados; uploads/downloads escaláveis conforme banda.
* Escalabilidade: componentes desacoplados para escalonar backend, Redis, MongoDB/Postgres conforme demanda.
* Disponibilidade: meta inicial 99% (SLA para produção futura).
* Backup/Recovery: backup diário dos bancos; plano de restauração testado.
* Observabilidade: logs estruturados, métricas (Prometheus ou equivalente) e alertas básicos.
* Compatibilidade: suporte moderno de navegadores (Chrome, Edge, Firefox).
* Portabilidade: rodar localmente via Docker e em servidores Linux (Kubernetes opcional posterior).

---

## 4. Regras de negócio

* Permissões podem ser concedidas a um usuário individual, a um grupo, ou a um setor. Permissões por nível mais específico sobrescrevem as herdadas do nível superior.
* Permissões básicas: READ, WRITE, DELETE, SHARE, ADMIN.
* Documentos podem ter status: DRAFT, PUBLISHED, ARCHIVED. Apenas usuários com permissão podem mudar status.
* Documentos sensíveis podem receber marcação adicional (SENSITIVE) que exigirá permissões especiais e mais registro em auditoria.
* Expiração: documentos podem ter data de validade; após a data, o documento entra em estado EXPIRED e só pode ser reativado por ADMIN.
* Retenção legal: se um documento estiver sob retenção legal, não pode ser excluído até liberação.

---

## 5. Modelagem de Dados (visão conceitual)

### 5.1 Banco relacional — PostgreSQL (dados relacionais)

**Tabelas principais (exemplos):**

* users (id, username, email, password_hash, full_name, status, created_at, last_login)
* roles (id, name, description)
* user_roles (user_id, role_id)
* groups (id, name, description, owner_id)
* group_members (group_id, user_id, role_override)
* sectors (id, name, parent_id, path, description, metadata)
* permissions (id, target_type, target_id, principal_type, principal_id, permission_set, granted_by, granted_at)
* document_metadata (doc_id, title, author_id, sector_id, status, created_at, updated_at, tombstone_flag, tombstone_reason)
* audits (id, user_id, action, target_type, target_id, timestamp, details)

Observações: PostgreSQL armazena identidades, relações, roles, regras de permissão e metadados estruturados que exigem integridade referencial.

### 5.2 Banco documental — MongoDB (documentos e metadados flexíveis)

**Coleções sugeridas:**

* documents

  * _id, document_id (UUID), file_refs (array: refs a storage), metadata (flexível), versions (array), current_version, check_out_by, retention_flag, created_at
* file_storage (metadados de blobs) — se optar por guardar binários em GridFS ou referência a S3/MinIO

Observação: usar MongoDB para documentos com metadados sem esquema rígido e para armazenar históricos/versionamento complexo se desejado. Alternativa: armazenar blobs em S3/MinIO e metadados em MongoDB.

---

## 6. Arquitetura proposta

* **Frontend:** Angular (SPA) comunicando com backend via REST + endpoints para download/preview.
* **Backend:** Java + Spring Boot (módulos: auth, users, groups, sectors, documents, search, audit, api-gateway opcional)
* **BDs:** PostgreSQL (identidade, permissões, relacionais) + MongoDB (documentos, versões, metadados flexíveis)
* **Cache & Mensageria:** Redis para resultados de pesquisa, sessões curtas, metadados frequentemente consultados e mensageria (pub/sub, filas leves) para notificações em tempo real e processamento assíncrono.
* **Storage de arquivos:** GridFS (MongoDB) ou filesystem local para MVP (mais simples). Arquivos grandes devem ficar em storage apropriado, não em Postgres.
* **Docker:** containers para cada serviço (postgres, mongo, redis, minio, backend, frontend, nginx/proxy, adminer/pgadmin opcional).
* **Orquestração:** docker-compose para MVP; Kubernetes para produção futura.

Diagrama lógico (resumo):

```
[Angular SPA] <--HTTPS--> [Spring Boot API] <---> {Postgres, MongoDB, Redis}
```

### Esquema Visual Macro — Repositório Institucional (MVP)

```
┌──────────────────────────────────────────────────────────────────┐
│                        VISÃO GERAL DO SISTEMA                    │
└──────────────────────────────────────────────────────────────────┘

                   ┌──────────────────────────────────────────┐
                   │              Usuários                     │
                   │ (Admin · Manager · Editor · Viewer)       │
                   └──────────────────────────────────────────┘
                                   │
                                   ▼
┌──────────────────────────────────────────────────────────────────┐
│                           FRONTEND (Angular)                     │
│      - Login • Dashboard • Upload • Busca • Preview • Admin      │
└──────────────────────────────────────────────────────────────────┘
                                   │ HTTPS
                                   ▼
┌──────────────────────────────────────────────────────────────────┐
│                    BACKEND (Java + Spring Boot)                  │
│                                                                  │
│  Módulos principais:                                             │
│   • Auth & JWT                                                   │
│   • Gestão de Usuários, Grupos e Setores                         │
│   • Documentos (upload, download, versões, metadados)            │
│   • Permissões (RBAC + ACL)                                      │
│   • Busca (full-text + filtros)                                  │
│   • Auditoria                                                    │
│   • Cache Layer (Redis)                                          │
└──────────────────────────────────────────────────────────────────┘
                        │                 │                │
                        │                 │                │
                        ▼                 ▼                ▼

┌───────────────────────┐   ┌────────────────────────┐   ┌───────────────────────┐
│ PostgreSQL (Relacional)│   │    MongoDB (Documentos)│   │ Redis (Cache / PubSub)│
│─────────────────────── │   │────────────────────────│   │───────────────────────│
│ • Usuários             │   │ • BLOBs (GridFS ou refs)│  │ • Cache pesquisa       │
│ • Grupos               │   │ • Versões               │  │ • Cache metadados      │
│ • Setores (hierarquia) │   │ • Metadados flexíveis   │  │ • Sessões curtas       │
│ • Permissões (ACL)     │   │ • Histórico             │  │ • Mensageria leve      │
│ • Metadados básicos    │   └────────────────────────┘   └───────────────────────┘
│ • Auditoria            │
└───────────────────────┘

                     ┌──────────────────────────────────┐
                     │ Storage de Arquivos (MVP):        │
                     │ • GridFS OU filesystem local       │
                     └──────────────────────────────────┘


┌──────────────────────────────────────────────────────────────────┐
│                   CAMADAS DE INFRA / DOCKER                      │
│──────────────────────────────────────────────────────────────────│
│ • Containers independentes: frontend, backend, postgres, mongo,  │
│   redis, nginx.                                                   │
│ • docker-compose orquestra tudo.                                 │
│ • Volumes persistentes: pgdata, mongodata, filestorage.           │
└──────────────────────────────────────────────────────────────────┘


┌──────────────────────────────────────────────────────────────────┐
│                         FLUXO PRINCIPAL                           │
│──────────────────────────────────────────────────────────────────│
│ 1. Usuário autentica → Angular → Backend → JWT                   │
│ 2. Usuário envia documento → Backend → MongoDB/GridFS             │
│ 3. Metadados estruturados → PostgreSQL                            │
│ 4. Permissões avaliadas (RBAC + ACL + herança de setores)         │
│ 5. Busca → Backend → Redis(Cache) → Mongo/Postgres                │
│ 6. Auditoria registrada em PostgreSQL                             │
└──────────────────────────────────────────────────────────────────┘


┌──────────────────────────────────────────────────────────────────┐
│                        HIERARQUIAS DO SISTEMA                    │
│──────────────────────────────────────────────────────────────────│
│ **Setores**: Empresa → Unidade → Departamento → Subsetor         │
│ **Documentos**: Coleção → Pasta → Subpasta → Documento           │
│ **Permissões**: Setor > Grupo > Usuário (mais específico vence)   │
└──────────────────────────────────────────────────────────────────┘
```

---

## 7. API — Endpoints essenciais (REST)

> Observação: usar padrões REST + HATEOAS leve quando útil. Versão inicial: `/api/v1`.

### Autenticação

* `POST /api/v1/auth/login` -> {username, password} => JWT
* `POST /api/v1/auth/refresh` -> refresh token
* `POST /api/v1/auth/logout`

### Usuários / Grupos / Setores

* `GET /api/v1/users` (filtros)

* `POST /api/v1/users`

* `GET /api/v1/users/{id}`

* `PUT /api/v1/users/{id}`

* `DELETE /api/v1/users/{id}`

* `GET /api/v1/groups`

* `POST /api/v1/groups`

* `POST /api/v1/groups/{id}/members` (adicionar membro)

* `GET /api/v1/sectors`

* `POST /api/v1/sectors`

* `PUT /api/v1/sectors/{id}`

### Documentos

* `POST /api/v1/documents` (upload + metadados)
* `GET /api/v1/documents/{id}` (metadados)
* `GET /api/v1/documents/{id}/download` (stream)
* `GET /api/v1/documents/{id}/preview` (render/thumbnail)
* `PUT /api/v1/documents/{id}` (atualizar metadados)
* `DELETE /api/v1/documents/{id}` (marcação/excluir lógico)
* `POST /api/v1/documents/{id}/restore` (restaurar versão)
* `GET /api/v1/documents/{id}/versions`
* `POST /api/v1/documents/{id}/permissions` (definir permissões)

### Busca

* `GET /api/v1/search?q=...&filters=...` -> full-text + metadados

### Auditoria

* `GET /api/v1/audit?target_type=document&target_id=...`

---

## 8. Autenticação e autorização

* Autenticação: OAuth2 / JWT via Spring Security. MVP pode usar JWT + refresh tokens.
* Autorização: combinar **roles** (papéis globais) + **ACLs** (permissões por recurso). Implementar um interceptor que resolve permissões combinando: permissões explícitas por usuário > permissões por grupo > permissões por setor (herdadas).
* Senhas: hashing com bcrypt/argon2; política de senha configurável.
* Rate limiting: limite por IP/usuário em endpoints sensíveis (login, upload).

---

## 9. Cache e Performance

* Redis para cache de: consultas de busca recentes (query result caching), metadados de documentos muito acessados, sessões curtas.
* Redis também será usado para mensageria (pub/sub e filas leves) — útil para notificações em tempo real, invalidação de cache distribuída e tarefas assíncronas simples.
* TTLs curtos para evitar inconsistências (ex.: 5 minutos para resultados de busca; invalidar cache ao atualizar documento).
* Estratégia de cache: cache-aside — aplicação consulta Redis; se miss, consulta DB, grava cache.

---

## 10. Dockerização e infraestrutura (MVP)

### Estrutura mínima de containers

* backend (spring-boot) — expõe 8080
* frontend (Angular) — serve via nginx — expõe 80
* postgres — volume persistente
* mongo — volume persistente
* redis — volume de dados (opcional)
* armazenamento de arquivos (filesystem ou GridFS) — volume persistente (opcional)
* nginx/reverse-proxy — TLS (letsencrypt em produção futura)
* pgadmin/adminer — opcional para administração

### Arquivos sugeridos

* `Dockerfile` para backend (multi-stage): compilar artefatos e embalar jar/imagen.
* `Dockerfile` para frontend (build Angular -> copiar para nginx image)
* `docker-compose.yml` com serviços acima, networks e volumes.

Exemplo de serviços no `docker-compose.yml` (resumo):

```yaml
services:
  postgres:
    image: postgres:15
    volumes: - pgdata:/var/lib/postgresql/data
  mongo:
    image: mongo:6
    volumes: - mongodata:/data/db
  redis:
    image: redis:7
  # Para um MVP simples não é necessário o MinIO. Usar filesystem montado ou GridFS.
  backend:
    build: ./backend
    depends_on: [postgres, mongo, redis]
  frontend:
    build: ./frontend
    depends_on: [backend]
volumes:
  pgdata: {}
  mongodata: {}
```

### Configuração de rede e saúde

* Healthchecks para cada serviço.
* Variáveis de ambiente para conexões seguras e segredos injetados via `.env` ou secret manager.

---

## 11. Segurança e conformidade

* HTTPS obrigatório em produção; usar nginx como TLS termination.
* Proteção contra uploads maliciosos: validação de tipo MIME, limitações de tamanho, anti-virus scanning (opcional plugin/clamscan).
* Encrypt at rest: considerar criptografia de blobs no storage (MinIO + KMS) ou utilizar disco criptografado.
* Políticas de retenção e logs para auditoria (armazenar logs críticos por X anos conforme compliance).
* Conformidade GDPR/LGPD: registro de consentimentos (se aplicável) e possibilidade de requisições de exclusão/portabilidade.

---

## 12. Backup & Recovery

* Postgres: dump diário (pg_dump) + WAL archiving se necessário.
* MongoDB: mongodump diário ou snapshots, testar restauração periodicamente.
* Storage de objetos (quando usado): snapshots/replicação ou sincronização para storage externo.
* Automatizar backups via scripts em container separado e armazenar em local externo (S3 compatível).

---

## 13. Observabilidade e manutenção

* Logs: JSON-structured logs (stdout) para collection por ELK/EFK ou Loki.
* Métricas: endpoint `/actuator/prometheus` no backend; coletar com Prometheus; dashboards Grafana.
* Tracing: OpenTelemetry (opcional para MVP em rotas críticas).

---

## 14. Testes e Qualidade

* Unit tests (Junit + Mockito) para backend; cobertura mínima 70% para módulos críticos.
* Integration tests usando Testcontainers (Postgres, Mongo) para validar integrações.
* E2E tests para frontend (Cypress) cobrindo fluxos críticos: login, upload, busca, download.
* Security tests: scan de dependências (OWASP Dependency Check), SAST básico.

---

## 15. Implantação e CI/CD

* Pipeline CI (GitHub Actions / GitLab CI): build, run unit tests, build Docker images, push to registry (private), run integration tests.
* CD: deploy via docker-compose em servidor de staging/prod ou pipeline para Kubernetes (manifests/Helm) em entregas futuras.
* Migrations: Flyway (Postgres) integrado ao backend para controlar schema.

---

## 16. Requisitos de UX / UI (resumo MVP)

* Tela de login segura.
* Dashboard com atalhos: meus documentos, documentos do setor, buscas salvas.
* Tela de upload com metadados obrigatórios e opção de escolher pasta/coleção e setor.
* Visualização de documento com metadados laterais, versão e histórico.
* Painel administrativo para gestão de usuários, grupos, setores e permissões (lista + editor rápido).

---

## 17. Casos de uso (resumido)

1. **UC-01**: Colaborador faz upload de documento — adiciona metadados, define setor; sistema cria versão inicial e registra auditoria.
2. **UC-02**: Gestor configura permissões de um setor — herda para documentos não explicitamente configurados.
3. **UC-03**: Usuário busca documento por texto e filtros — resultados paginados, preview rápido.
4. **UC-04**: Administrador restaura versão antiga de documento.
5. **UC-05**: Auditor consulta log de acesso de documento sensível.

---

## 18. Critérios de aceitação do MVP

* Usuário autenticado consegue fazer upload, download e visualizar documento.
* É possível definir e aplicar permissões por setor e por documento; herança funciona corretamente (testes cobrindo cenários de conflito).
* Busca full-text retorna resultados esperados em menos de 1s em dataset pequeno (até 10k docs).
* Versionamento funciona: é possível recuperar versão anterior.
* Sistema roda via `docker-compose up` com todos os serviços e configurações mínimas.

---

## 19. Roadmap mínimo pós-MVP (sugestões)

* Integração SSO (SAML/LDAP/Okta)
* Workflows de aprovação e publicações programadas
* OCR e indexação de texto dentro de imagens/PDFs
* Políticas de retenção legal avançadas
* Multi-tenancy (se necessário)
* Migração para Kubernetes / autoscaling

---

## 20. Restrições e Assunções

* Assumimos que a organização fornecerá um domínio TLS e DNS em produção.
* Assumimos que o volume inicial será moderado (<100k documentos) e que o MVP não precisa de sharding imediato.
* Arquitetura proposta privilegia modularidade e facilidade de implantação via Docker.

---

## 21. Glossário

* ACL: Access Control List
* RBAC: Role-Based Access Control
* JWT: JSON Web Token
* S3 (ou alternativas locais como GridFS/filesystem): API compatível para armazenamento de objetos
* CRUD: Create, Read, Update, Delete

---

## 22. Anexos técnicos (exemplos rápidos)

### Exemplo: política de resolução de conflitos de permissão (resumo)

1. Permissão explícita no recurso (documento) tem precedência sobre permissão de grupo/setor.
2. Se existirem permissões conflitantes entre grupos do mesmo usuário, aplicar a permissão mais permissiva **apenas** se política assim determinar — por padrão, negar prevalece (deny-by-default) e permissões devem ser acumulativas.

### Exemplo: breve `Dockerfile` (backend)

```dockerfile
# stage 1: build
FROM maven:3.9.0-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package

# stage 2: run
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/app.jar ./app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

### Exemplo: snippet docker-compose (resumido)

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_PASSWORD: example
    volumes:
      - pgdata:/var/lib/postgresql/data
  mongo:
    image: mongo:6
    volumes:
      - mongodata:/data/db
  redis:
    image: redis:7
  # Removido serviço MinIO no exemplo simplificado. Use filesystem ou GridFS.
  backend:
    build: ./backend
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/app
    depends_on: [postgres, mongo, redis]
  frontend:
    build: ./frontend
    depends_on: [backend]
volumes:
  pgdata: {}
  mongodata: {}
```

---

### Observação final

Este documento foi preparado com foco em um MVP operacional. Posso gerar diagramas ER, fluxos de permissão detalhados, modelos de dados completos (DDL para Postgres e esquemas para MongoDB), especificações OpenAPI/Swagger para todas as rotas e wireframes de tela.

Se quiser, já crio: (escolha uma ou mais opções)

* DDL completo do PostgreSQL + migrations Flyway
* Schemas e exemplos de documentos para MongoDB
* Especificação OpenAPI (YAML/JSON)
* docker-compose completo pronto para rodar
* Estrutura de projeto Spring Boot (pom + packages + exemplos de controllers/repositories)
* Prototipagem de telas em HTML/CSS/Tailwind ou esqueleto Angular
