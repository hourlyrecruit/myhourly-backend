package com.my_hourly.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    public String upload(MultipartFile file, String subFolder);
}
