package com.doritech.tmsservice.request;

public class VideoRequest {

	private String videoTitle;
	private String videoDescription;

	private Boolean isSecure;
	private Boolean allowDownload;
	private Boolean allowScreenRecord;
	private Boolean allowScreenshot;

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
}