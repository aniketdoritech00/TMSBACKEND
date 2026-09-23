package com.doritech.tmsservice.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
import com.doritech.tmsservice.tms.repository.VideoAccessControlRepository;
import com.doritech.tmsservice.tms.repository.VideoRepository;
import com.doritech.tmsservice.tms.repository.VideoSubProductRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class VideoServiceImpl implements VideoService {

	private static final Logger log = LoggerFactory.getLogger(VideoServiceImpl.class);

	private final VideoRepository videoRepository;
	private final FileStorageService fileStorageService;
	private final FileStorageProperties fileStorageProperties;
	private final VideoMetadataService videoMetadataService;
	private final VideoSubProductRepository videoSubProductRepository;
	private final VideoAccessControlRepository videoAccessControlRepository;

	public VideoServiceImpl(VideoRepository videoRepository, FileStorageService fileStorageService,
			FileStorageProperties fileStorageProperties, VideoMetadataService videoMetadataService,
			VideoSubProductRepository videoSubProductRepository,
			VideoAccessControlRepository videoAccessControlRepository) {
		this.videoRepository = videoRepository;
		this.fileStorageService = fileStorageService;
		this.fileStorageProperties = fileStorageProperties;
		this.videoMetadataService = videoMetadataService;
		this.videoSubProductRepository = videoSubProductRepository;
		this.videoAccessControlRepository = videoAccessControlRepository;
	}

	@Override
	public ResponseEntity getVideoDetailsById(Long id) {

		if (id == null || id <= 0) {
			return new ResponseEntity("ID must be greater than 0", HttpStatus.BAD_REQUEST.value(), null);
		}

		try {

			Optional<Video> videoOptional = videoRepository.findById(id);
			if (videoOptional.isEmpty()) {
				return new ResponseEntity("Video not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			Video video = videoOptional.get();
			List<Long> subProductIds = videoSubProductRepository.getSubProductIdsByVideoId(id);

			VideoResponse response = mapToFullResponse(video);

			response.setSubProductIds(subProductIds);

			return new ResponseEntity("Video fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			log.error("getVideoById :: error while fetching video with id={}", id, e);

			return new ResponseEntity("Something went wrong while fetching video",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
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
			videoAccessControlRepository.deleteByVideoId(id);
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
				entity.getStatus() != null ? entity.getStatus().name() : null, entity.getViewCount(),
				entity.getResolution(), entity.getVideoFormat(), entity.getDurationSeconds(),
				entity.getFileSizeBytes());
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

			System.out.println("Video metadataservice called");
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

//	@Override
//	public org.springframework.http.ResponseEntity<Resource> streamVideo(Long videoId) {
//
//		log.info("streamVideo :: request received for videoId={}", videoId);
//
//		if (videoId == null || videoId <= 0) {
//			log.error("streamVideo :: invalid videoId={}", videoId);
//			throw new BadRequestException("Invalid video id");
//		}
//
//		Video video;
//
//		try {
//			video = videoRepository.findById(videoId).orElseThrow(() -> {
//				log.error("streamVideo :: video not found for videoId={}", videoId);
//				return new ResourceNotFoundException("Video not found with id: " + videoId);
//			});
//
//		} catch (ResourceNotFoundException e) {
//			throw e;
//
//		} catch (Exception e) {
//
//			log.error("streamVideo :: error while fetching videoId={}", videoId, e);
//
//			throw new DatabaseOperationException("Something went wrong while fetching video");
//		}
//
//		String videoPath = video.getVideoUrl();
//
//		if (videoPath == null || videoPath.trim().isEmpty()) {
//
//			log.error("streamVideo :: video path is empty for videoId={}", videoId);
//
//			throw new BadRequestException("Video file not found");
//		}
//
//		try {
//
//			Path path = Paths.get(videoPath);
//
//			if (!Files.exists(path)) {
//
//				log.error("streamVideo :: video file does not exist. videoId={}, path={}", videoId, videoPath);
//
//				throw new BadRequestException("Video file not found");
//			}
//
//			if (!Files.isRegularFile(path)) {
//
//				log.error("streamVideo :: path is not a valid file. videoId={}, path={}", videoId, videoPath);
//
//				throw new BadRequestException("Invalid video file");
//			}
//
//			Resource resource = new UrlResource(path.toUri());
//
//			long fileLength = Files.size(path);
//
//			String contentType = Files.probeContentType(path);
//
//			if (contentType == null) {
//
//				String videoFormat = video.getVideoFormat();
//
//				if (videoFormat != null) {
//
//					if (videoFormat.equalsIgnoreCase("mp4")) {
//						contentType = "video/mp4";
//
//					} else if (videoFormat.equalsIgnoreCase("webm")) {
//						contentType = "video/webm";
//
//					} else if (videoFormat.equalsIgnoreCase("avi")) {
//						contentType = "video/x-msvideo";
//
//					} else if (videoFormat.equalsIgnoreCase("mov")) {
//						contentType = "video/quicktime";
//
//					} else if (videoFormat.equalsIgnoreCase("mkv")) {
//						contentType = "video/x-matroska";
//
//					} else {
//						contentType = "application/octet-stream";
//					}
//
//				} else {
//					contentType = "application/octet-stream";
//				}
//			}
//
//			log.info("streamVideo :: returning video successfully. videoId={}, size={}, contentType={}", videoId,
//					fileLength, contentType);
//
//			return org.springframework.http.ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
//					.contentLength(fileLength)
//					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + path.getFileName() + "\"")
//					.header(HttpHeaders.ACCEPT_RANGES, "none").body(resource);
//
//		} catch (BadRequestException e) {
//
//			throw e;
//
//		} catch (IOException e) {
//
//			log.error("streamVideo :: error reading video file. videoId={}, path={}", videoId, videoPath, e);
//
//			throw new DatabaseOperationException("Unable to read video file");
//
//		} catch (Exception e) {
//
//			log.error("streamVideo :: failed to stream videoId={}", videoId, e);
//
//			throw new DatabaseOperationException("Unable to stream video");
//		}
//	}

	@Override
	public void streamVideo(Long videoId, HttpServletRequest request, HttpServletResponse response) throws IOException {

		log.info("streamVideo :: request received for videoId={}", videoId);

		if (videoId == null || videoId <= 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid video id");
			return;
		}

		Video video;

		try {

			Optional<Video> videoOptional = videoRepository.findById(videoId);

			if (videoOptional.isEmpty()) {

				log.warn("streamVideo :: video not found for videoId={}", videoId);

				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Video not found with id: " + videoId);

				return;
			}

			video = videoOptional.get();

		} catch (Exception e) {

			log.error("streamVideo :: error while fetching videoId={}", videoId, e);

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Something went wrong while fetching video");

			return;
		}

		String videoPath = video.getVideoUrl();

		log.info("streamVideo :: video path={}", videoPath);

		if (videoPath == null || videoPath.trim().isEmpty()) {

			log.warn("streamVideo :: video path is empty for videoId={}", videoId);

			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Video file not found");

			return;
		}

		Path filePath;

		try {

			filePath = Paths.get(videoPath).toAbsolutePath().normalize();

			log.info("streamVideo :: resolved file path={}", filePath);

			if (!Files.exists(filePath)) {

				log.warn("streamVideo :: video file does not exist. videoId={}, path={}", videoId, filePath);

				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Video file not found");

				return;
			}

			if (!Files.isRegularFile(filePath)) {

				log.warn("streamVideo :: invalid video file. videoId={}, path={}", videoId, filePath);

				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid video file");

				return;
			}

		} catch (Exception e) {

			log.error("streamVideo :: error while resolving video path. videoId={}", videoId, e);

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to access video file");

			return;
		}

		long fileSize;

		try {

			fileSize = Files.size(filePath);

		} catch (IOException e) {

			log.error("streamVideo :: unable to read file size. videoId={}", videoId, e);

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to read video file");

			return;
		}

		if (fileSize <= 0) {

			log.warn("streamVideo :: video file is empty. videoId={}", videoId);

			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Video file is empty");

			return;
		}

		String contentType = resolveVideoContentType(filePath);

		response.setContentType(contentType);

		response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");

		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
				"inline; filename=\"" + filePath.getFileName().toString() + "\"");

		response.setHeader("Access-Control-Allow-Origin", "*");

		response.setHeader("Access-Control-Expose-Headers", "Content-Length, Content-Range, Accept-Ranges");

		response.setHeader("Cache-Control", "public, max-age=3600");

		String rangeHeader = request.getHeader(HttpHeaders.RANGE);

		log.info("streamVideo :: Range header={}", rangeHeader);

		if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {

			log.info("streamVideo :: streaming complete file. size={}", fileSize);

			response.setStatus(HttpServletResponse.SC_OK);

			response.setContentLengthLong(fileSize);

			try {

				streamBytesNio(filePath, 0, fileSize, response.getOutputStream());

				log.info("streamVideo :: complete video streaming finished. videoId={}", videoId);

			} catch (IOException e) {

				if (isClientDisconnected(e)) {

					log.warn("streamVideo :: client disconnected while streaming video: {}", filePath);

				} else {

					log.error("streamVideo :: error while streaming video. videoId={}", videoId, e);

					throw e;
				}
			}

			return;
		}

		try {

			String rangeValue = rangeHeader.substring(6).split(",")[0].trim();

			String[] rangeParts = rangeValue.split("-", 2);

			if (rangeParts.length == 0) {

				response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);

				response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize);

				return;
			}

			long start;

			if (!rangeParts[0].trim().isEmpty()) {

				start = Long.parseLong(rangeParts[0].trim());

			} else {

				long suffixLength = Long.parseLong(rangeParts.length > 1 ? rangeParts[1].trim() : "0");

				if (suffixLength <= 0) {

					response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);

					response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize);

					return;
				}

				start = Math.max(0, fileSize - suffixLength);
			}

			long end;
			if (rangeParts.length > 1 && !rangeParts[1].trim().isEmpty()) {

				end = Long.parseLong(rangeParts[1].trim());

			} else {

				end = fileSize - 1;
			}

			if (start < 0 || start >= fileSize || start > end) {

				log.warn("streamVideo :: invalid range. videoId={}, range={}, fileSize={}", videoId, rangeHeader,
						fileSize);

				response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);

				response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize);

				return;
			}

			end = Math.min(end, fileSize - 1);

			long contentLength = end - start + 1;

			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

			response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize);

			response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");

			response.setContentLengthLong(contentLength);

			log.info("streamVideo :: streaming range. videoId={}, start={}, end={}, length={}, totalSize={}", videoId,
					start, end, contentLength, fileSize);

			try {

				streamBytesNio(filePath, start, contentLength, response.getOutputStream());

				log.debug("streamVideo :: range streaming completed. videoId={}, start={}, end={}", videoId, start,
						end);

			} catch (IOException e) {

				if (isClientDisconnected(e)) {

					log.warn("streamVideo :: client disconnected during range streaming. videoId={}, start={}, end={}",
							videoId, start, end);

				} else {

					log.error("streamVideo :: error during range streaming. videoId={}", videoId, e);

					throw e;
				}
			}

		} catch (NumberFormatException e) {

			log.warn("streamVideo :: invalid Range header. videoId={}, range={}", videoId, rangeHeader);

			response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);

			response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize);
		}
	}

	private boolean isClientDisconnected(IOException e) {

		String message = e.getMessage();

		if (message == null) {
			return false;
		}

		String lowerMessage = message.toLowerCase();

		return lowerMessage.contains("connection reset") || lowerMessage.contains("broken pipe")
				|| lowerMessage.contains("connection aborted") || lowerMessage.contains("connection was aborted")
				|| lowerMessage.contains("stream closed");
	}

	private void streamBytesNio(Path filePath, long start, long length, OutputStream out) throws IOException {

		if (!Files.exists(filePath)) {
			throw new FileNotFoundException("File not found: " + filePath);
		}

		if (!Files.isRegularFile(filePath)) {
			throw new IOException("Not a regular file: " + filePath);
		}

		try (FileChannel fileChannel = FileChannel.open(filePath, StandardOpenOption.READ)) {

			fileChannel.position(start);

			ByteBuffer buffer = ByteBuffer.allocate(64 * 1024);

			long remaining = length;

			while (remaining > 0) {

				int bytesToRead = (int) Math.min(buffer.capacity(), remaining);

				buffer.clear();
				buffer.limit(bytesToRead);

				int bytesRead = fileChannel.read(buffer);

				if (bytesRead == -1) {
					break;
				}

				out.write(buffer.array(), 0, bytesRead);

				remaining -= bytesRead;
			}

			out.flush();
		}
	}

	private String resolveVideoContentType(Path filePath) {

		String fileName = filePath.getFileName().toString().toLowerCase();

		if (fileName.endsWith(".mp4")) {
			return "video/mp4";
		}

		if (fileName.endsWith(".webm")) {
			return "video/webm";
		}

		if (fileName.endsWith(".ogg") || fileName.endsWith(".ogv")) {
			return "video/ogg";
		}

		if (fileName.endsWith(".mov")) {
			return "video/quicktime";
		}

		if (fileName.endsWith(".avi")) {
			return "video/x-msvideo";
		}

		if (fileName.endsWith(".mkv")) {
			return "video/x-matroska";
		}

		return "application/octet-stream";
	}

	@Override
	public org.springframework.http.ResponseEntity<Resource> downloadVideo(Long videoId) {

		log.info("downloadVideo :: request received for videoId={}", videoId);

		if (videoId == null || videoId <= 0) {
			log.error("downloadVideo :: invalid videoId={}", videoId);
			throw new BadRequestException("Invalid video id");
		}

		Video video;

		try {

			video = videoRepository.findById(videoId).orElseThrow(() -> {
				log.error("downloadVideo :: video not found for videoId={}", videoId);
				return new ResourceNotFoundException("Video not found with id: " + videoId);
			});

		} catch (ResourceNotFoundException e) {

			throw e;

		} catch (Exception e) {

			log.error("downloadVideo :: error while fetching videoId={}", videoId, e);

			throw new DatabaseOperationException("Something went wrong while fetching video");
		}

		String videoPath = video.getVideoUrl();

		if (videoPath == null || videoPath.trim().isEmpty()) {

			log.error("downloadVideo :: video path is empty for videoId={}", videoId);

			throw new BadRequestException("Video file not found");
		}

		try {

			Path path = Paths.get(videoPath);

			if (!Files.exists(path)) {

				log.error("downloadVideo :: video file does not exist. videoId={}, path={}", videoId, videoPath);

				throw new BadRequestException("Video file not found");
			}

			if (!Files.isRegularFile(path)) {

				log.error("downloadVideo :: path is not a valid file. videoId={}, path={}", videoId, videoPath);

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

			String fileName = path.getFileName().toString();

			log.info("downloadVideo :: returning video successfully. videoId={}, size={}, contentType={}", videoId,
					fileLength, contentType);

			return org.springframework.http.ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.contentLength(fileLength)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
					.body(resource);

		} catch (BadRequestException e) {

			throw e;

		} catch (IOException e) {

			log.error("downloadVideo :: error reading video file. videoId={}, path={}", videoId, videoPath, e);

			throw new DatabaseOperationException("Unable to read video file");

		} catch (Exception e) {

			log.error("downloadVideo :: failed to download videoId={}", videoId, e);

			throw new DatabaseOperationException("Unable to download video");
		}
	}

	@Override
	public ResponseEntity uploadVideo(VideoRequest request, MultipartFile videoFile) {

		if (request == null) {
			throw new BadRequestException("Video data is required");
		}

		if (videoFile == null || videoFile.isEmpty()) {
			throw new BadRequestException("Video file is required");
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

		try {

			videoPath = fileStorageService.storeFile(videoFile, fileStorageProperties.getVideoPath());

			log.info("Video stored successfully at: {}", videoPath);

		} catch (Exception e) {

			log.error("Video upload failed", e);

			throw new DatabaseOperationException("Unable to store video file");
		}

		VideoMetadata metadata;

		try {

			Path path = Paths.get(videoPath);

			metadata = videoMetadataService.extractMetadata(path);

		} catch (Exception e) {

			try {
				Files.deleteIfExists(Paths.get(videoPath));
			} catch (Exception deleteException) {
				log.warn("Unable to delete uploaded video: {}", videoPath);
			}

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

		Video video = new Video();

		video.setVideoTitle(request.getVideoTitle().trim());
		video.setVideoDescription(request.getVideoDescription());
		video.setVideoUrl(videoPath);
		video.setThumbnailUrl(null);
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
			return new ResponseEntity("Video uploaded successfully", HttpStatus.CREATED.value(),
					mapToFullResponse(saved));
		} catch (Exception e) {
			try {
				Files.deleteIfExists(Paths.get(videoPath));
			} catch (Exception deleteException) {
				log.warn("Unable to delete uploaded video: {}", videoPath);
			}
			throw new DatabaseOperationException("Unable to save video details");
		}
	}
}