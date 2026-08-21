package com.doritech.tmsservice.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "video_access_control", uniqueConstraints = @UniqueConstraint(columnNames = { "video_id", "user_id" }))
public class VideoAccessControl {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "video_access_id")
	private Long videoAccessId;

	@Column(name = "video_id", nullable = false)
	private Long videoId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "access_duration_days")
	private Long accessDurationDays = 365L;

	@Column(name = "access_start_date")
	private LocalDateTime accessStartDate;

	@Column(name = "access_expiry_date")
	private LocalDateTime accessExpiryDate;

	@Column(name = "is_active")
	private Boolean isActive = true;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public VideoAccessControl() {
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		if (this.accessDurationDays == null) {
			this.accessDurationDays = 365L;
		}
		if (this.accessStartDate == null) {
			this.accessStartDate = LocalDateTime.now();
		}
		if (this.accessExpiryDate == null) {
			this.accessExpiryDate = this.accessStartDate.plusDays(this.accessDurationDays);
		}
		if (this.isActive == null) {
			this.isActive = true;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
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