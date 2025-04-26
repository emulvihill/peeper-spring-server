-- Create CompareProfile table
CREATE TABLE compare_profile
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    created  TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    name     VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_compare_profile PRIMARY KEY (id),
    CONSTRAINT uk_compare_profile_name UNIQUE (name)
);

-- Create PointOfInterest table
CREATE TABLE point_of_interest
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    created           TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified          TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    request           VARCHAR(255)          NOT NULL,
    detected          BIT(1)                NOT NULL,
    compare_profile_id BIGINT               NULL,
    CONSTRAINT pk_point_of_interest PRIMARY KEY (id)
);

-- Create POIAction table
CREATE TABLE poi_action
(
    id                BIGINT AUTO_INCREMENT NOT NULL,
    created           TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified          TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    action            VARCHAR(255)          NOT NULL,
    compare_profile_id BIGINT               NULL,
    CONSTRAINT pk_poi_action PRIMARY KEY (id)
);

-- Create PointOfInterestPOIAction join table
CREATE TABLE point_of_interest_poi_action
(
    id                   BIGINT AUTO_INCREMENT NOT NULL,
    created              TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified             TIMESTAMP             NOT NULL DEFAULT CURRENT_TIMESTAMP,
    point_of_interest_id BIGINT                NOT NULL,
    poi_action_id        BIGINT                NOT NULL,
    CONSTRAINT pk_point_of_interest_poi_action PRIMARY KEY (id)
);

-- Add foreign key constraints
ALTER TABLE point_of_interest
    ADD CONSTRAINT FK_POINT_OF_INTEREST_ON_COMPARE_PROFILE FOREIGN KEY (compare_profile_id) REFERENCES compare_profile (id);

ALTER TABLE poi_action
    ADD CONSTRAINT FK_POI_ACTION_ON_COMPARE_PROFILE FOREIGN KEY (compare_profile_id) REFERENCES compare_profile (id);

ALTER TABLE point_of_interest_poi_action
    ADD CONSTRAINT FK_POINT_OF_INTEREST_POI_ACTION_ON_POINT_OF_INTEREST FOREIGN KEY (point_of_interest_id) REFERENCES point_of_interest (id);

ALTER TABLE point_of_interest_poi_action
    ADD CONSTRAINT FK_POINT_OF_INTEREST_POI_ACTION_ON_POI_ACTION FOREIGN KEY (poi_action_id) REFERENCES poi_action (id);

-- Create indexes for foreign keys
CREATE INDEX IDX_POINT_OF_INTEREST_COMPARE_PROFILE_ID ON point_of_interest (compare_profile_id);
CREATE INDEX IDX_POI_ACTION_COMPARE_PROFILE_ID ON poi_action (compare_profile_id);
CREATE INDEX IDX_POINT_OF_INTEREST_POI_ACTION_POINT_OF_INTEREST_ID ON point_of_interest_poi_action (point_of_interest_id);
CREATE INDEX IDX_POINT_OF_INTEREST_POI_ACTION_POI_ACTION_ID ON point_of_interest_poi_action (poi_action_id);