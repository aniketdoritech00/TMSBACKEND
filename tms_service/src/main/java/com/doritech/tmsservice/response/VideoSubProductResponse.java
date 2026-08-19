package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class VideoSubProductResponse {

    private Long videoId;
    private Long subProductId;
    private LocalDateTime assignedAt;
    private Long assignedBy;

    public VideoSubProductResponse() {
    }

    public VideoSubProductResponse(Long videoId, Long subProductId, LocalDateTime assignedAt, Long assignedBy) {
        this.videoId = videoId;
        this.subProductId = subProductId;
        this.assignedAt = assignedAt;
        this.assignedBy = assignedBy;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getSubProductId() {
        return subProductId;
    }

    public void setSubProductId(Long subProductId) {
        this.subProductId = subProductId;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }

    @Override
    public String toString() {
        return "VideoSubProductResponse [videoId=" + videoId
                + ", subProductId=" + subProductId
                + ", assignedAt=" + assignedAt
                + ", assignedBy=" + assignedBy + "]";
    }
}