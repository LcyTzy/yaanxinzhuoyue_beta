package com.zhantu.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.zhantu.config.OssConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OssService {

    private final OssConfig ossConfig;

    public String upload(MultipartFile file, String dir) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("文件名不能为空");
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
            throw new RuntimeException("只支持jpg、jpeg、png、gif、webp格式的图片");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new RuntimeException("文件大小不能超过10MB");
        }

        String fileName = dir + "/" + UUID.randomUUID().toString().replace("-", "") + extension;

        try {
            OSS ossClient = new OSSClientBuilder().build(
                    ossConfig.getEndpoint(),
                    ossConfig.getAccessKeyId(),
                    ossConfig.getAccessKeySecret()
            );

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            ossClient.putObject(ossConfig.getBucketName(), fileName, file.getInputStream(), metadata);
            ossClient.shutdown();

            String url = ossConfig.getUrlPrefix() + "/" + fileName;
            log.info("文件上传成功: {}", url);
            return url;
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        try {
            String key = fileUrl.replace(ossConfig.getUrlPrefix() + "/", "");
            OSS ossClient = new OSSClientBuilder().build(
                    ossConfig.getEndpoint(),
                    ossConfig.getAccessKeyId(),
                    ossConfig.getAccessKeySecret()
            );
            ossClient.deleteObject(ossConfig.getBucketName(), key);
            ossClient.shutdown();
            log.info("文件删除成功: {}", key);
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
        }
    }
}
