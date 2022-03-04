-- WORKSPACE Table Create SQL
CREATE TABLE WORKSPACE
(
    `WORKSPACE_ID`        BIGINT          NOT NULL    AUTO_INCREMENT,
    `CREATED_DATE`        TIMESTAMP       NOT NULL    DEFAULT current_timestamp,
    `LAST_MODIFIED_DATE`  TIMESTAMP       NOT NULL    DEFAULT current_timestamp,
    `NAME`                VARCHAR(50)     NOT NULL,
    `WORKSPACE_LOGO_URL`  VARCHAR(200)    NULL,
    PRIMARY KEY (WORKSPACE_ID)
);

-- USER Table Create SQL
CREATE TABLE USER
(
    `USER_ID`             BIGINT          NOT NULL    AUTO_INCREMENT,
    `CREATED_DATE`        TIMESTAMP       NOT NULL    DEFAULT current_timestamp,
    `LAST_MODIFIED_DATE`  TIMESTAMP       NOT NULL    DEFAULT current_timestamp,
    `EMAIL`               VARCHAR(200)    NOT NULL,
    `CONTACT_EMAIL`       VARCHAR(200)    NULL,
    `NAME`                VARCHAR(20)     NULL,
    `PROFILE_IMAGE_URL`   VARCHAR(200)    NULL,
    `PHONE`               VARCHAR(25)     NULL,
    `NICKNAME`            VARCHAR(30)     NULL,
    `ROLE`                VARCHAR(20)     NOT NULL,
    `USER_PROVIDER`       VARCHAR(20)     NOT NULL,
    PRIMARY KEY (USER_ID)
);

CREATE UNIQUE INDEX UNQ_USER_EMAIL
    ON USER(EMAIL);

-- WORKSPACE_USER Table Create SQL
CREATE TABLE WORKSPACE_USER
(
    `WORKSPACE_USER_ID`   BIGINT          NOT NULL    AUTO_INCREMENT,
    `USER_ID`             BIGINT          NOT NULL,
    `WORKSPACE_ID`        BIGINT          NOT NULL,
    `CREATED_DATE`        TIMESTAMP       NOT NULL    DEFAULT current_timestamp,
    `LAST_MODIFIED_DATE`  TIMESTAMP       NOT NULL    DEFAULT current_timestamp,
    `IS_WORKSPACE_ADMIN`  TINYINT         NOT NULL,
    `NICKNAME`            VARCHAR(20)     NOT NULL,
    `PROFILE_IMAGE_URL`   VARCHAR(200)    NULL,
    `POSITION`            VARCHAR(20)     NULL,
    PRIMARY KEY (WORKSPACE_USER_ID)
);

CREATE INDEX IDX_WORKSPACE_USER_USER_ID
    ON WORKSPACE_USER(USER_ID);

CREATE INDEX IDX_WORKSPACE_USER_WORKSPACE_ID_NICKNAME
    ON WORKSPACE_USER(WORKSPACE_ID, NICKNAME);

ALTER TABLE WORKSPACE_USER
    ADD CONSTRAINT FK_WORKSPACE_USER_USER_ID_USER_USER_ID FOREIGN KEY (USER_ID)
        REFERENCES USER (USER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE WORKSPACE_USER
    ADD CONSTRAINT FK_WORKSPACE_USER_WORKSPACE_ID_WORKSPACE_WORKSPACE_ID FOREIGN KEY (WORKSPACE_ID)
        REFERENCES WORKSPACE (WORKSPACE_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

-- TEAM Table Create SQL
CREATE TABLE TEAM
(
    `TEAM_ID`             BIGINT          NOT NULL    AUTO_INCREMENT,
    `WORKSPACE_ID`        BIGINT          NOT NULL,
    `CREATED_DATE`        TIMESTAMP       NOT NULL    DEFAULT current_timestamp,
    `LAST_MODIFIED_DATE`  TIMESTAMP       NOT NULL    DEFAULT current_timestamp,
    `NAME`                VARCHAR(30)     NOT NULL,
    `TEAM_LOGO_URL`       VARCHAR(200)    NULL,
    PRIMARY KEY (TEAM_ID)
);

CREATE INDEX IDX_TEAM_WORKSPACE_ID
    ON TEAM(WORKSPACE_ID);

ALTER TABLE TEAM
    ADD CONSTRAINT FK_TEAM_WORKSPACE_ID_WORKSPACE_WORKSPACE_ID FOREIGN KEY (WORKSPACE_ID)
        REFERENCES WORKSPACE (WORKSPACE_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

-- TEAM_USER Table Create SQL
CREATE TABLE TEAM_USER
(
    `TEAM_USER_ID`        BIGINT        NOT NULL    AUTO_INCREMENT,
    `WORKSPACE_USER_ID`   BIGINT        NOT NULL,
    `TEAM_ID`             BIGINT        NOT NULL,
    `CREATED_DATE`        TIMESTAMP     NOT NULL    DEFAULT current_timestamp,
    `LAST_MODIFIED_DATE`  TIMESTAMP     NOT NULL    DEFAULT current_timestamp,
    `IS_TEAM_ADMIN`       TINYINT       NOT NULL,
    PRIMARY KEY (TEAM_USER_ID)
);

CREATE INDEX IDX_TEAM_USER_TEAM_ID
    ON TEAM_USER(TEAM_ID);

CREATE INDEX IDX_TEAM_USER_WORKSPACE_USER_ID
    ON TEAM_USER(WORKSPACE_USER_ID);

ALTER TABLE TEAM_USER
    ADD CONSTRAINT FK_TEAM_USER_TEAM_ID_TEAM_TEAM_ID FOREIGN KEY (TEAM_ID)
        REFERENCES TEAM (TEAM_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE TEAM_USER
    ADD CONSTRAINT FK_TEAM_USER_WORKSPACE_USER_ID_WORKSPACE_USER_WORKSPACE_USER_ID FOREIGN KEY (WORKSPACE_USER_ID)
        REFERENCES WORKSPACE_USER (WORKSPACE_USER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

-- MEETING Table Create SQL
CREATE TABLE MEETING
(
    `MEETING_ID`          BIGINT          NOT NULL    AUTO_INCREMENT,
    `WORKSPACE_ID`        BIGINT          NOT NULL,
    `TEAM_ID`             BIGINT          NOT NULL,
    `CREATED_DATE`        TIMESTAMP       NOT NULL    DEFAULT current_timestamp,
    `LAST_MODIFIED_DATE`  TIMESTAMP       NOT NULL    DEFAULT current_timestamp,
    `NAME`                VARCHAR(50)     NOT NULL,
    `PURPOSE`             VARCHAR(100)    NOT NULL,
    `START_DATE`          DATE            NOT NULL,
    `START_TIME`          TIME            NOT NULL,
    `END_TIME`            TIME            NOT NULL,
    `PLACE`               VARCHAR(200)    NOT NULL,
    `MEETING_STATUS`      VARCHAR(20)     NOT NULL,
    PRIMARY KEY (MEETING_ID)
);

CREATE INDEX IDX_MEETING_TEAM_ID
    ON MEETING(TEAM_ID);

CREATE INDEX IDX_MEETING_WORKSPACE_ID
    ON MEETING(WORKSPACE_ID);

ALTER TABLE MEETING
    ADD CONSTRAINT FK_MEETING_WORKSPACE_ID_WORKSPACE_WORKSPACE_ID FOREIGN KEY (WORKSPACE_ID)
        REFERENCES WORKSPACE (WORKSPACE_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE MEETING
    ADD CONSTRAINT FK_MEETING_TEAM_ID_TEAM_TEAM_ID FOREIGN KEY (TEAM_ID)
        REFERENCES TEAM (TEAM_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

-- ATTENDEE Table Create SQL
CREATE TABLE ATTENDEE
(
    `ATTENDEE_ID`         BIGINT         NOT NULL    AUTO_INCREMENT,
    `MEETING_ID`          BIGINT         NOT NULL,
    `WORKSPACE_USER_ID`   BIGINT         NOT NULL,
    `CREATED_DATE`        TIMESTAMP      NOT NULL    DEFAULT current_timestamp,
    `LAST_MODIFIED_DATE`  TIMESTAMP      NOT NULL    DEFAULT current_timestamp,
    `ATTEND_STATUS`       VARCHAR(20)    NOT NULL,
    `IS_MEETING_ADMIN`    TINYINT        NOT NULL,
    PRIMARY KEY (ATTENDEE_ID)
);

CREATE INDEX IDX_ATTENDEE_MEETING_ID
    ON ATTENDEE(MEETING_ID);

CREATE INDEX IDX_ATTENDEE_WORKSPACE_USER_ID
    ON ATTENDEE(WORKSPACE_USER_ID);

ALTER TABLE ATTENDEE
    ADD CONSTRAINT FK_ATTENDEE_MEETING_ID_MEETING_MEETING_ID FOREIGN KEY (MEETING_ID)
        REFERENCES MEETING (MEETING_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE ATTENDEE
    ADD CONSTRAINT FK_ATTENDEE_WORKSPACE_USER_ID_WORKSPACE_USER_WORKSPACE_USER_ID FOREIGN KEY (WORKSPACE_USER_ID)
        REFERENCES WORKSPACE_USER (WORKSPACE_USER_ID) ON DELETE RESTRICT ON UPDATE RESTRICT;