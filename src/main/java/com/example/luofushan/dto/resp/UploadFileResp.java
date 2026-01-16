package com.example.luofushan.dto.resp;

import lombok.Data;

@Data
public class UploadFileResp {
    private String url;
    private String fileType; // image / video / other
    private Long size;       // 大小
}
