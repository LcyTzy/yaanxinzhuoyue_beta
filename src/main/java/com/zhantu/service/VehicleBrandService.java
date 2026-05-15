package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.VehicleBrand;

import java.util.List;

public interface VehicleBrandService extends IService<VehicleBrand> {
    List<VehicleBrand> getAllBrands();
    List<VehicleBrand> getVehicleTree();
}
