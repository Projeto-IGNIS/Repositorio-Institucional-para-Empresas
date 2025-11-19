# 📜 Scripts de Gerenciamento

Utilitários para gerenciar o ambiente de desenvolvimento e containers Docker.

## 🔍 check-env.sh

**Verifica se o ambiente está configurado corretamente**

```bash
./scripts/check-env.sh
```

Verifica:
- ✅ Docker e Docker Compose instalados
- ✅ Arquivos `.env` existem
- ✅ Variáveis críticas definidas
- ✅ `.env` no `.gitignore` (segurança)
- ✅ Containers de infraestrutura rodando
- ✅ Portas disponíveis
- ✅ Ferramentas de desenvolvimento (Java, Maven, Node)

**Use sempre** antes de começar a desenvolver!

---

## 🚀 up.sh

**Inicia todos os serviços pela primeira vez**

```bash
./scripts/up.sh
```

Executa:
- `docker-compose up -d --build`
- Rebuilda imagens se necessário
- Inicia todos os containers

**Quando usar**: Primeira vez ou após mudanças no Dockerfile/docker-compose.yml

---

## ▶️ start.sh

**Reinicia containers existentes (rápido)**

```bash
./scripts/start.sh
```

Executa:
- `docker-compose start`
- NÃO rebuilda imagens
- Apenas inicia containers parados

**Quando usar**: Containers já foram criados, só estão parados

---

## ⏸️ stop.sh

**Para containers (mantém no Docker Desktop)**

```bash
./scripts/stop.sh
```

Executa:
- `docker-compose stop`
- Para containers mas não remove
- Dados persistem nos volumes

**Quando usar**: Fim do dia, pausar desenvolvimento

---

## 🛑 down.sh

**Para e remove todos os containers**

```bash
./scripts/down.sh
```

Executa:
- `docker-compose down`
- Remove containers e redes
- Volumes persistem (dados salvos)

**Quando usar**: Limpeza completa, antes de rebuild

⚠️ **Atenção**: Para remover volumes também, use `docker-compose down -v` (PERDE DADOS!)

---

## 💾 backup.sh

**Faz backup dos bancos de dados**

```bash
./scripts/backup.sh
```

Cria backups em `backups/`:
- PostgreSQL: `postgres_backup_YYYYMMDD_HHMMSS.sql`
- MongoDB: `mongo_backup_YYYYMMDD_HHMMSS/`

**Quando usar**: Antes de operações arriscadas, periodicamente

---

## 🔄 restore.sh

**Restaura backup dos bancos de dados**

```bash
./scripts/restore.sh
```

Interativo: lista backups disponíveis e pede confirmação

**Quando usar**: Após erro, para restaurar dados anteriores

⚠️ **Atenção**: SOBRESCREVE dados atuais!

---

## 🔄 Fluxo de Desenvolvimento Típico

### Primeira vez (novo desenvolvedor)
```bash
# 1. Verificar ambiente
./scripts/check-env.sh

# 2. Corrigir problemas (se houver)
cp .env.example .env
cp backend/.env.example backend/.env

# 3. Iniciar tudo
./scripts/up.sh

# 4. Aguardar containers ficarem healthy
docker-compose ps
```

### Dia a dia normal
```bash
# Manhã: Iniciar infraestrutura
docker-compose up -d postgres mongodb redis

# Desenvolver: Backend local
cd backend
./run-local.sh

# Desenvolver: Frontend
cd frontend/repositorio-institucional
ng serve

# Fim do dia: Parar containers
./scripts/stop.sh
```

### Antes de commit/push
```bash
# 1. Backup (segurança)
./scripts/backup.sh

# 2. Testar no Docker
./scripts/up.sh

# 3. Verificar logs
docker-compose logs backend

# 4. Se OK, fazer commit
git add .
git commit -m "feat: nova funcionalidade"
git push
```

### Limpeza/Rebuild
```bash
# 1. Backup (sempre!)
./scripts/backup.sh

# 2. Parar e remover tudo
./scripts/down.sh

# 3. Limpar cache Docker (opcional)
docker system prune -a --volumes

# 4. Rebuild do zero
./scripts/up.sh
```

---

## 🆘 Troubleshooting

### Script não executa
```bash
# Dar permissão
chmod +x scripts/*.sh

# Verificar line endings (WSL/Windows)
dos2unix scripts/*.sh
```

### Containers não sobem
```bash
# Verificar logs
docker-compose logs -f

# Verificar portas ocupadas
lsof -i:8080
lsof -i:5432

# Matar processos se necessário
lsof -ti:8080 | xargs kill -9
```

### Backup falha
```bash
# Verificar se containers estão rodando
docker-compose ps

# Verificar permissões da pasta backups
ls -la backups/

# Criar pasta se não existir
mkdir -p backups
```

---

## 📖 Documentação Relacionada

- **Variáveis de ambiente**: [`docs/ENV-VARIABLES.md`](../docs/ENV-VARIABLES.md)
- **Tasks do VS Code**: [`docs/VSCODE-TASKS.md`](../docs/VSCODE-TASKS.md)
- **Desenvolvimento**: [`docs/DEVELOPMENT.md`](../docs/DEVELOPMENT.md)
- **Guia rápido**: [`docs/QUICKSTART.md`](../docs/QUICKSTART.md)
