# Scripts Operacionais

Esta pasta contém scripts para automação e operação do sistema.

## Scripts planejados

```text
scripts/
├── setup/
│   ├── up.sh              # Iniciar ambiente completo
│   ├── down.sh            # Parar todos os serviços
│   └── seed.sh            # Inserir dados iniciais
├── backup/
│   ├── backup.sh          # Script de backup manual
│   └── restore.sh         # Script de restauração
├── maintenance/
│   ├── cleanup.sh         # Limpeza de arquivos temporários
│   └── health-check.sh    # Verificação de saúde do sistema
└── deploy/
    ├── build.sh           # Build de todas as aplicações
    └── deploy.sh          # Deploy em produção
```

## Descrição dos scripts

- **Setup**: Scripts para inicialização e configuração do ambiente
- **Backup**: Scripts para backup e restauração de dados
- **Maintenance**: Scripts de manutenção e monitoramento
- **Deploy**: Scripts para build e deploy da aplicação

## Tecnologias

- Bash
- Docker
- Docker Compose
- PostgreSQL tools
- MongoDB tools
