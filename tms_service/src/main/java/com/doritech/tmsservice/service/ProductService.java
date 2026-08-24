package com.doritech.tmsservice.service;

import java.util.List;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.ProductRequest;

import jakarta.validation.Valid;

public interface ProductService {

	ResponseEntity createProduct(@Valid List<ProductRequest> productRequest);

	ResponseEntity getProductById(Long id);


	ResponseEntity getAllProduct(int page, int size, String sortBy, String sortDir);
	ResponseEntity deleteProductDetails(Long id);

	ResponseEntity getProductsByCategoryId(Long categoryId);

}