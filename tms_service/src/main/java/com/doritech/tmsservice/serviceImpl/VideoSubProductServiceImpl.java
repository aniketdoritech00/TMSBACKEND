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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.config.FileStorageProperties;
import com.doritech.tmsservice.enums.VideoStatus;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.request.VideoMetadata;
import com.doritech.tmsservice.request.VideoRequest;
import com.doritech.tmsservice.request.VideoSubProductRequest;
import com.doritech.tmsservice.request.VideoUpdateRequest;
import com.doritech.tmsservice.response.VideoResponse;
import com.doritech.tmsservice.response.VideoSubProductResponse;
import com.doritech.tmsservice.service.FileStorageService;
import com.doritech.tmsservice.service.VideoMetadataService;
import com.doritech.tmsservice.service.VideoSubProductService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.SubProduct;
import com.doritech.tmsservice.tms.entity.Video;
import com.doritech.tmsservice.tms.entity.VideoSubProduct;
import com.doritech.tmsservice.tms.entity.VideoSubProduct.VideoSubProductId;
import com.doritech.tmsservice.tms.repository.SubProductRepository;
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
	private final SubProductRepository subProductRepository;
	private final VideoMetadataService videoMetadataService;

	public VideoSubProductServiceImpl(VideoSubProductRepository videoSubProductRepository,
			FileStorageProperties fileStorageProperties, VideoRepository videoRepository,
			FileStorageService fileStorageService, ApplicationEventPublisher eventPublisher,
			SubProductRepository subProductRepository, VideoMetadataService videoMetadataService) {
		this.videoSubProductRepository = videoSubProductRepository;
		this.fileStorageProperties = fileStorageProperties;
		this.fileStorageService = fileStorageService;
		this.videoRepository = videoRepository;
		this.eventPublisher = eventPublisher;
		this.subProductRepository = subProductRepository;
		this.videoMetadataService = videoMetadataService;
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity assignVideoToSubProduct(VideoSubProductRequest request) {
		try {
			if (request == null) {
				return new ResponseEntity("Video sub product data is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getVideoId() == null || request.getVideoId() <= 0) {
				return new ResponseEntity("Valid video id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getSubProductId() == null || request.getSubProductId() <= 0) {
				return new ResponseEntity("Valid sub product id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<Video> optionalVideo = videoRepository.findById(request.getVideoId());
			if (optionalVideo.isEmpty()) {
				return new ResponseEntity("Video not found with id: " + request.getVideoId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			Optional<SubProduct> optionalSubProduct = subProductRepository.findById(request.getSubProductId());

			if (optionalSubProduct.isEmpty()) {

				return new ResponseEntity("Sub product not found with id: " + request.getSubProductId(),
						HttpStatus.NOT_FOUND.value(), null);
			}
			VideoSubProductId id = new VideoSubProductId(request.getVideoId(), request.getSubProductId());

			boolean alreadyExists = videoSubProductRepository.existsById(id);
			if (alreadyExists) {
				return new ResponseEntity("Video is already assigned to this sub product", HttpStatus.CONFLICT.value(),
						null);
			}

			VideoSubProduct mapping = new VideoSubProduct();
			mapping.setId(id);
			if (request.getAssignedBy() != null) {
				mapping.setAssignedBy(request.getAssignedBy());
			} else {
				mapping.setAssignedBy(CurrentUser.getUserId());
			}

			mapping.setAssignedAt(LocalDateTime.now());
			VideoSubProduct savedMapping = videoSubProductRepository.save(mapping);
			return new ResponseEntity("Video assigned to sub product successfully", HttpStatus.CREATED.value(),
					mapToResponse(savedMapping));

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			return new ResponseEntity("Video is already assigned to this sub product or violates database constraints",
					HttpStatus.CONFLICT.value(), null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getVideosBySubProductId(Long subProductId) {
		try {
			if (subProductId == null || subProductId <= 0) {
				return new ResponseEntity("Valid sub product id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<SubProduct> optionalSubProduct = subProductRepository.findById(subProductId);
			if (optionalSubProduct.isEmpty()) {
				return new ResponseEntity("Sub product not found with id: " + subProductId,
						HttpStatus.NOT_FOUND.value(), null);
			}

			List<VideoSubProduct> mappings = videoSubProductRepository.findByIdSubProductId(subProductId);
			List<VideoSubProductResponse> responseList = mappings.stream().map(this::mapToResponse)
					.collect(Collectors.toList());
			return new ResponseEntity("Video list fetched successfully", HttpStatus.OK.value(), responseList);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity removeVideoFromSubProduct(Long videoId, Long subProductId) {
		try {
			if (videoId == null || videoId <= 0) {
				return new ResponseEntity("Valid video id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (subProductId == null || subProductId <= 0) {
				return new ResponseEntity("Valid sub product id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			VideoSubProductId id = new VideoSubProductId(videoId, subProductId);

			Optional<VideoSubProduct> optionalMapping = videoSubProductRepository.findById(id);

			if (optionalMapping.isEmpty()) {
				return new ResponseEntity("Video is not assigned to this sub product", HttpStatus.NOT_FOUND.value(),
						null);
			}

			videoSubProductRepository.delete(optionalMapping.get());
			return new ResponseEntity("Video removed from sub product successfully", HttpStatus.OK.value(), null);

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			return new ResponseEntity("Unable to remove video from sub product because of database constraints",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private VideoSubProductResponse mapToResponse(VideoSubProduct entity) {
		if (entity == null || entity.getId() == null) {
			return null;
		}
		return new VideoSubProductResponse(entity.getId().getVideoId(), entity.getId().getSubProductId(),
				entity.getAssignedAt(), entity.getAssignedBy());
	}

	@Override
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

			VideoMetadata metadata = videoMetadataService.extractMetadata(Paths.get(videoPath));

			log.info("Video metadata extracted successfully. Duration={}, Format={}, Resolution={}",
					metadata.getDurationSeconds(), metadata.getVideoFormat(), metadata.getResolution());

			String videoFormat = metadata.getVideoFormat();

			if (videoFormat != null && !videoFormat.trim().isEmpty()) {
				videoFormat = videoFormat.split(",")[0].trim();
			}

			if (videoFormat == null || videoFormat.trim().isEmpty()) {
				videoFormat = getVideoFormat(videoFile.getOriginalFilename());
			}

			System.out.println("Video format is " + videoFormat);

			Video video = new Video();

			video.setVideoTitle(request.getVideoTitle().trim());
			video.setVideoDescription(request.getVideoDescription());
			video.setVideoUrl(videoPath);
			video.setThumbnailUrl(thumbnailPath);
			video.setFileSizeBytes(videoFile.getSize());

			video.setDurationSeconds(metadata.getDurationSeconds());
			video.setResolution(metadata.getResolution());

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

//		if (request.getSubProductIds() == null || request.getSubProductIds().isEmpty()) {
//
//			return new ResponseEntity("At least one sub product is required", HttpStatus.BAD_REQUEST.value(), null);
//		}

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