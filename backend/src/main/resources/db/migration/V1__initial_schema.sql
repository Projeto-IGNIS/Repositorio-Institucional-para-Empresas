-- Initial Schema for Repositório Institucional
-- Version: 1.0
-- Date: 2025-11-18

-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Permissions table
CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Role-Permission mapping (Many-to-Many)
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- User-Role mapping (Many-to-Many)
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Groups table
CREATE TABLE groups (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- User-Group mapping (Many-to-Many)
CREATE TABLE user_groups (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    group_id BIGINT NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, group_id)
);

-- Sectors table (hierarchical structure)
CREATE TABLE sectors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    parent_sector_id BIGINT REFERENCES sectors(id) ON DELETE CASCADE,
    path VARCHAR(500),
    metadata_defaults JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Audit Log table
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(50) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    extra_data JSONB,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_active ON users(active);

CREATE INDEX idx_sectors_parent ON sectors(parent_sector_id);
CREATE INDEX idx_sectors_path ON sectors(path);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_audit_logs_target ON audit_logs(target_type, target_id);

-- Insert default admin role and permissions
INSERT INTO roles (name, description) VALUES 
    ('ADMIN', 'Administrator with full access'),
    ('USER', 'Regular user with limited access'),
    ('GUEST', 'Guest with read-only access');

INSERT INTO permissions (name, description, resource, action) VALUES
    ('READ_DOCUMENTS', 'View documents', 'DOCUMENT', 'READ'),
    ('CREATE_DOCUMENTS', 'Create new documents', 'DOCUMENT', 'CREATE'),
    ('UPDATE_DOCUMENTS', 'Update existing documents', 'DOCUMENT', 'UPDATE'),
    ('DELETE_DOCUMENTS', 'Delete documents', 'DOCUMENT', 'DELETE'),
    ('MANAGE_USERS', 'Manage users', 'USER', 'MANAGE'),
    ('MANAGE_ROLES', 'Manage roles and permissions', 'ROLE', 'MANAGE'),
    ('MANAGE_SECTORS', 'Manage organizational sectors', 'SECTOR', 'MANAGE'),
    ('VIEW_AUDIT_LOGS', 'View audit logs', 'AUDIT', 'READ');

-- Assign all permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ADMIN';

-- Assign read permission to USER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'USER' AND p.name IN ('READ_DOCUMENTS', 'CREATE_DOCUMENTS', 'UPDATE_DOCUMENTS');

-- Assign read-only permission to GUEST role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'GUEST' AND p.name = 'READ_DOCUMENTS';

-- Create default admin user (password: admin123 - CHANGE IN PRODUCTION!)
-- Password hash for 'admin123' using BCrypt
INSERT INTO users (username, email, password_hash, active) VALUES
    ('admin', 'admin@institucional.com', '$2a$10$XoVxKj7t/RQKLVzrXdBXxO8rk.EK5xF4zYvQhXCmGMGVJMQQqDQ2e', true);

-- Assign ADMIN role to admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ADMIN';

-- Create root sector
INSERT INTO sectors (name, parent_sector_id, path) VALUES
    ('Root', NULL, '/');

COMMENT ON TABLE users IS 'Stores user accounts';
COMMENT ON TABLE roles IS 'Stores user roles';
COMMENT ON TABLE permissions IS 'Stores granular permissions';
COMMENT ON TABLE sectors IS 'Hierarchical organizational structure';
COMMENT ON TABLE audit_logs IS 'Audit trail for important actions';
