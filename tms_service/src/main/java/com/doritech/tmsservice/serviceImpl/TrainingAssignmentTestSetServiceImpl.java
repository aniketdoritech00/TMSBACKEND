package com.doritech.tmsservice.serviceImpl;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.response.TestSetResponse;
import com.doritech.tmsservice.service.TrainingAssignmentTestSetService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TestAttempt;
import com.doritech.tmsservice.tms.entity.TestSet;
import com.doritech.tmsservice.tms.entity.TrainingAssignment;
import com.doritech.tmsservice.tms.entity.TrainingAssignmentTestSet;
import com.doritech.tmsservice.tms.repository.TestAttemptRepository;
import com.doritech.tmsservice.tms.repository.TrainingAssignmentRepository;
import com.doritech.tmsservice.tms.repository.TrainingAssignmentTestSetRepository;

@Service
public class TrainingAssignmentTestSetServiceImpl implements TrainingAssignmentTestSetService {

	private final TrainingAssignmentTestSetRepository trainingAssignmentTestSetRepository;
	private final TestAttemptRepository testAttemptRepository;
	private final TrainingAssignmentRepository trainingAssignmentRepository;

	public TrainingAssignmentTestSetServiceImpl(TrainingAssignmentTestSetRepository trainingAssignmentTestSetRepository,
			TestAttemptRepository testAttemptRepository, TrainingAssignmentRepository trainingAssignmentRepository) {
		this.trainingAssignmentTestSetRepository = trainingAssignmentTestSetRepository;
		this.testAttemptRepository = testAttemptRepository;
		this.trainingAssignmentRepository = trainingAssignmentRepository;
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTestSetByTrainingAssignmentId(Long trainingAssignmentId) {

		try {

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {
				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TrainingAssignment> optionalAssignment = trainingAssignmentRepository
					.findById(trainingAssignmentId);

			if (optionalAssignment.isEmpty()) {
				return new ResponseEntity("Training assignment not found with id: " + trainingAssignmentId,
						HttpStatus.NOT_FOUND.value(), null);
			}

			Optional<TrainingAssignmentTestSet> optionalAssignmentTestSet = trainingAssignmentTestSetRepository
					.findByTrainingAssignment_TrainingAssignmentId(trainingAssignmentId);

			if (optionalAssignmentTestSet.isEmpty()) {
				return new ResponseEntity("Test set not assigned to training assignment id: " + trainingAssignmentId,
						HttpStatus.NOT_FOUND.value(), null);
			}

			TrainingAssignmentTestSet assignmentTestSet = optionalAssignmentTestSet.get();

			TestSet testSet = assignmentTestSet.getTestSet();

			if (testSet == null) {
				return new ResponseEntity("Test set not found for training assignment id: " + trainingAssignmentId,
						HttpStatus.NOT_FOUND.value(), null);
			}

			TestSetResponse response = mapToResponse(testSet);

			return new ResponseEntity("Test set fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private TestSetResponse mapToResponse(TestSet testSet) {

		TestSetResponse response = new TestSetResponse();

		response.setTestSetId(testSet.getTestSetId());

		response.setTestName(testSet.getTestName());

		response.setTestDescription(testSet.getTestDescription());

		response.setTestSetCode(testSet.getTestSetCode());

		response.setSetNo(testSet.getSetNo());

		if (testSet.getTraining() != null) {

			response.setTrainingId(testSet.getTraining().getTrainingId());

			response.setTrainingCode(testSet.getTraining().getTrainingCode());

			response.setTrainingName(testSet.getTraining().getTrainingName());
		}

		response.setStartDateTime(testSet.getStartDateTime());

		response.setEndDateTime(testSet.getEndDateTime());

		response.setTimeLimitMinutes(testSet.getTimeLimitMinutes());

		response.setPassingPercentage(testSet.getPassingPercentage());

		response.setShuffleQuestions(testSet.getShuffleQuestions());

		response.setShuffleOptions(testSet.getShuffleOptions());

		response.setIsActive(testSet.getIsActive());

		response.setCreatedBy(testSet.getCreatedBy());

		response.setCreatedAt(testSet.getCreatedAt());

		response.setUpdatedAt(testSet.getUpdatedAt());

		response.setPublishedAt(testSet.getPublishedAt());

		Optional<TestAttempt> optionalAttempt = testAttemptRepository
				.findTopByTestSet_TestSetIdOrderByTestAttemptIdDesc(testSet.getTestSetId());

		if (optionalAttempt.isEmpty()) {

			response.setTestSetStatus("NOT_STARTED");

		} else {

			TestAttempt testAttempt = optionalAttempt.get();

			if (testAttempt.getStatus() != null) {

				response.setTestSetStatus(testAttempt.getStatus().name());

			} else {

				response.setTestSetStatus("NOT_STARTED");
			}
		}

		return response;
	}

}
