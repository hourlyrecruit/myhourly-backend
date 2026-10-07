package com.my_hourly.common.service.impl;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
import com.my_hourly.common.service.FileStorageServiceB2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class B2FileStorageServiceImpl implements FileStorageServiceB2 {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${b2.bucket}")
    private String bucket;

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("pdf", "png", "jpg", "jpeg");

    private static final long MAX_FILE_SIZE =
            25 * 1024 * 1024;

    @Override
    public String upload(MultipartFile file, String subFolder) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException(
                    "File must not be empty.",
                    ErrorCode.BAD_REQUEST);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException(
                    "File size exceeds the maximum allowed limit of 25MB.",
                    ErrorCode.BAD_REQUEST);
        }

        String extension = FilenameUtils.getExtension(
                file.getOriginalFilename());

        if (extension == null
                || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException(
                    "Unsupported file type: " + extension
                            + ". Allowed types: " + ALLOWED_EXTENSIONS,
                    ErrorCode.BAD_REQUEST);
        }

        String fileName =
                subFolder + "/" +
                        UUID.randomUUID() +
                        "." +
                        extension;

        try {

            PutObjectRequest request =
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .contentType(file.getContentType())
                            .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(file.getBytes())
            );

            GetObjectRequest getObjectRequest =
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(fileName)
                            .build();

            GetObjectPresignRequest presignRequest =
                    GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofMinutes(15))
                            .getObjectRequest(getObjectRequest)
                            .build();

            PresignedGetObjectRequest presigned =
                    s3Presigner.presignGetObject(presignRequest);

            return presigned.url().toString();

        } catch (Exception e) {

            log.error("B2 upload failed", e);

            throw new BadRequestException(
                    "Unable to upload file to Backblaze B2: "
                            + e.getMessage(),
                    ErrorCode.INVALID_REQUEST
            );
        }
    }
}