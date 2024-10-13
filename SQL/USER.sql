CREATE TYPE STATUS_ENUM AS ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED', 'LOCKED', 'PENDING', 'CLOSED');

-- Tabela: user_accounts
CREATE TABLE user_accounts (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    status STATUS_ENUM DEFAULT 'PENDING',
    failed_login_attempts INT DEFAULT 0,
    last_login TIMESTAMP,
    two_factor_enabled BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    consent_to_communication BOOLEAN DEFAULT FALSE,
    is_business_account BOOLEAN DEFAULT FALSE,
    hmac VARCHAR(255) NOT NULL
);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_user_accounts_updated_at
BEFORE UPDATE ON user_accounts
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Tabela: user_personal_data
CREATE TABLE user_personal_data (
    user_id INT PRIMARY KEY REFERENCES user_accounts(user_id),
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    country_of_origin VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    pesel VARCHAR(255) NOT NULL,
    id_card_number VARCHAR(255) NOT NULL,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    hmac VARCHAR(255) NOT NULL
);
CREATE TRIGGER update_user_personal_data_updated_at
BEFORE UPDATE ON user_personal_data
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();


-- Tabela: sessions
CREATE TABLE sessions (
    session_id VARCHAR(255) PRIMARY KEY,
    user_id INT REFERENCES user_accounts(user_id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    ip_address VARCHAR(255) NOT NULL,
    user_agent VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    hmac VARCHAR(255) NOT NULL
);

-- Tabela: activity_logs
CREATE TABLE activity_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES user_accounts(user_id),
    session_id VARCHAR(255) REFERENCES sessions(session_id),
    action VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(255) NOT NULL,
    user_agent VARCHAR(255) NOT NULL,
    hmac VARCHAR(255) NOT NULL
);

Create type CONSTENT_TYPE_ENUM as ENUM('marketing', 'data_sharing', 'communication');

-- Tabela: user_consent (opcjonalnie)
CREATE TABLE user_consent (
    consent_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES user_accounts(user_id),
    consent_type CONSTENT_TYPE_ENUM NOT NULL,
    is_granted BOOLEAN DEFAULT FALSE,
    consent_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    hmac VARCHAR(255) NOT NULL
);
