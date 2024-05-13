--liquibase formatted sql
--changeset kosandron:1
CREATE TABLE IF NOT EXISTS Owner (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    birthday TIMESTAMP
);
CREATE TABLE IF NOT EXISTS cat (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    birthday TIMESTAMP,
    breed VARCHAR(255),
    color VARCHAR(255),
    owner BIGINT REFERENCES Owner(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS catfriends (
    catid BIGINT references cat (id),
    friendid BIGINT references cat (id),
    PRIMARY KEY(catid, friendid)
);
CREATE TABLE IF NOT EXISTS owner_cat (
    owner_id BIGINT references owner (id),
    cat BIGINT references cat (id),
    PRIMARY KEY(owner_id, cat)
);
INSERT INTO Owner(name, birthday) VALUES('Vasya', '2011-05-16 00:00:00');