package com.doritech.tmsservice.serviceImpl;

import java.math.BigDecimal;
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

import com.doritech.tmsservice.enums.AssignmentStatus;
import com.doritech.tmsservice.enums.TestAttemptStatus;
import com.doritech.tmsservice.enums.TestResult;
import com.doritech.tmsservice.request.UserResponseRequest;
import com.doritech.tmsservice.response.DirectTestAssignmentResponse;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.UserResponseResponse;
import com.doritech.tmsservice.service.DirectTestAssignmentService;
import com.doritech.tmsservice.service.UserResponseService;
import com.doritech.tmsservice.tms.entity.DirectTestAssignment;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TestAttempt;
import com.doritech.tmsservice.tms.entity.TestQuestion;
import com.doritech.tmsservice.tms.entity.UserResponse;
import com.doritech.tmsservice.tms.repository.DirectTestAssignmentRepository;
import com.doritech.tmsservice.tms.repository.TestAttemptRepository;
import com.doritech.tmsservice.tms.repository.TestQuestionRepository;
import com.doritech.tmsservice.tms.repository.UserBatchRepository;
import com.doritech.tmsservice.tms.repository.UserResponseRepository;

@Service
public class UserResponseServiceImpl implements UserResponseService {

	private final UserResponseRepository userResponseRepository;
	private final TestAttemptRepository testAttemptRepository;
	private final TestQuestionRepository testQuestionRepository;
	private final DirectTestAssignmentService directTestAssignmentService;
	private final DirectTestAssignmentRepository directTestAssignmentRepository;
	private final UserBatchRepository userBatchRepository;

	public UserResponseServiceImpl(UserResponseRepository userResponseRepository,
			TestAttemptRepository testAttemptRepository, TestQuestionRepository testQuestionRepository,
			DirectTestAssignmentService directTestAssignmentService,
			DirectTestAssignmentRepository directTestAssignmentRepository, UserBatchRepository userBatchRepository) {

		this.userBatchRepository = userBatchRepository;
		this.directTestAssignmentRepository = directTestAssignmentRepository;
		this.directTestAssignmentService = directTestAssignmentService;
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

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity finishTest(Long testAttemptId) {

		try {
			// 1. Validate test attempt ID
			if (testAttemptId == null || testAttemptId <= 0) {
				return new ResponseEntity("Valid test attempt id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			// 2. Find test attempt
			TestAttempt attempt = testAttemptRepository.findById(testAttemptId).orElse(null);

			if (attempt == null) {
				return new ResponseEntity("Test attempt not found", HttpStatus.NOT_FOUND.value(), null);
			}

			if (attempt.getTestSet() == null || attempt.getUserId() == null) {
				return new ResponseEntity("Test set or user information is missing", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			Long testSetId = attempt.getTestSet().getTestSetId();
			Long userId = attempt.getUserId();

			// 3. Find individual assignment first
			DirectTestAssignment assignment = directTestAssignmentRepository
					.findDirectTestAssignmentByTestSet_TestSetIdAndUserId(testSetId, userId).orElse(null);

			// 4. If no individual assignment, find the batch assignment
			if (assignment == null) {

				List<Long> batchIds = userBatchRepository.findAllByUserId(userId).stream()
						.filter(ub -> ub.getBatch() != null).map(ub -> ub.getBatch().getBatchId())
						.filter(java.util.Objects::nonNull).distinct().collect(Collectors.toList());

				if (!batchIds.isEmpty()) {
					assignment = directTestAssignmentRepository.findByBatch_BatchIdIn(batchIds).stream()
							.filter(a -> a.getTestSet() != null && testSetId.equals(a.getTestSet().getTestSetId()))
							.findFirst().orElse(null);
				}
			}

			if (assignment == null) {
				return new ResponseEntity("Direct test assignment not found for this user or their batch",
						HttpStatus.NOT_FOUND.value(), null);
			}

			// 5. If already completed, return the existing result
			// without recalculating or overwriting it.
			if (attempt.getStatus() == TestAttemptStatus.COMPLETED) {

				DirectTestAssignmentResponse response = buildFinishTestResponse(assignment);

				// Prefer this attempt's saved result for repeat calls.
				response.setFinalScore(attempt.getTotalScore());
				response.setStatus(assignment.getStatus());
				response.setProgressPercentage(assignment.getProgressPercentage());
				response.setCompletionDate(assignment.getCompletionDate());
				response.setIsPassed(attempt.getResult() == TestResult.PASS);

				response.setTotalQuestions(attempt.getTotalQuestions());
				response.setCorrectAnswers(attempt.getCorrectAnswers());
				response.setWrongAnswers(attempt.getWrongAnswers());

				int attempted = attempt.getCorrectAnswers() == null || attempt.getWrongAnswers() == null ? 0
						: attempt.getCorrectAnswers() + attempt.getWrongAnswers();

				response.setAttemptedQuestions(attempted);

				return new ResponseEntity("Test result fetched successfully", HttpStatus.OK.value(), response);
			}

			// 6. Get submitted answers
			List<UserResponse> responses = userResponseRepository.findByTestAttempt_TestAttemptId(testAttemptId);

			int totalQuestions = Math.toIntExact(testQuestionRepository.countByTestSet_TestSetId(testSetId));

			if (totalQuestions == 0) {
				return new ResponseEntity("No questions found for this test", HttpStatus.BAD_REQUEST.value(), null);
			}

			// 7. Recheck answers against the correct answer stored on each question
			for (UserResponse userResponse : responses) {
				if (userResponse.getTestQuestion() != null) {
					boolean correct = checkAnswer(userResponse.getTestQuestion(), userResponse.getUserAnswer());

					userResponse.setIsCorrect(correct);
				}
			}

			// Persist any recalculated answer correctness
			userResponseRepository.saveAll(responses);

			int attemptedQuestions = (int) responses.stream()
					.filter(r -> r.getUserAnswer() != null && !r.getUserAnswer().trim().isEmpty()).count();

			int correctAnswers = (int) responses.stream().filter(r -> r.getUserAnswer() != null
					&& !r.getUserAnswer().trim().isEmpty() && Boolean.TRUE.equals(r.getIsCorrect())).count();

			int wrongAnswers = attemptedQuestions - correctAnswers;
			int skippedQuestions = Math.max(0, totalQuestions - attemptedQuestions);

			// 8. Calculate percentage score
			BigDecimal finalScore = BigDecimal.valueOf(correctAnswers).multiply(BigDecimal.valueOf(100))
					.divide(BigDecimal.valueOf(totalQuestions), 2, java.math.RoundingMode.HALF_UP);

			BigDecimal passingPercentage = attempt.getPassingPercentage();

			if (passingPercentage == null) {
				return new ResponseEntity("Passing percentage is not configured for this attempt",
						HttpStatus.BAD_REQUEST.value(), null);
			}

			boolean passed = finalScore.compareTo(passingPercentage) >= 0;
			LocalDateTime completedAt = LocalDateTime.now();

			// 9. Update test attempt
			attempt.setTotalQuestions(totalQuestions);
			attempt.setCorrectAnswers(correctAnswers);
			attempt.setWrongAnswers(wrongAnswers);
			attempt.setSkippedQuestions(skippedQuestions);
			attempt.setTotalScore(finalScore);
			attempt.setResult(passed ? TestResult.PASS : TestResult.FAIL);
			attempt.setStatus(TestAttemptStatus.COMPLETED);
			attempt.setEndTime(completedAt);

			testAttemptRepository.save(attempt);

			// 10. Update direct test assignment
			assignment.setStatus(AssignmentStatus.COMPLETED);
			assignment.setProgressPercentage(BigDecimal.valueOf(100));
			assignment.setCompletionDate(completedAt);
			assignment.setFinalScore(finalScore);
			assignment.setIsPassed(passed);
			assignment.setAttemptedQuestions(attemptedQuestions);
			assignment.setCorrectAnswers(correctAnswers);
			assignment.setWrongAnswers(wrongAnswers);
			assignment.setTotalQuestions(totalQuestions);
			assignment.setStartedAt(attempt.getStartTime());
			assignment
					.setRemarks(passed ? "Test completed successfully" : "Test completed; passing score not achieved");

			DirectTestAssignment savedAssignment = directTestAssignmentRepository.save(assignment);

			// 11. Return the complete assignment response
			DirectTestAssignmentResponse response = buildFinishTestResponse(savedAssignment);

			return new ResponseEntity("Direct test submitted successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {
			e.printStackTrace();

			return new ResponseEntity("Error while finishing test: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	/**
	 * Builds the direct-test assignment response. This helper avoids calling
	 * convertToResponse() on the service interface.
	 */
	
	private DirectTestAssignmentResponse buildFinishTestResponse(DirectTestAssignment assignment) {

		DirectTestAssignmentResponse response = new DirectTestAssignmentResponse();

		response.setDirectTestAssignmentId(assignment.getDirectTestAssignmentId());

		if (assignment.getTestSet() != null) {
			response.setTestSetId(assignment.getTestSet().getTestSetId());
			response.setTestName(assignment.getTestSet().getTestName());
		}

		response.setAssignedBy(assignment.getAssignedBy());
		response.setAssignedAt(assignment.getAssignedAt());
		response.setDueDate(assignment.getDueDate());
		response.setStatus(assignment.getStatus());
		response.setProgressPercentage(assignment.getProgressPercentage());
		response.setCompletionDate(assignment.getCompletionDate());
		response.setFinalScore(assignment.getFinalScore());
		response.setIsPassed(assignment.getIsPassed());
		response.setAttemptedQuestions(assignment.getAttemptedQuestions());
		response.setCorrectAnswers(assignment.getCorrectAnswers());
		response.setWrongAnswers(assignment.getWrongAnswers());
		response.setTotalQuestions(assignment.getTotalQuestions());
		response.setRemarks(assignment.getRemarks());
		response.setStartedAt(assignment.getStartedAt());

		if (assignment.getBatch() != null) {
			response.setBatchId(assignment.getBatch().getBatchId());
			response.setBatchName(assignment.getBatch().getBatchName());
			response.setUserId(null);
			response.setUserName(null);
		} else {
			response.setBatchId(null);
			response.setBatchName(null);
			response.setUserId(assignment.getUserId());
		}

		return response;
	}
}