package com.zhantu.service;

import com.zhantu.model.VehicleInfo;

public interface VinDecoder {
    VehicleInfo decodeVin(String vin);
    String getSourceName();
}
