package com.doritech.tmsservice.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.exception.GenericException;
import com.doritech.tmsservice.request.VideoRequest;
import com.doritech.tmsservice.service.VideoService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.Video;
import com.doritech.tmsservice.tms.repository.VideoRepository;

@RestController
@RequestMapping("/api/tms/videos")
public class VideoController {

	private final VideoService videoService;
	private final VideoRepository videoRepository;

	public VideoController(VideoService videoService, VideoRepository videoRepository) {
		this.videoService = videoService;
		this.videoRepository = videoRepository;
	}

	@PostMapping(value = "/uploadVideoWithThumbnail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity uploadVideAndThumbnail(@RequestPart("video") MultipartFile videoFile,
			@RequestPart("thumbnail") MultipartFile thumbnailFile, @RequestPart("videoData") VideoRequest request) {
		return videoService.uploadVideAndThumbnail(request, videoFile, thumbnailFile);
	}

	@GetMapping("/getAllVideo")
	public ResponseEntity getAllVideo(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "videoId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		return videoService.getAllVideo(page, size, sortBy, sortDir);
	}

	@GetMapping("/getVideoThumbnailById")
	public org.springframework.http.ResponseEntity<ByteArrayResource> getVideoThumbnailById(@RequestParam Long videoId)
			throws GenericException {

		try {

			Video video = videoRepository.findById(videoId).orElseThrow(() -> new GenericException("Video not found"));

			String path = video.getThumbnailUrl();

			if (path == null || path.trim().isEmpty()) {
				throw new GenericException("Video thumbnail not found");
			}

			byte[] imageData = videoService.getThumbnailByPath(path);

			Path filePath = Paths.get(path);

			String contentType = Files.probeContentType(filePath);

			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			ByteArrayResource resource = new ByteArrayResource(imageData);

			return org.springframework.http.ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.contentLength(imageData.length).body(resource);

		} catch (IOException e) {

			throw new GenericException("Error reading video thumbnail content type", e);
		}
	}

	@GetMapping("/streamVideo")
	public org.springframework.http.ResponseEntity<Resource> streamVideo(@RequestParam Long videoId) {
		return videoService.streamVideo(videoId);
	}

	@DeleteMapping("/deleteVideo/{id}")
	public ResponseEntity deleteVideo(@PathVariable Long id) {
		return videoService.deleteVideo(id);
	}
}