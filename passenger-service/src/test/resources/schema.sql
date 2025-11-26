DROP TABLE IF EXISTS passengers CASCADE;
DROP TABLE IF EXISTS drivers CASCADE;
DROP TABLE IF EXISTS ratings CASCADE;

CREATE TABLE IF NOT EXISTS passengers (
    id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    rating_avg FLOAT NOT NULL DEFAULT 0.0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    keycloak_sub VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS passengers_email_idx ON passengers(email);


CREATE TABLE IF NOT EXISTS drivers (
    id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    passenger_id UUID NOT NULL UNIQUE,
    license_no VARCHAR(50) NOT NULL UNIQUE,
    verification_status BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    FOREIGN KEY (passenger_id) REFERENCES passengers(id)
);

CREATE INDEX IF NOT EXISTS drivers_passenger_id_idx ON drivers(passenger_id);
CREATE INDEX IF NOT EXISTS drivers_license_no_idx ON drivers(license_no);


CREATE TABLE IF NOT EXISTS ratings (
    id UUID PRIMARY KEY DEFAULT RANDOM_UUID(),
    trip_id UUID NOT NULL,
    from_id UUID NOT NULL,
    to_id UUID NOT NULL,
    score FLOAT NOT NULL,
    comment VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS ratings_from_id_idx ON ratings(from_id);
CREATE INDEX IF NOT EXISTS ratings_to_id_idx ON ratings(to_id);
CREATE INDEX IF NOT EXISTS ratings_trip_id_idx ON ratings(trip_id);
