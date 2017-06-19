ALTER TABLE crimes ADD COLUMN latitude DOUBLE PRECISION;
ALTER TABLE crimes ADD COLUMN longitude DOUBLE PRECISION;

UPDATE crimes SET latitude = coordinates[0], longitude = coordinates[1];

ALTER TABLE crimes DROP COLUMN coordinates;

ALTER TABLE crimes ADD UNIQUE (committed_at, latitude, longitude, location, type);

CREATE TABLE crime_fetch_history
(
    id SERIAL PRIMARY KEY,
    fetched_at TIMESTAMPTZ UNIQUE,
    success BOOLEAN,
    crimes_added INTEGER
)