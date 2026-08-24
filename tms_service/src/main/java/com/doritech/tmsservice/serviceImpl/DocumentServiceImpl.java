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
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.config.FileStorageProperties;
import com.doritech.tmsservice.entity.Document;
import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.DocumentRepository;
import com.doritech.tmsservice.request.DocumentRequest;
import com.doritech.tmsservice.response.DocumentListResponse;
import com.doritech.tmsservice.response.DocumentResponse;
import com.doritech.tmsservice.service.DocumentService;
import com.doritech.tmsservice.service.FileStorageService;

@Service
public class DocumentServiceImpl implements DocumentService {

	private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);

	private final DocumentRepository documentRepository;
	private final FileStorageService fileStorageService;
	private final FileStorageProperties fileStorageProperties;

	public DocumentServiceImpl(DocumentRepository documentRepository, FileStorageService fileStorageService,
			FileStorageProperties fileStorageProperties) {
		this.documentRepository = documentRepository;
		this.fileStorageService = fileStorageService;
		this.fileStorageProperties = fileStorageProperties;
	}

	@Override
	public ResponseEntity createDocument(DocumentRequest documentRequest, MultipartFile file) {

		log.info("createDocument :: request received for name={}", documentRequest.getDocumentName());

		if (file == null || file.isEmpty()) {
			log.error("createDocument :: document file is missing");
			throw new BadRequestException("Document file must not be null");
		}

		String storedPath = fileStorageService.storeFile(file, fileStorageProperties.getDocumentPath());

		Document document = new Document();
		document.setDocumentName(documentRequest.getDocumentName());
		document.setDocumentDescription(documentRequest.getDocumentDescription());
		document.setDocumentUrl(storedPath);
		document.setDocumentType(documentRequest.getDocumentType());
		document.setFileSizeBytes(file.getSize());
		document.setIsSecure(documentRequest.getIsSecure());
		document.setUploadedBy(documentRequest.getUploadedBy());

		Document saved;
		try {
			saved = documentRepository.save(document);
		} catch (Exception e) {
			log.error("createDocument :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving document");
		}

		log.info("createDocument :: document saved successfully with id={}", saved.getDocumentId());

		return new ResponseEntity("Document saved successfully", HttpStatus.CREATED.value(), mapToFullResponse(saved));
	}

	@Override
	public ResponseEntity getDocumentById(Long id) {

		log.info("getDocumentById :: request received for id={}", id);

		if (id == null) {
			log.error("getDocumentById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		Document document = documentRepository.findById(id).orElseThrow(() -> {
			log.error("getDocumentById :: document not found for id={}", id);
			return new ResourceNotFoundException("Document not found with id: " + id);
		});

		log.info("getDocumentById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), mapToFullResponse(document));
	}

	@Override
	public ResponseEntity getAllDocument(int page, int size, String sortBy, String sortDir) {

		log.info("getAllDocument :: request received with page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy,
				sortDir);

		if (page < 0) {
			log.error("getAllDocument :: page cannot be negative");
			throw new BadRequestException("Page number can not be negative");
		}

		if (size <= 0) {
			log.error("getAllDocument :: size must be greater than 0");
			throw new BadRequestException("Page size must be greater than 0");
		}

		if (size > 100) {
			log.error("getAllDocument :: size exceeds max limit={}", size);
			throw new BadRequestException("Page size can not exceed 100");
		}

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<Document> documentPage;
		try {
			documentPage = documentRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			log.error("getAllDocument :: invalid sort field={}", sortBy);
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			log.error("getAllDocument :: error while fetching - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while fetching documents");
		}

		// NOTE: getAll me sirf lightweight fields — heavy document data (url,
		// fileSizeBytes) nahi bhejte.
		// Full detail sirf getDocumentById se milega.
		List<DocumentListResponse> responseList = documentPage.getContent().stream().map(this::mapToListResponse)
				.collect(Collectors.toList());

		Map<String, Object> pageData = new LinkedHashMap<>();
		pageData.put("content", responseList);
		pageData.put("pageNumber", documentPage.getNumber());
		pageData.put("pageSize", documentPage.getSize());
		pageData.put("totalElements", documentPage.getTotalElements());
		pageData.put("totalPages", documentPage.getTotalPages());
		pageData.put("isLast", documentPage.isLast());

		log.info("getAllDocument :: {} of {} documents fetched successfully", responseList.size(),
				documentPage.getTotalElements());

		return new ResponseEntity("Document fetch successfully", HttpStatus.OK.value(), pageData);
	}

	@Override
	public ResponseEntity deleteDocument(Long id) {

		log.info("deleteDocument :: request received for id={}", id);

		if (id == null) {
			log.error("deleteDocument :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		Document document = documentRepository.findById(id).orElseThrow(() -> {
			log.error("deleteDocument :: document not found for id={}", id);
			return new ResourceNotFoundException("Document not found with id: " + id);
		});

		try {
			documentRepository.delete(document);
		} catch (Exception e) {
			log.error("deleteDocument :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Cannot delete document, it may be linked to other records");
		}

		log.info("deleteDocument :: deleted successfully for id={}", id);

		return new ResponseEntity("Document deleted successfully", HttpStatus.OK.value(), null);
	}

	// Full detail mapping — sirf getById / create response ke liye
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

	// Lightweight mapping — getAll ke liye
	private DocumentListResponse mapToListResponse(Document entity) {
		return new DocumentListResponse(entity.getDocumentId(), entity.getDocumentName(), entity.getDocumentType(),
				entity.getIsSecure());
	}
}