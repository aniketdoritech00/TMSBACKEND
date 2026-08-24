package com.doritech.tmsservice.serviceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.InternalServerException;
import com.doritech.tmsservice.service.FileStorageService;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageServiceImpl.class);

    @Override
    public String storeFile(MultipartFile file, String targetFolderPath) {

        if (file == null || file.isEmpty()) {
            log.error("storeFile :: file is null or empty");
            throw new BadRequestException("File must not be empty");
        }

        String originalFileName = file.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String uniqueFileName = UUID.randomUUID().toString() + extension;

        try {
            Path folder = Paths.get(targetFolderPath);
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }

            Path targetPath = folder.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("storeFile :: file stored successfully at={}", targetPath.toString());

            return targetPath.toString();

        } catch (IOException e) {
            log.error("storeFile :: error while storing file - {}", e.getMessage(), e);
            throw new InternalServerException("Something went wrong while storing the file");
        }
    }
}