package com.doritech.tmsservice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.doritech.tmsservice.serviceImpl.VideoMetadataAsyncService;

@Component
public class VideoMetadataAsyncListener {

	private static final Logger log = LoggerFactory.getLogger(VideoMetadataAsyncListener.class);

	private final VideoMetadataAsyncService videoMetadataAsyncService;

	public VideoMetadataAsyncListener(VideoMetadataAsyncService videoMetadataAsyncService) {

		this.videoMetadataAsyncService = videoMetadataAsyncService;
	}

	@Async("videoTaskExecutor")
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleVideoMetadataEvent(VideoMetadataProcessEvent event) {

		try {

			log.info("Received video metadata event. videoId={}", event.getVideoId());

			videoMetadataAsyncService.processMetadata(event);

		} catch (Exception e) {

			log.error("Error while processing video metadata event. videoId={}",
					event != null ? event.getVideoId() : null, e);
		}
	}
}