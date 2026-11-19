CREATE TABLE organizations (
    id UUID PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(40) NOT NULL UNIQUE
);
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    username VARCHAR(80) NOT NULL UNIQUE,
    email VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY(user_id, role_id)
);
CREATE TABLE departments (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_department_org_name UNIQUE(organization_id,name)
);
CREATE TABLE employees (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    department_id UUID REFERENCES departments(id),
    employee_number VARCHAR(30) NOT NULL,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    work_email VARCHAR(160) NOT NULL,
    phone_number VARCHAR(30),
    job_title VARCHAR(120),
    employment_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    joining_date DATE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_employee_org_number UNIQUE(organization_id,employee_number),
    CONSTRAINT uk_employee_org_email UNIQUE(organization_id,work_email),
    CONSTRAINT ck_employee_status CHECK (employment_status IN ('ACTIVE','INACTIVE','ON_LEAVE','TERMINATED'))
);
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id),
    actor_username VARCHAR(80) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(80) NOT NULL,
    entity_id VARCHAR(80),
    details JSONB,
    ip_address INET,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_users_org ON users(organization_id);
CREATE INDEX idx_departments_org ON departments(organization_id);
CREATE INDEX idx_employees_org ON employees(organization_id);
CREATE INDEX idx_employees_department ON employees(department_id);
CREATE INDEX idx_employees_name ON employees(last_name,first_name);
CREATE INDEX idx_audit_org_created ON audit_logs(organization_id,created_at DESC);
INSERT INTO roles(name) VALUES ('SUPER_ADMIN'),('HR_ADMIN'),('HR_MANAGER'),('MANAGER'),('EMPLOYEE'),('FINANCE');
