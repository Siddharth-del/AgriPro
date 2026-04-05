package com.SmartAgriculture.Cropp.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {
        validateFile(file);

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new IllegalArgumentException("Original filename is missing");
        }

        String extension = getFileExtension(originalFileName);
        validateExtension(extension);
        
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId + extension;
        
        File folder = new File(path);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (!created) {
                throw new IOException("Failed to create directory: " + path);
            }
            log.info("Created directory: {}", path);
        }

        Path filePath = Paths.get(path, fileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded successfully: {}", fileName);
        } catch (IOException e) {
            log.error("Failed to upload file: {}", fileName, e);
            throw new IOException("Failed to save file: " + e.getMessage(), e);
        }

        return fileName;
    }

    @Override
    public boolean deleteImage(String path, String fileName) throws IOException {
        Path filePath = Paths.get(path, fileName);
        boolean deleted = Files.deleteIfExists(filePath);
        if (deleted) {
            log.info("File deleted successfully: {}", fileName);
        } else {
            log.warn("File not found for deletion: {}", fileName);
        }
        return deleted;
    }

    @Override
    public InputStream getResource(String path, String fileName) throws IOException {
        Path filePath = Paths.get(path, fileName);
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + fileName);
        }
        log.info("Retrieving file: {}", fileName);
        return Files.newInputStream(filePath);
    }

    @Override
    public boolean fileExists(String path, String fileName) {
        Path filePath = Paths.get(path, fileName);
        return Files.exists(filePath);
    }

   
    private String getFileExtension(String filename) {
        int lastIndexOf = filename.lastIndexOf('.');
        if (lastIndexOf == -1 || lastIndexOf == filename.length() - 1) {
            throw new IllegalArgumentException("File has no extension");
        }
        return filename.substring(lastIndexOf).toLowerCase();
    }

    private void validateExtension(String extension) {
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException(
                "Invalid file extension: " + extension + 
                ". Allowed extensions: " + ALLOWED_EXTENSIONS
            );
        }
    }

    
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                "File size exceeds maximum limit of " + (MAX_FILE_SIZE / (1024 * 1024)) + "MB"
            );
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }
    }
}