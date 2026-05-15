package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.Banner;

import java.util.List;

public interface BannerService extends IService<Banner> {
    List<Banner> getActiveBanners();
}
