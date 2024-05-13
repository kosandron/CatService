--liquibase formatted sql
--changeset kosandron:1
CREATE TABLE IF NOT EXISTS Owner (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    birthday TIMESTAMP
);
CREATE TABLE IF NOT EXISTS cats (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    birthday TIMESTAMP,
    breed VARCHAR(255),
    color VARCHAR(255),
    owner BIGINT REFERENCES Owner(id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS catfriends (
    catid BIGINT references cats (id),
    friendid BIGINT references cats (id),
    PRIMARY KEY(catid, friendid)
);
CREATE TABLE IF NOT EXISTS owner_cat (
    owner_id BIGINT references owner (id),
    cat BIGINT references cats (id),
    PRIMARY KEY(owner_id, cat)
);