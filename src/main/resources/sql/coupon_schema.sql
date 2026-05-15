CREATE TABLE IF NOT EXISTS `coupon` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `type` tinyint NOT NULL,
  `discount_amount` decimal(10,2) DEFAULT NULL,
  `discount_rate` decimal(3,2) DEFAULT NULL,
  `min_amount` decimal(10,2) NOT NULL DEFAULT 0,
  `total_count` int NOT NULL DEFAULT 0,
  `receive_count` int NOT NULL DEFAULT 0,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `user_coupon` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `coupon_id` bigint NOT NULL,
  `status` tinyint DEFAULT 0,
  `use_time` datetime DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_coupon_id` (`coupon_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `coupon` (`name`, `type`, `discount_amount`, `discount_rate`, `min_amount`, `total_count`, `start_time`, `end_time`, `status`) VALUES
('New User Coupon', 1, 20.00, NULL, 100.00, 1000, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1),
('Full Reduction Coupon', 1, 50.00, NULL, 200.00, 500, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1),
('10% Off Coupon', 2, NULL, 0.90, 150.00, 300, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1),
('20% Off Coupon', 2, NULL, 0.80, 300.00, 200, '2026-01-01 00:00:00', '2026-12-31 23:59:59', 1);
