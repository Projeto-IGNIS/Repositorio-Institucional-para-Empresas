# Especificação de Requisitos de Software (SRS)

**Projeto:** Repositorio-Institucional-para-Empresas (MVP)

**Autores:** Vinicius Dias e equipe

**Data:** 17 de Novembro de 2025

---

# 1. Visão Geral

## 1.1 Propósito deste documento

Este documento descreve os requisitos funcionais e não funcionais para o desenvolvimento de um Repositório Institucional para Empresas (MVP). Tem como objetivo servir de guia para a equipe de desenvolvimento, testes e stakeholders, definindo o escopo mínimo viável, critérios de aceitação, arquitetura tecnológica e restrições.

## 1.2 Público-alvo

* Equipe de desenvolvimento (back-end, front-end, infraestrutura)
* QA / Testers
* Product owner / orientadores acadêmicos
* Usuários finais (administradores, gestores, colaboradores)

## 1.3 Visão do produto

Um sistema web (aplicação cliente + API REST) para armazenamento, gerenciamento e busca de documentos institucionais, com controle de acesso baseado em permissões e grupos, hierarquia organizacional para setores, metadados flexíveis (document-store) e alto grau de customização. Inspirado no DSpace, mas simplificado para MVP.

# 2. Tecnologias e requisitos de implantação

* **Linguagens / Frameworks**: Java (Spring Boot) no back-end; Angular no front-end.
* **Banco de dados relacional**: PostgreSQL — para dados relacionais (usuários, permissões, estruturas organizacionais, logs de auditoria, configurações globais).
* **Banco de documentos**: MongoDB — para armazenar documentos binários (metadados JSON, versões, índices dos metadados complexos).
* **Cache / Mensageria**: Redis — caching de leitura, sessões curtas e pub/sub para eventos (notificações internas / filas simples de processamento).
* **Orquestração de Workflows**: Apache Airflow — para agendamento e execução de tarefas assíncronas, pipelines de dados, backups automatizados e processamento em lote.
* **Containerização**: Docker (tudo dockerizado). Preferência por `docker-compose` para desenvolvimento local e `Dockerfile` e manifestos para orquestração (k8s) como opção.
* **API docs**: Swagger/OpenAPI (documentação completa das APIs).
* **Observability**: Logs centralizados (stdout + integração com ELK/Prometheus/Grafana como opcional), métricas expostas via Prometheus endpoint.
* **CI/CD**: pipelines opcionais (GitHub Actions / GitLab CI) — definidas em entregáveis.

# 3. Escopo do MVP

## 3.1 Funcionalidades obrigatórias (MVP)

1. Autenticação e Autorização (JWT + Refresh Tokens)
2. Gerenciamento de usuários: CRUD de usuários, associação a grupos e funções
3. Grupos e permissões customizáveis (RBAC) — definição de permissões por recurso e ação
4. Estrutura organizacional hierárquica (setores/departamentos) com herança de permissões
5. Upload e versionamento básico de documentos (PDF, DOCX, imagens, etc.)
6. Metadados flexíveis (schema JSON armazenado em MongoDB), campos customizáveis por administrador
7. Pesquisa básica e avançada: por texto completo, filtros por metadados, facetas
8. Visualização de metadados e download do documento (autorizado)
9. API REST documentada via Swagger para todos os endpoints públicos
10. Sistema de auditoria (logs de ações importantes: upload, download, alteração de permissões)
11. Docker + docker-compose para levantar todo o ambiente local (backend, frontend, postgres, mongo, redis)
12. Interface web em Angular para as operações principais (login, upload, busca, gestão de usuários e setores)
13. Backup/restore básico (scripts para export/import de dados)
14. Cache de leitura com Redis para acelerar endpoints de busca/metadados
15. Processamento assíncrono com Apache Airflow para tarefas agendadas (indexação, backups programados, limpeza de arquivos temporários)
16. Critérios de aceitação e testes automatizados básicos (unitários e integração mínima)

## 3.2 Funcionalidades desejáveis (pós-MVP / Stretch goals)

* Workflows de aprovação (submissão -> revisão -> publicação)
* Integração com LDAP/AD para autenticação corporativa
* Tagging colaborativo e anotações nos documentos
* Extração automática de texto (OCR) para imagens/PDFs e indexação
* Repositório de conteúdo multimídia (streaming com transcodificação)
* Integração com S3 / MinIO para storage de arquivos
* GUI para configuração de esquemas de metadados avançados
* Multitenancy (varias empresas/instâncias isoladas)

# 4. Requisitos Funcionais (RF)

Cada requisito funcional tem um identificador (RF-xxx), descrição, prioridades e critérios de aceitação.

## RF-001: Autenticação de Usuários

* **Descrição:** Usuários devem autenticar-se utilizando email/usuario e senha; suporte a JWT.
* **Prioridade:** Alta
* **Critérios de aceitação:** Usuário obtém token JWT com tempo de vida configurável; refresh token funcional; endpoints protegidos retornam 401 sem token.

## RF-002: Cadastro e Gerenciamento de Usuários

* **Descrição:** Admin pode criar, editar, desativar, deletar usuários.
* **Prioridade:** Alta
* **Critérios de aceitação:** CRUD funcional via API e UI; senha armazenada com hashing seguro (bcrypt/argon2).

## RF-003: Grupos e Papéis (Roles)

* **Descrição:** Sistema de RBAC: criação de grupos/papéis com conjunto de permissões (ler, criar, atualizar, deletar, publicar, administrar).
* **Prioridade:** Alta
* **Critérios de aceitação:** É possível associar usuários a múltiplos grupos; resolução de permissões respeita ocorrência mais permissiva por padrão (configurável).

## RF-004: Hierarquia de Setores

* **Descrição:** Modelar setores com hierarquia (ex: Empresa > Unidade > Departamento). Permissões podem ser definidas por setor e herdadas.
* **Prioridade:** Alta
* **Critérios de aceitação:** Criar/editar/deletar setores, mover setor na hierarquia, herança testada.

## RF-005: Upload de Documentos com Versão

* **Descrição:** Usuários com permissão podem enviar documentos; cada upload cria uma versão.
* **Prioridade:** Alta
* **Critério de aceitação:** Documento possui metadados básicos (titulo, autor, data, tags), versão incrementada; possibilidade de rollback para versão anterior.

## RF-006: Metadados Flexíveis e Schemas

* **Descrição:** Administradores definem campos de metadados (tipos: string, date, enum, boolean, número, referência) que serão armazenados no MongoDB como JSON.
* **Prioridade:** Alta
* **Critério de aceitação:** UI para criar/editar esquemas; documentos aceitam metadados customizados; validação básica de tipos.

## RF-007: Pesquisa e Facetas

* **Descrição:** Buscar documentos por texto, por metadados, com suporte a filtros (data, autor, setor), ordenação e paginação.
* **Prioridade:** Alta
* **Critério de aceitação:** Endpoints de busca retornam resultados consistentes; tempo de resposta aceitável para dataset de teste (ver NFR).

## RF-008: Visualização e Download

* **Descrição:** Visualizar metadados e baixar arquivo original (se autorizado).
* **Prioridade:** Alta
* **Critério de aceitação:** Download controlado por permissão; preview para PDF/imagens (in-browser) funcionando.

## RF-009: Auditoria de Eventos

* **Descrição:** Registrar ações críticas (login, logout, upload, download, alterações de permissão) em log persistente no PostgreSQL.
* **Prioridade:** Alta
* **Critério de aceitação:** Consultas de auditoria por administrador e exportação CSV.

## RF-010: API REST e Documentação Swagger

* **Descrição:** Todas as APIs devem estar documentadas com OpenAPI/Swagger e disponíveis em endpoint `/swagger-ui`.
* **Prioridade:** Alta
* **Critério de aceitação:** Documentação interativa gerada automaticamente a partir do código; exemplos de request/response.

## RF-011: Dockerização Completa

* **Descrição:** Projeto deve ser inicializável em ambiente local via `docker-compose up --build`.
* **Prioridade:** Alta
* **Critério de aceitação:** Um comando levanta Frontend, Backend, DBs e Redis; documentação com passos para replicação.

## RF-012: Cache com Redis

* **Descrição:** Implementar cache para endpoints de leitura frequente e usar Redis Pub/Sub para notificações internas.
* **Prioridade:** Média
* **Critério de aceitação:** Cache configurável por TTL; endpoints demonstram ganhos de latência em testes.

## RF-013: Backup/Restore

* **Descrição:** Scripts/documentação para backup e restauração básicos (Postgres dump, Mongo dump/restore, volumes do Docker)
* **Prioridade:** Média
* **Critério de aceitação:** Restauração bem-sucedida em ambiente local.

## RF-014: Testes Automatizados

* **Descrição:** Conjunto de testes unitários e de integração para as funcionalidades críticas.
* **Prioridade:** Alta
* **Critério de aceitação:** Cobertura mínima definida (ex.: 60% para serviços críticos) e pipeline que executa testes localmente.

## RF-015: Workflows Assíncronos com Airflow

* **Descrição:** Utilizar Apache Airflow para orquestrar tarefas assíncronas e agendadas (indexação de documentos, backups automáticos, processamento em lote, geração de relatórios).
* **Prioridade:** Alta
* **Critério de aceitação:** DAGs configuradas para tarefas principais; interface Airflow acessível; logs de execução persistidos; capacidade de reprocessamento manual de tarefas falhadas.

# 5. Requisitos Não Funcionais (RNF)

## RNF-001: Performance

* O sistema deverá responder a operações CRUD simples (metadados) em até 300ms em ambiente de desenvolvimento local; pesquisas simples em dataset de 10k documentos devem retornar primeira página em menos de 1.5s (objetivo para MVP).

## RNF-002: Escalabilidade

* Arquitetura desacoplada (serviço REST + DBs + cache) que permita escalar componentes individualmente (replicar backend, replicas de Mongo/Postgres e Redis cluster em produção).

## RNF-003: Segurança

* Senhas armazenadas com hashing forte (bcrypt/argon2), TLS obrigatório em produção, proteção contra CSRF para frontend, validação e sanitização de entradas (especialmente uploads). Proteção contra upload de arquivos maliciosos — checagem de tipos e limites de tamanho.

## RNF-004: Disponibilidade

* Sistema deve ter mecanismos de backup e recuperação; para produção, meta de disponibilidade 99.5% (fora do escopo do MVP completar infra de HA).

## RNF-005: Manutenibilidade

* Código com padrões claros, modular, documentação de API automática (Swagger), README para developers, scripts Docker para levantar ambiente.

## RNF-006: Portabilidade

* Todo o sistema deve ser executável via Docker em Windows, macOS e Linux com instruções claras.

## RNF-007: Observability

* Logs legíveis, endpoints de métricas Prometheus, instruções para integrar Grafana (opcional). Erros registrados com rastreamento de request-id (correlation id).

## RNF-008: Compatibilidade e Localização

* Interface em Português (pt-BR) com possibilidade de adicionar outras línguas.

## RNF-009: Conformidade Legal / Privacidade

* Armazenar apenas metadados necessários, fornecer razão para coleta de dados quando aplicável; GDPR-like considerations para PII (ex.: possibilidade de anonimizar registros em testes).

# 6. Arquitetura e Design de Alto Nível

## 6.1 Componentes principais

* **API Gateway / Backend (Spring Boot)**: Exposição de endpoints REST, autenticação/authorization, regras de negócio, integração com DBs.
* **Frontend (Angular)**: SPA com rotas para login, dashboard, busca, gestor de documentos, gestão de usuários e setores.
* **PostgreSQL**: dados relacionais (usuários, roles, setores, logs).
* **MongoDB**: armazenamento de metadados flexíveis e registros de documentos (JSON), possivelmente binários armazenados via GridFS ou referência a storage (volumes / S3).
* **Redis**: cache e pub/sub para eventos (ex.: notificar indexação, atualização de cache).
* **Apache Airflow**: orquestração de workflows assíncronos, agendamento de tarefas (backups, indexação, relatórios), monitoramento de pipelines de dados.
* **Docker Compose**: arquivo para orquestrar serviços em desenvolvimento.

## 6.2 Fluxo de Upload simplificado

1. Frontend envia arquivo + metadados para endpoint protegido.
2. Backend valida permissões, sanitiza metadados e salva binário (GridFS ou storage) e metadados no MongoDB.
3. Backend registra evento de auditoria no PostgreSQL.
4. Backend publica mensagem em Redis para notificação imediata.
5. Airflow processa tarefas assíncronas (indexação full-text, extração de metadados automáticos, geração de thumbnails).

## 6.3 Modelos de armazenamento

* **Usuários / Roles / Setores / Auditoria** — Postgres (tabelas normalizadas)
* **Documentos / Metadados ricos / versões** — MongoDB (documentos JSON), arquivos grandes via GridFS ou storage vinculado
* **Cache** — Redis (metadados de leitura frequente, tokens, sessões curtas)

# 7. Modelo de Dados (Visão resumida)

> Nesta seção apresentamos um resumo conceitual — implementar DDL/Schema no desenvolvimento.

* **User** (id, username, email, password_hash, active, created_at, updated_at)
* **Role** (id, name, permissions[])
* **Group** (id, name, members[])
* **Sector** (id, name, parent_sector_id, path, metadata_defaults)
* **Document** (id, title, owner_id, current_version_id, sector_id, metadata_json, created_at, updated_at)
* **DocumentVersion** (id, document_id, version_number, file_ref, checksum, size, mime_type, created_at)
* **AuditLog** (id, user_id, action, target_type, target_id, timestamp, extra_data JSON)

# 8. Interfaces (APIs e UI)

## 8.1 Regras para API

* Seguir padrões RESTful
* Utilizar HTTP status codes apropriados
* Paginação via `page`/`size` ou `cursor` (preferência cursor para grandes datasets)
* Filtragem e ordenação via query params
* Endpoints devem estar documentados por Swagger/OpenAPI e incluir exemplos

## 8.2 Endpoints principais (resumo)

* `POST /api/auth/login` — Autenticação
* `POST /api/auth/refresh` — Refresh token
* `GET /api/users`, `POST /api/users`, `PUT /api/users/{id}` — Gerenciamento de usuários
* `GET /api/roles`, `POST /api/roles` — Papéis e permissões
* `GET /api/sectors`, `POST /api/sectors` — Hierarquia de setores
* `POST /api/documents` — Upload
* `GET /api/documents` — Busca/pesquisa (query params)
* `GET /api/documents/{id}` — Metadados do documento
* `GET /api/documents/{id}/versions` — Versões
* `GET /api/documents/{id}/download` — Download do arquivo
* `POST /api/documents/{id}/rollback` — Reverter versão
* `GET /api/audit` — Logs de auditoria (admin)

> Observação: As rotas e verbos devem ser detalhadas no OpenAPI do projeto.

# 9. Requisitos de Segurança

* TLS/HTTPS obrigatório em produção.
* Hash de senhas com sal e algoritmo forte (bcrypt/argon2) e políticas de senha (comprimento mínimo configurable).
* Rate limiting para endpoints críticos (login, upload) — implementar via Redis token bucket.
* Sanitização de arquivos enviados, limite de tamanho por arquivo e validação MIME type.
* Controle de permissões por recurso e por ação (RBAC). Verificar o princípio do menor privilégio.
* Logs de segurança e auditoria preservados (tampão de retenção configurável).

# 10. Requisitos Operacionais e de Deploy

* `docker-compose.yml` com os serviços: backend, frontend, postgres, mongo, redis, airflow (webserver, scheduler, worker), adminer/mongo-express (opcionais), swagger UI já exposto no backend.
* Scripts na raiz: `up.sh`, `down.sh`, `backup.sh`, `restore.sh`, `seed.sh` (cria admin, roles iniciais e dados exemplo).
* `.env.example` com variáveis necessárias (DB URL, JWT secret, redis host, etc.).
* Documentação passo-a-passo para desenvolvedores: pré-requisitos (Docker, Docker Compose), `docker-compose up --build`, acessar UI em `http://localhost:4200`, Swagger `http://localhost:8080/swagger-ui.html`.

# 11. Testes e Critérios de Aceitação

* Testes unitários: serviços críticos (autenticação, upload e permissões) com cobertura mínima acordada.
* Testes de integração: endpoints principais com banco em memória ou containers (Postgres/Mongo). Automatizados com pipeline local.
* Testes de carga básicos: scripts para simular carga de leitura e uploads (k6 ou similar) para validar RNF-001.
* Critérios de aceitação do MVP: todos os RFs marcados como Alta devem estar implementados e testados; docker-compose funcional; API documentada no Swagger; UI funcional para as operações básicas.

# 12. Restrições e Assunções

* Projeto será inicialmente single-tenant (uma instância por empresa) — multitenancy fica para evolução.
* Armazenamento de binários pode ser feito em GridFS ou volume Docker; integração com S3 é opcional futura.
* Infra de produção (HA, backups remotos, TLS gerenciado, monitoramento completo) não é responsabilidade do MVP.
* Equipe possui conhecimento básico em Java/Spring Boot e Angular.

# 13. Plano de Entregas (sugestão de releases)

* **Release 0 (Kickoff / Infra)**: Esqueleto do projeto (monorepo ou dois repositórios), docker-compose minimal, configuração de DBs, README.
* **Release 1 (Core Auth & Users)**: Auth JWT, CRUD Users, Roles, Swagger.
* **Release 2 (Document Management)**: Upload, metadados, versões, download, auditoria.
* **Release 3 (Search & UI)**: Search, frontend básico, caching Redis.
* **Release 4 (Polimento)**: Backup/restore, testes, documentação, scripts de seed.

# 14. Entregáveis

* Código-fonte (Backend + Frontend)
* DAGs do Airflow (workflows configurados)
* `docker-compose.yml` e `Dockerfile`s
* Scripts operacionais (`up.sh`, `backup.sh`, etc.)
* OpenAPI/Swagger gerado
* Documentação de desenvolvedor (README, postman collection opcional, documentação de DAGs)
* Testes automatizados e relatórios

# 15. Critérios de Sucesso

* Reprodutibilidade: qualquer participante deve conseguir rodar o sistema localmente em até 10 minutos seguindo o README.
* Funcionalidade: RFs de prioridade Alta implementados e testados.
* Documentação: Swagger acessível e README claro.
* Código: estrutura limpa, modular e com testes básicos.

# 16. Sugestões de Implementação técnica (observações)

* Use Spring Security com JWT e roles/authorities implementadas via annotations ou filtros customizados.
* Utilize Spring Data JPA para Postgres e Spring Data MongoDB para Mongo.
* Para versionamento de arquivos, usar GridFS ou uma solução que armazene arquivos em volumes e metadata no Mongo; manter checksum.
* Para pesquisa full-text considerar ElasticSearch ou usar text indexes do Mongo (para MVP, usar text index do Mongo e filtros no Postgres se necessário).
* Use MapStruct para mapeamento DTOs, Lombok para reduzir boilerplate (opcional), e Flyway/Liquibase para migrações de schema PostgreSQL.
* Swagger/OpenAPI com Springdoc (springdoc-openapi) para gerar documentação automaticamente.
* Para Airflow, criar DAGs para: backups diários (Postgres/Mongo), indexação de documentos novos, limpeza de arquivos temporários, geração de relatórios semanais. Integrar com backend via API REST para disparar tarefas.
* Airflow pode usar PostgreSQL como metadata database (compartilhado ou separado) e CeleryExecutor com Redis como message broker para execução distribuída.

# 17. Riscos

* Complexidade de manter dois bancos (relacional + documental) e garantia de consistência.
* Tempo gasto desenhando esquema de metadados altamente flexível.
* Questões de segurança relacionadas a uploads e armazenamento de PII.

# 18. Próximos Passos Imediatos

1. Revisão e aprovação deste SRS pela equipe.
2. Criar repositório e estrutura inicial (backend skeleton + frontend skeleton).
3. Criar `docker-compose` com serviços mínimos e script de seed para um usuário admin.
4. Implementar autenticacao (RF-001) e documentar via Swagger.

---

*FIM do documento.*
