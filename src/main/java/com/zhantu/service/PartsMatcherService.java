package com.zhantu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhantu.entity.Product;
import com.zhantu.mapper.ProductMapper;
import com.zhantu.model.VehicleInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PartsMatcherService {

    @Autowired
    private ProductMapper productMapper;

    public List<Product> getMatchingParts(VehicleInfo vehicle) {
        if (vehicle == null) {
            return Collections.emptyList();
        }

        List<Product> matchedProducts = new ArrayList<>();

        if (StringUtils.isNotBlank(vehicle.getGroupCode())) {
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Product::getGroupCode, vehicle.getGroupCode());
            wrapper.eq(Product::getStatus, 1);
            wrapper.eq(Product::getDeleted, 0);
            matchedProducts = productMapper.selectList(wrapper);
        }

        if (matchedProducts.isEmpty() && StringUtils.isNoneBlank(vehicle.getBrandName(), vehicle.getSeriesName())) {
            matchedProducts = matchByBrandAndSeries(vehicle.getBrandName(), vehicle.getSeriesName());
        }

        if (matchedProducts.isEmpty() && StringUtils.isNotBlank(vehicle.getBrandName())) {
            matchedProducts = matchByBrand(vehicle.getBrandName());
        }

        if (matchedProducts.isEmpty() && StringUtils.isNotBlank(vehicle.getSeriesName())) {
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(Product::getSeries, vehicle.getSeriesName());
            wrapper.eq(Product::getStatus, 1);
            wrapper.eq(Product::getDeleted, 0);
            matchedProducts = productMapper.selectList(wrapper);
        }

        if (matchedProducts.isEmpty()) {
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.and(w -> w.like(Product::getBrand, "通用").or().like(Product::getSeries, "通用"));
            wrapper.eq(Product::getStatus, 1);
            wrapper.eq(Product::getDeleted, 0);
            matchedProducts = productMapper.selectList(wrapper);
        }

        return matchedProducts;
    }

    private List<Product> matchByBrand(String brandName) {
        List<String> brandCandidates = extractBrandCandidates(brandName);
        for (String candidate : brandCandidates) {
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(Product::getBrand, candidate);
            wrapper.eq(Product::getStatus, 1);
            wrapper.eq(Product::getDeleted, 0);
            List<Product> result = productMapper.selectList(wrapper);
            if (!result.isEmpty()) {
                return result;
            }
        }
        return Collections.emptyList();
    }

    private List<Product> matchByBrandAndSeries(String brandName, String seriesName) {
        List<String> brandCandidates = extractBrandCandidates(brandName);
        for (String candidate : brandCandidates) {
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(Product::getBrand, candidate);
            wrapper.like(Product::getSeries, seriesName);
            wrapper.eq(Product::getStatus, 1);
            wrapper.eq(Product::getDeleted, 0);
            List<Product> result = productMapper.selectList(wrapper);
            if (!result.isEmpty()) {
                return result;
            }
        }
        return Collections.emptyList();
    }

    private List<String> extractBrandCandidates(String fullBrandName) {
        List<String> candidates = new ArrayList<>();
        candidates.add(fullBrandName);

        String core = fullBrandName
                .replace("华晨", "")
                .replace("北京", "")
                .replace("一汽", "")
                .replace("广汽", "")
                .replace("东风", "")
                .replace("长安", "")
                .replace("上汽", "")
                .replace("吉利", "")
                .replace("-", "")
                .replace("·", "")
                .trim();

        if (!core.isEmpty() && !core.equals(fullBrandName)) {
            candidates.add(core);
        }

        return candidates;
    }
}
