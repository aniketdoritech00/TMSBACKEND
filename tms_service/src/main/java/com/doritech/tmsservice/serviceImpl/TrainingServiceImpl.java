package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.enums.TrainingStatus;
import com.doritech.tmsservice.enums.TrainingType;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.request.TrainingRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.TrainingResponse;
import com.doritech.tmsservice.service.ParamService;
import com.doritech.tmsservice.service.TrainingService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.Training;
import com.doritech.tmsservice.tms.repository.TrainingRepository;

@Service
public class TrainingServiceImpl implements TrainingService {

	private final TrainingRepository trainingRepository;
	private final ParamService paramService;


	public TrainingServiceImpl(TrainingRepository trainingRepository, ParamService paramService) {
		this.trainingRepository = trainingRepository;
		this.paramService = paramService;
	}

	@Override
	public ResponseEntity createTraining(TrainingRequest request) {
		try {
			Long currentUserId = CurrentUser.getUserId();
			if (trainingRepository.existsByTrainingCode(request.getTrainingCode())) {
				return new ResponseEntity("Training code already exists!", HttpStatus.CONFLICT.value(), null);
			}

			Training training = mapToEntity(request);
			training.setCreatedBy(currentUserId);
			Training savedTraining = trainingRepository.save(training);
			
			if (savedTraining.getTrainingCode() != null
					&& !savedTraining.getTrainingCode().trim().isEmpty()) {

				try {
					paramService.updateCodeValue(savedTraining.getTrainingCode());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return new ResponseEntity("Training created successfully!", HttpStatus.CREATED.value(), savedTraining);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getTrainingById(Long id) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<Training> optionalTraining = trainingRepository.findById(id);
			if (optionalTraining.isEmpty()) {
				return new ResponseEntity("Training not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			TrainingResponse response = mapToResponse(optionalTraining.get());
			return new ResponseEntity("Training fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getAllTraining(int page, int size, String sortBy, String sortDir) {
		if (page < 0) {
			throw new BadRequestException("Page number cannot be negative");
		}
		if (size <= 0) {
			throw new BadRequestException("Page size must be greater than 0");
		}
		if (size > 100) {
			throw new BadRequestException("Page size cannot exceed 100");
		}
		if (!"asc".equalsIgnoreCase(sortDir) && !"desc".equalsIgnoreCase(sortDir)) {
			throw new BadRequestException("Sort direction must be either 'asc' or 'desc'");
		}

		Sort sort = "desc".equalsIgnoreCase(sortDir) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);
		Page<Training> trainingPage;
		try {
			trainingPage = trainingRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			throw new DatabaseOperationException("Something went wrong while fetching trainings");
		}

		List<TrainingResponse> responseList = trainingPage.getContent().stream().map(this::mapToResponse).toList();
		PageResponse<TrainingResponse> pageResponse = new PageResponse<>(responseList, trainingPage.getNumber(),
				trainingPage.getSize(), trainingPage.getTotalElements(), trainingPage.getTotalPages(),
				trainingPage.isLast());
		return new ResponseEntity("Trainings fetched successfully", HttpStatus.OK.value(), pageResponse);
	}

	@Override
	public ResponseEntity getAllTraining() {
		try {
			List<Training> trainings = trainingRepository.findAll();
			if (trainings.isEmpty()) {
				return new ResponseEntity("Training not found", HttpStatus.NOT_FOUND.value(), null);
			}

			List<TrainingResponse> trainingResponse = trainings.stream().map(this::mapToResponse).toList();
			return new ResponseEntity("Trainings fetched successfully", HttpStatus.OK.value(), trainingResponse);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity updateTraining(Long id, TrainingRequest request) {
		try {
			Long currentUserId = CurrentUser.getUserId();
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<Training> optionalTraining = trainingRepository.findById(id);
			if (optionalTraining.isEmpty()) {
				return new ResponseEntity("Training not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			Training training = optionalTraining.get();
			if (trainingRepository.existsByTrainingCodeAndTrainingIdNot(request.getTrainingCode(), id)) {
				return new ResponseEntity("Training code already exists!", HttpStatus.CONFLICT.value(), null);
			}

			training.setTrainingCode(request.getTrainingCode());
			training.setTrainingName(request.getTrainingName());
			training.setTrainingDescription(request.getTrainingDescription());

			if (request.getTrainingType() != null) {
				try {
					training.setTrainingType(TrainingType.valueOf(request.getTrainingType()));
				} catch (IllegalArgumentException e) {
					return new ResponseEntity("Invalid training type. Must be one of REGULAR, MANDATORY, REFRESHER",
							HttpStatus.BAD_REQUEST.value(), null);
				}
			} else {
				training.setTrainingType(null);
			}

			training.setTrainingCategoryId(request.getTrainingCategoryId());
			training.setTrainerId(request.getTrainerId());
			training.setTrainingDurationDays(request.getTrainingDurationDays());
			training.setPassingPercentage(request.getPassingPercentage());
			training.setIsMandatory(request.getIsMandatory());
			training.setParentTrainingId(request.getParentTrainingId());
			training.setHasAssessment(request.getHasAssessment());
			training.setHasVideoAssessment(request.getHasVideoAssessment());
			if (request.getStatus() != null) {
				try {
					training.setStatus(TrainingStatus.valueOf(request.getStatus()));
				} catch (IllegalArgumentException e) {
					return new ResponseEntity("Invalid status. Must be one of DRAFT, PUBLISHED, ARCHIVED",
							HttpStatus.BAD_REQUEST.value(), null);
				}
			}

			training.setUpdatedBy(currentUserId);
			training.setUpdatedAt(LocalDateTime.now());
			Training updatedTraining = trainingRepository.save(training);

			return new ResponseEntity("Training updated successfully!", HttpStatus.OK.value(), updatedTraining);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity deleteTraining(Long id) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Training training = trainingRepository.findById(id).orElse(null);
			if (training == null) {
				return new ResponseEntity("Training not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			String trainingCode = training.getTrainingCode();
			trainingRepository.delete(training);
			trainingRepository.flush();
			
			if (trainingCode != null && !trainingCode.trim().isEmpty()) {
				try {
					paramService.updateCodeValueOnDelete(trainingCode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return new ResponseEntity("Training deleted successfully", HttpStatus.OK.value(), null);

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			return new ResponseEntity("Training cannot be deleted because it is linked to other records",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Something went wrong while deleting training",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity publishTraining(Long id) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Training training = trainingRepository.findById(id).orElse(null);
			if (training == null) {
				return new ResponseEntity("Training not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			if (training.getStatus() == TrainingStatus.PUBLISHED) {
				return new ResponseEntity("Training is already published", HttpStatus.CONFLICT.value(), null);
			}

			training.setStatus(TrainingStatus.PUBLISHED);
			training.setPublishedAt(LocalDateTime.now());
			training.setUpdatedBy(CurrentUser.getUserId());
			training.setUpdatedAt(LocalDateTime.now());
			Training savedTraining = trainingRepository.save(training);
			return new ResponseEntity("Training published successfully!", HttpStatus.OK.value(), savedTraining);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getTrainingByCategoryId(Long trainingCategoryId) {


		try {
			if (trainingCategoryId == null || trainingCategoryId <= 0) {
				return new ResponseEntity(
						"Invalid training category id",
						HttpStatus.BAD_REQUEST.value(),
						null
				);
			}

			List<Training> trainings = trainingRepository.findByTrainingCategoryId(trainingCategoryId);

			if (trainings.isEmpty()) {
				return new ResponseEntity(
						"No trainings found for training category id: " + trainingCategoryId,
						HttpStatus.NOT_FOUND.value(),
						null
				);
			}

			List<TrainingResponse> responseList = trainings.stream()
					.map(this::mapToResponse)
					.toList();


			return new ResponseEntity(
					"Trainings fetched successfully",
					HttpStatus.OK.value(),
					responseList
			);

		} catch (Exception e) {

			return new ResponseEntity(
					"Internal server error!",
					HttpStatus.INTERNAL_SERVER_ERROR.value(),
					null
			);
		}
	}
	
	private Training mapToEntity(TrainingRequest request) {
		Training training = new Training();
		training.setTrainingCode(request.getTrainingCode());
		training.setTrainingName(request.getTrainingName());
		training.setTrainingDescription(request.getTrainingDescription());
		if (request.getTrainingType() != null) {
			try {
				training.setTrainingType(TrainingType.valueOf(request.getTrainingType()));
			} catch (IllegalArgumentException e) {
				throw new BadRequestException("Invalid training type. Must be one of REGULAR, MANDATORY, REFRESHER");
			}
		}

		training.setTrainingCategoryId(request.getTrainingCategoryId());
		training.setTrainerId(request.getTrainerId());
		training.setTrainingDurationDays(request.getTrainingDurationDays());
		training.setPassingPercentage(request.getPassingPercentage());
		training.setIsMandatory(request.getIsMandatory());
		training.setParentTrainingId(request.getParentTrainingId());
		training.setHasAssessment(request.getHasAssessment());
		training.setHasVideoAssessment(request.getHasVideoAssessment());
		if (request.getStatus() != null) {
			try {
				training.setStatus(TrainingStatus.valueOf(request.getStatus()));
			} catch (IllegalArgumentException e) {
				throw new BadRequestException("Invalid status. Must be one of DRAFT, PUBLISHED, ARCHIVED");
			}
		}

		training.setCreatedAt(LocalDateTime.now());
		training.setUpdatedAt(LocalDateTime.now());
		return training;
	}

	private TrainingResponse mapToResponse(Training entity) {
		TrainingResponse response = new TrainingResponse();
		response.setTrainingId(entity.getTrainingId());
		response.setTrainingCode(entity.getTrainingCode());
		response.setTrainingName(entity.getTrainingName());
		response.setTrainingDescription(entity.getTrainingDescription());
		response.setTrainingType(entity.getTrainingType() != null ? entity.getTrainingType().name() : null);
		response.setTrainingCategoryId(entity.getTrainingCategoryId());
		response.setTrainerId(entity.getTrainerId());
		response.setTrainingDurationDays(entity.getTrainingDurationDays());
		response.setPassingPercentage(entity.getPassingPercentage());
		response.setIsMandatory(entity.getIsMandatory());
		response.setParentTrainingId(entity.getParentTrainingId());
		response.setHasAssessment(entity.getHasAssessment());
		response.setHasVideoAssessment(entity.getHasVideoAssessment());
		response.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
		response.setCreatedBy(entity.getCreatedBy());
		response.setCreatedAt(entity.getCreatedAt());
		response.setUpdatedAt(entity.getUpdatedAt());
		response.setPublishedAt(entity.getPublishedAt());
		return response;
	}
}
