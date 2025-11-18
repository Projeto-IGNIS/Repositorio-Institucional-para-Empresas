# Roteiro de Implementação - MVP Repositório Institucional

**Projeto:** Repositorio-Institucional-para-Empresas  
**Data:** 17 de Novembro de 2025

---

## 📋 Etapa 1: Configuração Inicial do Ambiente

**Objetivo:** Preparar a infraestrutura base do projeto

- [ ] Criar repositório Git (estrutura monorepo ou separada)
- [ ] Configurar `.gitignore` para Java, Angular e Docker
- [ ] Criar estrutura de pastas: `/backend`, `/frontend`, `/airflow`, `/scripts`, `/docs`
- [ ] Configurar `docker-compose.yml` com serviços básicos (PostgreSQL, MongoDB, Redis)
- [ ] Criar arquivo `.env.example` com variáveis de ambiente
- [ ] Documentar README inicial com instruções de setup

**Entregável:** Estrutura base do projeto + Docker Compose funcional

---

## 📋 Etapa 2: Setup do Backend (Spring Boot)

**Objetivo:** Criar esqueleto da API REST

- [ ] Inicializar projeto Spring Boot (Spring Initializr ou CLI)
- [ ] Configurar dependências: Spring Web, Spring Security, Spring Data JPA, Spring Data MongoDB, Redis, JWT
- [ ] Configurar `application.yml` para múltiplos ambientes (dev, prod)
- [ ] Criar estrutura de pacotes: controller, service, repository, model, dto, config, security
- [ ] Configurar Swagger/OpenAPI (Springdoc)
- [ ] Criar Dockerfile para o backend
- [ ] Testar build e execução via Docker

**Entregável:** Backend rodando em container com Swagger acessível

---

## 📋 Etapa 3: Setup do Frontend (Angular)

**Objetivo:** Criar aplicação cliente SPA

- [ ] Inicializar projeto Angular 19
- [ ] Configurar estrutura de módulos e componentes base
- [ ] Instalar dependências: Angular Material, HttpClient, JWT, etc.
- [ ] Criar estrutura de pastas: components, services, guards, models, interceptors
- [ ] Configurar rotas principais (login, dashboard, documentos, usuários)
- [ ] Criar Dockerfile e nginx.conf para servir aplicação
- [ ] Integrar frontend ao docker-compose

**Entregável:** Frontend rodando em container acessível na porta 4200

---

## 📋 Etapa 4: Autenticação e Autorização (RF-001, RF-002, RF-003)

**Objetivo:** Implementar sistema de segurança

- [ ] Criar entidades: User, Role, Group
- [ ] Implementar hash de senhas (BCrypt)
- [ ] Criar endpoints de autenticação: `/api/auth/login`, `/api/auth/refresh`
- [ ] Implementar geração e validação de JWT
- [ ] Configurar Spring Security com filtros JWT
- [ ] Criar CRUD de usuários com controle de permissões
- [ ] Implementar sistema RBAC (roles e permissions)
- [ ] Criar telas de login e gestão de usuários no frontend
- [ ] Implementar Guards e Interceptors no Angular

**Entregável:** Autenticação funcional com controle de acesso

---

## 📋 Etapa 5: Hierarquia Organizacional (RF-004)

**Objetivo:** Modelar estrutura de setores

- [ ] Criar entidade Sector com auto-relacionamento (parent_id)
- [ ] Implementar lógica de herança de permissões
- [ ] Criar endpoints CRUD para setores: `/api/sectors`
- [ ] Implementar busca hierárquica e navegação em árvore
- [ ] Criar interface no frontend para gestão de setores
- [ ] Implementar validações de integridade (não permitir loops)

**Entregável:** Sistema de hierarquia de setores funcional

---

## 📋 Etapa 6: Upload e Versionamento de Documentos (RF-005, RF-006)

**Objetivo:** Gerenciar documentos e metadados

- [ ] Criar entidades: Document, DocumentVersion
- [ ] Configurar GridFS ou storage para arquivos binários
- [ ] Implementar endpoint de upload: `POST /api/documents`
- [ ] Implementar versionamento automático
- [ ] Criar schema de metadados flexível (JSON no MongoDB)
- [ ] Implementar validações: tamanho, tipo MIME, sanitização
- [ ] Criar tela de upload com formulário de metadados
- [ ] Implementar rollback de versões

**Entregável:** Sistema de upload e versionamento funcional

---

## 📋 Etapa 7: Pesquisa e Visualização (RF-007, RF-008)

**Objetivo:** Busca e acesso aos documentos

- [ ] Implementar endpoint de busca: `GET /api/documents` com filtros
- [ ] Criar índices no MongoDB para otimizar buscas
- [ ] Implementar paginação e ordenação
- [ ] Criar busca por metadados e full-text
- [ ] Implementar endpoint de download: `GET /api/documents/{id}/download`
- [ ] Adicionar controle de permissões para download
- [ ] Criar tela de busca avançada no frontend
- [ ] Implementar preview de documentos (PDF, imagens)

**Entregável:** Sistema de busca e visualização operacional

---

## 📋 Etapa 8: Sistema de Auditoria (RF-009)

**Objetivo:** Registrar ações críticas do sistema

- [ ] Criar entidade AuditLog
- [ ] Implementar interceptor/listener para eventos
- [ ] Registrar ações: login, logout, upload, download, alterações
- [ ] Criar endpoint de consulta: `GET /api/audit`
- [ ] Implementar filtros por usuário, ação, período
- [ ] Criar tela de visualização de logs para admin
- [ ] Implementar exportação de logs (CSV)

**Entregável:** Sistema de auditoria completo

---

## 📋 Etapa 9: Cache e Otimização (RF-012)

**Objetivo:** Melhorar performance com Redis

- [ ] Configurar Spring Cache com Redis
- [ ] Implementar cache em endpoints de leitura frequente
- [ ] Configurar TTL para diferentes tipos de dados
- [ ] Implementar Redis Pub/Sub para notificações
- [ ] Criar estratégia de invalidação de cache
- [ ] Testar ganhos de performance

**Entregável:** Sistema de cache funcional com melhorias mensuráveis

---

## 📋 Etapa 10: Configuração do Airflow (RF-015)

**Objetivo:** Orquestrar tarefas assíncronas

- [ ] Adicionar Airflow ao docker-compose (webserver, scheduler, worker)
- [ ] Configurar conexão do Airflow com PostgreSQL e MongoDB
- [ ] Criar DAG para backup automático (diário)
- [ ] Criar DAG para indexação de documentos
- [ ] Criar DAG para limpeza de arquivos temporários
- [ ] Criar DAG para geração de relatórios periódicos
- [ ] Documentar DAGs e configurações
- [ ] Testar execução e monitoramento via Airflow UI

**Entregável:** Workflows Airflow operacionais

---

## 📋 Etapa 11: Backup e Restore (RF-013)

**Objetivo:** Garantir recuperação de dados

- [ ] Criar script `backup.sh` (Postgres dump + Mongo dump)
- [ ] Criar script `restore.sh` para restauração
- [ ] Documentar procedimentos de backup/restore
- [ ] Integrar backup automático via Airflow
- [ ] Testar recuperação completa em ambiente limpo

**Entregável:** Sistema de backup/restore documentado e testado

---

## 📋 Etapa 12: Testes Automatizados (RF-014)

**Objetivo:** Garantir qualidade do código

- [ ] Configurar JUnit e Mockito no backend
- [ ] Criar testes unitários para serviços críticos
- [ ] Criar testes de integração para endpoints principais
- [ ] Configurar Jasmine/Karma no frontend
- [ ] Criar testes unitários de componentes Angular
- [ ] Atingir cobertura mínima de 60%
- [ ] Configurar execução de testes no CI/CD (opcional)

**Entregável:** Suite de testes com cobertura adequada

---

## 📋 Etapa 13: Documentação e Scripts Operacionais

**Objetivo:** Facilitar uso e manutenção

- [ ] Criar script `up.sh` para iniciar ambiente completo
- [ ] Criar script `down.sh` para parar serviços
- [ ] Criar script `seed.sh` com dados iniciais (admin, roles)
- [ ] Documentar variáveis de ambiente no `.env.example`
- [ ] Atualizar README com instruções completas
- [ ] Documentar APIs no Swagger com exemplos
- [ ] Criar guia de contribuição (CONTRIBUTING.md)
- [ ] Documentar arquitetura e fluxos principais

**Entregável:** Documentação completa e scripts operacionais

---

## 📋 Etapa 14: Polimento e Ajustes Finais

**Objetivo:** Preparar para entrega do MVP

- [ ] Revisar e ajustar todas as funcionalidades
- [ ] Corrigir bugs identificados
- [ ] Melhorar UX/UI do frontend
- [ ] Validar todos os critérios de aceitação
- [ ] Realizar testes de carga básicos
- [ ] Configurar logs e observability
- [ ] Preparar ambiente de demonstração
- [ ] Revisão final de código e documentação

**Entregável:** MVP completo e pronto para uso

---

## ✅ Critérios de Sucesso do MVP

- [ ] Sistema rodando completo com `docker-compose up --build`
- [ ] Todos os RFs de prioridade Alta implementados
- [ ] Swagger acessível e documentado
- [ ] Frontend funcional para operações principais
- [ ] Testes automatizados com cobertura mínima
- [ ] Documentação clara e completa
- [ ] Backup/restore funcionando
- [ ] Airflow com DAGs operacionais

---

**Nota:** Este roteiro é uma sugestão. Ajuste conforme as necessidades da equipe e prioridades do projeto.
