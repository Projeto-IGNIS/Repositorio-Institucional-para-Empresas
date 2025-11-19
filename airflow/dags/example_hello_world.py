"""
DAG - Backup Diário do Banco de Dados
Realiza backup automático do PostgreSQL e MongoDB
"""
from datetime import datetime, timedelta
from airflow import DAG
from airflow.operators.python import PythonOperator
from airflow.operators.bash import BashOperator
from airflow.providers.postgres.operators.postgres import PostgresOperator
from airflow.providers.postgres.hooks.postgres import PostgresHook
import logging

default_args = {
    'owner': 'repositorio-institucional',
    'depends_on_past': False,
    'start_date': datetime(2025, 11, 19),
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 2,
    'retry_delay': timedelta(minutes=5),
}

dag = DAG(
    'database_backup_daily',
    default_args=default_args,
    description='Backup automático diário dos bancos de dados',
    schedule_interval='0 3 * * *',  # 3 AM todos os dias
    catchup=False,
    tags=['backup', 'database', 'maintenance'],
)

def check_database_health():
    """Verifica a saúde do banco antes do backup"""
    try:
        pg_hook = PostgresHook(postgres_conn_id='repositorio_postgres')
        
        # Testa conexão
        conn = pg_hook.get_conn()
        cursor = conn.cursor()
        
        # Verifica tabelas principais
        cursor.execute("""
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'public' 
            AND table_type = 'BASE TABLE'
        """)
        tables = cursor.fetchall()
        
        logging.info(f"✅ Banco saudável - {len(tables)} tabelas encontradas")
        logging.info(f"Tabelas: {[t[0] for t in tables]}")
        
        cursor.close()
        conn.close()
        return {'status': 'healthy', 'tables': len(tables)}
        
    except Exception as e:
        logging.error(f"❌ Erro ao verificar banco: {str(e)}")
        raise

def get_database_stats():
    """Coleta estatísticas do banco"""
    try:
        pg_hook = PostgresHook(postgres_conn_id='repositorio_postgres')
        conn = pg_hook.get_conn()
        cursor = conn.cursor()
        
        # Conta registros principais
        stats = {}
        for table in ['users', 'roles', 'permissions', 'sectors', 'audit_logs']:
            cursor.execute(f"SELECT COUNT(*) FROM {table}")
            count = cursor.fetchone()[0]
            stats[table] = count
            logging.info(f"📊 {table}: {count} registros")
        
        cursor.close()
        conn.close()
        return stats
        
    except Exception as e:
        logging.error(f"❌ Erro ao coletar estatísticas: {str(e)}")
        return {}

# Tarefa 1: Verificar saúde do banco
task_health_check = PythonOperator(
    task_id='check_database_health',
    python_callable=check_database_health,
    dag=dag,
)

# Tarefa 2: Coletar estatísticas
task_stats = PythonOperator(
    task_id='collect_statistics',
    python_callable=get_database_stats,
    dag=dag,
)

# Tarefa 3: Backup PostgreSQL
task_backup_postgres = BashOperator(
    task_id='backup_postgresql',
    bash_command="""
    TIMESTAMP=$(date +%Y%m%d_%H%M%S)
    BACKUP_DIR=/opt/airflow/backups/postgres
    mkdir -p $BACKUP_DIR
    
    docker exec repositorio-postgres pg_dump -U postgres repositorio_db | gzip > $BACKUP_DIR/backup_$TIMESTAMP.sql.gz
    
    echo "✅ Backup PostgreSQL criado: backup_$TIMESTAMP.sql.gz"
    ls -lh $BACKUP_DIR/backup_$TIMESTAMP.sql.gz
    """,
    dag=dag,
)

# Tarefa 4: Backup MongoDB
task_backup_mongo = BashOperator(
    task_id='backup_mongodb',
    bash_command="""
    TIMESTAMP=$(date +%Y%m%d_%H%M%S)
    BACKUP_DIR=/opt/airflow/backups/mongodb
    mkdir -p $BACKUP_DIR
    
    docker exec repositorio-mongodb mongodump --db=repositorio_db --archive=$BACKUP_DIR/backup_$TIMESTAMP.archive --gzip
    
    echo "✅ Backup MongoDB criado: backup_$TIMESTAMP.archive"
    """,
    dag=dag,
)

# Tarefa 5: Limpar backups antigos (manter últimos 7 dias)
task_cleanup_old_backups = BashOperator(
    task_id='cleanup_old_backups',
    bash_command="""
    find /opt/airflow/backups/postgres -name "backup_*.sql.gz" -mtime +7 -delete
    find /opt/airflow/backups/mongodb -name "backup_*.archive" -mtime +7 -delete
    echo "🧹 Backups antigos removidos (mantidos últimos 7 dias)"
    """,
    dag=dag,
)

# Tarefa 6: Verificar espaço em disco
task_check_disk_space = BashOperator(
    task_id='check_disk_space',
    bash_command="""
    echo "💾 Espaço em disco:"
    df -h /opt/airflow/backups
    du -sh /opt/airflow/backups/*
    """,
    dag=dag,
)

# Fluxo de execução
task_health_check >> task_stats >> [task_backup_postgres, task_backup_mongo] >> task_cleanup_old_backups >> task_check_disk_space
