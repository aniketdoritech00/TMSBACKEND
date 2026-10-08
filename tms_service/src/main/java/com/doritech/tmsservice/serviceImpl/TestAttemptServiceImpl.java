package com.doritech.tmsservice.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.enums.TestAttemptStatus;
import com.doritech.tmsservice.enums.TestResult;
import com.doritech.tmsservice.request.TestAttemptRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.TestAttemptResponse;
import com.doritech.tmsservice.service.TestAttemptService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TestAttempt;
import com.doritech.tmsservice.tms.entity.TestSet;
import com.doritech.tmsservice.tms.entity.TrainingAssignment;
import com.doritech.tmsservice.tms.entity.UserResponse;
import com.doritech.tmsservice.tms.repository.TestAttemptRepository;
import com.doritech.tmsservice.tms.repository.TestSetRepository;
import com.doritech.tmsservice.tms.repository.TrainingAssignmentRepository;
import com.doritech.tmsservice.tms.repository.UserResponseRepository;

@Service
public class TestAttemptServiceImpl implements TestAttemptService {

	private final TestAttemptRepository testAttemptRepository;
	private final TestSetRepository testSetRepository;
	private final TrainingAssignmentRepository trainingAssignmentRepository;
	private final UserResponseRepository userResponseRepository;

	public TestAttemptServiceImpl(TestAttemptRepository testAttemptRepository, TestSetRepository testSetRepository,
			TrainingAssignmentRepository trainingAssignmentRepository, UserResponseRepository userResponseRepository) {

		this.testAttemptRepository = testAttemptRepository;
		this.testSetRepository = testSetRepository;
		this.trainingAssignmentRepository = trainingAssignmentRepository;
		this.userResponseRepository = userResponseRepository;
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity createTestAttempt(TestAttemptRequest request) {

		try {

			if (request == null) {
				return new ResponseEntity("Request cannot be null", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTestSetId() == null || request.getTestSetId() <= 0) {

				return new ResponseEntity("Valid test set id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getUserId() == null || request.getUserId() <= 0) {

				return new ResponseEntity("Valid user id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestSet> testSetOptional = testSetRepository.findById(request.getTestSetId());

			if (testSetOptional.isEmpty()) {

				return new ResponseEntity("Test set not found", HttpStatus.NOT_FOUND.value(), null);
			}

			Integer attemptNumber = request.getAttemptNumber();

			if (attemptNumber == null) {

				long attemptCount = testAttemptRepository.countByTestSet_TestSetIdAndUserId(request.getTestSetId(),
						request.getUserId());

				attemptNumber = (int) attemptCount + 1;

			} else {

				if (attemptNumber <= 0) {

					return new ResponseEntity("Attempt number must be greater than 0", HttpStatus.BAD_REQUEST.value(),
							null);
				}
			}

			boolean exists = testAttemptRepository.existsByTestSet_TestSetIdAndUserIdAndAttemptNumber(
					request.getTestSetId(), request.getUserId(), attemptNumber);

			if (exists) {

				return new ResponseEntity("This attempt number already exists for the user",
						HttpStatus.CONFLICT.value(), null);
			}

			TestAttempt testAttempt = new TestAttempt();

			testAttempt.setTestSet(testSetOptional.get());

			testAttempt.setUserId(request.getUserId());

			if (request.getTrainingAssignmentId() != null) {

				if (request.getTrainingAssignmentId() <= 0) {

					return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
				}

				Optional<TrainingAssignment> assignmentOptional = trainingAssignmentRepository
						.findById(request.getTrainingAssignmentId());

				if (assignmentOptional.isEmpty()) {

					return new ResponseEntity("Training assignment not found", HttpStatus.NOT_FOUND.value(), null);
				}

				testAttempt.setTrainingAssignment(assignmentOptional.get());
			}

			testAttempt.setPassingPercentage(request.getPassingPercentage());

			testAttempt.setAttemptNumber(attemptNumber);

			testAttempt.setTotalQuestions(0);
			testAttempt.setCorrectAnswers(0);
			testAttempt.setWrongAnswers(0);
			testAttempt.setSkippedQuestions(0);
			testAttempt.setViolationCount(0);

			testAttempt.setStatus(TestAttemptStatus.IN_PROGRESS);

			testAttempt.setStartTime(LocalDateTime.now());

			TestAttempt savedAttempt = testAttemptRepository.save(testAttempt);

			return new ResponseEntity("Test attempt created successfully", HttpStatus.CREATED.value(),
					mapToResponse(savedAttempt));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while creating test attempt: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTestAttemptById(Long testAttemptId) {

		try {

			if (testAttemptId == null || testAttemptId <= 0) {

				return new ResponseEntity("Invalid test attempt id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestAttempt> optional = testAttemptRepository.findById(testAttemptId);

			if (optional.isEmpty()) {

				return new ResponseEntity("Test attempt not found", HttpStatus.NOT_FOUND.value(), null);
			}

			return new ResponseEntity("Test attempt fetched successfully", HttpStatus.OK.value(),
					mapToResponse(optional.get()));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching test attempt: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllTestAttempts(int page, int size, String sortBy, String sortDir) {

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

				sortBy = "testAttemptId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<TestAttempt> attemptPage = testAttemptRepository.findAll(pageable);

			List<TestAttemptResponse> response = attemptPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<TestAttemptResponse> pageResponse = new PageResponse<>(response, attemptPage.getNumber(),
					attemptPage.getSize(), attemptPage.getTotalElements(), attemptPage.getTotalPages(),
					attemptPage.isLast());

			return new ResponseEntity("Test attempts fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching test attempts: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTestAttemptsByUserId(Long userId, int page, int size, String sortBy, String sortDir) {

		try {

			if (userId == null || userId <= 0) {

				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
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

				sortBy = "testAttemptId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<TestAttempt> attemptPage = testAttemptRepository.findByUserId(userId, pageable);

			List<TestAttemptResponse> response = attemptPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<TestAttemptResponse> pageResponse = new PageResponse<>(response, attemptPage.getNumber(),
					attemptPage.getSize(), attemptPage.getTotalElements(), attemptPage.getTotalPages(),
					attemptPage.isLast());

			return new ResponseEntity("Test attempts fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching user test attempts: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTestAttemptsByTestSetAndUser(Long testSetId, Long userId, int page, int size,
			String sortBy, String sortDir) {

		try {

			if (testSetId == null || testSetId <= 0) {

				return new ResponseEntity("Invalid test set id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (userId == null || userId <= 0) {

				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
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

				sortBy = "testAttemptId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<TestAttempt> attemptPage = testAttemptRepository.findByTestSet_TestSetIdAndUserId(testSetId, userId,
					pageable);

			List<TestAttemptResponse> response = attemptPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<TestAttemptResponse> pageResponse = new PageResponse<>(response, attemptPage.getNumber(),
					attemptPage.getSize(), attemptPage.getTotalElements(), attemptPage.getTotalPages(),
					attemptPage.isLast());

			return new ResponseEntity("Test attempts fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching test attempts: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTestAttemptsByTrainingAssignment(Long trainingAssignmentId, int page, int size,
			String sortBy, String sortDir) {

		try {

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {

				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
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

				sortBy = "testAttemptId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<TestAttempt> attemptPage = testAttemptRepository
					.findByTrainingAssignment_TrainingAssignmentId(trainingAssignmentId, pageable);

			List<TestAttemptResponse> response = attemptPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<TestAttemptResponse> pageResponse = new PageResponse<>(response, attemptPage.getNumber(),
					attemptPage.getSize(), attemptPage.getTotalElements(), attemptPage.getTotalPages(),
					attemptPage.isLast());

			return new ResponseEntity("Test attempts fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching training assignment attempts: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getUserTestAttemptsByTrainingAssignment(Long trainingAssignmentId, Long userId, int page,
			int size, String sortBy, String sortDir) {

		try {

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {

				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (userId == null || userId <= 0) {

				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
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

				sortBy = "testAttemptId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<TestAttempt> attemptPage = testAttemptRepository
					.findByTrainingAssignment_TrainingAssignmentIdAndUserId(trainingAssignmentId, userId, pageable);

			List<TestAttemptResponse> response = attemptPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<TestAttemptResponse> pageResponse = new PageResponse<>(response, attemptPage.getNumber(),
					attemptPage.getSize(), attemptPage.getTotalElements(), attemptPage.getTotalPages(),
					attemptPage.isLast());

			return new ResponseEntity("Test attempts fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching user test attempts: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getInProgressTestAttempt(Long userId) {

		try {

			if (userId == null || userId <= 0) {

				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestAttempt> optional = testAttemptRepository.findByUserIdAndStatus(userId,
					TestAttemptStatus.IN_PROGRESS);

			if (optional.isEmpty()) {

				return new ResponseEntity("No in-progress test attempt found", HttpStatus.NOT_FOUND.value(), null);
			}

			return new ResponseEntity("In-progress test attempt found", HttpStatus.OK.value(),
					mapToResponse(optional.get()));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching in-progress attempt: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity updateTestAttempt(Long testAttemptId, TestAttemptRequest request) {

		try {

			if (testAttemptId == null || testAttemptId <= 0) {

				return new ResponseEntity("Invalid test attempt id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request == null) {

				return new ResponseEntity("Request cannot be null", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestAttempt> optional = testAttemptRepository.findById(testAttemptId);

			if (optional.isEmpty()) {

				return new ResponseEntity("Test attempt not found", HttpStatus.NOT_FOUND.value(), null);
			}

			TestAttempt testAttempt = optional.get();

			if (request.getPassingPercentage() != null) {

				testAttempt.setPassingPercentage(request.getPassingPercentage());
			}

			if (request.getAttemptNumber() != null) {

				if (request.getAttemptNumber() <= 0) {

					return new ResponseEntity("Attempt number must be greater than 0", HttpStatus.BAD_REQUEST.value(),
							null);
				}

				boolean exists = testAttemptRepository.existsByTestSet_TestSetIdAndUserIdAndAttemptNumber(
						testAttempt.getTestSet().getTestSetId(), testAttempt.getUserId(), request.getAttemptNumber());

				if (exists && !request.getAttemptNumber().equals(testAttempt.getAttemptNumber())) {

					return new ResponseEntity("This attempt number already exists for the user",
							HttpStatus.CONFLICT.value(), null);
				}

				testAttempt.setAttemptNumber(request.getAttemptNumber());
			}

			TestAttempt updated = testAttemptRepository.save(testAttempt);

			return new ResponseEntity("Test attempt updated successfully", HttpStatus.OK.value(),
					mapToResponse(updated));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while updating test attempt: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity completeTestAttempt(Long testAttemptId) {

		try {

			if (testAttemptId == null || testAttemptId <= 0) {
				return new ResponseEntity("Invalid test attempt id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestAttempt> optional = testAttemptRepository.findById(testAttemptId);

			if (optional.isEmpty()) {
				return new ResponseEntity("Test attempt not found", HttpStatus.NOT_FOUND.value(), null);
			}

			TestAttempt attempt = optional.get();

			if (attempt.getStatus() == TestAttemptStatus.COMPLETED) {
				return new ResponseEntity("Test attempt is already completed", HttpStatus.CONFLICT.value(),
						mapToResponse(attempt));
			}

			if (attempt.getStatus() == TestAttemptStatus.ABANDONED) {
				return new ResponseEntity("Abandoned test attempt cannot be completed", HttpStatus.CONFLICT.value(),
						null);
			}

			List<UserResponse> responses = userResponseRepository.findByTestAttempt_TestAttemptId(testAttemptId);

			int totalQuestions = 0;
			int correctAnswers = 0;
			int wrongAnswers = 0;
			int skippedQuestions = 0;

			BigDecimal totalScore = BigDecimal.ZERO;

			if (responses != null && !responses.isEmpty()) {

				totalQuestions = responses.size();

				for (UserResponse response : responses) {

					String userAnswer = response.getUserAnswer();

					Boolean isCorrect = response.getIsCorrect();

					if (userAnswer == null || userAnswer.trim().isEmpty()) {

						skippedQuestions++;

						response.setIsCorrect(false);

					} else if (Boolean.TRUE.equals(isCorrect)) {

						correctAnswers++;

						if (response.getTestQuestion() != null && response.getTestQuestion().getMarks() != null) {

							totalScore = totalScore.add(response.getTestQuestion().getMarks());
						}

					}
					// Wrong answer
					else {

						wrongAnswers++;
					}
				}
			}

			/*
			 * Save calculated values
			 */
			attempt.setTotalQuestions(totalQuestions);
			attempt.setCorrectAnswers(correctAnswers);
			attempt.setWrongAnswers(wrongAnswers);
			attempt.setSkippedQuestions(skippedQuestions);
			attempt.setTotalScore(totalScore);

			/*
			 * Calculate percentage
			 *
			 * Example: Total Questions = 3 Correct Answers = 3
			 *
			 * Percentage = (3 / 3) * 100 = 100%
			 */
			BigDecimal percentage = BigDecimal.ZERO;

			if (totalQuestions > 0) {

				percentage = BigDecimal.valueOf(correctAnswers)
						.divide(BigDecimal.valueOf(totalQuestions), 4, RoundingMode.HALF_UP)
						.multiply(new BigDecimal("100"));
			}

			BigDecimal passingPercentage = attempt.getPassingPercentage();

			if (passingPercentage != null && totalQuestions > 0) {

				if (percentage.compareTo(passingPercentage) >= 0) {

					attempt.setResult(TestResult.PASS);

				} else {

					attempt.setResult(TestResult.FAIL);
				}
			}
			attempt.setStatus(TestAttemptStatus.COMPLETED);
			attempt.setEndTime(LocalDateTime.now());

			TestAttempt saved = testAttemptRepository.save(attempt);

			return new ResponseEntity("Test completed successfully", HttpStatus.OK.value(), mapToResponse(saved));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while completing test: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity abandonTestAttempt(Long testAttemptId) {

		try {

			if (testAttemptId == null || testAttemptId <= 0) {

				return new ResponseEntity("Invalid test attempt id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestAttempt> optional = testAttemptRepository.findById(testAttemptId);

			if (optional.isEmpty()) {

				return new ResponseEntity("Test attempt not found", HttpStatus.NOT_FOUND.value(), null);
			}

			TestAttempt attempt = optional.get();

			if (attempt.getStatus() == TestAttemptStatus.COMPLETED) {

				return new ResponseEntity("Completed test attempt cannot be abandoned", HttpStatus.CONFLICT.value(),
						null);
			}

			if (attempt.getStatus() == TestAttemptStatus.ABANDONED) {

				return new ResponseEntity("Test attempt is already abandoned", HttpStatus.CONFLICT.value(),
						mapToResponse(attempt));
			}

			attempt.setStatus(TestAttemptStatus.ABANDONED);

			attempt.setEndTime(LocalDateTime.now());

			TestAttempt saved = testAttemptRepository.save(attempt);

			return new ResponseEntity("Test attempt abandoned successfully", HttpStatus.OK.value(),
					mapToResponse(saved));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while abandoning test attempt: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity incrementViolationCount(Long testAttemptId) {

		try {

			if (testAttemptId == null || testAttemptId <= 0) {

				return new ResponseEntity("Invalid test attempt id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestAttempt> optional = testAttemptRepository.findById(testAttemptId);

			if (optional.isEmpty()) {

				return new ResponseEntity("Test attempt not found", HttpStatus.NOT_FOUND.value(), null);
			}

			TestAttempt attempt = optional.get();

			if (attempt.getStatus() != TestAttemptStatus.IN_PROGRESS) {

				return new ResponseEntity("Violation count can only be updated for an in-progress test",
						HttpStatus.CONFLICT.value(), null);
			}

			Integer count = attempt.getViolationCount();

			if (count == null) {
				count = 0;
			}

			attempt.setViolationCount(count + 1);

			TestAttempt saved = testAttemptRepository.save(attempt);

			return new ResponseEntity("Violation count updated successfully", HttpStatus.OK.value(),
					mapToResponse(saved));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while updating violation count: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity deleteTestAttempt(Long testAttemptId) {

		try {

			if (testAttemptId == null || testAttemptId <= 0) {

				return new ResponseEntity("Invalid test attempt id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestAttempt> optional = testAttemptRepository.findById(testAttemptId);

			if (optional.isEmpty()) {

				return new ResponseEntity("Test attempt not found", HttpStatus.NOT_FOUND.value(), null);
			}

			testAttemptRepository.deleteById(testAttemptId);

			return new ResponseEntity("Test attempt deleted successfully", HttpStatus.OK.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while deleting test attempt: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private TestAttemptResponse mapToResponse(TestAttempt attempt) {

		TestAttemptResponse response = new TestAttemptResponse();

		response.setTestAttemptId(attempt.getTestAttemptId());

		if (attempt.getTestSet() != null) {
			response.setTestSetId(attempt.getTestSet().getTestSetId());
		}

		response.setUserId(attempt.getUserId());

		if (attempt.getTrainingAssignment() != null) {
			response.setTrainingAssignmentId(attempt.getTrainingAssignment().getTrainingAssignmentId());
		}

		response.setStartTime(attempt.getStartTime());
		response.setEndTime(attempt.getEndTime());

		response.setTotalScore(attempt.getTotalScore());

		response.setTotalQuestions(attempt.getTotalQuestions());
		response.setCorrectAnswers(attempt.getCorrectAnswers());
		response.setWrongAnswers(attempt.getWrongAnswers());
		response.setSkippedQuestions(attempt.getSkippedQuestions());

		response.setPassingPercentage(attempt.getPassingPercentage());

		BigDecimal percentage = BigDecimal.ZERO;

		if (attempt.getTotalQuestions() != null && attempt.getTotalQuestions() > 0
				&& attempt.getCorrectAnswers() != null) {

			percentage = BigDecimal.valueOf(attempt.getCorrectAnswers())
					.divide(BigDecimal.valueOf(attempt.getTotalQuestions()), 2, RoundingMode.HALF_UP)
					.multiply(new BigDecimal("100"));
		}

		response.setPercentage(percentage);

		response.setResult(attempt.getResult());
		response.setStatus(attempt.getStatus());

		response.setViolationCount(attempt.getViolationCount());
		response.setAttemptNumber(attempt.getAttemptNumber());

		return response;
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTestSetStatusByTestSetId(Long testSetId) {

		try {

			if (testSetId == null || testSetId <= 0) {
				return new ResponseEntity("Invalid test set id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestAttempt> optionalAttempt = testAttemptRepository
					.findTopByTestSet_TestSetIdOrderByTestAttemptIdDesc(testSetId);

			if (optionalAttempt.isEmpty()) {

				return new ResponseEntity("Test set status fetched successfully", HttpStatus.OK.value(), "NOT_STARTED");
			}

			TestAttempt testAttempt = optionalAttempt.get();

			TestAttemptStatus status = testAttempt.getStatus();

			String testSetStatus;

			if (status == null) {
				testSetStatus = "NOT_STARTED";
			} else {
				testSetStatus = status.name();
			}

			return new ResponseEntity("Test set status fetched successfully", HttpStatus.OK.value(), testSetStatus);

		} catch (Exception e) {

			return new ResponseEntity("Failed to fetch test set status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
					null);
		}
	}

	@Override
	@Transactional(readOnly = true, transactionManager = "tmsTransactionManager")
	public ResponseEntity getTestAttemptByTestSetId(Long testSetId) {

	    ResponseEntity response = new ResponseEntity();

	    try {

	        if (testSetId == null || testSetId <= 0) {
	            response.setMessage("Invalid test set ID");
	            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
	            response.setPayload(null);
	            return response;
	        }

	        Long userId = CurrentUser.getUserId();

	        if (userId == null) {
	            response.setMessage("Current user not found");
	            response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
	            response.setPayload(null);
	            return response;
	        }

	        TestAttempt testAttempt = testAttemptRepository
	                .findTopByTestSet_TestSetIdAndUserIdOrderByAttemptNumberDesc(
	                        testSetId, userId)
	                .orElse(null);

	        if (testAttempt == null) {
	            response.setMessage(
	                    "No test attempt found for the current user");
	            response.setStatusCode(HttpStatus.NOT_FOUND.value());
	            response.setPayload(null);
	            return response;
	        }

	        TestAttemptResponse result = new TestAttemptResponse();

	        result.setTestAttemptId(
	                testAttempt.getTestAttemptId());

	        /*
	         * Get only the ID from the lazy TestSet relationship.
	         * Do not return the TestSet entity itself.
	         */
	        if (testAttempt.getTestSet() != null) {
	            result.setTestSetId(
	                    testAttempt.getTestSet().getTestSetId());
	        }

	        result.setUserId(
	                testAttempt.getUserId());

	        /*
	         * Get only the ID from TrainingAssignment.
	         */
	        if (testAttempt.getTrainingAssignment() != null) {
	            result.setTrainingAssignmentId(
	                    testAttempt.getTrainingAssignment()
	                            .getTrainingAssignmentId());
	        }

	        result.setStartTime(
	                testAttempt.getStartTime());

	        result.setEndTime(
	                testAttempt.getEndTime());

	        result.setTotalScore(
	                testAttempt.getTotalScore());

	        result.setTotalQuestions(
	                testAttempt.getTotalQuestions());

	        result.setCorrectAnswers(
	                testAttempt.getCorrectAnswers());

	        result.setWrongAnswers(
	                testAttempt.getWrongAnswers());

	        result.setSkippedQuestions(
	                testAttempt.getSkippedQuestions());

	        result.setPassingPercentage(
	                testAttempt.getPassingPercentage());

	        /*
	         * Calculate percentage.
	         *
	         * Example:
	         * correctAnswers = 8
	         * totalQuestions = 10
	         * percentage = 80.00
	         */
	        if (testAttempt.getTotalQuestions() != null
	                && testAttempt.getTotalQuestions() > 0
	                && testAttempt.getCorrectAnswers() != null) {

	            BigDecimal percentage = BigDecimal.valueOf(
	                    testAttempt.getCorrectAnswers())
	                    .multiply(BigDecimal.valueOf(100))
	                    .divide(
	                            BigDecimal.valueOf(
	                                    testAttempt.getTotalQuestions()),
	                            2,
	                            java.math.RoundingMode.HALF_UP);

	            result.setPercentage(percentage);

	        } else {
	            result.setPercentage(BigDecimal.ZERO);
	        }

	        result.setResult(
	                testAttempt.getResult());

	        result.setStatus(
	                testAttempt.getStatus());

	        result.setViolationCount(
	                testAttempt.getViolationCount());

	        result.setAttemptNumber(
	                testAttempt.getAttemptNumber());

	        response.setMessage(
	                "Test attempt details fetched successfully");

	        response.setStatusCode(
	                HttpStatus.OK.value());

	        response.setPayload(result);

	    } catch (Exception e) {

	        e.printStackTrace();

	        response.setMessage(
	                "Failed to fetch test attempt details");

	        response.setStatusCode(
	                HttpStatus.INTERNAL_SERVER_ERROR.value());

	        response.setPayload(null);
	    }

	    return response;
	}
}