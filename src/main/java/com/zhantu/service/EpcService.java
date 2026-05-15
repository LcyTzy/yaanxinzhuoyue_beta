package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.VehicleModel;

import java.util.List;
import java.util.Map;

public interface EpcService extends IService<VehicleModel> {
    Map<String, Object> queryByVin(String vin);
    List<VehicleModel> getModelsByVinPrefix(String vinPrefix);
    List<Map<String, Object>> getPartsByModelId(Long modelId);
    List<Map<String, Object>> searchByKeyword(String keyword);
}
