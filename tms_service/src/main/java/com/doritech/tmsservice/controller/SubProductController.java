package com.doritech.tmsservice.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.request.SubProductRequest;
import com.doritech.tmsservice.service.SubProductService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/sub-products")
public class SubProductController {

	private final SubProductService subProductService;

	public SubProductController(SubProductService subProductService) {
		this.subProductService = subProductService;
	}

	@PostMapping("/createSubProduct")
	public ResponseEntity createSubProduct(@Valid @RequestBody SubProductRequest subProductRequest) {
		return subProductService.createSubProduct(subProductRequest);
	}

	@PutMapping("/updateSubProduct")
	public ResponseEntity updateSubProduct(@RequestParam Long id, @Valid @RequestBody SubProductRequest request) {
		return subProductService.updateSubProduct(id, request);
	}

	@GetMapping("/getSubProductById/{id}")
	public ResponseEntity getSubProductById(@PathVariable("id") Long id) {
		return subProductService.getSubProductById(id);
	}

	@GetMapping("/getAllSubProduct")
	public ResponseEntity getAllSubProduct(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size,
			@RequestParam(value = "sortBy", defaultValue = "subProductId") String sortBy,
			@RequestParam(value = "sortDir", defaultValue = "asc") String sortDir) {
		return subProductService.getAllSubProduct(page, size, sortBy, sortDir);
	}

	@GetMapping("/getSubProductsByProductId/{productId}")
	public ResponseEntity getSubProductsByProductId(@PathVariable("productId") Long productId) {
		return subProductService.getSubProductsByProductId(productId);
	}

	@DeleteMapping("/deleteSubProduct")
	public ResponseEntity deleteSubProduct(@RequestParam Long id) {
		return subProductService.deleteSubProduct(id);
	}
}