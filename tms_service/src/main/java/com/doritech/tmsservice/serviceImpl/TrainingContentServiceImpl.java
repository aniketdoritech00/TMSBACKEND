package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.enums.ContentType;
import com.doritech.tmsservice.enums.UserVideoStatus;
import com.doritech.tmsservice.request.TrainingContentRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.TrainingContentResponse;
import com.doritech.tmsservice.service.TrainingContentService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.Training;
import com.doritech.tmsservice.tms.entity.TrainingAssignment;
import com.doritech.tmsservice.tms.entity.TrainingContent;
import com.doritech.tmsservice.tms.entity.UserVideo;
import com.doritech.tmsservice.tms.entity.Video;
import com.doritech.tmsservice.tms.repository.TrainingAssignmentRepository;
import com.doritech.tmsservice.tms.repository.TrainingContentRepository;
import com.doritech.tmsservice.tms.repository.TrainingRepository;
import com.doritech.tmsservice.tms.repository.UserVideoRepository;
import com.doritech.tmsservice.tms.repository.VideoRepository;

@Service
public class TrainingContentServiceImpl implements TrainingContentService {

	private final TrainingContentRepository trainingContentRepository;
	private final TrainingRepository trainingRepository;
	private final VideoRepository videoRepository;
	private final TrainingAssignmentRepository trainingAssignmentRepository;
	private final UserVideoRepository userVideoRepository;

	public TrainingContentServiceImpl(TrainingContentRepository trainingContentRepository,
			TrainingRepository trainingRepository, VideoRepository videoRepository, TrainingAssignmentRepository trainingAssignmentRepository, UserVideoRepository userVideoRepository) {

		this.trainingContentRepository = trainingContentRepository;
		this.trainingRepository = trainingRepository;
		this.videoRepository = videoRepository;
		this.trainingAssignmentRepository = trainingAssignmentRepository;
		this.userVideoRepository = userVideoRepository;
	}

//	@Override
//	public ResponseEntity createTrainingContent(TrainingContentRequest request) {
//
//		try {
//
//			if (request == null) {
//				return new ResponseEntity("Training content data is required!", HttpStatus.BAD_REQUEST.value(), null);
//			}
//			if (request.getTrainingId() == null || request.getTrainingId() <= 0) {
//				return new ResponseEntity("Valid training id is required!", HttpStatus.BAD_REQUEST.value(), null);
//			}
//			if (request.getContentType() == null || request.getContentType().trim().isEmpty()) {
//				return new ResponseEntity("Content type is required!", HttpStatus.BAD_REQUEST.value(), null);
//			}
//			if (request.getContentReferenceId() == null || request.getContentReferenceId() <= 0) {
//				return new ResponseEntity("Valid content reference id is required!", HttpStatus.BAD_REQUEST.value(),
//						null);
//			}
//
//			Long currentUserId = CurrentUser.getUserId();
//			Optional<Training> trainingOptional = trainingRepository.findById(request.getTrainingId());
//			if (trainingOptional.isEmpty()) {
//				return new ResponseEntity("Training not found with id: " + request.getTrainingId(),
//						HttpStatus.NOT_FOUND.value(), null);
//			}
//			Training training = trainingOptional.get();
//			ContentType contentType;
//			try {
//				contentType = ContentType.valueOf(request.getContentType().trim().toUpperCase());
//			} catch (IllegalArgumentException e) {
//				return new ResponseEntity("Invalid content type: " + request.getContentType(),
//						HttpStatus.BAD_REQUEST.value(), null);
//			}
//			boolean alreadyExists = trainingContentRepository
//					.existsByTraining_TrainingIdAndContentTypeAndContentReferenceId(request.getTrainingId(),
//							contentType, request.getContentReferenceId());
//
//			if (alreadyExists) {
//				return new ResponseEntity("Training content already exists for this training!",
//						HttpStatus.CONFLICT.value(), null);
//			}
//
//			TrainingContent trainingContent = new TrainingContent();
//			trainingContent.setTraining(training);
//			trainingContent.setContentType(contentType);
//			trainingContent.setContentReferenceId(request.getContentReferenceId());
//			trainingContent.setRequired(request.getIsRequired() != null ? request.getIsRequired() : false);
//
//			trainingContent.setCreatedBy(currentUserId);
//			trainingContent.setCreatedAt(LocalDateTime.now());
//			TrainingContent savedTrainingContent = trainingContentRepository.save(trainingContent);
//
//			return new ResponseEntity("Training content created successfully!", HttpStatus.CREATED.value(),
//					mapToResponse(savedTrainingContent));
//
//		} catch (DataIntegrityViolationException e) {
//			e.printStackTrace();
//			return new ResponseEntity("Training content already exists or violates database constraints!",
//					HttpStatus.CONFLICT.value(), null);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
//		}
//	}
//

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity createTrainingContent(TrainingContentRequest request) {

		try {

			if (request == null) {

				return new ResponseEntity("Training content data is required!", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTrainingId() == null || request.getTrainingId() <= 0) {

				return new ResponseEntity("Valid training id is required!", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getContentType() == null || request.getContentType().trim().isEmpty()) {

				return new ResponseEntity("Content type is required!", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getContentReferenceId() == null || request.getContentReferenceId() <= 0) {

				return new ResponseEntity("Valid content reference id is required!", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			Long currentUserId = CurrentUser.getUserId();

			Optional<Training> trainingOptional = trainingRepository.findById(request.getTrainingId());

			if (trainingOptional.isEmpty()) {

				return new ResponseEntity("Training not found with id: " + request.getTrainingId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			Training training = trainingOptional.get();

			ContentType contentType;

			try {

				contentType = ContentType.valueOf(request.getContentType().trim().toUpperCase());

			} catch (IllegalArgumentException e) {

				return new ResponseEntity("Invalid content type: " + request.getContentType(),
						HttpStatus.BAD_REQUEST.value(), null);
			}

			boolean alreadyExists = trainingContentRepository
					.existsByTraining_TrainingIdAndContentTypeAndContentReferenceId(request.getTrainingId(),
							contentType, request.getContentReferenceId());

			if (alreadyExists) {

				return new ResponseEntity("Training content already exists for this training!",
						HttpStatus.CONFLICT.value(), null);
			}

			Video video = null;

			if (contentType == ContentType.VIDEO) {

				video = videoRepository.findById(request.getContentReferenceId()).orElse(null);

				if (video == null) {

					return new ResponseEntity("Video not found with id: " + request.getContentReferenceId(),
							HttpStatus.NOT_FOUND.value(), null);
				}
			}

			/*
			 * Create Training Content
			 */
			TrainingContent trainingContent = new TrainingContent();

			trainingContent.setTraining(training);

			trainingContent.setContentType(contentType);

			trainingContent.setContentReferenceId(request.getContentReferenceId());

			trainingContent.setRequired(request.getIsRequired() != null ? request.getIsRequired() : false);

			trainingContent.setCreatedBy(currentUserId);

			trainingContent.setCreatedAt(LocalDateTime.now());

			TrainingContent savedTrainingContent = trainingContentRepository.save(trainingContent);

			if (contentType == ContentType.VIDEO) {

				Long videoId = request.getContentReferenceId();

				List<TrainingAssignment> assignments = trainingAssignmentRepository
						.findByTraining_TrainingId(request.getTrainingId());

				if (assignments != null && !assignments.isEmpty()) {

					for (TrainingAssignment assignment : assignments) {
						Long userId = assignment.getUserId();

						if (userId == null || userId <= 0) {
							continue;
						}

						boolean userVideoExists = userVideoRepository.existsByUserIdAndVideo_VideoId(userId, videoId);
						if (userVideoExists) {
							continue;
						}

						UserVideo userVideo = new UserVideo();

						userVideo.setUserId(userId);

						userVideo.setVideo(video);

						userVideo.setTrainingAssignment(assignment);

						userVideo.setStatus(UserVideoStatus.ASSIGNED);

						userVideo.setWatchedCount(0);

						userVideo.setWatchedSeconds(0);

						userVideo.setLastWatchedAt(null);

						userVideo.setCompletedAt(null);

						userVideo.setExpiryDate(assignment.getDueDate());

						userVideo.setAssignedAt(LocalDateTime.now());

						userVideo.setAssignedBy(currentUserId);

						/*
						 * Save UserVideo
						 */
						userVideoRepository.save(userVideo);
					}
				}
			}

			return new ResponseEntity("Training content created successfully!", HttpStatus.CREATED.value(),
					mapToResponse(savedTrainingContent));

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("Training content already exists or violates database constraints!",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTrainingContentById(Long id) {

		try {

			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid training content id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TrainingContent> optionalTrainingContent = trainingContentRepository.findById(id);

			if (optionalTrainingContent.isEmpty()) {
				return new ResponseEntity("Training content not found with id: " + id, HttpStatus.NOT_FOUND.value(),
						null);
			}

			TrainingContentResponse response = mapToResponse(optionalTrainingContent.get());

			return new ResponseEntity("Training content fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllTrainingContent() {
		try {
			List<TrainingContent> trainingContents = trainingContentRepository.findAll();
			if (trainingContents.isEmpty()) {
				return new ResponseEntity("Training content not found", HttpStatus.NOT_FOUND.value(), null);
			}

			List<TrainingContentResponse> responseList = trainingContents.stream().map(this::mapToResponse).toList();
			return new ResponseEntity("Training contents fetched successfully", HttpStatus.OK.value(), responseList);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTrainingContentByTrainingId(Long trainingId) {
		try {
			if (trainingId == null || trainingId <= 0) {
				return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
			}
			if (!trainingRepository.existsById(trainingId)) {
				return new ResponseEntity("Training not found with id: " + trainingId, HttpStatus.NOT_FOUND.value(),
						null);
			}

			List<TrainingContent> trainingContents = trainingContentRepository.findByTraining_TrainingId(trainingId);
			if (trainingContents.isEmpty()) {
				return new ResponseEntity("No training content found for training id: " + trainingId,
						HttpStatus.NOT_FOUND.value(), null);
			}
			List<TrainingContentResponse> responseList = trainingContents.stream().map(this::mapToResponse).toList();
			return new ResponseEntity("Training contents fetched successfully", HttpStatus.OK.value(), responseList);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity updateTrainingContent(Long id, TrainingContentRequest request) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid training content id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request == null) {

				return new ResponseEntity("Training content data is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTrainingId() == null || request.getTrainingId() <= 0) {

				return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getContentType() == null || request.getContentType().trim().isEmpty()) {

				return new ResponseEntity("Content type is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getContentReferenceId() == null || request.getContentReferenceId() <= 0) {

				return new ResponseEntity("Valid content reference id is required", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			Long currentUserId = CurrentUser.getUserId();

			Optional<TrainingContent> optionalTrainingContent = trainingContentRepository.findById(id);

			if (optionalTrainingContent.isEmpty()) {

				return new ResponseEntity("Training content not found with id: " + id, HttpStatus.NOT_FOUND.value(),
						null);
			}

			TrainingContent trainingContent = optionalTrainingContent.get();

			Optional<Training> optionalTraining = trainingRepository.findById(request.getTrainingId());

			if (optionalTraining.isEmpty()) {

				return new ResponseEntity("Training not found with id: " + request.getTrainingId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			Training training = optionalTraining.get();

			/*
			 * Convert String contentType from request into ContentType enum.
			 *
			 * Example: "VIDEO" -> ContentType.VIDEO
			 */
			ContentType contentType;

			try {

				contentType = ContentType.valueOf(request.getContentType().trim().toUpperCase());

			} catch (IllegalArgumentException e) {

				return new ResponseEntity("Invalid content type: " + request.getContentType(),
						HttpStatus.BAD_REQUEST.value(), null);
			}

			/*
			 * Check duplicate training content.
			 *
			 * IMPORTANT: Pass ContentType enum here, NOT String.
			 */
			boolean alreadyExists = trainingContentRepository
					.existsByTraining_TrainingIdAndContentTypeAndContentReferenceIdAndTrainingContentIdNot(
							request.getTrainingId(), contentType, request.getContentReferenceId(), id);

			if (alreadyExists) {

				return new ResponseEntity("Training content already exists for this training",
						HttpStatus.CONFLICT.value(), null);
			}

			trainingContent.setTraining(training);
			trainingContent.setContentType(contentType);
			trainingContent.setContentReferenceId(request.getContentReferenceId());
			if (request.getIsRequired() != null) {
				trainingContent.setRequired(request.getIsRequired());
			}

			trainingContent.setUpdatedBy(currentUserId);
			trainingContent.setUpdatedAt(LocalDateTime.now());
			TrainingContent updatedTrainingContent = trainingContentRepository.save(trainingContent);
			TrainingContentResponse response = mapToResponse(updatedTrainingContent);
			return new ResponseEntity("Training content updated successfully", HttpStatus.OK.value(), response);

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			return new ResponseEntity("Training content already exists or violates database constraints",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity deleteTrainingContent(Long id) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid training content id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TrainingContent> optionalTrainingContent = trainingContentRepository.findById(id);
			if (optionalTrainingContent.isEmpty()) {
				return new ResponseEntity("Training content not found with id: " + id, HttpStatus.NOT_FOUND.value(),
						null);
			}

			TrainingContent trainingContent = optionalTrainingContent.get();
			trainingContentRepository.delete(trainingContent);
			trainingContentRepository.flush();
			return new ResponseEntity("Training content deleted successfully", HttpStatus.OK.value(), null);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			return new ResponseEntity("Training content cannot be deleted because it is linked to other records",
					HttpStatus.CONFLICT.value(), null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Something went wrong while deleting training content",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private TrainingContent mapToEntity(TrainingContentRequest request, Training training) {
		TrainingContent trainingContent = new TrainingContent();
		trainingContent.setTraining(training);
		trainingContent.setContentType(parseContentType(request.getContentType()));
		trainingContent.setContentReferenceId(request.getContentReferenceId());
		if (request.getIsRequired() != null) {
			trainingContent.setRequired(request.getIsRequired());
		} else {
			trainingContent.setRequired(false);
		}
		return trainingContent;
	}

	private TrainingContentResponse mapToResponse(TrainingContent entity) {
		TrainingContentResponse response = new TrainingContentResponse();
		response.setTrainingContentId(entity.getTrainingContentId());
		if (entity.getTraining() != null) {
			response.setTrainingId(entity.getTraining().getTrainingId());
			response.setTrainingCode(entity.getTraining().getTrainingCode());
			response.setTrainingName(entity.getTraining().getTrainingName());
		}

		response.setContentType(entity.getContentType().name());
		response.setContentReferenceId(entity.getContentReferenceId());
		response.setIsRequired(entity.isRequired());
		response.setCreatedBy(entity.getCreatedBy());
		response.setUpdatedBy(entity.getUpdatedBy());
		response.setCreatedAt(entity.getCreatedAt());
		response.setUpdatedAt(entity.getUpdatedAt());

		return response;
	}

	private ContentType parseContentType(String contentType) {
		try {
			return ContentType.valueOf(contentType.trim().toUpperCase());
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid content type: " + contentType);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllTrainingContent(int page, int size, String sortBy, String sortDir) {
		try {
			if (page < 0) {
				return new ResponseEntity("Page number cannot be negative", HttpStatus.BAD_REQUEST.value(), null);
			}
			if (size <= 0) {
				return new ResponseEntity("Page size must be greater than 0", HttpStatus.BAD_REQUEST.value(), null);
			}
			if (size > 400) {
				return new ResponseEntity("Page size cannot exceed 100", HttpStatus.BAD_REQUEST.value(), null);
			}
			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "trainingContentId";
			}
			if (sortDir == null || sortDir.trim().isEmpty()) {
				sortDir = "asc";
			}
			if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
				return new ResponseEntity("Invalid sort direction. Use 'asc' or 'desc'", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			List<String> allowedSortFields = List.of("trainingContentId", "contentType", "contentReferenceId",
					"isRequired", "createdAt", "updatedAt");
			if (!allowedSortFields.contains(sortBy)) {
				return new ResponseEntity("Invalid sort field: " + sortBy, HttpStatus.BAD_REQUEST.value(), null);
			}

			Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
			Pageable pageable = PageRequest.of(page, size, sort);
			Page<TrainingContent> trainingContentPage;
			try {
				trainingContentPage = trainingContentRepository.findAll(pageable);
			} catch (PropertyReferenceException e) {
				return new ResponseEntity("Invalid sort field: " + sortBy, HttpStatus.BAD_REQUEST.value(), null);
			}

			List<TrainingContentResponse> responseList = trainingContentPage.getContent().stream()
					.map(this::mapToResponse).collect(Collectors.toList());
			PageResponse<TrainingContentResponse> pageResponse = new PageResponse<>(responseList,
					trainingContentPage.getNumber(), trainingContentPage.getSize(),
					trainingContentPage.getTotalElements(), trainingContentPage.getTotalPages(),
					trainingContentPage.isLast());

			return new ResponseEntity("Training contents fetched successfully", HttpStatus.OK.value(), pageResponse);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Something went wrong while fetching training contents",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}
}