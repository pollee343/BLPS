DROP TYPE IF EXISTS usage_direction CASCADE;
DROP TYPE IF EXISTS usage_type CASCADE;
DROP TYPE IF EXISTS operation_type CASCADE;
DROP TYPE IF EXISTS promised_payment_status CASCADE;

CREATE TYPE operation_type AS ENUM ('INCOME', 'EXPENSE');
CREATE TYPE usage_type AS ENUM ('CALL', 'SMS', 'INTERNET');
CREATE TYPE usage_direction AS ENUM ('INCOMING', 'OUTGOING');
CREATE TYPE promised_payment_status AS ENUM ('ACTIVE', 'OVERDUE', 'PAID');
CREATE TYPE application_type AS ENUM ('PROMISED_PAYMENT_REJECTION', 'LEGALLY_RELIABLE_REPORT');

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_type
        WHERE typname = 'usage_direction'
    ) THEN
CREATE TYPE usage_direction AS ENUM ('INCOMING', 'OUTGOING');
END IF;
END
$$;

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    last_name VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    middle_name VARCHAR(255),
    birth_date DATE NOT NULL,
    passport_series VARCHAR(4) NOT NULL,
    passport_number VARCHAR(6) NOT NULL,
    CONSTRAINT uq_users_passport UNIQUE (passport_series, passport_number)
    );

CREATE TABLE IF NOT EXISTS bank (
    id BIGSERIAL PRIMARY KEY,
    card_number VARCHAR(16) NOT NULL UNIQUE,
    cvc VARCHAR(3) NOT NULL,
    balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
    CONSTRAINT chk_bank_balance_non_negative CHECK (balance >= 0)
    );

CREATE TABLE IF NOT EXISTS user_data (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(12) NOT NULL UNIQUE,

    balance NUMERIC(19, 2) NOT NULL DEFAULT 0,
    remaining_seconds INTEGER NOT NULL DEFAULT 0,
    remaining_bytes BIGINT NOT NULL DEFAULT 0,
    remaining_sms INTEGER NOT NULL DEFAULT 0,
    is_blocked BOOLEAN NOT NULL DEFAULT FALSE,

    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,

    CONSTRAINT chk_user_data_balance_non_negative CHECK (balance >= 0),
    CONSTRAINT chk_user_data_minutes_non_negative CHECK (remaining_seconds >= 0),
    CONSTRAINT chk_user_data_bytes_non_negative CHECK (remaining_bytes >= 0),
    CONSTRAINT chk_user_data_sms_non_negative CHECK (remaining_sms >= 0)
);

CREATE TABLE IF NOT EXISTS money_operations (
    id BIGSERIAL PRIMARY KEY,
    operation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    op_name VARCHAR(300) NOT NULL,
    op_type operation_type NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    user_data_id BIGINT NOT NULL REFERENCES user_data(id) ON DELETE CASCADE,

    CONSTRAINT chk_money_operations_amount_positive CHECK (amount > 0)
);

CREATE TABLE IF NOT EXISTS promised_payments (
    id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(19, 2) NOT NULL,
    amount_to_repay NUMERIC(19, 2) NOT NULL,
    status promised_payment_status NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    repaid_at TIMESTAMP,
    user_data_id BIGINT NOT NULL REFERENCES user_data(id) ON DELETE CASCADE,

    CONSTRAINT chk_promised_payments_amount_positive CHECK (amount > 0),
    CONSTRAINT chk_promised_payments_amount_single_limit CHECK (amount <= 1500.00),
    CONSTRAINT chk_promised_payments_amount_to_repay_positive CHECK (amount_to_repay > 0)
);

CREATE TABLE IF NOT EXISTS service_usage (
    id BIGSERIAL PRIMARY KEY,
    operation_type usage_type NOT NULL,
    direction usage_direction,
    name VARCHAR(300),
    units_used INTEGER NOT NULL,
    operation_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_data_id BIGINT NOT NULL REFERENCES user_data(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS applications (
    id BIGSERIAL PRIMARY KEY,
    user_data_id BIGINT NOT NULL REFERENCES user_data(id) ON DELETE CASCADE,
    app_type application_type NOT NULL,
    email VARCHAR(255) NOT NULL,
    is_waiting BOOLEAN NOT NULL DEFAULT true
)