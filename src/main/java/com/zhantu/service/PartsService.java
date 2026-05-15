package com.zhantu.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhantu.entity.Category;
import com.zhantu.entity.Product;
import com.zhantu.mapper.CategoryMapper;
import com.zhantu.mapper.ProductMapper;
import com.zhantu.model.PartCategoryItem;
import com.zhantu.model.VehicleInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartsService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    public Map<String, List<PartCategoryItem>> getCategorizedParts(VehicleInfo vehicle) {
        List<Product> matchedProducts = new ArrayList<>();

        // 策略1：精确匹配 group_code
        if (StringUtils.isNotBlank(vehicle.getGroupCode())) {
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Product::getGroupCode, vehicle.getGroupCode());
            wrapper.eq(Product::getStatus, 1);
            wrapper.eq(Product::getDeleted, 0);
            matchedProducts = productMapper.selectList(wrapper);
        }

        // 策略2：品牌+车系模糊匹配
        if (matchedProducts.isEmpty() && StringUtils.isNoneBlank(vehicle.getBrandName(), vehicle.getSeriesName())) {
            matchedProducts = matchByBrandAndSeries(vehicle.getBrandName(), vehicle.getSeriesName());
        }

        // 策略3：仅品牌匹配
        if (matchedProducts.isEmpty() && StringUtils.isNotBlank(vehicle.getBrandName())) {
            matchedProducts = matchByBrand(vehicle.getBrandName());
        }

        // 策略4：仅车系匹配
        if (matchedProducts.isEmpty() && StringUtils.isNotBlank(vehicle.getSeriesName())) {
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.like(Product::getSeries, vehicle.getSeriesName());
            wrapper.eq(Product::getStatus, 1);
            wrapper.eq(Product::getDeleted, 0);
            matchedProducts = productMapper.selectList(wrapper);
        }

        // 策略5：通用配件匹配（brand或series包含"通用"）
        if (matchedProducts.isEmpty()) {
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.and(w -> w.like(Product::getBrand, "通用").or().like(Product::getSeries, "通用"));
            wrapper.eq(Product::getStatus, 1);
            wrapper.eq(Product::getDeleted, 0);
            matchedProducts = productMapper.selectList(wrapper);
        }

        Map<Long, String> categoryMap = buildCategoryMap();

        return matchedProducts.stream()
                .map(p -> convertToCategoryItem(p, categoryMap, vehicle))
                .collect(Collectors.groupingBy(PartCategoryItem::getCategory, LinkedHashMap::new, Collectors.toList()));
    }

    private Map<Long, String> buildCategoryMap() {
        List<Category> categories = categoryMapper.selectList(null);
        return categories.stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
    }

    private PartCategoryItem convertToCategoryItem(Product product, Map<Long, String> categoryMap, VehicleInfo vehicle) {
        PartCategoryItem item = new PartCategoryItem();
        item.setPartId(product.getId().toString());
        item.setName(product.getName());
        item.setOeNumber(product.getOeNumber());
        item.setBrand(product.getBrand());
        item.setCode(product.getCode());
        item.setSubName(product.getSubName());
        item.setPrice(product.getPrice());
        item.setStock(product.getStock());
        item.setImageUrl(product.getImage());

        String categoryName = categoryMap.getOrDefault(product.getCategoryId(), "其他");
        item.setCategory(categoryName);

        item.setSpecs(product.getSpec());

        Map<String, String> specDetails = parseSpecDetails(product.getSpec());
        item.setSpecDetails(specDetails);

        String compatibleInfo = buildCompatibleInfo(vehicle);
        item.setCompatibleInfo(compatibleInfo);

        // 添加养护指导信息
        Map<String, String> maintenanceInfo = buildMaintenanceInfo(product, vehicle);
        item.setMaintenanceInfo(maintenanceInfo);

        return item;
    }

    private Map<String, String> parseSpecDetails(String spec) {
        Map<String, String> details = new LinkedHashMap<>();
        if (StringUtils.isBlank(spec)) {
            return details;
        }

        String[] parts = spec.split("[/\\s]+");
        for (String part : parts) {
            part = part.trim();
            if (part.contains(":") || part.contains("：")) {
                String[] kv = part.split("[:：]", 2);
                if (kv.length == 2) {
                    details.put(kv[0].trim(), kv[1].trim());
                }
            } else if (part.matches(".*\\d+.*mm.*")) {
                if (part.toLowerCase().contains("内径")) {
                    details.put("内径", part.replaceAll("[^0-9.]", ""));
                } else if (part.toLowerCase().contains("外径")) {
                    details.put("外径", part.replaceAll("[^0-9.]", ""));
                } else if (part.toLowerCase().contains("高")) {
                    details.put("高", part.replaceAll("[^0-9.]", ""));
                }
            }
        }

        return details;
    }

    private String buildCompatibleInfo(VehicleInfo vehicle) {
        List<String> parts = new ArrayList<>();
        if (StringUtils.isNotBlank(vehicle.getBrandName())) {
            parts.add(vehicle.getBrandName());
        }
        if (StringUtils.isNotBlank(vehicle.getSeriesName())) {
            parts.add(vehicle.getSeriesName());
        }
        if (StringUtils.isNotBlank(vehicle.getYear())) {
            parts.add(vehicle.getYear() + "款");
        }
        if (StringUtils.isNotBlank(vehicle.getEngineModel())) {
            parts.add("发动机型号:" + vehicle.getEngineModel());
        }
        return String.join(" ", parts);
    }

    private Map<String, String> buildMaintenanceInfo(Product product, VehicleInfo vehicle) {
        Map<String, String> info = new LinkedHashMap<>();
        
        String categoryName = product.getSeries() != null ? product.getSeries() : "";
        
        // 根据配件类型添加养护指导
        if (categoryName.contains("机油") || product.getName().contains("机油")) {
            info.put("更换周期", "5000-10000公里");
            info.put("更换量", product.getSpec() != null ? product.getSpec() : "4L");
            info.put("安装位置", "发动机");
        } else if (categoryName.contains("滤清器") || product.getName().contains("滤清器")) {
            info.put("更换周期", "10000-20000公里");
            info.put("安装位置", getFilterPosition(product.getName()));
        } else if (categoryName.contains("雨刮") || product.getName().contains("雨刮")) {
            info.put("更换周期", "12个月");
            info.put("安装位置", "前挡风玻璃");
        } else if (categoryName.contains("灯泡") || product.getName().contains("灯泡")) {
            info.put("更换周期", "损坏时更换");
            info.put("安装位置", getLightPosition(product.getName()));
        } else if (categoryName.contains("变速箱") || product.getName().contains("变速箱")) {
            info.put("更换周期", "40000-60000公里");
            info.put("更换量", product.getSpec() != null ? product.getSpec() : "6L");
            info.put("安装位置", "变速箱");
        } else if (categoryName.contains("刹车") || product.getName().contains("刹车")) {
            info.put("更换周期", "20000-40000公里");
            info.put("安装位置", "刹车系统");
        }
        
        return info;
    }

    private String getFilterPosition(String productName) {
        if (productName.contains("机油")) return "发动机机油路";
        if (productName.contains("空气")) return "发动机进气系统";
        if (productName.contains("汽油")) return "燃油系统";
        if (productName.contains("空调")) return "空调系统";
        return "过滤系统";
    }

    private String getLightPosition(String productName) {
        if (productName.contains("近光")) return "前大灯-近光";
        if (productName.contains("远光")) return "前大灯-远光";
        if (productName.contains("雾灯")) return "雾灯";
        if (productName.contains("刹车")) return "尾灯-刹车灯";
        return "照明系统";
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
