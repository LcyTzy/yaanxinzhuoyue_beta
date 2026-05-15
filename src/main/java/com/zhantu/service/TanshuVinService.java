package com.zhantu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhantu.model.TanshuVinResponse;
import com.zhantu.model.VehicleInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class TanshuVinService implements VinDecoder {

    private static final Logger log = LoggerFactory.getLogger(TanshuVinService.class);

    @Value("${vin.tanshu.api-key}")
    private String tanshuApiKey;

    @Autowired
    private VinCacheService cacheService;

    @Autowired
    private ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public VehicleInfo decodeVin(String vin) {
        VehicleInfo cached = cacheService.get("tanshu:" + vin);
        if (cached != null) {
            return cached;
        }

        VehicleInfo info = queryApi(vin);
        if (info != null) {
            info.setSource("tanshu");
            if (info.getVehicleLength() != null && info.getVehicleWidth() != null && info.getVehicleHigh() != null) {
                info.setSize(info.getVehicleLength() + "×" + info.getVehicleWidth() + "×" + info.getVehicleHigh());
            }
            cacheService.put("tanshu:" + vin, info);
        }
        return info;
    }

    @Override
    public String getSourceName() {
        return "tanshu";
    }

    private VehicleInfo queryApi(String vin) {
        if (tanshuApiKey == null || tanshuApiKey.isEmpty()) {
            log.warn("Tanshu API key is empty, cannot query VIN {}", vin);
            return null;
        }

        String url = "https://api.tanshuapi.com/api/vin/v2/index"
                + "?key=" + tanshuApiKey
                + "&vin=" + vin;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Tanshu API raw response for VIN {}: {}", vin, response.getBody());

                TanshuVinResponse apiResp = objectMapper.readValue(response.getBody(), TanshuVinResponse.class);
                if (apiResp.getCode() == 1 && apiResp.getData() != null) {
                    VehicleInfo info = mapToVehicleInfo(apiResp.getData());
                    log.info("Mapped VehicleInfo: brandName={}, seriesName={}, name={}, year={}, manufacturer={}",
                            info.getBrandName(), info.getSeriesName(), info.getName(), info.getYear(), info.getManufacturer());
                    return info;
                } else {
                    log.warn("Tanshu API returned code={}, msg={}", apiResp.getCode(), apiResp.getMsg());
                }
            }
        } catch (Exception e) {
            log.error("Tanshu API error for VIN {}: {}", vin, e.getMessage(), e);
        }
        return null;
    }

    private VehicleInfo mapToVehicleInfo(TanshuVinResponse.VinData d) {
        VehicleInfo v = new VehicleInfo();
        v.setVin(d.getVin());
        v.setBrandName(d.getBrandName());
        v.setManufacturer(d.getManufacturer());
        v.setSeriesName(d.getSeriesName());
        v.setName(d.getName());
        v.setYear(d.getYear());
        v.setPrice(d.getPrice());
        v.setMarketPrice(d.getMarketPrice());
        v.setGearbox(d.getGearbox());
        v.setGearNum(d.getGearnum());
        v.setGearType(d.getGeartype());
        v.setEngineModel(d.getEngineModel());
        v.setDrivenType(d.getDrivenType());
        v.setDisplacement(d.getDisplacement());
        v.setDisplacementMl(d.getDisplacementMl());
        v.setEffluentStandard(d.getEffluentStandard());
        v.setScale(d.getScale());
        v.setZws(d.getZws());
        v.setMarketDate(d.getMarketDate());
        v.setStopDate(d.getStopDate());
        v.setVehicleLength(d.getLength());
        v.setVehicleWidth(d.getWidth());
        v.setVehicleHigh(d.getHigh());
        v.setWheelbase(d.getWheelbase());
        v.setTrackFront(d.getTrackfront());
        v.setTrackRear(d.getTrackrear());
        v.setFullWeight(d.getFullWeight());
        v.setFullWeightMax(d.getFullWeightMax());
        v.setFullWeightZz(d.getFullWeightZz());
        v.setFrontTyreSize(d.getFrontTyreSize());
        v.setRearTyreSize(d.getRearTyreSize());
        v.setPowerType(d.getRlxs());
        v.setOilNum(d.getRyxh());
        v.setGearboxNumber(d.getGearboxNumber());
        v.setChassisNumber(d.getChassisNumber());
        v.setMaxpower(d.getZdgl());
        v.setMaxHorsepower(d.getZdml());
        v.setFrontBrakeType(d.getFrontBrakeType());
        v.setRearBrakeType(d.getRearBrakeType());
        v.setParkingBrakeType(d.getParkingBrakeType());
        v.setCylinders(d.getQfs());
        v.setFueljetType(d.getGyfs());
        v.setBodyType(d.getBodyType());
        v.setSaleState(d.getVersion());
        v.setImageUrl(d.getImg());
        v.setIsImport(d.getIsImport());
        v.setIsRules(d.getIsRules());
        v.setIsCommercial(d.getIsCommercial());
        v.setMatchingMode(d.getMatchingMode());
        v.setCid(d.getCid());
        v.setBodyStructure(d.getCsjg());
        v.setNumberOfCarriages(d.getCms());
        v.setFuelConsumption(d.getNedczhyh());

        if (d.getModelList() != null && !d.getModelList().isEmpty()) {
            List<VehicleInfo.ModelListItem> items = new ArrayList<>();
            for (TanshuVinResponse.ModelItem mi : d.getModelList()) {
                VehicleInfo.ModelListItem item = new VehicleInfo.ModelListItem();
                item.setCid(mi.getCid());
                item.setBrandName(mi.getBrandName());
                item.setSeriesName(mi.getSeriesName());
                item.setName(mi.getName());
                items.add(item);
            }
            v.setModelList(items);
        }

        return v;
    }
}