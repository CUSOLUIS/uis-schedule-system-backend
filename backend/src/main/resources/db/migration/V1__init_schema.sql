CREATE TABLE roles (
    role_guid UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    email VARCHAR(250) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    first_name VARCHAR(128),
    last_name VARCHAR(128),
    username VARCHAR(128) UNIQUE,
    last_session TIMESTAMP,
    is_enabled BOOLEAN,
    account_no_expired BOOLEAN,
    account_no_locked BOOLEAN,
    credential_no_expired BOOLEAN
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_guid UUID NOT NULL,
    PRIMARY KEY (user_id, role_guid),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_role FOREIGN KEY (role_guid) REFERENCES roles(role_guid)
);
