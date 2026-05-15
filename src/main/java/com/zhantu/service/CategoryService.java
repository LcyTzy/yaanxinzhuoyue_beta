package com.zhantu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhantu.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> getCategoryTree();
}
