package com.doritech.tmsservice.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.entity.DocumentSubProduct;
import com.doritech.tmsservice.entity.DocumentSubProduct.DocumentSubProductId;
import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceAlreadyExistsException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.DocumentSubProductRepository;
import com.doritech.tmsservice.request.DocumentSubProductRequest;
import com.doritech.tmsservice.response.DocumentSubProductResponse;
import com.doritech.tmsservice.service.DocumentSubProductService;

@Service
public class DocumentSubProductServiceImpl implements DocumentSubProductService {

	private static final Logger log = LoggerFactory.getLogger(DocumentSubProductServiceImpl.class);

	private final DocumentSubProductRepository documentSubProductRepository;

	public DocumentSubProductServiceImpl(DocumentSubProductRepository documentSubProductRepository) {
		this.documentSubProductRepository = documentSubProductRepository;
	}

	@Override
	public ResponseEntity assignDocumentToSubProduct(DocumentSubProductRequest request) {

		log.info("assignDocumentToSubProduct :: documentId={}, subProductId={}", request.getDocumentId(),
				request.getSubProductId());

		DocumentSubProductId id = new DocumentSubProductId(request.getDocumentId(), request.getSubProductId());

		if (documentSubProductRepository.existsById(id)) {
			log.error("assignDocumentToSubProduct :: mapping already exists for documentId={}, subProductId={}",
					request.getDocumentId(), request.getSubProductId());
			throw new ResourceAlreadyExistsException("Document is already assigned to this sub product");
		}

		DocumentSubProduct mapping = new DocumentSubProduct();
		mapping.setId(id);
		mapping.setAssignedBy(request.getAssignedBy());

		DocumentSubProduct saved;
		try {
			saved = documentSubProductRepository.save(mapping);
		} catch (Exception e) {
			log.error("assignDocumentToSubProduct :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while assigning document to sub product");
		}

		log.info("assignDocumentToSubProduct :: assigned successfully documentId={}, subProductId={}",
				request.getDocumentId(), request.getSubProductId());

		return new ResponseEntity("Document assigned to sub product successfully", HttpStatus.CREATED.value(),
				mapToResponse(saved));
	}

	@Override
	public ResponseEntity getDocumentsBySubProductId(Long subProductId) {

		log.info("getDocumentsBySubProductId :: request received for subProductId={}", subProductId);

		if (subProductId == null) {
			log.error("getDocumentsBySubProductId :: subProductId is null");
			throw new BadRequestException("Sub Product ID can not be null");
		}

		List<DocumentSubProduct> mappings = documentSubProductRepository.findByIdSubProductId(subProductId);

		List<DocumentSubProductResponse> responseList = mappings.stream().map(this::mapToResponse)
				.collect(Collectors.toList());

		log.info("getDocumentsBySubProductId :: {} mappings fetched for subProductId={}", responseList.size(),
				subProductId);

		return new ResponseEntity("Document list fetch successfully", HttpStatus.OK.value(), responseList);
	}

	@Override
	public ResponseEntity removeDocumentFromSubProduct(Long documentId, Long subProductId) {

		log.info("removeDocumentFromSubProduct :: documentId={}, subProductId={}", documentId, subProductId);

		if (documentId == null || subProductId == null) {
			log.error("removeDocumentFromSubProduct :: documentId or subProductId is null");
			throw new BadRequestException("Document ID and Sub Product ID can not be null");
		}

		DocumentSubProductId id = new DocumentSubProductId(documentId, subProductId);

		DocumentSubProduct mapping = documentSubProductRepository.findById(id).orElseThrow(() -> {
			log.error("removeDocumentFromSubProduct :: mapping not found for documentId={}, subProductId={}",
					documentId, subProductId);
			return new ResourceNotFoundException("Mapping not found for given document and sub product");
		});

		try {
			documentSubProductRepository.delete(mapping);
		} catch (Exception e) {
			log.error("removeDocumentFromSubProduct :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while removing the mapping");
		}

		log.info("removeDocumentFromSubProduct :: removed successfully documentId={}, subProductId={}", documentId,
				subProductId);

		return new ResponseEntity("Document removed from sub product successfully", HttpStatus.OK.value(), null);
	}

	private DocumentSubProductResponse mapToResponse(DocumentSubProduct entity) {
		return new DocumentSubProductResponse(entity.getId().getDocumentId(), entity.getId().getSubProductId(),
				entity.getAssignedAt(), entity.getAssignedBy());
	}
}