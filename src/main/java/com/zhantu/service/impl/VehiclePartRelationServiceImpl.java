package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.VehiclePartRelation;
import com.zhantu.mapper.VehiclePartRelationMapper;
import com.zhantu.service.VehiclePartRelationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehiclePartRelationServiceImpl extends ServiceImpl<VehiclePartRelationMapper, VehiclePartRelation> implements VehiclePartRelationService {

    @Override
    public List<VehiclePartRelation> getPartsByModelId(Long modelId) {
        LambdaQueryWrapper<VehiclePartRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehiclePartRelation::getVehicleModelId, modelId);
        return this.list(wrapper);
    }

    @Override
    public List<Long> getProductIdsByModelId(Long modelId) {
        List<VehiclePartRelation> relations = getPartsByModelId(modelId);
        return relations.stream()
                .map(VehiclePartRelation::getProductId)
                .collect(Collectors.toList());
    }

    @Override
    public void addRelation(Long modelId, Long productId, String position) {
        VehiclePartRelation relation = new VehiclePartRelation();
        relation.setVehicleModelId(modelId);
        relation.setProductId(productId);
        relation.setPosition(position);
        this.save(relation);
    }

    @Override
    public void removeRelation(Long modelId, Long productId) {
        LambdaQueryWrapper<VehiclePartRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehiclePartRelation::getVehicleModelId, modelId)
               .eq(VehiclePartRelation::getProductId, productId);
        this.remove(wrapper);
    }

    @Override
    public void batchAddRelations(Long modelId, List<Long> productIds) {
        for (Long productId : productIds) {
            addRelation(modelId, productId, null);
        }
    }
}
