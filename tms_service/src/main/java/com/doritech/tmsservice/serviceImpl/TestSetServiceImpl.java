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

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.request.TestSetRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.TestSetResponse;
import com.doritech.tmsservice.service.ParamService;
import com.doritech.tmsservice.service.TestSetService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TestAttempt;
import com.doritech.tmsservice.tms.entity.TestSet;
import com.doritech.tmsservice.tms.entity.Training;
import com.doritech.tmsservice.tms.repository.TestAttemptRepository;
import com.doritech.tmsservice.tms.repository.TestSetRepository;
import com.doritech.tmsservice.tms.repository.TrainingRepository;

@Service
public class TestSetServiceImpl implements TestSetService {

	private final TestSetRepository testSetRepository;
	private final TrainingRepository trainingRepository;
	private final ParamService paramService;
	private final TestAttemptRepository testAttemptRepository;

	public TestSetServiceImpl(TestSetRepository testSetRepository, TrainingRepository trainingRepository,
			ParamService paramService, TestAttemptRepository testAttemptRepository) {
		this.testSetRepository = testSetRepository;
		this.trainingRepository = trainingRepository;
		this.paramService = paramService;
		this.testAttemptRepository = testAttemptRepository;
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity createTestSet(TestSetRequest request) {

		try {

			if (request == null) {
				return new ResponseEntity("Test set data is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTestName() == null || request.getTestName().trim().isEmpty()) {

				return new ResponseEntity("Test name is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTestSetCode() != null && !request.getTestSetCode().trim().isEmpty()
					&& testSetRepository.existsByTestSetCode(request.getTestSetCode().trim())) {

				return new ResponseEntity("Test code already exists", HttpStatus.CONFLICT.value(), null);
			}

			if (request.getSetNo() != null && !request.getSetNo().trim().isEmpty()
					&& testSetRepository.existsBySetNo(request.getSetNo().trim())) {

				return new ResponseEntity("Set number already exists", HttpStatus.CONFLICT.value(), null);
			}

			Training training = null;

			if (request.getTrainingId() != null) {

				if (request.getTrainingId() <= 0) {

					return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
				}

				Optional<Training> optionalTraining = trainingRepository.findById(request.getTrainingId());

				if (optionalTraining.isEmpty()) {

					return new ResponseEntity("Training not found with id: " + request.getTrainingId(),
							HttpStatus.NOT_FOUND.value(), null);
				}

				training = optionalTraining.get();
			}

			if (request.getStartDateTime() != null && request.getEndDateTime() != null
					&& request.getEndDateTime().isBefore(request.getStartDateTime())) {

				return new ResponseEntity("End date time cannot be before start date time",
						HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getPassingPercentage() != null && (request.getPassingPercentage().doubleValue() < 0
					|| request.getPassingPercentage().doubleValue() > 100)) {

				return new ResponseEntity("Passing percentage must be between 0 and 100",
						HttpStatus.BAD_REQUEST.value(), null);
			}

			Long currentUserId = CurrentUser.getUserId();

			TestSet testSet = new TestSet();

			testSet.setTestName(request.getTestName().trim());

			testSet.setTestDescription(request.getTestDescription());

			testSet.setTestSetCode(request.getTestSetCode() != null ? request.getTestSetCode().trim() : null);

			testSet.setSetNo(request.getSetNo() != null ? request.getSetNo().trim() : null);

			testSet.setTraining(training);

			testSet.setStartDateTime(request.getStartDateTime());

			testSet.setEndDateTime(request.getEndDateTime());

			testSet.setTimeLimitMinutes(request.getTimeLimitMinutes() != null ? request.getTimeLimitMinutes() : 60);

			testSet.setPassingPercentage(request.getPassingPercentage() != null ? request.getPassingPercentage()
					: new java.math.BigDecimal("70.00"));

			testSet.setShuffleQuestions(request.getShuffleQuestions() != null ? request.getShuffleQuestions() : true);

			testSet.setShuffleOptions(request.getShuffleOptions() != null ? request.getShuffleOptions() : true);

			testSet.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

			testSet.setCreatedBy(currentUserId);
			testSet.setCreatedAt(LocalDateTime.now());
			testSet.setUpdatedAt(LocalDateTime.now());

			TestSet savedTestSet = testSetRepository.save(testSet);

			if (savedTestSet.getTestSetCode() != null) {
				try {
					paramService.updateCodeValue(savedTestSet.getTestSetCode());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return new ResponseEntity("Test set created successfully", HttpStatus.CREATED.value(),
					mapToResponse(savedTestSet));

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("Test set already exists or violates database constraints",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTestSetById(Long id) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid test set id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestSet> optionalTestSet = testSetRepository.findById(id);

			if (optionalTestSet.isEmpty()) {

				return new ResponseEntity("Test set not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			return new ResponseEntity("Test set fetched successfully", HttpStatus.OK.value(),
					mapToResponse(optionalTestSet.get()));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllTestSets() {

		try {

			List<TestSet> testSets = testSetRepository.findAll(Sort.by(Sort.Direction.DESC, "testSetId"));

			List<TestSetResponse> response = testSets.stream().map(this::mapToResponse).collect(Collectors.toList());

			return new ResponseEntity("Test sets fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllTestSets(int page, int size, String sortBy, String sortDir) {

		try {

			if (page < 0) {
				page = 0;
			}

			if (size <= 0) {
				size = 10;
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "testSetId";
			}

			Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<TestSet> testSetPage = testSetRepository.findAll(pageable);

			List<TestSetResponse> content = testSetPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<TestSetResponse> pageResponse = new PageResponse<>(content, testSetPage.getNumber(),
					testSetPage.getSize(), testSetPage.getTotalElements(), testSetPage.getTotalPages(),
					testSetPage.isLast());

			return new ResponseEntity("Test sets fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

//	@Override
//	@Transactional(value = "tmsTransactionManager", readOnly = true)
//	public ResponseEntity getTestSetsByTrainingId(Long trainingId) {
//
//		try {
//
//			if (trainingId == null || trainingId <= 0) {
//
//				return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
//			}
//
//			Optional<Training> optionalTraining = trainingRepository.findById(trainingId);
//
//			if (optionalTraining.isEmpty()) {
//
//				return new ResponseEntity("Training not found with id: " + trainingId, HttpStatus.NOT_FOUND.value(),
//						null);
//			}
//
//			List<TestSet> testSets = testSetRepository.findByTraining_TrainingIdOrderByTestSetIdDesc(trainingId);
//
//			List<TestSetResponse> response = testSets.stream().map(this::mapToResponse).collect(Collectors.toList());
//
//			return new ResponseEntity("Test sets fetched successfully", HttpStatus.OK.value(), response);
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
//		}
//	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTestSetsByTrainingId(Long trainingId) {

		try {

			if (trainingId == null || trainingId <= 0) {

				return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<Training> optionalTraining = trainingRepository.findById(trainingId);

			if (optionalTraining.isEmpty()) {

				return new ResponseEntity("Training not found with id: " + trainingId, HttpStatus.NOT_FOUND.value(),
						null);
			}

			List<TestSet> testSets = testSetRepository.findByTraining_TrainingIdOrderByTestSetIdDesc(trainingId);

			List<TestSetResponse> response = testSets.stream().map(this::mapToResponse).collect(Collectors.toList());

			return new ResponseEntity("Test sets fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTestSetsByTrainingId(Long trainingId, int page, int size, String sortBy, String sortDir) {

		try {

			if (trainingId == null || trainingId <= 0) {

				return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<Training> optionalTraining = trainingRepository.findById(trainingId);

			if (optionalTraining.isEmpty()) {

				return new ResponseEntity("Training not found with id: " + trainingId, HttpStatus.NOT_FOUND.value(),
						null);
			}

			if (page < 0) {
				page = 0;
			}

			if (size <= 0) {
				size = 10;
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "testSetId";
			}

			Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<TestSet> testSetPage = testSetRepository.findByTraining_TrainingId(trainingId, pageable);

			List<TestSetResponse> content = testSetPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<TestSetResponse> pageResponse = new PageResponse<>(content, testSetPage.getNumber(),
					testSetPage.getSize(), testSetPage.getTotalElements(), testSetPage.getTotalPages(),
					testSetPage.isLast());

			return new ResponseEntity("Test sets fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity updateTestSet(Long id, TestSetRequest request) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid test set id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request == null) {

				return new ResponseEntity("Test set data is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTestName() == null || request.getTestName().trim().isEmpty()) {

				return new ResponseEntity("Test name is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestSet> optionalTestSet = testSetRepository.findById(id);

			if (optionalTestSet.isEmpty()) {

				return new ResponseEntity("Test set not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			if (request.getTestSetCode() != null && !request.getTestSetCode().trim().isEmpty()
					&& testSetRepository.existsByTestSetCodeAndTestSetIdNot(request.getTestSetCode().trim(), id)) {

				return new ResponseEntity("Test code already exists", HttpStatus.CONFLICT.value(), null);
			}

			if (request.getSetNo() != null && !request.getSetNo().trim().isEmpty()
					&& testSetRepository.existsBySetNoAndTestSetIdNot(request.getSetNo().trim(), id)) {

				return new ResponseEntity("Set number already exists", HttpStatus.CONFLICT.value(), null);
			}

			Training training = null;

			if (request.getTrainingId() != null) {

				if (request.getTrainingId() <= 0) {

					return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
				}

				Optional<Training> optionalTraining = trainingRepository.findById(request.getTrainingId());

				if (optionalTraining.isEmpty()) {

					return new ResponseEntity("Training not found with id: " + request.getTrainingId(),
							HttpStatus.NOT_FOUND.value(), null);
				}

				training = optionalTraining.get();
			}

			if (request.getStartDateTime() != null && request.getEndDateTime() != null
					&& request.getEndDateTime().isBefore(request.getStartDateTime())) {

				return new ResponseEntity("End date time cannot be before start date time",
						HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getPassingPercentage() != null && (request.getPassingPercentage().doubleValue() < 0
					|| request.getPassingPercentage().doubleValue() > 100)) {

				return new ResponseEntity("Passing percentage must be between 0 and 100",
						HttpStatus.BAD_REQUEST.value(), null);
			}

			TestSet testSet = optionalTestSet.get();

			testSet.setTestName(request.getTestName().trim());

			testSet.setTestDescription(request.getTestDescription());

			testSet.setTestSetCode(request.getTestSetCode() != null ? request.getTestSetCode().trim() : null);

			testSet.setSetNo(request.getSetNo() != null ? request.getSetNo().trim() : null);

			testSet.setTraining(training);

			testSet.setStartDateTime(request.getStartDateTime());

			testSet.setEndDateTime(request.getEndDateTime());

			if (request.getTimeLimitMinutes() != null) {
				testSet.setTimeLimitMinutes(request.getTimeLimitMinutes());
			}

			if (request.getPassingPercentage() != null) {
				testSet.setPassingPercentage(request.getPassingPercentage());
			}

			if (request.getShuffleQuestions() != null) {
				testSet.setShuffleQuestions(request.getShuffleQuestions());
			}

			if (request.getShuffleOptions() != null) {
				testSet.setShuffleOptions(request.getShuffleOptions());
			}

			if (request.getIsActive() != null) {
				testSet.setIsActive(request.getIsActive());
			}

			testSet.setUpdatedAt(LocalDateTime.now());

			TestSet updatedTestSet = testSetRepository.save(testSet);

			return new ResponseEntity("Test set updated successfully", HttpStatus.OK.value(),
					mapToResponse(updatedTestSet));

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("Test set already exists or violates database constraints",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity deleteTestSet(Long id) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid test set id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestSet> optionalTestSet = testSetRepository.findById(id);

			if (optionalTestSet.isEmpty()) {
				return new ResponseEntity("Test set not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}
			String testSetCode = optionalTestSet.get().getTestSetCode();
			testSetRepository.delete(optionalTestSet.get());

			if (testSetCode != null && !testSetCode.trim().isEmpty()) {
				try {
					paramService.updateCodeValueOnDelete(testSetCode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return new ResponseEntity("Test set deleted successfully", HttpStatus.OK.value(), null);

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("Cannot delete test set because it is being used", HttpStatus.CONFLICT.value(),
					null);

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