package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.request.UserResponseRequest;
import com.doritech.tmsservice.response.UserResponseResponse;
import com.doritech.tmsservice.service.UserResponseService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TestAttempt;
import com.doritech.tmsservice.tms.entity.TestQuestion;
import com.doritech.tmsservice.tms.entity.UserResponse;
import com.doritech.tmsservice.tms.repository.TestAttemptRepository;
import com.doritech.tmsservice.tms.repository.TestQuestionRepository;
import com.doritech.tmsservice.tms.repository.UserResponseRepository;

@Service
public class UserResponseServiceImpl implements UserResponseService {

	private final UserResponseRepository userResponseRepository;
	private final TestAttemptRepository testAttemptRepository;
	private final TestQuestionRepository testQuestionRepository;

	public UserResponseServiceImpl(UserResponseRepository userResponseRepository,
			TestAttemptRepository testAttemptRepository, TestQuestionRepository testQuestionRepository) {

		this.userResponseRepository = userResponseRepository;
		this.testAttemptRepository = testAttemptRepository;
		this.testQuestionRepository = testQuestionRepository;
	}

	@Override
	@Transactional
	public ResponseEntity submitResponse(UserResponseRequest request) {

		try {

			if (request == null) {
				return new ResponseEntity("Request cannot be null", 400, null);
			}

			if (request.getTestAttemptId() == null) {
				return new ResponseEntity("Test attempt id is required", 400, null);
			}

			if (request.getTestQuestionId() == null) {
				return new ResponseEntity("Test question id is required", 400, null);
			}

			Optional<TestAttempt> attemptOptional = testAttemptRepository.findById(request.getTestAttemptId());

			if (attemptOptional.isEmpty()) {
				return new ResponseEntity("Test attempt not found", 404, null);
			}

			TestAttempt attempt = attemptOptional.get();

			Optional<TestQuestion> questionOptional = testQuestionRepository.findById(request.getTestQuestionId());

			if (questionOptional.isEmpty()) {
				return new ResponseEntity("Test question not found", 404, null);
			}

			TestQuestion question = questionOptional.get();

			Optional<UserResponse> existingResponse = userResponseRepository
					.findByTestAttempt_TestAttemptIdAndTestQuestion_TestQuestionId(request.getTestAttemptId(),
							request.getTestQuestionId());

			UserResponse userResponse;

			if (existingResponse.isPresent()) {

				userResponse = existingResponse.get();

			} else {

				userResponse = new UserResponse();

				userResponse.setTestAttempt(attempt);
				userResponse.setTestQuestion(question);
			}

			userResponse.setUserAnswer(request.getUserAnswer());

			userResponse.setTimeTakenSeconds(request.getTimeTakenSeconds());

			boolean correct = checkAnswer(question, request.getUserAnswer());

			userResponse.setIsCorrect(correct);

			if (userResponse.getResponseDate() == null) {
				userResponse.setResponseDate(LocalDateTime.now());
			}

			UserResponse saved = userResponseRepository.save(userResponse);

			return new ResponseEntity(
					existingResponse.isPresent() ? "Response updated successfully" : "Response submitted successfully",
					200, mapToResponse(saved));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while submitting response: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getResponseById(Long userResponseId) {

		try {

			Optional<UserResponse> optional = userResponseRepository.findById(userResponseId);

			if (optional.isEmpty()) {
				return new ResponseEntity("User response not found", 404, null);
			}

			return new ResponseEntity("User response fetched successfully", 200, mapToResponse(optional.get()));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching response: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getResponsesByTestAttemptId(Long testAttemptId) {

		try {

			List<UserResponse> responses = userResponseRepository.findByTestAttempt_TestAttemptId(testAttemptId);

			List<UserResponseResponse> result = responses.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Responses fetched successfully", 200, result);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching responses: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getResponseByAttemptAndQuestion(Long testAttemptId, Long testQuestionId) {

		try {

			Optional<UserResponse> optional = userResponseRepository
					.findByTestAttempt_TestAttemptIdAndTestQuestion_TestQuestionId(testAttemptId, testQuestionId);

			if (optional.isEmpty()) {
				return new ResponseEntity("Response not found", 404, null);
			}

			return new ResponseEntity("Response fetched successfully", 200, mapToResponse(optional.get()));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching response: " + e.getMessage(), 500, null);
		}
	}

	@Override
	@Transactional
	public ResponseEntity updateResponse(Long userResponseId, UserResponseRequest request) {

		try {

			Optional<UserResponse> optional = userResponseRepository.findById(userResponseId);

			if (optional.isEmpty()) {
				return new ResponseEntity("User response not found", 404, null);
			}

			UserResponse response = optional.get();

			response.setUserAnswer(request.getUserAnswer());

			response.setTimeTakenSeconds(request.getTimeTakenSeconds());

			boolean correct = checkAnswer(response.getTestQuestion(), request.getUserAnswer());

			response.setIsCorrect(correct);

			UserResponse saved = userResponseRepository.save(response);

			return new ResponseEntity("Response updated successfully", 200, mapToResponse(saved));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while updating response: " + e.getMessage(), 500, null);
		}
	}

	@Override
	@Transactional
	public ResponseEntity deleteResponse(Long userResponseId) {

		try {

			if (!userResponseRepository.existsById(userResponseId)) {

				return new ResponseEntity("User response not found", 404, null);
			}

			userResponseRepository.deleteById(userResponseId);

			return new ResponseEntity("Response deleted successfully", 200, null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while deleting response: " + e.getMessage(), 500, null);
		}
	}

	@Override
	@Transactional
	public ResponseEntity deleteResponsesByTestAttemptId(Long testAttemptId) {

		try {

			userResponseRepository.deleteByTestAttempt_TestAttemptId(testAttemptId);

			return new ResponseEntity("Responses deleted successfully", 200, null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while deleting responses: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getCorrectResponses(Long testAttemptId) {

		try {

			List<UserResponse> responses = userResponseRepository
					.findByTestAttempt_TestAttemptIdAndIsCorrect(testAttemptId, true);

			List<UserResponseResponse> result = responses.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Correct responses fetched successfully", 200, result);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching correct responses: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getResponseCount(Long testAttemptId) {

		try {

			long count = userResponseRepository.countByTestAttempt_TestAttemptId(testAttemptId);

			return new ResponseEntity("Response count fetched successfully", 200, count);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while counting responses: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getCorrectAnswerCount(Long testAttemptId) {

		try {

			long count = userResponseRepository.countByTestAttempt_TestAttemptIdAndIsCorrect(testAttemptId, true);

			return new ResponseEntity("Correct answer count fetched successfully", 200, count);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while counting correct answers: " + e.getMessage(), 500, null);
		}
	}

	private boolean checkAnswer(TestQuestion question, String userAnswer) {

		if (userAnswer == null || userAnswer.trim().isEmpty()) {
			return false;
		}

		/*
		 * Replace this with your actual TestQuestion correct-answer field.
		 *
		 * Example:
		 *
		 * return question.getCorrectAnswer() .equalsIgnoreCase(userAnswer.trim());
		 */

		return question.getCorrectAnswer().equalsIgnoreCase(userAnswer.trim());
	}

	private UserResponseResponse mapToResponse(UserResponse entity) {

		UserResponseResponse response = new UserResponseResponse();

		response.setUserResponseId(entity.getUserResponseId());

		response.setTestAttemptId(entity.getTestAttempt() != null ? entity.getTestAttempt().getTestAttemptId() : null);

		response.setTestQuestionId(
				entity.getTestQuestion() != null ? entity.getTestQuestion().getTestQuestionId() : null);

		response.setUserAnswer(entity.getUserAnswer());

		response.setIsCorrect(entity.getIsCorrect());

		response.setTimeTakenSeconds(entity.getTimeTakenSeconds());

		response.setResponseDate(entity.getResponseDate());

		return response;
	}
}