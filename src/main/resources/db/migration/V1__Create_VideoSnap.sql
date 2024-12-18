CREATE TABLE video_snap
(
    id      BIGINT AUTO_INCREMENT NOT NULL,
    feed_id BIGINT                NOT NULL,
    date    DATETIME              NOT NULL,
    data    TEXT                  NOT NULL,
    CONSTRAINT pk_video_snap PRIMARY KEY (id)
);