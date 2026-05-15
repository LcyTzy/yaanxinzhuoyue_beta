package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.Product;
import com.zhantu.entity.VehicleModel;
import com.zhantu.entity.VehiclePartRelation;
import com.zhantu.mapper.VehicleModelMapper;
import com.zhantu.service.EpcService;
import com.zhantu.service.ProductService;
import com.zhantu.service.VehiclePartRelationService;
import com.zhantu.service.VinDecoderManager;
import com.zhantu.model.VehicleInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EpcServiceImpl extends ServiceImpl<VehicleModelMapper, VehicleModel> implements EpcService {

    private final ProductService productService;
    private final VehiclePartRelationService vehiclePartRelationService;
    private final VinDecoderManager vinDecoderManager;

    @Override
    public Map<String, Object> queryByVin(String vin) {
        Map<String, Object> result = new HashMap<>();
        
        if (vin == null || vin.length() != 17) {
            result.put("valid", false);
            result.put("message", "Invalid VIN length");
            return result;
        }
        
        VehicleInfo info = vinDecoderManager.decode(vin.toUpperCase());
        if (info == null) {
            result.put("valid", false);
            result.put("message", "VIN decode failed");
            return result;
        }
        
        result.put("valid", true);
        result.put("brand", info.getBrandName());
        result.put("year", info.getYear());
        result.put("origin", "China");
        
        String vinPrefix = vin.substring(0, 3).toUpperCase();
        List<VehicleModel> models = getModelsByVinPrefix(vinPrefix);
        result.put("models", models);
        result.put("modelCount", models.size());
        
        return result;
    }

    @Override
    public List<VehicleModel> getModelsByVinPrefix(String vinPrefix) {
        if (!StringUtils.hasText(vinPrefix)) {
            return Collections.emptyList();
        }
        
        LambdaQueryWrapper<VehicleModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(VehicleModel::getVinPrefix, vinPrefix);
        wrapper.eq(VehicleModel::getStatus, 1);
        wrapper.orderByAsc(VehicleModel::getSort);
        
        return this.list(wrapper);
    }

    @Override
    public List<Map<String, Object>> getPartsByModelId(Long modelId) {
        List<VehiclePartRelation> relations = vehiclePartRelationService.list(
            new LambdaQueryWrapper<VehiclePartRelation>()
                .eq(VehiclePartRelation::getVehicleModelId, modelId)
        );
        
        if (relations.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Long> productIds = relations.stream()
                .map(VehiclePartRelation::getProductId)
                .collect(Collectors.toList());
        
        List<Product> products = productIds.isEmpty() ? java.util.Collections.emptyList() : productService.listByIds(productIds);
        
        Map<Long, String> positionMap = relations.stream()
                .collect(Collectors.toMap(
                    VehiclePartRelation::getProductId,
                    r -> r.getPosition() != null ? r.getPosition() : "",
                    (a, b) -> a
                ));
        
        return products.stream().map(product -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", product.getId());
            map.put("code", product.getCode());
            map.put("name", product.getName());
            map.put("oem", product.getOem());
            map.put("price", product.getPrice());
            map.put("stock", product.getStock());
            map.put("brand", product.getBrand());
            map.put("position", positionMap.get(product.getId()));
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> searchByKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Collections.emptyList();
        }
        
        String trimmedKeyword = keyword.trim().toUpperCase();
        
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Product::getOem, trimmedKeyword)
                .or().like(Product::getName, keyword)
                .or().like(Product::getCode, trimmedKeyword);
        wrapper.eq(Product::getStatus, 1);
        wrapper.last("LIMIT 50");
        
        List<Product> products = productService.list(wrapper);
        
        return products.stream().map(product -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", product.getId());
            map.put("code", product.getCode());
            map.put("name", product.getName());
            map.put("oem", product.getOem());
            map.put("price", product.getPrice());
            map.put("stock", product.getStock());
            map.put("brand", product.getBrand());
            
            List<VehiclePartRelation> relations = vehiclePartRelationService.list(
                new LambdaQueryWrapper<VehiclePartRelation>()
                    .eq(VehiclePartRelation::getProductId, product.getId())
                    .last("LIMIT 5")
            );
            
            List<String> vehicleNames = relations.stream().map(r -> {
                VehicleModel model = this.getById(r.getVehicleModelId());
                return model != null ? model.getName() : "";
            }).filter(StringUtils::hasText).collect(Collectors.toList());
            
            map.put("applicableVehicles", vehicleNames);
            return map;
        }).collect(Collectors.toList());
    }
}
