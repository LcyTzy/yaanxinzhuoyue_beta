package com.zhantu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhantu.entity.Category;
import com.zhantu.mapper.CategoryMapper;
import com.zhantu.service.CacheService;
import com.zhantu.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    private static final String CATEGORY_TREE_CACHE_KEY = "category:tree";

    @Override
    public boolean save(Category entity) {
        if (entity.getParentId() == null || entity.getParentId() == 0) {
            entity.setParentId(0L);
            entity.setLevel(1);
        } else {
            Category parent = this.getById(entity.getParentId());
            if (parent != null) {
                entity.setLevel(parent.getLevel() + 1);
            } else {
                entity.setLevel(2);
            }
        }
        boolean result = super.save(entity);
        evictCategoryTreeCache();
        return result;
    }

    @Override
    public boolean updateById(Category entity) {
        Category old = this.getById(entity.getId());
        if (old != null && entity.getParentId() != null && !entity.getParentId().equals(old.getParentId())) {
            if (entity.getParentId() == 0) {
                entity.setLevel(1);
            } else {
                Category parent = this.getById(entity.getParentId());
                if (parent != null) {
                    entity.setLevel(parent.getLevel() + 1);
                }
            }
        }
        boolean result = super.updateById(entity);
        evictCategoryTreeCache();
        return result;
    }

    @Override
    public boolean removeById(java.io.Serializable id) {
        boolean result = super.removeById(id);
        evictCategoryTreeCache();
        return result;
    }

    @Override
    public List<Category> getCategoryTree() {
        String cached = cacheService.getCategoryTreeCache();
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, new TypeReference<List<Category>>() {});
            } catch (JsonProcessingException e) {
                log.warn("分类树缓存解析失败", e);
            }
        }

        List<Category> allCategories = this.list();
        List<Category> rootCategories = allCategories.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() == 0)
                .collect(Collectors.toList());

        for (Category root : rootCategories) {
            List<Category> children = allCategories.stream()
                    .filter(c -> c.getParentId() != null && c.getParentId().equals(root.getId()))
                    .collect(Collectors.toList());
            root.setChildren(children);
        }

        try {
            cacheService.setCategoryTreeCache(objectMapper.writeValueAsString(rootCategories));
        } catch (JsonProcessingException e) {
            log.warn("分类树缓存写入失败", e);
        }

        return rootCategories;
    }

    private void evictCategoryTreeCache() {
        cacheService.evictCategoryTreeCache();
    }
}
