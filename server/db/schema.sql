DROP USER IF EXISTS la_admin;
CREATE USER la_admin WITH PASSWORD 'Password1';
DROP SCHEMA IF EXISTS main CASCADE;
CREATE SCHEMA main;
SET search_path TO main,public;
ALTER USER la_admin SET search_path TO main,public;

CREATE TABLE tbl_user (
                      id INT GENERATED ALWAYS AS IDENTITY,
                      email VARCHAR(255)          NOT NULL,
                      full_name VARCHAR(255)      NOT NULL,
                      contact_info VARCHAR(1024)  NOT NULL
);

CREATE TABLE tbl_log (
                      id INT GENERATED ALWAYS AS IDENTITY,
                      description VARCHAR(1024),
                      log_type_id INT
);

CREATE TABLE tbl_alert (
                      id INT GENERATED ALWAYS AS IDENTITY,
                      label VARCHAR(256)          NOT NULL,
                      regex VARCHAR(4096)         NOT NULL,
                      log_id INT                  NOT NULL,
                      severity INT                DEFAULT 0,
                      occurrences INT             DEFAULT 0,
                      last_occurrence TIMESTAMP,
                      last_user_email VARCHAR(255)
);

CREATE TABLE tbl_alert_update (
                           id INT GENERATED ALWAYS AS IDENTITY,
                           change_type VARCHAR(8)  NOT NULL,
                           alert_id INT            NOT NULL,
                           new_occurrences INT     DEFAULT 0,
                           last_occurrence TIMESTAMP,
                           user_email VARCHAR(255),
                           log_id INT              NOT NULL
);

CREATE TABLE tbl_note (
                      id INT GENERATED ALWAYS AS IDENTITY,
                      email VARCHAR(255)          NOT NULL,
                      info VARCHAR(32768)         NOT NULL,
                      alert_id INT                NOT NULL
);

CREATE ROLE la_adm;
GRANT la_adm TO la_admin;
ALTER DEFAULT PRIVILEGES IN SCHEMA main GRANT ALL ON TABLES TO la_adm;