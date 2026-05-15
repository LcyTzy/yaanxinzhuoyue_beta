package com.zhantu.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class PartCategoryItem {
    private String partId;
    private String name;
    private String oeNumber;
    private String category;
    private String specs;
    private String imageUrl;
    private BigDecimal price;
    private Integer stock;
    private String brand;
    private String code;
    private String subName;
    private Map<String, String> specDetails;
    private String compatibleInfo;
    private Map<String, String> maintenanceInfo;
}
