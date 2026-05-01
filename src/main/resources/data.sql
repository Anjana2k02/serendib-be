-- Seed categories
INSERT INTO categories (name) VALUES ('Coins') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Ancient Artifacts') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Kandy Era') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Kings & Royalty') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Agriculture') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Statues') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Culture') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Technology') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Architecture') ON CONFLICT (name) DO NOTHING;
INSERT INTO categories (name) VALUES ('Traditional Arts') ON CONFLICT (name) DO NOTHING;

-- Seed user types
INSERT INTO user_types (name) VALUES ('Student') ON CONFLICT (name) DO NOTHING;
INSERT INTO user_types (name) VALUES ('Teacher') ON CONFLICT (name) DO NOTHING;
INSERT INTO user_types (name) VALUES ('Professor') ON CONFLICT (name) DO NOTHING;
INSERT INTO user_types (name) VALUES ('Researcher') ON CONFLICT (name) DO NOTHING;
INSERT INTO user_types (name) VALUES ('Tourist') ON CONFLICT (name) DO NOTHING;
INSERT INTO user_types (name) VALUES ('History Enthusiast') ON CONFLICT (name) DO NOTHING;
INSERT INTO user_types (name) VALUES ('Local Visitor') ON CONFLICT (name) DO NOTHING;
INSERT INTO user_types (name) VALUES ('Other') ON CONFLICT (name) DO NOTHING;
