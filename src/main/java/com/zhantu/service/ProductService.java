package com.zhantu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.Product;

public interface ProductService extends IService<Product> {
    IPage<Product> getProductPage(Long categoryId, String keyword, String brand, Long vehicleModelId, String sortBy, String sortOrder,
                                   java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice, Integer pageNum, Integer pageSize);
    IPage<Product> getAdminProductPage(Long categoryId, String keyword, String brand, Integer pageNum, Integer pageSize);
    IPage<Product> getHotProducts(Integer pageNum, Integer pageSize);
    Product getProductById(Long id);
}
