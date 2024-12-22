CREATE TABLE video_snap
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    created  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    feed_id BIGINT                NOT NULL,
    data    TEXT                  NOT NULL,
    CONSTRAINT pk_video_snap PRIMARY KEY (id)
);