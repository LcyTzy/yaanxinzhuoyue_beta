package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.VehicleSeries;
import com.zhantu.mapper.VehicleSeriesMapper;
import com.zhantu.service.VehicleSeriesService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleSeriesServiceImpl extends ServiceImpl<VehicleSeriesMapper, VehicleSeries> implements VehicleSeriesService {

    @Override
    public List<VehicleSeries> getSeriesByBrandId(Long brandId) {
        LambdaQueryWrapper<VehicleSeries> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehicleSeries::getBrandId, brandId)
               .eq(VehicleSeries::getStatus, 1)
               .orderByAsc(VehicleSeries::getSort);
        return this.list(wrapper);
    }
}
