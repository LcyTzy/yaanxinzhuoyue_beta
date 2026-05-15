package com.zhantu.service;

import com.zhantu.service.impl.OrdersServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrdersServiceTest {

    @Autowired
    private OrdersService ordersService;

    @Test
    void testCreateOrderWithEmptyCart() {
        assertThrows(RuntimeException.class, () -> {
            ordersService.createOrder(1L, java.util.Collections.emptyList(),
                    "测试用户", "13800138000", "测试地址", null, null);
        });
    }

    @Test
    void testGetOrderDetailWithInvalidId() {
        assertThrows(RuntimeException.class, () -> {
            ordersService.getOrderDetail(1L, 999999L);
        });
    }
}
