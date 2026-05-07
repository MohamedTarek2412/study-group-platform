-- ============================================================
-- V2__ensure_group_member_cache.sql
-- Ensures group_member_cache exists for deployments where V1
-- was applied before this table was added to the schema.
-- ============================================================

CREATE TABLE IF NOT EXISTS group_member_cache (
    id       BIGSERIAL PRIMARY KEY,
    group_id BIGINT    NOT NULL,
    user_id  BIGINT    NOT NULL,
    CONSTRAINT uq_group_member UNIQUE (group_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_group_member_cache_group_id ON group_member_cache(group_id);
CREATE INDEX IF NOT EXISTS idx_group_member_cache_user_id  ON group_member_cache(user_id);
