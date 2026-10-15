package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.ProductRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface ProductService {

	ResponseEntity getProductById(Long id);

	ResponseEntity getAllProduct(int page, int size, String sortBy, String sortDir);

	ResponseEntity deleteProductDetails(Long id);

	ResponseEntity getProductsByCategoryId(Long categoryId);

	ResponseEntity createProduct(ProductRequest request);

	ResponseEntity updateProduct(Long id, ProductRequest request);

	ResponseEntity getAllProduct();

	ResponseEntity getAllProductFilter(int page, int size, String productName, String productCode,
			Long productCategoryId, Boolean isActive, String sortBy, String sortDir);

	
}