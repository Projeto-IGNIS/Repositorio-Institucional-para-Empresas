"""
DAG - Manutenção de Usuários e Permissões
Gerenciamento automático de usuários inativos e limpeza de sessões
"""
from datetime import datetime, timedelta
from airflow import DAG
from airflow.operators.python import PythonOperator
from airflow.providers.postgres.hooks.postgres import PostgresHook
import logging

default_args = {
    'owner': 'repositorio-institucional',
    'depends_on_past': False,
    'start_date': datetime(2025, 11, 19),
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

dag = DAG(
    'user_maintenance',
    default_args=default_args,
    description='Manutenção automática de usuários e permissões',
    schedule_interval='0 1 * * 0',  # Domingo às 1h da manhã
    catchup=False,
    tags=['users', 'maintenance', 'cleanup'],
)

def identify_inactive_users():
    """Identifica usuários inativos há mais de 90 dias"""
    try:
        pg_hook = PostgresHook(postgres_conn_id='repositorio_postgres')
        conn = pg_hook.get_conn()
        cursor = conn.cursor()
        
        cursor.execute("""
            SELECT 
                u.id,
                u.username,
                u.email,
                u.updated_at,
                COALESCE(MAX(al.timestamp), u.created_at) as last_activity
            FROM users u
            LEFT JOIN audit_logs al ON u.id = al.user_id
            WHERE u.active = true
            GROUP BY u.id, u.username, u.email, u.updated_at, u.created_at
            HAVING COALESCE(MAX(al.timestamp), u.created_at) < NOW() - INTERVAL '90 days'
            ORDER BY last_activity
        """)
        
        inactive_users = cursor.fetchall()
        
        if inactive_users:
            logging.warning(f"⚠️  {len(inactive_users)} usuários inativos encontrados:")
            for user_id, username, email, updated, last_activity in inactive_users:
                days_inactive = (datetime.now() - last_activity).days
                logging.warning(f"  - {username} ({email}): {days_inactive} dias inativo")
        else:
            logging.info("✅ Nenhum usuário inativo encontrado")
        
        cursor.close()
        conn.close()
        
        return {'inactive_count': len(inactive_users)}
        
    except Exception as e:
        logging.error(f"❌ Erro ao identificar usuários inativos: {str(e)}")
        return {'inactive_count': 0}

def generate_user_report():
    """Gera relatório consolidado de usuários"""
    try:
        pg_hook = PostgresHook(postgres_conn_id='repositorio_postgres')
        conn = pg_hook.get_conn()
        cursor = conn.cursor()
        
        report = {}
        
        # Total de usuários
        cursor.execute("SELECT COUNT(*) FROM users WHERE active = true")
        report['total_active'] = cursor.fetchone()[0]
        
        cursor.execute("SELECT COUNT(*) FROM users WHERE active = false")
        report['total_inactive'] = cursor.fetchone()[0]
        
        # Usuários por role
        cursor.execute("""
            SELECT r.name, COUNT(DISTINCT ur.user_id) as user_count
            FROM roles r
            LEFT JOIN user_roles ur ON r.id = ur.role_id
            GROUP BY r.name
            ORDER BY user_count DESC
        """)
        report['users_by_role'] = {role: count for role, count in cursor.fetchall()}
        
        # Usuários por grupo
        cursor.execute("""
            SELECT g.name, COUNT(DISTINCT ug.user_id) as user_count
            FROM groups g
            LEFT JOIN user_groups ug ON g.id = ug.group_id
            GROUP BY g.name
            ORDER BY user_count DESC
        """)
        report['users_by_group'] = {group: count for group, count in cursor.fetchall()}
        
        # Usuários criados nos últimos 30 dias
        cursor.execute("""
            SELECT COUNT(*)
            FROM users
            WHERE created_at > NOW() - INTERVAL '30 days'
        """)
        report['new_users_30d'] = cursor.fetchone()[0]
        
        cursor.close()
        conn.close()
        
        logging.info("📊 Relatório de Usuários:")
        logging.info(f"  Usuários ativos: {report['total_active']}")
        logging.info(f"  Usuários inativos: {report['total_inactive']}")
        logging.info(f"  Novos usuários (30 dias): {report['new_users_30d']}")
        logging.info(f"  Distribuição por role:")
        for role, count in report['users_by_role'].items():
            logging.info(f"    - {role}: {count}")
        
        return report
        
    except Exception as e:
        logging.error(f"❌ Erro ao gerar relatório: {str(e)}")
        return {}

def audit_permissions():
    """Audita permissões e identifica inconsistências"""
    try:
        pg_hook = PostgresHook(postgres_conn_id='repositorio_postgres')
        conn = pg_hook.get_conn()
        cursor = conn.cursor()
        
        issues = []
        
        # 1. Usuários sem roles
        cursor.execute("""
            SELECT u.id, u.username, u.email
            FROM users u
            LEFT JOIN user_roles ur ON u.id = ur.user_id
            WHERE u.active = true
            AND ur.user_id IS NULL
        """)
        users_without_roles = cursor.fetchall()
        
        if users_without_roles:
            logging.warning(f"⚠️  {len(users_without_roles)} usuários sem roles:")
            for user_id, username, email in users_without_roles:
                logging.warning(f"  - {username} ({email})")
            issues.append({'type': 'users_without_roles', 'count': len(users_without_roles)})
        
        # 2. Roles sem permissões
        cursor.execute("""
            SELECT r.id, r.name
            FROM roles r
            LEFT JOIN role_permissions rp ON r.id = rp.role_id
            WHERE rp.role_id IS NULL
        """)
        roles_without_permissions = cursor.fetchall()
        
        if roles_without_permissions:
            logging.warning(f"⚠️  {len(roles_without_permissions)} roles sem permissões:")
            for role_id, role_name in roles_without_permissions:
                logging.warning(f"  - {role_name}")
            issues.append({'type': 'roles_without_permissions', 'count': len(roles_without_permissions)})
        
        # 3. Grupos vazios
        cursor.execute("""
            SELECT g.id, g.name
            FROM groups g
            LEFT JOIN user_groups ug ON g.id = ug.group_id
            WHERE ug.group_id IS NULL
        """)
        empty_groups = cursor.fetchall()
        
        if empty_groups:
            logging.warning(f"⚠️  {len(empty_groups)} grupos vazios:")
            for group_id, group_name in empty_groups:
                logging.warning(f"  - {group_name}")
            issues.append({'type': 'empty_groups', 'count': len(empty_groups)})
        
        cursor.close()
        conn.close()
        
        if not issues:
            logging.info("✅ Nenhuma inconsistência encontrada")
        else:
            logging.warning(f"🔍 {len(issues)} tipos de inconsistências detectadas")
        
        return {'issues': issues, 'count': len(issues)}
        
    except Exception as e:
        logging.error(f"❌ Erro ao auditar permissões: {str(e)}")
        return {'issues': [], 'count': 0}

def check_password_expiry():
    """Verifica senhas que precisam ser renovadas (exemplo - campo não existe ainda)"""
    try:
        logging.info("🔐 Verificação de expiração de senha (funcionalidade futura)")
        logging.info("ℹ️  Campo 'password_expires_at' será implementado no futuro")
        
        # Quando implementado, fará algo como:
        # SELECT username, email, password_updated_at
        # FROM users
        # WHERE password_updated_at < NOW() - INTERVAL '180 days'
        
        return {'status': 'skipped', 'reason': 'feature_not_implemented'}
        
    except Exception as e:
        logging.error(f"❌ Erro: {str(e)}")
        return {'status': 'error'}

def clean_duplicate_permissions():
    """Remove permissões duplicadas (se houver)"""
    try:
        pg_hook = PostgresHook(postgres_conn_id='repositorio_postgres')
        conn = pg_hook.get_conn()
        cursor = conn.cursor()
        
        # Verifica duplicatas em role_permissions
        cursor.execute("""
            SELECT role_id, permission_id, COUNT(*)
            FROM role_permissions
            GROUP BY role_id, permission_id
            HAVING COUNT(*) > 1
        """)
        
        duplicates = cursor.fetchall()
        
        if duplicates:
            logging.warning(f"⚠️  {len(duplicates)} permissões duplicadas encontradas")
            # Aqui você poderia implementar a limpeza automática
        else:
            logging.info("✅ Nenhuma permissão duplicada encontrada")
        
        cursor.close()
        conn.close()
        
        return {'duplicates': len(duplicates)}
        
    except Exception as e:
        logging.error(f"❌ Erro ao verificar duplicatas: {str(e)}")
        return {'duplicates': 0}

# Definir tarefas
task_inactive_users = PythonOperator(
    task_id='identify_inactive_users',
    python_callable=identify_inactive_users,
    dag=dag,
)

task_user_report = PythonOperator(
    task_id='generate_user_report',
    python_callable=generate_user_report,
    dag=dag,
)

task_audit_perms = PythonOperator(
    task_id='audit_permissions',
    python_callable=audit_permissions,
    dag=dag,
)

task_password_check = PythonOperator(
    task_id='check_password_expiry',
    python_callable=check_password_expiry,
    dag=dag,
)

task_clean_dupes = PythonOperator(
    task_id='clean_duplicate_permissions',
    python_callable=clean_duplicate_permissions,
    dag=dag,
)

# Fluxo paralelo de análises
[task_inactive_users, task_audit_perms, task_password_check] >> task_user_report >> task_clean_dupes
