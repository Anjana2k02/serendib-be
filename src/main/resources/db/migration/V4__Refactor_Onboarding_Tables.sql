-- Create categories table
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Insert predefined categories
INSERT INTO categories (name) VALUES 
('Coins'),
('Ancient Artifacts'),
('Kandy Era'),
('Kings & Royalty'),
('Agriculture'),
('Statues'),
('Culture'),
('Technology'),
('Architecture'),
('Traditional Arts')
ON CONFLICT (name) DO NOTHING;

-- Create user_types table
CREATE TABLE IF NOT EXISTS user_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Insert predefined user types
INSERT INTO user_types (name) VALUES 
('Student'),
('Teacher'),
('Professor'),
('Researcher'),
('Tourist'),
('History Enthusiast'),
('Local Visitor'),
('Other')
ON CONFLICT (name) DO NOTHING;

-- Drop old string columns if they still exist
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'onboarding_responses' AND column_name = 'user_type' 
               AND data_type = 'character varying') THEN
        ALTER TABLE onboarding_responses DROP COLUMN user_type;
    END IF;
    
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'onboarding_responses' AND column_name = 'interests') THEN
        ALTER TABLE onboarding_responses DROP COLUMN interests;
    END IF;
END $$;

-- Add user_type_id column if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'onboarding_responses' AND column_name = 'user_type_id') THEN
        ALTER TABLE onboarding_responses ADD COLUMN user_type_id BIGINT;
    END IF;
END $$;

-- Add foreign key constraint if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints
                   WHERE constraint_name = 'fk_user_type' AND table_name = 'onboarding_responses') THEN
        ALTER TABLE onboarding_responses
            ADD CONSTRAINT fk_user_type
            FOREIGN KEY (user_type_id)
            REFERENCES user_types(id);
    END IF;
END $$;

-- Create junction table for interests
CREATE TABLE IF NOT EXISTS onboarding_response_categories (
    response_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (response_id, category_id),
    CONSTRAINT fk_response
        FOREIGN KEY (response_id)
        REFERENCES onboarding_responses(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON DELETE CASCADE
);

-- Add comments
COMMENT ON TABLE categories IS 'Lookup table for survey interests/categories';
COMMENT ON TABLE user_types IS 'Lookup table for user types';
COMMENT ON TABLE onboarding_response_categories IS 'Junction table mapping responses to multiple categories (interests)';
