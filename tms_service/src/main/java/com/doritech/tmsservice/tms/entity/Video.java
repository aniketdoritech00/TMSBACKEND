package com.doritech.tmsservice.tms.entity;

import java.time.LocalDateTime;

import com.doritech.tmsservice.enums.VideoStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "videos")
public class Video {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "video_id")
	private Long videoId;

	@Column(name = "video_title", nullable = false, length = 255)
	private String videoTitle;

	@Column(name = "video_description", columnDefinition = "TEXT")
	private String videoDescription;

	@Column(name = "video_url", nullable = false, length = 500)
	private String videoUrl;

	@Column(name = "thumbnail_url", length = 500)
	private String thumbnailUrl;

	@Column(name = "duration_seconds")
	private Integer durationSeconds;

	@Column(name = "file_size_bytes")
	private Long fileSizeBytes;

	@Column(name = "video_format", length = 20)
	private String videoFormat;

	@Column(name = "resolution", length = 20)
	private String resolution;

	@Column(name = "is_secure")
	private Boolean isSecure = true;

	@Column(name = "allow_download")
	private Boolean allowDownload = false;

	@Column(name = "allow_screen_record")
	private Boolean allowScreenRecord = false;

	@Column(name = "allow_screenshot")
	private Boolean allowScreenshot = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private VideoStatus status = VideoStatus.ACTIVE;

	@Column(name = "view_count")
	private Integer viewCount = 0;

	@Column(name = "uploaded_by")
	private Long uploadedBy;

	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

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

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
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

	public String getVideoFormat() {
		return videoFormat;
	}

	public void setVideoFormat(String videoFormat) {
		this.videoFormat = videoFormat;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public Boolean getIsSecure() {
		return isSecure;
	}

	public void setIsSecure(Boolean isSecure) {
		this.isSecure = isSecure;
	}

	public Boolean getAllowDownload() {
		return allowDownload;
	}

	public void setAllowDownload(Boolean allowDownload) {
		this.allowDownload = allowDownload;
	}

	public Boolean getAllowScreenRecord() {
		return allowScreenRecord;
	}

	public void setAllowScreenRecord(Boolean allowScreenRecord) {
		this.allowScreenRecord = allowScreenRecord;
	}

	public Boolean getAllowScreenshot() {
		return allowScreenshot;
	}

	public void setAllowScreenshot(Boolean allowScreenshot) {
		this.allowScreenshot = allowScreenshot;
	}

	public VideoStatus getStatus() {
		return status;
	}

	public void setStatus(VideoStatus status) {
		this.status = status;
	}

	public Integer getViewCount() {
		return viewCount;
	}

	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}

	public Long getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(Long uploadedBy) {
		this.uploadedBy = uploadedBy;
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