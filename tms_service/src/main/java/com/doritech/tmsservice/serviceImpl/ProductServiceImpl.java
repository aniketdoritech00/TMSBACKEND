package com.doritech.tmsservice.serviceImpl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.request.ProductRequest;
import com.doritech.tmsservice.response.ProductResponse;
import com.doritech.tmsservice.service.ProductService;
import com.doritech.tmsservice.tms.entity.Product;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public ResponseEntity createProduct(List<ProductRequest> productRequestList) {
		try {
			if (productRequestList == null || productRequestList.isEmpty()) {
				return new ResponseEntity("List is empty", HttpStatus.BAD_REQUEST.value(), null);
			}

			for (ProductRequest request : productRequestList) {
				if (request.getProductCode() != null
						&& productRepository.existsByProductCode(request.getProductCode())) {
					return new ResponseEntity("Product already exists with code: " + request.getProductCode(),
							HttpStatus.CONFLICT.value(), null);
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
			List<Product> savedList = productRepository.saveAll(productList);
			List<ProductResponse> responseList = savedList.stream().map(this::mapToResponse)
					.collect(Collectors.toList());
			return new ResponseEntity("Product saved successfully", HttpStatus.CREATED.value(), responseList);
		} catch (Exception e) {
			return new ResponseEntity("Something went wrong while saving product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getProductById(Long id) {
		try {
			Product product = productRepository.findById(id).orElse(null);
			if (product == null) {
				return new ResponseEntity("Product not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}
			ProductResponse response = mapToResponse(product);
			return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), response);
		} catch (Exception e) {
			return new ResponseEntity("Something went wrong while fetching product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getAllProduct(int page, int size, String sortBy, String sortDir) {

		try {

			if (page < 0) {
				return new ResponseEntity("Page number can not be negative", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (size <= 0) {
				return new ResponseEntity("Page size must be greater than 0", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (size > 100) {
				return new ResponseEntity("Page size can not exceed 100", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "productId";
			}

			if (sortDir == null || sortDir.trim().isEmpty()) {
				sortDir = "asc";
			}

			Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
			Pageable pageable = PageRequest.of(page, size, sort);
			Page<Product> productPage;

			try {
				productPage = productRepository.findAll(pageable);
			} catch (PropertyReferenceException e) {
				return new ResponseEntity("Invalid sort field: " + sortBy, HttpStatus.BAD_REQUEST.value(), null);
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
			return new ResponseEntity("Product fetch successfully", HttpStatus.OK.value(), pageData);
		} catch (Exception e) {
			return new ResponseEntity("Something went wrong while fetching products",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getProductsByCategoryId(Long categoryId) {
		try {
			List<Product> productList = productRepository.findByProductCategoryId(categoryId);
			List<ProductResponse> responseList = productList.stream().map(this::mapToResponse)
					.collect(Collectors.toList());
			return new ResponseEntity("Product fetch successfully", HttpStatus.OK.value(), responseList);
		} catch (Exception e) {
			return new ResponseEntity("Something went wrong while fetching products",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity deleteProductDetails(Long id) {
		try {
			Product product = productRepository.findById(id).orElse(null);
			if (product == null) {
				return new ResponseEntity("Product not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}
			try {
				productRepository.delete(product);
			} catch (Exception e) {
				return new ResponseEntity("Cannot delete product, it may be linked to other records",
						HttpStatus.CONFLICT.value(), null);
			}
			return new ResponseEntity("Product deleted successfully", HttpStatus.OK.value(), null);
		} catch (Exception e) {
			return new ResponseEntity("Something went wrong while deleting product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private ProductResponse mapToResponse(Product entity) {
		return new ProductResponse(entity.getProductId(), entity.getProductCategoryId(), entity.getProductName(),
				entity.getProductCode(), entity.getProductDescription(), entity.getProductImageUrl(),
				entity.getDisplayOrder(), entity.getIsActive(), entity.getCreatedAt(), entity.getUpdatedAt());
	}
}