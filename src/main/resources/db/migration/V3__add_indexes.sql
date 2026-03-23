-- Skill name search (autocomplete)
CREATE INDEX IF NOT EXISTS idx_skills_name ON skills(name);

-- Case-insensitive skill name search
CREATE INDEX IF NOT EXISTS idx_skills_name_lower ON skills(LOWER(name));
