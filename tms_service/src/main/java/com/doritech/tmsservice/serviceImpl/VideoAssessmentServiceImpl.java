package com.doritech.tmsservice.serviceImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.enums.AssessmentStatus;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.request.VideoAssessmentRequest;
import com.doritech.tmsservice.request.VideoMetadata;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.VideoAssessmentResponse;
import com.doritech.tmsservice.service.VideoAssessmentService;
import com.doritech.tmsservice.service.VideoMetadataService;
import com.doritech.tmsservice.service.VideoServiceImpl;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.Training;
import com.doritech.tmsservice.tms.entity.TrainingAssignment;
import com.doritech.tmsservice.tms.entity.VideoAssessment;
import com.doritech.tmsservice.tms.repository.TrainingAssignmentRepository;
import com.doritech.tmsservice.tms.repository.TrainingRepository;
import com.doritech.tmsservice.tms.repository.VideoAssessmentRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class VideoAssessmentServiceImpl implements VideoAssessmentService {

	private static final Logger log = LoggerFactory.getLogger(VideoServiceImpl.class);

	private final VideoAssessmentRepository videoAssessmentRepository;
	private final TrainingRepository trainingRepository;
	private final TrainingAssignmentRepository trainingAssignmentRepository;
	private final VideoMetadataService videoMetadataService;

	public VideoAssessmentServiceImpl(VideoAssessmentRepository videoAssessmentRepository,
			TrainingRepository trainingRepository, TrainingAssignmentRepository trainingAssignmentRepository,
			VideoMetadataService videoMetadataService) {
		this.videoAssessmentRepository = videoAssessmentRepository;
		this.trainingRepository = trainingRepository;
		this.trainingAssignmentRepository = trainingAssignmentRepository;
		this.videoMetadataService = videoMetadataService;
	}

	@Value("${file.upload.assessment-video-path}")
	private String assessmentVideoFolder;

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity uploadVideoAssessment(VideoAssessmentRequest request, MultipartFile videoFile) {

		if (request == null) {
			throw new BadRequestException("Request cannot be null");
		}

		if (request.getTrainingId() == null || request.getTrainingId() <= 0) {
			throw new BadRequestException("Training ID is required");
		}

		if (request.getTrainingAssignmentId() == null || request.getTrainingAssignmentId() <= 0) {
			throw new BadRequestException("Training assignment ID is required");
		}

		if (videoFile == null || videoFile.isEmpty()) {
			throw new BadRequestException("Video file is required");
		}

		String originalFileName = videoFile.getOriginalFilename();

		if (originalFileName == null || originalFileName.trim().isEmpty()) {
			throw new BadRequestException("Invalid video file");
		}

		String extension = "";

		int lastDot = originalFileName.lastIndexOf(".");

		if (lastDot >= 0) {
			extension = originalFileName.substring(lastDot).toLowerCase();
		}

		if (!extension.equals(".mp4") && !extension.equals(".mov") && !extension.equals(".avi")
				&& !extension.equals(".mkv") && !extension.equals(".webm")) {

			throw new BadRequestException("Only MP4, MOV, AVI, MKV and WEBM video files are allowed");
		}

		Long userId = CurrentUser.getUserId();

		System.out.println("Current user id is " + userId);

		if (userId == null || userId <= 0) {
			throw new BadRequestException("Unable to identify current user");
		}

		Training training = trainingRepository.findById(request.getTrainingId()).orElse(null);

		if (training == null) {
			throw new BadRequestException("Training not found");
		}

		TrainingAssignment trainingAssignment = trainingAssignmentRepository.findById(request.getTrainingAssignmentId())
				.orElse(null);

		if (trainingAssignment == null) {
			throw new BadRequestException("Training assignment not found");
		}

		if (trainingAssignment.getTraining() == null || trainingAssignment.getTraining().getTrainingId() == null
				|| !trainingAssignment.getTraining().getTrainingId().equals(request.getTrainingId())) {

			throw new BadRequestException("Training assignment does not belong to the selected training");
		}

		if (trainingAssignment.getUserId() != null && !trainingAssignment.getUserId().equals(userId)) {

			return new ResponseEntity("This training assignment does not belong to the current user",
					HttpStatus.FORBIDDEN.value(), null);
		}

		boolean alreadySubmitted = videoAssessmentRepository
				.existsByTrainingAssignment_TrainingAssignmentIdAndUserId(request.getTrainingAssignmentId(), userId);

		if (alreadySubmitted) {
			return new ResponseEntity("Video assessment has already been submitted for this training assignment",
					HttpStatus.CONFLICT.value(), null);
		}

		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

		String fileName = "assessment_" + timestamp + "_" + UUID.randomUUID() + extension;

		Path trainingFolder = Paths.get(assessmentVideoFolder, "training_" + request.getTrainingId());

		Path assignmentFolder = trainingFolder.resolve("assignment_" + request.getTrainingAssignmentId());

		Path destination = assignmentFolder.resolve(fileName);

		/*
		 * Store video
		 */
		try {

			Files.createDirectories(assignmentFolder);

			Files.copy(videoFile.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

			System.out.println("Assessment video stored successfully at: " + destination);

		} catch (IOException e) {

			return new ResponseEntity("Failed to store video assessment: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}

		/*
		 * Extract video metadata
		 */
		VideoMetadata metadata;

		try {

			System.out.println("Video metadata service called");

			metadata = videoMetadataService.extractMetadata(destination);

			System.out.println("Video metadata extracted successfully. " + "Duration=" + metadata.getDurationSeconds()
					+ ", Format=" + metadata.getVideoFormat() + ", Resolution=" + metadata.getResolution());

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Unable to extract video metadata: " + e.getMessage(),
					HttpStatus.BAD_REQUEST.value(), null);
		}

		/*
		 * Validate duration
		 */
		if (metadata.getDurationSeconds() == null || metadata.getDurationSeconds() <= 0) {

			return new ResponseEntity("Unable to determine video duration", HttpStatus.BAD_REQUEST.value(), null);
		}

		VideoAssessment assessment = new VideoAssessment();

		assessment.setTraining(training);
		assessment.setUserId(userId);
		assessment.setTrainingAssignment(trainingAssignment);

		String videoUrl = "/VIDEO/ASSESSMENT/training_" + request.getTrainingId() + "/assignment_"
				+ request.getTrainingAssignmentId() + "/" + fileName;

		assessment.setVideoUrl(videoUrl);

		// File size
		assessment.setFileSizeBytes(videoFile.getSize());

		// Video duration in seconds
		assessment.setVideoDurationSeconds(metadata.getDurationSeconds());

		// Status
		assessment.setStatus(AssessmentStatus.SUBMITTED);

		// Submission time
		assessment.setSubmittedAt(LocalDateTime.now());

		/*
		 * Save assessment
		 */
		try {

			VideoAssessment savedAssessment = videoAssessmentRepository.save(assessment);

			System.out.println("Video assessment saved successfully. ID=" + savedAssessment.getVideoAssessmentId());

			return new ResponseEntity("Video assessment uploaded successfully", HttpStatus.CREATED.value(),
					savedAssessment);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to save video assessment: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getMyVideoAssessments(int page, int size, String sortBy, String sortDir) {

		try {

			if (page < 0) {
				return new ResponseEntity("Page number cannot be negative", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (size <= 0) {
				return new ResponseEntity("Page size must be greater than zero", HttpStatus.BAD_REQUEST.value(), null);
			}

			Long userId = CurrentUser.getUserId();

			if (userId == null || userId <= 0) {
				return new ResponseEntity("Unable to identify current user", HttpStatus.UNAUTHORIZED.value(), null);
			}

			Sort.Direction direction;

			if ("asc".equalsIgnoreCase(sortDir)) {
				direction = Sort.Direction.ASC;
			} else {
				direction = Sort.Direction.DESC;
			}

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<VideoAssessment> assessmentPage = videoAssessmentRepository.findByUserId(userId, pageable);

			List<VideoAssessmentResponse> responseList = assessmentPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<VideoAssessmentResponse> pageResponse = new PageResponse<>(responseList,
					assessmentPage.getNumber(), assessmentPage.getSize(), assessmentPage.getTotalElements(),
					assessmentPage.getTotalPages(), assessmentPage.isLast());

			return new ResponseEntity("Video assessments fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (PropertyReferenceException e) {

			return new ResponseEntity("Invalid sort field: " + sortBy, HttpStatus.BAD_REQUEST.value(), null);

		} catch (Exception e) {

			return new ResponseEntity("Failed to fetch video assessments: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private VideoAssessmentResponse mapToResponse(VideoAssessment assessment) {

		VideoAssessmentResponse response = new VideoAssessmentResponse();

		response.setVideoAssessmentId(assessment.getVideoAssessmentId());

		if (assessment.getTraining() != null) {

			response.setTrainingId(assessment.getTraining().getTrainingId());

			response.setTrainingCode(assessment.getTraining().getTrainingCode());

			response.setTrainingName(assessment.getTraining().getTrainingName());
		}

		response.setUserId(assessment.getUserId());

		if (assessment.getTrainingAssignment() != null) {

			response.setTrainingAssignmentId(assessment.getTrainingAssignment().getTrainingAssignmentId());
		}

		response.setVideoUrl(assessment.getVideoUrl());

		response.setVideoDurationSeconds(assessment.getVideoDurationSeconds());

		response.setFileSizeBytes(assessment.getFileSizeBytes());

		response.setStatus(assessment.getStatus());

		response.setSubmittedAt(assessment.getSubmittedAt());

		response.setEvaluatedAt(assessment.getEvaluatedAt());

		response.setEvaluatedBy(assessment.getEvaluatedBy());

		response.setResult(assessment.getResult());

		response.setEvaluationNotes(assessment.getEvaluationNotes());

		response.setScore(assessment.getScore());

		return response;
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getVideoAssessmentById(Long videoAssessmentId) {

		try {

			if (videoAssessmentId == null || videoAssessmentId <= 0) {

				return new ResponseEntity("Video assessment ID is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Long userId = CurrentUser.getUserId();

			if (userId == null || userId <= 0) {

				return new ResponseEntity("Unable to identify current user", HttpStatus.UNAUTHORIZED.value(), null);
			}

			VideoAssessment assessment = videoAssessmentRepository
					.findByVideoAssessmentIdAndUserId(videoAssessmentId, userId).orElse(null);

			if (assessment == null) {

				return new ResponseEntity("Video assessment not found", HttpStatus.NOT_FOUND.value(), null);
			}

			VideoAssessmentResponse response = mapToResponse(assessment);

			return new ResponseEntity("Video assessment fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			return new ResponseEntity("Failed to fetch video assessment: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public void streamVideo(Long videoId, HttpServletRequest request, HttpServletResponse response) throws IOException {

		log.info("streamVideo :: request received for videoId={}", videoId);

		if (videoId == null || videoId <= 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid video id");
			return;
		}

		VideoAssessment videoAssessment;

		try {

			Optional<VideoAssessment> videoOptional = videoAssessmentRepository.findById(videoId);

			if (videoOptional.isEmpty()) {

				log.warn("streamVideo :: video not found for videoId={}", videoId);

				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Video not found with id: " + videoId);

				return;
			}

			videoAssessment = videoOptional.get();

		} catch (Exception e) {

			log.error("streamVideo :: error while fetching videoId={}", videoId, e);

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Something went wrong while fetching video");

			return;
		}

		String videoPath = videoAssessment.getVideoUrl();

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
}