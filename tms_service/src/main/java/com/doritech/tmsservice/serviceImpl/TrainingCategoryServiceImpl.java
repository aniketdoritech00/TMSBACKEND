package com.doritech.tmsservice.serviceImpl;

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
import com.doritech.tmsservice.entity.TrainingCategory;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceAlreadyExistsException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.TrainingCategoryRepository;
import com.doritech.tmsservice.request.TrainingCategoryRequest;
import com.doritech.tmsservice.response.TrainingCategoryResponse;
import com.doritech.tmsservice.service.TrainingCategoryService;

@Service
public class TrainingCategoryServiceImpl implements TrainingCategoryService {

	private static final Logger log = LoggerFactory.getLogger(TrainingCategoryServiceImpl.class);

	@Autowired
	private TrainingCategoryRepository trainingCategoryRepository;

	@Override
	public ResponseEntity createTrainingCategory(TrainingCategoryRequest request) {

		log.info("createTrainingCategory :: request received for name={}", request.getCategoryName());

		if (request.getCategoryCode() != null
				&& trainingCategoryRepository.existsByCategoryCode(request.getCategoryCode())) {
			log.error("createTrainingCategory :: duplicate code={}", request.getCategoryCode());
			throw new ResourceAlreadyExistsException(
					"Training category already exists with code: " + request.getCategoryCode());
		}

		TrainingCategory category = new TrainingCategory();
		category.setCategoryName(request.getCategoryName());
		category.setCategoryDescription(request.getCategoryDescription());
		category.setCategoryCode(request.getCategoryCode());

		TrainingCategory saved;
		try {
			saved = trainingCategoryRepository.save(category);
		} catch (Exception e) {
			log.error("createTrainingCategory :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving training category");
		}

		log.info("createTrainingCategory :: saved successfully with id={}", saved.getTrainingCategoryId());

		return new ResponseEntity("Training category saved successfully", HttpStatus.CREATED.value(),
				mapToResponse(saved));
	}

	@Override
	public ResponseEntity getTrainingCategoryById(Long id) {

		log.info("getTrainingCategoryById :: request received for id={}", id);

		if (id == null) {
			log.error("getTrainingCategoryById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		TrainingCategory category = trainingCategoryRepository.findById(id).orElseThrow(() -> {
			log.error("getTrainingCategoryById :: not found for id={}", id);
			return new ResourceNotFoundException("Training category not found with id: " + id);
		});

		log.info("getTrainingCategoryById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), mapToResponse(category));
	}

	@Override
	public ResponseEntity getAllTrainingCategory(int page, int size, String sortBy, String sortDir) {

		log.info("getAllTrainingCategory :: request received with page={}, size={}, sortBy={}, sortDir={}", page, size,
				sortBy, sortDir);

		if (page < 0) {
			log.error("getAllTrainingCategory :: page cannot be negative");
			throw new BadRequestException("Page number can not be negative");
		}

		if (size <= 0) {
			log.error("getAllTrainingCategory :: size must be greater than 0");
			throw new BadRequestException("Page size must be greater than 0");
		}

		if (size > 100) {
			log.error("getAllTrainingCategory :: size exceeds max limit={}", size);
			throw new BadRequestException("Page size can not exceed 100");
		}

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<TrainingCategory> categoryPage;
		try {
			categoryPage = trainingCategoryRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			log.error("getAllTrainingCategory :: invalid sort field={}", sortBy);
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			log.error("getAllTrainingCategory :: error while fetching - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while fetching training categories");
		}

		List<TrainingCategoryResponse> responseList = categoryPage.getContent().stream().map(this::mapToResponse)
				.collect(Collectors.toList());

		Map<String, Object> pageData = new LinkedHashMap<>();
		pageData.put("content", responseList);
		pageData.put("pageNumber", categoryPage.getNumber());
		pageData.put("pageSize", categoryPage.getSize());
		pageData.put("totalElements", categoryPage.getTotalElements());
		pageData.put("totalPages", categoryPage.getTotalPages());
		pageData.put("isLast", categoryPage.isLast());

		log.info("getAllTrainingCategory :: {} of {} categories fetched successfully", responseList.size(),
				categoryPage.getTotalElements());

		return new ResponseEntity("Training category fetch successfully", HttpStatus.OK.value(), pageData);
	}

	@Override
	public ResponseEntity deleteTrainingCategory(Long id) {

		log.info("deleteTrainingCategory :: request received for id={}", id);

		if (id == null) {
			log.error("deleteTrainingCategory :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		TrainingCategory category = trainingCategoryRepository.findById(id).orElseThrow(() -> {
			log.error("deleteTrainingCategory :: not found for id={}", id);
			return new ResourceNotFoundException("Training category not found with id: " + id);
		});

		try {
			trainingCategoryRepository.delete(category);
		} catch (Exception e) {
			log.error("deleteTrainingCategory :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Cannot delete training category, it may be linked to other records");
		}

		log.info("deleteTrainingCategory :: deleted successfully for id={}", id);

		return new ResponseEntity("Training category deleted successfully", HttpStatus.OK.value(), null);
	}

	private TrainingCategoryResponse mapToResponse(TrainingCategory entity) {
		TrainingCategoryResponse response = new TrainingCategoryResponse();
		response.setTrainingCategoryId(entity.getTrainingCategoryId());
		response.setCategoryName(entity.getCategoryName());
		response.setCategoryDescription(entity.getCategoryDescription());
		response.setCategoryCode(entity.getCategoryCode());
		response.setCreatedAt(entity.getCreatedAt());
		return response;
	}
}