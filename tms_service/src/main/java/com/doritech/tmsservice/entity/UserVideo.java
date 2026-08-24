package com.doritech.tmsservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_videos")
public class UserVideo {

	public enum Status {
		ASSIGNED, WATCHING, COMPLETED, EXPIRED
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_video_id")
	private Long userVideoId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "video_id", nullable = false)
	private Long videoId;

	@Column(name = "training_assignment_id")
	private Long trainingAssignmentId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status status = Status.ASSIGNED;

	@Column(name = "watched_count")
	private Integer watchedCount = 0;

	@Column(name = "last_watched_at")
	private LocalDateTime lastWatchedAt;

	@Column(name = "completed_at")
	private LocalDateTime completedAt;

	@Column(name = "expiry_date")
	private LocalDateTime expiryDate;

	@Column(name = "assigned_at")
	private LocalDateTime assignedAt;

	@Column(name = "assigned_by")
	private Long assignedBy;

	public UserVideo() {
	}

	@PrePersist
	protected void onCreate() {
		this.assignedAt = LocalDateTime.now();
		if (this.status == null) {
			this.status = Status.ASSIGNED;
		}
		if (this.watchedCount == null) {
			this.watchedCount = 0;
		}
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

	public Long getTrainingAssignmentId() {
		return trainingAssignmentId;
	}

	public void setTrainingAssignmentId(Long trainingAssignmentId) {
		this.trainingAssignmentId = trainingAssignmentId;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getWatchedCount() {
		return watchedCount;
	}

	public void setWatchedCount(Integer watchedCount) {
		this.watchedCount = watchedCount;
	}

	public LocalDateTime getLastWatchedAt() {
		return lastWatchedAt;
	}

	public void setLastWatchedAt(LocalDateTime lastWatchedAt) {
		this.lastWatchedAt = lastWatchedAt;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
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
}