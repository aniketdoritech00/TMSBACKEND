package com.doritech.tmsservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.ProductRequest;
import com.doritech.tmsservice.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/products")
public class ProductController {

	private static final Logger log = LoggerFactory.getLogger(ProductController.class);

	@Autowired
	private ProductService productService;

	@PostMapping("/createProduct")
	public ResponseEntity createProduct(@Valid @RequestBody List<ProductRequest> productRequest) {
		log.info("createProduct :: request received with size={}", productRequest.size());
		return productService.createProduct(productRequest);
	}

	@GetMapping("/getProductById/{id}")
	public ResponseEntity getProductById(@PathVariable("id") Long id) {
		log.info("getProductById :: request received for id={}", id);
		return productService.getProductById(id);
	}

	@GetMapping("/getAllProduct")
	public ResponseEntity getAllProduct(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "productId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		log.info("getAllProduct :: request received with page={}, size={}", page, size);
		return productService.getAllProduct(page, size, sortBy, sortDir);
	}

	@DeleteMapping("/deleteProductDetails/{id}")
	public ResponseEntity deleteProductDetails(@PathVariable("id") Long id) {
		log.info("deleteProductDetails :: request received");
		return productService.deleteProductDetails(id);
	}
}