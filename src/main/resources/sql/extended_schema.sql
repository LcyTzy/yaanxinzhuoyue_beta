-- 门店表
CREATE TABLE IF NOT EXISTS store (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(500),
    phone VARCHAR(20),
    business_hours VARCHAR(100),
    image VARCHAR(500),
    latitude DOUBLE,
    longitude DOUBLE,
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 保养档案表
CREATE TABLE IF NOT EXISTS maintenance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vehicle_vin VARCHAR(50),
    vehicle_info VARCHAR(200),
    service_type VARCHAR(100),
    description VARCHAR(500),
    mileage INT,
    service_date DATE,
    store_name VARCHAR(200),
    cost DECIMAL(10,2),
    images VARCHAR(1000),
    next_remind_date DATE,
    reminded INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 发票表
CREATE TABLE IF NOT EXISTS invoice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_id BIGINT,
    order_no VARCHAR(64),
    type INT DEFAULT 1,
    title VARCHAR(200),
    tax_no VARCHAR(50),
    amount DECIMAL(10,2),
    email VARCHAR(100),
    status INT DEFAULT 0,
    invoice_no VARCHAR(50),
    invoice_url VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 询价表
CREATE TABLE IF NOT EXISTS inquiry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(50),
    user_phone VARCHAR(20),
    product_name VARCHAR(200),
    oe_number VARCHAR(50),
    vin_code VARCHAR(50),
    quantity INT DEFAULT 1,
    description VARCHAR(500),
    quoted_price DECIMAL(10,2),
    status INT DEFAULT 0,
    reply VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 保险年检表
CREATE TABLE IF NOT EXISTS insurance_service (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vehicle_vin VARCHAR(50),
    vehicle_info VARCHAR(200),
    service_type INT DEFAULT 1,
    contact_name VARCHAR(50),
    contact_phone VARCHAR(20),
    quoted_price DECIMAL(10,2),
    status INT DEFAULT 0,
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 仓库表
CREATE TABLE IF NOT EXISTS warehouse (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    address VARCHAR(500),
    contact_name VARCHAR(50),
    contact_phone VARCHAR(20),
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 默认门店数据
INSERT INTO store (name, address, phone, business_hours, status) VALUES
('新卓阅汽配旗舰店', '北京市朝阳区建国路88号', '010-88886666', '08:00-20:00', 1),
('新卓阅汽配朝阳店', '北京市朝阳区望京西路12号', '010-66668888', '08:30-19:30', 1),
('新卓阅汽配海淀店', '北京市海淀区中关村大街56号', '010-55556666', '09:00-18:00', 1);

-- 默认仓库数据
INSERT INTO warehouse (name, address, contact_name, contact_phone, status) VALUES
('北京总仓', '北京市大兴区物流园A区', '张经理', '010-99998888', 1),
('华东分仓', '上海市嘉定区物流园B区', '李经理', '021-88887777', 1);