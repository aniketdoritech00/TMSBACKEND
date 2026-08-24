package com.doritech.tmsservice.serviceImpl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.entity.ProductCategory;
import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceAlreadyExistsException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.ProductCategoryRepository;
import com.doritech.tmsservice.request.ProductCategoryRequest;
import com.doritech.tmsservice.response.ProductCategoryResponse;
import com.doritech.tmsservice.service.ProductCategoryService;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

	private static final Logger log = LoggerFactory.getLogger(ProductCategoryServiceImpl.class);

	private final ProductCategoryRepository productCategoryRepository;

	public ProductCategoryServiceImpl(ProductCategoryRepository productCategoryRepository) {
		this.productCategoryRepository = productCategoryRepository;
	}

	@Override
	public ResponseEntity createProductCategory(List<ProductCategoryRequest> productCategoryRequestList) {

		log.info("createProductCategory :: request received with size={}",
				productCategoryRequestList == null ? 0 : productCategoryRequestList.size());

		if (productCategoryRequestList == null || productCategoryRequestList.isEmpty()) {
			log.error("createProductCategory :: request list is empty");
			throw new BadRequestException("List is empty");
		}

		for (ProductCategoryRequest request : productCategoryRequestList) {
			if (request.getProductCategoryCode() != null
					&& productCategoryRepository.existsByProductCategoryCode(request.getProductCategoryCode())) {
				log.error("createProductCategory :: duplicate code={}", request.getProductCategoryCode());
				throw new ResourceAlreadyExistsException(
						"Product category already exists with code: " + request.getProductCategoryCode());
			}
		}

		List<ProductCategory> categoryList = productCategoryRequestList.stream().map(request -> {
			ProductCategory productCategory = new ProductCategory();
			productCategory.setProductCategoryName(request.getProductCategoryName());
			productCategory.setProductCategoryCode(request.getProductCategoryCode());
			productCategory.setProductCategoryDescription(request.getProductCategoryDescription());
			productCategory.setProductCategoryDisplayOrder(request.getProductCategoryDisplayOrder());
			productCategory.setIsActive(request.getIsActive());
			return productCategory;
		}).collect(Collectors.toList());

		List<ProductCategory> savedList;
		try {
			savedList = productCategoryRepository.saveAll(categoryList);
		} catch (Exception e) {
			log.error("createProductCategory :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving product category");
		}

		List<ProductCategoryResponse> responseList = savedList.stream().map(this::mapToResponse)
				.collect(Collectors.toList());

		log.info("createProductCategory :: {} categories saved successfully", responseList.size());

		return new ResponseEntity("Product category saved successfully", HttpStatus.CREATED.value(), responseList);
	}

	@Override
	public ResponseEntity getProductCategoryById(Long id) {

		log.info("getProductCategoryById :: request received for id={}", id);

		if (id == null) {
			log.error("getProductCategoryById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		ProductCategory category = productCategoryRepository.findById(id).orElseThrow(() -> {
			log.error("getProductCategoryById :: category not found for id={}", id);
			return new ResourceNotFoundException("Product category not found with id: " + id);
		});

		ProductCategoryResponse categoryResponse = mapToResponse(category);

		log.info("getProductCategoryById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), categoryResponse);
	}

	@Override
	public ResponseEntity getAllProductCategory(int page, int size, String sortBy, String sortDir) {

		log.info("getAllProductCategory :: request received with page={}, size={}, sortBy={}, sortDir={}", page, size,
				sortBy, sortDir);

		if (page < 0) {
			log.error("getAllProductCategory :: page cannot be negative");
			throw new BadRequestException("Page number can not be negative");
		}

		if (size <= 0) {
			log.error("getAllProductCategory :: size must be greater than 0");
			throw new BadRequestException("Page size must be greater than 0");
		}

		if (size > 100) {
			log.error("getAllProductCategory :: size exceeds max limit={}", size);
			throw new BadRequestException("Page size can not exceed 100");
		}

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<ProductCategory> categoryPage;
		try {
			categoryPage = productCategoryRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			log.error("getAllProductCategory :: invalid sort field={}", sortBy);
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			log.error("getAllProductCategory :: error while fetching - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while fetching product categories");
		}

		List<ProductCategoryResponse> responseList = categoryPage.getContent().stream().map(this::mapToResponse)
				.collect(Collectors.toList());

		Map<String, Object> pageData = new LinkedHashMap<>();
		pageData.put("content", responseList);
		pageData.put("pageNumber", categoryPage.getNumber());
		pageData.put("pageSize", categoryPage.getSize());
		pageData.put("totalElements", categoryPage.getTotalElements());
		pageData.put("totalPages", categoryPage.getTotalPages());
		pageData.put("isLast", categoryPage.isLast());

		log.info("getAllProductCategory :: {} of {} categories fetched successfully", responseList.size(),
				categoryPage.getTotalElements());

		return new ResponseEntity("Product category fetch successfully", HttpStatus.OK.value(), pageData);
	}

	@Override
	public ResponseEntity deleteProductCategory(Long id) {

		log.info("deleteProductCategory :: request received for id={}", id);

		if (id == null) {
			log.error("deleteProductCategory :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		ProductCategory category = productCategoryRepository.findById(id).orElseThrow(() -> {
			log.error("deleteProductCategory :: category not found for id={}", id);
			return new ResourceNotFoundException("Product category not found with id: " + id);
		});

		try {
			productCategoryRepository.delete(category);
		} catch (Exception e) {
			log.error("deleteProductCategory :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Cannot delete product category, it may be linked to other records");
		}

		log.info("deleteProductCategory :: deleted successfully for id={}", id);

		return new ResponseEntity("Product category deleted successfully", HttpStatus.OK.value(), null);
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
}