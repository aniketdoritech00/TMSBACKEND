package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.enums.QuestionType;
import com.doritech.tmsservice.request.VideoAssessmentQuestionRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.VideoAssessmentQuestionResponse;
import com.doritech.tmsservice.service.VideoAssessmentQuestionService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.Training;
import com.doritech.tmsservice.tms.entity.VideoAssessmentQuestion;
import com.doritech.tmsservice.tms.repository.TrainingRepository;
import com.doritech.tmsservice.tms.repository.VideoAssessmentQuestionRepository;

@Service
public class VideoAssessmentQuestionServiceImpl implements VideoAssessmentQuestionService {

	private final VideoAssessmentQuestionRepository videoAssessmentQuestionRepository;

	private final TrainingRepository trainingRepository;

	public VideoAssessmentQuestionServiceImpl(VideoAssessmentQuestionRepository videoAssessmentQuestionRepository,
			TrainingRepository trainingRepository) {
		this.videoAssessmentQuestionRepository = videoAssessmentQuestionRepository;
		this.trainingRepository = trainingRepository;
	}

	@Override
	@Transactional(value = "tmsTransactionManager")
	public ResponseEntity createVideoAssessmentQuestion(List<VideoAssessmentQuestionRequest> requests) {

		try {

			Long currentUserId = CurrentUser.getUserId();

			if (currentUserId == null || currentUserId <= 0) {
				return new ResponseEntity("Unable to identify current user", HttpStatus.UNAUTHORIZED.value(), null);
			}

			if (requests == null || requests.isEmpty()) {
				return new ResponseEntity("Question list cannot be empty", HttpStatus.BAD_REQUEST.value(), null);
			}

			List<VideoAssessmentQuestionResponse> responseList = new ArrayList<>();

			for (VideoAssessmentQuestionRequest request : requests) {

				if (request == null) {
					return new ResponseEntity("Question data cannot be null", HttpStatus.BAD_REQUEST.value(), null);
				}

				if (request.getTrainingId() == null || request.getTrainingId() <= 0) {

					return new ResponseEntity("Training ID is required", HttpStatus.BAD_REQUEST.value(), null);
				}

				if (request.getQuestionText() == null || request.getQuestionText().trim().isEmpty()) {

					return new ResponseEntity("Question text is required", HttpStatus.BAD_REQUEST.value(), null);
				}

				Training training = trainingRepository.findById(request.getTrainingId()).orElse(null);

				if (training == null) {

					return new ResponseEntity("Training not found for training ID: " + request.getTrainingId(),
							HttpStatus.NOT_FOUND.value(), null);
				}

				String questionText = request.getQuestionText().trim();

				boolean alreadyExists = videoAssessmentQuestionRepository
						.existsByTraining_TrainingIdAndQuestionTextIgnoreCase(request.getTrainingId(), questionText);

				if (alreadyExists) {

					return new ResponseEntity("Question already exists for this training: " + questionText,
							HttpStatus.CONFLICT.value(), null);
				}

				VideoAssessmentQuestion question = new VideoAssessmentQuestion();

				question.setTraining(training);
				question.setQuestionText(questionText);
				question.setCreatedAt(LocalDateTime.now());
				question.setCreateBy(currentUserId);

				if (request.getQuestionType() != null) {

					question.setQuestionType(request.getQuestionType());

				} else {

					question.setQuestionType(QuestionType.ORAL);
				}

				VideoAssessmentQuestion savedQuestion = videoAssessmentQuestionRepository.save(question);

				VideoAssessmentQuestionResponse response = convertToResponse(savedQuestion);

				responseList.add(response);
			}

			return new ResponseEntity("Video assessment questions created successfully", HttpStatus.CREATED.value(),
					responseList);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to create video assessment questions",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getVideoAssessmentQuestionById(Long videoAssessmentQuestionId) {

		try {

			if (videoAssessmentQuestionId == null || videoAssessmentQuestionId <= 0) {

				return new ResponseEntity("Video assessment question ID is required", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			VideoAssessmentQuestion question = videoAssessmentQuestionRepository.findById(videoAssessmentQuestionId)
					.orElse(null);

			if (question == null) {

				return new ResponseEntity("Video assessment question not found", HttpStatus.NOT_FOUND.value(), null);
			}

			VideoAssessmentQuestionResponse response = convertToResponse(question);

			return new ResponseEntity("Video assessment question fetched successfully", HttpStatus.OK.value(),
					response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch video assessment question",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllVideoAssessmentQuestions(int page, int size, String sortBy, String sortDir) {

		try {

			if (page < 0) {
				return new ResponseEntity("Page number cannot be negative", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (size <= 0) {
				return new ResponseEntity("Page size must be greater than zero", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "videoAssessmentQuestionId";
			}

			if (sortDir == null || sortDir.trim().isEmpty()) {
				sortDir = "desc";
			}

			Sort sort;

			if (sortDir.equalsIgnoreCase("asc")) {
				sort = Sort.by(sortBy).ascending();
			} else if (sortDir.equalsIgnoreCase("desc")) {
				sort = Sort.by(sortBy).descending();
			} else {
				return new ResponseEntity("Sort direction must be asc or desc", HttpStatus.BAD_REQUEST.value(), null);
			}

			Pageable pageable = PageRequest.of(page, size, sort);

			Page<VideoAssessmentQuestion> questionPage = videoAssessmentQuestionRepository.findAll(pageable);

			List<VideoAssessmentQuestionResponse> content = questionPage.getContent().stream()
					.map(this::convertToResponse).collect(Collectors.toList());

			PageResponse<VideoAssessmentQuestionResponse> pageResponse = new PageResponse<>(content,
					questionPage.getNumber(), questionPage.getSize(), questionPage.getTotalElements(),
					questionPage.getTotalPages(), questionPage.isLast());

			return new ResponseEntity("Video assessment questions fetched successfully", HttpStatus.OK.value(),
					pageResponse);

		} catch (PropertyReferenceException e) {

			return new ResponseEntity("Invalid sort field: " + sortBy, HttpStatus.BAD_REQUEST.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch video assessment questions",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getVideoAssessmentQuestionsByTrainingId(Long trainingId, int page, int size, String sortBy,
			String sortDir) {

		try {

			if (trainingId == null || trainingId <= 0) {

				return new ResponseEntity("Training ID is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (page < 0) {

				return new ResponseEntity("Page number cannot be negative", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (size <= 0) {

				return new ResponseEntity("Page size must be greater than zero", HttpStatus.BAD_REQUEST.value(), null);
			}

			Training training = trainingRepository.findById(trainingId).orElse(null);

			if (training == null) {

				return new ResponseEntity("Training not found", HttpStatus.NOT_FOUND.value(), null);
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "videoAssessmentQuestionId";
			}

			if (sortDir == null || sortDir.trim().isEmpty()) {
				sortDir = "desc";
			}

			Sort sort;

			if (sortDir.equalsIgnoreCase("asc")) {
				sort = Sort.by(sortBy).ascending();
			} else if (sortDir.equalsIgnoreCase("desc")) {
				sort = Sort.by(sortBy).descending();
			} else {

				return new ResponseEntity("Sort direction must be asc or desc", HttpStatus.BAD_REQUEST.value(), null);
			}

			Pageable pageable = PageRequest.of(page, size, sort);

			Page<VideoAssessmentQuestion> questionPage = videoAssessmentQuestionRepository
					.findByTraining_TrainingId(trainingId, pageable);

			List<VideoAssessmentQuestionResponse> content = questionPage.getContent().stream()
					.map(this::convertToResponse).collect(Collectors.toList());

			PageResponse<VideoAssessmentQuestionResponse> pageResponse = new PageResponse<>(content,
					questionPage.getNumber(), questionPage.getSize(), questionPage.getTotalElements(),
					questionPage.getTotalPages(), questionPage.isLast());

			return new ResponseEntity("Video assessment questions fetched successfully", HttpStatus.OK.value(),
					pageResponse);

		} catch (PropertyReferenceException e) {

			return new ResponseEntity("Invalid sort field: " + sortBy, HttpStatus.BAD_REQUEST.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch video assessment questions",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager")
	public ResponseEntity updateVideoAssessmentQuestion(Long videoAssessmentQuestionId,
			VideoAssessmentQuestionRequest request) {

		try {

			if (videoAssessmentQuestionId == null || videoAssessmentQuestionId <= 0) {

				return new ResponseEntity("Video assessment question ID is required", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			if (request == null) {

				return new ResponseEntity("Request cannot be null", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTrainingId() == null || request.getTrainingId() <= 0) {

				return new ResponseEntity("Training ID is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getQuestionText() == null || request.getQuestionText().trim().isEmpty()) {

				return new ResponseEntity("Question text is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			VideoAssessmentQuestion question = videoAssessmentQuestionRepository.findById(videoAssessmentQuestionId)
					.orElse(null);

			if (question == null) {

				return new ResponseEntity("Video assessment question not found", HttpStatus.NOT_FOUND.value(), null);
			}

			Training training = trainingRepository.findById(request.getTrainingId()).orElse(null);

			if (training == null) {

				return new ResponseEntity("Training not found", HttpStatus.NOT_FOUND.value(), null);
			}

			String questionText = request.getQuestionText().trim();

			if (videoAssessmentQuestionRepository
					.existsByTraining_TrainingIdAndQuestionTextIgnoreCaseAndVideoAssessmentQuestionIdNot(
							request.getTrainingId(), questionText, videoAssessmentQuestionId)) {

				return new ResponseEntity("Question already exists for this training", HttpStatus.CONFLICT.value(),
						null);
			}

			question.setTraining(training);
			question.setQuestionText(questionText);

			if (request.getQuestionType() != null) {
				question.setQuestionType(request.getQuestionType());
			} else {
				question.setQuestionType(QuestionType.ORAL);
			}

			VideoAssessmentQuestion updatedQuestion = videoAssessmentQuestionRepository.save(question);

			VideoAssessmentQuestionResponse response = convertToResponse(updatedQuestion);

			return new ResponseEntity("Video assessment question updated successfully", HttpStatus.OK.value(),
					response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to update video assessment question",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager")
	public ResponseEntity deleteVideoAssessmentQuestion(Long videoAssessmentQuestionId) {

		try {

			if (videoAssessmentQuestionId == null || videoAssessmentQuestionId <= 0) {

				return new ResponseEntity("Video assessment question ID is required", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			VideoAssessmentQuestion question = videoAssessmentQuestionRepository.findById(videoAssessmentQuestionId)
					.orElse(null);

			if (question == null) {

				return new ResponseEntity("Video assessment question not found", HttpStatus.NOT_FOUND.value(), null);
			}

			videoAssessmentQuestionRepository.delete(question);

			return new ResponseEntity("Video assessment question deleted successfully", HttpStatus.OK.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to delete video assessment question",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private VideoAssessmentQuestionResponse convertToResponse(VideoAssessmentQuestion question) {

		VideoAssessmentQuestionResponse response = new VideoAssessmentQuestionResponse();

		response.setVideoAssessmentQuestionId(question.getVideoAssessmentQuestionId());

		if (question.getTraining() != null) {

			response.setTrainingId(question.getTraining().getTrainingId());

			response.setTrainingCode(question.getTraining().getTrainingCode());

			response.setTrainingName(question.getTraining().getTrainingName());
		}

		response.setQuestionText(question.getQuestionText());
		response.setQuestionType(question.getQuestionType());
		response.setCreatedAt(question.getCreatedAt());

		return response;
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllVideoAssessmentQuestionsWithoutPaginatioan() {
		try {

			List<VideoAssessmentQuestion> questions = videoAssessmentQuestionRepository.findAll();

			if (questions == null || questions.isEmpty()) {

				return new ResponseEntity("No video assessment questions found", HttpStatus.NOT_FOUND.value(), null);
			}

			List<VideoAssessmentQuestionResponse> responseList = questions.stream().map(this::convertToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Video assessment questions fetched successfully", HttpStatus.OK.value(),
					responseList);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch video assessment questions",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}
}