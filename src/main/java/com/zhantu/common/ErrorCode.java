package com.zhantu.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    SUCCESS(200, "操作成功"),

    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_DISABLED(1003, "账号已被禁用"),
    USER_PHONE_EXISTS(1004, "手机号已注册"),
    USER_NOT_LOGIN(1005, "请先登录"),

    PRODUCT_NOT_FOUND(2001, "商品不存在"),
    PRODUCT_OFF_SHELF(2002, "商品已下架"),
    PRODUCT_STOCK_INSUFFICIENT(2003, "商品库存不足"),

    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态异常"),
    ORDER_CANNOT_CANCEL(3003, "订单无法取消"),
    ORDER_CANNOT_REFUND(3004, "订单无法退款"),
    ORDER_SYSTEM_BUSY(3005, "系统繁忙，请稍后重试"),

    CART_EMPTY(4001, "购物车为空"),

    COUPON_NOT_FOUND(5001, "优惠券不存在"),
    COUPON_UNAVAILABLE(5002, "优惠券不可用"),
    COUPON_MIN_AMOUNT(5003, "订单金额不满足优惠券使用条件"),

    CATEGORY_HAS_CHILDREN(6001, "该分类下存在子分类，无法删除"),
    CATEGORY_HAS_PRODUCTS(6002, "该分类下存在商品，无法删除"),

    VIN_INVALID(7001, "VIN码格式不正确"),
    VIN_NOT_FOUND(7002, "未查询到该车辆信息"),

    CAPTCHA_ERROR(8001, "验证码错误或已过期"),

    INTERNAL_ERROR(9999, "系统内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}