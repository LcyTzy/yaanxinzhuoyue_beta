-- 安全与合规升级脚本

USE autoparts;

-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
    `username` VARCHAR(50) DEFAULT NULL COMMENT '用户名/角色',
    `module` VARCHAR(50) DEFAULT NULL COMMENT '模块',
    `operation` VARCHAR(100) DEFAULT NULL COMMENT '操作',
    `request_uri` VARCHAR(255) DEFAULT NULL COMMENT '请求URI',
    `request_method` VARCHAR(10) DEFAULT NULL COMMENT '请求方法',
    `request_params` TEXT DEFAULT NULL COMMENT '请求参数',
    `ip` VARCHAR(50) DEFAULT NULL COMMENT 'IP地址',
    `status` TINYINT DEFAULT NULL COMMENT '状态：0失败 1成功',
    `error_msg` TEXT DEFAULT NULL COMMENT '错误信息',
    `execution_time` BIGINT DEFAULT NULL COMMENT '执行时间(ms)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 订单表增加字段
ALTER TABLE `orders` ADD COLUMN IF NOT EXISTS `pay_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '实付金额';
ALTER TABLE `orders` ADD COLUMN IF NOT EXISTS `coupon_discount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '优惠券抵扣金额';
ALTER TABLE `orders` ADD COLUMN IF NOT EXISTS `user_coupon_id` BIGINT DEFAULT NULL COMMENT '使用的优惠券ID';
ALTER TABLE `orders` ADD COLUMN IF NOT EXISTS `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间';
ALTER TABLE `orders` ADD COLUMN IF NOT EXISTS `ship_time` DATETIME DEFAULT NULL COMMENT '发货时间';
ALTER TABLE `orders` ADD COLUMN IF NOT EXISTS `logistics_company` VARCHAR(50) DEFAULT NULL COMMENT '物流公司';
ALTER TABLE `orders` ADD COLUMN IF NOT EXISTS `logistics_no` VARCHAR(50) DEFAULT NULL COMMENT '物流单号';

-- 退款表
CREATE TABLE IF NOT EXISTS `refund_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '退款ID',
    `refund_no` VARCHAR(50) NOT NULL COMMENT '退款单号',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `refund_amount` DECIMAL(10,2) NOT NULL COMMENT '退款金额',
    `refund_reason` VARCHAR(500) DEFAULT NULL COMMENT '退款原因',
    `refund_images` TEXT DEFAULT NULL COMMENT '退款凭证图片',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待审核 1审核通过 2审核拒绝 3退款中 4退款完成',
    `audit_remark` VARCHAR(500) DEFAULT NULL COMMENT '审核备注',
    `refund_time` DATETIME DEFAULT NULL COMMENT '退款完成时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_refund_no` (`refund_no`),
    INDEX `idx_order_id` (`order_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款订单表';

-- 搜索历史表
CREATE TABLE IF NOT EXISTS `search_history` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `keyword` VARCHAR(100) NOT NULL COMMENT '搜索关键词',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_keyword` (`keyword`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索历史表';

-- 用户表增加隐私同意字段
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `privacy_agreed` TINYINT DEFAULT 0 COMMENT '是否同意隐私协议：0否 1是';
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS `privacy_agreed_time` DATETIME DEFAULT NULL COMMENT '同意隐私协议时间';
