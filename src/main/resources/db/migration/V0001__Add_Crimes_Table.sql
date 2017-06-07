CREATE TABLE crimes
(
    id SERIAL PRIMARY KEY,
    committed_at DATE,
    coordinates POINT,
    location TEXT,
    type TEXT
)