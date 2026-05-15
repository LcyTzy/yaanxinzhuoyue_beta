-- 商品SKU表
CREATE TABLE IF NOT EXISTS product_sku (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku_code VARCHAR(100),
    spec_name VARCHAR(50),
    spec_value VARCHAR(100),
    price DECIMAL(10,2),
    stock INT DEFAULT 0,
    image VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 商品分享记录表
CREATE TABLE IF NOT EXISTS product_share (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    user_id BIGINT,
    share_type VARCHAR(20),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;