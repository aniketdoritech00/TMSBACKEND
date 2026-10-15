package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.request.UserResponseRequest;
import com.doritech.tmsservice.response.PageResponse;
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
	@Transactional("tmsTransactionManager")
	public ResponseEntity submitResponse(UserResponseRequest request) {

		try {

			if (request == null) {

				return new ResponseEntity("Request cannot be null", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTestAttemptId() == null || request.getTestAttemptId() <= 0) {

				return new ResponseEntity("Valid test attempt id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTestQuestionId() == null || request.getTestQuestionId() <= 0) {

				return new ResponseEntity("Valid test question id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestAttempt> attemptOptional = testAttemptRepository.findById(request.getTestAttemptId());

			if (attemptOptional.isEmpty()) {

				return new ResponseEntity("Test attempt not found", HttpStatus.NOT_FOUND.value(), null);
			}

			TestAttempt attempt = attemptOptional.get();

			Optional<TestQuestion> questionOptional = testQuestionRepository.findById(request.getTestQuestionId());

			if (questionOptional.isEmpty()) {

				return new ResponseEntity("Test question not found", HttpStatus.NOT_FOUND.value(), null);
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

				userResponse.setResponseDate(LocalDateTime.now());
			}

			userResponse.setUserAnswer(request.getUserAnswer());

			userResponse.setTimeTakenSeconds(request.getTimeTakenSeconds());

			boolean correct = checkAnswer(question, request.getUserAnswer());

			userResponse.setIsCorrect(correct);

			if (userResponse.getResponseDate() == null) {

				userResponse.setResponseDate(LocalDateTime.now());
			}

			UserResponse saved = userResponseRepository.save(userResponse);

			String message = existingResponse.isPresent() ? "Response updated successfully"
					: "Response submitted successfully";

			return new ResponseEntity(message, HttpStatus.OK.value(), mapToResponse(saved));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while submitting response: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getResponseById(Long userResponseId) {

		try {

			if (userResponseId == null || userResponseId <= 0) {

				return new ResponseEntity("Invalid user response id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<UserResponse> optional = userResponseRepository.findById(userResponseId);

			if (optional.isEmpty()) {

				return new ResponseEntity("User response not found", HttpStatus.NOT_FOUND.value(), null);
			}

			return new ResponseEntity("User response fetched successfully", HttpStatus.OK.value(),
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

				sortBy = "userResponseId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<UserResponse> responsePage = userResponseRepository.findAll(pageable);

			List<UserResponseResponse> response = responsePage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<UserResponseResponse> pageResponse = new PageResponse<>(response, responsePage.getNumber(),
					responsePage.getSize(), responsePage.getTotalElements(), responsePage.getTotalPages(),
					responsePage.isLast());

			return new ResponseEntity("User responses fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching responses: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getResponsesByTestAttemptId(Long testAttemptId, int page, int size, String sortBy,
			String sortDir) {

		try {

			if (testAttemptId == null || testAttemptId <= 0) {

				return new ResponseEntity("Invalid test attempt id", HttpStatus.BAD_REQUEST.value(), null);
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

				sortBy = "userResponseId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<UserResponse> responsePage = userResponseRepository.findByTestAttempt_TestAttemptId(testAttemptId,
					pageable);

			List<UserResponseResponse> response = responsePage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<UserResponseResponse> pageResponse = new PageResponse<>(response, responsePage.getNumber(),
					responsePage.getSize(), responsePage.getTotalElements(), responsePage.getTotalPages(),
					responsePage.isLast());

			return new ResponseEntity("Responses fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching responses: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getResponseByAttemptAndQuestion(Long testAttemptId, Long testQuestionId) {

		try {

			if (testAttemptId == null || testAttemptId <= 0) {

				return new ResponseEntity("Invalid test attempt id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (testQuestionId == null || testQuestionId <= 0) {

				return new ResponseEntity("Invalid test question id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<UserResponse> optional = userResponseRepository
					.findByTestAttempt_TestAttemptIdAndTestQuestion_TestQuestionId(testAttemptId, testQuestionId);

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
	public ResponseEntity updateResponse(Long userResponseId, UserResponseRequest request) {

		try {

			if (userResponseId == null || userResponseId <= 0) {

				return new ResponseEntity("Invalid user response id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request == null) {

				return new ResponseEntity("Request cannot be null", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<UserResponse> optional = userResponseRepository.findById(userResponseId);

			if (optional.isEmpty()) {

				return new ResponseEntity("User response not found", HttpStatus.NOT_FOUND.value(), null);
			}

			UserResponse response = optional.get();

			response.setUserAnswer(request.getUserAnswer());

			response.setTimeTakenSeconds(request.getTimeTakenSeconds());

			boolean correct = checkAnswer(response.getTestQuestion(), request.getUserAnswer());

			response.setIsCorrect(correct);

			UserResponse saved = userResponseRepository.save(response);

			return new ResponseEntity("Response updated successfully", HttpStatus.OK.value(), mapToResponse(saved));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while updating response: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity deleteResponse(Long userResponseId) {

		try {

			if (userResponseId == null || userResponseId <= 0) {

				return new ResponseEntity("Invalid user response id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<UserResponse> optional = userResponseRepository.findById(userResponseId);

			if (optional.isEmpty()) {

				return new ResponseEntity("User response not found", HttpStatus.NOT_FOUND.value(), null);
			}

			userResponseRepository.deleteById(userResponseId);

			return new ResponseEntity("Response deleted successfully", HttpStatus.OK.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while deleting response: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity deleteResponsesByTestAttemptId(Long testAttemptId) {

		try {

			if (testAttemptId == null || testAttemptId <= 0) {

				return new ResponseEntity("Invalid test attempt id", HttpStatus.BAD_REQUEST.value(), null);
			}

			long count = userResponseRepository.countByTestAttempt_TestAttemptId(testAttemptId);

			if (count == 0) {

				return new ResponseEntity("No responses found for this test attempt", HttpStatus.NOT_FOUND.value(),
						null);
			}

			userResponseRepository.deleteByTestAttempt_TestAttemptId(testAttemptId);

			return new ResponseEntity("Responses deleted successfully", HttpStatus.OK.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while deleting responses: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getCorrectResponses(Long testAttemptId, int page, int size, String sortBy, String sortDir) {

		try {

			if (testAttemptId == null || testAttemptId <= 0) {

				return new ResponseEntity("Invalid test attempt id", HttpStatus.BAD_REQUEST.value(), null);
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

				sortBy = "userResponseId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<UserResponse> responsePage = userResponseRepository
					.findByTestAttempt_TestAttemptIdAndIsCorrect(testAttemptId, true, pageable);

			List<UserResponseResponse> response = responsePage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<UserResponseResponse> pageResponse = new PageResponse<>(response, responsePage.getNumber(),
					responsePage.getSize(), responsePage.getTotalElements(), responsePage.getTotalPages(),
					responsePage.isLast());

			return new ResponseEntity("Correct responses fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching correct responses: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getResponseCount(Long testAttemptId) {

		try {

			if (testAttemptId == null || testAttemptId <= 0) {

				return new ResponseEntity("Invalid test attempt id", HttpStatus.BAD_REQUEST.value(), null);
			}

			long count = userResponseRepository.countByTestAttempt_TestAttemptId(testAttemptId);

			return new ResponseEntity("Response count fetched successfully", HttpStatus.OK.value(), count);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while counting responses: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getCorrectAnswerCount(Long testAttemptId) {

		try {

			if (testAttemptId == null || testAttemptId <= 0) {

				return new ResponseEntity("Invalid test attempt id", HttpStatus.BAD_REQUEST.value(), null);
			}

			long count = userResponseRepository.countByTestAttempt_TestAttemptIdAndIsCorrect(testAttemptId, true);

			return new ResponseEntity("Correct answer count fetched successfully", HttpStatus.OK.value(), count);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while counting correct answers: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private boolean checkAnswer(TestQuestion question, String userAnswer) {

		if (question == null) {
			return false;
		}

		if (userAnswer == null || userAnswer.trim().isEmpty()) {

			return false;
		}

		if (question.getCorrectAnswer() == null) {
			return false;
		}

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