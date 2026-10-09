-- FitTrack database schema (MySQL 8)
-- Run:  mysql -u root -p < sql/schema.sql

CREATE DATABASE IF NOT EXISTS fittrack CHARACTER SET utf8mb4;
USE fittrack;

DROP TABLE IF EXISTS activity_log;
DROP TABLE IF EXISTS challenge_participants;
DROP TABLE IF EXISTS challenges;
DROP TABLE IF EXISTS fitness_content;
DROP TABLE IF EXISTS goals;
DROP TABLE IF EXISTS workouts;
DROP TABLE IF EXISTS settings;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(80)  NOT NULL,
    email         VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(160) NOT NULL,
    role          ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
    active        TINYINT(1)   NOT NULL DEFAULT 1,
    weight_kg     DECIMAL(5,2) NULL,
    height_cm     DECIMAL(5,2) NULL,
    fitness_goal  VARCHAR(30)  NOT NULL DEFAULT 'GENERAL',
    joined_on     DATE         NOT NULL
);

CREATE TABLE workouts (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT          NOT NULL,
    workout_type  VARCHAR(40)  NOT NULL,
    duration_min  INT          NOT NULL,
    intensity     ENUM('LOW','MEDIUM','HIGH') NOT NULL,
    calories      INT          NOT NULL DEFAULT 0,
    workout_date  DATE         NOT NULL,
    notes         VARCHAR(255) NULL,
    CONSTRAINT fk_workout_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_workout_user_date (user_id, workout_date)
);

-- weekly targets a user sets for himself/herself
CREATE TABLE goals (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT          NOT NULL,
    title         VARCHAR(100) NOT NULL,
    metric        ENUM('MINUTES','CALORIES','WORKOUTS') NOT NULL,
    target_value  INT          NOT NULL,
    created_on    DATE         NOT NULL,
    CONSTRAINT fk_goal_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE challenges (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(100) NOT NULL,
    description   VARCHAR(400) NULL,
    metric        ENUM('MINUTES','WORKOUTS') NOT NULL,
    target_value  INT          NOT NULL,
    start_date    DATE         NOT NULL,
    end_date      DATE         NOT NULL,
    created_by    INT          NULL,
    CONSTRAINT fk_challenge_admin FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE challenge_participants (
    challenge_id  INT  NOT NULL,
    user_id       INT  NOT NULL,
    joined_on     DATE NOT NULL,
    PRIMARY KEY (challenge_id, user_id),
    CONSTRAINT fk_cp_challenge FOREIGN KEY (challenge_id) REFERENCES challenges(id) ON DELETE CASCADE,
    CONSTRAINT fk_cp_user      FOREIGN KEY (user_id)      REFERENCES users(id)      ON DELETE CASCADE
);

CREATE TABLE fitness_content (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(120) NOT NULL,
    category      VARCHAR(40)  NOT NULL,
    body          TEXT         NOT NULL,
    submitted_by  INT          NOT NULL,
    status        ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    admin_note    VARCHAR(255) NULL,
    created_at    DATETIME     NOT NULL,
    CONSTRAINT fk_content_user FOREIGN KEY (submitted_by) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE settings (
    setting_key   VARCHAR(50)  PRIMARY KEY,
    setting_value VARCHAR(200) NOT NULL
);

CREATE TABLE activity_log (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT          NULL,
    actor_name    VARCHAR(80)  NOT NULL,
    action        VARCHAR(40)  NOT NULL,
    details       VARCHAR(255) NULL,
    created_at    DATETIME     NOT NULL,
    INDEX idx_activity_time (created_at)
);

INSERT INTO settings (setting_key, setting_value) VALUES
    ('site_name', 'FitTrack'),
    ('allow_registration', 'true'),
    ('max_workout_minutes', '300'),
    ('default_weight_kg', '65'),
    ('require_content_approval', 'true');

-- The default admin (admin@fittrack.com / admin123) is created automatically
-- by AppContextListener on first start, so the password gets hashed properly.
