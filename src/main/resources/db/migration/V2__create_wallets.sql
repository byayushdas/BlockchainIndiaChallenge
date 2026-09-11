CREATE TABLE wallets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    currency CHAR(3) NOT NULL,
    balance NUMERIC(19,2) NOT NULL,
    version BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_wallet_user
        FOREIGN KEY (user_id) REFERENCES users(id),

    CONSTRAINT positive_balance
        CHECK (balance >= 0)
);
