-- Seed data for mentor specialties.
-- Runs on every startup (spring.sql.init.mode=always), so each insert must be idempotent:
-- ON CONFLICT relies on the unique constraint uk_specialties_name.
INSERT INTO specialties (name) VALUES ('Backend Development') ON CONFLICT (name) DO NOTHING;
INSERT INTO specialties (name) VALUES ('Frontend Development') ON CONFLICT (name) DO NOTHING;
INSERT INTO specialties (name) VALUES ('Mobile Development') ON CONFLICT (name) DO NOTHING;
INSERT INTO specialties (name) VALUES ('DevOps & Cloud') ON CONFLICT (name) DO NOTHING;
INSERT INTO specialties (name) VALUES ('Data Science & AI') ON CONFLICT (name) DO NOTHING;
INSERT INTO specialties (name) VALUES ('Cybersecurity') ON CONFLICT (name) DO NOTHING;
INSERT INTO specialties (name) VALUES ('UI/UX Design') ON CONFLICT (name) DO NOTHING;
INSERT INTO specialties (name) VALUES ('Product Management') ON CONFLICT (name) DO NOTHING;
INSERT INTO specialties (name) VALUES ('QA & Testing') ON CONFLICT (name) DO NOTHING;
INSERT INTO specialties (name) VALUES ('Career Change') ON CONFLICT (name) DO NOTHING;
INSERT INTO specialties (name) VALUES ('Interview Prep') ON CONFLICT (name) DO NOTHING;
INSERT INTO specialties (name) VALUES ('Resume & LinkedIn Review') ON CONFLICT (name) DO NOTHING;
INSERT INTO specialties (name) VALUES ('Leadership & Management') ON CONFLICT (name) DO NOTHING;
