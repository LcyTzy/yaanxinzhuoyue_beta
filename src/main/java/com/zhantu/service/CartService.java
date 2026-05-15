package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.dto.CartVO;
import com.zhantu.entity.Cart;

import java.util.List;

public interface CartService extends IService<Cart> {
    void addToCart(Long userId, Long productId, Integer quantity);
    void updateCart(Long userId, Long cartId, Integer quantity);
    void deleteCart(Long userId, Long cartId);
    List<CartVO> getCartList(Long userId);
    void clearCart(Long userId);
}
