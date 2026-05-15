package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.common.BusinessException;
import com.zhantu.dto.CartVO;
import com.zhantu.entity.Cart;
import com.zhantu.entity.Product;
import com.zhantu.mapper.CartMapper;
import com.zhantu.service.CartService;
import com.zhantu.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final ProductService productService;

    @Override
    public void addToCart(Long userId, Long productId, Integer quantity) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId).eq(Cart::getProductId, productId);
        Cart existCart = this.getOne(wrapper);
        
        if (existCart != null) {
            existCart.setQuantity(existCart.getQuantity() + quantity);
            this.updateById(existCart);
        } else {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setChecked(1);
            this.save(cart);
        }
    }

    @Override
    public void updateCart(Long userId, Long cartId, Integer quantity) {
        Cart cart = this.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException(400, "购物车项不存在");
        }
        cart.setQuantity(quantity);
        this.updateById(cart);
    }

    @Override
    public void deleteCart(Long userId, Long cartId) {
        Cart cart = this.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException(400, "购物车项不存在");
        }
        this.removeById(cartId);
    }

    @Override
    public List<CartVO> getCartList(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId).orderByDesc(Cart::getCreateTime);
        List<Cart> carts = this.list(wrapper);
        
        List<Long> productIds = carts.stream().map(Cart::getProductId).collect(Collectors.toList());
        List<Product> products = productIds.isEmpty() ? java.util.Collections.emptyList() : productService.listByIds(productIds);
        
        return carts.stream().map(cart -> {
            CartVO vo = new CartVO();
            vo.setId(cart.getId());
            vo.setUserId(cart.getUserId());
            vo.setProductId(cart.getProductId());
            vo.setQuantity(cart.getQuantity());
            vo.setChecked(cart.getChecked());
            
            Product product = products.stream()
                .filter(p -> p.getId().equals(cart.getProductId()))
                .findFirst()
                .orElse(null);
            
            if (product != null) {
                vo.setProductName(product.getName());
                vo.setProductSubName(product.getSubName());
                vo.setProductImage(product.getImage());
                vo.setPrice(product.getPrice());
            }
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void clearCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        this.remove(wrapper);
    }
}
