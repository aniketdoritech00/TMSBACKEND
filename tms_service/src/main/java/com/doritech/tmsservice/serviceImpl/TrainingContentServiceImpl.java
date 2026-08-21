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
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.config.FileStorageProperties;
import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.entity.TrainingContent;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.TrainingContentRepository;
import com.doritech.tmsservice.request.TrainingContentRequest;
import com.doritech.tmsservice.response.TrainingContentListResponse;
import com.doritech.tmsservice.response.TrainingContentResponse;
import com.doritech.tmsservice.service.FileStorageService;
import com.doritech.tmsservice.service.TrainingContentService;

@Service
public class TrainingContentServiceImpl implements TrainingContentService {

	private static final Logger log = LoggerFactory.getLogger(TrainingContentServiceImpl.class);

	@Autowired
	private TrainingContentRepository trainingContentRepository;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private FileStorageProperties fileStorageProperties;

	@Override
	public ResponseEntity createTrainingContent(TrainingContentRequest request, MultipartFile file) {

		log.info("createTrainingContent :: request received for trainingId={}, contentType={}", request.getTrainingId(),
				request.getContentType());

		TrainingContent content = new TrainingContent();
		content.setTrainingId(request.getTrainingId());

		TrainingContent.ContentType contentType;
		try {
			contentType = TrainingContent.ContentType.valueOf(request.getContentType());
		} catch (IllegalArgumentException e) {
			log.error("createTrainingContent :: invalid contentType={}", request.getContentType());
			throw new BadRequestException("Invalid content type. Must be one of VIDEO, PDF, DOCUMENT, AUDIO, YOUTUBE");
		}
		content.setContentType(contentType);

		// Case 1: content references an EXISTING video/document record (via
		// content_reference_id)
		if (request.getContentReferenceId() != null) {
			content.setContentReferenceId(request.getContentReferenceId());
			content.setDocumentUrl(null);
		}
		// Case 2: YOUTUBE link — direct URL, no file upload needed
		else if (contentType == TrainingContent.ContentType.YOUTUBE) {
			if (request.getDocumentUrl() == null || request.getDocumentUrl().isBlank()) {
				log.error("createTrainingContent :: youtube url missing");
				throw new BadRequestException("Document URL is required for YOUTUBE content type");
			}
			content.setDocumentUrl(request.getDocumentUrl());
		}
		// Case 3: direct file upload (PDF, AUDIO, new DOCUMENT, new VIDEO not yet in
		// videos/documents table)
		else {
			if (file == null || file.isEmpty()) {
				log.error("createTrainingContent :: file missing for direct upload contentType={}", contentType);
				throw new BadRequestException("Either a file must be uploaded or a document URL must be provided");
			}
			String storedPath = fileStorageService.storeFile(file, fileStorageProperties.getTrainingContentPath());
			content.setDocumentUrl(storedPath);
			content.setFileSizeBytes(file.getSize());
		}

		content.setDocumentName(request.getDocumentName());
		content.setDocumentDescription(request.getDocumentDescription());
		content.setDisplayOrder(request.getDisplayOrder());
		content.setIsRequired(request.getIsRequired());

		TrainingContent saved;
		try {
			saved = trainingContentRepository.save(content);
		} catch (Exception e) {
			log.error("createTrainingContent :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving training content");
		}

		log.info("createTrainingContent :: saved successfully with id={}", saved.getTrainingContentId());

		return new ResponseEntity("Training content saved successfully", HttpStatus.CREATED.value(),
				mapToFullResponse(saved));
	}

	@Override
	public ResponseEntity getTrainingContentById(Long id) {

		log.info("getTrainingContentById :: request received for id={}", id);

		if (id == null) {
			log.error("getTrainingContentById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		TrainingContent content = trainingContentRepository.findById(id).orElseThrow(() -> {
			log.error("getTrainingContentById :: not found for id={}", id);
			return new ResourceNotFoundException("Training content not found with id: " + id);
		});

		log.info("getTrainingContentById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), mapToFullResponse(content));
	}

	@Override
	public ResponseEntity getAllTrainingContent(int page, int size, String sortBy, String sortDir) {

		log.info("getAllTrainingContent :: request received with page={}, size={}, sortBy={}, sortDir={}", page, size,
				sortBy, sortDir);

		if (page < 0) {
			log.error("getAllTrainingContent :: page cannot be negative");
			throw new BadRequestException("Page number can not be negative");
		}

		if (size <= 0) {
			log.error("getAllTrainingContent :: size must be greater than 0");
			throw new BadRequestException("Page size must be greater than 0");
		}

		if (size > 100) {
			log.error("getAllTrainingContent :: size exceeds max limit={}", size);
			throw new BadRequestException("Page size can not exceed 100");
		}

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<TrainingContent> contentPage;
		try {
			contentPage = trainingContentRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			log.error("getAllTrainingContent :: invalid sort field={}", sortBy);
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			log.error("getAllTrainingContent :: error while fetching - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while fetching training content");
		}

		// NOTE: getAll me sirf lightweight fields — document_url/file details nahi
		// bhejte.
		// Full detail sirf getTrainingContentById se milega.
		List<TrainingContentListResponse> responseList = contentPage.getContent().stream().map(this::mapToListResponse)
				.collect(Collectors.toList());

		Map<String, Object> pageData = new LinkedHashMap<>();
		pageData.put("content", responseList);
		pageData.put("pageNumber", contentPage.getNumber());
		pageData.put("pageSize", contentPage.getSize());
		pageData.put("totalElements", contentPage.getTotalElements());
		pageData.put("totalPages", contentPage.getTotalPages());
		pageData.put("isLast", contentPage.isLast());

		log.info("getAllTrainingContent :: {} of {} training content fetched successfully", responseList.size(),
				contentPage.getTotalElements());

		return new ResponseEntity("Training content fetch successfully", HttpStatus.OK.value(), pageData);
	}

	@Override
	public ResponseEntity getContentByTrainingId(Long trainingId) {

		log.info("getContentByTrainingId :: request received for trainingId={}", trainingId);

		if (trainingId == null) {
			log.error("getContentByTrainingId :: trainingId is null");
			throw new BadRequestException("Training ID can not be null");
		}

		List<TrainingContent> contentList = trainingContentRepository
				.findByTrainingIdOrderByDisplayOrderAsc(trainingId);

		// Ye bhi lightweight rakha hai - training ke andar content list dikhani hai,
		// poora detail nahi chahiye
		List<TrainingContentListResponse> responseList = contentList.stream().map(this::mapToListResponse)
				.collect(Collectors.toList());

		log.info("getContentByTrainingId :: {} content items fetched for trainingId={}", responseList.size(),
				trainingId);

		return new ResponseEntity("Training content fetch successfully", HttpStatus.OK.value(), responseList);
	}

	@Override
	public ResponseEntity deleteTrainingContent(Long id) {

		log.info("deleteTrainingContent :: request received for id={}", id);

		if (id == null) {
			log.error("deleteTrainingContent :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		TrainingContent content = trainingContentRepository.findById(id).orElseThrow(() -> {
			log.error("deleteTrainingContent :: not found for id={}", id);
			return new ResourceNotFoundException("Training content not found with id: " + id);
		});

		try {
			trainingContentRepository.delete(content);
		} catch (Exception e) {
			log.error("deleteTrainingContent :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Cannot delete training content, it may be linked to other records");
		}

		log.info("deleteTrainingContent :: deleted successfully for id={}", id);

		return new ResponseEntity("Training content deleted successfully", HttpStatus.OK.value(), null);
	}

	// Full detail mapping — sirf getById / create response ke liye
	private TrainingContentResponse mapToFullResponse(TrainingContent entity) {
		TrainingContentResponse response = new TrainingContentResponse();
		response.setTrainingContentId(entity.getTrainingContentId());
		response.setTrainingId(entity.getTrainingId());
		response.setContentType(entity.getContentType() != null ? entity.getContentType().name() : null);
		response.setContentReferenceId(entity.getContentReferenceId());
		response.setDocumentName(entity.getDocumentName());
		response.setDocumentDescription(entity.getDocumentDescription());
		response.setDocumentUrl(entity.getDocumentUrl());
		response.setFileSizeBytes(entity.getFileSizeBytes());
		response.setDisplayOrder(entity.getDisplayOrder());
		response.setIsRequired(entity.getIsRequired());
		response.setCreatedAt(entity.getCreatedAt());
		return response;
	}

	// Lightweight mapping — getAll / getByTrainingId ke liye
	private TrainingContentListResponse mapToListResponse(TrainingContent entity) {
		return new TrainingContentListResponse(entity.getTrainingContentId(), entity.getTrainingId(),
				entity.getContentType() != null ? entity.getContentType().name() : null, entity.getDisplayOrder(),
				entity.getIsRequired());
	}
}