CREATE DATABASE auth_db;
CREATE DATABASE wholesale_db;
CREATE DATABASE integration_db;

\connect auth_db;

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    account_status VARCHAR(30) NOT NULL,
    company_name VARCHAR(200) NOT NULL,
    contact_person VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    business_type VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

INSERT INTO users (
    id,
    username,
    email,
    password,
    role,
    account_status,
    company_name,
    contact_person,
    phone,
    business_type,
    created_at,
    updated_at
) VALUES (
    1,
    'bootstrap-admin',
    'admin@bazuuyu.com',
    '$2a$10$OmF/H2j6YG2y7GNOJLAgRei/JLS3uXFK9Xkc.6CuIEGCrFs/Kva8S',
    'ADMIN',
    'APPROVED',
    'Bazuuyu',
    'Bootstrap Admin',
    '0000000000',
    'Operations',
    NOW(),
    NOW()
)
ON CONFLICT (username) DO NOTHING;

SELECT setval('users_id_seq', GREATEST((SELECT COALESCE(MAX(id), 1) FROM users), 1), true);

\connect wholesale_db;

CREATE TABLE IF NOT EXISTS wholesale_accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    company_name VARCHAR(200),
    contact_name VARCHAR(100),
    phone VARCHAR(50),
    business_type VARCHAR(100),
    country VARCHAR(100),
    state VARCHAR(100),
    status VARCHAR(30) NOT NULL,
    can_view_price BOOLEAN NOT NULL,
    can_place_order BOOLEAN NOT NULL,
    approved_at TIMESTAMP,
    approved_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS wholesale_application_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    company_name VARCHAR(200) NOT NULL,
    contact_name VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    business_type VARCHAR(100),
    country VARCHAR(100),
    state VARCHAR(100),
    note VARCHAR(1000),
    review_status VARCHAR(30) NOT NULL,
    review_note VARCHAR(1000),
    submitted_at TIMESTAMP,
    reviewed_at TIMESTAMP,
    reviewed_by VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_wholesale_application_records_user_id
    ON wholesale_application_records (user_id);

CREATE INDEX IF NOT EXISTS idx_wholesale_application_records_review_status
    ON wholesale_application_records (review_status);

CREATE TABLE IF NOT EXISTS wholesale_pricing_rules (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(120) NOT NULL,
    channel VARCHAR(20) NOT NULL,
    min_quantity INT NOT NULL,
    unit_price NUMERIC(12, 2) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_wholesale_pricing_rules_lookup
    ON wholesale_pricing_rules (sku, channel, min_quantity, active);

CREATE TABLE IF NOT EXISTS shipping_policies (
    id BIGSERIAL PRIMARY KEY,
    mode VARCHAR(20) NOT NULL,
    free_shipping_threshold NUMERIC(12, 2) NOT NULL,
    flat_shipping_fee NUMERIC(12, 2) NOT NULL,
    per_kg_fee NUMERIC(12, 2) NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory_records (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(120) NOT NULL UNIQUE,
    available_quantity INT NOT NULL,
    source_mode VARCHAR(20) NOT NULL,
    eta_days INT NOT NULL,
    active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS wholesale_quotes (
    id BIGSERIAL PRIMARY KEY,
    buyer_email VARCHAR(150) NOT NULL,
    price_channel VARCHAR(20) NOT NULL,
    shipment_mode VARCHAR(20) NOT NULL,
    subtotal NUMERIC(12, 2) NOT NULL,
    shipping_fee NUMERIC(12, 2) NOT NULL,
    total NUMERIC(12, 2) NOT NULL,
    converted_to_order BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS manual_orders (
    id BIGSERIAL PRIMARY KEY,
    buyer_email VARCHAR(150) NOT NULL,
    quote_id BIGINT NOT NULL,
    shopify_order_id BIGINT UNIQUE,
    status VARCHAR(20) NOT NULL,
    tracking_number VARCHAR(80),
    total NUMERIC(12, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS lead_records (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    company_name VARCHAR(200),
    stage VARCHAR(20) NOT NULL,
    note VARCHAR(1000),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS funnel_events (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(150) NOT NULL,
    event_type VARCHAR(40) NOT NULL,
    source VARCHAR(60),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS processed_webhook_events (
    id BIGSERIAL PRIMARY KEY,
    webhook_id VARCHAR(120) NOT NULL UNIQUE,
    topic VARCHAR(80) NOT NULL,
    processed BOOLEAN NOT NULL,
    received_at TIMESTAMP NOT NULL,
    processed_at TIMESTAMP
);
