package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class VideoResponse {

    private Long videoId;
    private String videoTitle;
    private String videoDescription;
    private String videoUrl;
    private String thumbnailUrl;
    private Integer durationSeconds;
    private Long fileSizeBytes;
    private String videoFormat;
    private String resolution;
    private Boolean isSecure;
    private Boolean allowDownload;
    private Boolean allowScreenRecord;
    private Boolean allowScreenshot;
    private String status;
    private Integer viewCount;
    private Long uploadedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VideoResponse() {
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDescription() {
        return videoDescription;
    }

    public void setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getVideoFormat() {
        return videoFormat;
    }

    public void setVideoFormat(String videoFormat) {
        this.videoFormat = videoFormat;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public Boolean getIsSecure() {
        return isSecure;
    }

    public void setIsSecure(Boolean isSecure) {
        this.isSecure = isSecure;
    }

    public Boolean getAllowDownload() {
        return allowDownload;
    }

    public void setAllowDownload(Boolean allowDownload) {
        this.allowDownload = allowDownload;
    }

    public Boolean getAllowScreenRecord() {
        return allowScreenRecord;
    }

    public void setAllowScreenRecord(Boolean allowScreenRecord) {
        this.allowScreenRecord = allowScreenRecord;
    }

    public Boolean getAllowScreenshot() {
        return allowScreenshot;
    }

    public void setAllowScreenshot(Boolean allowScreenshot) {
        this.allowScreenshot = allowScreenshot;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Long getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "VideoResponse [videoId=" + videoId + ", videoTitle=" + videoTitle
                + ", status=" + status + ", viewCount=" + viewCount + "]";
    }
}