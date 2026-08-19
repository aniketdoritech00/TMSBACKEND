package com.doritech.tmsservice.request;

import jakarta.validation.constraints.NotNull;

public class VideoSubProductRequest {

    @NotNull(message = "{videoSubProduct.videoId.notnull}")
    private Long videoId;

    @NotNull(message = "{videoSubProduct.subProductId.notnull}")
    private Long subProductId;

    private Long assignedBy;

    public VideoSubProductRequest() {
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

    public Long getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(Long assignedBy) {
        this.assignedBy = assignedBy;
    }

    @Override
    public String toString() {
        return "VideoSubProductRequest [videoId=" + videoId
                + ", subProductId=" + subProductId
                + ", assignedBy=" + assignedBy + "]";
    }
}