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
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.ProductCategoryRequest;
import com.doritech.tmsservice.service.ProductCategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tmsService/api/product-categories")
public class ProductCategoryController {

	private static final Logger log = LoggerFactory.getLogger(ProductCategoryController.class);

	@Autowired
	private ProductCategoryService productCategoryService;

	@PostMapping("/createProductCategory")
	public ResponseEntity createProductCategory(@Valid @RequestBody List<ProductCategoryRequest> productCategoryRequest) {
		log.info("createProductCategory :: request received for code={}");

		return productCategoryService.createProductCategory(productCategoryRequest);

	}
	
	@GetMapping("/getAllProductCategory")
	public ResponseEntity getAllProductCategory() {

		log.info("getAllProductCategory :: request received for igetAllProductCategory");

		return productCategoryService.getAllProductCategory();
	}

	@GetMapping("/getProductCategoryById/{id}")
	public ResponseEntity getProductCategoryById(@PathVariable("id") Long id) {

		log.info("getProductCategoryById :: request received for id={}", id);

		return productCategoryService.getProductCategoryById(id);
	}
}