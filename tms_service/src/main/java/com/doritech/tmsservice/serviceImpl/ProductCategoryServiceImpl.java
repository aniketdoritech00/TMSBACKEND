package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.request.ProductCategoryRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.ProductCategoryResponse;
import com.doritech.tmsservice.service.ParamService;
import com.doritech.tmsservice.service.ProductCategoryService;
import com.doritech.tmsservice.tms.entity.ProductCategory;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.repository.ProductCategoryRepository;

import jakarta.transaction.Transactional;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

	private final ProductCategoryRepository productCategoryRepository;
	private final ParamService paramService;

	public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepository, ParamService paramService) {
		this.productCategoryRepository = productCategoryRepository;
		this.paramService = paramService;
	}

	@Override
	public ResponseEntity createProductCategory(ProductCategoryRequest request) {
		try {
			if (productCategoryRepository.existsByProductCategoryCode(request.getProductCategoryCode())) {
				return new ResponseEntity("Product category code already exists!", HttpStatus.CONFLICT.value(), null);
			}
			if (productCategoryRepository.existsByProductCategoryName(request.getProductCategoryName())) {
				return new ResponseEntity("Product category name already exists!", HttpStatus.CONFLICT.value(), null);
			}
			if (productCategoryRepository
					.existsByProductCategoryDisplayOrder(request.getProductCategoryDisplayOrder())) {
				return new ResponseEntity("Product category display order already exists!", HttpStatus.CONFLICT.value(),
						null);
			}
			ProductCategory productCategory = mapToEntity(request);
			ProductCategory savedProductCategory = productCategoryRepository.save(productCategory);
			try {
				paramService.updateCodeValue(savedProductCategory.getProductCategoryCode());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new ResponseEntity("Product category created successfully!", HttpStatus.CREATED.value(),
					savedProductCategory);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getProductCategoryById(Long id) {
		try {
			Optional<ProductCategory> categoryOptional = productCategoryRepository.findById(id);
			if (categoryOptional.isEmpty()) {
				return new ResponseEntity("Product category not found with id: " + id, HttpStatus.NOT_FOUND.value(),
						null);
			}
			ProductCategoryResponse categoryResponse = mapToResponse(categoryOptional.get());
			return new ResponseEntity("Product category fetched successfully", HttpStatus.OK.value(), categoryResponse);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getAllProductCategory(int page, int size, String sortBy, String sortDir) {

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

		Page<ProductCategory> categoryPage;

		try {
			categoryPage = productCategoryRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			throw new DatabaseOperationException("Something went wrong while fetching product categories");
		}
		List<ProductCategoryResponse> responseList = categoryPage.getContent().stream().map(this::mapToResponse)
				.toList();
		PageResponse<ProductCategoryResponse> pageResponse = new PageResponse<>(responseList, categoryPage.getNumber(),
				categoryPage.getSize(), categoryPage.getTotalElements(), categoryPage.getTotalPages(),
				categoryPage.isLast());
		return new ResponseEntity("Product categories fetched successfully", HttpStatus.OK.value(), pageResponse);
	}

	@Override
	@Transactional
	public ResponseEntity deleteProductCategory(Long id) {
		try {
			if (id == null) {
				throw new BadRequestException("Product category ID cannot be null");
			}
			ProductCategory category = productCategoryRepository.findById(id)
					.orElseThrow(() -> new ResourceNotFoundException("Product category not found with id: " + id));

			productCategoryRepository.delete(category);
			productCategoryRepository.flush();

			return new ResponseEntity("Product category deleted successfully", HttpStatus.OK.value(), null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(e.getMessage(), HttpStatus.OK.value(), null);
		}
	}

	private ProductCategory mapToEntity(ProductCategoryRequest request) {

		ProductCategory productCategory = new ProductCategory();

		productCategory.setProductCategoryName(request.getProductCategoryName());
		productCategory.setProductCategoryCode(request.getProductCategoryCode());
		productCategory.setProductCategoryDescription(request.getProductCategoryDescription());
		productCategory.setProductCategoryDisplayOrder(request.getProductCategoryDisplayOrder());
		productCategory.setIsActive(request.getIsActive());

		productCategory.setCreatedAt(LocalDateTime.now());
		productCategory.setUpdatedAt(LocalDateTime.now());

		return productCategory;
	}

	private ProductCategoryResponse mapToResponse(ProductCategory entity) {
		ProductCategoryResponse response = new ProductCategoryResponse();
		response.setProductCategoryId(entity.getProductCategoryId());
		response.setProductCategoryName(entity.getProductCategoryName());
		response.setProductCategoryCode(entity.getProductCategoryCode());
		response.setProductCategoryDescription(entity.getProductCategoryDescription());
		response.setProductCategoryDisplayOrder(entity.getProductCategoryDisplayOrder());
		response.setIsActive(entity.getIsActive());
		response.setCreatedAt(entity.getCreatedAt());
		response.setUpdatedAt(entity.getUpdatedAt());
		return response;
	}

	@Override
	public ResponseEntity getAllProductCategory() {
		try {
			List<ProductCategory> categories = productCategoryRepository.findAll();
			if (categories.isEmpty()) {
				return new ResponseEntity("Product category not found", HttpStatus.NOT_FOUND.value(), null);
			}
			List<ProductCategoryResponse> categoryResponse = categories.stream().map(this::mapToResponse).toList();
			return new ResponseEntity("Product category fetched successfully", HttpStatus.OK.value(), categoryResponse);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}

	}
}