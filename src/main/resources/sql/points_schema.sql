ALTER TABLE `user` ADD COLUMN `points` int DEFAULT 0 COMMENT '用户积分' AFTER `avatar`;

CREATE TABLE `points_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `points` int NOT NULL,
  `type` varchar(50) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
