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

import com.doritech.tmsservice.request.SubProductRequest;
import com.doritech.tmsservice.response.SubProductResponse;
import com.doritech.tmsservice.service.SubProductService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.SubProduct;
import com.doritech.tmsservice.tms.repository.SubProductRepository;

@Service
public class SubProductServiceImpl implements SubProductService {

	private final SubProductRepository subProductRepository;

	public SubProductServiceImpl(SubProductRepository subProductRepository) {
		this.subProductRepository = subProductRepository;
	}

	@Override
	public ResponseEntity createSubProduct(List<SubProductRequest> subProductRequestList) {
		try {
			if (subProductRequestList == null || subProductRequestList.isEmpty()) {
				return new ResponseEntity("List is empty", HttpStatus.BAD_REQUEST.value(), null);
			}
			for (SubProductRequest request : subProductRequestList) {
				if (request.getSubProductCode() != null
						&& subProductRepository.existsBySubProductCode(request.getSubProductCode())) {
					return new ResponseEntity("Sub product already exists with code: " + request.getSubProductCode(),
							HttpStatus.CONFLICT.value(), null);
				}
			}

			List<SubProduct> subProductList = subProductRequestList.stream().map(request -> {
				SubProduct subProduct = new SubProduct();

				subProduct.setProductId(request.getProductId());
				subProduct.setSubProductName(request.getSubProductName());
				subProduct.setSubProductCode(request.getSubProductCode());
				subProduct.setSubProductDescription(request.getSubProductDescription());
				subProduct.setDisplayOrder(request.getDisplayOrder());
				subProduct.setIsActive(request.getIsActive());

				return subProduct;

			}).collect(Collectors.toList());
			List<SubProduct> savedList = subProductRepository.saveAll(subProductList);
			List<SubProductResponse> responseList = savedList.stream().map(this::mapToResponse)
					.collect(Collectors.toList());
			return new ResponseEntity("Sub product saved successfully", HttpStatus.CREATED.value(), responseList);

		} catch (Exception e) {
			return new ResponseEntity("Something went wrong while saving sub product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getSubProductById(Long id) {
		try {
			if (id == null) {
				return new ResponseEntity("ID can not be null", HttpStatus.BAD_REQUEST.value(), null);
			}
			SubProduct subProduct = subProductRepository.findById(id).orElse(null);
			if (subProduct == null) {
				return new ResponseEntity("Sub product not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}
			SubProductResponse response = mapToResponse(subProduct);
			return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			return new ResponseEntity("Something went wrong while fetching sub product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getAllSubProduct(int page, int size, String sortBy, String sortDir) {
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
				sortBy = "subProductId";
			}
			if (sortDir == null || sortDir.trim().isEmpty()) {
				sortDir = "asc";
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

			return new ResponseEntity("Something went wrong while fetching sub products",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity getSubProductsByProductId(Long productId) {
		try {
			if (productId == null) {
				return new ResponseEntity("Product ID can not be null", HttpStatus.BAD_REQUEST.value(), null);
			}
			List<SubProduct> subProductList = subProductRepository.findByProductId(productId);
			List<SubProductResponse> responseList = subProductList.stream().map(this::mapToResponse)
					.collect(Collectors.toList());
			return new ResponseEntity("Sub product fetch successfully", HttpStatus.OK.value(), responseList);

		} catch (Exception e) {

			return new ResponseEntity("Something went wrong while fetching sub products",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity deleteSubProduct(Long id) {
		try {
			if (id == null) {
				return new ResponseEntity("ID can not be null", HttpStatus.BAD_REQUEST.value(), null);
			}
			SubProduct subProduct = subProductRepository.findById(id).orElse(null);

			if (subProduct == null) {
				return new ResponseEntity("Sub product not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			try {
				subProductRepository.delete(subProduct);
			} catch (Exception e) {
				return new ResponseEntity("Cannot delete sub product, it may be linked to other records",
						HttpStatus.CONFLICT.value(), null);
			}
			return new ResponseEntity("Sub product deleted successfully", HttpStatus.OK.value(), null);
		} catch (Exception e) {

			return new ResponseEntity("Something went wrong while deleting sub product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private SubProductResponse mapToResponse(SubProduct entity) {
		return new SubProductResponse(entity.getSubProductId(), entity.getProductId(), entity.getSubProductName(),
				entity.getSubProductCode(), entity.getSubProductDescription(), entity.getDisplayOrder(),
				entity.getIsActive(), entity.getCreatedAt(), entity.getUpdatedAt());
	}
}