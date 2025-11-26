DROP TABLE IF EXISTS cars;

CREATE TABLE cars (
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
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

CREATE INDEX cars_driver_id_idx ON cars (driver_id);
CREATE INDEX cars_plate_idx ON cars (plate);
