package com.doritech.tmsservice.serviceImpl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.entity.SubProduct;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceAlreadyExistsException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.SubProductRepository;
import com.doritech.tmsservice.request.SubProductRequest;
import com.doritech.tmsservice.response.SubProductResponse;
import com.doritech.tmsservice.service.SubProductService;

@Service
public class SubProductServiceImpl implements SubProductService {

	private static final Logger log = LoggerFactory.getLogger(SubProductServiceImpl.class);

	private final SubProductRepository subProductRepository;

	public SubProductServiceImpl(SubProductRepository subProductRepository) {
		this.subProductRepository = subProductRepository;
	}

	@Override
	public ResponseEntity createSubProduct(List<SubProductRequest> subProductRequestList) {

		log.info("createSubProduct :: request received with size={}",
				subProductRequestList == null ? 0 : subProductRequestList.size());

		if (subProductRequestList == null || subProductRequestList.isEmpty()) {
			log.error("createSubProduct :: request list is empty");
			throw new BadRequestException("List is empty");
		}

		for (SubProductRequest request : subProductRequestList) {
			if (request.getSubProductCode() != null
					&& subProductRepository.existsBySubProductCode(request.getSubProductCode())) {
				log.error("createSubProduct :: duplicate code={}", request.getSubProductCode());
				throw new ResourceAlreadyExistsException(
						"Sub product already exists with code: " + request.getSubProductCode());
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

		List<SubProduct> savedList;
		try {
			savedList = subProductRepository.saveAll(subProductList);
		} catch (Exception e) {
			log.error("createSubProduct :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving sub product");
		}

		List<SubProductResponse> responseList = savedList.stream().map(this::mapToResponse)
				.collect(Collectors.toList());

		log.info("createSubProduct :: {} sub products saved successfully", responseList.size());

		return new ResponseEntity("Sub product saved successfully", HttpStatus.CREATED.value(), responseList);
	}

	@Override
	public ResponseEntity getSubProductById(Long id) {

		log.info("getSubProductById :: request received for id={}", id);

		if (id == null) {
			log.error("getSubProductById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		SubProduct subProduct = subProductRepository.findById(id).orElseThrow(() -> {
			log.error("getSubProductById :: sub product not found for id={}", id);
			return new ResourceNotFoundException("Sub product not found with id: " + id);
		});

		SubProductResponse response = mapToResponse(subProduct);

		log.info("getSubProductById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), response);
	}

	@Override
	public ResponseEntity getAllSubProduct(int page, int size, String sortBy, String sortDir) {

		log.info("getAllSubProduct :: request received with page={}, size={}, sortBy={}, sortDir={}", page, size,
				sortBy, sortDir);

		if (page < 0) {
			log.error("getAllSubProduct :: page cannot be negative");
			throw new BadRequestException("Page number can not be negative");
		}

		if (size <= 0) {
			log.error("getAllSubProduct :: size must be greater than 0");
			throw new BadRequestException("Page size must be greater than 0");
		}

		if (size > 100) {
			log.error("getAllSubProduct :: size exceeds max limit={}", size);
			throw new BadRequestException("Page size can not exceed 100");
		}

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<SubProduct> subProductPage;
		try {
			subProductPage = subProductRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			log.error("getAllSubProduct :: invalid sort field={}", sortBy);
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			log.error("getAllSubProduct :: error while fetching - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while fetching sub products");
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

		log.info("getAllSubProduct :: {} of {} sub products fetched successfully", responseList.size(),
				subProductPage.getTotalElements());

		return new ResponseEntity("Sub product fetch successfully", HttpStatus.OK.value(), pageData);
	}

	@Override
	public ResponseEntity getSubProductsByProductId(Long productId) {

		log.info("getSubProductsByProductId :: request received for productId={}", productId);

		if (productId == null) {
			log.error("getSubProductsByProductId :: productId is null");
			throw new BadRequestException("Product ID can not be null");
		}

		List<SubProduct> subProductList = subProductRepository.findByProductId(productId);

		List<SubProductResponse> responseList = subProductList.stream().map(this::mapToResponse)
				.collect(Collectors.toList());

		log.info("getSubProductsByProductId :: {} sub products fetched for productId={}", responseList.size(),
				productId);

		return new ResponseEntity("Sub product fetch successfully", HttpStatus.OK.value(), responseList);
	}

	@Override
	public ResponseEntity deleteSubProduct(Long id) {

		log.info("deleteSubProduct :: request received for id={}", id);

		if (id == null) {
			log.error("deleteSubProduct :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		SubProduct subProduct = subProductRepository.findById(id).orElseThrow(() -> {
			log.error("deleteSubProduct :: sub product not found for id={}", id);
			return new ResourceNotFoundException("Sub product not found with id: " + id);
		});

		try {
			subProductRepository.delete(subProduct);
		} catch (Exception e) {
			log.error("deleteSubProduct :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Cannot delete sub product, it may be linked to other records");
		}

		log.info("deleteSubProduct :: deleted successfully for id={}", id);

		return new ResponseEntity("Sub product deleted successfully", HttpStatus.OK.value(), null);
	}

	private SubProductResponse mapToResponse(SubProduct entity) {
		return new SubProductResponse(entity.getSubProductId(), entity.getProductId(), entity.getSubProductName(),
				entity.getSubProductCode(), entity.getSubProductDescription(), entity.getDisplayOrder(),
				entity.getIsActive(), entity.getCreatedAt(), entity.getUpdatedAt());
	}
}