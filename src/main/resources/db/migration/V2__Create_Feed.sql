CREATE TABLE feed
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    created  TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id  BIGINT                NOT NULL,
    name     VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_feed PRIMARY KEY (id)
);

INSERT INTO feed (id, user_id, name)
VALUES (1, 1, 'Test Feed 1');