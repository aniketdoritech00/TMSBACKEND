package com.doritech.tmsservice.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.entity.ProductCategory;
import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.ProductCategoryRepository;
import com.doritech.tmsservice.request.ProductCategoryRequest;
import com.doritech.tmsservice.response.ProductCategoryResponse;
import com.doritech.tmsservice.service.ProductCategoryService;

import jakarta.validation.Valid;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

	private static final Logger log = LoggerFactory.getLogger(ProductCategoryServiceImpl.class);

	@Autowired
	private ProductCategoryRepository productCategoryRepository;

	@Override
	public ResponseEntity createProductCategory(@Valid List<ProductCategoryRequest> productCategoryRequestList) {

		log.info("createProductCategory :: request received with size={}",
				productCategoryRequestList == null ? 0 : productCategoryRequestList.size());

		if (productCategoryRequestList == null || productCategoryRequestList.isEmpty()) {
			log.error("createProductCategory :: request list is empty");
			throw new ServiceException("List is empty");
		}

		List<ProductCategory> categoryList = productCategoryRequestList.stream().map(request -> {
			ProductCategory productCategory = new ProductCategory();
			productCategory.setProductCategoryCode(request.getProductCategoryCode());
			productCategory.setProductCategoryName(request.getProductCategoryName());
			productCategory.setProductCategoryDescription(request.getProductCategoryDescription());
			productCategory.setProductCategoryImageUrl(request.getProductCategoryImageUrl());
			productCategory.setProductCategoryDisplayOrder(request.getProductCategoryDisplayOrder());
			productCategory.setIsActive(request.getIsActive());
			return productCategory;
		}).collect(Collectors.toList());

		List<ProductCategory> savedList;
		try {
			savedList = productCategoryRepository.saveAll(categoryList);
		} catch (Exception e) {
			log.error("createProductCategory :: error while saving - {}", e.getMessage(), e);
			throw new ServiceException("Something went wrong while saving product category");
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
			throw new ServiceException("ID can not be null");
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
	public ResponseEntity getAllProductCategory() {

		log.info("getAllProductCategory :: request received");

		List<ProductCategory> categoryList = productCategoryRepository.findAll();

		List<ProductCategoryResponse> categoryResponses = categoryList.stream().map(entity -> {

			ProductCategoryResponse categoryResponse = new ProductCategoryResponse();

			categoryResponse.setProductCategoryId(entity.getProductCategoryId());
			categoryResponse.setProductCategoryCode(entity.getProductCategoryCode());
			categoryResponse.setProductCategoryName(entity.getProductCategoryName());
			categoryResponse.setProductCategoryDescription(entity.getProductCategoryDescription());
			categoryResponse.setProductCategoryImageUrl(entity.getProductCategoryImageUrl());
			categoryResponse.setProductCategoryDisplayOrder(entity.getProductCategoryDisplayOrder());
			categoryResponse.setIsActive(entity.getIsActive());
			categoryResponse.setCreatedAt(entity.getCreatedAt());
			categoryResponse.setUpdatedAt(entity.getUpdatedAt());

			return categoryResponse;

		}).collect(Collectors.toList());

		log.info("getAllProductCategory :: {} categories fetched successfully", categoryResponses.size());

		return new ResponseEntity("Product category fetch successfully", HttpStatus.OK.value(), categoryResponses);
	}

	private ProductCategoryResponse mapToResponse(ProductCategory entity) {
		return new ProductCategoryResponse(entity.getProductCategoryId(), entity.getProductCategoryName(),
				entity.getProductCategoryCode(), entity.getProductCategoryDescription(),
				entity.getProductCategoryImageUrl(), entity.getProductCategoryDisplayOrder(), entity.getIsActive(),
				entity.getCreatedAt(), entity.getUpdatedAt());
	}

}
