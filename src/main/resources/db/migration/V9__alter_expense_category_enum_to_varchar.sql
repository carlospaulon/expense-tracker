-- enum to varchar
ALTER TABLE expenses
ALTER COLUMN category TYPE VARCHAR(50)
USING category::text;

-- remove native enum (v2)
DROP TYPE IF EXISTS expense_category;