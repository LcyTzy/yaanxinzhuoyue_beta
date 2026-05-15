package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.ProductVehicle;

import java.util.List;

public interface ProductVehicleService extends IService<ProductVehicle> {
    List<Long> getVehicleModelIdsByProductId(Long productId);
    void bindVehicleModels(Long productId, List<Long> vehicleModelIds);
    List<Long> getProductIdsByVehicleModelId(Long vehicleModelId);
}
