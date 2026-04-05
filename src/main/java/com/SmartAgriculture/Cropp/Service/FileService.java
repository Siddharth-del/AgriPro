package com.SmartAgriculture.Cropp.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public interface FileService {

    String uploadImage(String path, MultipartFile file) throws IOException;

    default boolean deleteImage(String path, String fileName) throws IOException {
        Path filePath = Paths.get(path, fileName);
        return Files.deleteIfExists(filePath);
    }

    default InputStream getResource(String path, String fileName) throws IOException {
        Path filePath = Paths.get(path, fileName);
        if (!Files.exists(filePath)) {
            throw new IOException("File not found: " + fileName);
        }
        return Files.newInputStream(filePath);
    }

    default boolean fileExists(String path, String fileName) {
        Path filePath = Paths.get(path, fileName);
        return Files.exists(filePath);
    }
}