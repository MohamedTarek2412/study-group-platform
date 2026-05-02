-- ============================================================
-- V1__init_auth_users.sql
-- Initial schema for the auth-service
-- ============================================================

CREATE TABLE IF NOT EXISTS users (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    email               VARCHAR(255)    NOT NULL UNIQUE,
    password            VARCHAR(255)    NOT NULL,
    role                VARCHAR(30)     NOT NULL DEFAULT 'USER',
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Indexes for common query patterns
CREATE INDEX idx_users_email          ON users (email);
CREATE INDEX idx_users_role           ON users (role);
CREATE INDEX idx_users_is_active      ON users (is_active);
CREATE INDEX idx_users_created_at     ON users (created_at);

-- Auto-update updated_at on row modification
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

COMMENT ON TABLE users IS 'Stores authentication credentials and user identity';
COMMENT ON COLUMN users.email IS 'Unique identifier for authentication';
COMMENT ON COLUMN users.password IS 'BCrypt-hashed password';
COMMENT ON COLUMN users.role IS 'ADMIN | CREATOR | USER';
COMMENT ON COLUMN users.is_active IS 'Soft delete flag';
