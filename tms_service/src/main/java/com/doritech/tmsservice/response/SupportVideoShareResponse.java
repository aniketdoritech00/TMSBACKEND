package com.doritech.tmsservice.response;

import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.SupportVideoShareStatus;

public class SupportVideoShareResponse {

	private Long supportVideoShareId;

	private Long supportRequestId;

	private Long videoId;

	private Long userId;

	private Long sharedBy;

	private LocalDateTime sharedAt;

	private SupportVideoShareStatus status;

	private String shareUrl;

	private LocalDateTime expiresAt;

	private LocalDateTime viewedAt;

	private LocalDateTime completedAt;

	public Long getSupportVideoShareId() {
		return supportVideoShareId;
	}

	public void setSupportVideoShareId(Long supportVideoShareId) {
		this.supportVideoShareId = supportVideoShareId;
	}

	public Long getSupportRequestId() {
		return supportRequestId;
	}

	public void setSupportRequestId(Long supportRequestId) {
		this.supportRequestId = supportRequestId;
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

	public Long getSharedBy() {
		return sharedBy;
	}

	public void setSharedBy(Long sharedBy) {
		this.sharedBy = sharedBy;
	}

	public LocalDateTime getSharedAt() {
		return sharedAt;
	}

	public void setSharedAt(LocalDateTime sharedAt) {
		this.sharedAt = sharedAt;
	}

	public SupportVideoShareStatus getStatus() {
		return status;
	}

	public void setStatus(SupportVideoShareStatus status) {
		this.status = status;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public LocalDateTime getViewedAt() {
		return viewedAt;
	}

	public void setViewedAt(LocalDateTime viewedAt) {
		this.viewedAt = viewedAt;
	}

	public LocalDateTime getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(LocalDateTime completedAt) {
		this.completedAt = completedAt;
	}

}