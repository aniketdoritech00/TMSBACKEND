package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.entity.Training;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceAlreadyExistsException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.TrainingRepository;
import com.doritech.tmsservice.request.TrainingRequest;
import com.doritech.tmsservice.response.TrainingResponse;
import com.doritech.tmsservice.service.TrainingService;

@Service
public class TrainingServiceImpl implements TrainingService {

	private static final Logger log = LoggerFactory.getLogger(TrainingServiceImpl.class);

	@Autowired
	private TrainingRepository trainingRepository;

	@Override
	public ResponseEntity createTraining(TrainingRequest request) {

		log.info("createTraining :: request received for code={}", request.getTrainingCode());

		if (trainingRepository.existsByTrainingCode(request.getTrainingCode())) {
			log.error("createTraining :: duplicate code={}", request.getTrainingCode());
			throw new ResourceAlreadyExistsException("Training already exists with code: " + request.getTrainingCode());
		}

		Training training = new Training();
		training.setTrainingCode(request.getTrainingCode());
		training.setTrainingName(request.getTrainingName());
		training.setTrainingDescription(request.getTrainingDescription());

		if (request.getTrainingType() != null) {
			try {
				training.setTrainingType(Training.TrainingType.valueOf(request.getTrainingType()));
			} catch (IllegalArgumentException e) {
				log.error("createTraining :: invalid trainingType={}", request.getTrainingType());
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
				training.setStatus(Training.Status.valueOf(request.getStatus()));
			} catch (IllegalArgumentException e) {
				log.error("createTraining :: invalid status={}", request.getStatus());
				throw new BadRequestException("Invalid status. Must be one of DRAFT, PUBLISHED, ARCHIVED");
			}
		}

		training.setCreatedBy(request.getCreatedBy());

		Training saved;
		try {
			saved = trainingRepository.save(training);
		} catch (Exception e) {
			log.error("createTraining :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving training");
		}

		log.info("createTraining :: saved successfully with id={}", saved.getTrainingId());

		return new ResponseEntity("Training saved successfully", HttpStatus.CREATED.value(), mapToResponse(saved));
	}

	@Override
	public ResponseEntity getTrainingById(Long id) {

		log.info("getTrainingById :: request received for id={}", id);

		if (id == null) {
			log.error("getTrainingById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		Training training = trainingRepository.findById(id).orElseThrow(() -> {
			log.error("getTrainingById :: not found for id={}", id);
			return new ResourceNotFoundException("Training not found with id: " + id);
		});

		log.info("getTrainingById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), mapToResponse(training));
	}

	@Override
	public ResponseEntity getAllTraining(int page, int size, String sortBy, String sortDir) {

		log.info("getAllTraining :: request received with page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy,
				sortDir);

		if (page < 0) {
			log.error("getAllTraining :: page cannot be negative");
			throw new BadRequestException("Page number can not be negative");
		}

		if (size <= 0) {
			log.error("getAllTraining :: size must be greater than 0");
			throw new BadRequestException("Page size must be greater than 0");
		}

		if (size > 100) {
			log.error("getAllTraining :: size exceeds max limit={}", size);
			throw new BadRequestException("Page size can not exceed 100");
		}

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<Training> trainingPage;
		try {
			trainingPage = trainingRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			log.error("getAllTraining :: invalid sort field={}", sortBy);
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			log.error("getAllTraining :: error while fetching - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while fetching trainings");
		}

		List<TrainingResponse> responseList = trainingPage.getContent().stream().map(this::mapToResponse)
				.collect(Collectors.toList());

		Map<String, Object> pageData = new LinkedHashMap<>();
		pageData.put("content", responseList);
		pageData.put("pageNumber", trainingPage.getNumber());
		pageData.put("pageSize", trainingPage.getSize());
		pageData.put("totalElements", trainingPage.getTotalElements());
		pageData.put("totalPages", trainingPage.getTotalPages());
		pageData.put("isLast", trainingPage.isLast());

		log.info("getAllTraining :: {} of {} trainings fetched successfully", responseList.size(),
				trainingPage.getTotalElements());

		return new ResponseEntity("Training fetch successfully", HttpStatus.OK.value(), pageData);
	}

	@Override
	public ResponseEntity deleteTraining(Long id) {

		log.info("deleteTraining :: request received for id={}", id);

		if (id == null) {
			log.error("deleteTraining :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		Training training = trainingRepository.findById(id).orElseThrow(() -> {
			log.error("deleteTraining :: not found for id={}", id);
			return new ResourceNotFoundException("Training not found with id: " + id);
		});

		try {
			trainingRepository.delete(training);
		} catch (Exception e) {
			log.error("deleteTraining :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Cannot delete training, it may be linked to other records");
		}

		log.info("deleteTraining :: deleted successfully for id={}", id);

		return new ResponseEntity("Training deleted successfully", HttpStatus.OK.value(), null);
	}

	@Override
	public ResponseEntity publishTraining(Long id) {

		log.info("publishTraining :: request received for id={}", id);

		if (id == null) {
			log.error("publishTraining :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		Training training = trainingRepository.findById(id).orElseThrow(() -> {
			log.error("publishTraining :: not found for id={}", id);
			return new ResourceNotFoundException("Training not found with id: " + id);
		});

		if (training.getStatus() == Training.Status.PUBLISHED) {
			log.error("publishTraining :: already published for id={}", id);
			throw new BadRequestException("Training is already published");
		}

		training.setStatus(Training.Status.PUBLISHED);
		training.setPublishedAt(LocalDateTime.now());

		Training saved;
		try {
			saved = trainingRepository.save(training);
		} catch (Exception e) {
			log.error("publishTraining :: error while publishing - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while publishing training");
		}

		log.info("publishTraining :: published successfully for id={}", id);

		return new ResponseEntity("Training published successfully", HttpStatus.OK.value(), mapToResponse(saved));
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