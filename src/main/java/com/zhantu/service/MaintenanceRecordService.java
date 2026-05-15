package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.MaintenanceRecord;

import java.util.List;

public interface MaintenanceRecordService extends IService<MaintenanceRecord> {
    List<MaintenanceRecord> getUserRecords(Long userId);
    void addRecord(Long userId, MaintenanceRecord record);
    List<MaintenanceRecord> getDueReminders();
}