# Airflow - Orquestração de Workflows

Esta pasta contém os DAGs e configurações do Apache Airflow para automação de tarefas.

## Estrutura planejada

```text
airflow/
├── dags/
│   ├── backup_dag.py
│   ├── indexing_dag.py
│   ├── cleanup_dag.py
│   └── reports_dag.py
├── plugins/
├── config/
│   └── airflow.cfg
└── docker-compose.airflow.yml
```

## DAGs planejados

- **Backup automático**: Backup diário de PostgreSQL e MongoDB
- **Indexação de documentos**: Reindexação periódica para busca
- **Limpeza de arquivos**: Remoção de arquivos temporários
- **Geração de relatórios**: Relatórios periódicos de uso do sistema

## Tecnologias

- Apache Airflow
- Python
- PostgreSQL
- MongoDB
- Docker
