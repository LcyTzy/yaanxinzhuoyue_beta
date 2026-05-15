package com.zhantu.controller;

import com.zhantu.annotation.OperationLog;
import com.zhantu.annotation.RateLimit;
import com.zhantu.common.Result;
import com.zhantu.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Tag(name = "文件上传")
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final OssService ossService;

    @Value("${oss.aliyun.enabled:false}")
    private boolean ossEnabled;

    private static final String UPLOAD_DIR = "uploads/products/";

    @PostMapping("/image")
    @Operation(summary = "上传商品图片")
    @RateLimit(time = 60, count = 20)
    @OperationLog(module = "文件上传", operation = "上传图片")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error("文件大小不能超过10MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return Result.error("文件名不能为空");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
        boolean allowed = false;
        for (String ext : allowedExtensions) {
            if (ext.equalsIgnoreCase(extension)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            return Result.error("只支持jpg、jpeg、png、gif、webp格式的图片");
        }

        if (ossEnabled) {
            String url = ossService.upload(file, "products");
            return Result.success(url);
        }

        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        String uploadPath = System.getProperty("user.dir") + File.separator + UPLOAD_DIR;

        try {
            Path dirPath = Paths.get(uploadPath);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            Path filePath = dirPath.resolve(fileName);
            file.transferTo(filePath.toFile());

            String url = "/uploads/products/" + fileName;
            return Result.success(url);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
}
