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

CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     login VARCHAR(255),
    password varchar(255),
    catownerid bigint
);
CREATE TABLE IF NOT EXISTS user_role (
                                         user_id BIGINT,
                                         role varchar(255),
    PRIMARY KEY (user_id, role)
);
insert into users(login, password, catownerid) values (null, '$2a$10$UdJ6CG8n2vOOXtPSrEvaP.uv.wNJFd6Ksh8tTFwPFUX3iZbhpxY52', 1);
insert into user_role(user_id, role) values (1, 'ADMIN');