package com.doritech.tmsservice.service;

import java.util.List;

import com.doritech.tmsservice.request.ProductCategoryRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.Valid;

public interface ProductCategoryService {

	ResponseEntity getProductCategoryById(Long id);

	ResponseEntity createProductCategory(@Valid List<ProductCategoryRequest> productCategoryRequest);

	ResponseEntity getAllProductCategory(int page, int size, String sortBy, String sortDir);

	ResponseEntity deleteProductCategory(Long id);

}
