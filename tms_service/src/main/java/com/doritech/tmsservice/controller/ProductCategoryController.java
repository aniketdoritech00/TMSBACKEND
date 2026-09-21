package com.doritech.tmsservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	@GetMapping("/getAllProductCategoryFilter")
	public ResponseEntity getAllProductCategoryFilter(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String productCategoryName,
			@RequestParam(required = false) String productCategoryCode,
			@RequestParam(required = false) Boolean isActive,
			@RequestParam(defaultValue = "productCategoryId") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDir) {

		return productCategoryService.getAllProductCategoryFilter(page, size, productCategoryName, productCategoryCode,
				isActive, sortBy, sortDir);
	}

	@DeleteMapping("/deleteProductCategoryByCategoryId")
	public ResponseEntity deleteProductCategoryByCategoryId(@RequestParam(required = false) Long id) {
		if (id == null) {
			return new ResponseEntity("Product category ID is required", HttpStatus.BAD_REQUEST.value(), null);
		}
		return productCategoryService.deleteProductCategory(id);
	}

	@GetMapping("/getAllproductcategory")
	public ResponseEntity getAllProductCategory() {
		return productCategoryService.getAllProductCategory();
	}

	@PutMapping("/updateProductCategory/{id}")
	public ResponseEntity updateProductCategory(@PathVariable Long id,
			@Valid @RequestBody ProductCategoryRequest request) {
		return productCategoryService.updateProductCategory(id, request);
	}
}
