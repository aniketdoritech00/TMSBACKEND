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
import com.doritech.tmsservice.entity.TestQuestion;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.TestQuestionRepository;
import com.doritech.tmsservice.request.TestQuestionRequest;
import com.doritech.tmsservice.response.TestQuestionListResponse;
import com.doritech.tmsservice.response.TestQuestionResponse;
import com.doritech.tmsservice.response.TestQuestionSecureResponse;
import com.doritech.tmsservice.service.TestQuestionService;

@Service
public class TestQuestionServiceImpl implements TestQuestionService {

	private static final Logger log = LoggerFactory.getLogger(TestQuestionServiceImpl.class);

	private final TestQuestionRepository testQuestionRepository;

	public TestQuestionServiceImpl(TestQuestionRepository testQuestionRepository) {
		this.testQuestionRepository = testQuestionRepository;
	}

	@Override
	public ResponseEntity createTestQuestion(TestQuestionRequest request) {

		log.info("createTestQuestion :: request received for testSetId={}", request.getTestSetId());

		TestQuestion question = new TestQuestion();
		question.setTestSetId(request.getTestSetId());
		question.setQuestionGroupId(request.getQuestionGroupId());
		question.setQuestionText(request.getQuestionText());

		if (request.getQuestionType() != null) {
			try {
				question.setQuestionType(TestQuestion.QuestionType.valueOf(request.getQuestionType()));
			} catch (IllegalArgumentException e) {
				log.error("createTestQuestion :: invalid questionType={}", request.getQuestionType());
				throw new BadRequestException(
						"Invalid question type. Must be one of MCQ, DROPDOWN, FILL_IN_BLANKS, ONE_WORD");
			}
		}

		question.setCorrectAnswer(request.getCorrectAnswer());
		question.setTimeLimitSeconds(request.getTimeLimitSeconds());
		question.setMarks(request.getMarks());
		question.setDisplayOrder(request.getDisplayOrder());

		TestQuestion saved;
		try {
			saved = testQuestionRepository.save(question);
		} catch (Exception e) {
			log.error("createTestQuestion :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving test question");
		}

		log.info("createTestQuestion :: saved successfully with id={}", saved.getTestQuestionId());

		return new ResponseEntity("Test question saved successfully", HttpStatus.CREATED.value(),
				mapToFullResponse(saved));
	}

	@Override
	public ResponseEntity getTestQuestionById(Long id) {

		log.info("getTestQuestionById :: request received for id={}", id);

		if (id == null) {
			log.error("getTestQuestionById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		TestQuestion question = testQuestionRepository.findById(id).orElseThrow(() -> {
			log.error("getTestQuestionById :: not found for id={}", id);
			return new ResourceNotFoundException("Test question not found with id: " + id);
		});

		log.info("getTestQuestionById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), mapToFullResponse(question));
	}

	@Override
	public ResponseEntity getAllTestQuestion(int page, int size, String sortBy, String sortDir) {

		log.info("getAllTestQuestion :: request received with page={}, size={}, sortBy={}, sortDir={}", page, size,
				sortBy, sortDir);

		if (page < 0) {
			log.error("getAllTestQuestion :: page cannot be negative");
			throw new BadRequestException("Page number can not be negative");
		}

		if (size <= 0) {
			log.error("getAllTestQuestion :: size must be greater than 0");
			throw new BadRequestException("Page size must be greater than 0");
		}

		if (size > 100) {
			log.error("getAllTestQuestion :: size exceeds max limit={}", size);
			throw new BadRequestException("Page size can not exceed 100");
		}

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<TestQuestion> questionPage;
		try {
			questionPage = testQuestionRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			log.error("getAllTestQuestion :: invalid sort field={}", sortBy);
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			log.error("getAllTestQuestion :: error while fetching - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while fetching test questions");
		}

		List<TestQuestionListResponse> responseList = questionPage.getContent().stream().map(this::mapToListResponse)
				.collect(Collectors.toList());

		Map<String, Object> pageData = new LinkedHashMap<>();
		pageData.put("content", responseList);
		pageData.put("pageNumber", questionPage.getNumber());
		pageData.put("pageSize", questionPage.getSize());
		pageData.put("totalElements", questionPage.getTotalElements());
		pageData.put("totalPages", questionPage.getTotalPages());
		pageData.put("isLast", questionPage.isLast());

		log.info("getAllTestQuestion :: {} of {} test questions fetched successfully", responseList.size(),
				questionPage.getTotalElements());

		return new ResponseEntity("Test question fetch successfully", HttpStatus.OK.value(), pageData);
	}

	@Override
	public ResponseEntity getQuestionsByTestSetId(Long testSetId) {

		log.info("getQuestionsByTestSetId :: request received for testSetId={}", testSetId);

		if (testSetId == null) {
			log.error("getQuestionsByTestSetId :: testSetId is null");
			throw new BadRequestException("Test Set ID can not be null");
		}

		List<TestQuestion> questionList = testQuestionRepository.findByTestSetIdOrderByDisplayOrderAsc(testSetId);

		List<TestQuestionSecureResponse> responseList = questionList.stream().map(this::mapToSecureResponse)
				.collect(Collectors.toList());

		log.info("getQuestionsByTestSetId :: {} questions fetched for testSetId={}", responseList.size(), testSetId);

		return new ResponseEntity("Test question fetch successfully", HttpStatus.OK.value(), responseList);
	}

	private TestQuestionSecureResponse mapToSecureResponse(TestQuestion entity) {
		return new TestQuestionSecureResponse(entity.getTestQuestionId(), entity.getTestSetId(),
				entity.getQuestionGroupId(), entity.getQuestionText(),
				entity.getQuestionType() != null ? entity.getQuestionType().name() : null, entity.getTimeLimitSeconds(),
				entity.getMarks(), entity.getDisplayOrder());
	}

	@Override
	public ResponseEntity deleteTestQuestion(Long id) {

		log.info("deleteTestQuestion :: request received for id={}", id);

		if (id == null) {
			log.error("deleteTestQuestion :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		TestQuestion question = testQuestionRepository.findById(id).orElseThrow(() -> {
			log.error("deleteTestQuestion :: not found for id={}", id);
			return new ResourceNotFoundException("Test question not found with id: " + id);
		});

		try {
			testQuestionRepository.delete(question);
		} catch (Exception e) {
			log.error("deleteTestQuestion :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Cannot delete test question, it may be linked to other records");
		}

		log.info("deleteTestQuestion :: deleted successfully for id={}", id);

		return new ResponseEntity("Test question deleted successfully", HttpStatus.OK.value(), null);
	}

	private TestQuestionResponse mapToFullResponse(TestQuestion entity) {
		TestQuestionResponse response = new TestQuestionResponse();
		response.setTestQuestionId(entity.getTestQuestionId());
		response.setTestSetId(entity.getTestSetId());
		response.setQuestionGroupId(entity.getQuestionGroupId());
		response.setQuestionText(entity.getQuestionText());
		response.setQuestionType(entity.getQuestionType() != null ? entity.getQuestionType().name() : null);
		response.setCorrectAnswer(entity.getCorrectAnswer());
		response.setTimeLimitSeconds(entity.getTimeLimitSeconds());
		response.setMarks(entity.getMarks());
		response.setDisplayOrder(entity.getDisplayOrder());
		response.setCreatedAt(entity.getCreatedAt());
		response.setUpdatedAt(entity.getUpdatedAt());
		return response;
	}

	private TestQuestionListResponse mapToListResponse(TestQuestion entity) {
		return new TestQuestionListResponse(entity.getTestQuestionId(), entity.getTestSetId(),
				entity.getQuestionType() != null ? entity.getQuestionType().name() : null, entity.getMarks(),
				entity.getDisplayOrder());
	}
}