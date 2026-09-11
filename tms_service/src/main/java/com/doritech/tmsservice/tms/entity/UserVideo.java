package com.doritech.tmsservice.tms.entity;

import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.UserVideoStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_videos")
public class UserVideo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_video_id")
	private Long userVideoId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "video_id", nullable = false)
	private Video video;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "training_assignment_id")
	private TrainingAssignment trainingAssignment;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private UserVideoStatus status = UserVideoStatus.ASSIGNED;

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

	@Column(name = "watched_seconds")
	private Integer watchedSeconds = 0;

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

	public Video getVideo() {
		return video;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public TrainingAssignment getTrainingAssignment() {
		return trainingAssignment;
	}

	public void setTrainingAssignment(TrainingAssignment trainingAssignment) {
		this.trainingAssignment = trainingAssignment;
	}

	public UserVideoStatus getStatus() {
		return status;
	}

	public void setStatus(UserVideoStatus status) {
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

	public Integer getWatchedSeconds() {
		return watchedSeconds;
	}

	public void setWatchedSeconds(Integer watchedSeconds) {
		this.watchedSeconds = watchedSeconds;
	}

}