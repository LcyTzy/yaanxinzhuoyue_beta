-- 积分商城表
CREATE TABLE IF NOT EXISTS points_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    image VARCHAR(500),
    points INT NOT NULL,
    stock INT DEFAULT 0,
    status INT DEFAULT 1,
    description VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS points_exchange (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    product_name VARCHAR(200),
    points INT NOT NULL,
    status INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS sign_in_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    sign_date DATE NOT NULL,
    points INT DEFAULT 0,
    consecutive_days INT DEFAULT 1,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_date (user_id, sign_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 默认积分商品
INSERT INTO points_product (name, image, points, stock, status, description) VALUES
('洗车券', '', 100, 999, 1, '免费洗车一次'),
('50元优惠券', '', 500, 100, 1, '满200减50元优惠券'),
('车载手机支架', '', 300, 50, 1, '高品质车载手机支架'),
('汽车香薰', '', 200, 80, 1, '持久清香汽车香薰'),
('玻璃水2瓶装', '', 150, 200, 1, '高效清洁玻璃水'),
('100元保养券', '', 1000, 30, 1, '满500减100元保养券');