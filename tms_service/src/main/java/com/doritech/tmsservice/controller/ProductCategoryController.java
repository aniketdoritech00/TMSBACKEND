package com.doritech.tmsservice.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.request.ProductCategoryRequest;
import com.doritech.tmsservice.service.ProductCategoryService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/product-categories")
public class ProductCategoryController {

	private final ProductCategoryService productCategoryService;

	public ProductCategoryController(ProductCategoryService productCategoryService) {
		this.productCategoryService = productCategoryService;
	}

	@PostMapping("/createProductCategory")
	public ResponseEntity createProductCategory(@Valid @RequestBody ProductCategoryRequest productCategoryRequest) {
		return productCategoryService.createProductCategory(productCategoryRequest);
	}

	@GetMapping("/getAllProductCategory")
	public ResponseEntity getAllProductCategory(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "productCategoryId") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDir) {
		return productCategoryService.getAllProductCategory(page, size, sortBy, sortDir);
	}

	@GetMapping("/getProductCategoryById/{id}")
	public ResponseEntity getProductCategoryById(@PathVariable Long id) {
		return productCategoryService.getProductCategoryById(id);
	}

	@DeleteMapping("/deleteProductCategoryByCategoryId/{id}")
	public ResponseEntity deleteProductCategoryByCategoryId(@PathVariable Long id) {
		return productCategoryService.deleteProductCategory(id);
	}

	@GetMapping("/getAllproductcategory")
	public ResponseEntity getAllProductCategory(){
		return productCategoryService.getAllProductCategory();
		
	}

}
