-- 营销活动表
CREATE TABLE IF NOT EXISTS flash_sale (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200),
    flash_price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    sold_count INT DEFAULT 0,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS promotion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    type INT DEFAULT 1,
    threshold_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 默认满减活动
INSERT INTO promotion (name, type, threshold_amount, discount_amount, start_time, end_time, status) VALUES
('满200减20', 1, 200.00, 20.00, '2024-01-01 00:00:00', '2030-12-31 23:59:59', 1),
('满500减60', 1, 500.00, 60.00, '2024-01-01 00:00:00', '2030-12-31 23:59:59', 1),
('满1000减150', 1, 1000.00, 150.00, '2024-01-01 00:00:00', '2030-12-31 23:59:59', 1);