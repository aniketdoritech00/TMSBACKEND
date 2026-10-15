package com.doritech.tmsservice.serviceImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.config.FileStorageProperties;
import com.doritech.tmsservice.request.DocumentRequest;
import com.doritech.tmsservice.response.DocumentListResponse;
import com.doritech.tmsservice.response.DocumentResponse;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.service.DocumentService;
import com.doritech.tmsservice.service.FileStorageService;
import com.doritech.tmsservice.tms.entity.Document;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.repository.DocumentRepository;
import com.doritech.tmsservice.tms.repository.DocumentSubProductRepository;

@Service
public class DocumentServiceImpl implements DocumentService {

	private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

	private final DocumentRepository documentRepository;
	private final FileStorageService fileStorageService;
	private final FileStorageProperties fileStorageProperties;
	private final DocumentSubProductRepository documentSubProductRepository;

	public DocumentServiceImpl(DocumentRepository documentRepository, FileStorageService fileStorageService,
			FileStorageProperties fileStorageProperties, DocumentSubProductRepository documentSubProductRepository) {
		this.documentRepository = documentRepository;
		this.fileStorageService = fileStorageService;
		this.fileStorageProperties = fileStorageProperties;
		this.documentSubProductRepository = documentSubProductRepository;
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity createDocument(DocumentRequest documentRequest, MultipartFile file) {
		if (documentRequest == null) {
			return new ResponseEntity("Document data is required", HttpStatus.BAD_REQUEST.value(), null);
		}
		if (file == null || file.isEmpty()) {
			return new ResponseEntity("Document file is required", HttpStatus.BAD_REQUEST.value(), null);
		}

		if (documentRequest.getDocumentName() == null || documentRequest.getDocumentName().trim().isEmpty()) {
			return new ResponseEntity("Document name is required", HttpStatus.BAD_REQUEST.value(), null);
		}
		String storedPath = null;
		try {
			String documentType = getDocumentType(file);
			storedPath = fileStorageService.storeFile(file, fileStorageProperties.getDocumentPath());

			Document document = new Document();
			document.setDocumentName(documentRequest.getDocumentName().trim());
			document.setDocumentDescription(documentRequest.getDocumentDescription());
			document.setDocumentUrl(storedPath);
			document.setDocumentType(documentType);
			document.setFileSizeBytes(file.getSize());
			document.setIsSecure(documentRequest.getIsSecure());
			document.setUploadedBy(CurrentUser.getUserId());
			document.setCreatedAt(LocalDateTime.now());
			document.setUpdatedAt(LocalDateTime.now());

			Document savedDocument = documentRepository.save(document);

			return new ResponseEntity("Document saved successfully", HttpStatus.CREATED.value(),
					mapToFullResponse(savedDocument));

		} catch (Exception e) {
			deleteFileQuietly(storedPath);
			return new ResponseEntity("Something went wrong while saving document",
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
			log.warn("Unable to delete document file: {}", filePath);
		}
	}

	private String getDocumentType(MultipartFile file) {
		String originalFileName = file.getOriginalFilename();
		if (originalFileName != null && originalFileName.contains(".")) {
			int lastDot = originalFileName.lastIndexOf(".");
			if (lastDot < originalFileName.length() - 1) {
				return originalFileName.substring(lastDot + 1).toUpperCase();
			}
		}

		String contentType = file.getContentType();
		if (contentType != null && contentType.contains("/")) {
			return contentType.substring(contentType.lastIndexOf("/") + 1).toUpperCase();
		}

		return "UNKNOWN";
	}

	@Override
	public ResponseEntity getDocumentDetailsById(Long id) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid document id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<Document> documentOptional = documentRepository.findById(id);
			if (documentOptional.isEmpty()) {
				return new ResponseEntity("Document not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			Document document = documentOptional.get();
			List<Long> subProductIds = documentSubProductRepository.getSubProductIdsByDocumentId(id);

			DocumentResponse response = mapToFullResponse(document);
			response.setSubProductIds(subProductIds);
			return new ResponseEntity("Document fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			log.error("getDocumentById :: error while fetching document for id={}", id, e);

			return new ResponseEntity("Something went wrong while fetching document",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public org.springframework.http.ResponseEntity<Resource> previewDocument(Long id) {
		try {
			if (id == null || id <= 0) {
				return org.springframework.http.ResponseEntity.badRequest().build();
			}
			Optional<Document> documentOptional = documentRepository.findById(id);
			if (documentOptional.isEmpty()) {
				return org.springframework.http.ResponseEntity.notFound().build();
			}
			Document document = documentOptional.get();
			String documentPath = document.getDocumentUrl();
			if (documentPath == null || documentPath.trim().isEmpty()) {
				return org.springframework.http.ResponseEntity.notFound().build();
			}
			Path path = Paths.get(documentPath);
			if (!Files.exists(path) || !Files.isRegularFile(path)) {
				return org.springframework.http.ResponseEntity.notFound().build();
			}
			Resource resource = new UrlResource(path.toUri());
			if (!resource.exists() || !resource.isReadable()) {
				return org.springframework.http.ResponseEntity.notFound().build();
			}
			String contentType = Files.probeContentType(path);
			if (contentType == null) {
				contentType = "application/octet-stream";
			}
			return org.springframework.http.ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION,
							"inline; filename=\"" + path.getFileName().toString() + "\"")
					.body(resource);

		} catch (Exception e) {
			return org.springframework.http.ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	public org.springframework.http.ResponseEntity<Resource> downloadDocument(Long id) {

		try {

			if (id == null || id <= 0) {
				return org.springframework.http.ResponseEntity.badRequest().build();
			}

			Optional<Document> documentOptional = documentRepository.findById(id);

			if (documentOptional.isEmpty()) {
				return org.springframework.http.ResponseEntity.notFound().build();
			}

			Document document = documentOptional.get();

			String documentPath = document.getDocumentUrl();

			if (documentPath == null || documentPath.trim().isEmpty()) {
				return org.springframework.http.ResponseEntity.notFound().build();
			}

			Path path = Paths.get(documentPath);

			if (!Files.exists(path) || !Files.isRegularFile(path)) {
				return org.springframework.http.ResponseEntity.notFound().build();
			}

			Resource resource = new UrlResource(path.toUri());

			if (!resource.exists() || !resource.isReadable()) {
				return org.springframework.http.ResponseEntity.notFound().build();
			}

			String contentType = Files.probeContentType(path);

			if (contentType == null) {
				contentType = "application/octet-stream";
			}

			String fileName = path.getFileName().toString();

			return org.springframework.http.ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
					.body(resource);

		} catch (Exception e) {

			return org.springframework.http.ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	public ResponseEntity getAllDocument(int page, int size, String sortBy, String sortDir) {
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
				sortBy = "documentId";
			}
			if (sortDir == null || sortDir.trim().isEmpty()) {
				sortDir = "asc";
			}

			if (!sortDir.equalsIgnoreCase("asc") && !sortDir.equalsIgnoreCase("desc")) {
				return new ResponseEntity("Invalid sort direction. Use 'asc' or 'desc'", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			List<String> allowedSortFields = List.of("documentId", "documentName", "uploadedBy", "createdAt",
					"updatedAt");
			if (!allowedSortFields.contains(sortBy)) {
				return new ResponseEntity("Invalid sort field: " + sortBy, HttpStatus.BAD_REQUEST.value(), null);
			}

			Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
			Pageable pageable = PageRequest.of(page, size, sort);
			Page<Document> documentPage;
			try {
				documentPage = documentRepository.findAll(pageable);
			} catch (PropertyReferenceException e) {
				return new ResponseEntity("Invalid sort field: " + sortBy, HttpStatus.BAD_REQUEST.value(), null);
			}

			if (documentPage.getTotalElements() == 0) {
				return new ResponseEntity("No documents found", HttpStatus.NOT_FOUND.value(), null);
			}

			List<DocumentListResponse> responseList = documentPage.getContent().stream().map(this::mapToListResponse)
					.collect(Collectors.toList());
			PageResponse<DocumentListResponse> pageResponse = new PageResponse<>();

			pageResponse.setContent(responseList);
			pageResponse.setPageNumber(documentPage.getNumber());
			pageResponse.setPageSize(documentPage.getSize());
			pageResponse.setTotalElements(documentPage.getTotalElements());
			pageResponse.setTotalPages(documentPage.getTotalPages());
			pageResponse.setLastPage(documentPage.isLast());

			return new ResponseEntity("Documents fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			return new ResponseEntity("Something went wrong while fetching documents",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity deleteDocument(Long id) {
		try {
			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid document id", HttpStatus.BAD_REQUEST.value(), null);
			}
			Optional<Document> documentOptional = documentRepository.findById(id);
			if (documentOptional.isEmpty()) {
				return new ResponseEntity("Document not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}
			boolean isMapped = documentSubProductRepository.existsByIdDocumentId(id);
			if (isMapped) {
				return new ResponseEntity("Cannot delete document because it is mapped to one or more sub products",
						HttpStatus.CONFLICT.value(), null);
			}

			Document document = documentOptional.get();
			documentRepository.delete(document);

			return new ResponseEntity("Document deleted successfully", HttpStatus.OK.value(), null);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity("Something went wrong while deleting document",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
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

	private DocumentListResponse mapToListResponse(Document entity) {
		return new DocumentListResponse(entity.getDocumentId(), entity.getDocumentName(), entity.getDocumentType(),
				entity.getIsSecure(), entity.getDocumentDescription(), entity.getFileSizeBytes());
	}
}