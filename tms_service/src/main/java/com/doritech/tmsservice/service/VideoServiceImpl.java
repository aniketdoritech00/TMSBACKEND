package com.doritech.tmsservice.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.config.FileStorageProperties;
import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.entity.Video;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.VideoRepository;
import com.doritech.tmsservice.request.VideoRequest;
import com.doritech.tmsservice.response.VideoListResponse;
import com.doritech.tmsservice.response.VideoResponse;

@Service
public class VideoServiceImpl implements VideoService {

	private static final Logger log = LoggerFactory.getLogger(VideoServiceImpl.class);

	@Autowired
	private VideoRepository videoRepository;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private FileStorageProperties fileStorageProperties;

	@Override
	public ResponseEntity createVideo(VideoRequest videoRequest, MultipartFile file) {

		log.info("createVideo :: request received for title={}", videoRequest.getVideoTitle());

		if (file == null || file.isEmpty()) {
			log.error("createVideo :: video file is missing");
			throw new BadRequestException("Video file must not be null");
		}

		String storedPath = fileStorageService.storeFile(file, fileStorageProperties.getVideoPath());

		Video video = new Video();
		video.setVideoTitle(videoRequest.getVideoTitle());
		video.setVideoDescription(videoRequest.getVideoDescription());
		video.setVideoUrl(storedPath);
		video.setThumbnailUrl(videoRequest.getThumbnailUrl());
		video.setDurationSeconds(videoRequest.getDurationSeconds());
		video.setFileSizeBytes(file.getSize());
		video.setVideoFormat(videoRequest.getVideoFormat());
		video.setResolution(videoRequest.getResolution());
		video.setIsSecure(videoRequest.getIsSecure());
		video.setAllowDownload(videoRequest.getAllowDownload());
		video.setAllowScreenRecord(videoRequest.getAllowScreenRecord());
		video.setAllowScreenshot(videoRequest.getAllowScreenshot());

		try {
			video.setStatus(Video.Status.valueOf(videoRequest.getStatus()));
		} catch (IllegalArgumentException e) {
			log.error("createVideo :: invalid status value={}", videoRequest.getStatus());
			throw new BadRequestException("Invalid status. Must be one of ACTIVE, INACTIVE, ARCHIVED");
		}

		video.setUploadedBy(videoRequest.getUploadedBy());

		Video saved;
		try {
			saved = videoRepository.save(video);
		} catch (Exception e) {
			log.error("createVideo :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving video");
		}

		log.info("createVideo :: video saved successfully with id={}", saved.getVideoId());

		return new ResponseEntity("Video saved successfully", HttpStatus.CREATED.value(), mapToFullResponse(saved));
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

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<Video> videoPage;
		try {
			videoPage = videoRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			log.error("getAllVideo :: invalid sort field={}", sortBy);
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			log.error("getAllVideo :: error while fetching - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while fetching videos");
		}

		// NOTE: getAll me sirf lightweight fields — heavy video data (url, format,
		// resolution) nahi bhejte.
		// Full detail sirf getVideoById se milega.
		List<VideoListResponse> responseList = videoPage.getContent().stream().map(this::mapToListResponse)
				.collect(Collectors.toList());

		Map<String, Object> pageData = new LinkedHashMap<>();
		pageData.put("content", responseList);
		pageData.put("pageNumber", videoPage.getNumber());
		pageData.put("pageSize", videoPage.getSize());
		pageData.put("totalElements", videoPage.getTotalElements());
		pageData.put("totalPages", videoPage.getTotalPages());
		pageData.put("isLast", videoPage.isLast());

		log.info("getAllVideo :: {} of {} videos fetched successfully", responseList.size(),
				videoPage.getTotalElements());

		return new ResponseEntity("Video fetch successfully", HttpStatus.OK.value(), pageData);
	}

	@Override
	public ResponseEntity deleteVideo(Long id) {

		log.info("deleteVideo :: request received for id={}", id);

		if (id == null) {
			log.error("deleteVideo :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		Video video = videoRepository.findById(id).orElseThrow(() -> {
			log.error("deleteVideo :: video not found for id={}", id);
			return new ResourceNotFoundException("Video not found with id: " + id);
		});

		try {
			videoRepository.delete(video);
		} catch (Exception e) {
			log.error("deleteVideo :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Cannot delete video, it may be linked to other records");
		}

		log.info("deleteVideo :: deleted successfully for id={}", id);

		return new ResponseEntity("Video deleted successfully", HttpStatus.OK.value(), null);
	}

	// Full detail mapping — sirf getById / create response ke liye
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

	// Lightweight mapping — getAll ke liye
	private VideoListResponse mapToListResponse(Video entity) {
		return new VideoListResponse(entity.getVideoId(), entity.getVideoTitle(),
				entity.getStatus() != null ? entity.getStatus().name() : null, entity.getViewCount());
	}
}