CREATE DATABASE IF NOT EXISTS fitness_membership
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE fitness_membership;

CREATE TABLE IF NOT EXISTS members (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    phone VARCHAR(30),
    email VARCHAR(254),
    gender VARCHAR(30),
    registration_date VARCHAR(10),
    status VARCHAR(30) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_members_email (email)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS coaches (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    phone VARCHAR(30),
    specialization VARCHAR(120),
    status VARCHAR(30) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS memberships (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    type VARCHAR(80) NOT NULL,
    start_date VARCHAR(10),
    end_date VARCHAR(10),
    price DECIMAL(12, 2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_memberships_member_id (member_id),
    CONSTRAINT fk_memberships_member
        FOREIGN KEY (member_id) REFERENCES members (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS workout_classes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    coach_id BIGINT NOT NULL,
    class_date VARCHAR(10),
    class_time VARCHAR(5),
    room VARCHAR(80),
    capacity INT NOT NULL,
    status VARCHAR(30) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_workout_classes_coach_id (coach_id),
    CONSTRAINT fk_workout_classes_coach
        FOREIGN KEY (coach_id) REFERENCES coaches (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS class_bookings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    booking_date VARCHAR(10),
    status VARCHAR(30) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_class_bookings_member_id (member_id),
    KEY idx_class_bookings_class_id (class_id),
    CONSTRAINT fk_class_bookings_member
        FOREIGN KEY (member_id) REFERENCES members (id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_class_bookings_class
        FOREIGN KEY (class_id) REFERENCES workout_classes (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    payment_date VARCHAR(10),
    method VARCHAR(50),
    status VARCHAR(30) NOT NULL,
    description VARCHAR(500),
    PRIMARY KEY (id),
    KEY idx_payments_member_id (member_id),
    CONSTRAINT fk_payments_member
        FOREIGN KEY (member_id) REFERENCES members (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT NOT NULL AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    attendance_date VARCHAR(10),
    status VARCHAR(30) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_attendance_booking_id (booking_id),
    CONSTRAINT fk_attendance_booking
        FOREIGN KEY (booking_id) REFERENCES class_bookings (id)
        ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB;
