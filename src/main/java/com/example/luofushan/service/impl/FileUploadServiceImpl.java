package com.example.luofushan.service.impl;

import com.example.luofushan.common.exception.LuoFuShanException;
import com.example.luofushan.config.FileUploadProperties;
import com.example.luofushan.service.FileUploadService;
import com.example.luofushan.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileUploadProperties fileUploadProperties;

    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw LuoFuShanException.fileUploadFailed("文件不能为空");
        }

        String uploadDir = fileUploadProperties.getUploadDir();
        String urlPrefix = fileUploadProperties.getUrlPrefix();

        try {
            String relativePath = FileStorageUtil.store(file, uploadDir);
            if (urlPrefix.endsWith("/")) {
                return urlPrefix + relativePath;
            } else {
                return urlPrefix + "/" + relativePath;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw LuoFuShanException.fileUploadFailed("文件上传失败，请稍后重试");
        }
    }
}
