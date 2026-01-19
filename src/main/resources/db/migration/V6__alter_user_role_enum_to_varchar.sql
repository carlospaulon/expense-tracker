-- change enum role type to varchar
ALTER TABLE users
ALTER COLUMN role TYPE VARCHAR(50)
USING role::text;

-- define default value
ALTER TABLE users
ALTER COLUMN role SET DEFAULT 'USER';

-- remove native enum (V2)
DROP TYPE IF EXISTS user_role;