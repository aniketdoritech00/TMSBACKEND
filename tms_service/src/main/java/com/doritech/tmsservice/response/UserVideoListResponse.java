package com.doritech.tmsservice.response;

public class UserVideoListResponse {

    private Long userVideoId;
    private Long userId;
    private Long videoId;
    private String status;

    public UserVideoListResponse() {
    }

    public UserVideoListResponse(Long userVideoId, Long userId, Long videoId, String status) {
        this.userVideoId = userVideoId;
        this.userId = userId;
        this.videoId = videoId;
        this.status = status;
    }

    public Long getUserVideoId() {
        return userVideoId;
    }

    public void setUserVideoId(Long userVideoId) {
        this.userVideoId = userVideoId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}