package com.doritech.tmsservice.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class VideoAccessControlRequest {

	@NotNull(message = "{videoAccessControl.videoId.notnull}")
	private Long videoId;

	@NotNull(message = "{videoAccessControl.userId.notnull}")
	private Long userId;

	@Min(value = 1, message = "{videoAccessControl.accessDurationDays.min}")
	private Long accessDurationDays;

	private Boolean isActive;

	public VideoAccessControlRequest() {
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "VideoAccessControlRequest [videoId=" + videoId + ", userId=" + userId + "]";
	}
}