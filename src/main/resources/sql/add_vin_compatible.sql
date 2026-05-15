ALTER TABLE `product` ADD COLUMN `vin_compatible` varchar(255) DEFAULT NULL COMMENT '兼容VIN码，多个用逗号分隔' AFTER `image`;
