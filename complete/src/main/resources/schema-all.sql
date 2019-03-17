DROP TABLE people IF EXISTS;

CREATE TABLE people  (
    person_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(20) NOT NULL,
    last_name VARCHAR(20)
);

DROP TABLE people_error IF EXISTS;

CREATE TABLE people_error  (
    person_error_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    error_cause VARCHAR(2000)
);
