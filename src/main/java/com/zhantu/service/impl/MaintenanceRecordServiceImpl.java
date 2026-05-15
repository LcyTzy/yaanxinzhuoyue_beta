package com.zhantu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.MaintenanceRecord;
import com.zhantu.mapper.MaintenanceRecordMapper;
import com.zhantu.service.MaintenanceRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MaintenanceRecordServiceImpl extends ServiceImpl<MaintenanceRecordMapper, MaintenanceRecord> implements MaintenanceRecordService {

    @Override
    public List<MaintenanceRecord> getUserRecords(Long userId) {
        return list(new LambdaQueryWrapper<MaintenanceRecord>()
                .eq(MaintenanceRecord::getUserId, userId)
                .orderByDesc(MaintenanceRecord::getServiceDate));
    }

    @Override
    public void addRecord(Long userId, MaintenanceRecord record) {
        record.setUserId(userId);
        save(record);
    }

    @Override
    public List<MaintenanceRecord> getDueReminders() {
        return list(new LambdaQueryWrapper<MaintenanceRecord>()
                .le(MaintenanceRecord::getNextRemindDate, LocalDate.now().plusDays(7))
                .eq(MaintenanceRecord::getReminded, 0));
    }
}