package com.zhantu.service;

import com.zhantu.model.VehicleInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class VinDecoderManager {

    @Autowired
    private TanshuVinService tanshuVinService;

    public VehicleInfo decode(String vin) {
        return tanshuVinService.decodeVin(vin);
    }

    public VehicleInfo decode(String vin, String source) {
        return tanshuVinService.decodeVin(vin);
    }

    public List<String> getAvailableSources() {
        return Collections.singletonList("tanshu");
    }

    public String getActiveSource(String vin) {
        VehicleInfo result = decode(vin);
        return result != null ? result.getSource() : "unknown";
    }
}