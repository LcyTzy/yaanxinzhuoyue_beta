-- Inventory management system database tables

-- Supplier table
CREATE TABLE IF NOT EXISTS supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(50),
    phone VARCHAR(20),
    address VARCHAR(255),
    status TINYINT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Purchase order main table
CREATE TABLE IF NOT EXISTS purchase_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    supplier_id BIGINT,
    supplier_name VARCHAR(100),
    total_amount DECIMAL(12,2) DEFAULT 0.00,
    status VARCHAR(20) DEFAULT 'draft',
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
);

-- Purchase order detail table
CREATE TABLE IF NOT EXISTS purchase_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200),
    product_code VARCHAR(50),
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2),
    total_price DECIMAL(10,2),
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_order(id) ON DELETE CASCADE,
    INDEX idx_purchase_order_id (purchase_order_id),
    INDEX idx_product_id (product_id)
);

-- Inventory log table
CREATE TABLE IF NOT EXISTS inventory_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200),
    change_type VARCHAR(20) NOT NULL,
    change_quantity INT NOT NULL,
    before_quantity INT,
    after_quantity INT,
    related_order_no VARCHAR(64),
    remark VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product_id (product_id),
    INDEX idx_change_type (change_type),
    INDEX idx_create_time (create_time),
    INDEX idx_related_order_no (related_order_no)
);

-- Insert test supplier data
INSERT IGNORE INTO supplier (id, name, contact_person, phone, address, status) VALUES
(1, 'Guangzhou Auto Parts Supplier', 'Manager Zhang', '13800138001', 'Guangzhou Tianhe District Auto Parts City A101', 1),
(2, 'Shanghai Oil Agent', 'Manager Li', '13800138002', 'Shanghai Jiading District Auto Parts Market B205', 1),
(3, 'Beijing Filter Wholesale', 'Manager Wang', '13800138003', 'Beijing Chaoyang District Auto Parts City C308', 1),
(4, 'Shenzhen Electronic Parts', 'Manager Zhao', '13800138004', 'Shenzhen Futian District Electronic Market D412', 1),
(5, 'Chongqing Brake Pad Factory', 'Manager Liu', '13800138005', 'Chongqing Jiulongpo District Auto Parts City E516', 1);
