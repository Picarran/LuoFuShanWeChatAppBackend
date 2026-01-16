package com.example.luofushan.controller;

import com.example.luofushan.dto.Result;
import com.example.luofushan.dto.resp.UploadFileResp;
import com.example.luofushan.service.FileUploadService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/common")
public class CommonController {

    @Resource
    private FileUploadService fileUploadService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<UploadFileResp> upload(@RequestPart("file") MultipartFile file) {
        UploadFileResp resp = fileUploadService.upload(file);
        return Result.buildSuccess(resp);
    }
}
