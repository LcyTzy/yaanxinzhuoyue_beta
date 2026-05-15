package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhantu.entity.Product;
import com.zhantu.mapper.ProductMapper;
import com.zhantu.service.CacheService;
import com.zhantu.service.ProductService;
import com.zhantu.service.ProductVehicleService;
import com.zhantu.service.ProductFavoriteService;
import com.zhantu.service.CategoryService;
import com.zhantu.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductVehicleService productVehicleService;
    private final ProductFavoriteService productFavoriteService;
    private final CategoryService categoryService;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    @Override
    public IPage<Product> getProductPage(Long categoryId, String keyword, String brand, Long vehicleModelId, String sortBy, String sortOrder, java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice, Integer pageNum, Integer pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Product::getStatus, 1);
        
        if (categoryId != null) {
            Category category = categoryService.getById(categoryId);
            if (category != null && category.getParentId() == 0) {
                List<Category> children = categoryService.list(
                    new LambdaQueryWrapper<Category>().eq(Category::getParentId, categoryId)
                );
                if (!children.isEmpty()) {
                    List<Long> categoryIds = new ArrayList<>();
                    categoryIds.add(categoryId);
                    children.forEach(c -> categoryIds.add(c.getId()));
                    wrapper.in(Product::getCategoryId, categoryIds);
                } else {
                    wrapper.eq(Product::getCategoryId, categoryId);
                }
            } else {
                wrapper.eq(Product::getCategoryId, categoryId);
            }
        }
        
        if (StringUtils.hasText(keyword)) {
            String trimmedKeyword = keyword.trim().toUpperCase();
            if (isVinCode(trimmedKeyword)) {
                wrapper.like(Product::getVinCompatible, trimmedKeyword);
            } else if (isOemNumber(trimmedKeyword)) {
                wrapper.and(w -> w.like(Product::getOem, trimmedKeyword)
                        .or().like(Product::getCode, trimmedKeyword));
            } else {
                wrapper.and(w -> w.like(Product::getName, keyword)
                        .or().like(Product::getCode, keyword)
                        .or().like(Product::getOem, keyword)
                        .or().like(Product::getSeries, keyword)
                        .or().like(Product::getBrand, keyword));
            }
            cacheService.incrementSearchHot(keyword.trim());
        }
        
        if (StringUtils.hasText(brand)) {
            wrapper.like(Product::getBrand, brand);
        }
        
        if (minPrice != null) {
            wrapper.ge(Product::getPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(Product::getPrice, maxPrice);
        }
        
        if (vehicleModelId != null) {
            List<Long> productIds = productVehicleService.getProductIdsByVehicleModelId(vehicleModelId);
            if (productIds.isEmpty()) {
                productIds.add(-1L);
            }
            wrapper.in(Product::getId, productIds);
        }
        
        if ("price".equals(sortBy)) {
            if ("asc".equals(sortOrder)) {
                wrapper.orderByAsc(Product::getPrice);
            } else {
                wrapper.orderByDesc(Product::getPrice);
            }
        } else if ("sales".equals(sortBy)) {
            wrapper.orderByDesc(Product::getSales);
        } else {
            wrapper.orderByDesc(Product::getCreateTime);
        }
        
        IPage<Product> result = this.page(page, wrapper);

        List<Long> productIds = result.getRecords().stream()
                .map(Product::getId).collect(Collectors.toList());
        if (!productIds.isEmpty()) {
            Map<Long, Long> favoriteCounts = productFavoriteService.getFavoriteCounts(productIds);
            result.getRecords().forEach(product -> {
                product.setFavoriteCount(favoriteCounts.getOrDefault(product.getId(), 0L).intValue());
            });
        }

        return result;
    }

    @Override
    public IPage<Product> getHotProducts(Integer pageNum, Integer pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, 1);
        wrapper.orderByDesc(Product::getSales);
        IPage<Product> result = this.page(page, wrapper);
        List<Long> productIds = result.getRecords().stream()
                .map(Product::getId).collect(Collectors.toList());
        if (!productIds.isEmpty()) {
            Map<Long, Long> favoriteCounts = productFavoriteService.getFavoriteCounts(productIds);
            result.getRecords().forEach(product -> {
                product.setFavoriteCount(favoriteCounts.getOrDefault(product.getId(), 0L).intValue());
            });
        }
        return result;
    }

    private boolean isVinCode(String keyword) {
        return keyword.length() == 17 && keyword.matches("^[A-HJ-NPR-Z0-9]+$");
    }

    private boolean isOemNumber(String keyword) {
        return keyword.matches("^[A-Z0-9-]{5,20}$");
    }

    @Override
    public IPage<Product> getAdminProductPage(Long categoryId, String keyword, String brand, Integer pageNum, Integer pageSize) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        
        if (categoryId != null) {
            Category category = categoryService.getById(categoryId);
            if (category != null && category.getParentId() == 0) {
                List<Category> children = categoryService.list(
                    new LambdaQueryWrapper<Category>().eq(Category::getParentId, categoryId)
                );
                if (!children.isEmpty()) {
                    List<Long> categoryIds = new ArrayList<>();
                    categoryIds.add(categoryId);
                    children.forEach(c -> categoryIds.add(c.getId()));
                    wrapper.in(Product::getCategoryId, categoryIds);
                } else {
                    wrapper.eq(Product::getCategoryId, categoryId);
                }
            } else {
                wrapper.eq(Product::getCategoryId, categoryId);
            }
        }
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Product::getName, keyword)
                    .or().like(Product::getCode, keyword)
                    .or().like(Product::getOem, keyword)
                    .or().like(Product::getSeries, keyword));
        }
        
        if (StringUtils.hasText(brand)) {
            wrapper.like(Product::getBrand, brand);
        }
        
        wrapper.orderByDesc(Product::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    public Product getProductById(Long id) {
        String cached = cacheService.getProductCache(id);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, Product.class);
            } catch (JsonProcessingException e) {
                cacheService.evictProductCache(id);
            }
        }
        
        Product product = this.getById(id);
        if (product != null) {
            product.setFavoriteCount((int) productFavoriteService.getFavoriteCount(id));
            try {
                cacheService.setProductCache(id, objectMapper.writeValueAsString(product));
            } catch (JsonProcessingException e) {
                // ignore cache error
            }
        }
        return product;
    }
}
