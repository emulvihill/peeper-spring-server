CREATE TABLE comparison_result
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    response_id     VARCHAR(255)          NOT NULL,
    before_id       BIGINT                NULL,
    after_id        BIGINT                NULL,
    date            datetime              NOT NULL,
    result          TEXT                  NOT NULL,
    result_detected BIT(1)                NOT NULL,
    CONSTRAINT pk_comparison_result PRIMARY KEY (id)
);

ALTER TABLE comparison_result
    ADD CONSTRAINT uc_comparison_result_after UNIQUE (after_id);

ALTER TABLE comparison_result
    ADD CONSTRAINT uc_comparison_result_before UNIQUE (before_id);

ALTER TABLE comparison_result
    ADD CONSTRAINT FK_COMPARISON_RESULT_ON_AFTER FOREIGN KEY (after_id) REFERENCES video_snap (id);

ALTER TABLE comparison_result
    ADD CONSTRAINT FK_COMPARISON_RESULT_ON_BEFORE FOREIGN KEY (before_id) REFERENCES video_snap (id);