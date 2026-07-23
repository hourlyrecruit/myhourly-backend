package com.my_hourly.employee.FileValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;

import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MultipartFileValidator {

    private static final long MAX_SIZE = 2 * 1024 * 1024; // 5 MB
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList("image/jpeg", "image/png");
    private static final Tika TIKA = new Tika();

    public static void validate(MultipartFile file) {
        // 1. Check if empty
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or missing.");
        }

        // 2. Validate Size
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("File size exceeds the 5MB limit.");
        }

        // 3. Clean and Validate Filename / Extension (Prevents Path Traversal)
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw new IllegalArgumentException("Invalid file path sequence in filename.");
        }

        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("File extension is not allowed.");
        }

        // 4. Validate Deep Magic Bytes (MIME Type) using Apache Tika
        try {
            // Tika inspects the raw input stream bytes directly from memory
            String detectedMimeType = TIKA.detect(file.getInputStream());

            if (!ALLOWED_MIME_TYPES.contains(detectedMimeType.toLowerCase())) {
                throw new IllegalArgumentException("Spoofed file detected. Content does not match extension.");
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read file stream for security validation.", e);
        }
    }

    private static String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return (lastDot == -1) ? "" : filename.substring(lastDot + 1);
    }
}
