package com.example.luofushan.util;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.UUID;

public class FileStorageUtil {

    public static String store(MultipartFile file, String uploadDir, String fileType) throws IOException {
        LocalDate today = LocalDate.now();
        Path dir = Paths.get(
                uploadDir,
                fileType, // image / video / other
                String.valueOf(today.getYear()),
                String.format("%02d", today.getMonthValue()),
                String.format("%02d", today.getDayOfMonth())
        );
        Files.createDirectories(dir);

        String original = file.getOriginalFilename();
        String ext = StringUtils.getFilenameExtension(original);
        String filename = UUID.randomUUID().toString().replace("-", "");
        if (StringUtils.hasText(ext)) {
            filename = filename + "." + ext;
        }

        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());

        // 返回相对路径：fileType/yyyy/MM/dd/xxx.ext
        return fileType + "/"
                + today.getYear() + "/"
                + String.format("%02d", today.getMonthValue()) + "/"
                + String.format("%02d", today.getDayOfMonth()) + "/"
                + filename;
    }
}
