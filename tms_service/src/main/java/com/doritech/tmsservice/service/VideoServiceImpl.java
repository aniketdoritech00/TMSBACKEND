package com.doritech.tmsservice.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.config.FileStorageProperties;
import com.doritech.tmsservice.enums.VideoStatus;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.request.VideoMetadata;
import com.doritech.tmsservice.request.VideoRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.VideoListResponse;
import com.doritech.tmsservice.response.VideoResponse;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.Video;
import com.doritech.tmsservice.tms.repository.VideoRepository;

@Service
public class VideoServiceImpl implements VideoService {

	private static final Logger log = LoggerFactory.getLogger(VideoServiceImpl.class);

	private final VideoRepository videoRepository;
	private final FileStorageService fileStorageService;
	private final FileStorageProperties fileStorageProperties;
	private final VideoMetadataService videoMetadataService;

	public VideoServiceImpl(VideoRepository videoRepository, FileStorageService fileStorageService,
			FileStorageProperties fileStorageProperties, VideoMetadataService videoMetadataService) {
		this.videoRepository = videoRepository;
		this.fileStorageService = fileStorageService;
		this.fileStorageProperties = fileStorageProperties;
		this.videoMetadataService = videoMetadataService;
	}

	@Override
	public ResponseEntity getVideoById(Long id) {

		log.info("getVideoById :: request received for id={}", id);

		if (id == null) {
			log.error("getVideoById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		Video video = videoRepository.findById(id).orElseThrow(() -> {
			log.error("getVideoById :: video not found for id={}", id);
			return new ResourceNotFoundException("Video not found with id: " + id);
		});

		log.info("getVideoById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), mapToFullResponse(video));
	}

	@Override
	public ResponseEntity getAllVideo(int page, int size, String sortBy, String sortDir) {

		log.info("getAllVideo :: request received with page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy,
				sortDir);

		if (page < 0) {
			log.error("getAllVideo :: page cannot be negative");
			throw new BadRequestException("Page number can not be negative");
		}

		if (size <= 0) {
			log.error("getAllVideo :: size must be greater than 0");
			throw new BadRequestException("Page size must be greater than 0");
		}

		if (size > 100) {
			log.error("getAllVideo :: size exceeds max limit={}", size);
			throw new BadRequestException("Page size can not exceed 100");
		}

		if (sortBy == null || sortBy.trim().isEmpty()) {
			sortBy = "videoId";
		}

		if (sortDir == null || sortDir.trim().isEmpty()) {
			sortDir = "desc";
		}

		Sort sort;

		if (sortDir.equalsIgnoreCase("desc")) {
			sort = Sort.by(sortBy).descending();
		} else if (sortDir.equalsIgnoreCase("asc")) {
			sort = Sort.by(sortBy).ascending();
		} else {
			log.error("getAllVideo :: invalid sort direction={}", sortDir);
			throw new BadRequestException("Invalid sort direction: " + sortDir);
		}

		Pageable pageable = PageRequest.of(page, size, sort);

		Page<Video> videoPage;

		try {

			videoPage = videoRepository.findAll(pageable);

		} catch (PropertyReferenceException e) {

			log.error("getAllVideo :: invalid sort field={}", sortBy, e);

			throw new BadRequestException("Invalid sort field: " + sortBy);

		} catch (Exception e) {

			log.error("getAllVideo :: error while fetching videos - {}", e.getMessage(), e);

			throw new DatabaseOperationException("Something went wrong while fetching videos");
		}

		List<VideoListResponse> responseList = videoPage.getContent().stream().map(this::mapToListResponse)
				.collect(Collectors.toList());

		PageResponse<VideoListResponse> pageResponse = new PageResponse<>();

		pageResponse.setContent(responseList);
		pageResponse.setPageNumber(videoPage.getNumber());
		pageResponse.setPageSize(videoPage.getSize());
		pageResponse.setTotalElements(videoPage.getTotalElements());
		pageResponse.setTotalPages(videoPage.getTotalPages());
		pageResponse.setLastPage(videoPage.isLast());

		log.info("getAllVideo :: {} of {} videos fetched successfully", responseList.size(),
				videoPage.getTotalElements());

		return new ResponseEntity("Video fetch successfully", HttpStatus.OK.value(), pageResponse);
	}

	@Override
	@Transactional(transactionManager = "tmsTransactionManager")
	public ResponseEntity deleteVideo(Long id) {

		if (id == null || id <= 0) {
			throw new BadRequestException("Invalid video id");
		}

		Video video = videoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));

		Long mappingCount = videoRepository.countVideoMappings(id);

		if (mappingCount != null && mappingCount > 0) {
			throw new BadRequestException("Video cannot be deleted because it is mapped with other records");
		}

		String videoPath = video.getVideoUrl();

		try {
			videoRepository.delete(video);
			if (videoPath != null && !videoPath.trim().isEmpty()) {
				Files.deleteIfExists(Paths.get(videoPath));
			}

		} catch (Exception e) {

			throw new DatabaseOperationException("Cannot delete video");
		}

		return new ResponseEntity("Video deleted successfully", HttpStatus.OK.value(), null);
	}

	private VideoResponse mapToFullResponse(Video entity) {
		VideoResponse response = new VideoResponse();
		response.setVideoId(entity.getVideoId());
		response.setVideoTitle(entity.getVideoTitle());
		response.setVideoDescription(entity.getVideoDescription());
		response.setVideoUrl(entity.getVideoUrl());
		response.setThumbnailUrl(entity.getThumbnailUrl());
		response.setDurationSeconds(entity.getDurationSeconds());
		response.setFileSizeBytes(entity.getFileSizeBytes());
		response.setVideoFormat(entity.getVideoFormat());
		response.setResolution(entity.getResolution());
		response.setIsSecure(entity.getIsSecure());
		response.setAllowDownload(entity.getAllowDownload());
		response.setAllowScreenRecord(entity.getAllowScreenRecord());
		response.setAllowScreenshot(entity.getAllowScreenshot());
		response.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
		response.setViewCount(entity.getViewCount());
		response.setUploadedBy(entity.getUploadedBy());
		response.setCreatedAt(entity.getCreatedAt());
		response.setUpdatedAt(entity.getUpdatedAt());
		return response;
	}

	private VideoListResponse mapToListResponse(Video entity) {
		return new VideoListResponse(entity.getVideoId(), entity.getVideoTitle(), entity.getVideoDescription(),
				entity.getStatus() != null ? entity.getStatus().name() : null, entity.getViewCount());
	}

	@Override
	public ResponseEntity uploadVideAndThumbnail(VideoRequest request, MultipartFile videoFile,
			MultipartFile thumbnailFile) {

		log.info("uploadVideo :: request received for title={}", request != null ? request.getVideoTitle() : null);

		if (request == null) {
			throw new BadRequestException("Video data is required");
		}

		if (videoFile == null || videoFile.isEmpty()) {
			throw new BadRequestException("Video file is required");
		}

		if (thumbnailFile == null || thumbnailFile.isEmpty()) {
			throw new BadRequestException("Thumbnail file is required");
		}

		if (request.getVideoTitle() == null || request.getVideoTitle().trim().isEmpty()) {
			throw new BadRequestException("Video title is required");
		}

		if (request.getIsSecure() == null) {
			throw new BadRequestException("isSecure is required");
		}

		if (request.getAllowDownload() == null) {
			throw new BadRequestException("allowDownload is required");
		}

		if (request.getAllowScreenRecord() == null) {
			throw new BadRequestException("allowScreenRecord is required");
		}

		if (request.getAllowScreenshot() == null) {
			throw new BadRequestException("allowScreenshot is required");
		}

		String videoPath = null;
		String thumbnailPath = null;

		try {

			videoPath = fileStorageService.storeFile(videoFile, fileStorageProperties.getVideoPath());

			thumbnailPath = fileStorageService.storeFile(thumbnailFile, fileStorageProperties.getImagePath());

			log.info("Video stored successfully at: {}", videoPath);
			log.info("Thumbnail stored successfully at: {}", thumbnailPath);

		} catch (Exception e) {

			log.error("File upload failed", e);

			throw new DatabaseOperationException("Unable to store video files");
		}

		VideoMetadata metadata;

		try {

			Path path = Paths.get(videoPath);

			metadata = videoMetadataService.extractMetadata(path);

			log.info("Video metadata extracted successfully. Duration={}, Format={}, Resolution={}",
					metadata.getDurationSeconds(), metadata.getVideoFormat(), metadata.getResolution());

		} catch (Exception e) {

			log.error("Metadata extraction failed for video: {}", videoPath, e);

			throw new BadRequestException("Unable to extract video metadata");
		}

		String videoFormat = metadata.getVideoFormat();

		String originalFileName = videoFile.getOriginalFilename();

		if (originalFileName != null && originalFileName.contains(".")) {

			int lastDot = originalFileName.lastIndexOf(".");

			if (lastDot < originalFileName.length() - 1) {

				videoFormat = originalFileName.substring(lastDot + 1).toLowerCase();
			}
		}

		if (videoFormat == null || videoFormat.trim().isEmpty()) {

			videoFormat = "unknown";
		}

		log.info("Video format detected: {}", videoFormat);

		Video video = new Video();

		video.setVideoTitle(request.getVideoTitle().trim());

		video.setVideoDescription(request.getVideoDescription());

		video.setVideoUrl(videoPath);

		video.setThumbnailUrl(thumbnailPath);

		video.setFileSizeBytes(videoFile.getSize());

		video.setDurationSeconds(metadata.getDurationSeconds());

		video.setVideoFormat(videoFormat);

		video.setResolution(metadata.getResolution());

		video.setIsSecure(request.getIsSecure());

		video.setAllowDownload(request.getAllowDownload());

		video.setAllowScreenRecord(request.getAllowScreenRecord());

		video.setAllowScreenshot(request.getAllowScreenshot());

		video.setStatus(VideoStatus.ACTIVE);

		video.setViewCount(0);

		video.setUploadedBy(CurrentUser.getUserId());

		video.setCreatedAt(LocalDateTime.now());

		video.setUpdatedAt(LocalDateTime.now());

		try {

			Video saved = videoRepository.save(video);

			log.info("Video details saved successfully. Video ID={}", saved.getVideoId());

			return new ResponseEntity("Video uploaded successfully", HttpStatus.CREATED.value(),
					mapToFullResponse(saved));

		} catch (Exception e) {

			log.error("Failed to save video details. Video path={}", videoPath, e);

			throw new DatabaseOperationException("Unable to save video details");
		}
	}

	@Override
	public byte[] getThumbnailByPath(String path) throws IOException {

		log.info("getThumbnailByPath :: reading thumbnail from path={}", path);

		if (path == null || path.trim().isEmpty()) {
			throw new BadRequestException("Thumbnail path is required");
		}

		Path filePath = Paths.get(path);

		if (!Files.exists(filePath)) {
			log.error("getThumbnailByPath :: thumbnail file not found={}", path);
			throw new BadRequestException("Thumbnail file not found");
		}

		if (!Files.isRegularFile(filePath)) {
			log.error("getThumbnailByPath :: path is not a file={}", path);
			throw new BadRequestException("Invalid thumbnail file");
		}

		try {

			byte[] imageData = Files.readAllBytes(filePath);

			log.info("getThumbnailByPath :: thumbnail read successfully, size={} bytes", imageData.length);

			return imageData;

		} catch (IOException e) {

			log.error("getThumbnailByPath :: error reading thumbnail={}", path, e);

			throw e;
		}
	}

	@Override
	public org.springframework.http.ResponseEntity<Resource> streamVideo(Long videoId) {

		log.info("streamVideo :: request received for videoId={}", videoId);

		if (videoId == null || videoId <= 0) {
			log.error("streamVideo :: invalid videoId={}", videoId);
			throw new BadRequestException("Invalid video id");
		}

		Video video;

		try {
			video = videoRepository.findById(videoId).orElseThrow(() -> {
				log.error("streamVideo :: video not found for videoId={}", videoId);
				return new ResourceNotFoundException("Video not found with id: " + videoId);
			});

		} catch (ResourceNotFoundException e) {
			throw e;

		} catch (Exception e) {

			log.error("streamVideo :: error while fetching videoId={}", videoId, e);

			throw new DatabaseOperationException("Something went wrong while fetching video");
		}

		String videoPath = video.getVideoUrl();

		if (videoPath == null || videoPath.trim().isEmpty()) {

			log.error("streamVideo :: video path is empty for videoId={}", videoId);

			throw new BadRequestException("Video file not found");
		}

		try {

			Path path = Paths.get(videoPath);

			if (!Files.exists(path)) {

				log.error("streamVideo :: video file does not exist. videoId={}, path={}", videoId, videoPath);

				throw new BadRequestException("Video file not found");
			}

			if (!Files.isRegularFile(path)) {

				log.error("streamVideo :: path is not a valid file. videoId={}, path={}", videoId, videoPath);

				throw new BadRequestException("Invalid video file");
			}

			Resource resource = new UrlResource(path.toUri());

			long fileLength = Files.size(path);

			String contentType = Files.probeContentType(path);

			if (contentType == null) {

				String videoFormat = video.getVideoFormat();

				if (videoFormat != null) {

					if (videoFormat.equalsIgnoreCase("mp4")) {
						contentType = "video/mp4";

					} else if (videoFormat.equalsIgnoreCase("webm")) {
						contentType = "video/webm";

					} else if (videoFormat.equalsIgnoreCase("avi")) {
						contentType = "video/x-msvideo";

					} else if (videoFormat.equalsIgnoreCase("mov")) {
						contentType = "video/quicktime";

					} else if (videoFormat.equalsIgnoreCase("mkv")) {
						contentType = "video/x-matroska";

					} else {
						contentType = "application/octet-stream";
					}

				} else {
					contentType = "application/octet-stream";
				}
			}

			log.info("streamVideo :: returning video successfully. videoId={}, size={}, contentType={}", videoId,
					fileLength, contentType);

			return org.springframework.http.ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.contentLength(fileLength)
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + "\"")
					.header(HttpHeaders.ACCEPT_RANGES, "none").body(resource);

		} catch (BadRequestException e) {

			throw e;

		} catch (IOException e) {

			log.error("streamVideo :: error reading video file. videoId={}, path={}", videoId, videoPath, e);

			throw new DatabaseOperationException("Unable to read video file");

		} catch (Exception e) {

			log.error("streamVideo :: failed to stream videoId={}", videoId, e);

			throw new DatabaseOperationException("Unable to stream video");
		}
	}
}