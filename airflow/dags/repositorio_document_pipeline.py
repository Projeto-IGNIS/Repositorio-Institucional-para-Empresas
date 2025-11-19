"""
DAG - Monitoramento e Auditoria do Sistema
Gera relatórios de auditoria e monitora atividades suspeitas
"""
from datetime import datetime, timedelta
from airflow import DAG
from airflow.operators.python import PythonOperator
from airflow.operators.bash import BashOperator
from airflow.providers.postgres.hooks.postgres import PostgresHook
import logging
import json

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
    'system_audit_monitoring',
    default_args=default_args,
    description='Monitoramento de auditoria e análise de segurança',
    schedule_interval='0 */4 * * *',  # A cada 4 horas
    catchup=False,
    tags=['audit', 'security', 'monitoring'],
)

def analyze_user_activity():
    """Analisa atividade recente de usuários"""
    try:
        pg_hook = PostgresHook(postgres_conn_id='repositorio_postgres')
        conn = pg_hook.get_conn()
        cursor = conn.cursor()
        
        # Usuários ativos nas últimas 4 horas
        cursor.execute("""
            SELECT 
                u.username,
                COUNT(*) as action_count,
                MAX(al.timestamp) as last_action
            FROM audit_logs al
            JOIN users u ON al.user_id = u.id
            WHERE al.timestamp > NOW() - INTERVAL '4 hours'
            GROUP BY u.username
            ORDER BY action_count DESC
            LIMIT 10
        """)
        
        active_users = cursor.fetchall()
        logging.info(f"👥 {len(active_users)} usuários ativos nas últimas 4 horas")
        
        for user, count, last_action in active_users:
            logging.info(f"  - {user}: {count} ações (última: {last_action})")
        
        cursor.close()
        conn.close()
        
        return {'active_users': len(active_users)}
        
    except Exception as e:
        logging.error(f"❌ Erro ao analisar atividades: {str(e)}")
        return {'active_users': 0}

def detect_suspicious_activity():
    """Detecta atividades suspeitas"""
    try:
        pg_hook = PostgresHook(postgres_conn_id='repositorio_postgres')
        conn = pg_hook.get_conn()
        cursor = conn.cursor()
        
        suspicious_activities = []
        
        # 1. Múltiplas tentativas de login falhas
        cursor.execute("""
            SELECT user_id, COUNT(*) as failed_attempts
            FROM audit_logs
            WHERE action = 'LOGIN_FAILED'
            AND timestamp > NOW() - INTERVAL '1 hour'
            GROUP BY user_id
            HAVING COUNT(*) >= 3
        """)
        failed_logins = cursor.fetchall()
        
        if failed_logins:
            logging.warning(f"⚠️  {len(failed_logins)} usuários com múltiplas falhas de login")
            suspicious_activities.append({
                'type': 'multiple_failed_logins',
                'count': len(failed_logins)
            })
        
        # 2. Acessos de IPs desconhecidos
        cursor.execute("""
            SELECT DISTINCT ip_address, COUNT(*) as access_count
            FROM audit_logs
            WHERE timestamp > NOW() - INTERVAL '4 hours'
            AND ip_address IS NOT NULL
            GROUP BY ip_address
            HAVING COUNT(*) > 50
        """)
        high_activity_ips = cursor.fetchall()
        
        if high_activity_ips:
            logging.warning(f"⚠️  {len(high_activity_ips)} IPs com alta atividade")
            for ip, count in high_activity_ips:
                logging.warning(f"  - IP {ip}: {count} requisições")
            
            suspicious_activities.append({
                'type': 'high_activity_ips',
                'count': len(high_activity_ips)
            })
        
        # 3. Ações administrativas fora do horário
        cursor.execute("""
            SELECT u.username, al.action, al.timestamp
            FROM audit_logs al
            JOIN users u ON al.user_id = u.id
            WHERE al.action IN ('DELETE_DOCUMENTS', 'MANAGE_USERS', 'MANAGE_ROLES')
            AND EXTRACT(HOUR FROM al.timestamp) NOT BETWEEN 8 AND 18
            AND al.timestamp > NOW() - INTERVAL '4 hours'
        """)
        off_hours_admin = cursor.fetchall()
        
        if off_hours_admin:
            logging.warning(f"⚠️  {len(off_hours_admin)} ações administrativas fora do horário")
            for user, action, timestamp in off_hours_admin:
                logging.warning(f"  - {user} executou {action} em {timestamp}")
            
            suspicious_activities.append({
                'type': 'off_hours_admin_actions',
                'count': len(off_hours_admin)
            })
        
        cursor.close()
        conn.close()
        
        if suspicious_activities:
            logging.warning(f"🚨 Total: {len(suspicious_activities)} tipos de atividades suspeitas detectadas")
        else:
            logging.info("✅ Nenhuma atividade suspeita detectada")
        
        return {'suspicious_count': len(suspicious_activities), 'details': suspicious_activities}
        
    except Exception as e:
        logging.error(f"❌ Erro ao detectar atividades suspeitas: {str(e)}")
        return {'suspicious_count': 0, 'details': []}

def generate_audit_report():
    """Gera relatório de auditoria"""
    try:
        pg_hook = PostgresHook(postgres_conn_id='repositorio_postgres')
        conn = pg_hook.get_conn()
        cursor = conn.cursor()
        
        report = {
            'timestamp': datetime.now().isoformat(),
            'period': 'últimas 24 horas'
        }
        
        # Total de ações por tipo
        cursor.execute("""
            SELECT action, COUNT(*) as count
            FROM audit_logs
            WHERE timestamp > NOW() - INTERVAL '24 hours'
            GROUP BY action
            ORDER BY count DESC
        """)
        actions = cursor.fetchall()
        report['actions_by_type'] = {action: count for action, count in actions}
        
        # Total de usuários ativos
        cursor.execute("""
            SELECT COUNT(DISTINCT user_id)
            FROM audit_logs
            WHERE timestamp > NOW() - INTERVAL '24 hours'
        """)
        report['active_users'] = cursor.fetchone()[0]
        
        # Total de eventos
        cursor.execute("""
            SELECT COUNT(*)
            FROM audit_logs
            WHERE timestamp > NOW() - INTERVAL '24 hours'
        """)
        report['total_events'] = cursor.fetchone()[0]
        
        cursor.close()
        conn.close()
        
        logging.info("📊 Relatório de Auditoria:")
        logging.info(f"  Período: {report['period']}")
        logging.info(f"  Total de eventos: {report['total_events']}")
        logging.info(f"  Usuários ativos: {report['active_users']}")
        logging.info(f"  Ações registradas: {len(report['actions_by_type'])}")
        
        for action, count in list(report['actions_by_type'].items())[:5]:
            logging.info(f"    - {action}: {count}")
        
        return report
        
    except Exception as e:
        logging.error(f"❌ Erro ao gerar relatório: {str(e)}")
        return {}

def cleanup_old_logs():
    """Remove logs antigos (mais de 90 dias)"""
    try:
        pg_hook = PostgresHook(postgres_conn_id='repositorio_postgres')
        conn = pg_hook.get_conn()
        cursor = conn.cursor()
        
        cursor.execute("""
            DELETE FROM audit_logs
            WHERE timestamp < NOW() - INTERVAL '90 days'
        """)
        
        deleted_count = cursor.rowcount
        conn.commit()
        
        logging.info(f"🧹 {deleted_count} registros de log antigos removidos")
        
        cursor.close()
        conn.close()
        
        return {'deleted': deleted_count}
        
    except Exception as e:
        logging.error(f"❌ Erro ao limpar logs: {str(e)}")
        return {'deleted': 0}

# Definir as tarefas
task_analyze_activity = PythonOperator(
    task_id='analyze_user_activity',
    python_callable=analyze_user_activity,
    dag=dag,
)

task_detect_suspicious = PythonOperator(
    task_id='detect_suspicious_activity',
    python_callable=detect_suspicious_activity,
    dag=dag,
)

task_generate_report = PythonOperator(
    task_id='generate_audit_report',
    python_callable=generate_audit_report,
    dag=dag,
)

task_cleanup_logs = PythonOperator(
    task_id='cleanup_old_logs',
    python_callable=cleanup_old_logs,
    dag=dag,
)

task_check_database = BashOperator(
    task_id='check_database_size',
    bash_command="""
    echo "💾 Verificando tamanho do banco de dados:"
    docker exec repositorio-postgres psql -U postgres -d repositorio_db -c "
        SELECT 
            pg_size_pretty(pg_database_size('repositorio_db')) as db_size,
            (SELECT COUNT(*) FROM audit_logs) as log_count
    "
    """,
    dag=dag,
)

# Fluxo de execução paralelo para análises
task_analyze_activity >> task_generate_report
task_detect_suspicious >> task_generate_report
task_generate_report >> [task_cleanup_logs, task_check_database]
