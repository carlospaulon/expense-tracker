CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');

CREATE TYPE expense_category AS ENUM (
    'GROCERIES',
    'LEISURE',
    'ELECTRONICS',
    'UTILITIES',
    'CLOTHING',
    'HEALTH',
    'TRANSPORT',
    'ENTERTAINMENT',
    'OTHERS'
);