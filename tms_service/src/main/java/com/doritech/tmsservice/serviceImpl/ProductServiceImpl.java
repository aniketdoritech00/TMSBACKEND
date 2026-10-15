package com.doritech.tmsservice.serviceImpl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.request.ProductRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.ProductResponse;
import com.doritech.tmsservice.service.ParamService;
import com.doritech.tmsservice.service.ProductService;
import com.doritech.tmsservice.tms.entity.Product;
import com.doritech.tmsservice.tms.entity.ProductCategory;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.repository.ProductCategoryRepository;
import com.doritech.tmsservice.tms.repository.ProductRepository;
import com.doritech.tmsservice.tms.repository.SubProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final ProductCategoryRepository productCategoryRepository;
	private final ParamService paramService;
	private final SubProductRepository subProductRepository;

	public ProductServiceImpl(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository,
			ParamService paramService, SubProductRepository subProductRepository) {
		this.subProductRepository = subProductRepository;
		this.productRepository = productRepository;
		this.productCategoryRepository = productCategoryRepository;
		this.paramService = paramService;
	}

	@Override
	public ResponseEntity createProduct(ProductRequest request) {
		try {
			if (request == null) {
				return new ResponseEntity("Product request cannot be null", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getProductCategoryId() == null || request.getProductCategoryId() <= 0) {
				return new ResponseEntity("Invalid product category id", HttpStatus.BAD_REQUEST.value(), null);
			}

			ProductCategory productCategory = productCategoryRepository.findById(request.getProductCategoryId())
					.orElse(null);

			if (productCategory == null) {
				return new ResponseEntity("Product category not found with id: " + request.getProductCategoryId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {
				return new ResponseEntity("Product name cannot be empty", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (productRepository.existsByProductName(request.getProductName().trim())) {
				return new ResponseEntity("Product already exists with name: " + request.getProductName(),
						HttpStatus.CONFLICT.value(), null);
			}

			if (request.getProductCode() != null
					&& productRepository.existsByProductCode(request.getProductCode().trim())) {
				return new ResponseEntity("Product already exists with code: " + request.getProductCode(),
						HttpStatus.CONFLICT.value(), null);
			}

			if (request.getDisplayOrder() != null
					&& productRepository.existsByDisplayOrder(request.getDisplayOrder())) {
				return new ResponseEntity("Product already exists with display order: " + request.getDisplayOrder(),
						HttpStatus.CONFLICT.value(), null);
			}

			Product product = new Product();

			product.setProductCategory(productCategory);
			product.setProductName(request.getProductName().trim());

			if (request.getProductCode() != null) {
				product.setProductCode(request.getProductCode().trim());
			}

			product.setProductDescription(request.getProductDescription());
			product.setDisplayOrder(request.getDisplayOrder());
			product.setIsActive(request.getIsActive());

			Product savedProduct = productRepository.save(product);

			try {
				if (savedProduct.getProductCode() != null) {
					paramService.updateCodeValue(savedProduct.getProductCode());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			ProductResponse response = mapToResponse(savedProduct);

			return new ResponseEntity("Product saved successfully", HttpStatus.CREATED.value(), response);

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();

			return new ResponseEntity("Product cannot be saved because duplicate or linked data exists",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {
			e.printStackTrace();

			return new ResponseEntity("Something went wrong while saving product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity updateProduct(Long id, ProductRequest request) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid product id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request == null) {
				return new ResponseEntity("Product request cannot be null", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getProductCategoryId() == null || request.getProductCategoryId() <= 0) {
				return new ResponseEntity("Invalid product category id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Product product = productRepository.findById(id).orElse(null);

			if (product == null) {
				return new ResponseEntity("Product not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			ProductCategory productCategory = productCategoryRepository.findById(request.getProductCategoryId())
					.orElse(null);

			if (productCategory == null) {
				return new ResponseEntity("Product category not found with id: " + request.getProductCategoryId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			if (request.getProductName() == null || request.getProductName().trim().isEmpty()) {
				return new ResponseEntity("Product name cannot be empty", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (productRepository.existsByProductNameAndProductIdNot(request.getProductName().trim(), id)) {

				return new ResponseEntity("Product already exists with name: " + request.getProductName(),
						HttpStatus.CONFLICT.value(), null);
			}

			if (request.getProductCode() != null
					&& productRepository.existsByProductCodeAndProductIdNot(request.getProductCode().trim(), id)) {

				return new ResponseEntity("Product already exists with code: " + request.getProductCode(),
						HttpStatus.CONFLICT.value(), null);
			}

			if (request.getDisplayOrder() != null
					&& productRepository.existsByDisplayOrderAndProductIdNot(request.getDisplayOrder(), id)) {

				return new ResponseEntity("Product already exists with display order: " + request.getDisplayOrder(),
						HttpStatus.CONFLICT.value(), null);
			}

			product.setProductCategory(productCategory);
			product.setProductName(request.getProductName().trim());

			if (request.getProductCode() != null) {
				product.setProductCode(request.getProductCode().trim());
			} else {
				product.setProductCode(null);
			}

			product.setProductDescription(request.getProductDescription());
			product.setDisplayOrder(request.getDisplayOrder());
			product.setIsActive(request.getIsActive());

			Product updatedProduct = productRepository.save(product);

			ProductResponse response = mapToResponse(updatedProduct);

			return new ResponseEntity("Product updated successfully", HttpStatus.OK.value(), response);

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();

			return new ResponseEntity("Product cannot be updated because duplicate or linked data exists",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {
			e.printStackTrace();

			return new ResponseEntity("Something went wrong while updating product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getProductById(Long id) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid product id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Product product = productRepository.findById(id).orElse(null);

			if (product == null) {
				return new ResponseEntity("Product not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			ProductResponse response = mapToResponse(product);

			return new ResponseEntity("Product fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {
			e.printStackTrace();

			return new ResponseEntity("Something went wrong while fetching product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getAllProduct(int page, int size, String sortBy, String sortDir) {
		try {
			if (page < 0) {
				return new ResponseEntity("Page number cannot be negative", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (size <= 0) {
				return new ResponseEntity("Page size must be greater than 0", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (size > 400) {
				return new ResponseEntity("Page size cannot exceed 100", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "productId";
			}

			if (sortDir == null || sortDir.trim().isEmpty()) {
				sortDir = "asc";
			}

			List<String> allowedSortFields = List.of("productId", "productCategory", "productName", "productCode",
					"displayOrder", "isActive", "createdAt", "updatedAt");

			if (!allowedSortFields.contains(sortBy)) {
				return new ResponseEntity("Invalid sort field: " + sortBy, HttpStatus.BAD_REQUEST.value(), null);
			}

			if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {

				return new ResponseEntity("Invalid sort direction. Use asc or desc", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			Sort sort;

			if (sortDir.equalsIgnoreCase("desc")) {
				sort = Sort.by(sortBy).descending();
			} else {
				sort = Sort.by(sortBy).ascending();
			}

			Pageable pageable = PageRequest.of(page, size, sort);

			Page<Product> productPage = productRepository.findAll(pageable);

			List<ProductResponse> responseList = productPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			Map<String, Object> pageData = new LinkedHashMap<>();

			pageData.put("content", responseList);
			pageData.put("pageNumber", productPage.getNumber());
			pageData.put("pageSize", productPage.getSize());
			pageData.put("totalElements", productPage.getTotalElements());
			pageData.put("totalPages", productPage.getTotalPages());
			pageData.put("isLast", productPage.isLast());

			return new ResponseEntity("Products fetched successfully", HttpStatus.OK.value(), pageData);

		} catch (Exception e) {
			e.printStackTrace();

			return new ResponseEntity("Something went wrong while fetching products",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getAllProductFilter(int page, int size, String productName, String productCode,
			Long productCategoryId, Boolean isActive, String sortBy, String sortDir) {

		try {

			Sort sort;

			if (sortDir.equalsIgnoreCase("desc")) {

				sort = Sort.by(sortBy).descending();

			} else {

				sort = Sort.by(sortBy).ascending();
			}

			Pageable pageable = PageRequest.of(page, size, sort);

			Page<Product> productPage = productRepository.findProductFilter(productName, productCode, productCategoryId,
					isActive, pageable);

			List<Product> content = productPage.getContent();

			PageResponse<Product> pageResponse = new PageResponse<>(content, productPage.getNumber(),
					productPage.getSize(), productPage.getTotalElements(), productPage.getTotalPages(),
					productPage.isLast());

			return new ResponseEntity("Products fetched successfully!", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getProductsByCategoryId(Long categoryId) {
		try {
			if (categoryId == null || categoryId <= 0) {
				return new ResponseEntity("Invalid category id", HttpStatus.BAD_REQUEST.value(), null);
			}

			ProductCategory productCategory = productCategoryRepository.findById(categoryId).orElse(null);

			if (productCategory == null) {
				return new ResponseEntity("Product category not found with id: " + categoryId,
						HttpStatus.NOT_FOUND.value(), null);
			}

			List<Product> productList = productRepository.findByProductCategory_ProductCategoryId(categoryId);

			List<ProductResponse> responseList = productList.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Products fetched successfully", HttpStatus.OK.value(), responseList);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Something went wrong while fetching products",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity deleteProductDetails(Long id) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid product id", HttpStatus.BAD_REQUEST.value(), null);
			}
			Product product = productRepository.findById(id).orElse(null);
			if (product == null) {
				return new ResponseEntity("Product not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}
			boolean subProductExists = subProductRepository.existsByProduct_ProductId(id);
			if (subProductExists) {
				return new ResponseEntity(
						"Product cannot be deleted because it is already linked with one or more sub-products",
						HttpStatus.CONFLICT.value(), null);
			}
			String productCode = product.getProductCode();
			try {
				productRepository.delete(product);
				productRepository.flush();
			} catch (DataIntegrityViolationException e) {
				e.printStackTrace();
				return new ResponseEntity("Product cannot be deleted because it is linked to other records",
						HttpStatus.CONFLICT.value(), null);
			}
			if (productCode != null && !productCode.trim().isEmpty()) {
				try {
					paramService.updateCodeValueOnDelete(productCode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return new ResponseEntity("Product deleted successfully", HttpStatus.OK.value(), null);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Something went wrong while deleting product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getAllProduct() {
		try {
			List<Product> productList = productRepository.findByIsActiveTrue();

			if (productList == null || productList.isEmpty()) {
				return new ResponseEntity("No products found", HttpStatus.NOT_FOUND.value(), null);
			}

			List<ProductResponse> responseList = productList.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Products fetched successfully", HttpStatus.OK.value(), responseList);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Something went wrong while fetching products",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private ProductResponse mapToResponse(Product entity) {
		Long categoryId = null;
		if (entity.getProductCategory() != null) {
			categoryId = entity.getProductCategory().getProductCategoryId();
		}
		return new ProductResponse(entity.getProductId(), categoryId, entity.getProductName(), entity.getProductCode(),
				entity.getProductDescription(), entity.getDisplayOrder(), entity.getIsActive(), entity.getCreatedAt(),
				entity.getUpdatedAt());
	}
}
