package com.my_hourly.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageServiceB2 {
    String upload(MultipartFile file, String subFolder);
}
