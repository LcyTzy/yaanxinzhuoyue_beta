package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.ProductSku;

import java.util.List;

public interface ProductSkuService extends IService<ProductSku> {
    List<ProductSku> getSkusByProductId(Long productId);
    void saveSkus(Long productId, List<ProductSku> skus);
}