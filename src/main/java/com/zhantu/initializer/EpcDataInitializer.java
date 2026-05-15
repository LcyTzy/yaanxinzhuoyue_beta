package com.zhantu.initializer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhantu.entity.*;
import com.zhantu.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class EpcDataInitializer implements CommandLineRunner {

    private final VehicleBrandService vehicleBrandService;
    private final VehicleSeriesService vehicleSeriesService;
    private final VehicleModelService vehicleModelService;
    private final VehiclePartRelationService vehiclePartRelationService;
    private final ProductService productService;

    private final Map<String, Long> brandCache = new HashMap<>();
    private final Map<String, Long> seriesCache = new HashMap<>();
    private final Map<String, Long> modelCache = new HashMap<>();

    @Override
    public void run(String... args) {
        long count = vehicleBrandService.count();
        if (count > 0) {
            log.info("EPC数据已存在，跳过初始化");
            return;
        }
        log.info("开始初始化EPC数据...");
        initVehicleData();
        initPartRelations();
        log.info("EPC数据初始化完成");
    }

    private void initVehicleData() {
        Map<String, Map<String, String[]>> vehicleData = new HashMap<>();

        vehicleData.put("大众", Map.of(
            "朗逸", new String[]{"2023", "2022", "2021"},
            "速腾", new String[]{"2023", "2022", "2021"},
            "帕萨特", new String[]{"2023", "2022"},
            "途观", new String[]{"2023", "2022"}
        ));
        vehicleData.put("一汽-大众", Map.of(
            "迈腾", new String[]{"2023", "2022", "2021"},
            "宝来", new String[]{"2023", "2022"},
            "探岳", new String[]{"2023", "2022"}
        ));
        vehicleData.put("丰田", Map.of(
            "凯美瑞", new String[]{"2023", "2022", "2021"},
            "卡罗拉", new String[]{"2023", "2022", "2021"},
            "RAV4", new String[]{"2023", "2022"}
        ));
        vehicleData.put("本田", Map.of(
            "雅阁", new String[]{"2023", "2022", "2021"},
            "思域", new String[]{"2023", "2022", "2021"},
            "CR-V", new String[]{"2023", "2022"}
        ));
        vehicleData.put("日产", Map.of(
            "轩逸", new String[]{"2023", "2022", "2021"},
            "天籁", new String[]{"2023", "2022"}
        ));
        vehicleData.put("现代", Map.of(
            "伊兰特", new String[]{"2023", "2022"},
            "索纳塔", new String[]{"2023", "2022"}
        ));
        vehicleData.put("北京现代", Map.of(
            "途胜", new String[]{"2023", "2022"},
            "ix35", new String[]{"2023", "2022"}
        ));
        vehicleData.put("别克", Map.of(
            "英朗", new String[]{"2023", "2022"},
            "君威", new String[]{"2023", "2022"}
        ));
        vehicleData.put("福特", Map.of(
            "福克斯", new String[]{"2023", "2022"},
            "蒙迪欧", new String[]{"2023", "2022"}
        ));
        vehicleData.put("雪佛兰", Map.of(
            "科鲁兹", new String[]{"2023", "2022"},
            "迈锐宝", new String[]{"2023", "2022"}
        ));
        vehicleData.put("宝马", Map.of(
            "3系", new String[]{"2023", "2022", "2021"},
            "5系", new String[]{"2023", "2022"},
            "X3", new String[]{"2023", "2022"}
        ));
        vehicleData.put("华晨宝马", Map.of(
            "X1", new String[]{"2023", "2022"},
            "1系", new String[]{"2023", "2022"}
        ));
        vehicleData.put("奔驰", Map.of(
            "C级", new String[]{"2023", "2022", "2021"},
            "E级", new String[]{"2023", "2022"},
            "GLC", new String[]{"2023", "2022"}
        ));
        vehicleData.put("奥迪", Map.of(
            "A4L", new String[]{"2023", "2022", "2021"},
            "A6L", new String[]{"2023", "2022"},
            "Q5", new String[]{"2023", "2022"}
        ));

        for (Map.Entry<String, Map<String, String[]>> brandEntry : vehicleData.entrySet()) {
            String brandName = brandEntry.getKey();
            VehicleBrand brand = new VehicleBrand();
            brand.setName(brandName);
            brand.setInitial(brandName.substring(0, 1));
            brand.setSort(0);
            brand.setStatus(1);
            vehicleBrandService.save(brand);
            brandCache.put(brandName, brand.getId());

            for (Map.Entry<String, String[]> seriesEntry : brandEntry.getValue().entrySet()) {
                String seriesName = seriesEntry.getKey();
                VehicleSeries series = new VehicleSeries();
                series.setBrandId(brand.getId());
                series.setName(seriesName);
                series.setSort(0);
                series.setStatus(1);
                vehicleSeriesService.save(series);
                seriesCache.put(brandName + "-" + seriesName, series.getId());

                for (String year : seriesEntry.getValue()) {
                    VehicleModel model = new VehicleModel();
                    model.setSeriesId(series.getId());
                    model.setName(seriesName + " " + year + "款");
                    model.setYear(year);
                    model.setStatus(1);
                    model.setSort(0);
                    vehicleModelService.save(model);
                    modelCache.put(brandName + "-" + seriesName + "-" + year, model.getId());
                }
            }
        }
    }

    private void initPartRelations() {
        List<Product> products = productService.list();
        Pattern pattern = Pattern.compile("(一汽-大众|华晨宝马|北京现代|大众|丰田|本田|日产|现代|别克|福特|雪佛兰|宝马|奔驰|奥迪)(.*?)(\\d{4})");

        for (Product product : products) {
            if (product.getSeries() == null || product.getSeries().isEmpty()) {
                continue;
            }
            Matcher matcher = pattern.matcher(product.getSeries());
            if (matcher.find()) {
                String brand = matcher.group(1);
                String series = matcher.group(2).trim();
                String year = matcher.group(3);

                String modelKey = brand + "-" + series + "-" + year;
                Long modelId = modelCache.get(modelKey);

                if (modelId != null) {
                    VehiclePartRelation relation = new VehiclePartRelation();
                    relation.setVehicleModelId(modelId);
                    relation.setProductId(product.getId());
                    vehiclePartRelationService.save(relation);
                }
            }
        }
    }
}
