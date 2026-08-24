package com.doritech.tmsservice.serviceImpl;

import java.util.ArrayList;
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

import com.doritech.tmsservice.entity.InTrainingQuestion;
import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.InTrainingQuestionRepository;
import com.doritech.tmsservice.request.InTrainingQuestionRequest;
import com.doritech.tmsservice.response.InTrainingQuestionListResponse;
import com.doritech.tmsservice.response.InTrainingQuestionResponse;
import com.doritech.tmsservice.response.InTrainingQuestionSecureResponse;
import com.doritech.tmsservice.service.InTrainingQuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class InTrainingQuestionServiceImpl implements InTrainingQuestionService {

	private static final Logger log = LoggerFactory.getLogger(InTrainingQuestionServiceImpl.class);

	private final InTrainingQuestionRepository inTrainingQuestionRepository;

	public InTrainingQuestionServiceImpl(InTrainingQuestionRepository inTrainingQuestionRepository) {
		this.inTrainingQuestionRepository = inTrainingQuestionRepository;
	}

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public ResponseEntity createInTrainingQuestion(InTrainingQuestionRequest request) {

		log.info("createInTrainingQuestion :: request received for videoId={}, timestamp={}", request.getVideoId(),
				request.getTimestampSeconds());

		InTrainingQuestion question = new InTrainingQuestion();
		question.setVideoId(request.getVideoId());
		question.setTimestampSeconds(request.getTimestampSeconds());
		question.setQuestionText(request.getQuestionText());

		if (request.getQuestionType() != null) {
			try {
				question.setQuestionType(InTrainingQuestion.QuestionType.valueOf(request.getQuestionType()));
			} catch (IllegalArgumentException e) {
				log.error("createInTrainingQuestion :: invalid questionType={}", request.getQuestionType());
				throw new BadRequestException(
						"Invalid question type. Must be one of MCQ, FILL_IN_BLANKS, ONE_WORD, DROPDOWN");
			}
		}

		if (request.getOptions() != null && !request.getOptions().isEmpty()) {
			try {
				question.setOptions(objectMapper.writeValueAsString(request.getOptions()));
			} catch (Exception e) {
				log.error("createInTrainingQuestion :: error converting options to JSON - {}", e.getMessage(), e);
				throw new BadRequestException("Invalid options format");
			}
		}

		question.setCorrectAnswer(request.getCorrectAnswer());
		question.setTimerSeconds(request.getTimerSeconds());
		question.setIsRequired(request.getIsRequired());
		question.setDisplayOrder(request.getDisplayOrder());

		InTrainingQuestion saved;
		try {
			saved = inTrainingQuestionRepository.save(question);
		} catch (Exception e) {
			log.error("createInTrainingQuestion :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving in-training question");
		}

		log.info("createInTrainingQuestion :: saved successfully with id={}", saved.getQuestionId());

		return new ResponseEntity("In-training question saved successfully", HttpStatus.CREATED.value(),
				mapToFullResponse(saved));
	}

	@Override
	public ResponseEntity getInTrainingQuestionById(Long id) {

		log.info("getInTrainingQuestionById :: request received for id={}", id);

		if (id == null) {
			log.error("getInTrainingQuestionById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		InTrainingQuestion question = inTrainingQuestionRepository.findById(id).orElseThrow(() -> {
			log.error("getInTrainingQuestionById :: not found for id={}", id);
			return new ResourceNotFoundException("In-training question not found with id: " + id);
		});

		log.info("getInTrainingQuestionById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), mapToFullResponse(question));
	}

	@Override
	public ResponseEntity getAllInTrainingQuestion(int page, int size, String sortBy, String sortDir) {

		log.info("getAllInTrainingQuestion :: request received with page={}, size={}, sortBy={}, sortDir={}", page,
				size, sortBy, sortDir);

		if (page < 0) {
			log.error("getAllInTrainingQuestion :: page cannot be negative");
			throw new BadRequestException("Page number can not be negative");
		}

		if (size <= 0) {
			log.error("getAllInTrainingQuestion :: size must be greater than 0");
			throw new BadRequestException("Page size must be greater than 0");
		}

		if (size > 100) {
			log.error("getAllInTrainingQuestion :: size exceeds max limit={}", size);
			throw new BadRequestException("Page size can not exceed 100");
		}

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<InTrainingQuestion> questionPage;
		try {
			questionPage = inTrainingQuestionRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			log.error("getAllInTrainingQuestion :: invalid sort field={}", sortBy);
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			log.error("getAllInTrainingQuestion :: error while fetching - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while fetching in-training questions");
		}

		List<InTrainingQuestionListResponse> responseList = questionPage.getContent().stream()
				.map(this::mapToListResponse).collect(Collectors.toList());

		Map<String, Object> pageData = new LinkedHashMap<>();
		pageData.put("content", responseList);
		pageData.put("pageNumber", questionPage.getNumber());
		pageData.put("pageSize", questionPage.getSize());
		pageData.put("totalElements", questionPage.getTotalElements());
		pageData.put("totalPages", questionPage.getTotalPages());
		pageData.put("isLast", questionPage.isLast());

		log.info("getAllInTrainingQuestion :: {} of {} questions fetched successfully", responseList.size(),
				questionPage.getTotalElements());

		return new ResponseEntity("In-training question fetch successfully", HttpStatus.OK.value(), pageData);
	}

	@Override
	public ResponseEntity getQuestionsByVideoId(Long videoId) {

		log.info("getQuestionsByVideoId :: request received for videoId={}", videoId);

		if (videoId == null) {
			log.error("getQuestionsByVideoId :: videoId is null");
			throw new BadRequestException("Video ID can not be null");
		}

		List<InTrainingQuestion> questionList = inTrainingQuestionRepository
				.findByVideoIdOrderByTimestampSecondsAsc(videoId);

		List<InTrainingQuestionSecureResponse> responseList = questionList.stream().map(this::mapToSecureResponse)
				.collect(Collectors.toList());

		log.info("getQuestionsByVideoId :: {} questions fetched for videoId={}", responseList.size(), videoId);

		return new ResponseEntity("In-training question fetch successfully", HttpStatus.OK.value(), responseList);
	}

	@SuppressWarnings("unchecked")
	private InTrainingQuestionSecureResponse mapToSecureResponse(InTrainingQuestion entity) {
		InTrainingQuestionSecureResponse response = new InTrainingQuestionSecureResponse();
		response.setQuestionId(entity.getQuestionId());
		response.setVideoId(entity.getVideoId());
		response.setTimestampSeconds(entity.getTimestampSeconds());
		response.setQuestionText(entity.getQuestionText());
		response.setQuestionType(entity.getQuestionType() != null ? entity.getQuestionType().name() : null);

		if (entity.getOptions() != null) {
			try {
				response.setOptions(objectMapper.readValue(entity.getOptions(), List.class));
			} catch (Exception e) {
				log.error("mapToSecureResponse :: error parsing options JSON for questionId={} - {}",
						entity.getQuestionId(), e.getMessage());
				response.setOptions(new ArrayList<>());
			}
		}

		response.setTimerSeconds(entity.getTimerSeconds());
		response.setIsRequired(entity.getIsRequired());
		response.setDisplayOrder(entity.getDisplayOrder());
		return response;
	}

	@Override
	public ResponseEntity deleteInTrainingQuestion(Long id) {

		log.info("deleteInTrainingQuestion :: request received for id={}", id);

		if (id == null) {
			log.error("deleteInTrainingQuestion :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		InTrainingQuestion question = inTrainingQuestionRepository.findById(id).orElseThrow(() -> {
			log.error("deleteInTrainingQuestion :: not found for id={}", id);
			return new ResourceNotFoundException("In-training question not found with id: " + id);
		});

		try {
			inTrainingQuestionRepository.delete(question);
		} catch (Exception e) {
			log.error("deleteInTrainingQuestion :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException(
					"Cannot delete in-training question, it may be linked to other records");
		}

		log.info("deleteInTrainingQuestion :: deleted successfully for id={}", id);

		return new ResponseEntity("In-training question deleted successfully", HttpStatus.OK.value(), null);
	}

	@SuppressWarnings("unchecked")
	private InTrainingQuestionResponse mapToFullResponse(InTrainingQuestion entity) {
		InTrainingQuestionResponse response = new InTrainingQuestionResponse();
		response.setQuestionId(entity.getQuestionId());
		response.setVideoId(entity.getVideoId());
		response.setTimestampSeconds(entity.getTimestampSeconds());
		response.setQuestionText(entity.getQuestionText());
		response.setQuestionType(entity.getQuestionType() != null ? entity.getQuestionType().name() : null);

		if (entity.getOptions() != null) {
			try {
				response.setOptions(objectMapper.readValue(entity.getOptions(), List.class));
			} catch (Exception e) {
				log.error("mapToFullResponse :: error parsing options JSON for questionId={} - {}",
						entity.getQuestionId(), e.getMessage());
				response.setOptions(new ArrayList<>());
			}
		}

		response.setCorrectAnswer(entity.getCorrectAnswer());
		response.setTimerSeconds(entity.getTimerSeconds());
		response.setIsRequired(entity.getIsRequired());
		response.setDisplayOrder(entity.getDisplayOrder());
		response.setCreatedAt(entity.getCreatedAt());
		response.setUpdatedAt(entity.getUpdatedAt());
		return response;
	}

	private InTrainingQuestionListResponse mapToListResponse(InTrainingQuestion entity) {
		return new InTrainingQuestionListResponse(entity.getQuestionId(), entity.getVideoId(),
				entity.getTimestampSeconds(), entity.getQuestionType() != null ? entity.getQuestionType().name() : null,
				entity.getDisplayOrder());
	}
}