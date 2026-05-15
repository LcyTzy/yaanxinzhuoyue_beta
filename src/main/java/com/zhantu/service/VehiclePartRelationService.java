package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.VehiclePartRelation;

import java.util.List;

public interface VehiclePartRelationService extends IService<VehiclePartRelation> {
    List<VehiclePartRelation> getPartsByModelId(Long modelId);
    List<Long> getProductIdsByModelId(Long modelId);
    void addRelation(Long modelId, Long productId, String position);
    void removeRelation(Long modelId, Long productId);
    void batchAddRelations(Long modelId, List<Long> productIds);
}
