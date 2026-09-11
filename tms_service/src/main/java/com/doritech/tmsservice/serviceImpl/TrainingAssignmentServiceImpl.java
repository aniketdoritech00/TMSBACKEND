package com.doritech.tmsservice.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
import com.doritech.tmsservice.enums.AssignmentStatus;
import com.doritech.tmsservice.erp.entity.UserMaster;
import com.doritech.tmsservice.erp.repository.UserMasterRepository;
import com.doritech.tmsservice.request.TrainingAssignmentRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.TrainingAssignmentResponse;
import com.doritech.tmsservice.service.TrainingAssignmentService;
import com.doritech.tmsservice.tms.entity.Batch;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.Training;
import com.doritech.tmsservice.tms.entity.TrainingAssignment;
import com.doritech.tmsservice.tms.repository.BatchRepository;
import com.doritech.tmsservice.tms.repository.TrainingAssignmentRepository;
import com.doritech.tmsservice.tms.repository.TrainingRepository;

@Service
public class TrainingAssignmentServiceImpl implements TrainingAssignmentService {

	private final TrainingAssignmentRepository trainingAssignmentRepository;

	private final TrainingRepository trainingRepository;

	private final UserMasterRepository userRepository;

	private final BatchRepository batchRepository;

	public TrainingAssignmentServiceImpl(TrainingAssignmentRepository trainingAssignmentRepository,
			TrainingRepository trainingRepository, UserMasterRepository userRepository,
			BatchRepository batchRepository) {

		this.trainingAssignmentRepository = trainingAssignmentRepository;
		this.trainingRepository = trainingRepository;
		this.userRepository = userRepository;
		this.batchRepository = batchRepository;
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity createTrainingAssignment(TrainingAssignmentRequest request) {

		try {

			if (request == null) {

				return new ResponseEntity("Training assignment data is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTrainingId() == null || request.getTrainingId() <= 0) {

				return new ResponseEntity("Valid training id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Training training = trainingRepository.findById(request.getTrainingId()).orElse(null);

			if (training == null) {

				return new ResponseEntity("Training not found", HttpStatus.NOT_FOUND.value(), null);
			}

			UserMaster user = null;

			if (request.getUserId() != null) {

				Integer userIdInt;

				try {

					userIdInt = Math.toIntExact(request.getUserId());

				} catch (ArithmeticException e) {

					return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
				}

				user = userRepository.findById(userIdInt).orElse(null);

				if (user == null) {

					return new ResponseEntity("User not found", HttpStatus.NOT_FOUND.value(), null);
				}

				boolean alreadyExists = trainingAssignmentRepository
						.existsByTraining_TrainingIdAndUserId(request.getTrainingId(), request.getUserId());

				if (alreadyExists) {

					return new ResponseEntity("Training is already assigned to this user", HttpStatus.CONFLICT.value(),
							null);
				}
			}

			Long currentUserIdLong = CurrentUser.getUserId();

			if (currentUserIdLong == null || currentUserIdLong <= 0) {

				return new ResponseEntity("Unable to identify current user", HttpStatus.UNAUTHORIZED.value(), null);
			}

			Integer currentUserId;

			try {

				currentUserId = Math.toIntExact(currentUserIdLong);

			} catch (ArithmeticException e) {

				return new ResponseEntity("Invalid current user id", HttpStatus.UNAUTHORIZED.value(), null);
			}

			UserMaster assignedBy = userRepository.findById(currentUserId).orElse(null);

			if (assignedBy == null) {

				return new ResponseEntity("Assigning user not found", HttpStatus.NOT_FOUND.value(), null);
			}

			Batch batch = null;

			if (request.getBatchId() != null) {

				if (request.getBatchId() <= 0) {

					return new ResponseEntity("Invalid batch id", HttpStatus.BAD_REQUEST.value(), null);
				}

				batch = batchRepository.findById(request.getBatchId()).orElse(null);

				if (batch == null) {

					return new ResponseEntity("Batch not found", HttpStatus.NOT_FOUND.value(), null);
				}
			}

			TrainingAssignment trainingAssignment = new TrainingAssignment();

			trainingAssignment.setTraining(training);

			if (user != null) {

				trainingAssignment.setUserId(user.getUserId().longValue());

			} else {

				trainingAssignment.setUserId(null);
			}

			trainingAssignment.setBatch(batch);

			trainingAssignment.setAssignedBy(assignedBy.getUserId().longValue());

			trainingAssignment.setAssignedAt(LocalDateTime.now());

			trainingAssignment.setDueDate(request.getDueDate());

			trainingAssignment.setStatus(AssignmentStatus.NOT_STARTED);

			trainingAssignment.setProgressPercentage(new BigDecimal("0.00"));

			trainingAssignment.setIsPassed(false);

			trainingAssignment.setCertificateGenerated(false);

			trainingAssignment.setAttemptedQuestions(0);

			trainingAssignment.setCorrectAnswers(0);

			trainingAssignment.setWrongAnswers(0);

			trainingAssignment.setTotalQuestions(0);

			trainingAssignment.setStartedAt(null);

			TrainingAssignment savedAssignment = trainingAssignmentRepository.save(trainingAssignment);

			TrainingAssignmentResponse response = convertToResponse(savedAssignment);

			return new ResponseEntity("Training assigned successfully", HttpStatus.CREATED.value(), response);

		} catch (DataIntegrityViolationException e) {

			return new ResponseEntity("Training is already assigned to this user", HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to create training assignment: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTrainingAssignmentById(Long id) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			TrainingAssignment trainingAssignment = trainingAssignmentRepository.findById(id).orElse(null);

			if (trainingAssignment == null) {

				return new ResponseEntity("Training assignment not found", HttpStatus.NOT_FOUND.value(), null);
			}

			TrainingAssignmentResponse response = convertToResponse(trainingAssignment);

			return new ResponseEntity("Training assignment fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch training assignment: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllTrainingAssignments() {

		try {

			List<TrainingAssignment> assignments = trainingAssignmentRepository.findAll();

			List<TrainingAssignmentResponse> responseList = assignments.stream().map(this::convertToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Training assignments fetched successfully", HttpStatus.OK.value(), responseList);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch training assignments: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTrainingAssignmentsByTrainingId(Long trainingId) {

		try {

			if (trainingId == null || trainingId <= 0) {

				return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Training training = trainingRepository.findById(trainingId).orElse(null);

			if (training == null) {

				return new ResponseEntity("Training not found", HttpStatus.NOT_FOUND.value(), null);
			}

			List<TrainingAssignment> assignments = trainingAssignmentRepository.findByTraining_TrainingId(trainingId);

			List<TrainingAssignmentResponse> responseList = assignments.stream().map(this::convertToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Training assignments fetched successfully", HttpStatus.OK.value(), responseList);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch training assignments: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTrainingAssignmentsByUserId(Long userId) {
		try {
			if (userId == null || userId <= 0) {
				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Integer userIdInt;
			try {
				userIdInt = Math.toIntExact(userId);
			} catch (ArithmeticException e) {
				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
			}

			UserMaster user = userRepository.findById(userIdInt).orElse(null);
			if (user == null) {
				return new ResponseEntity("User not found", HttpStatus.NOT_FOUND.value(), null);
			}

			List<TrainingAssignment> assignments = trainingAssignmentRepository.findAssignmentsByUserIdOrBatch(userId);

			List<TrainingAssignmentResponse> responseList = assignments.stream().map(this::convertToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Training assignments fetched successfully", HttpStatus.OK.value(), responseList);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Failed to fetch training assignments: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity updateTrainingAssignment(Long id, TrainingAssignmentRequest request) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request == null) {

				return new ResponseEntity("Training assignment data is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTrainingId() == null || request.getTrainingId() <= 0) {

				return new ResponseEntity("Valid training id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getUserId() == null || request.getUserId() <= 0) {

				return new ResponseEntity("Valid user id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			TrainingAssignment trainingAssignment = trainingAssignmentRepository.findById(id).orElse(null);

			if (trainingAssignment == null) {

				return new ResponseEntity("Training assignment not found", HttpStatus.NOT_FOUND.value(), null);
			}

			Training training = trainingRepository.findById(request.getTrainingId()).orElse(null);

			if (training == null) {

				return new ResponseEntity("Training not found", HttpStatus.NOT_FOUND.value(), null);
			}

			Integer userIdInt;

			try {

				userIdInt = Math.toIntExact(request.getUserId());

			} catch (ArithmeticException e) {

				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
			}

			UserMaster user = userRepository.findById(userIdInt).orElse(null);

			if (user == null) {

				return new ResponseEntity("User not found", HttpStatus.NOT_FOUND.value(), null);
			}

			boolean duplicate = trainingAssignmentRepository
					.existsByTraining_TrainingIdAndUserIdAndTrainingAssignmentIdNot(request.getTrainingId(),
							request.getUserId(), id);

			if (duplicate) {

				return new ResponseEntity("Training is already assigned to this user", HttpStatus.CONFLICT.value(),
						null);
			}

			Batch batch = null;

			if (request.getBatchId() != null) {

				if (request.getBatchId() <= 0) {

					return new ResponseEntity("Invalid batch id", HttpStatus.BAD_REQUEST.value(), null);
				}

				batch = batchRepository.findById(request.getBatchId()).orElse(null);

				if (batch == null) {

					return new ResponseEntity("Batch not found", HttpStatus.NOT_FOUND.value(), null);
				}
			}

			trainingAssignment.setTraining(training);

			trainingAssignment.setUserId(user.getUserId().longValue());

			trainingAssignment.setBatch(batch);

			trainingAssignment.setDueDate(request.getDueDate());

			TrainingAssignment updatedAssignment = trainingAssignmentRepository.save(trainingAssignment);

			TrainingAssignmentResponse response = convertToResponse(updatedAssignment);

			return new ResponseEntity("Training assignment updated successfully", HttpStatus.OK.value(), response);

		} catch (DataIntegrityViolationException e) {

			return new ResponseEntity("Training is already assigned to this user", HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to update training assignment: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity deleteTrainingAssignment(Long id) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			TrainingAssignment trainingAssignment = trainingAssignmentRepository.findById(id).orElse(null);

			if (trainingAssignment == null) {

				return new ResponseEntity("Training assignment not found", HttpStatus.NOT_FOUND.value(), null);
			}

			trainingAssignmentRepository.delete(trainingAssignment);

			return new ResponseEntity("Training assignment deleted successfully", HttpStatus.OK.value(), null);

		} catch (DataIntegrityViolationException e) {

			return new ResponseEntity("Training assignment cannot be deleted because it is being used",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to delete training assignment: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllTrainingAssignments(int page, int size, String sortBy, String sortDir) {

		try {

			if (page < 0) {

				return new ResponseEntity("Page number cannot be negative", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (size <= 0) {

				return new ResponseEntity("Page size must be greater than zero", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {

				sortBy = "trainingAssignmentId";
			}

			if (sortDir == null || sortDir.trim().isEmpty()) {

				sortDir = "desc";
			}

			Sort.Direction direction;

			try {

				direction = Sort.Direction.fromString(sortDir);

			} catch (IllegalArgumentException e) {

				return new ResponseEntity("Invalid sort direction. Use asc or desc", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<TrainingAssignment> assignmentPage = trainingAssignmentRepository.findAll(pageable);

			List<TrainingAssignmentResponse> responseList = assignmentPage.getContent().stream()
					.map(this::convertToResponse).collect(Collectors.toList());

			return new ResponseEntity("Training assignments fetched successfully", HttpStatus.OK.value(), responseList);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch training assignments: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTrainingAssignmentsByTrainingId(Long trainingId, int page, int size, String sortBy,
			String sortDir) {

		try {

			if (trainingId == null || trainingId <= 0) {

				return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
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

				sortBy = "trainingAssignmentId";
			}

			if (sortDir == null || sortDir.trim().isEmpty()) {

				sortDir = "desc";
			}

			Sort.Direction direction;

			try {

				direction = Sort.Direction.fromString(sortDir);

			} catch (IllegalArgumentException e) {

				return new ResponseEntity("Invalid sort direction. Use asc or desc", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<TrainingAssignment> assignmentPage = trainingAssignmentRepository.findByTraining_TrainingId(trainingId,
					pageable);

			List<TrainingAssignmentResponse> responseList = assignmentPage.getContent().stream()
					.map(this::convertToResponse).collect(Collectors.toList());

			return new ResponseEntity("Training assignments fetched successfully", HttpStatus.OK.value(), responseList);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch training assignments: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTrainingAssignmentsByUserId(Long userId, int page, int size, String sortBy,
			String sortDir) {

		try {

			if (userId == null || userId <= 0) {

				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Integer userIdInt;

			try {

				userIdInt = Math.toIntExact(userId);

			} catch (ArithmeticException e) {

				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
			}

			UserMaster user = userRepository.findById(userIdInt).orElse(null);

			if (user == null) {

				return new ResponseEntity("User not found", HttpStatus.NOT_FOUND.value(), null);
			}

			if (page < 0) {

				return new ResponseEntity("Page number cannot be negative", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (size <= 0) {

				return new ResponseEntity("Page size must be greater than zero", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {

				sortBy = "trainingAssignmentId";
			}

			if (sortDir == null || sortDir.trim().isEmpty()) {

				sortDir = "desc";
			}

			Sort.Direction direction;

			try {

				direction = Sort.Direction.fromString(sortDir);

			} catch (IllegalArgumentException e) {

				return new ResponseEntity("Invalid sort direction. Use asc or desc", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<TrainingAssignment> assignmentPage = trainingAssignmentRepository
					.findAssignmentsByUserIdOrBatch(userId, pageable);

			List<TrainingAssignmentResponse> responseList = assignmentPage.getContent().stream()
					.map(this::convertToResponse).collect(Collectors.toList());

			PageResponse<TrainingAssignmentResponse> pageResponse = new PageResponse<>(responseList,
					assignmentPage.getNumber(), assignmentPage.getSize(), assignmentPage.getTotalElements(),
					assignmentPage.getTotalPages(), assignmentPage.isLast());

			return new ResponseEntity("Training assignments fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch training assignments: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private TrainingAssignmentResponse convertToResponse(TrainingAssignment assignment) {

		TrainingAssignmentResponse response = new TrainingAssignmentResponse();

		response.setTrainingAssignmentId(assignment.getTrainingAssignmentId());

		if (assignment.getTraining() != null) {

			response.setTrainingId(assignment.getTraining().getTrainingId());

			response.setTrainingCode(assignment.getTraining().getTrainingCode());

			response.setTrainingName(assignment.getTraining().getTrainingName());
		}

		response.setUserId(assignment.getUserId());

		if (assignment.getBatch() != null) {

			response.setBatchId(assignment.getBatch().getBatchId());

			response.setBatchName(assignment.getBatch().getBatchName());
		}

		response.setAssignedBy(assignment.getAssignedBy());

		if (assignment.getAssignedBy() != null) {
			UserMaster assignedByUser = userRepository.findById(assignment.getAssignedBy().intValue()).orElse(null);

			if (assignedByUser != null) {
				response.setAssignedByName(
						userRepository.findEmployeeNameByUserId(assignment.getAssignedBy().intValue()));
			}
		}

		response.setAssignedAt(assignment.getAssignedAt());

		response.setDueDate(assignment.getDueDate());

		response.setStatus(assignment.getStatus());

		response.setProgressPercentage(assignment.getProgressPercentage());

		response.setCompletionDate(assignment.getCompletionDate());

		response.setFinalScore(assignment.getFinalScore());

		response.setIsPassed(assignment.getIsPassed());

		response.setCertificateGenerated(assignment.getCertificateGenerated());

		response.setAttemptedQuestions(assignment.getAttemptedQuestions());

		response.setCorrectAnswers(assignment.getCorrectAnswers());

		response.setWrongAnswers(assignment.getWrongAnswers());

		response.setTotalQuestions(assignment.getTotalQuestions());

		response.setRemarks(assignment.getRemarks());

		response.setStartedAt(assignment.getStartedAt());

		return response;
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTrainingAssignmentsForCurrentUser() {

		try {
			Long userId = CurrentUser.getUserId();
			if (userId == null || userId <= 0) {
				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Integer userIdInt;
			try {
				userIdInt = Math.toIntExact(userId);
			} catch (ArithmeticException e) {
				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
			}

			UserMaster user = userRepository.findById(userIdInt).orElse(null);
			if (user == null) {
				return new ResponseEntity("User not found", HttpStatus.NOT_FOUND.value(), null);
			}

			List<TrainingAssignment> assignments = trainingAssignmentRepository.findAssignmentsByUserIdOrBatch(userId);

			List<TrainingAssignmentResponse> responseList = assignments.stream().map(this::convertToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Training assignments fetched successfully", HttpStatus.OK.value(), responseList);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Failed to fetch training assignments: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTrainingAssignmentsForCurrentUser(int page, int size, String sortBy, String sortDir) {
		try {
			Long userId = CurrentUser.getUserId();
			if (userId == null || userId <= 0) {

				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Integer userIdInt;

			try {

				userIdInt = Math.toIntExact(userId);

			} catch (ArithmeticException e) {

				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
			}

			UserMaster user = userRepository.findById(userIdInt).orElse(null);

			if (user == null) {

				return new ResponseEntity("User not found", HttpStatus.NOT_FOUND.value(), null);
			}

			if (page < 0) {

				return new ResponseEntity("Page number cannot be negative", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (size <= 0) {

				return new ResponseEntity("Page size must be greater than zero", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {

				sortBy = "trainingAssignmentId";
			}

			if (sortDir == null || sortDir.trim().isEmpty()) {

				sortDir = "desc";
			}

			Sort.Direction direction;

			try {

				direction = Sort.Direction.fromString(sortDir);

			} catch (IllegalArgumentException e) {

				return new ResponseEntity("Invalid sort direction. Use asc or desc", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<TrainingAssignment> assignmentPage = trainingAssignmentRepository
					.findAssignmentsByUserIdOrBatch(userId, pageable);

			List<TrainingAssignmentResponse> responseList = assignmentPage.getContent().stream()
					.map(this::convertToResponse).collect(Collectors.toList());

			PageResponse<TrainingAssignmentResponse> pageResponse = new PageResponse<>(responseList,
					assignmentPage.getNumber(), assignmentPage.getSize(), assignmentPage.getTotalElements(),
					assignmentPage.getTotalPages(), assignmentPage.isLast());

			return new ResponseEntity("Training assignments fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch training assignments: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

}