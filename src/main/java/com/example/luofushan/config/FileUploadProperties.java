package com.example.luofushan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "file")
public class FileUploadProperties {
  
    private String uploadDir;

    private String urlPrefix;
}
