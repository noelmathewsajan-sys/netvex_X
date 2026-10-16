-- ============================================================================
-- INTERNET BILLING MANAGEMENT SYSTEM
-- MySQL / MySQL Workbench Database Script
-- Database Name: InternetBillingDB
-- ============================================================================

CREATE DATABASE IF NOT EXISTS InternetBillingDB;
USE InternetBillingDB;

-- Drop tables in reverse order of foreign key relationships
DROP TABLE IF EXISTS Payments;
DROP TABLE IF EXISTS Bills;
DROP TABLE IF EXISTS `Usage`;
DROP TABLE IF EXISTS Customers;
DROP TABLE IF EXISTS Plans;
DROP TABLE IF EXISTS Admin;

-- 1. Admin Table
CREATE TABLE Admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Internet Plans Table
CREATE TABLE Plans (
    plan_id VARCHAR(30) PRIMARY KEY,
    plan_name VARCHAR(100) NOT NULL,
    speed_tier VARCHAR(50) NOT NULL,
    monthly_tariff DECIMAL(10,2) NOT NULL,
    data_limit DECIMAL(10,2) NOT NULL,
    extra_data_charge DECIMAL(10,2) NOT NULL,
    equipment_rental DECIMAL(10,2) NOT NULL,
    late_payment_fine DECIMAL(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Customers Table
CREATE TABLE Customers (
    customer_id VARCHAR(30) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    plan_id VARCHAR(30) NOT NULL,
    connection_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT FK_Customers_Plans FOREIGN KEY (plan_id) REFERENCES Plans(plan_id) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Monthly Usage Table
CREATE TABLE `Usage` (
    usage_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id VARCHAR(30) NOT NULL,
    billing_month VARCHAR(20) NOT NULL,
    data_used DECIMAL(10,2) NOT NULL,
    extra_data DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT FK_Usage_Customers FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE CASCADE,
    CONSTRAINT UQ_Usage_Customer_Month UNIQUE (customer_id, billing_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Bills Table
CREATE TABLE Bills (
    bill_id VARCHAR(30) PRIMARY KEY,
    customer_id VARCHAR(30) NOT NULL,
    usage_id INT NULL,
    billing_month VARCHAR(20) NOT NULL,
    plan_tariff DECIMAL(10,2) NOT NULL,
    equipment_rental DECIMAL(10,2) NOT NULL,
    extra_usage_charge DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    late_fine DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(10,2) NOT NULL,
    due_date DATE NOT NULL,
    bill_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID',
    CONSTRAINT FK_Bills_Customers FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE CASCADE,
    CONSTRAINT FK_Bills_Usage FOREIGN KEY (usage_id) REFERENCES `Usage`(usage_id) ON DELETE SET NULL,
    CONSTRAINT UQ_Bills_Customer_Month UNIQUE (customer_id, billing_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Payments Table
CREATE TABLE Payments (
    payment_id VARCHAR(30) PRIMARY KEY,
    bill_id VARCHAR(30) NOT NULL,
    customer_id VARCHAR(30) NOT NULL,
    payment_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_reference VARCHAR(100) NOT NULL,
    payment_status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS',
    CONSTRAINT FK_Payments_Bills FOREIGN KEY (bill_id) REFERENCES Bills(bill_id) ON DELETE CASCADE,
    CONSTRAINT FK_Payments_Customers FOREIGN KEY (customer_id) REFERENCES Customers(customer_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================================================
-- Sample Initial Data (ISP Demo Data)
-- ============================================================================

-- 1. Administrator Account
INSERT INTO Admin (username, password) VALUES ('admin', 'admin123');

-- 2. Broadband Plans
INSERT INTO Plans (plan_id, plan_name, speed_tier, monthly_tariff, data_limit, extra_data_charge, equipment_rental, late_payment_fine) VALUES
('PLAN01', 'Basic Starter', '25 Mbps', 399.00, 100.00, 10.00, 50.00, 50.00),
('PLAN02', 'Standard Home', '50 Mbps', 699.00, 250.00, 8.00, 75.00, 75.00),
('PLAN03', 'Premium Streamer', '100 Mbps', 999.00, 500.00, 5.00, 100.00, 100.00),
('PLAN04', 'Ultra Enterprise', '300 Mbps', 1499.00, 1000.00, 3.00, 150.00, 150.00);

-- 3. Customers
INSERT INTO Customers (customer_id, name, address, phone, email, username, password, plan_id, connection_date, status) VALUES
('CUST101', 'John Doe', '123 Elm Street, Cityville', '9876543210', 'john.doe@email.com', 'johndoe', 'cust123', 'PLAN02', '2026-01-15', 'ACTIVE'),
('CUST102', 'Sarah Connor', '456 Pine Avenue, Metropolis', '9876543211', 'sarah.c@email.com', 'sarahc', 'cust123', 'PLAN03', '2026-02-10', 'ACTIVE'),
('CUST103', 'Michael Scott', '1725 Slough Ave, Scranton', '9876543212', 'michael.s@email.com', 'michaels', 'cust123', 'PLAN01', '2026-03-05', 'ACTIVE'),
('CUST104', 'Bruce Wayne', '1007 Mountain Drive, Gotham', '9876543213', 'bruce.w@email.com', 'brucew', 'cust123', 'PLAN04', '2026-01-20', 'ACTIVE'),
('CUST105', 'Peter Parker', '20 Ingram Street, Queens', '9876543214', 'peter.p@email.com', 'peterp', 'cust123', 'PLAN02', '2026-04-12', 'INACTIVE');

-- 4. Monthly Usage
INSERT INTO `Usage` (customer_id, billing_month, data_used, extra_data) VALUES
('CUST101', '2026-08', 220.00, 0.00),
('CUST101', '2026-09', 275.00, 25.00),
('CUST102', '2026-08', 480.00, 0.00),
('CUST102', '2026-09', 530.00, 30.00),
('CUST103', '2026-08', 95.00, 0.00),
('CUST103', '2026-09', 115.00, 15.00),
('CUST104', '2026-09', 650.00, 0.00);

-- 5. Monthly Bills
INSERT INTO Bills (bill_id, customer_id, usage_id, billing_month, plan_tariff, equipment_rental, extra_usage_charge, late_fine, total_amount, due_date, bill_status) VALUES
('BILL-202608-101', 'CUST101', 1, '2026-08', 699.00, 75.00, 0.00, 0.00, 774.00, '2026-09-05', 'PAID'),
('BILL-202609-101', 'CUST101', 2, '2026-09', 699.00, 75.00, 200.00, 0.00, 974.00, '2026-10-05', 'UNPAID'),
('BILL-202608-102', 'CUST102', 3, '2026-08', 999.00, 100.00, 0.00, 0.00, 1099.00, '2026-09-05', 'PAID'),
('BILL-202609-102', 'CUST102', 4, '2026-09', 999.00, 100.00, 150.00, 0.00, 1249.00, '2026-10-05', 'UNPAID'),
('BILL-202608-103', 'CUST103', 5, '2026-08', 399.00, 50.00, 0.00, 50.00, 499.00, '2026-09-05', 'OVERDUE');

-- 6. Payment Transactions
INSERT INTO Payments (payment_id, bill_id, customer_id, payment_date, amount, payment_method, payment_reference, payment_status) VALUES
('PAY-88001', 'BILL-202608-101', 'CUST101', '2026-09-02 11:30:00', 774.00, 'UPI', 'UPI-REF-99281729', 'SUCCESS'),
('PAY-88002', 'BILL-202608-102', 'CUST102', '2026-09-03 14:15:22', 1099.00, 'Credit Card', 'TXN-CC-48201948', 'SUCCESS');
