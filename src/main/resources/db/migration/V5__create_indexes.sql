CREATE INDEX idx_expenses_user_id
    ON expenses(user_id);

CREATE INDEX idx_expenses_expense_date
    ON expenses(expense_date);

CREATE INDEX idx_expenses_user_date
    ON expenses(user_id, expense_date DESC);

CREATE INDEX idx_expenses_user_category
    ON expenses(user_id, category);