package com.doritech.tmsservice.serviceImpl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.entity.Product;
import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceAlreadyExistsException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.ProductRepository;
import com.doritech.tmsservice.request.ProductRequest;
import com.doritech.tmsservice.response.ProductResponse;
import com.doritech.tmsservice.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

	@Autowired
	private ProductRepository productRepository;

	@Override
	public ResponseEntity createProduct(List<ProductRequest> productRequestList) {

		log.info("createProduct :: request received with size={}",
				productRequestList == null ? 0 : productRequestList.size());

		if (productRequestList == null || productRequestList.isEmpty()) {
			log.error("createProduct :: request list is empty");
			throw new BadRequestException("List is empty");
		}

		for (ProductRequest request : productRequestList) {
			if (request.getProductCode() != null && productRepository.existsByProductCode(request.getProductCode())) {
				log.error("createProduct :: duplicate code={}", request.getProductCode());
				throw new ResourceAlreadyExistsException(
						"Product already exists with code: " + request.getProductCode());
			}
		}

		List<Product> productList = productRequestList.stream().map(request -> {
			Product product = new Product();
			product.setProductCategoryId(request.getProductCategoryId());
			product.setProductName(request.getProductName());
			product.setProductCode(request.getProductCode());
			product.setProductDescription(request.getProductDescription());
			product.setProductImageUrl(request.getProductImageUrl());
			product.setDisplayOrder(request.getDisplayOrder());
			product.setIsActive(request.getIsActive());
			return product;
		}).collect(Collectors.toList());

		List<Product> savedList;
		try {
			savedList = productRepository.saveAll(productList);
		} catch (Exception e) {
			log.error("createProduct :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving product");
		}

		List<ProductResponse> responseList = savedList.stream().map(this::mapToResponse).collect(Collectors.toList());

		log.info("createProduct :: {} products saved successfully", responseList.size());

		return new ResponseEntity("Product saved successfully", HttpStatus.CREATED.value(), responseList);
	}

	@Override
	public ResponseEntity getProductById(Long id) {

		log.info("getProductById :: request received for id={}", id);

		if (id == null) {
			log.error("getProductById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		Product product = productRepository.findById(id).orElseThrow(() -> {
			log.error("getProductById :: product not found for id={}", id);
			return new ResourceNotFoundException("Product not found with id: " + id);
		});

		ProductResponse response = mapToResponse(product);

		log.info("getProductById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), response);
	}

	@Override
	public ResponseEntity getAllProduct(int page, int size, String sortBy, String sortDir) {

		log.info("getAllProduct :: request received with page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy,
				sortDir);

		if (page < 0) {
			log.error("getAllProduct :: page cannot be negative");
			throw new BadRequestException("Page number can not be negative");
		}

		if (size <= 0) {
			log.error("getAllProduct :: size must be greater than 0");
			throw new BadRequestException("Page size must be greater than 0");
		}

		if (size > 100) {
			log.error("getAllProduct :: size exceeds max limit={}", size);
			throw new BadRequestException("Page size can not exceed 100");
		}

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<Product> productPage;
		try {
			productPage = productRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			log.error("getAllProduct :: invalid sort field={}", sortBy);
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			log.error("getAllProduct :: error while fetching - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while fetching products");
		}

		List<ProductResponse> responseList = productPage.getContent().stream().map(this::mapToResponse)
				.collect(Collectors.toList());

		Map<String, Object> pageData = new LinkedHashMap<>();
		pageData.put("content", responseList);
		pageData.put("pageNumber", productPage.getNumber());
		pageData.put("pageSize", productPage.getSize());
		pageData.put("totalElements", productPage.getTotalElements());
		pageData.put("totalPages", productPage.getTotalPages());
		pageData.put("isLast", productPage.isLast());

		log.info("getAllProduct :: {} of {} products fetched successfully", responseList.size(),
				productPage.getTotalElements());

		return new ResponseEntity("Product fetch successfully", HttpStatus.OK.value(), pageData);
	}

	@Override
	public ResponseEntity getProductsByCategoryId(Long categoryId) {

		log.info("getProductsByCategoryId :: request received for categoryId={}", categoryId);

		if (categoryId == null) {
			log.error("getProductsByCategoryId :: categoryId is null");
			throw new BadRequestException("Category ID can not be null");
		}

		List<Product> productList = productRepository.findByProductCategoryId(categoryId);

		List<ProductResponse> responseList = productList.stream().map(this::mapToResponse).collect(Collectors.toList());

		log.info("getProductsByCategoryId :: {} products fetched for categoryId={}", responseList.size(), categoryId);

		return new ResponseEntity("Product fetch successfully", HttpStatus.OK.value(), responseList);
	}

	@Override
	public ResponseEntity deleteProductDetails(Long id) {

		log.info("deleteProduct :: request received for id={}", id);

		if (id == null) {
			log.error("deleteProduct :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		Product product = productRepository.findById(id).orElseThrow(() -> {
			log.error("deleteProduct :: product not found for id={}", id);
			return new ResourceNotFoundException("Product not found with id: " + id);
		});

		try {
			productRepository.delete(product);
		} catch (Exception e) {
			log.error("deleteProduct :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Cannot delete product, it may be linked to other records");
		}

		log.info("deleteProduct :: deleted successfully for id={}", id);

		return new ResponseEntity("Product deleted successfully", HttpStatus.OK.value(), null);
	}

	private ProductResponse mapToResponse(Product entity) {
		return new ProductResponse(entity.getProductId(), entity.getProductCategoryId(), entity.getProductName(),
				entity.getProductCode(), entity.getProductDescription(), entity.getProductImageUrl(),
				entity.getDisplayOrder(), entity.getIsActive(), entity.getCreatedAt(), entity.getUpdatedAt());
	}

}