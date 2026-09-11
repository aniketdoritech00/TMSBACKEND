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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.request.InTrainingUserResponseRequest;
import com.doritech.tmsservice.response.InTrainingUserResponseResponse;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.service.InTrainingUserResponseService;
import com.doritech.tmsservice.tms.entity.InTrainingQuestion;
import com.doritech.tmsservice.tms.entity.InTrainingUserResponse;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TrainingAssignment;
import com.doritech.tmsservice.tms.repository.InTrainingQuestionRepository;
import com.doritech.tmsservice.tms.repository.InTrainingUserResponseRepository;
import com.doritech.tmsservice.tms.repository.TrainingAssignmentRepository;

@Service
public class InTrainingUserResponseServiceImpl implements InTrainingUserResponseService {

	private final InTrainingUserResponseRepository inTrainingUserResponseRepository;
	private final InTrainingQuestionRepository inTrainingQuestionRepository;
	private final TrainingAssignmentRepository trainingAssignmentRepository;

	public InTrainingUserResponseServiceImpl(InTrainingUserResponseRepository inTrainingUserResponseRepository,
			InTrainingQuestionRepository inTrainingQuestionRepository,
			TrainingAssignmentRepository trainingAssignmentRepository) {

		this.inTrainingUserResponseRepository = inTrainingUserResponseRepository;

		this.inTrainingQuestionRepository = inTrainingQuestionRepository;

		this.trainingAssignmentRepository = trainingAssignmentRepository;
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity submitResponse(InTrainingUserResponseRequest request) {

		try {

			if (request == null) {

				return new ResponseEntity("Request cannot be null", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getQuestionId() == null || request.getQuestionId() <= 0) {

				return new ResponseEntity("Valid question id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTrainingAssignmentId() == null || request.getTrainingAssignmentId() <= 0) {

				return new ResponseEntity("Valid training assignment id is required", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			Optional<InTrainingQuestion> questionOptional = inTrainingQuestionRepository
					.findById(request.getQuestionId());

			if (questionOptional.isEmpty()) {

				return new ResponseEntity("In-training question not found", HttpStatus.NOT_FOUND.value(), null);
			}

			Optional<TrainingAssignment> assignmentOptional = trainingAssignmentRepository
					.findById(request.getTrainingAssignmentId());

			if (assignmentOptional.isEmpty()) {

				return new ResponseEntity("Training assignment not found", HttpStatus.NOT_FOUND.value(), null);
			}

			InTrainingQuestion question = questionOptional.get();

			TrainingAssignment assignment = assignmentOptional.get();

			Optional<InTrainingUserResponse> existingResponse = inTrainingUserResponseRepository
					.findByQuestion_QuestionIdAndTrainingAssignment_TrainingAssignmentId(request.getQuestionId(),
							request.getTrainingAssignmentId());

			InTrainingUserResponse userResponse;

			if (existingResponse.isPresent()) {

				userResponse = existingResponse.get();

			} else {

				userResponse = new InTrainingUserResponse();

				userResponse.setQuestion(question);

				userResponse.setTrainingAssignment(assignment);

				userResponse.setRespondedAt(LocalDateTime.now());
			}

			userResponse.setUserAnswer(request.getUserAnswer());

			userResponse.setTimeTakenSeconds(request.getTimeTakenSeconds());

			boolean correct = checkAnswer(question, request.getUserAnswer());

			userResponse.setIsCorrect(correct);

			if (userResponse.getRespondedAt() == null) {

				userResponse.setRespondedAt(LocalDateTime.now());
			}

			InTrainingUserResponse saved = inTrainingUserResponseRepository.save(userResponse);

			String message = existingResponse.isPresent() ? "Response updated successfully"
					: "Response submitted successfully";

			return new ResponseEntity(message, HttpStatus.OK.value(), mapToResponse(saved));

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("Response already exists or violates database constraints",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while submitting response: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getResponseById(Long inTrainingUserResponseId) {

		try {

			if (inTrainingUserResponseId == null || inTrainingUserResponseId <= 0) {

				return new ResponseEntity("Invalid in-training user response id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<InTrainingUserResponse> optional = inTrainingUserResponseRepository
					.findById(inTrainingUserResponseId);

			if (optional.isEmpty()) {

				return new ResponseEntity("In-training user response not found", HttpStatus.NOT_FOUND.value(), null);
			}

			return new ResponseEntity("In-training user response fetched successfully", HttpStatus.OK.value(),
					mapToResponse(optional.get()));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching response: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllResponses(int page, int size, String sortBy, String sortDir) {

		try {

			if (page < 0) {
				page = 0;
			}

			if (size <= 0) {
				size = 10;
			}

			if (size > 100) {
				size = 100;
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {

				sortBy = "inTrainingUserResponseId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<InTrainingUserResponse> responsePage = inTrainingUserResponseRepository.findAll(pageable);

			List<InTrainingUserResponseResponse> content = responsePage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<InTrainingUserResponseResponse> pageResponse = new PageResponse<>(content,
					responsePage.getNumber(), responsePage.getSize(), responsePage.getTotalElements(),
					responsePage.getTotalPages(), responsePage.isLast());

			return new ResponseEntity("In-training user responses fetched successfully", HttpStatus.OK.value(),
					pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching responses: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getResponsesByTrainingAssignment(Long trainingAssignmentId, int page, int size, String sortBy,
			String sortDir) {

		try {

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {

				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TrainingAssignment> assignmentOptional = trainingAssignmentRepository
					.findById(trainingAssignmentId);

			if (assignmentOptional.isEmpty()) {

				return new ResponseEntity("Training assignment not found", HttpStatus.NOT_FOUND.value(), null);
			}

			if (page < 0) {
				page = 0;
			}

			if (size <= 0) {
				size = 10;
			}

			if (size > 100) {
				size = 100;
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {

				sortBy = "inTrainingUserResponseId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<InTrainingUserResponse> responsePage = inTrainingUserResponseRepository
					.findByTrainingAssignment_TrainingAssignmentId(trainingAssignmentId, pageable);

			List<InTrainingUserResponseResponse> content = responsePage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<InTrainingUserResponseResponse> pageResponse = new PageResponse<>(content,
					responsePage.getNumber(), responsePage.getSize(), responsePage.getTotalElements(),
					responsePage.getTotalPages(), responsePage.isLast());

			return new ResponseEntity("Responses fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching responses by training assignment: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getResponsesByQuestion(Long questionId, int page, int size, String sortBy, String sortDir) {

		try {

			if (questionId == null || questionId <= 0) {

				return new ResponseEntity("Invalid question id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<InTrainingQuestion> questionOptional = inTrainingQuestionRepository.findById(questionId);

			if (questionOptional.isEmpty()) {

				return new ResponseEntity("In-training question not found", HttpStatus.NOT_FOUND.value(), null);
			}

			if (page < 0) {
				page = 0;
			}

			if (size <= 0) {
				size = 10;
			}

			if (size > 100) {
				size = 100;
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {

				sortBy = "inTrainingUserResponseId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<InTrainingUserResponse> responsePage = inTrainingUserResponseRepository
					.findByQuestion_QuestionId(questionId, pageable);

			List<InTrainingUserResponseResponse> content = responsePage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<InTrainingUserResponseResponse> pageResponse = new PageResponse<>(content,
					responsePage.getNumber(), responsePage.getSize(), responsePage.getTotalElements(),
					responsePage.getTotalPages(), responsePage.isLast());

			return new ResponseEntity("Responses fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching responses by question: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getResponseByQuestionAndAssignment(Long questionId, Long trainingAssignmentId) {

		try {

			if (questionId == null || questionId <= 0) {

				return new ResponseEntity("Invalid question id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {

				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<InTrainingUserResponse> optional = inTrainingUserResponseRepository
					.findByQuestion_QuestionIdAndTrainingAssignment_TrainingAssignmentId(questionId,
							trainingAssignmentId);

			if (optional.isEmpty()) {

				return new ResponseEntity("Response not found", HttpStatus.NOT_FOUND.value(), null);
			}

			return new ResponseEntity("Response fetched successfully", HttpStatus.OK.value(),
					mapToResponse(optional.get()));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching response: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity updateResponse(Long inTrainingUserResponseId, InTrainingUserResponseRequest request) {

		try {

			if (inTrainingUserResponseId == null || inTrainingUserResponseId <= 0) {

				return new ResponseEntity("Invalid in-training user response id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request == null) {

				return new ResponseEntity("Request cannot be null", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<InTrainingUserResponse> optional = inTrainingUserResponseRepository
					.findById(inTrainingUserResponseId);

			if (optional.isEmpty()) {

				return new ResponseEntity("In-training user response not found", HttpStatus.NOT_FOUND.value(), null);
			}

			InTrainingUserResponse response = optional.get();

			if (request.getUserAnswer() != null) {

				response.setUserAnswer(request.getUserAnswer());

				boolean correct = checkAnswer(response.getQuestion(), request.getUserAnswer());

				response.setIsCorrect(correct);
			}

			if (request.getTimeTakenSeconds() != null) {

				response.setTimeTakenSeconds(request.getTimeTakenSeconds());
			}

			InTrainingUserResponse saved = inTrainingUserResponseRepository.save(response);

			return new ResponseEntity("Response updated successfully", HttpStatus.OK.value(), mapToResponse(saved));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while updating response: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity deleteResponse(Long inTrainingUserResponseId) {

		try {

			if (inTrainingUserResponseId == null || inTrainingUserResponseId <= 0) {

				return new ResponseEntity("Invalid in-training user response id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<InTrainingUserResponse> optional = inTrainingUserResponseRepository
					.findById(inTrainingUserResponseId);

			if (optional.isEmpty()) {

				return new ResponseEntity("In-training user response not found", HttpStatus.NOT_FOUND.value(), null);
			}

			inTrainingUserResponseRepository.delete(optional.get());

			return new ResponseEntity("Response deleted successfully", HttpStatus.OK.value(), null);

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("Response cannot be deleted because it is being used",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while deleting response: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity deleteResponsesByTrainingAssignment(Long trainingAssignmentId) {

		try {

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {

				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			long count = inTrainingUserResponseRepository
					.countByTrainingAssignment_TrainingAssignmentId(trainingAssignmentId);

			if (count == 0) {

				return new ResponseEntity("No responses found for this training assignment",
						HttpStatus.NOT_FOUND.value(), null);
			}

			inTrainingUserResponseRepository.deleteByTrainingAssignment_TrainingAssignmentId(trainingAssignmentId);

			return new ResponseEntity("Responses deleted successfully", HttpStatus.OK.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while deleting responses: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getCorrectAnswerCount(Long trainingAssignmentId) {

		try {

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {

				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			long count = inTrainingUserResponseRepository
					.countByTrainingAssignment_TrainingAssignmentIdAndIsCorrect(trainingAssignmentId, true);

			return new ResponseEntity("Correct answer count fetched successfully", HttpStatus.OK.value(), count);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while counting correct answers: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getSkippedAnswerCount(Long trainingAssignmentId) {

		try {

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {

				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			/*
			 * A skipped answer is treated as a response where userAnswer is null or blank.
			 *
			 * If your database has a dedicated skipped flag, replace this logic with that
			 * field.
			 */

			long totalResponses = inTrainingUserResponseRepository
					.countByTrainingAssignment_TrainingAssignmentId(trainingAssignmentId);

			long answeredResponses = inTrainingUserResponseRepository
					.countByTrainingAssignment_TrainingAssignmentIdAndUserAnswerIsNotNullAndUserAnswerNot(
							trainingAssignmentId, "");

			long skippedCount = totalResponses - answeredResponses;

			if (skippedCount < 0) {
				skippedCount = 0;
			}

			return new ResponseEntity("Skipped answer count fetched successfully", HttpStatus.OK.value(), skippedCount);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while counting skipped answers: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getResponseCount(Long trainingAssignmentId) {

		try {

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {

				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			long count = inTrainingUserResponseRepository
					.countByTrainingAssignment_TrainingAssignmentId(trainingAssignmentId);

			return new ResponseEntity("Response count fetched successfully", HttpStatus.OK.value(), count);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while counting responses: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private boolean checkAnswer(InTrainingQuestion question, String userAnswer) {

		if (question == null) {
			return false;
		}

		if (userAnswer == null || userAnswer.trim().isEmpty()) {

			return false;
		}

		if (question.getCorrectAnswer() == null || question.getCorrectAnswer().trim().isEmpty()) {

			return false;
		}

		return question.getCorrectAnswer().trim().equalsIgnoreCase(userAnswer.trim());
	}

	private InTrainingUserResponseResponse mapToResponse(InTrainingUserResponse entity) {

		InTrainingUserResponseResponse response = new InTrainingUserResponseResponse();

		response.setInTrainingUserResponseId(entity.getInTrainingUserResponseId());

		response.setQuestionId(entity.getQuestion() != null ? entity.getQuestion().getQuestionId() : null);

		response.setTrainingAssignmentId(
				entity.getTrainingAssignment() != null ? entity.getTrainingAssignment().getTrainingAssignmentId()
						: null);

		response.setUserAnswer(entity.getUserAnswer());

		response.setIsCorrect(entity.getIsCorrect());

		response.setTimeTakenSeconds(entity.getTimeTakenSeconds());

		response.setRespondedAt(entity.getRespondedAt());

		return response;
	}
}