CREATE TABLE snap_comparison
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created         TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified        TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    current_id      BIGINT                NULL,
    previous_id     BIGINT                NULL,
    feed_id         BIGINT                NOT NULL,
    raw_comparison  TEXT                  NOT NULL,
    result_detected BIT(1)                NOT NULL,
    CONSTRAINT pk_snap_comparison PRIMARY KEY (id)
);

ALTER TABLE snap_comparison
    ADD CONSTRAINT FK_SNAP_COMPARISON_ON_CURRENT FOREIGN KEY (current_id) REFERENCES video_snap (id);

ALTER TABLE snap_comparison
    ADD CONSTRAINT FK_SNAP_COMPARISON_ON_PREVIOUS FOREIGN KEY (previous_id) REFERENCES video_snap (id);

CREATE TABLE snap_comparison_detections
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    snap_comparison_id BIGINT NOT NULL,
    detection          TEXT   NOT NULL,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comparison_snap_comparison_detections FOREIGN KEY (snap_comparison_id) REFERENCES snap_comparison (id) ON DELETE CASCADE
);

CREATE INDEX IDX_COMPARISON_SNAP_COMPARISON_DETECTIONS ON snap_comparison_detections (snap_comparison_id);