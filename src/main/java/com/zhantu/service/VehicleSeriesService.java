package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.VehicleSeries;

import java.util.List;

public interface VehicleSeriesService extends IService<VehicleSeries> {
    List<VehicleSeries> getSeriesByBrandId(Long brandId);
}
