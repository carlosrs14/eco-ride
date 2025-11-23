CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS outbox_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(100),
    aggregate_id UUID,
    event_type VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    message_id UUID NOT NULL DEFAULT gen_random_uuid(),
    saga_id UUID,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING|SENDING|SENT|FAILED
    retry_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    processed_at TIMESTAMP,
    last_error TEXT
);

CREATE INDEX IF NOT EXISTS idx_outbox_status_created_at ON outbox_events(status, created_at);

CREATE TABLE IF NOT EXISTS processed_messages (
    message_id UUID PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP NOT NULL DEFAULT NOW()
);
