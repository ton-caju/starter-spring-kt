-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(255) NOT NULL,
    birthday DATE NOT NULL
);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users(email);
