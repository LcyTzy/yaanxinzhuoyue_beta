package com.zhantu.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleInfo {
    private String vin;
    private String brandName;
    private String manufacturer;
    private String seriesName;
    private String name;
    private String zws;
    private String year;
    private String saleState;
    private String price;
    private String marketPrice;
    private String powerType;
    private String oilNum;
    private String fueljetType;
    private String effluentStandard;
    private String model;
    private String marketDate;
    private String stopDate;
    private String engineModel;
    private String color;
    private String displacement;
    private String displacementMl;
    private String gearbox;
    private String gearNum;
    private String gearType;
    private String drivenType;
    private String numberOfCarriages;
    private String maxpower;
    private String maxHorsepower;
    private String wheelbase;
    private String axesNum;
    private String vehicleLength;
    private String vehicleWidth;
    private String vehicleHigh;
    private String size;
    private String trackFront;
    private String trackRear;
    private String fullWeight;
    private String fullWeightMax;
    private String fullWeightZz;
    private String scale;
    private String groupName;
    private String groupCode;
    private String remark;
    private String source;
    private String epc;
    private String buildDate;
    private String imageUrl;
    private String bodyType;
    private String bodyStructure;
    private String fuelConsumption;
    private String frontTyreSize;
    private String rearTyreSize;
    private String frontBrakeType;
    private String rearBrakeType;
    private String parkingBrakeType;
    private String cylinders;
    private String gearboxNumber;
    private String chassisNumber;
    private String isImport;
    private String isRules;
    private Integer isCommercial;
    private Integer matchingMode;
    private String cid;
    private List<ModelListItem> modelList;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ModelListItem {
        private String cid;
        private String brandName;
        private String seriesName;
        private String name;
    }
}