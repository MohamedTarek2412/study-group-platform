-- ============================================================
-- V1__init_user_profiles.sql
-- Initial schema for the user-service
-- ============================================================

CREATE TABLE IF NOT EXISTS user_profiles (
    id                  UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    auth_user_id        UUID            NOT NULL UNIQUE,  -- FK to auth-service (logical, cross-service)
    username            VARCHAR(50)     NOT NULL UNIQUE,
    email               VARCHAR(255)    NOT NULL UNIQUE,
    display_name        VARCHAR(100)    NOT NULL,
    bio                 TEXT,
    avatar_url          VARCHAR(500),
    role                VARCHAR(30)     NOT NULL DEFAULT 'STUDENT',
    creator_status      VARCHAR(30)     NOT NULL DEFAULT 'NOT_APPLIED',
    subjects            TEXT[],                           -- array of subject tags
    profile_complete    BOOLEAN         NOT NULL DEFAULT FALSE,
    is_active           BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Indexes for common query patterns
CREATE INDEX idx_user_profiles_auth_user_id  ON user_profiles (auth_user_id);
CREATE INDEX idx_user_profiles_username      ON user_profiles (username);
CREATE INDEX idx_user_profiles_email         ON user_profiles (email);
CREATE INDEX idx_user_profiles_role          ON user_profiles (role);
CREATE INDEX idx_user_profiles_creator_status ON user_profiles (creator_status);

-- Auto-update updated_at on row modification
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_user_profiles_updated_at
    BEFORE UPDATE ON user_profiles
    FOR EACH ROW
    EXECUTE FUNCTION set_updated_at();

COMMENT ON TABLE user_profiles IS 'Stores public-facing profile data for all registered users';
COMMENT ON COLUMN user_profiles.auth_user_id IS 'References the user identity in auth-service';
COMMENT ON COLUMN user_profiles.creator_status IS 'NOT_APPLIED | PENDING | APPROVED | REJECTED';
COMMENT ON COLUMN user_profiles.role IS 'STUDENT | CREATOR | ADMIN';
