package com.zhantu.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TanshuVinResponse {
    private int code;
    private String msg;

    @JsonProperty("data")
    private VinData data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VinData {
        private String vin;
        @JsonProperty("matching_mode")
        private int matchingMode;
        @JsonProperty("is_commercial")
        private int isCommercial;
        @JsonProperty("is_import")
        private String isImport;
        @JsonProperty("is_rules")
        private String isRules;
        private String cid;
        @JsonProperty("brand_name")
        private String brandName;
        private String manufacturer;
        @JsonProperty("series_name")
        private String seriesName;
        private String name;
        private String year;
        private String price;
        @JsonProperty("market_price")
        private String marketPrice;
        private String gearbox;
        private String gearnum;
        private String geartype;
        @JsonProperty("engine_model")
        private String engineModel;
        @JsonProperty("driven_type")
        private String drivenType;
        @JsonProperty("displacement_ml")
        private String displacementMl;
        private String displacement;
        private String nedczhyh;
        @JsonProperty("effluent_standard")
        private String effluentStandard;
        private String scale;
        private String csjg;
        private String cms;
        private String zws;
        @JsonProperty("market_date")
        private String marketDate;
        @JsonProperty("stop_date")
        private String stopDate;
        private String length;
        private String width;
        private String high;
        private String wheelbase;
        private String trackfront;
        private String trackrear;
        @JsonProperty("full_weight")
        private String fullWeight;
        @JsonProperty("full_weight_max")
        private String fullWeightMax;
        @JsonProperty("full_weight_zz")
        private String fullWeightZz;
        @JsonProperty("front_tyre_size")
        private String frontTyreSize;
        @JsonProperty("rear_tyre_size")
        private String rearTyreSize;
        private String rlxs;
        private String ryxh;
        @JsonProperty("gearbox_number")
        private String gearboxNumber;
        @JsonProperty("chassis_number")
        private String chassisNumber;
        private String zdgl;
        private String zdml;
        @JsonProperty("front_brake_type")
        private String frontBrakeType;
        @JsonProperty("rear_brake_type")
        private String rearBrakeType;
        @JsonProperty("parking_brake_type")
        private String parkingBrakeType;
        private String qfs;
        private String gyfs;
        @JsonProperty("body_type")
        private String bodyType;
        private String version;
        private String img;
        @JsonProperty("model_list")
        private List<ModelItem> modelList;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ModelItem {
        private String cid;
        @JsonProperty("brand_name")
        private String brandName;
        @JsonProperty("series_name")
        private String seriesName;
        private String name;
    }
}