package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class VideoAccessControlListResponse {

	private Long videoAccessId;
	private Long videoId;
	private Long userId;
	private LocalDateTime accessExpiryDate;
	private Boolean isActive;

	public VideoAccessControlListResponse() {
	}

	public VideoAccessControlListResponse(Long videoAccessId, Long videoId, Long userId, LocalDateTime accessExpiryDate,
			Boolean isActive) {
		this.videoAccessId = videoAccessId;
		this.videoId = videoId;
		this.userId = userId;
		this.accessExpiryDate = accessExpiryDate;
		this.isActive = isActive;
	}

	public Long getVideoAccessId() {
		return videoAccessId;
	}

	public void setVideoAccessId(Long videoAccessId) {
		this.videoAccessId = videoAccessId;
	}

	public Long getVideoId() {
		return videoId;
	}

	public void setVideoId(Long videoId) {
		this.videoId = videoId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public LocalDateTime getAccessExpiryDate() {
		return accessExpiryDate;
	}

	public void setAccessExpiryDate(LocalDateTime accessExpiryDate) {
		this.accessExpiryDate = accessExpiryDate;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}
}