package com.doritech.tmsservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file.upload")
public class FileStorageProperties {

    private String videoPath;
    private String documentPath;
    private String trainingContentPath;
    private long maxSizeMb;

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getTrainingContentPath() {
        return trainingContentPath;
    }

    public void setTrainingContentPath(String trainingContentPath) {
        this.trainingContentPath = trainingContentPath;
    }

    public long getMaxSizeMb() {
        return maxSizeMb;
    }

    public void setMaxSizeMb(long maxSizeMb) {
        this.maxSizeMb = maxSizeMb;
    }
}