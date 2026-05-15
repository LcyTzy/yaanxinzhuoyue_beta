package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.ProductVehicle;
import com.zhantu.mapper.ProductVehicleMapper;
import com.zhantu.service.ProductVehicleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductVehicleServiceImpl extends ServiceImpl<ProductVehicleMapper, ProductVehicle> implements ProductVehicleService {

    @Override
    public List<Long> getVehicleModelIdsByProductId(Long productId) {
        LambdaQueryWrapper<ProductVehicle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductVehicle::getProductId, productId);
        return this.list(wrapper).stream()
                .map(ProductVehicle::getVehicleModelId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void bindVehicleModels(Long productId, List<Long> vehicleModelIds) {
        LambdaQueryWrapper<ProductVehicle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductVehicle::getProductId, productId);
        this.remove(wrapper);
        
        if (vehicleModelIds != null && !vehicleModelIds.isEmpty()) {
            List<ProductVehicle> list = vehicleModelIds.stream()
                    .map(modelId -> {
                        ProductVehicle pv = new ProductVehicle();
                        pv.setProductId(productId);
                        pv.setVehicleModelId(modelId);
                        return pv;
                    })
                    .collect(Collectors.toList());
            this.saveBatch(list);
        }
    }

    @Override
    public List<Long> getProductIdsByVehicleModelId(Long vehicleModelId) {
        LambdaQueryWrapper<ProductVehicle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductVehicle::getVehicleModelId, vehicleModelId);
        return this.list(wrapper).stream()
                .map(ProductVehicle::getProductId)
                .collect(Collectors.toList());
    }
}
