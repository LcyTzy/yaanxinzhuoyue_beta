package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.ProductSku;
import com.zhantu.mapper.ProductSkuMapper;
import com.zhantu.service.ProductSkuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductSkuServiceImpl extends ServiceImpl<ProductSkuMapper, ProductSku> implements ProductSkuService {

    @Override
    public List<ProductSku> getSkusByProductId(Long productId) {
        return list(new LambdaQueryWrapper<ProductSku>()
                .eq(ProductSku::getProductId, productId));
    }

    @Override
    @Transactional
    public void saveSkus(Long productId, List<ProductSku> skus) {
        remove(new LambdaQueryWrapper<ProductSku>().eq(ProductSku::getProductId, productId));
        for (ProductSku sku : skus) {
            sku.setProductId(productId);
            save(sku);
        }
    }
}