-- 战途汽配商城数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS autoparts DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE autoparts;

-- 分类表
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父分类ID，0表示一级分类',
    `level` INT NOT NULL DEFAULT 1 COMMENT '分类层级',
    `sort` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `icon` VARCHAR(255) DEFAULT NULL COMMENT '图标',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `code` VARCHAR(50) NOT NULL COMMENT '商品编码',
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `sub_name` VARCHAR(200) DEFAULT NULL COMMENT '商品副标题/描述',
    `oem` VARCHAR(100) DEFAULT NULL COMMENT 'OEM编号',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `stock` INT NOT NULL DEFAULT 999 COMMENT '库存',
    `category_id` BIGINT NOT NULL COMMENT '分类ID',
    `series` VARCHAR(100) DEFAULT NULL COMMENT '适用车系',
    `quality_grade` VARCHAR(50) DEFAULT NULL COMMENT '质量等级',
    `viscosity` VARCHAR(50) DEFAULT NULL COMMENT '粘度（油品专用）',
    `spec` VARCHAR(100) DEFAULT NULL COMMENT '规格',
    `unit` VARCHAR(20) DEFAULT '件' COMMENT '单位',
    `brand` VARCHAR(100) DEFAULT NULL COMMENT '适用品牌',
    `image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0下架 1上架',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_code` (`code`),
    INDEX `idx_category_id` (`category_id`),
    INDEX `idx_brand` (`brand`),
    INDEX `idx_name` (`name`),
    INDEX `idx_oem` (`oem`),
    INDEX `idx_series` (`series`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
    `password` VARCHAR(255) DEFAULT NULL COMMENT '密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：USER用户 ADMIN管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 购物车表
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '数量',
    `checked` TINYINT NOT NULL DEFAULT 1 COMMENT '是否选中：0未选中 1选中',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    UNIQUE INDEX `uk_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 订单表
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '订单状态：0待付款 1已付款 2已发货 3已完成 4已取消',
    `receiver_name` VARCHAR(50) DEFAULT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) DEFAULT NULL COMMENT '收货人电话',
    `receiver_address` VARCHAR(500) DEFAULT NULL COMMENT '收货地址',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_order_no` (`order_no`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单商品表
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单商品ID',
    `order_id` BIGINT NOT NULL COMMENT '订单ID',
    `product_id` BIGINT NOT NULL COMMENT '商品ID',
    `product_name` VARCHAR(200) NOT NULL COMMENT '商品名称快照',
    `product_code` VARCHAR(50) NOT NULL COMMENT '商品编码快照',
    `product_image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片快照',
    `price` DECIMAL(10,2) NOT NULL COMMENT '购买时价格',
    `quantity` INT NOT NULL COMMENT '购买数量',
    `total_price` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    PRIMARY KEY (`id`),
    INDEX `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品表';

-- 收货地址表
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地址ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `receiver_name` VARCHAR(50) DEFAULT NULL COMMENT '收货人姓名',
    `receiver_phone` VARCHAR(20) DEFAULT NULL COMMENT '收货人电话',
    `province` VARCHAR(50) DEFAULT NULL COMMENT '省份',
    `city` VARCHAR(50) DEFAULT NULL COMMENT '城市',
    `district` VARCHAR(50) DEFAULT NULL COMMENT '区县',
    `detail` VARCHAR(500) DEFAULT NULL COMMENT '详细地址',
    `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认：0否 1是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';
