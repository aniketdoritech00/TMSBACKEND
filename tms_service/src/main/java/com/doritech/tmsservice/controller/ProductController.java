package com.doritech.tmsservice.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.request.ProductRequest;
import com.doritech.tmsservice.service.ProductService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@PostMapping("/createProduct")
	public ResponseEntity createProduct(@Valid @RequestBody ProductRequest productRequest) {
		return productService.createProduct(productRequest);
	}

	@PutMapping("/updateProduct")
	public ResponseEntity updateProduct(@RequestParam Long id, @Valid @RequestBody ProductRequest productRequest) {
		return productService.updateProduct(id, productRequest);
	}

	@GetMapping("/getProductById")
	public ResponseEntity getProductById(@RequestParam Long id) {
		return productService.getProductById(id);
	}

	@GetMapping("/getAllProduct")
	public ResponseEntity getAllProduct(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "productId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {

		return productService.getAllProduct(page, size, sortBy, sortDir);
	}

	@GetMapping("/getProductsByCategoryId")
	public ResponseEntity getProductsByCategoryId(@RequestParam Long categoryId) {
		return productService.getProductsByCategoryId(categoryId);
	}

	@DeleteMapping("/deleteProductDetails")
	public ResponseEntity deleteProductDetails(@RequestParam Long id) {
		return productService.deleteProductDetails(id);
	}

	@GetMapping("/getAllProductWithoutPagination")
	public ResponseEntity getAllProduct() {
		return productService.getAllProduct();
	}
}