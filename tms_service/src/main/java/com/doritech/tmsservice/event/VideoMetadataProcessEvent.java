package com.doritech.tmsservice.event;

public class VideoMetadataProcessEvent {

	private final Long videoId;
	private final String videoPath;

	public VideoMetadataProcessEvent(Long videoId, String videoPath) {
		this.videoId = videoId;
		this.videoPath = videoPath;
	}

	public Long getVideoId() {
		return videoId;
	}

	public String getVideoPath() {
		return videoPath;
	}
}