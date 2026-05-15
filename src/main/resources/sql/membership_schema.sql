-- 会员等级表
CREATE TABLE IF NOT EXISTS membership_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    level INT NOT NULL,
    min_points INT DEFAULT 0,
    discount DECIMAL(3,2) DEFAULT 1.00,
    icon VARCHAR(100),
    description VARCHAR(200),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 默认会员等级数据
INSERT INTO membership_level (name, level, min_points, discount, icon, description) VALUES
('普通会员', 1, 0, 1.00, '⭐', '注册即享，购物积分'),
('银卡会员', 2, 500, 0.98, '⭐⭐', '累计500积分升级，享9.8折'),
('金卡会员', 3, 2000, 0.95, '⭐⭐⭐', '累计2000积分升级，享9.5折'),
('钻石会员', 4, 5000, 0.90, '💎', '累计5000积分升级，享9折'),
('至尊会员', 5, 10000, 0.85, '👑', '累计10000积分升级，享8.5折');

-- 用户表增加会员字段
ALTER TABLE user ADD COLUMN IF NOT EXISTS membership_level INT DEFAULT 1;
ALTER TABLE user ADD COLUMN IF NOT EXISTS total_spent DECIMAL(12,2) DEFAULT 0.00;