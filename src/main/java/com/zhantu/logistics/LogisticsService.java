package com.zhantu.logistics;

import java.util.List;
import java.util.Map;

public interface LogisticsService {

    Map<String, Object> queryTracking(String logisticsCompany, String logisticsNo);

    List<String> getSupportedCompanies();
}