SET client_encoding TO utf8;

CREATE TABLE project110.category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE project110.product (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10, 2) NOT NULL
);

ALTER TABLE IF EXISTS project110.product
    ADD CONSTRAINT product_category_id_fk FOREIGN KEY (category_id) REFERENCES project110.category;