//package com.my_hourly.common.service.impl;
//
//import com.my_hourly.common.enums.ErrorCode;
//import com.my_hourly.common.exception.BadRequestException;
//import com.my_hourly.common.service.FileStorageServiceCloudFlare;
//import lombok.RequiredArgsConstructor;
//import org.apache.commons.io.FilenameUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//import software.amazon.awssdk.core.sync.RequestBody;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//
//import java.io.IOException;
//import java.util.Set;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//public class R2FileStorageServiceImpl implements FileStorageServiceCloudFlare {
//
//    private final S3Client s3Client;
//
//    @Value("${r2.bucket}")
//    private String bucket;
//
//    @Value("${r2.public-url}")
//    private String publicUrl;
//
//    private static final Set<String> ALLOWED_EXTENSIONS =
//            Set.of("pdf","png","jpg","jpeg");
//
//    private static final long MAX_FILE_SIZE =
//            25 * 1024 * 1024;
//
//    @Override
//    public String upload(MultipartFile file, String subFolder) {
//
//        // Keep all your existing validations here.
//
//        String extension = FilenameUtils.getExtension(
//                file.getOriginalFilename());
//
//        String fileName =
//                subFolder + "/" + UUID.randomUUID() + "." + extension;
//
//        try {
//
//            PutObjectRequest request =
//                    PutObjectRequest.builder()
//                            .bucket(bucket)
//                            .key(fileName)
//                            .contentType(file.getContentType())
//                            .build();
//
//            s3Client.putObject(
//                    request,
//                    RequestBody.fromBytes(file.getBytes()));
//
//            return publicUrl + "/" + fileName;
//
//        } catch (IOException e) {
//
//            throw new BadRequestException(
//                    e.getMessage(),
//                    ErrorCode.INVALID_REQUEST);
//        }
//    }
//}