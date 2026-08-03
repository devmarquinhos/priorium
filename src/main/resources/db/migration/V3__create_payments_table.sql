-- payments
CREATE TABLE IF NOT EXISTS payment (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    subscription_id BIGINT,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(100),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_sub FOREIGN KEY (subscription_id) REFERENCES subscription(id) ON DELETE SET NULL
);