package com.zhantu.initializer;

import com.zhantu.entity.Category;
import com.zhantu.entity.Product;
import com.zhantu.service.CategoryService;
import com.zhantu.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryService categoryService;
    private final ProductService productService;

    @Override
    public void run(String... args) {
        if (categoryService.count() > 0) {
            log.info("数据已存在，跳过初始化");
            return;
        }

        log.info("开始初始化分类数据...");
        initCategories();
        
        log.info("开始初始化商品数据...");
        initProducts();
        
        log.info("数据初始化完成!");
    }

    private void initCategories() {
        List<Category> categories = new ArrayList<>();
        
        categories.add(createCategory(1L, "变速箱油", 0L, 1, 1));
        categories.add(createCategory(2L, "发动机油", 0L, 1, 2));
        categories.add(createCategory(3L, "滤清器", 0L, 1, 3));
        categories.add(createCategory(4L, "辅助油品", 0L, 1, 4));
        
        categories.add(createCategory(31L, "空气滤清器", 3L, 2, 1));
        categories.add(createCategory(32L, "空调滤清器", 3L, 2, 2));
        categories.add(createCategory(33L, "机油滤清器", 3L, 2, 3));
        categories.add(createCategory(34L, "汽油滤清器", 3L, 2, 4));
        categories.add(createCategory(35L, "变速箱滤清器", 3L, 2, 5));
        
        categoryService.saveBatch(categories);
        log.info("分类数据初始化完成，共 {} 条", categories.size());
    }

    private Category createCategory(Long id, String name, Long parentId, int level, int sort) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setParentId(parentId);
        category.setLevel(level);
        category.setSort(sort);
        category.setStatus(1);
        return category;
    }

    private void initProducts() {
        List<Product> products = new ArrayList<>();
        int id = 1;

        products.add(createProduct(id++, "ATF-5S-001", "全合成自动变速箱油 ATF 5S", "适用于4-5速自动变速箱", null,
                new BigDecimal("19.30"), 1L, "丰田,本田,现代,马自达,大众,雷克萨斯,三菱", "全合成", "ATF 5S", "1L*12", "桶", "丰田,本田,现代,马自达,大众,雷克萨斯,三菱"));
        products.add(createProduct(id++, "ATF-6SP-001", "全合成自动变速箱油 ATF 6SP", "适用于6速自动变速箱", null,
                new BigDecimal("24.70"), 1L, "丰田,本田,现代,马自达,大众,雷克萨斯,三菱", "全合成", "ATF 6SP", "1L*12", "桶", "丰田,本田,现代,马自达,大众,雷克萨斯,三菱"));
        products.add(createProduct(id++, "ATF-VI-001", "全合成自动变速箱油 ATF VI", "高级车型6速有线线性电磁阀控制变速器", null,
                new BigDecimal("24.70"), 1L, "高级车型通用", "全合成", "ATF VI", "1L*12", "桶", "通用"));
        products.add(createProduct(id++, "ATF-6HP-001", "全合成自动变速箱油 ATF 6HP", "德系车后驱动ZF6HP系列6速有级智能变速器", null,
                new BigDecimal("24.90"), 1L, "德系车", "全合成", "ATF 6HP", "1L*12", "桶", "宝马,奥迪,保时捷"));
        products.add(createProduct(id++, "ATF-8HP-001", "全合成自动变速箱油 ATF 8HP", "德系车后驱动ZF8HP系列8速有级智能变速器", null,
                new BigDecimal("27.70"), 1L, "德系车", "全合成", "ATF 8HP", "1L*12", "桶", "宝马,奥迪,保时捷"));
        products.add(createProduct(id++, "ATF-8SP-001", "全合成自动变速箱油 ATF 8SP", "适用于奥迪Q7、保时捷、途锐3.0T", null,
                new BigDecimal("27.70"), 1L, "奥迪,保时捷,大众", "全合成", "ATF 8SP", "1L*12", "桶", "奥迪,保时捷,大众"));
        products.add(createProduct(id++, "ATF-9HP-001", "全合成自动变速箱油 ATF 9HP", "适用于极光路虎、丰田、自由光、捷豹14年后", null,
                new BigDecimal("34.00"), 1L, "路虎,丰田,Jeep,捷豹", "全合成", "ATF 9HP", "1L*12", "桶", "路虎,丰田,Jeep,捷豹"));
        products.add(createProduct(id++, "ATF-722-001", "全合成自动变速箱油 ATF 722.9", "用于奔驰系列722.9二代含1代7速有机智能变速器", null,
                new BigDecimal("28.00"), 1L, "奔驰", "全合成", "ATF 722.9", "1L*12", "桶", "奔驰"));
        products.add(createProduct(id++, "CVT-NS3-001", "全合成无极变速箱油 CVT NS-3", "捷特科非标专用无极CVT结构", null,
                new BigDecimal("28.00"), 1L, "日产,斯巴鲁,三菱,雷诺", "全合成", "CVT NS-3", "1L*12", "桶", "日产,斯巴鲁,三菱,雷诺"));
        products.add(createProduct(id++, "CVT-F2-001", "全合成无极变速箱油 CVT F2", "低排量紧凑型无极CVT结构", null,
                new BigDecimal("28.00"), 1L, "日系车,邦奇,宝马MINI,奔驰B系", "全合成", "CVT F2", "1L*12", "桶", "日产,宝马,奔驰"));
        products.add(createProduct(id++, "CVT-PLUS-001", "全合成无极变速箱油 CVT+", "德系车无极CVT变速器含模拟档6速7速", null,
                new BigDecimal("28.00"), 1L, "德系车", "全合成", "CVT+", "1L*12", "桶", "奥迪,大众"));
        products.add(createProduct(id++, "DCT-F-001", "全合成双离合变速箱油 DCT F+", "所有湿式双离合变速器系列用油", null,
                new BigDecimal("28.00"), 1L, "通用", "全合成", "DCT F+", "1L*12", "桶", "大众,福特,现代"));
        products.add(createProduct(id++, "MTF-75W90-001", "全合成手动变速箱油 MTF75W-90", "适用于大众、福特、比亚迪", null,
                new BigDecimal("24.90"), 1L, "大众,福特,比亚迪", "全合成", "MTF75W-90", "1L*12", "桶", "大众,福特,比亚迪"));
        products.add(createProduct(id++, "BODY-OIL-001", "全合成阀体油 BOODY OIL", "适用于干式双离合器电单元用油", null,
                new BigDecimal("24.60"), 1L, "通用", "全合成", "BOODY OIL", "1L*12", "桶", "大众,福特"));
        products.add(createProduct(id++, "DOT4-001", "刹车油 DOT4", "通用刹车油", null,
                new BigDecimal("13.50"), 1L, "通用", "DOT4", "DOT4", "500ML*12", "桶", "通用"));

        products.add(createProduct(id++, "CF4-15W40-3.5L", "合成柴机油 CF-4 15W-40", "CF-4级别柴机油", null,
                new BigDecimal("43.24"), 2L, "通用", "CF-4", "15W-40", "3.5L*6", "桶", "通用"));
        products.add(createProduct(id++, "CF4-15W40-4L", "合成柴机油 CF-4 15W-40", "CF-4级别柴机油", null,
                new BigDecimal("48.44"), 2L, "通用", "CF-4", "15W-40", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "CF4-15W40-18L", "合成柴机油 CF-4 15W-40", "CF-4级别柴机油", null,
                new BigDecimal("210.33"), 2L, "通用", "CF-4", "15W-40", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "CF4-20W50-3.5L", "合成柴机油 CF-4 20W-50", "CF-4级别柴机油", null,
                new BigDecimal("45.24"), 2L, "通用", "CF-4", "20W-50", "3.5L*6", "桶", "通用"));
        products.add(createProduct(id++, "CF4-20W50-4L", "合成柴机油 CF-4 20W-50", "CF-4级别柴机油", null,
                new BigDecimal("50.44"), 2L, "通用", "CF-4", "20W-50", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "CF4-20W50-18L", "合成柴机油 CF-4 20W-50", "CF-4级别柴机油", null,
                new BigDecimal("221.33"), 2L, "通用", "CF-4", "20W-50", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "CH4-15W40-4L", "合成柴机油 CH-4 15W-40", "CH-4级别柴机油", null,
                new BigDecimal("51.44"), 2L, "通用", "CH-4", "15W-40", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "CH4-15W40-18L", "合成柴机油 CH-4 15W-40", "CH-4级别柴机油", null,
                new BigDecimal("226.33"), 2L, "通用", "CH-4", "15W-40", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "CH4-20W50-4L", "合成柴机油 CH-4 20W-50", "CH-4级别柴机油", null,
                new BigDecimal("52.44"), 2L, "通用", "CH-4", "20W-50", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "CH4-20W50-18L", "合成柴机油 CH-4 20W-50", "CH-4级别柴机油", null,
                new BigDecimal("228.33"), 2L, "通用", "CH-4", "20W-50", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "CI4-15W40-4L", "合成柴机油 CI-4 15W-40", "CI-4级别柴机油", null,
                new BigDecimal("52.44"), 2L, "通用", "CI-4", "15W-40", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "CI4-15W40-18L", "合成柴机油 CI-4 15W-40", "CI-4级别柴机油", null,
                new BigDecimal("228.33"), 2L, "通用", "CI-4", "15W-40", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "CI4-20W50-4L", "合成柴机油 CI-4 20W-50", "CI-4级别柴机油", null,
                new BigDecimal("54.44"), 2L, "通用", "CI-4", "20W-50", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "CI4-20W50-18L", "合成柴机油 CI-4 20W-50", "CI-4级别柴机油", null,
                new BigDecimal("235.33"), 2L, "通用", "CI-4", "20W-50", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "CJ4-5W30-4L", "合成柴机油 CJ-4 5W-30", "CJ-4级别柴机油", null,
                new BigDecimal("64.44"), 2L, "通用", "CJ-4", "5W-30", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "CJ4-5W40-4L", "合成柴机油 CJ-4 5W-40", "CJ-4级别柴机油", null,
                new BigDecimal("64.44"), 2L, "通用", "CJ-4", "5W-40", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "CJ4-6W-15W40-4L", "合成柴机油 CJ-4(6万公里) 15W-40", "CJ-4级别柴机油6万公里", null,
                new BigDecimal("66.44"), 2L, "通用", "CJ-4", "15W-40", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "CJ4-6W-15W40-18L", "合成柴机油 CJ-4(6万公里) 15W-40", "CJ-4级别柴机油6万公里", null,
                new BigDecimal("277.33"), 2L, "通用", "CJ-4", "15W-40", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "CK4-10W40-4L", "合成柴机油 CK-4(10万公里) 10W-40", "CK-4级别柴机油10万公里", null,
                new BigDecimal("77.44"), 2L, "通用", "CK-4", "10W-40", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "CK4-10W40-18L", "合成柴机油 CK-4(10万公里) 10W-40", "CK-4级别柴机油10万公里", null,
                new BigDecimal("342.33"), 2L, "通用", "CK-4", "10W-40", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "NG-15W40-4L", "全合成天然气专用油(3万) 15W-40", "天然气发动机专用机油3万公里", null,
                new BigDecimal("51.44"), 2L, "天然气车", "全合成", "15W-40", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "NG-15W40-18L", "全合成天然气专用油(3万) 15W-40", "天然气发动机专用机油3万公里", null,
                new BigDecimal("226.33"), 2L, "天然气车", "全合成", "15W-40", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "NG-20W50-4L", "全合成天然气专用油(3万) 20W-50", "天然气发动机专用机油3万公里", null,
                new BigDecimal("52.44"), 2L, "天然气车", "全合成", "20W-50", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "NG-20W50-18L", "全合成天然气专用油(3万) 20W-50", "天然气发动机专用机油3万公里", null,
                new BigDecimal("228.33"), 2L, "天然气车", "全合成", "20W-50", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "NG6-15W40-4L", "全合成天然气专用油(6万) 15W-40", "天然气发动机专用机油6万公里", null,
                new BigDecimal("70.44"), 2L, "天然气车", "全合成", "15W-40", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "NG6-15W40-18L", "全合成天然气专用油(6万) 15W-40", "天然气发动机专用机油6万公里", null,
                new BigDecimal("302.33"), 2L, "天然气车", "全合成", "15W-40", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "NG6-20W50-4L", "全合成天然气专用油(6万) 20W-50", "天然气发动机专用机油6万公里", null,
                new BigDecimal("73.44"), 2L, "天然气车", "全合成", "20W-50", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "NG6-20W50-18L", "全合成天然气专用油(6万) 20W-50", "天然气发动机专用机油6万公里", null,
                new BigDecimal("307.33"), 2L, "天然气车", "全合成", "20W-50", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "NG10-10W40-4L", "全合成天然气专用油(10万) 10W-40", "天然气发动机专用机油10万公里", null,
                new BigDecimal("88.44"), 2L, "天然气车", "全合成", "10W-40", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "NG10-10W40-18L", "全合成天然气专用油(10万) 10W-40", "天然气发动机专用机油10万公里", null,
                new BigDecimal("390.33"), 2L, "天然气车", "全合成", "10W-40", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "NG10-20W50-4L", "全合成天然气专用油(10万) 20W-50", "天然气发动机专用机油10万公里", null,
                new BigDecimal("89.44"), 2L, "天然气车", "全合成", "20W-50", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "NG10-20W50-18L", "全合成天然气专用油(10万) 20W-50", "天然气发动机专用机油10万公里", null,
                new BigDecimal("398.33"), 2L, "天然气车", "全合成", "20W-50", "18L*1", "桶", "通用"));

        products.add(createProduct(id++, "YY-46-4L", "液压油 高温高压 46#", "高温高压液压油", null,
                new BigDecimal("44.44"), 4L, "通用", "液压油", "46#", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "YY-46-16L", "液压油 高温高压 46#", "高温高压液压油", null,
                new BigDecimal("171.76"), 4L, "通用", "液压油", "46#", "16L*1", "桶", "通用"));
        products.add(createProduct(id++, "YY-46-18L", "液压油 高温高压 46#", "高温高压液压油", null,
                new BigDecimal("192.33"), 4L, "通用", "液压油", "46#", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "YY-68-16L", "液压油 高温高压 68#", "高温高压液压油", null,
                new BigDecimal("179.76"), 4L, "通用", "液压油", "68#", "16L*1", "桶", "通用"));
        products.add(createProduct(id++, "YY-68-18L", "液压油 高温高压 68#", "高温高压液压油", null,
                new BigDecimal("200.33"), 4L, "通用", "液压油", "68#", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "CL-7590-1L", "齿轮油 GL-5 75-90", "GL-5级别齿轮油", null,
                new BigDecimal("15.35"), 4L, "通用", "GL-5", "75-90", "1L*12", "桶", "通用"));
        products.add(createProduct(id++, "CL-7590-2L", "齿轮油 GL-5 75-90", "GL-5级别齿轮油", null,
                new BigDecimal("28.70"), 4L, "通用", "GL-5", "75-90", "2L*8", "桶", "通用"));
        products.add(createProduct(id++, "CL-85W90-3.5L", "齿轮油 GL-5 85W-90", "GL-5级别齿轮油", null,
                new BigDecimal("45.24"), 4L, "通用", "GL-5", "85W-90", "3.5L*6", "桶", "通用"));
        products.add(createProduct(id++, "CL-85W90-4L", "齿轮油 GL-5 85W-90", "GL-5级别齿轮油", null,
                new BigDecimal("48.44"), 4L, "通用", "GL-5", "85W-90", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "CL-85W90-16L", "齿轮油 GL-5 85W-90", "GL-5级别齿轮油", null,
                new BigDecimal("184.76"), 4L, "通用", "GL-5", "85W-90", "16L*1", "桶", "通用"));
        products.add(createProduct(id++, "CL-85W90-18L", "齿轮油 GL-5 85W-90", "GL-5级别齿轮油", null,
                new BigDecimal("205.33"), 4L, "通用", "GL-5", "85W-90", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "CL-85W140-3.5L", "齿轮油 GL-5 85W-140", "GL-5级别齿轮油", null,
                new BigDecimal("46.24"), 4L, "通用", "GL-5", "85W-140", "3.5L*6", "桶", "通用"));
        products.add(createProduct(id++, "CL-85W140-4L", "齿轮油 GL-5 85W-140", "GL-5级别齿轮油", null,
                new BigDecimal("50.44"), 4L, "通用", "GL-5", "85W-140", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "CL-85W140-16L", "齿轮油 GL-5 85W-140", "GL-5级别齿轮油", null,
                new BigDecimal("190.76"), 4L, "通用", "GL-5", "85W-140", "16L*1", "桶", "通用"));
        products.add(createProduct(id++, "CL-85W140-18L", "齿轮油 GL-5 85W-140", "GL-5级别齿轮油", null,
                new BigDecimal("216.33"), 4L, "通用", "GL-5", "85W-140", "18L*1", "桶", "通用"));
        products.add(createProduct(id++, "YYCD-8-1L", "液力传动油 8号", "8号液力传动油", null,
                new BigDecimal("12.35"), 4L, "通用", "液力传动油", "8号", "1L*12", "桶", "通用"));
        products.add(createProduct(id++, "YYCD-8-2L", "液力传动油 8号", "8号液力传动油", null,
                new BigDecimal("22.76"), 4L, "通用", "液力传动油", "8号", "2L*8", "桶", "通用"));
        products.add(createProduct(id++, "YYCD-8-4L", "液力传动油 8号", "8号液力传动油", null,
                new BigDecimal("43.44"), 4L, "通用", "液力传动油", "8号", "4L*6", "桶", "通用"));
        products.add(createProduct(id++, "YYCD-8-16L", "液力传动油 8号", "8号液力传动油", null,
                new BigDecimal("164.76"), 4L, "通用", "液力传动油", "8号", "16L*1", "桶", "通用"));
        products.add(createProduct(id++, "YYCD-8-18L", "液力传动油 8号", "8号液力传动油", null,
                new BigDecimal("185.33"), 4L, "通用", "液力传动油", "8号", "18L*1", "桶", "通用"));

        String[] carBrands = {"大众", "本田", "丰田", "日产", "现代", "别克", "福特", "雪佛兰"};
        String[] carSeries = {"朗逸", "宝来", "速腾", "帕萨特", "迈腾", "雅阁", "思域", "CR-V", "凯美瑞", "卡罗拉", "RAV4", "轩逸", "天籁", "伊兰特", "索纳塔", "英朗", "君威", "福克斯", "蒙迪欧", "科鲁兹"};
        
        for (int i = 0; i < 20; i++) {
            String brand = carBrands[i % carBrands.length];
            String series = carSeries[i];
            products.add(createProduct(id++, "AF-" + String.format("%03d", i + 1), "空气滤清器", "适用于" + brand + series,
                    "OEM-" + (10000 + i), new BigDecimal(20 + i * 2), 31L, brand + series, null, null, "个", "个", brand));
        }
        
        for (int i = 0; i < 20; i++) {
            String brand = carBrands[i % carBrands.length];
            String series = carSeries[i];
            products.add(createProduct(id++, "CF-" + String.format("%03d", i + 1), "空调滤清器", "适用于" + brand + series,
                    "OEM-" + (20000 + i), new BigDecimal(18 + i * 2), 32L, brand + series, null, null, "个", "个", brand));
        }
        
        for (int i = 0; i < 20; i++) {
            String brand = carBrands[i % carBrands.length];
            String series = carSeries[i];
            products.add(createProduct(id++, "OF-" + String.format("%03d", i + 1), "机油滤清器", "适用于" + brand + series,
                    "OEM-" + (30000 + i), new BigDecimal(12 + i), 33L, brand + series, null, null, "个", "个", brand));
        }
        
        for (int i = 0; i < 20; i++) {
            String brand = carBrands[i % carBrands.length];
            String series = carSeries[i];
            products.add(createProduct(id++, "FF-" + String.format("%03d", i + 1), "汽油滤清器", "适用于" + brand + series,
                    "OEM-" + (40000 + i), new BigDecimal(30 + i * 2), 34L, brand + series, null, null, "个", "个", brand));
        }
        
        String[] transBrands = {"大众", "本田", "丰田", "日产", "现代", "宝马", "奔驰", "奥迪", "福特", "别克"};
        for (int i = 0; i < 20; i++) {
            String brand = transBrands[i % transBrands.length];
            products.add(createProduct(id++, "TF-" + String.format("%03d", i + 1), "变速箱滤清器", "适用于" + brand + "变速箱",
                    "OEM-" + (50000 + i), new BigDecimal(65 + i * 3), 35L, brand, null, null, "个", "个", brand));
        }

        productService.saveBatch(products);
        log.info("商品数据初始化完成，共 {} 条", products.size());
    }

    private Product createProduct(int id, String code, String name, String subName, String oem,
                                   BigDecimal price, Long categoryId, String series, String qualityGrade,
                                   String viscosity, String spec, String unit, String brand) {
        Product product = new Product();
        product.setId((long) id);
        product.setCode(code);
        product.setName(name);
        product.setSubName(subName);
        product.setOem(oem);
        product.setPrice(price);
        product.setStock(999);
        product.setCategoryId(categoryId);
        product.setSeries(series);
        product.setQualityGrade(qualityGrade);
        product.setViscosity(viscosity);
        product.setSpec(spec);
        product.setUnit(unit);
        product.setBrand(brand);
        product.setStatus(1);
        return product;
    }
}
