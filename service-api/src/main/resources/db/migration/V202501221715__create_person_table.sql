SET client_encoding TO utf8;SET client_encoding TO utf8;

CREATE TABLE project110.person (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birth_date DATE NOT NULL
);
