package com.doritech.tmsservice.serviceImpl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.config.FileStorageProperties;
import com.doritech.tmsservice.request.DocumentSubProductRequest;
import com.doritech.tmsservice.request.DocumentSubProductUploadRequest;
import com.doritech.tmsservice.response.DocumentResponse;
import com.doritech.tmsservice.response.DocumentSubProductResponse;
import com.doritech.tmsservice.service.DocumentSubProductService;
import com.doritech.tmsservice.service.FileStorageService;
import com.doritech.tmsservice.tms.entity.Document;
import com.doritech.tmsservice.tms.entity.DocumentSubProduct;
import com.doritech.tmsservice.tms.entity.DocumentSubProduct.DocumentSubProductId;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.repository.DocumentRepository;
import com.doritech.tmsservice.tms.repository.DocumentSubProductRepository;
import com.doritech.tmsservice.tms.repository.SubProductRepository;

@Service
public class DocumentSubProductServiceImpl implements DocumentSubProductService {
	private static final Logger log = LoggerFactory.getLogger(DocumentSubProductServiceImpl.class);
	private final DocumentSubProductRepository documentSubProductRepository;
	private final DocumentRepository documentRepository;
	private final FileStorageService fileStorageService;
	private final FileStorageProperties fileStorageProperties;
	private final SubProductRepository subProductRepository;

	public DocumentSubProductServiceImpl(DocumentSubProductRepository documentSubProductRepository,
			DocumentRepository documentRepository, FileStorageService fileStorageService,
			FileStorageProperties fileStorageProperties, SubProductRepository subProductRepository) {
		this.documentSubProductRepository = documentSubProductRepository;
		this.documentRepository = documentRepository;
		this.fileStorageService = fileStorageService;
		this.fileStorageProperties = fileStorageProperties;
		this.subProductRepository = subProductRepository;
	}

	@Override
	public ResponseEntity assignDocumentToSubProduct(DocumentSubProductRequest request) {
		try {
			if (request == null) {
				return new ResponseEntity("Request data is required", HttpStatus.BAD_REQUEST.value(), null);
			}
			if (request.getDocumentId() == null || request.getDocumentId() <= 0) {
				return new ResponseEntity("Invalid document id", HttpStatus.BAD_REQUEST.value(), null);
			}
			if (request.getSubProductId() == null || request.getSubProductId() <= 0) {
				return new ResponseEntity("Invalid sub product id", HttpStatus.BAD_REQUEST.value(), null);
			}
			if (!documentRepository.existsById(request.getDocumentId())) {
				return new ResponseEntity("Document not found with id: " + request.getDocumentId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			if (!subProductRepository.existsById(request.getSubProductId())) {
				return new ResponseEntity("Sub product not found with id: " + request.getSubProductId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			DocumentSubProductId id = new DocumentSubProductId(request.getDocumentId(), request.getSubProductId());
			if (documentSubProductRepository.existsById(id)) {
				return new ResponseEntity("Document is already assigned to this sub product",
						HttpStatus.CONFLICT.value(), null);
			}
			DocumentSubProduct mapping = new DocumentSubProduct();
			mapping.setId(id);
			mapping.setAssignedBy(CurrentUser.getUserId());
			DocumentSubProduct saved = documentSubProductRepository.save(mapping);
			return new ResponseEntity("Document assigned to sub product successfully", HttpStatus.CREATED.value(),
					mapToResponse(saved));
		} catch (Exception e) {
			return new ResponseEntity("Something went wrong while assigning document to sub product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private String getDocumentType(MultipartFile document) {

		String fileName = document.getOriginalFilename();

		if (fileName == null || !fileName.contains(".")) {
			return "OTHER";
		}

		String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

		switch (extension) {

		case "pdf":
			return "PDF";

		case "doc":
		case "docx":
			return "WORD";

		case "xls":
		case "xlsx":
			return "EXCEL";

		case "ppt":
		case "pptx":
			return "POWERPOINT";

		case "txt":
			return "TEXT";

		case "csv":
			return "CSV";

		case "jpg":
		case "jpeg":
		case "png":
		case "gif":
		case "webp":
			return "IMAGE";

		default:
			return "OTHER";
		}
	}

	@Override
	public ResponseEntity getDocumentsBySubProductId(Long subProductId) {
		try {
			if (subProductId == null || subProductId <= 0) {
				return new ResponseEntity("Invalid sub product id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (!subProductRepository.existsById(subProductId)) {
				return new ResponseEntity("Sub product not found with id: " + subProductId,
						HttpStatus.NOT_FOUND.value(), null);
			}
			List<DocumentSubProduct> mappings = documentSubProductRepository.findByIdSubProductId(subProductId);
			if (mappings == null || mappings.isEmpty()) {
				return new ResponseEntity("No documents found for sub product id: " + subProductId,
						HttpStatus.NOT_FOUND.value(), null);
			}

			List<DocumentSubProductResponse> responseList = mappings.stream().map(this::mapToResponse)
					.collect(Collectors.toList());
			return new ResponseEntity("Document list fetched successfully", HttpStatus.OK.value(), responseList);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Something went wrong while fetching documents",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity removeDocumentFromSubProduct(Long documentId, Long subProductId) {
		try {
			if (documentId == null || documentId <= 0) {
				return new ResponseEntity("Invalid document id", HttpStatus.BAD_REQUEST.value(), null);
			}
			if (subProductId == null || subProductId <= 0) {
				return new ResponseEntity("Invalid sub product id", HttpStatus.BAD_REQUEST.value(), null);
			}
			if (!documentRepository.existsById(documentId)) {
				return new ResponseEntity("Document not found with id: " + documentId, HttpStatus.NOT_FOUND.value(),
						null);
			}

			if (!subProductRepository.existsById(subProductId)) {
				return new ResponseEntity("Sub product not found with id: " + subProductId,
						HttpStatus.NOT_FOUND.value(), null);
			}

			DocumentSubProductId id = new DocumentSubProductId(documentId, subProductId);
			Optional<DocumentSubProduct> mappingOptional = documentSubProductRepository.findById(id);
			if (mappingOptional.isEmpty()) {
				return new ResponseEntity("Document is not assigned to this sub product", HttpStatus.NOT_FOUND.value(),
						null);
			}

			DocumentSubProduct mapping = mappingOptional.get();
			documentSubProductRepository.delete(mapping);
			return new ResponseEntity("Document removed from sub product successfully", HttpStatus.OK.value(), null);
		} catch (Exception e) {
			return new ResponseEntity("Something went wrong while removing document from sub product",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private DocumentSubProductResponse mapToResponse(DocumentSubProduct entity) {
		return new DocumentSubProductResponse(entity.getId().getDocumentId(), entity.getId().getSubProductId(),
				entity.getAssignedAt(), entity.getAssignedBy());
	}

	@Override
	@Transactional(transactionManager = "tmsTransactionManager")
	public ResponseEntity uploadDocumentAndAssignToSubProducts(DocumentSubProductUploadRequest request,
			MultipartFile document) {

		if (request == null) {
			return new ResponseEntity("Document data is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (document == null || document.isEmpty()) {
			return new ResponseEntity("Document file is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (request.getDocumentName() == null || request.getDocumentName().trim().isEmpty()) {

			return new ResponseEntity("Document name is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (request.getSubProductIds() == null || request.getSubProductIds().isEmpty()) {

			return new ResponseEntity("At least one sub product is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		for (Long subProductId : request.getSubProductIds()) {

			if (subProductId == null || subProductId <= 0) {

				return new ResponseEntity("Invalid sub product id: " + subProductId, HttpStatus.BAD_REQUEST.value(),
						null);
			}
		}

		List<Long> subProductIds = request.getSubProductIds().stream().distinct().collect(Collectors.toList());

		String documentPath = null;

		try {

			// Store physical file
			documentPath = fileStorageService.storeFile(document, fileStorageProperties.getDocumentPath());

			// Create document entity
			Document documentEntity = new Document();

			documentEntity.setDocumentName(request.getDocumentName().trim());

			documentEntity.setDocumentDescription(request.getDocumentDescription());

			documentEntity.setDocumentUrl(documentPath);

			documentEntity.setIsSecure(request.isSecure());

			documentEntity.setDocumentType(getDocumentType(document));

			// Set file size in bytes
			documentEntity.setFileSizeBytes(document.getSize());

			documentEntity.setUploadedBy(CurrentUser.getUserId());

			documentEntity.setCreatedAt(LocalDateTime.now());

			documentEntity.setUpdatedAt(LocalDateTime.now());

			Document savedDocument = documentRepository.save(documentEntity);

			Long currentUserId = CurrentUser.getUserId();

			// Assign document to sub products
			for (Long subProductId : subProductIds) {

				DocumentSubProductId mappingId = new DocumentSubProductId(savedDocument.getDocumentId(), subProductId);

				DocumentSubProduct mapping = new DocumentSubProduct();

				mapping.setId(mappingId);
				mapping.setAssignedBy(currentUserId);

				documentSubProductRepository.save(mapping);
			}

			DocumentResponse response = mapToFullResponse(savedDocument);

			response.setSubProductIds(subProductIds);

			return new ResponseEntity("Document uploaded and assigned to sub products successfully",
					HttpStatus.CREATED.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			deleteFileQuietly(documentPath);

			return new ResponseEntity("Something went wrong while uploading document and assigning sub products",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity updateDocumentAndAssignToSubProducts(Long documentId, DocumentSubProductUploadRequest request,
			MultipartFile document) {
		if (documentId == null || documentId <= 0) {
			return new ResponseEntity("Invalid document id", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (request == null) {
			return new ResponseEntity("Document data is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (request.getDocumentName() == null || request.getDocumentName().trim().isEmpty()) {
			return new ResponseEntity("Document name is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (request.getSubProductIds() == null || request.getSubProductIds().isEmpty()) {
			return new ResponseEntity("At least one sub product is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		for (Long subProductId : request.getSubProductIds()) {
			if (subProductId == null || subProductId <= 0) {
				return new ResponseEntity("Invalid sub product id: " + subProductId, HttpStatus.BAD_REQUEST.value(),
						null);
			}
		}

		List<Long> subProductIds = request.getSubProductIds().stream().distinct().collect(Collectors.toList());
		String newDocumentPath = null;
		String oldDocumentPath = null;

		try {
			Optional<Document> documentOptional = documentRepository.findById(documentId);
			if (documentOptional.isEmpty()) {
				return new ResponseEntity("Document not found with id: " + documentId, HttpStatus.NOT_FOUND.value(),
						null);
			}

			Document existingDocument = documentOptional.get();
			oldDocumentPath = existingDocument.getDocumentUrl();
			if (document != null && !document.isEmpty()) {
				newDocumentPath = fileStorageService.storeFile(document, fileStorageProperties.getDocumentPath());
				existingDocument.setDocumentUrl(newDocumentPath);
			}

			existingDocument.setDocumentName(request.getDocumentName().trim());
			existingDocument.setDocumentDescription(request.getDocumentDescription());
			existingDocument.setUpdatedAt(LocalDateTime.now());
			Document updatedDocument = documentRepository.save(existingDocument);
			List<DocumentSubProduct> existingMappings = documentSubProductRepository.findByIdDocumentId(documentId);
			if (existingMappings != null && !existingMappings.isEmpty()) {
				documentSubProductRepository.deleteAll(existingMappings);
			}

			Long currentUserId = CurrentUser.getUserId();
			for (Long subProductId : subProductIds) {
				DocumentSubProductId mappingId = new DocumentSubProductId(documentId, subProductId);
				DocumentSubProduct mapping = new DocumentSubProduct();
				mapping.setId(mappingId);
				mapping.setAssignedBy(currentUserId);
				documentSubProductRepository.save(mapping);
			}

			DocumentResponse response = mapToFullResponse(updatedDocument);
			response.setSubProductIds(subProductIds);
			if (newDocumentPath != null && oldDocumentPath != null && !oldDocumentPath.equals(newDocumentPath)) {
				deleteFileQuietly(oldDocumentPath);
			}

			return new ResponseEntity("Document updated and assigned to sub products successfully",
					HttpStatus.OK.value(), response);

		} catch (Exception e) {
			deleteFileQuietly(newDocumentPath);
			log.error("updateDocumentAndAssignToSubProducts :: error while updating documentId={}", documentId, e);

			return new ResponseEntity("Something went wrong while updating document and assigning sub products",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private void deleteFileQuietly(String filePath) {
		if (filePath == null || filePath.trim().isEmpty()) {
			return;
		}
		try {
			Files.deleteIfExists(Paths.get(filePath));
		} catch (Exception e) {
			log.warn("Unable to delete file: {}", filePath);
		}
	}

	private DocumentResponse mapToFullResponse(Document entity) {
		DocumentResponse response = new DocumentResponse();
		response.setDocumentId(entity.getDocumentId());
		response.setDocumentName(entity.getDocumentName());
		response.setDocumentDescription(entity.getDocumentDescription());
		response.setDocumentUrl(entity.getDocumentUrl());
		response.setDocumentType(entity.getDocumentType());
		response.setFileSizeBytes(entity.getFileSizeBytes());
		response.setIsSecure(entity.getIsSecure());
		response.setUploadedBy(entity.getUploadedBy());
		response.setCreatedAt(entity.getCreatedAt());
		response.setUpdatedAt(entity.getUpdatedAt());
		return response;
	}
}