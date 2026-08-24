package com.doritech.tmsservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.ProductCategoryRequest;
import com.doritech.tmsservice.service.ProductCategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/product-categories")
public class ProductCategoryController {

	private static final Logger log = LoggerFactory.getLogger(ProductCategoryController.class);

	private final ProductCategoryService productCategoryService;
	
	public ProductCategoryController(ProductCategoryService productCategoryService) {
		this.productCategoryService = productCategoryService;
	}

	@PostMapping("/createProductCategory")
	public ResponseEntity createProductCategory(
			@Valid @RequestBody List<ProductCategoryRequest> productCategoryRequest) {
		log.info("createProductCategory :: request received for code={}");

		return productCategoryService.createProductCategory(productCategoryRequest);

	}

	@GetMapping("/getAllProductCategory")
	public ResponseEntity getAllProductCategory(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "productCategoryId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		log.info("getAllProductCategory :: request received with page={}, size={}", page, size);
		return productCategoryService.getAllProductCategory(page, size, sortBy, sortDir);
	}

	@GetMapping("/getProductCategoryById/{id}")
	public ResponseEntity getProductCategoryById(@PathVariable("id") Long id) {

		log.info("getProductCategoryById :: request received for id={}", id);

		return productCategoryService.getProductCategoryById(id);
	}
}