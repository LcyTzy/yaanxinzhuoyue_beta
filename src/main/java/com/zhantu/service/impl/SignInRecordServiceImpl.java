package com.zhantu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhantu.entity.SignInRecord;
import com.zhantu.mapper.SignInRecordMapper;
import com.zhantu.service.SignInRecordService;
import org.springframework.stereotype.Service;

@Service
public class SignInRecordServiceImpl extends ServiceImpl<SignInRecordMapper, SignInRecord> implements SignInRecordService {
}