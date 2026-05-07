-- ============================================================
-- V2__add_user_profile_subjects_table.sql
-- Add the join table required by UserProfile @ElementCollection subjects
-- ============================================================

CREATE TABLE IF NOT EXISTS user_profile_subjects (
    profile_id  UUID        NOT NULL REFERENCES user_profiles(id) ON DELETE CASCADE,
    subject     VARCHAR(255) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_profile_subjects_profile_id ON user_profile_subjects(profile_id);

-- Migrate any existing TEXT[] subjects data into the new join table
INSERT INTO user_profile_subjects (profile_id, subject)
SELECT id, unnest(subjects)
FROM user_profiles
WHERE subjects IS NOT NULL AND array_length(subjects, 1) > 0;

-- The old subjects column is no longer used by Hibernate (handled via join table)
-- Keep it for now to avoid data loss; can be dropped in a future migration.
