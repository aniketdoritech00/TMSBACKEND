package com.doritech.tmsservice.serviceImpl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.event.VideoMetadataProcessEvent;
import com.doritech.tmsservice.request.VideoMetadata;
import com.doritech.tmsservice.service.VideoMetadataService;
import com.doritech.tmsservice.tms.entity.Video;
import com.doritech.tmsservice.tms.repository.VideoRepository;

@Service
public class VideoMetadataAsyncService {

	private static final Logger log = LoggerFactory.getLogger(VideoMetadataAsyncService.class);

	private final VideoRepository videoRepository;
	private final VideoMetadataService videoMetadataService;

	public VideoMetadataAsyncService(VideoRepository videoRepository, VideoMetadataService videoMetadataService) {

		this.videoRepository = videoRepository;
		this.videoMetadataService = videoMetadataService;
	}

	@Transactional("tmsTransactionManager")
	public void processMetadata(VideoMetadataProcessEvent event) {

		if (event == null) {

			log.warn("VideoMetadataAsyncService :: event is null");

			return;
		}

		Long videoId = event.getVideoId();
		String videoPath = event.getVideoPath();

		try {

			if (videoId == null) {

				log.warn("VideoMetadataAsyncService :: videoId is null");

				return;
			}

			if (videoPath == null || videoPath.trim().isEmpty()) {

				log.warn("VideoMetadataAsyncService :: videoPath is empty for videoId={}", videoId);

				return;
			}

			/*
			 * ======================================================== CHECK FILE
			 * ========================================================
			 */

			Path path = Paths.get(videoPath);

			if (!java.nio.file.Files.exists(path)) {

				log.error("Video file does not exist. videoId={}, path={}", videoId, videoPath);

				return;
			}

			log.info("Starting async video metadata extraction. videoId={}", videoId);

			VideoMetadata metadata = videoMetadataService.extractMetadata(path);

			Video video = videoRepository.findById(videoId).orElse(null);

			if (video == null) {

				log.warn("Video not found while updating metadata. videoId={}", videoId);

				return;
			}

			/*
			 * ======================================================== UPDATE METADATA
			 * ========================================================
			 */

			video.setDurationSeconds(metadata.getDurationSeconds());

			video.setResolution(metadata.getResolution());

			/*
			 * We already have format from filename.
			 *
			 * But if filename format was unknown and FFPROBE gives a format, use FFPROBE
			 * format as fallback.
			 */

			if (video.getVideoFormat() == null || video.getVideoFormat().trim().isEmpty()
					|| "unknown".equalsIgnoreCase(video.getVideoFormat())) {

				video.setVideoFormat(metadata.getVideoFormat());
			}

			video.setUpdatedAt(LocalDateTime.now());

			videoRepository.save(video);

			log.info("Async video metadata updated successfully. videoId={}", videoId);

		} catch (Exception e) {

			/*
			 * IMPORTANT: Metadata failure should NOT delete the uploaded video.
			 *
			 * The video upload itself was already successful.
			 */

			log.error("Failed to process video metadata asynchronously. videoId={}", videoId, e);
		}
	}
}