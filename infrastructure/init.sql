-- Initialization script for PostgreSQL
-- Create schemas or tables if necessary.
-- The smartaudit database is created via POSTGRES_DB environment variable.

CREATE TABLE IF NOT EXISTS audit_requests (
    id UUID PRIMARY KEY,
    project_name VARCHAR(255),
    contract_code TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP
);
