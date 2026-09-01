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
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.request.TrainingCategoryRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.TrainingCategoryResponse;
import com.doritech.tmsservice.service.ParamService;
import com.doritech.tmsservice.service.TrainingCategoryService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TrainingCategory;
import com.doritech.tmsservice.tms.repository.TrainingCategoryRepository;

@Service
public class TrainingCategoryServiceImpl implements TrainingCategoryService {

	private final TrainingCategoryRepository trainingCategoryRepository;
	private final ParamService paramService;

	public TrainingCategoryServiceImpl(TrainingCategoryRepository trainingCategoryRepository,
			ParamService paramService) {

		this.trainingCategoryRepository = trainingCategoryRepository;
		this.paramService = paramService;
	}

	@Override
	public ResponseEntity createTrainingCategory(TrainingCategoryRequest request) {

		try {

			Long currentUserId = CurrentUser.getUserId();

			// Check duplicate category code
			if (trainingCategoryRepository.existsByCategoryCode(request.getCategoryCode())) {
				return new ResponseEntity("Training category code already exists!", HttpStatus.CONFLICT.value(), null);
			}

			// Check duplicate category name
			if (trainingCategoryRepository.existsByCategoryName(request.getCategoryName())) {
				return new ResponseEntity("Training category name already exists!", HttpStatus.CONFLICT.value(), null);
			}

			TrainingCategory trainingCategory = mapToEntity(request);

			trainingCategory.setCreatedBy(currentUserId);

			TrainingCategory savedTrainingCategory = trainingCategoryRepository.save(trainingCategory);

			// Update parameter/code sequence
			if (savedTrainingCategory.getCategoryCode() != null
					&& !savedTrainingCategory.getCategoryCode().trim().isEmpty()) {

				try {
					paramService.updateCodeValue(savedTrainingCategory.getCategoryCode());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return new ResponseEntity("Training category created successfully!", HttpStatus.CREATED.value(),
					savedTrainingCategory);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getTrainingCategoryById(Long id) {

		try {

			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid training category id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TrainingCategory> optionalTrainingCategory = trainingCategoryRepository.findById(id);

			if (optionalTrainingCategory.isEmpty()) {

				return new ResponseEntity("Training category not found with id: " + id, HttpStatus.NOT_FOUND.value(),
						null);
			}

			TrainingCategoryResponse response = mapToResponse(optionalTrainingCategory.get());

			return new ResponseEntity("Training category fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getAllTrainingCategory(int page, int size, String sortBy, String sortDir) {

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

		Page<TrainingCategory> categoryPage;

		try {

			categoryPage = trainingCategoryRepository.findAll(pageable);

		} catch (PropertyReferenceException e) {

			throw new BadRequestException("Invalid sort field: " + sortBy);

		} catch (Exception e) {

			throw new DatabaseOperationException("Something went wrong while fetching training categories");
		}

		List<TrainingCategoryResponse> responseList = categoryPage.getContent().stream().map(this::mapToResponse)
				.toList();

		PageResponse<TrainingCategoryResponse> pageResponse = new PageResponse<>(responseList, categoryPage.getNumber(),
				categoryPage.getSize(), categoryPage.getTotalElements(), categoryPage.getTotalPages(),
				categoryPage.isLast());

		return new ResponseEntity("Training categories fetched successfully", HttpStatus.OK.value(), pageResponse);
	}

	@Override
	public ResponseEntity deleteTrainingCategory(Long id) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid training category id", HttpStatus.BAD_REQUEST.value(), null);
			}

			TrainingCategory category = trainingCategoryRepository.findById(id).orElse(null);
			if (category == null) {
				return new ResponseEntity("Training category not found with id: " + id, HttpStatus.NOT_FOUND.value(),
						null);
			}

			String categoryCode = category.getCategoryCode();
			trainingCategoryRepository.delete(category);
			trainingCategoryRepository.flush();

			if (categoryCode != null && !categoryCode.trim().isEmpty()) {
				try {
					paramService.updateCodeValueOnDelete(categoryCode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return new ResponseEntity("Training category deleted successfully", HttpStatus.OK.value(), null);
		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			return new ResponseEntity("Training category cannot be deleted because it is linked to other records",
					HttpStatus.CONFLICT.value(), null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Something went wrong while deleting training category",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getAllTrainingCategory() {
		try {
			List<TrainingCategory> categories = trainingCategoryRepository.findAll();
			if (categories.isEmpty()) {
				return new ResponseEntity("Training category not found", HttpStatus.NOT_FOUND.value(), null);
			}
			List<TrainingCategoryResponse> categoryResponse = categories.stream().map(this::mapToResponse).toList();
			return new ResponseEntity("Training categories fetched successfully", HttpStatus.OK.value(),
					categoryResponse);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity updateTrainingCategory(Long id, TrainingCategoryRequest request) {
		try {
			Long currentUserId = CurrentUser.getUserId();
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid training category id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TrainingCategory> optionalTrainingCategory = trainingCategoryRepository.findById(id);
			if (optionalTrainingCategory.isEmpty()) {
				return new ResponseEntity("Training category not found!", HttpStatus.NOT_FOUND.value(), null);
			}

			TrainingCategory trainingCategory = optionalTrainingCategory.get();
			if (trainingCategoryRepository.existsByCategoryCodeAndTrainingCategoryIdNot(request.getCategoryCode(),
					id)) {
				return new ResponseEntity("Training category code already exists!", HttpStatus.CONFLICT.value(), null);
			}

			if (trainingCategoryRepository.existsByCategoryNameAndTrainingCategoryIdNot(request.getCategoryName(),
					id)) {
				return new ResponseEntity("Training category name already exists!", HttpStatus.CONFLICT.value(), null);
			}
			trainingCategory.setCategoryName(request.getCategoryName());
			trainingCategory.setCategoryDescription(request.getCategoryDescription());
			trainingCategory.setCategoryCode(request.getCategoryCode());
			trainingCategory.setUpdatedBy(currentUserId);
			trainingCategory.setUpdatedAt(LocalDateTime.now());
			TrainingCategory updatedTrainingCategory = trainingCategoryRepository.save(trainingCategory);
			if (updatedTrainingCategory.getCategoryCode() != null
					&& !updatedTrainingCategory.getCategoryCode().trim().isEmpty()) {
				try {
					paramService.updateCodeValue(updatedTrainingCategory.getCategoryCode());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return new ResponseEntity("Training category updated successfully!", HttpStatus.OK.value(),
					updatedTrainingCategory);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private TrainingCategory mapToEntity(TrainingCategoryRequest request) {
		TrainingCategory trainingCategory = new TrainingCategory();
		trainingCategory.setCategoryName(request.getCategoryName());
		trainingCategory.setCategoryCode(request.getCategoryCode());
		trainingCategory.setCategoryDescription(request.getCategoryDescription());
		trainingCategory.setCreatedAt(LocalDateTime.now());
		trainingCategory.setUpdatedAt(LocalDateTime.now());
		return trainingCategory;
	}

	private TrainingCategoryResponse mapToResponse(TrainingCategory entity) {
		TrainingCategoryResponse response = new TrainingCategoryResponse();
		response.setTrainingCategoryId(entity.getTrainingCategoryId());
		response.setCategoryName(entity.getCategoryName());
		response.setCategoryCode(entity.getCategoryCode());
		response.setCategoryDescription(entity.getCategoryDescription());
		response.setCreatedAt(entity.getCreatedAt());
		return response;
	}
}
