-- Create onboarding_responses table
CREATE TABLE IF NOT EXISTS onboarding_responses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    visitor_type VARCHAR(50) NOT NULL,
    country VARCHAR(100),
    user_type VARCHAR(50) NOT NULL,
    interests TEXT,
    time_preference VARCHAR(50) NOT NULL,
    language_preference VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- Create index on user_id for faster lookups
CREATE INDEX idx_onboarding_responses_user_id ON onboarding_responses(user_id);

-- Add comments for documentation
COMMENT ON TABLE onboarding_responses IS 'Stores user onboarding survey responses';
COMMENT ON COLUMN onboarding_responses.visitor_type IS 'Local (Sri Lankan) or Foreign Visitor';
COMMENT ON COLUMN onboarding_responses.country IS 'Country name (required for foreign visitors)';
COMMENT ON COLUMN onboarding_responses.user_type IS 'Student, Teacher, Professor, Researcher, Tourist, History Enthusiast, Local Visitor, or Other';
COMMENT ON COLUMN onboarding_responses.interests IS 'JSON array of selected interests';
COMMENT ON COLUMN onboarding_responses.time_preference IS 'Preferred time to spend in museum';
COMMENT ON COLUMN onboarding_responses.language_preference IS 'Preferred language (Sinhala, Tamil, English, Other)';
