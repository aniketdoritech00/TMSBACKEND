package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.ProductCategoryRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.Valid;

public interface ProductCategoryService {

	ResponseEntity getProductCategoryById(Long id);

	ResponseEntity createProductCategory(ProductCategoryRequest productCategoryRequest);

	ResponseEntity deleteProductCategory(Long id);

	ResponseEntity getAllProductCategory(int page, int size, String sortBy, String sortDir);

	ResponseEntity getAllProductCategory();

	ResponseEntity updateProductCategory(Long id, @Valid ProductCategoryRequest request);

	ResponseEntity getAllProductCategoryFilter(int page, int size, String productCategoryName,
			String productCategoryCode, String sortBy, String sortDir);

}
