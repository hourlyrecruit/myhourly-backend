package com.my_hourly.common.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryFileStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "png", "jpg", "jpeg"
    );

    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024; // 5MB

    @Override
    public String upload(MultipartFile file, String subFolder) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException(
                    "Uploaded file is empty.", ErrorCode.INVALID_REQUEST
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException(
                    "File size must not exceed 5MB.", ErrorCode.INVALID_REQUEST
            );
        }

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : ""
        );

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex + 1).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(
                    "Only PDF, PNG, JPG, JPEG files are allowed.",
                    ErrorCode.INVALID_REQUEST
            );
        }

        try {
            String publicId = subFolder + "/" + UUID.randomUUID();

            // PDFs must go through the "raw" resource type; images use "image"
            String resourceType = "pdf".equals(extension) ? "raw" : "image";

            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", resourceType,
                            "overwrite", false
                    )
            );

            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new BadRequestException(
                    "Failed to upload file: " + e.getMessage(),
                    ErrorCode.INVALID_REQUEST
            );
        }
    }
}