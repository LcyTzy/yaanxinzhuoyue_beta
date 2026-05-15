CREATE TABLE IF NOT EXISTS `vehicle_brand` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `logo` varchar(255) DEFAULT NULL,
  `initial` char(1) DEFAULT NULL,
  `sort` int DEFAULT 0,
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_initial` (`initial`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vehicle_series` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `brand_id` bigint NOT NULL,
  `name` varchar(100) NOT NULL,
  `sort` int DEFAULT 0,
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_brand_id` (`brand_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `vehicle_model` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `series_id` bigint NOT NULL,
  `name` varchar(100) NOT NULL,
  `year` varchar(10) DEFAULT NULL,
  `displacement` varchar(20) DEFAULT NULL,
  `engine` varchar(50) DEFAULT NULL,
  `transmission` varchar(20) DEFAULT NULL,
  `sort` int DEFAULT 0,
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_series_id` (`series_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `product_vehicle` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `vehicle_model_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_vehicle` (`product_id`, `vehicle_model_id`),
  KEY `idx_vehicle_model_id` (`vehicle_model_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `vehicle_brand` (`name`, `initial`, `sort`, `status`) VALUES
('Audi', 'A', 1, 1),
('BMW', 'B', 2, 1),
('Benz', 'B', 3, 1),
('VW', 'D', 4, 1),
('Toyota', 'F', 5, 1),
('Honda', 'B', 6, 1),
('Nissan', 'R', 7, 1),
('Hyundai', 'X', 8, 1),
('Kia', 'Q', 9, 1),
('Ford', 'F', 10, 1);

INSERT INTO `vehicle_series` (`brand_id`, `name`, `sort`, `status`) VALUES
(1, 'A4L', 1, 1),
(1, 'A6L', 2, 1),
(1, 'Q5L', 3, 1),
(2, '3 Series', 1, 1),
(2, '5 Series', 2, 1),
(2, 'X3', 3, 1),
(3, 'C-Class', 1, 1),
(3, 'E-Class', 2, 1),
(3, 'GLC', 3, 1),
(4, 'Lavida', 1, 1),
(4, 'Passat', 2, 1),
(4, 'Magotan', 3, 1),
(5, 'Corolla', 1, 1),
(5, 'Camry', 2, 1),
(5, 'RAV4', 3, 1),
(6, 'Accord', 1, 1),
(6, 'Civic', 2, 1),
(6, 'CR-V', 3, 1);

INSERT INTO `vehicle_model` (`series_id`, `name`, `year`, `displacement`, `engine`, `transmission`, `sort`, `status`) VALUES
(1, 'A4L 40TFSI', '2023', '2.0T', 'EA888', '7DCT', 1, 1),
(1, 'A4L 45TFSI', '2023', '2.0T', 'EA888', '7DCT', 2, 1),
(2, 'A6L 45TFSI', '2023', '2.0T', 'EA888', '7DCT', 1, 1),
(2, 'A6L 55TFSI', '2023', '3.0T', 'EA839', '7DCT', 2, 1),
(4, '325Li', '2023', '2.0T', 'B48', '8AT', 1, 1),
(4, '330Li', '2023', '2.0T', 'B48', '8AT', 2, 1),
(5, '525Li', '2023', '2.0T', 'B48', '8AT', 1, 1),
(5, '530Li', '2023', '2.0T', 'B48', '8AT', 2, 1),
(10, 'Lavida 1.5L', '2023', '1.5L', 'EA211', '6AT', 1, 1),
(10, 'Lavida 1.4T', '2023', '1.4T', 'EA211', '7DCT', 2, 1),
(11, 'Passat 330TSI', '2023', '2.0T', 'EA888', '7DCT', 1, 1),
(11, 'Passat 380TSI', '2023', '2.0T', 'EA888', '7DCT', 2, 1),
(13, 'Corolla 1.2T', '2023', '1.2T', '9NR-FTS', 'CVT', 1, 1),
(13, 'Corolla 1.5L', '2023', '1.5L', 'M15C', 'CVT', 2, 1),
(14, 'Camry 2.0L', '2023', '2.0L', 'M20C', 'CVT', 1, 1),
(14, 'Camry 2.5L', '2023', '2.5L', 'A25A', '8AT', 2, 1);
