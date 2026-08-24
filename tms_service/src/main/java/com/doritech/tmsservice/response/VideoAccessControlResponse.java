package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

public class VideoAccessControlResponse {

	private Long videoAccessId;
	private Long videoId;
	private Long userId;
	private Long accessDurationDays;
	private LocalDateTime accessStartDate;
	private LocalDateTime accessExpiryDate;
	private Boolean isActive;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public VideoAccessControlResponse() {
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

	public Long getAccessDurationDays() {
		return accessDurationDays;
	}

	public void setAccessDurationDays(Long accessDurationDays) {
		this.accessDurationDays = accessDurationDays;
	}

	public LocalDateTime getAccessStartDate() {
		return accessStartDate;
	}

	public void setAccessStartDate(LocalDateTime accessStartDate) {
		this.accessStartDate = accessStartDate;
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
}