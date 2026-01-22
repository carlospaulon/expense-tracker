-- Remove constraint
ALTER TABLE expenses
DROP CONSTRAINT IF EXISTS chk_expenses_amount_positive;

-- Add correct one
ALTER TABLE expenses
ADD CONSTRAINT chk_expenses_amount_positive
CHECK (amount > 0)