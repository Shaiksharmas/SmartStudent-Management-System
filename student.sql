-- student.sql
-- Database schema for the SmartStudent Student Management System

CREATE DATABASE IF NOT EXISTS smartstudent;
USE smartstudent;

-- Main students table
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    roll_no VARCHAR(20) NOT NULL UNIQUE,
    department VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    marks DECIMAL(5,2) NOT NULL DEFAULT 0
);

-- Optional: admins table, used only if AdminService.USE_DB_AUTH is set to true
CREATE TABLE IF NOT EXISTS admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

INSERT INTO admins (username, password) VALUES ('admin', 'admin123')
    ON DUPLICATE KEY UPDATE username = username;

-- Sample data so the app has something to show immediately
INSERT INTO students (name, roll_no, department, email, phone, marks) VALUES
('Aarav Sharma', 'CSE001', 'Computer Science', 'aarav.sharma@example.com', '9876543210', 88.50),
('Priya Nair', 'CSE002', 'Computer Science', 'priya.nair@example.com', '9876543211', 92.00),
('Rohan Verma', 'ECE001', 'Electronics', 'rohan.verma@example.com', '9876543212', 74.25),
('Sneha Reddy', 'ME001', 'Mechanical', 'sneha.reddy@example.com', '9876543213', 65.75),
('Karan Mehta', 'CSE003', 'Computer Science', 'karan.mehta@example.com', '9876543214', 55.00),
('Anjali Iyer', 'ECE002', 'Electronics', 'anjali.iyer@example.com', '9876543215', 81.10)
ON DUPLICATE KEY UPDATE name = VALUES(name);
