ALTER TABLE `orders` ADD COLUMN `coupon_discount` decimal(10,2) DEFAULT 0.00 AFTER `total_amount`;
ALTER TABLE `orders` ADD COLUMN `user_coupon_id` bigint DEFAULT NULL AFTER `pay_amount`;
