package com.example.luofushan.service;

import com.example.luofushan.dto.resp.UploadFileResp;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    UploadFileResp upload(MultipartFile file);
}
