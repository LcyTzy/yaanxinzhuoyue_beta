-- 添加订单物流信息字段
ALTER TABLE `orders` ADD COLUMN `logistics_company` VARCHAR(100) DEFAULT NULL COMMENT '物流公司';
ALTER TABLE `orders` ADD COLUMN `logistics_no` VARCHAR(100) DEFAULT NULL COMMENT '物流单号';
ALTER TABLE `orders` ADD COLUMN `ship_time` DATETIME DEFAULT NULL COMMENT '发货时间';
