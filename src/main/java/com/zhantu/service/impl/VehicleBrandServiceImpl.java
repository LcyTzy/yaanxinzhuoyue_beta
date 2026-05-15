package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.VehicleBrand;
import com.zhantu.entity.VehicleModel;
import com.zhantu.entity.VehicleSeries;
import com.zhantu.mapper.VehicleBrandMapper;
import com.zhantu.service.VehicleBrandService;
import com.zhantu.service.VehicleModelService;
import com.zhantu.service.VehicleSeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleBrandServiceImpl extends ServiceImpl<VehicleBrandMapper, VehicleBrand> implements VehicleBrandService {

    private final VehicleSeriesService vehicleSeriesService;
    private final VehicleModelService vehicleModelService;

    @Override
    public List<VehicleBrand> getAllBrands() {
        LambdaQueryWrapper<VehicleBrand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VehicleBrand::getStatus, 1)
               .orderByAsc(VehicleBrand::getInitial)
               .orderByAsc(VehicleBrand::getSort);
        return this.list(wrapper);
    }

    @Override
    public List<VehicleBrand> getVehicleTree() {
        List<VehicleBrand> brands = this.getAllBrands();
        for (VehicleBrand brand : brands) {
            List<VehicleSeries> series = vehicleSeriesService.getSeriesByBrandId(brand.getId());
            for (VehicleSeries s : series) {
                List<VehicleModel> models = vehicleModelService.getModelsBySeriesId(s.getId());
                s.setModels(models);
            }
            brand.setSeriesList(series);
        }
        return brands;
    }
}
