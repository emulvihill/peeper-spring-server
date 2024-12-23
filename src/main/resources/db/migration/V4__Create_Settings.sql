CREATE TABLE setting
(
    id    BIGINT AUTO_INCREMENT NOT NULL,
    `key` VARCHAR(255)          NULL,
    scope VARCHAR(255)          NULL,
    value TEXT                  NULL,
    CONSTRAINT pk_setting PRIMARY KEY (id)
);


INSERT INTO setting (id, `key`, scope, value)
VALUES (1, 'comparison_provider', 'GLOBAL', 'openai');


INSERT INTO setting (id, `key`, scope, value)
VALUES (2, 'comparison_model', 'GLOBAL', 'gpt-4o-mini');