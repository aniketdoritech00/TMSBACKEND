package com.doritech.tmsservice.request;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public class UserVideoRequest {

	@NotNull(message = "{userVideo.userId.notnull}")
	private Long userId;

	@NotNull(message = "{userVideo.videoId.notnull}")
	private Long videoId;

	private Long trainingAssignmentId;

	private LocalDateTime expiryDate;

	private Integer watchedSeconds;

	public UserVideoRequest() {

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

	public Long getTrainingAssignmentId() {
		return trainingAssignmentId;
	}

	public void setTrainingAssignmentId(Long trainingAssignmentId) {
		this.trainingAssignmentId = trainingAssignmentId;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Integer getWatchedSeconds() {
		return watchedSeconds;
	}

	public void setWatchedSeconds(Integer watchedSeconds) {
		this.watchedSeconds = watchedSeconds;
	}

}