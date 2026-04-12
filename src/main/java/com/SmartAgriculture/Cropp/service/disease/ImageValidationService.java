package com.SmartAgriculture.Cropp.service.disease;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.SmartAgriculture.Cropp.exception.InvalidImageException;

@Service
public class ImageValidationService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final int MIN_DIMENSION = 50;

    public void validate(MultipartFile file) throws IOException {
        validateFileType(file);
        validateFileSize(file);
        BufferedImage image = readImage(file);
        validateDimensions(image);
        validateIsPlantLeaf(image);
    }

    private void validateFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("image/jpeg") &&
                 !contentType.equals("image/png") &&
                 !contentType.equals("image/webp"))) {
            throw new InvalidImageException(
                    "Invalid file type.",
                    "Please upload a JPG, PNG or WEBP image.");
        }
    }

    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidImageException(
                    "File size exceeds 10MB limit.",
                    "Please upload an image smaller than 10MB.");
        }
    }

    private BufferedImage readImage(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            BufferedImage image = ImageIO.read(is);
            if (image == null) {
                throw new InvalidImageException(
                        "Could not read the uploaded file.",
                        "Please upload a valid image file.");
            }
            return image;
        }
    }

    private void validateDimensions(BufferedImage image) {
        if (image.getWidth() < MIN_DIMENSION || image.getHeight() < MIN_DIMENSION) {
            throw new InvalidImageException(
                    "Image is too small.",
                    "Please upload an image of at least 50x50 pixels.");
        }
    }

    private void validateIsPlantLeaf(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int totalPixels = width * height;

        long greenCount = 0;
        long darkCount = 0;
        long brightOrangeCount = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                boolean isGreen = g > r + 15 && g > b + 10 && g > 40;
                boolean isDark = r < 50 && g < 50 && b < 50;
                boolean isBrightOrange = r > 180 && g > 80 && g < 160 && b < 80;

                if (isGreen) greenCount++;
                if (isDark) darkCount++;
                if (isBrightOrange) brightOrangeCount++;
            }
        }

        double greenRatio = (double) greenCount / totalPixels;
        double darkRatio = (double) darkCount / totalPixels;
        double brightOrangeRatio = (double) brightOrangeCount / totalPixels;

        boolean hasEnoughGreen = greenRatio > 0.10;
        boolean isNotMostlyDark = darkRatio < 0.50;
        boolean isNotSunsetImage = brightOrangeRatio < 0.15;

        if (!hasEnoughGreen || !isNotMostlyDark || !isNotSunsetImage) {
            throw new InvalidImageException(
                    "The uploaded image does not appear to be a plant leaf.",
                    "Please upload a clear close-up photo of a plant leaf.");
        }
    }
}