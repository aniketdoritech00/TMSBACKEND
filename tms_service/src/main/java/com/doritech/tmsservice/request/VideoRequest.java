package com.doritech.tmsservice.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class VideoRequest {

	@NotNull(message = "{video.title.notnull}")
	@NotBlank(message = "{video.title.notblank}")
	@Size(min = 2, max = 255, message = "{video.title.size}")
	private String videoTitle;

	@Size(max = 2000, message = "{video.description.size}")
	private String videoDescription;

	@Size(max = 500, message = "{video.thumbnailUrl.size}")
	@Pattern(regexp = "^(https?://).*$", message = "{video.thumbnailUrl.pattern}")
	private String thumbnailUrl;

	@Min(value = 0, message = "{video.durationSeconds.min}")
	private Integer durationSeconds;

	@Size(max = 20, message = "{video.format.size}")
	private String videoFormat;

	@Size(max = 20, message = "{video.resolution.size}")
	private String resolution;

	private Boolean isSecure;

	private Boolean allowDownload;

	private Boolean allowScreenRecord;

	private Boolean allowScreenshot;

	@NotNull(message = "{video.status.notnull}")
	@Pattern(regexp = "^(ACTIVE|INACTIVE|ARCHIVED)$", message = "{video.status.pattern}")
	private String status;

	@NotNull(message = "{video.uploadedBy.notnull}")
	private Long uploadedBy;

	public VideoRequest() {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getUploadedBy() {
		return uploadedBy;
	}

	public void setUploadedBy(Long uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	@Override
	public String toString() {
		return "VideoRequest [videoTitle=" + videoTitle + ", status=" + status + ", uploadedBy=" + uploadedBy + "]";
	}
}