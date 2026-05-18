-- V1__init_schema.sql

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'DRIVER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS vehicles (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        user_id BIGINT NOT NULL,
                                        plate_no VARCHAR(20) NOT NULL UNIQUE,
    type VARCHAR(30) NOT NULL,
    brand VARCHAR(50),
    model VARCHAR(50),
    color VARCHAR(30),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS parking_lots (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100),
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    total_floors INT NOT NULL DEFAULT 1,
    hourly_rate DECIMAL(10,2) NOT NULL,
    daily_rate DECIMAL(10,2),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS parking_slots (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             lot_id BIGINT NOT NULL,
                                             slot_no VARCHAR(20) NOT NULL,
    floor INT NOT NULL DEFAULT 1,
    zone VARCHAR(10),
    slot_type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_slot_lot (lot_id, slot_no),
    CONSTRAINT fk_slot_lot FOREIGN KEY (lot_id) REFERENCES parking_lots(id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS bookings (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        booking_ref VARCHAR(20) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    slot_id BIGINT NOT NULL,
    scheduled_check_in TIMESTAMP,
    scheduled_check_out TIMESTAMP,
    actual_check_in TIMESTAMP,
    actual_check_out TIMESTAMP,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    total_amount DECIMAL(10,2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_booking_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
    CONSTRAINT fk_booking_slot FOREIGN KEY (slot_id) REFERENCES parking_slots(id)
    );

CREATE TABLE IF NOT EXISTS payments (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        booking_id BIGINT NOT NULL,
                                        amount DECIMAL(10,2) NOT NULL,
    method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    paid_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
    );

CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              user_id BIGINT NOT NULL,
                                              token VARCHAR(512) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Seed admin user (password: Admin@123)
INSERT IGNORE INTO users (name, email, password, phone, role)
VALUES ('System Admin', 'admin@parking.com',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E',
        '01700000000', 'ADMIN');

-- Seed a sample parking lot
INSERT IGNORE INTO parking_lots (name, address, city, latitude, longitude, total_floors, hourly_rate, daily_rate)
VALUES ('Central Parking Plaza', '123 Main Street', 'Dhaka', 23.8103, 90.4125, 3, 50.00, 500.00);
