package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.request.InTrainingUserResponseRequest;
import com.doritech.tmsservice.response.InTrainingUserResponseResponse;
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

	private final InTrainingUserResponseRepository responseRepository;
	private final InTrainingQuestionRepository questionRepository;
	private final TrainingAssignmentRepository trainingAssignmentRepository;

	public InTrainingUserResponseServiceImpl(InTrainingUserResponseRepository responseRepository,
			InTrainingQuestionRepository questionRepository,
			TrainingAssignmentRepository trainingAssignmentRepository) {

		this.responseRepository = responseRepository;
		this.questionRepository = questionRepository;
		this.trainingAssignmentRepository = trainingAssignmentRepository;
	}

	@Override
	@Transactional
	public ResponseEntity submitResponse(InTrainingUserResponseRequest request) {

		try {

			if (request == null) {
				return new ResponseEntity("Request cannot be null", 400, null);
			}

			if (request.getQuestionId() == null) {
				return new ResponseEntity("Question id is required", 400, null);
			}

			Optional<InTrainingQuestion> questionOptional = questionRepository.findById(request.getQuestionId());

			if (questionOptional.isEmpty()) {
				return new ResponseEntity("Training question not found", 404, null);
			}

			InTrainingQuestion question = questionOptional.get();

			TrainingAssignment trainingAssignment = null;

			if (request.getTrainingAssignmentId() != null) {

				Optional<TrainingAssignment> assignmentOptional = trainingAssignmentRepository
						.findById(request.getTrainingAssignmentId());

				if (assignmentOptional.isEmpty()) {
					return new ResponseEntity("Training assignment not found", 404, null);
				}

				trainingAssignment = assignmentOptional.get();
			}

			Optional<InTrainingUserResponse> existingResponse = responseRepository
					.findByQuestion_QuestionIdAndTrainingAssignment_TrainingAssignmentId(request.getQuestionId(),
							request.getTrainingAssignmentId());

			InTrainingUserResponse response;

			if (existingResponse.isPresent()) {

				response = existingResponse.get();

			} else {

				response = new InTrainingUserResponse();

				response.setQuestion(question);
				response.setTrainingAssignment(trainingAssignment);
			}

			response.setUserAnswer(request.getUserAnswer());

			response.setIsSkipped(request.getIsSkipped() != null ? request.getIsSkipped() : false);

			response.setTimeTakenSeconds(request.getTimeTakenSeconds());
			
			if (Boolean.TRUE.equals(response.getIsSkipped())) {

				response.setIsCorrect(false);

			} else {

				boolean correct = checkAnswer(question, request.getUserAnswer());

				response.setIsCorrect(correct);
			}

			if (response.getRespondedAt() == null) {
				response.setRespondedAt(LocalDateTime.now());
			}

			InTrainingUserResponse saved = responseRepository.save(response);

			return new ResponseEntity(existingResponse.isPresent() ? "Training response updated successfully"
					: "Training response submitted successfully", 200, mapToResponse(saved));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while submitting training response: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getResponseById(Long inTrainingUserResponseId) {

		try {

			Optional<InTrainingUserResponse> optional = responseRepository.findById(inTrainingUserResponseId);

			if (optional.isEmpty()) {
				return new ResponseEntity("Training response not found", 404, null);
			}

			return new ResponseEntity("Training response fetched successfully", 200, mapToResponse(optional.get()));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching training response: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getResponsesByTrainingAssignment(Long trainingAssignmentId) {

		try {

			List<InTrainingUserResponse> responses = responseRepository
					.findByTrainingAssignment_TrainingAssignmentId(trainingAssignmentId);

			List<InTrainingUserResponseResponse> result = responses.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Training responses fetched successfully", 200, result);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching training responses: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getResponsesByQuestion(Long questionId) {

		try {

			List<InTrainingUserResponse> responses = responseRepository.findByQuestion_QuestionId(questionId);

			List<InTrainingUserResponseResponse> result = responses.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Question responses fetched successfully", 200, result);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching question responses: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getResponseByQuestionAndAssignment(Long questionId, Long trainingAssignmentId) {

		try {

			Optional<InTrainingUserResponse> optional = responseRepository
					.findByQuestion_QuestionIdAndTrainingAssignment_TrainingAssignmentId(questionId,
							trainingAssignmentId);

			if (optional.isEmpty()) {
				return new ResponseEntity("Training response not found", 404, null);
			}

			return new ResponseEntity("Training response fetched successfully", 200, mapToResponse(optional.get()));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching training response: " + e.getMessage(), 500, null);
		}
	}

	@Override
	@Transactional
	public ResponseEntity updateResponse(Long inTrainingUserResponseId, InTrainingUserResponseRequest request) {

		try {

			Optional<InTrainingUserResponse> optional = responseRepository.findById(inTrainingUserResponseId);

			if (optional.isEmpty()) {
				return new ResponseEntity("Training response not found", 404, null);
			}

			InTrainingUserResponse response = optional.get();

			response.setUserAnswer(request.getUserAnswer());

			response.setIsSkipped(request.getIsSkipped() != null ? request.getIsSkipped() : false);

			response.setTimeTakenSeconds(request.getTimeTakenSeconds());

			if (Boolean.TRUE.equals(response.getIsSkipped())) {

				response.setIsCorrect(false);

			} else {

				response.setIsCorrect(checkAnswer(response.getQuestion(), request.getUserAnswer()));
			}

			InTrainingUserResponse saved = responseRepository.save(response);

			return new ResponseEntity("Training response updated successfully", 200, mapToResponse(saved));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while updating training response: " + e.getMessage(), 500, null);
		}
	}

	@Override
	@Transactional
	public ResponseEntity deleteResponse(Long inTrainingUserResponseId) {

		try {

			if (!responseRepository.existsById(inTrainingUserResponseId)) {

				return new ResponseEntity("Training response not found", 404, null);
			}

			responseRepository.deleteById(inTrainingUserResponseId);

			return new ResponseEntity("Training response deleted successfully", 200, null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while deleting training response: " + e.getMessage(), 500, null);
		}
	}

	@Override
	@Transactional
	public ResponseEntity deleteResponsesByTrainingAssignment(Long trainingAssignmentId) {

		try {

			responseRepository.deleteByTrainingAssignment_TrainingAssignmentId(trainingAssignmentId);

			return new ResponseEntity("Training responses deleted successfully", 200, null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while deleting training responses: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getCorrectAnswerCount(Long trainingAssignmentId) {

		try {

			long count = responseRepository
					.countByTrainingAssignment_TrainingAssignmentIdAndIsCorrect(trainingAssignmentId, true);

			return new ResponseEntity("Correct answer count fetched successfully", 200, count);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while counting correct answers: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getSkippedAnswerCount(Long trainingAssignmentId) {

		try {

			long count = responseRepository
					.countByTrainingAssignment_TrainingAssignmentIdAndIsSkipped(trainingAssignmentId, true);

			return new ResponseEntity("Skipped answer count fetched successfully", 200, count);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while counting skipped answers: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getResponseCount(Long trainingAssignmentId) {

		try {

			long count = responseRepository.countByTrainingAssignment_TrainingAssignmentId(trainingAssignmentId);

			return new ResponseEntity("Response count fetched successfully", 200, count);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while counting responses: " + e.getMessage(), 500, null);
		}
	}

	private boolean checkAnswer(InTrainingQuestion question, String userAnswer) {

		if (userAnswer == null || userAnswer.trim().isEmpty()) {

			return false;
		}

		/*
		 * Replace this according to your InTrainingQuestion entity.
		 *
		 * Example:
		 *
		 * return question.getCorrectAnswer() .equalsIgnoreCase(userAnswer.trim());
		 */

		return question.getCorrectAnswer().equalsIgnoreCase(userAnswer.trim());
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

		response.setIsSkipped(entity.getIsSkipped());

		response.setTimeTakenSeconds(entity.getTimeTakenSeconds());

		response.setRespondedAt(entity.getRespondedAt());

		return response;
	}
}