package com.example.luofushan.service.impl;

import com.example.luofushan.common.exception.LuoFuShanException;
import com.example.luofushan.config.FileUploadProperties;
import com.example.luofushan.dto.resp.UploadFileResp;
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
    public UploadFileResp upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw LuoFuShanException.fileUploadFailed("文件不能为空");
        }

        String uploadDir = fileUploadProperties.getUploadDir();
        String urlPrefix = fileUploadProperties.getUrlPrefix();

        String fileType = resolveFileType(file);

        try {
            //按 fileType + 日期分目录
            String relativePath = FileStorageUtil.store(file, uploadDir, fileType);

            String url = urlPrefix.endsWith("/") ? urlPrefix + relativePath : urlPrefix + "/" + relativePath;

            UploadFileResp resp = new UploadFileResp();
            resp.setUrl(url);
            resp.setFileType(fileType);
            resp.setSize(file.getSize());
            return resp;
        } catch (IOException e) {
            e.printStackTrace();
            throw LuoFuShanException.fileUploadFailed("文件上传失败，请稍后重试");
        }
    }

    private String resolveFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                return "image";
            }
            if (contentType.startsWith("video/")) {
                return "video";
            }
        }
        return "other";
    }
}
