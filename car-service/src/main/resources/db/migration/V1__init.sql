CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS  cars (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plate VARCHAR(10) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    color VARCHAR(10),
    seats INT NOT NULL,
    driver_id UUID NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS cars_driver_id_idx ON cars (driver_id);
CREATE INDEX IF NOT EXISTS cars_plate_idx ON cars (plate);

