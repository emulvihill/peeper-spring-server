CREATE TABLE feed
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    user_id BIGINT                NOT NULL,
    name    VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_feed PRIMARY KEY (id)
);