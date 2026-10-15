package com.doritech.tmsservice.response;

public class VideoListResponse {

	private Long videoId;
	private String videoTitle;
	private String videoDescription;
	private String status;
	private Integer viewCount;
	private String resolution;
	private String videoFormat;
	private Integer durationSeconds;
	private Long fileSizeBytes;

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

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getVideoFormat() {
		return videoFormat;
	}

	public void setVideoFormat(String videoFormat) {
		this.videoFormat = videoFormat;
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

	public VideoListResponse(Long videoId, String videoTitle, String videoDescription, String status, Integer viewCount,
			String resolution, String videoFormat, Integer durationSeconds, Long fileSizeBytes) {
		super();
		this.videoId = videoId;
		this.videoTitle = videoTitle;
		this.videoDescription = videoDescription;
		this.status = status;
		this.viewCount = viewCount;
		this.resolution = resolution;
		this.videoFormat = videoFormat;
		this.durationSeconds = durationSeconds;
		this.fileSizeBytes = fileSizeBytes;
	}

	public VideoListResponse() {
		super();
	}

}