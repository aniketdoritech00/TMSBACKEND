package com.doritech.tmsservice.serviceImpl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.config.FileStorageProperties;
import com.doritech.tmsservice.enums.VideoStatus;
import com.doritech.tmsservice.event.VideoMetadataProcessEvent;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceAlreadyExistsException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.request.VideoRequest;
import com.doritech.tmsservice.request.VideoSubProductRequest;
import com.doritech.tmsservice.request.VideoUpdateRequest;
import com.doritech.tmsservice.response.VideoResponse;
import com.doritech.tmsservice.response.VideoSubProductResponse;
import com.doritech.tmsservice.service.FileStorageService;
import com.doritech.tmsservice.service.VideoMetadataService;
import com.doritech.tmsservice.service.VideoSubProductService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.Video;
import com.doritech.tmsservice.tms.entity.VideoSubProduct;
import com.doritech.tmsservice.tms.entity.VideoSubProduct.VideoSubProductId;
import com.doritech.tmsservice.tms.repository.VideoRepository;
import com.doritech.tmsservice.tms.repository.VideoSubProductRepository;

@Service
public class VideoSubProductServiceImpl implements VideoSubProductService {

	private static final Logger log = LoggerFactory.getLogger(VideoSubProductServiceImpl.class);

	private final VideoSubProductRepository videoSubProductRepository;
	private final FileStorageProperties fileStorageProperties;
	private final VideoRepository videoRepository;
	private final FileStorageService fileStorageService;
	private final ApplicationEventPublisher eventPublisher;
	
	public VideoSubProductServiceImpl(VideoSubProductRepository videoSubProductRepository,
			FileStorageProperties fileStorageProperties, VideoRepository videoRepository,
			FileStorageService fileStorageService, ApplicationEventPublisher eventPublisher) {
		this.videoSubProductRepository = videoSubProductRepository;
		this.fileStorageProperties = fileStorageProperties;
		this.fileStorageService = fileStorageService;
		this.videoRepository = videoRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public ResponseEntity assignVideoToSubProduct(VideoSubProductRequest request) {

		log.info("assignVideoToSubProduct :: videoId={}, subProductId={}", request.getVideoId(),
				request.getSubProductId());

		VideoSubProductId id = new VideoSubProductId(request.getVideoId(), request.getSubProductId());

		if (videoSubProductRepository.existsById(id)) {
			log.error("assignVideoToSubProduct :: mapping already exists for videoId={}, subProductId={}",
					request.getVideoId(), request.getSubProductId());
			throw new ResourceAlreadyExistsException("Video is already assigned to this sub product");
		}

		VideoSubProduct mapping = new VideoSubProduct();
		mapping.setId(id);
		mapping.setAssignedBy(request.getAssignedBy());

		VideoSubProduct saved;
		try {
			saved = videoSubProductRepository.save(mapping);
		} catch (Exception e) {
			log.error("assignVideoToSubProduct :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while assigning video to sub product");
		}

		log.info("assignVideoToSubProduct :: assigned successfully videoId={}, subProductId={}", request.getVideoId(),
				request.getSubProductId());

		return new ResponseEntity("Video assigned to sub product successfully", HttpStatus.CREATED.value(),
				mapToResponse(saved));
	}

	@Override
	public ResponseEntity getVideosBySubProductId(Long subProductId) {

		log.info("getVideosBySubProductId :: request received for subProductId={}", subProductId);

		if (subProductId == null) {
			log.error("getVideosBySubProductId :: subProductId is null");
			throw new BadRequestException("Sub Product ID can not be null");
		}

		List<VideoSubProduct> mappings = videoSubProductRepository.findByIdSubProductId(subProductId);

		List<VideoSubProductResponse> responseList = mappings.stream().map(this::mapToResponse)
				.collect(Collectors.toList());

		log.info("getVideosBySubProductId :: {} mappings fetched for subProductId={}", responseList.size(),
				subProductId);

		return new ResponseEntity("Video list fetch successfully", HttpStatus.OK.value(), responseList);
	}

	@Override
	public ResponseEntity removeVideoFromSubProduct(Long videoId, Long subProductId) {

		log.info("removeVideoFromSubProduct :: videoId={}, subProductId={}", videoId, subProductId);

		if (videoId == null || subProductId == null) {
			log.error("removeVideoFromSubProduct :: videoId or subProductId is null");
			throw new BadRequestException("Video ID and Sub Product ID can not be null");
		}

		VideoSubProductId id = new VideoSubProductId(videoId, subProductId);

		VideoSubProduct mapping = videoSubProductRepository.findById(id).orElseThrow(() -> {
			log.error("removeVideoFromSubProduct :: mapping not found for videoId={}, subProductId={}", videoId,
					subProductId);
			return new ResourceNotFoundException("Mapping not found for given video and sub product");
		});

		try {
			videoSubProductRepository.delete(mapping);
		} catch (Exception e) {
			log.error("removeVideoFromSubProduct :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while removing the mapping");
		}

		log.info("removeVideoFromSubProduct :: removed successfully videoId={}, subProductId={}", videoId,
				subProductId);

		return new ResponseEntity("Video removed from sub product successfully", HttpStatus.OK.value(), null);
	}

	private VideoSubProductResponse mapToResponse(VideoSubProduct entity) {
		return new VideoSubProductResponse(entity.getId().getVideoId(), entity.getId().getSubProductId(),
				entity.getAssignedAt(), entity.getAssignedBy());
	}

//
//	@Override
//	@Transactional("tmsTransactionManager")
//	public ResponseEntity uploadVideAndThumbnail(VideoRequest request, MultipartFile videoFile,
//			MultipartFile thumbnailFile, List<Long> subProductIds) {
//
//		if (request == null) {
//			throw new BadRequestException("Video data is required");
//		}
//
//		if (videoFile == null || videoFile.isEmpty()) {
//			throw new BadRequestException("Video file is required");
//		}
//
//		// Thumbnail is optional
//		if (subProductIds == null || subProductIds.isEmpty()) {
//			throw new BadRequestException("At least one sub product is required");
//		}
//
//		if (request.getVideoTitle() == null || request.getVideoTitle().trim().isEmpty()) {
//			throw new BadRequestException("Video title is required");
//		}
//
//		if (request.getIsSecure() == null) {
//			throw new BadRequestException("isSecure is required");
//		}
//
//		if (request.getAllowDownload() == null) {
//			throw new BadRequestException("allowDownload is required");
//		}
//
//		if (request.getAllowScreenRecord() == null) {
//			throw new BadRequestException("allowScreenRecord is required");
//		}
//
//		if (request.getAllowScreenshot() == null) {
//			throw new BadRequestException("allowScreenshot is required");
//		}
//
//		// Validate sub-product IDs before uploading the large video
//		for (Long subProductId : subProductIds) {
//			if (subProductId == null || subProductId <= 0) {
//				throw new BadRequestException("Invalid sub product id: " + subProductId);
//			}
//		}
//
//		String videoPath = null;
//		String thumbnailPath = null;
//
//		try {
//
//			// Store video
//			videoPath = fileStorageService.storeFile(videoFile, fileStorageProperties.getVideoPath());
//
//			// Store thumbnail only when provided
//			if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
//
//				thumbnailPath = fileStorageService.storeFile(thumbnailFile, fileStorageProperties.getImagePath());
//			}
//
//			// Extract video metadata
//			Path path = Paths.get(videoPath);
//
//			VideoMetadata metadata = videoMetadataService.extractMetadata(path);
//
//			String videoFormat = metadata.getVideoFormat();
//
//			String originalFileName = videoFile.getOriginalFilename();
//
//			if (originalFileName != null && originalFileName.contains(".")) {
//
//				int lastDot = originalFileName.lastIndexOf(".");
//
//				if (lastDot < originalFileName.length() - 1) {
//
//					videoFormat = originalFileName.substring(lastDot + 1).toLowerCase();
//				}
//			}
//
//			if (videoFormat == null || videoFormat.trim().isEmpty()) {
//				videoFormat = "unknown";
//			}
//
//			// Create Video entity
//			Video video = new Video();
//
//			video.setVideoTitle(request.getVideoTitle().trim());
//			video.setVideoDescription(request.getVideoDescription());
//
//			video.setVideoUrl(videoPath);
//
//			// Thumbnail can be null
//			video.setThumbnailUrl(thumbnailPath);
//
//			video.setFileSizeBytes(videoFile.getSize());
//			video.setDurationSeconds(metadata.getDurationSeconds());
//			video.setVideoFormat(videoFormat);
//			video.setResolution(metadata.getResolution());
//
//			video.setIsSecure(request.getIsSecure());
//			video.setAllowDownload(request.getAllowDownload());
//			video.setAllowScreenRecord(request.getAllowScreenRecord());
//			video.setAllowScreenshot(request.getAllowScreenshot());
//
//			video.setStatus(VideoStatus.ACTIVE);
//			video.setViewCount(0);
//
//			video.setUploadedBy(CurrentUser.getUserId());
//
//			video.setCreatedAt(LocalDateTime.now());
//			video.setUpdatedAt(LocalDateTime.now());
//
//			// Save video
//			Video savedVideo = videoRepository.save(video);
//
//			// Create mappings for all sub-products
//			for (Long subProductId : subProductIds) {
//
//				VideoSubProductId mappingId = new VideoSubProductId(savedVideo.getVideoId(), subProductId);
//
//				// Normally this cannot exist because video is newly created,
//				// but keeping the check is safe.
//				if (videoSubProductRepository.existsById(mappingId)) {
//					continue;
//				}
//
//				VideoSubProduct mapping = new VideoSubProduct();
//
//				mapping.setId(mappingId);
//				mapping.setAssignedBy(CurrentUser.getUserId());
//
//				videoSubProductRepository.save(mapping);
//			}
//
//			return new ResponseEntity("Video uploaded and assigned to sub products successfully",
//					HttpStatus.CREATED.value(), mapToFullResponse(savedVideo));
//
//		} catch (BadRequestException e) {
//
//			// Delete uploaded files if anything fails
//			deleteFileQuietly(videoPath);
//			deleteFileQuietly(thumbnailPath);
//
//			throw e;
//
//		} catch (Exception e) {
//
//			// Delete files if database/mapping/metadata operation fails
//			deleteFileQuietly(videoPath);
//			deleteFileQuietly(thumbnailPath);
//
//			log.error("uploadVideAndThumbnail :: failed to upload video", e);
//
//			throw new DatabaseOperationException("Unable to upload video and assign sub products");
//		}
//	}
//
//	
//	
	public ResponseEntity uploadVideAndThumbnail(VideoRequest request, MultipartFile videoFile,
			MultipartFile thumbnailFile, List<Long> subProductIds) {

		if (request == null) {
			throw new BadRequestException("Video data is required");
		}

		if (videoFile == null || videoFile.isEmpty()) {
			throw new BadRequestException("Video file is required");
		}

		if (subProductIds == null || subProductIds.isEmpty()) {
			throw new BadRequestException("At least one sub product is required");
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

		for (Long subProductId : subProductIds) {
			if (subProductId == null || subProductId <= 0) {
				throw new BadRequestException("Invalid sub product id: " + subProductId);
			}
		}

		Long currentUserId = CurrentUser.getUserId();

		String videoPath = null;
		String thumbnailPath = null;

		try {

			videoPath = fileStorageService.storeFile(videoFile, fileStorageProperties.getVideoPath());

			if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
				thumbnailPath = fileStorageService.storeFile(thumbnailFile, fileStorageProperties.getImagePath());
			}

			String videoFormat = getVideoFormat(videoFile.getOriginalFilename());

			Video video = new Video();

			video.setVideoTitle(request.getVideoTitle().trim());
			video.setVideoDescription(request.getVideoDescription());
			video.setVideoUrl(videoPath);
			video.setThumbnailUrl(thumbnailPath);
			video.setFileSizeBytes(videoFile.getSize());

			video.setDurationSeconds(null);
			video.setResolution(null);

			video.setVideoFormat(videoFormat);

			video.setIsSecure(request.getIsSecure());
			video.setAllowDownload(request.getAllowDownload());
			video.setAllowScreenRecord(request.getAllowScreenRecord());
			video.setAllowScreenshot(request.getAllowScreenshot());

			video.setStatus(VideoStatus.ACTIVE);
			video.setViewCount(0);

			video.setUploadedBy(currentUserId);

			video.setCreatedAt(LocalDateTime.now());
			video.setUpdatedAt(LocalDateTime.now());

			Video savedVideo = videoRepository.save(video);

			List<VideoSubProduct> mappings = new ArrayList<>();

			for (Long subProductId : subProductIds) {

				VideoSubProductId mappingId = new VideoSubProductId(savedVideo.getVideoId(), subProductId);

				VideoSubProduct mapping = new VideoSubProduct();

				mapping.setId(mappingId);
				mapping.setAssignedBy(currentUserId);

				mappings.add(mapping);
			}

			if (!mappings.isEmpty()) {
				videoSubProductRepository.saveAll(mappings);
			}

			eventPublisher.publishEvent(new VideoMetadataProcessEvent(savedVideo.getVideoId(), videoPath));

			return new ResponseEntity("Video uploaded and assigned to sub products successfully",
					HttpStatus.CREATED.value(), mapToFullResponse(savedVideo));

		} catch (BadRequestException e) {

			// Delete uploaded files if anything fails
			deleteFileQuietly(videoPath);
			deleteFileQuietly(thumbnailPath);

			throw e;

		} catch (Exception e) {

			// Delete files if database/mapping/metadata operation fails
			deleteFileQuietly(videoPath);
			deleteFileQuietly(thumbnailPath);

			log.error("uploadVideAndThumbnail :: failed to upload video", e);

			throw new DatabaseOperationException("Unable to upload video and assign sub products");
		}
	}

	private String getVideoFormat(String originalFileName) {

		if (originalFileName == null || originalFileName.trim().isEmpty()) {

			return "unknown";
		}

		int lastDot = originalFileName.lastIndexOf(".");

		if (lastDot >= 0 && lastDot < originalFileName.length() - 1) {

			return originalFileName.substring(lastDot + 1).toLowerCase();
		}

		return "unknown";
	}

	private void deleteFileQuietly(String filePath) {
		if (filePath == null || filePath.trim().isEmpty()) {
			return;
		}
		try {
			Files.deleteIfExists(Paths.get(filePath));
		} catch (Exception e) {
			log.warn("Unable to delete file: {}", filePath);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity updateVideo(Long videoId, VideoUpdateRequest request) {

		if (videoId == null || videoId <= 0) {
			return new ResponseEntity("Invalid video id", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (request == null) {
			return new ResponseEntity("Video data is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (request.getVideoTitle() == null || request.getVideoTitle().trim().isEmpty()) {

			return new ResponseEntity("Video title is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (request.getIsSecure() == null) {
			return new ResponseEntity("isSecure is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (request.getAllowDownload() == null) {
			return new ResponseEntity("allowDownload is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (request.getAllowScreenRecord() == null) {
			return new ResponseEntity("allowScreenRecord is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (request.getAllowScreenshot() == null) {
			return new ResponseEntity("allowScreenshot is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (request.getSubProductIds() == null || request.getSubProductIds().isEmpty()) {

			return new ResponseEntity("At least one sub product is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		for (Long subProductId : request.getSubProductIds()) {

			if (subProductId == null || subProductId <= 0) {

				return new ResponseEntity("Invalid sub product id: " + subProductId, HttpStatus.BAD_REQUEST.value(),
						null);
			}
		}

		try {

			Optional<Video> videoOptional = videoRepository.findById(videoId);

			if (videoOptional.isEmpty()) {

				return new ResponseEntity("Video not found with id: " + videoId, HttpStatus.NOT_FOUND.value(), null);
			}

			Video video = videoOptional.get();

			// --------------------------------------------------
			// 2. Update only editable video fields
			// --------------------------------------------------

			video.setVideoTitle(request.getVideoTitle().trim());

			video.setVideoDescription(request.getVideoDescription());

			video.setIsSecure(request.getIsSecure());

			video.setAllowDownload(request.getAllowDownload());

			video.setAllowScreenRecord(request.getAllowScreenRecord());

			video.setAllowScreenshot(request.getAllowScreenshot());

			video.setUpdatedAt(LocalDateTime.now());

			// --------------------------------------------------
			// 3. Save video
			// --------------------------------------------------

			Video updatedVideo = videoRepository.save(video);

			// --------------------------------------------------
			// 4. Remove existing mappings
			// --------------------------------------------------

			List<VideoSubProduct> existingMappings = videoSubProductRepository.findByIdVideoId(videoId);

			videoSubProductRepository.deleteAll(existingMappings);

			// --------------------------------------------------
			// 5. Create new mappings
			// --------------------------------------------------

			List<Long> subProductIds = request.getSubProductIds().stream().distinct().collect(Collectors.toList());

			Long currentUserId = CurrentUser.getUserId();

			for (Long subProductId : subProductIds) {

				VideoSubProductId mappingId = new VideoSubProductId(videoId, subProductId);

				VideoSubProduct mapping = new VideoSubProduct();

				mapping.setId(mappingId);

				mapping.setAssignedBy(currentUserId);

				videoSubProductRepository.save(mapping);
			}

			// --------------------------------------------------
			// 6. Prepare response
			// --------------------------------------------------

			VideoResponse response = mapToFullResponse(updatedVideo);

			response.setSubProductIds(subProductIds);

			return new ResponseEntity("Video updated successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			log.error("updateVideo :: error while updating video id={}", videoId, e);

			return new ResponseEntity("Something went wrong while updating video",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
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
}