package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.enums.TestAttemptStatus;
import com.doritech.tmsservice.enums.TestResult;
import com.doritech.tmsservice.request.TestAttemptRequest;
import com.doritech.tmsservice.response.TestAttemptResponse;
import com.doritech.tmsservice.service.TestAttemptService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TestAttempt;
import com.doritech.tmsservice.tms.entity.TestSet;
import com.doritech.tmsservice.tms.entity.TrainingAssignment;
import com.doritech.tmsservice.tms.repository.TestAttemptRepository;
import com.doritech.tmsservice.tms.repository.TestSetRepository;
import com.doritech.tmsservice.tms.repository.TrainingAssignmentRepository;

@Service
public class TestAttemptServiceImpl implements TestAttemptService {

	private final TestAttemptRepository testAttemptRepository;
	private final TestSetRepository testSetRepository;
	private final TrainingAssignmentRepository trainingAssignmentRepository;

	public TestAttemptServiceImpl(TestAttemptRepository testAttemptRepository, TestSetRepository testSetRepository,
			TrainingAssignmentRepository trainingAssignmentRepository) {

		this.testAttemptRepository = testAttemptRepository;
		this.testSetRepository = testSetRepository;
		this.trainingAssignmentRepository = trainingAssignmentRepository;
	}

	@Override
	@Transactional
	public ResponseEntity createTestAttempt(TestAttemptRequest request) {

		try {

			if (request == null) {
				return new ResponseEntity("Request cannot be null", 400, null);
			}

			if (request.getTestSetId() == null) {
				return new ResponseEntity("Test set id is required", 400, null);
			}

			if (request.getUserId() == null) {
				return new ResponseEntity("User id is required", 400, null);
			}

			Optional<TestSet> testSetOptional = testSetRepository.findById(request.getTestSetId());

			if (testSetOptional.isEmpty()) {
				return new ResponseEntity("Test set not found", 404, null);
			}

			Integer attemptNumber = request.getAttemptNumber();

			if (attemptNumber == null) {

				long attemptCount = testAttemptRepository.countByTestSet_TestSetIdAndUserId(request.getTestSetId(),
						request.getUserId());

				attemptNumber = (int) attemptCount + 1;
			}

			boolean exists = testAttemptRepository.existsByTestSet_TestSetIdAndUserIdAndAttemptNumber(
					request.getTestSetId(), request.getUserId(), attemptNumber);

			if (exists) {
				return new ResponseEntity("This attempt number already exists for the user", 409, null);
			}

			TestAttempt testAttempt = new TestAttempt();

			testAttempt.setTestSet(testSetOptional.get());
			testAttempt.setUserId(request.getUserId());

			if (request.getTrainingAssignmentId() != null) {

				Optional<TrainingAssignment> assignmentOptional = trainingAssignmentRepository
						.findById(request.getTrainingAssignmentId());

				if (assignmentOptional.isEmpty()) {
					return new ResponseEntity("Training assignment not found", 404, null);
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

			return new ResponseEntity("Test attempt created successfully", 201, mapToResponse(savedAttempt));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while creating test attempt: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getTestAttemptById(Long testAttemptId) {

		try {

			if (testAttemptId == null) {
				return new ResponseEntity("Test attempt id is required", 400, null);
			}

			Optional<TestAttempt> optional = testAttemptRepository.findById(testAttemptId);

			if (optional.isEmpty()) {
				return new ResponseEntity("Test attempt not found", 404, null);
			}

			return new ResponseEntity("Test attempt fetched successfully", 200, mapToResponse(optional.get()));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching test attempt: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getTestAttemptsByUserId(Long userId) {

		try {

			if (userId == null) {
				return new ResponseEntity("User id is required", 400, null);
			}

			List<TestAttempt> attempts = testAttemptRepository.findByUserId(userId);

			List<TestAttemptResponse> response = attempts.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Test attempts fetched successfully", 200, response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching test attempts: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getTestAttemptsByTestSetAndUser(Long testSetId, Long userId) {

		try {

			if (testSetId == null || userId == null) {
				return new ResponseEntity("Test set id and user id are required", 400, null);
			}

			List<TestAttempt> attempts = testAttemptRepository.findByTestSet_TestSetIdAndUserId(testSetId, userId);

			List<TestAttemptResponse> response = attempts.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Test attempts fetched successfully", 200, response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching test attempts: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getTestAttemptsByTrainingAssignment(Long trainingAssignmentId) {

		try {

			if (trainingAssignmentId == null) {
				return new ResponseEntity("Training assignment id is required", 400, null);
			}

			List<TestAttempt> attempts = testAttemptRepository
					.findByTrainingAssignment_TrainingAssignmentId(trainingAssignmentId);

			List<TestAttemptResponse> response = attempts.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Test attempts fetched successfully", 200, response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching test attempts: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getUserTestAttemptsByTrainingAssignment(Long trainingAssignmentId, Long userId) {

		try {

			if (trainingAssignmentId == null || userId == null) {
				return new ResponseEntity("Training assignment id and user id are required", 400, null);
			}

			List<TestAttempt> attempts = testAttemptRepository
					.findByTrainingAssignment_TrainingAssignmentIdAndUserId(trainingAssignmentId, userId);

			List<TestAttemptResponse> response = attempts.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Test attempts fetched successfully", 200, response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching test attempts: " + e.getMessage(), 500, null);
		}
	}

	@Override
	public ResponseEntity getInProgressTestAttempt(Long userId) {

		try {

			if (userId == null) {
				return new ResponseEntity("User id is required", 400, null);
			}

			Optional<TestAttempt> optional = testAttemptRepository.findByUserIdAndStatus(userId,
					TestAttemptStatus.IN_PROGRESS);

			if (optional.isEmpty()) {
				return new ResponseEntity("No in-progress test attempt found", 404, null);
			}

			return new ResponseEntity("In-progress test attempt found", 200, mapToResponse(optional.get()));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while fetching in-progress attempt: " + e.getMessage(), 500, null);
		}
	}

	@Override
	@Transactional
	public ResponseEntity updateTestAttempt(Long testAttemptId, TestAttemptRequest request) {

		try {

			if (testAttemptId == null) {
				return new ResponseEntity("Test attempt id is required", 400, null);
			}

			Optional<TestAttempt> optional = testAttemptRepository.findById(testAttemptId);

			if (optional.isEmpty()) {
				return new ResponseEntity("Test attempt not found", 404, null);
			}

			TestAttempt testAttempt = optional.get();

			if (request.getPassingPercentage() != null) {
				testAttempt.setPassingPercentage(request.getPassingPercentage());
			}

			if (request.getAttemptNumber() != null) {
				testAttempt.setAttemptNumber(request.getAttemptNumber());
			}

			TestAttempt updated = testAttemptRepository.save(testAttempt);

			return new ResponseEntity("Test attempt updated successfully", 200, mapToResponse(updated));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while updating test attempt: " + e.getMessage(), 500, null);
		}
	}

	@Override
	@Transactional
	public ResponseEntity completeTestAttempt(Long testAttemptId) {
		try {
			Optional<TestAttempt> optional = testAttemptRepository.findById(testAttemptId);

			if (optional.isEmpty()) {
				return new ResponseEntity("Test attempt not found", 404, null);
			}

			TestAttempt attempt = optional.get();

			attempt.setStatus(TestAttemptStatus.COMPLETED);
			attempt.setEndTime(LocalDateTime.now());

			if (attempt.getPassingPercentage() != null && attempt.getTotalQuestions() != null
					&& attempt.getTotalQuestions() > 0) {

				double percentage = ((double) attempt.getCorrectAnswers() / attempt.getTotalQuestions()) * 100;

				if (percentage >= attempt.getPassingPercentage().doubleValue()) {

					attempt.setResult(TestResult.PASS);

				} else {

					attempt.setResult(TestResult.FAIL);
				}
			}

			TestAttempt saved = testAttemptRepository.save(attempt);

			return new ResponseEntity("Test completed successfully", 200, mapToResponse(saved));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while completing test: " + e.getMessage(), 500, null);
		}
	}

	@Override
	@Transactional
	public ResponseEntity abandonTestAttempt(Long testAttemptId) {

		try {

			Optional<TestAttempt> optional = testAttemptRepository.findById(testAttemptId);

			if (optional.isEmpty()) {
				return new ResponseEntity("Test attempt not found", 404, null);
			}

			TestAttempt attempt = optional.get();

			attempt.setStatus(TestAttemptStatus.ABANDONED);
			attempt.setEndTime(LocalDateTime.now());

			TestAttempt saved = testAttemptRepository.save(attempt);

			return new ResponseEntity("Test attempt abandoned successfully", 200, mapToResponse(saved));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while abandoning test attempt: " + e.getMessage(), 500, null);
		}
	}

	@Override
	@Transactional
	public ResponseEntity incrementViolationCount(Long testAttemptId) {

		try {

			Optional<TestAttempt> optional = testAttemptRepository.findById(testAttemptId);

			if (optional.isEmpty()) {
				return new ResponseEntity("Test attempt not found", 404, null);
			}

			TestAttempt attempt = optional.get();

			Integer count = attempt.getViolationCount();

			if (count == null) {
				count = 0;
			}

			attempt.setViolationCount(count + 1);

			TestAttempt saved = testAttemptRepository.save(attempt);

			return new ResponseEntity("Violation count updated successfully", 200, mapToResponse(saved));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while updating violation count: " + e.getMessage(), 500, null);
		}
	}

	@Override
	@Transactional
	public ResponseEntity deleteTestAttempt(Long testAttemptId) {

		try {

			Optional<TestAttempt> optional = testAttemptRepository.findById(testAttemptId);

			if (optional.isEmpty()) {
				return new ResponseEntity("Test attempt not found", 404, null);
			}

			testAttemptRepository.deleteById(testAttemptId);

			return new ResponseEntity("Test attempt deleted successfully", 200, null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Error while deleting test attempt: " + e.getMessage(), 500, null);
		}
	}

	private TestAttemptResponse mapToResponse(TestAttempt entity) {

		TestAttemptResponse response = new TestAttemptResponse();

		response.setTestAttemptId(entity.getTestAttemptId());

		response.setTestSetId(entity.getTestSet() != null ? entity.getTestSet().getTestSetId() : null);

		response.setUserId(entity.getUserId());

		response.setTrainingAssignmentId(
				entity.getTrainingAssignment() != null ? entity.getTrainingAssignment().getTrainingAssignmentId()
						: null);

		response.setStartTime(entity.getStartTime());
		response.setEndTime(entity.getEndTime());

		response.setTotalScore(entity.getTotalScore());
		response.setTotalQuestions(entity.getTotalQuestions());
		response.setCorrectAnswers(entity.getCorrectAnswers());
		response.setWrongAnswers(entity.getWrongAnswers());
		response.setSkippedQuestions(entity.getSkippedQuestions());

		response.setPassingPercentage(entity.getPassingPercentage());

		response.setResult(entity.getResult() != null ? entity.getResult().name() : null);

		response.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);

		response.setViolationCount(entity.getViolationCount());

		response.setAttemptNumber(entity.getAttemptNumber());

		return response;
	}
}