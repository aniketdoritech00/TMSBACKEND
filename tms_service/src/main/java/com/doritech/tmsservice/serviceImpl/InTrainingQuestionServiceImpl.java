package com.doritech.tmsservice.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.enums.TestQuestionType;
import com.doritech.tmsservice.request.InTrainingQuestionRequest;
import com.doritech.tmsservice.response.InTrainingQuestionResponse;
import com.doritech.tmsservice.response.InTrainingQuestionResultResponse;
import com.doritech.tmsservice.response.InTrainingResultResponse;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.service.InTrainingQuestionService;
import com.doritech.tmsservice.tms.entity.InTrainingQuestion;
import com.doritech.tmsservice.tms.entity.InTrainingUserResponse;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.Training;
import com.doritech.tmsservice.tms.entity.TrainingAssignment;
import com.doritech.tmsservice.tms.entity.Video;
import com.doritech.tmsservice.tms.repository.InTrainingQuestionRepository;
import com.doritech.tmsservice.tms.repository.InTrainingUserResponseRepository;
import com.doritech.tmsservice.tms.repository.TrainingAssignmentRepository;
import com.doritech.tmsservice.tms.repository.UserBatchRepository;
import com.doritech.tmsservice.tms.repository.VideoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class InTrainingQuestionServiceImpl implements InTrainingQuestionService {

	private final InTrainingQuestionRepository inTrainingQuestionRepository;
	private final VideoRepository videoRepository;
	private final ObjectMapper objectMapper;
	private final TrainingAssignmentRepository trainingAssignmentRepository;
	private final InTrainingUserResponseRepository inTrainingUserResponseRepository;
	private final UserBatchRepository userBatchRepository;

	public InTrainingQuestionServiceImpl(InTrainingQuestionRepository inTrainingQuestionRepository,
			VideoRepository videoRepository, ObjectMapper objectMapper,
			TrainingAssignmentRepository trainingAssignmentRepository,
			InTrainingUserResponseRepository inTrainingUserResponseRepository,
			UserBatchRepository userBatchRepository) {

		this.inTrainingQuestionRepository = inTrainingQuestionRepository;
		this.videoRepository = videoRepository;
		this.objectMapper = objectMapper;
		this.trainingAssignmentRepository = trainingAssignmentRepository;
		this.inTrainingUserResponseRepository = inTrainingUserResponseRepository;
		this.userBatchRepository = userBatchRepository;
	}

//	@Override
//	@Transactional("tmsTransactionManager")
//	public ResponseEntity createInTrainingQuestion(InTrainingQuestionRequest request) {
//		try {
//			if (request == null) {
//				return new ResponseEntity("In-training question data is required", HttpStatus.BAD_REQUEST.value(),
//						null);
//			}
//			if (request.getVideoId() == null || request.getVideoId() <= 0) {
//				return new ResponseEntity("Valid video id is required", HttpStatus.BAD_REQUEST.value(), null);
//			}
//			if (request.getTimestampSeconds() == null || request.getTimestampSeconds() < 0) {
//				return new ResponseEntity("Valid timestamp seconds is required", HttpStatus.BAD_REQUEST.value(), null);
//			}
//			if (request.getQuestionText() == null || request.getQuestionText().trim().isEmpty()) {
//				return new ResponseEntity("Question text is required", HttpStatus.BAD_REQUEST.value(), null);
//			}
//			if (request.getQuestionType() == null || request.getQuestionType().trim().isEmpty()) {
//				return new ResponseEntity("Question type is required", HttpStatus.BAD_REQUEST.value(), null);
//			}
//			Long currentUserId = CurrentUser.getUserId();
//			Optional<Video> optionalVideo = videoRepository.findById(request.getVideoId());
//			if (optionalVideo.isEmpty()) {
//				return new ResponseEntity("Video not found with id: " + request.getVideoId(),
//						HttpStatus.NOT_FOUND.value(), null);
//			}
//
//			TestQuestionType questionType;
//
//			try {
//
//				questionType = TestQuestionType.valueOf(request.getQuestionType().trim().toUpperCase());
//
//			} catch (IllegalArgumentException e) {
//
//				return new ResponseEntity("Invalid question type: " + request.getQuestionType(),
//						HttpStatus.BAD_REQUEST.value(), null);
//			}
//
//			boolean alreadyExists = inTrainingQuestionRepository
//					.existsByVideo_VideoIdAndTimestampSecondsAndQuestionText(request.getVideoId(),
//							request.getTimestampSeconds(), request.getQuestionText().trim());
//
//			if (alreadyExists) {
//
//				return new ResponseEntity(
//						"In-training question already exists for this video at the specified timestamp",
//						HttpStatus.CONFLICT.value(), null);
//			}
//
//			InTrainingQuestion question = new InTrainingQuestion();
//
//			question.setVideo(optionalVideo.get());
//			question.setTimestampSeconds(request.getTimestampSeconds());
//			question.setQuestionText(request.getQuestionText().trim());
//			question.setQuestionType(questionType);
//			question.setOptions(request.getOptions());
//			question.setCorrectAnswer(request.getCorrectAnswer());
//			question.setTimerSeconds(request.getTimerSeconds() != null ? request.getTimerSeconds() : 30);
//			question.setIsRequired(request.getIsRequired() != null ? request.getIsRequired() : true);
//			question.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
//			question.setCreatedAt(LocalDateTime.now());
//			question.setUpdatedAt(LocalDateTime.now());
//
//			InTrainingQuestion savedQuestion = inTrainingQuestionRepository.save(question);
//
//			return new ResponseEntity("In-training question created successfully", HttpStatus.CREATED.value(),
//					mapToResponse(savedQuestion));
//
//		} catch (DataIntegrityViolationException e) {
//
//			e.printStackTrace();
//
//			return new ResponseEntity("In-training question already exists or violates database constraints",
//					HttpStatus.CONFLICT.value(), null);
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
//		}
//	}
//
	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getInTrainingQuestionById(Long id) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid in-training question id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<InTrainingQuestion> optionalQuestion = inTrainingQuestionRepository.findById(id);

			if (optionalQuestion.isEmpty()) {

				return new ResponseEntity("In-training question not found with id: " + id, HttpStatus.NOT_FOUND.value(),
						null);
			}

			return new ResponseEntity("In-training question fetched successfully", HttpStatus.OK.value(),
					mapToResponse(optionalQuestion.get()));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllInTrainingQuestions() {

		try {

			List<InTrainingQuestion> questions = inTrainingQuestionRepository
					.findAll(Sort.by(Sort.Direction.ASC, "displayOrder"));

			List<InTrainingQuestionResponse> response = questions.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("In-training questions fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllInTrainingQuestions(int page, int size, String sortBy, String sortDir) {

		try {

			if (page < 0) {
				page = 0;
			}

			if (size <= 0) {
				size = 10;
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "questionId";
			}

			Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<InTrainingQuestion> questionPage = inTrainingQuestionRepository.findAll(pageable);

			List<InTrainingQuestionResponse> content = questionPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<InTrainingQuestionResponse> pageResponse = new PageResponse<>(content,
					questionPage.getNumber(), questionPage.getSize(), questionPage.getTotalElements(),
					questionPage.getTotalPages(), questionPage.isLast());

			return new ResponseEntity("In-training questions fetched successfully", HttpStatus.OK.value(),
					pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getInTrainingQuestionsByVideoId(Long videoId) {

		try {

			if (videoId == null || videoId <= 0) {

				return new ResponseEntity("Invalid video id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<Video> optionalVideo = videoRepository.findById(videoId);

			if (optionalVideo.isEmpty()) {

				return new ResponseEntity("Video not found with id: " + videoId, HttpStatus.NOT_FOUND.value(), null);
			}

			List<InTrainingQuestion> questions = inTrainingQuestionRepository
					.findByVideo_VideoIdOrderByDisplayOrderAsc(videoId);

			List<InTrainingQuestionResponse> response = questions.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("In-training questions fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getInTrainingQuestionsByVideoId(Long videoId, Long trainingAssignmentId) {

		try {

			if (videoId == null || videoId <= 0) {

				return new ResponseEntity("Invalid video id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {

				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Long currentUserId = CurrentUser.getUserId();
			System.out.println("Current user id is " + currentUserId);

			if (currentUserId == null || currentUserId <= 0) {

				return new ResponseEntity("Unable to identify current user", HttpStatus.UNAUTHORIZED.value(), null);
			}

			Optional<Video> optionalVideo = videoRepository.findById(videoId);

			if (optionalVideo.isEmpty()) {

				return new ResponseEntity("Video not found with id: " + videoId, HttpStatus.NOT_FOUND.value(), null);
			}

			Optional<TrainingAssignment> optionalAssignment = trainingAssignmentRepository
					.findById(trainingAssignmentId);

			if (optionalAssignment.isEmpty()) {

				return new ResponseEntity("Training assignment not found with id: " + trainingAssignmentId,
						HttpStatus.NOT_FOUND.value(), null);
			}

			TrainingAssignment trainingAssignment = optionalAssignment.get();

			boolean isAssignedDirectlyToUser = trainingAssignment.getUserId() != null
					&& trainingAssignment.getUserId().equals(currentUserId);

			boolean isAssignedThroughBatch = false;

			if (trainingAssignment.getBatch() != null && trainingAssignment.getBatch().getBatchId() != null) {

				isAssignedThroughBatch = userBatchRepository.existsByUserIdAndBatch_BatchId(currentUserId,
						trainingAssignment.getBatch().getBatchId());
			}

			if (!isAssignedDirectlyToUser && !isAssignedThroughBatch) {
				return new ResponseEntity("Training assignment does not belong to current user",
						HttpStatus.FORBIDDEN.value(), null);
			}

			List<InTrainingQuestion> questions = inTrainingQuestionRepository
					.findByVideo_VideoIdOrderByDisplayOrderAsc(videoId);

			List<InTrainingQuestionResponse> response = questions.stream().map(question -> {

				InTrainingQuestionResponse questionResponse = mapToResponse(question);

				Optional<InTrainingUserResponse> userResponse = inTrainingUserResponseRepository
						.findByQuestion_QuestionIdAndTrainingAssignment_TrainingAssignmentId(question.getQuestionId(),
								trainingAssignmentId);

				questionResponse.setIsAttempted(userResponse.isPresent());

				return questionResponse;

			}).collect(Collectors.toList());

			return new ResponseEntity("In-training questions fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getInTrainingQuestionsByVideoId(Long videoId, int page, int size, String sortBy,
			String sortDir) {

		try {

			if (videoId == null || videoId <= 0) {

				return new ResponseEntity("Invalid video id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<Video> optionalVideo = videoRepository.findById(videoId);

			if (optionalVideo.isEmpty()) {

				return new ResponseEntity("Video not found with id: " + videoId, HttpStatus.NOT_FOUND.value(), null);
			}

			if (page < 0) {
				page = 0;
			}

			if (size <= 0) {
				size = 10;
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "displayOrder";
			}

			Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<InTrainingQuestion> questionPage = inTrainingQuestionRepository.findByVideo_VideoId(videoId, pageable);

			List<InTrainingQuestionResponse> content = questionPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<InTrainingQuestionResponse> pageResponse = new PageResponse<>(content,
					questionPage.getNumber(), questionPage.getSize(), questionPage.getTotalElements(),
					questionPage.getTotalPages(), questionPage.isLast());

			return new ResponseEntity("In-training questions fetched successfully", HttpStatus.OK.value(),
					pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getInTrainingResultByVideoId(
			Long videoId,
			Long trainingAssignmentId) {

		try {

			if (videoId == null || videoId <= 0) {

				return new ResponseEntity(
						"Invalid video id",
						HttpStatus.BAD_REQUEST.value(),
						null);
			}

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {

				return new ResponseEntity(
						"Invalid training assignment id",
						HttpStatus.BAD_REQUEST.value(),
						null);
			}

			Long currentUserId = CurrentUser.getUserId();

			System.out.println("Current user id is " + currentUserId);

			if (currentUserId == null || currentUserId <= 0) {

				return new ResponseEntity(
						"Unable to identify current user",
						HttpStatus.UNAUTHORIZED.value(),
						null);
			}

			Optional<Video> optionalVideo = videoRepository.findById(videoId);

			if (optionalVideo.isEmpty()) {

				return new ResponseEntity(
						"Video not found with id: " + videoId,
						HttpStatus.NOT_FOUND.value(),
						null);
			}

			Optional<TrainingAssignment> optionalAssignment =
					trainingAssignmentRepository.findById(trainingAssignmentId);

			if (optionalAssignment.isEmpty()) {

				return new ResponseEntity(
						"Training assignment not found with id: "
								+ trainingAssignmentId,
						HttpStatus.NOT_FOUND.value(),
						null);
			}

			TrainingAssignment trainingAssignment =
					optionalAssignment.get();

			boolean isAssignedDirectlyToUser =
					trainingAssignment.getUserId() != null
							&& trainingAssignment.getUserId()
									.equals(currentUserId);

			boolean isAssignedThroughBatch = false;

			if (trainingAssignment.getBatch() != null
					&& trainingAssignment.getBatch().getBatchId() != null) {

				isAssignedThroughBatch =
						userBatchRepository.existsByUserIdAndBatch_BatchId(
								currentUserId,
								trainingAssignment.getBatch().getBatchId());
			}

			if (!isAssignedDirectlyToUser && !isAssignedThroughBatch) {

				return new ResponseEntity(
						"Training assignment does not belong to current user",
						HttpStatus.FORBIDDEN.value(),
						null);
			}

			Training training = trainingAssignment.getTraining();

			if (training == null) {

				return new ResponseEntity(
						"Training not found for training assignment",
						HttpStatus.NOT_FOUND.value(),
						null);
			}

			List<InTrainingQuestion> questions =
					inTrainingQuestionRepository
							.findByVideo_VideoIdOrderByDisplayOrderAsc(videoId);

			if (questions == null || questions.isEmpty()) {

				return new ResponseEntity(
						"No in-training questions found for video id: "
								+ videoId,
						HttpStatus.NOT_FOUND.value(),
						null);
			}

			int totalQuestions = questions.size();

			int attemptedQuestions = 0;

			int correctAnswers = 0;

			int wrongAnswers = 0;

			int skippedQuestions = 0;

			List<InTrainingQuestionResultResponse> questionResults =
					new ArrayList<>();

			for (InTrainingQuestion question : questions) {

				InTrainingQuestionResultResponse questionResult =
						new InTrainingQuestionResultResponse();

				questionResult.setQuestionId(
						question.getQuestionId());

				questionResult.setTimestampSeconds(
						question.getTimestampSeconds());

				questionResult.setQuestionText(
						question.getQuestionText());

				if (question.getQuestionType() != null) {

					questionResult.setQuestionType(
							question.getQuestionType().name());
				}

				questionResult.setOptions(
						question.getOptions());

				questionResult.setCorrectAnswer(
						question.getCorrectAnswer());


				Optional<InTrainingUserResponse> optionalUserResponse =
						inTrainingUserResponseRepository
								.findByQuestion_QuestionIdAndTrainingAssignment_TrainingAssignmentId(
										question.getQuestionId(),
										trainingAssignmentId);

				if (optionalUserResponse.isPresent()) {

					InTrainingUserResponse userResponse =
							optionalUserResponse.get();

					questionResult.setAttempted(true);

					questionResult.setUserAnswer(
							userResponse.getUserAnswer());

					questionResult.setCorrect(
							Boolean.TRUE.equals(
									userResponse.getIsCorrect()));

					questionResult.setSkipped(
							Boolean.TRUE.equals(
									userResponse.getIsSkipped()));

					questionResult.setTimeTakenSeconds(
							userResponse.getTimeTakenSeconds());

					if (Boolean.TRUE.equals(
							userResponse.getIsSkipped())) {

						skippedQuestions++;

					} else {

						attemptedQuestions++;

						if (Boolean.TRUE.equals(
								userResponse.getIsCorrect())) {

							correctAnswers++;

						} else {

							wrongAnswers++;
						}
					}

				} else {

					questionResult.setAttempted(false);

					questionResult.setCorrect(false);

					questionResult.setSkipped(false);

					questionResult.setUserAnswer(null);

					questionResult.setTimeTakenSeconds(null);

				}

				questionResults.add(questionResult);
			}

			/*
			 * ---------------------------------------------------------
			 * 8. Calculate score
			 * ---------------------------------------------------------
			 *
			 * Score = Correct Answers / Total Questions * 100
			 */

			BigDecimal overallScore = BigDecimal.ZERO;

			if (totalQuestions > 0) {

				overallScore = BigDecimal.valueOf(correctAnswers)
						.multiply(BigDecimal.valueOf(100))
						.divide(
								BigDecimal.valueOf(totalQuestions),
								2,
								RoundingMode.HALF_UP);
			}


			BigDecimal passingPercentage =
					training.getPassingPercentage();

			if (passingPercentage == null) {

				passingPercentage = BigDecimal.ZERO;
			}

			boolean passed =
					overallScore.compareTo(passingPercentage) >= 0;

			String result = passed ? "PASSED" : "FAILED";

			InTrainingResultResponse resultResponse =
					new InTrainingResultResponse();

			resultResponse.setTrainingAssignmentId(
					trainingAssignmentId);

			resultResponse.setTrainingId(
					training.getTrainingId());

			resultResponse.setTrainingName(
					training.getTrainingName());

			resultResponse.setVideoId(videoId);

			resultResponse.setTotalQuestions(
					totalQuestions);

			resultResponse.setAttemptedQuestions(
					attemptedQuestions);

			resultResponse.setCorrectAnswers(
					correctAnswers);

			resultResponse.setWrongAnswers(
					wrongAnswers);

			resultResponse.setSkippedQuestions(
					skippedQuestions);

			resultResponse.setOverallScore(
					overallScore);

			resultResponse.setPassingPercentage(
					passingPercentage);

			resultResponse.setPassed(passed);

			resultResponse.setResult(result);

			resultResponse.setQuestions(questionResults);

			return new ResponseEntity(
					"In-training result fetched successfully",
					HttpStatus.OK.value(),
					resultResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity(
					"Internal server error!",
					HttpStatus.INTERNAL_SERVER_ERROR.value(),
					null);
		}
	}
	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity updateInTrainingQuestion(Long id, InTrainingQuestionRequest request) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid in-training question id", HttpStatus.BAD_REQUEST.value(), null);
			}
			if (request == null) {
				return new ResponseEntity("In-training question data is required", HttpStatus.BAD_REQUEST.value(),
						null);
			}
			if (request.getVideoId() == null || request.getVideoId() <= 0) {
				return new ResponseEntity("Valid video id is required", HttpStatus.BAD_REQUEST.value(), null);
			}
			if (request.getTimestampSeconds() == null || request.getTimestampSeconds() < 0) {
				return new ResponseEntity("Valid timestamp seconds is required", HttpStatus.BAD_REQUEST.value(), null);
			}
			if (request.getQuestionText() == null || request.getQuestionText().trim().isEmpty()) {
				return new ResponseEntity("Question text is required", HttpStatus.BAD_REQUEST.value(), null);
			}
			if (request.getQuestionType() == null || request.getQuestionType().trim().isEmpty()) {
				return new ResponseEntity("Question type is required", HttpStatus.BAD_REQUEST.value(), null);
			}
			Optional<InTrainingQuestion> optionalQuestion = inTrainingQuestionRepository.findById(id);
			if (optionalQuestion.isEmpty()) {
				return new ResponseEntity("In-training question not found with id: " + id, HttpStatus.NOT_FOUND.value(),
						null);
			}
			Optional<Video> optionalVideo = videoRepository.findById(request.getVideoId());
			if (optionalVideo.isEmpty()) {
				return new ResponseEntity("Video not found with id: " + request.getVideoId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			TestQuestionType questionType;
			try {
				questionType = TestQuestionType.valueOf(request.getQuestionType().trim().toUpperCase());
			} catch (IllegalArgumentException e) {
				return new ResponseEntity("Invalid question type: " + request.getQuestionType(),
						HttpStatus.BAD_REQUEST.value(), null);
			}

			boolean alreadyExists = inTrainingQuestionRepository
					.existsByVideo_VideoIdAndTimestampSecondsAndQuestionTextAndQuestionIdNot(request.getVideoId(),
							request.getTimestampSeconds(), request.getQuestionText().trim(), id);
			if (alreadyExists) {
				return new ResponseEntity(
						"Another in-training question already exists for this video at the specified timestamp",
						HttpStatus.CONFLICT.value(), null);
			}

			InTrainingQuestion question = optionalQuestion.get();

			question.setVideo(optionalVideo.get());
			question.setTimestampSeconds(request.getTimestampSeconds());
			question.setQuestionText(request.getQuestionText().trim());
			question.setQuestionType(questionType);
			question.setOptions(request.getOptions());
			question.setCorrectAnswer(request.getCorrectAnswer());

			if (request.getTimerSeconds() != null) {
				question.setTimerSeconds(request.getTimerSeconds());
			}

			if (request.getIsRequired() != null) {
				question.setIsRequired(request.getIsRequired());
			}

			if (request.getDisplayOrder() != null) {
				question.setDisplayOrder(request.getDisplayOrder());
			}

			question.setUpdatedAt(LocalDateTime.now());

			InTrainingQuestion updatedQuestion = inTrainingQuestionRepository.save(question);

			return new ResponseEntity("In-training question updated successfully", HttpStatus.OK.value(),
					mapToResponse(updatedQuestion));

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("In-training question already exists or violates database constraints",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity deleteInTrainingQuestion(Long id) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid in-training question id", HttpStatus.BAD_REQUEST.value(), null);
			}
			Optional<InTrainingQuestion> optionalQuestion = inTrainingQuestionRepository.findById(id);
			if (optionalQuestion.isEmpty()) {
				return new ResponseEntity("In-training question not found with id: " + id, HttpStatus.NOT_FOUND.value(),
						null);
			}

			inTrainingQuestionRepository.delete(optionalQuestion.get());
			return new ResponseEntity("In-training question deleted successfully", HttpStatus.OK.value(), null);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			return new ResponseEntity("Cannot delete in-training question because it is being used",
					HttpStatus.CONFLICT.value(), null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private InTrainingQuestionResponse mapToResponse(InTrainingQuestion question) {

		InTrainingQuestionResponse response = new InTrainingQuestionResponse();

		response.setQuestionId(question.getQuestionId());

		if (question.getVideo() != null) {
			response.setVideoId(question.getVideo().getVideoId());
		}

		response.setTimestampSeconds(question.getTimestampSeconds());
		response.setQuestionText(question.getQuestionText());

		response.setQuestionType(question.getQuestionType() != null ? question.getQuestionType().name() : null);

		if (question.getOptions() != null && !question.getOptions().trim().isEmpty()) {
			try {
				List<String> options = objectMapper.readValue(question.getOptions(), new TypeReference<List<String>>() {
				});

				response.setOptions(options);

			} catch (Exception e) {
				response.setOptions(new ArrayList<>());
			}
		} else {
			response.setOptions(new ArrayList<>());
		}

		response.setCorrectAnswer(question.getCorrectAnswer());
		response.setTimerSeconds(question.getTimerSeconds());
		response.setIsRequired(question.getIsRequired());
		response.setDisplayOrder(question.getDisplayOrder());
		response.setCreatedAt(question.getCreatedAt());
		response.setUpdatedAt(question.getUpdatedAt());

		return response;
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity createInTrainingQuestion(List<InTrainingQuestionRequest> requests) {

	    try {

	        if (requests == null || requests.isEmpty()) {
	            return new ResponseEntity(
	                    "At least one in-training question is required",
	                    HttpStatus.BAD_REQUEST.value(),
	                    null);
	        }

	        Long currentUserId = CurrentUser.getUserId();

	        List<InTrainingQuestion> questionsToSave = new ArrayList<>();

	        for (InTrainingQuestionRequest request : requests) {

	            if (request == null) {
	                return new ResponseEntity(
	                        "In-training question data is required",
	                        HttpStatus.BAD_REQUEST.value(),
	                        null);
	            }

	            if (request.getVideoId() == null || request.getVideoId() <= 0) {
	                return new ResponseEntity(
	                        "Valid video id is required",
	                        HttpStatus.BAD_REQUEST.value(),
	                        null);
	            }

	            if (request.getTimestampSeconds() == null || request.getTimestampSeconds() < 0) {
	                return new ResponseEntity(
	                        "Valid timestamp seconds is required",
	                        HttpStatus.BAD_REQUEST.value(),
	                        null);
	            }

	            if (request.getQuestionText() == null
	                    || request.getQuestionText().trim().isEmpty()) {
	                return new ResponseEntity(
	                        "Question text is required",
	                        HttpStatus.BAD_REQUEST.value(),
	                        null);
	            }

	            if (request.getQuestionType() == null
	                    || request.getQuestionType().trim().isEmpty()) {
	                return new ResponseEntity(
	                        "Question type is required",
	                        HttpStatus.BAD_REQUEST.value(),
	                        null);
	            }

	            Optional<Video> optionalVideo =
	                    videoRepository.findById(request.getVideoId());

	            if (optionalVideo.isEmpty()) {
	                return new ResponseEntity(
	                        "Video not found with id: " + request.getVideoId(),
	                        HttpStatus.NOT_FOUND.value(),
	                        null);
	            }

	            TestQuestionType questionType;

	            try {

	                questionType = TestQuestionType.valueOf(
	                        request.getQuestionType()
	                                .trim()
	                                .toUpperCase());

	            } catch (IllegalArgumentException e) {

	                return new ResponseEntity(
	                        "Invalid question type: "
	                                + request.getQuestionType(),
	                        HttpStatus.BAD_REQUEST.value(),
	                        null);
	            }

	            boolean alreadyExists =
	                    inTrainingQuestionRepository
	                            .existsByVideo_VideoIdAndTimestampSecondsAndQuestionText(
	                                    request.getVideoId(),
	                                    request.getTimestampSeconds(),
	                                    request.getQuestionText().trim());

	            if (alreadyExists) {

	                return new ResponseEntity(
	                        "In-training question already exists for video id "
	                                + request.getVideoId()
	                                + " at timestamp "
	                                + request.getTimestampSeconds()
	                                + " with question text: "
	                                + request.getQuestionText().trim(),
	                        HttpStatus.CONFLICT.value(),
	                        null);
	            }

	            InTrainingQuestion question = new InTrainingQuestion();

	            question.setVideo(optionalVideo.get());

	            question.setTimestampSeconds(
	                    request.getTimestampSeconds());

	            question.setQuestionText(
	                    request.getQuestionText().trim());

	            question.setQuestionType(questionType);

	            question.setOptions(
	                    request.getOptions());

	            question.setCorrectAnswer(
	                    request.getCorrectAnswer());

	            question.setTimerSeconds(
	                    request.getTimerSeconds() != null
	                            ? request.getTimerSeconds()
	                            : 30);

	            question.setIsRequired(
	                    request.getIsRequired() != null
	                            ? request.getIsRequired()
	                            : true);

	            question.setDisplayOrder(
	                    request.getDisplayOrder() != null
	                            ? request.getDisplayOrder()
	                            : 0);

	            question.setCreatedAt(LocalDateTime.now());

	            question.setUpdatedAt(LocalDateTime.now());

	            questionsToSave.add(question);
	        }

	        List<InTrainingQuestion> savedQuestions =
	                inTrainingQuestionRepository.saveAll(questionsToSave);

	        List<InTrainingQuestionResponse> responseList =
	                savedQuestions.stream()
	                        .map(this::mapToResponse)
	                        .toList();

	        return new ResponseEntity(
	                savedQuestions.size()
	                        + " in-training question(s) created successfully",
	                HttpStatus.CREATED.value(),
	                responseList);

	    } catch (DataIntegrityViolationException e) {

	        e.printStackTrace();

	        return new ResponseEntity(
	                "One or more in-training questions already exist or violate database constraints",
	                HttpStatus.CONFLICT.value(),
	                null);

	    } catch (Exception e) {

	        e.printStackTrace();

	        return new ResponseEntity(
	                "Internal server error!",
	                HttpStatus.INTERNAL_SERVER_ERROR.value(),
	                null);
	    }
	}
}