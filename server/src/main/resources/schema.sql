DROP TABLE IF EXISTS users, requests, items, bookings, comments CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT       NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL UNIQUE
);
CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT                      NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  VARCHAR                     NOT NULL,
    requestor_id BIGINT                      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT  NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR NOT NULL,
    description  VARCHAR NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id     BIGINT  NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    request_id   BIGINT REFERENCES requests (id)
);
CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT                      NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT                      NOT NULL REFERENCES items (id) ON DELETE CASCADE,
    booker_id  BIGINT                      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status     VARCHAR                     NOT NULL
);
CREATE TABLE IF NOT EXISTS comments
(
    id           BIGINT                      NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    comment_text TEXT                        NOT NULL,
    item_id      BIGINT                      NOT NULL REFERENCES items (id),
    author_id    BIGINT REFERENCES users (id),
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
