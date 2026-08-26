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
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.request.SubProductRequest;
import com.doritech.tmsservice.response.SubProductResponse;
import com.doritech.tmsservice.service.ParamService;
import com.doritech.tmsservice.service.SubProductService;
import com.doritech.tmsservice.tms.entity.Product;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.SubProduct;
import com.doritech.tmsservice.tms.repository.ProductRepository;
import com.doritech.tmsservice.tms.repository.SubProductRepository;

@Service
public class SubProductServiceImpl implements SubProductService {

	private final SubProductRepository subProductRepository;
	private final ProductRepository productRepository;
	private final ParamService paramService;

	public SubProductServiceImpl(SubProductRepository subProductRepository, ProductRepository productRepository,
			ParamService paramService) {

		this.subProductRepository = subProductRepository;
		this.productRepository = productRepository;
		this.paramService = paramService;
	}

	@Override
	public ResponseEntity createSubProduct(SubProductRequest request) {

		try {

			if (request == null) {
				return new ResponseEntity("Sub product request cannot be null", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getProductId() == null || request.getProductId() <= 0) {
				return new ResponseEntity("Valid product ID is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getSubProductName() == null || request.getSubProductName().trim().isEmpty()) {

				return new ResponseEntity("Sub product name is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			String subProductName = request.getSubProductName().trim();

			String subProductCode = request.getSubProductCode() != null ? request.getSubProductCode().trim() : null;

			Product product = productRepository.findById(request.getProductId()).orElse(null);

			if (product == null) {
				return new ResponseEntity("Product not found with id: " + request.getProductId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			if (subProductRepository.existsBySubProductNameAndProduct_ProductId(subProductName,
					request.getProductId())) {

				return new ResponseEntity("Sub product already exists with name: " + subProductName,
						HttpStatus.CONFLICT.value(), null);
			}

			if (subProductCode != null && !subProductCode.isEmpty()
					&& subProductRepository.existsBySubProductCode(subProductCode)) {

				return new ResponseEntity("Sub product already exists with code: " + subProductCode,
						HttpStatus.CONFLICT.value(), null);
			}

			if (request.getDisplayOrder() != null && subProductRepository
					.existsByDisplayOrderAndProduct_ProductId(request.getDisplayOrder(), request.getProductId())) {

				return new ResponseEntity("Display order already exists for this product: " + request.getDisplayOrder(),
						HttpStatus.CONFLICT.value(), null);
			}

			SubProduct subProduct = new SubProduct();

			subProduct.setProduct(product);
			subProduct.setSubProductName(subProductName);
			subProduct.setSubProductCode(subProductCode);
			subProduct.setSubProductDescription(request.getSubProductDescription());
			subProduct.setDisplayOrder(request.getDisplayOrder());
			subProduct.setIsActive(request.getIsActive());

			SubProduct savedSubProduct = subProductRepository.save(subProduct);

			if (savedSubProduct.getSubProductCode() != null) {
				try {
					paramService.updateCodeValue(savedSubProduct.getSubProductCode());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			SubProductResponse response = mapToResponse(savedSubProduct);

			return new ResponseEntity("Sub product created successfully", HttpStatus.CREATED.value(), response);

		} catch (DataIntegrityViolationException e) {
			e.printStackTrace();
			return new ResponseEntity("Sub product cannot be created because duplicate data exists",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Something went wrong while creating sub product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getSubProductById(Long id) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Valid sub product ID is required", HttpStatus.BAD_REQUEST.value(), null);
			}
			SubProduct subProduct = subProductRepository.findById(id).orElse(null);
			if (subProduct == null) {
				return new ResponseEntity("Sub product not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			SubProductResponse response = mapToResponse(subProduct);

			return new ResponseEntity("Sub product fetched successfully", HttpStatus.OK.value(), response);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Something went wrong while fetching sub product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getAllSubProduct(int page, int size, String sortBy, String sortDir) {

		try {

			if (page < 0) {
				return new ResponseEntity("Page number cannot be negative", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (size <= 0) {
				return new ResponseEntity("Page size must be greater than 0", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (size > 100) {
				return new ResponseEntity("Page size cannot exceed 100", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "subProductId";
			}

			if (sortDir == null || sortDir.trim().isEmpty()) {
				sortDir = "asc";
			}

			if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {

				return new ResponseEntity("Invalid sort direction. Use 'asc' or 'desc'", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			List<String> allowedSortFields = List.of("subProductId", "subProductName", "subProductCode", "displayOrder",
					"isActive", "createdAt", "updatedAt");

			if (!allowedSortFields.contains(sortBy)) {
				return new ResponseEntity("Invalid sort field: " + sortBy, HttpStatus.BAD_REQUEST.value(), null);
			}

			Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

			Pageable pageable = PageRequest.of(page, size, sort);

			Page<SubProduct> subProductPage;

			try {

				subProductPage = subProductRepository.findAll(pageable);

			} catch (PropertyReferenceException e) {

				return new ResponseEntity("Invalid sort field: " + sortBy, HttpStatus.BAD_REQUEST.value(), null);
			}

			List<SubProductResponse> responseList = subProductPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			Map<String, Object> pageData = new LinkedHashMap<>();

			pageData.put("content", responseList);
			pageData.put("pageNumber", subProductPage.getNumber());
			pageData.put("pageSize", subProductPage.getSize());
			pageData.put("totalElements", subProductPage.getTotalElements());
			pageData.put("totalPages", subProductPage.getTotalPages());
			pageData.put("isLast", subProductPage.isLast());
			return new ResponseEntity("Sub product fetch successfully", HttpStatus.OK.value(), pageData);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Something went wrong while fetching sub products",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getSubProductsByProductId(Long productId) {
		try {
			if (productId == null || productId <= 0) {
				return new ResponseEntity("Valid product ID is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Product product = productRepository.findById(productId).orElse(null);
			if (product == null) {
				return new ResponseEntity("Product not found with id: " + productId, HttpStatus.NOT_FOUND.value(),
						null);
			}
			List<SubProduct> subProductList = subProductRepository.findByProduct_ProductId(productId);
			List<SubProductResponse> responseList = subProductList.stream().map(this::mapToResponse)
					.collect(Collectors.toList());
			return new ResponseEntity("Sub product fetch successfully", HttpStatus.OK.value(), responseList);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Something went wrong while fetching sub products",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity deleteSubProduct(Long id) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Valid sub product ID is required", HttpStatus.BAD_REQUEST.value(), null);
			}
			SubProduct subProduct = subProductRepository.findById(id).orElse(null);
			if (subProduct == null) {
				return new ResponseEntity("Sub product not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}
			String subProductCode = subProduct.getSubProductCode();
			try {
				subProductRepository.delete(subProduct);
				subProductRepository.flush();
			} catch (DataIntegrityViolationException e) {
				e.printStackTrace();
				return new ResponseEntity("Cannot delete sub product because it is linked to other records",
						HttpStatus.CONFLICT.value(), null);
			}

			if (subProductCode != null && !subProductCode.trim().isEmpty()) {
				try {
					paramService.updateCodeValueOnDelete(subProductCode);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return new ResponseEntity("Sub product deleted successfully", HttpStatus.OK.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Something went wrong while deleting sub product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private SubProductResponse mapToResponse(SubProduct entity) {
		Product product = entity.getProduct();
		Long productId = product != null ? product.getProductId() : null;
		return new SubProductResponse(entity.getSubProductId(), productId, entity.getSubProductName(),
				entity.getSubProductCode(), entity.getSubProductDescription(), entity.getDisplayOrder(),
				entity.getIsActive(), entity.getCreatedAt(), entity.getUpdatedAt());
	}

	@Override
	public ResponseEntity updateSubProduct(Long id, SubProductRequest request) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid sub product id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request == null) {
				return new ResponseEntity("Sub product request cannot be null", HttpStatus.BAD_REQUEST.value(), null);
			}

			SubProduct subProduct = subProductRepository.findById(id).orElse(null);
			if (subProduct == null) {
				return new ResponseEntity("Sub product not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			if (request.getProductId() == null || request.getProductId() <= 0) {
				return new ResponseEntity("Product ID is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Product product = productRepository.findById(request.getProductId()).orElse(null);

			if (product == null) {
				return new ResponseEntity("Product not found with id: " + request.getProductId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			if (request.getSubProductName() == null || request.getSubProductName().trim().isEmpty()) {

				return new ResponseEntity("Sub product name is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			String subProductName = request.getSubProductName().trim();

			if (subProductRepository.existsBySubProductNameAndProduct_ProductIdAndSubProductIdNot(subProductName,
					request.getProductId(), id)) {

				return new ResponseEntity("Sub product already exists with name: " + subProductName,
						HttpStatus.CONFLICT.value(), null);
			}

			// Check duplicate display order under same product
			if (request.getDisplayOrder() != null
					&& subProductRepository.existsByDisplayOrderAndProduct_ProductIdAndSubProductIdNot(
							request.getDisplayOrder(), request.getProductId(), id)) {

				return new ResponseEntity("Display order already exists for this product: " + request.getDisplayOrder(),
						HttpStatus.CONFLICT.value(), null);
			}

			subProduct.setProduct(product);

			subProduct.setSubProductName(subProductName);
			subProduct.setSubProductDescription(request.getSubProductDescription());
			subProduct.setDisplayOrder(request.getDisplayOrder());
			subProduct.setIsActive(request.getIsActive());

			SubProduct updatedSubProduct = subProductRepository.save(subProduct);

			SubProductResponse response = mapToResponse(updatedSubProduct);

			return new ResponseEntity("Sub product updated successfully", HttpStatus.OK.value(), response);

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("Sub product cannot be updated because duplicate or linked data exists",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Something went wrong while updating sub product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}
}