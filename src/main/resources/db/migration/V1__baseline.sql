-- users
CREATE TABLE IF NOT EXISTS "user" (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_admin BOOLEAN DEFAULT FALSE,
    created_in TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- reset tokens
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_reset_token_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE
);

-- categories
CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    color TEXT,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    parent_task_id BIGINT,
    CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT fk_category_self FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE
);

-- tasks
CREATE TABLE IF NOT EXISTS task (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    importance INTEGER NOT NULL,
    deadline TIMESTAMP,
    user_id BIGINT,
    category_id BIGINT,
    parent_task_id BIGINT,
    CONSTRAINT fk_task_user FOREIGN KEY (user_id) REFERENCES "user"(id) ON DELETE CASCADE,
    CONSTRAINT fk_task_category FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL,
    CONSTRAINT fk_task_self FOREIGN KEY (parent_task_id) REFERENCES task(id) ON DELETE CASCADE
);

ALTER TABLE category ADD CONSTRAINT fk_category_task FOREIGN KEY (parent_task_id) REFERENCES task(id) ON DELETE CASCADE;

-- task dependencies
CREATE TABLE IF NOT EXISTS task_dependency (
    id BIGSERIAL PRIMARY KEY,
    blocking_task_id BIGINT NOT NULL,
    blocked_task_id BIGINT NOT NULL,
    CONSTRAINT fk_blocking_task FOREIGN KEY (blocking_task_id) REFERENCES task(id) ON DELETE CASCADE,
    CONSTRAINT fk_blocked_task FOREIGN KEY (blocked_task_id) REFERENCES task(id) ON DELETE CASCADE
);