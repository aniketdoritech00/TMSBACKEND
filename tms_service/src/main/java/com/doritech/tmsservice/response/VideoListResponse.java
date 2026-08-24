package com.doritech.tmsservice.response;

public class VideoListResponse {

    private Long videoId;
    private String videoTitle;
    private String status;
    private Integer viewCount;

    public VideoListResponse() {
    }

    public VideoListResponse(Long videoId, String videoTitle, String status, Integer viewCount) {
        this.videoId = videoId;
        this.videoTitle = videoTitle;
        this.status = status;
        this.viewCount = viewCount;
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
}