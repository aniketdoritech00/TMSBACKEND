package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.enums.TestQuestionType;
import com.doritech.tmsservice.request.TestQuestionRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.QuestionOptionResponse;
import com.doritech.tmsservice.response.TestQuestionResponse;
import com.doritech.tmsservice.service.TestQuestionService;
import com.doritech.tmsservice.tms.entity.QuestionOption;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TestQuestion;
import com.doritech.tmsservice.tms.entity.TestSet;
import com.doritech.tmsservice.tms.repository.TestQuestionRepository;
import com.doritech.tmsservice.tms.repository.TestSetRepository;

@Service
public class TestQuestionServiceImpl implements TestQuestionService {

	private final TestQuestionRepository testQuestionRepository;
	private final TestSetRepository testSetRepository;

	public TestQuestionServiceImpl(TestQuestionRepository testQuestionRepository, TestSetRepository testSetRepository) {

		this.testQuestionRepository = testQuestionRepository;
		this.testSetRepository = testSetRepository;
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity createTestQuestion(TestQuestionRequest request) {

		try {

			if (request == null) {

				return new ResponseEntity("Test question data is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTestSetId() == null || request.getTestSetId() <= 0) {

				return new ResponseEntity("Valid test set id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getQuestionText() == null || request.getQuestionText().trim().isEmpty()) {

				return new ResponseEntity("Question text is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getQuestionType() == null || request.getQuestionType().trim().isEmpty()) {

				return new ResponseEntity("Question type is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestSet> optionalTestSet = testSetRepository.findById(request.getTestSetId());

			if (optionalTestSet.isEmpty()) {

				return new ResponseEntity("Test set not found with id: " + request.getTestSetId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			TestQuestionType questionType;

			try {

				questionType = TestQuestionType.valueOf(request.getQuestionType().trim().toUpperCase());

			} catch (IllegalArgumentException e) {

				return new ResponseEntity("Invalid question type: " + request.getQuestionType(),
						HttpStatus.BAD_REQUEST.value(), null);
			}

			boolean alreadyExists = testQuestionRepository
					.existsByTestSet_TestSetIdAndQuestionText(request.getTestSetId(), request.getQuestionText().trim());

			if (alreadyExists) {

				return new ResponseEntity("Test question already exists for this test set", HttpStatus.CONFLICT.value(),
						null);
			}

			TestQuestion testQuestion = new TestQuestion();

			testQuestion.setTestSet(optionalTestSet.get());

			testQuestion.setQuestionGroupId(request.getQuestionGroupId());

			testQuestion.setQuestionText(request.getQuestionText().trim());

			testQuestion.setQuestionType(questionType);

			testQuestion.setCorrectAnswer(request.getCorrectAnswer());

			testQuestion
					.setTimeLimitSeconds(request.getTimeLimitSeconds() != null ? request.getTimeLimitSeconds() : 60);

			testQuestion.setMarks(request.getMarks() != null ? request.getMarks() : new java.math.BigDecimal("1.00"));

			testQuestion.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

			testQuestion.setCreatedAt(LocalDateTime.now());

			testQuestion.setUpdatedAt(LocalDateTime.now());

			TestQuestion savedQuestion = testQuestionRepository.save(testQuestion);

			return new ResponseEntity("Test question created successfully", HttpStatus.CREATED.value(),
					mapToResponse(savedQuestion));

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("Test question already exists or violates database constraints",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTestQuestionById(Long id) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid test question id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestQuestion> optionalQuestion = testQuestionRepository.findById(id);

			if (optionalQuestion.isEmpty()) {

				return new ResponseEntity("Test question not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			return new ResponseEntity("Test question fetched successfully", HttpStatus.OK.value(),
					mapToResponse(optionalQuestion.get()));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllTestQuestions() {

		try {

			List<TestQuestion> questions = testQuestionRepository.findAll(Sort.by(Sort.Direction.ASC, "displayOrder"));

			List<TestQuestionResponse> response = questions.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Test questions fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllTestQuestions(int page, int size, String sortBy, String sortDir) {

		try {

			if (page < 0) {
				page = 0;
			}

			if (size <= 0) {
				size = 10;
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "testQuestionId";
			}

			Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<TestQuestion> questionPage = testQuestionRepository.findAll(pageable);

			List<TestQuestionResponse> content = questionPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<TestQuestionResponse> pageResponse = new PageResponse<>(content, questionPage.getNumber(),
					questionPage.getSize(), questionPage.getTotalElements(), questionPage.getTotalPages(),
					questionPage.isLast());

			return new ResponseEntity("Test questions fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTestQuestionsByTestSetId(Long testSetId) {

		try {

			if (testSetId == null || testSetId <= 0) {

				return new ResponseEntity("Invalid test set id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestSet> optionalTestSet = testSetRepository.findById(testSetId);

			if (optionalTestSet.isEmpty()) {

				return new ResponseEntity("Test set not found with id: " + testSetId, HttpStatus.NOT_FOUND.value(),
						null);
			}

			List<TestQuestion> questions = testQuestionRepository
					.findByTestSet_TestSetIdOrderByDisplayOrderAsc(testSetId);

			List<TestQuestionResponse> response = questions.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Test questions fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getTestQuestionsByTestSetId(Long testSetId, int page, int size, String sortBy,
			String sortDir) {

		try {

			if (testSetId == null || testSetId <= 0) {

				return new ResponseEntity("Invalid test set id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestSet> optionalTestSet = testSetRepository.findById(testSetId);

			if (optionalTestSet.isEmpty()) {

				return new ResponseEntity("Test set not found with id: " + testSetId, HttpStatus.NOT_FOUND.value(),
						null);
			}

			if (page < 0) {
				page = 0;
			}

			if (size <= 0) {
				size = 10;
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "displayOrder";
			}

			Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<TestQuestion> questionPage = testQuestionRepository.findByTestSet_TestSetId(testSetId, pageable);

			List<TestQuestionResponse> content = questionPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<TestQuestionResponse> pageResponse = new PageResponse<>(content, questionPage.getNumber(),
					questionPage.getSize(), questionPage.getTotalElements(), questionPage.getTotalPages(),
					questionPage.isLast());

			return new ResponseEntity("Test questions fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity updateTestQuestion(Long id, TestQuestionRequest request) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid test question id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request == null) {

				return new ResponseEntity("Test question data is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTestSetId() == null || request.getTestSetId() <= 0) {

				return new ResponseEntity("Valid test set id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getQuestionText() == null || request.getQuestionText().trim().isEmpty()) {

				return new ResponseEntity("Question text is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getQuestionType() == null || request.getQuestionType().trim().isEmpty()) {

				return new ResponseEntity("Question type is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestQuestion> optionalQuestion = testQuestionRepository.findById(id);

			if (optionalQuestion.isEmpty()) {

				return new ResponseEntity("Test question not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			Optional<TestSet> optionalTestSet = testSetRepository.findById(request.getTestSetId());

			if (optionalTestSet.isEmpty()) {

				return new ResponseEntity("Test set not found with id: " + request.getTestSetId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			TestQuestionType questionType;

			try {

				questionType = TestQuestionType.valueOf(request.getQuestionType().trim().toUpperCase());

			} catch (IllegalArgumentException e) {

				return new ResponseEntity("Invalid question type: " + request.getQuestionType(),
						HttpStatus.BAD_REQUEST.value(), null);
			}

			boolean alreadyExists = testQuestionRepository.existsByTestSet_TestSetIdAndQuestionTextAndTestQuestionIdNot(
					request.getTestSetId(), request.getQuestionText().trim(), id);

			if (alreadyExists) {

				return new ResponseEntity("Another test question with the same text already exists for this test set",
						HttpStatus.CONFLICT.value(), null);
			}

			TestQuestion question = optionalQuestion.get();

			question.setTestSet(optionalTestSet.get());

			question.setQuestionGroupId(request.getQuestionGroupId());

			question.setQuestionText(request.getQuestionText().trim());

			question.setQuestionType(questionType);

			question.setCorrectAnswer(request.getCorrectAnswer());

			if (request.getTimeLimitSeconds() != null) {
				question.setTimeLimitSeconds(request.getTimeLimitSeconds());
			}

			if (request.getMarks() != null) {
				question.setMarks(request.getMarks());
			}

			if (request.getDisplayOrder() != null) {
				question.setDisplayOrder(request.getDisplayOrder());
			}

			question.setUpdatedAt(LocalDateTime.now());

			TestQuestion updatedQuestion = testQuestionRepository.save(question);

			return new ResponseEntity("Test question updated successfully", HttpStatus.OK.value(),
					mapToResponse(updatedQuestion));

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("Test question already exists or violates database constraints",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity deleteTestQuestion(Long id) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid test question id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestQuestion> optionalQuestion = testQuestionRepository.findById(id);

			if (optionalQuestion.isEmpty()) {

				return new ResponseEntity("Test question not found with id: " + id, HttpStatus.NOT_FOUND.value(), null);
			}

			testQuestionRepository.delete(optionalQuestion.get());

			return new ResponseEntity("Test question deleted successfully", HttpStatus.OK.value(), null);

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("Cannot delete test question because it is being used",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private TestQuestionResponse mapToResponse(TestQuestion question) {

		TestQuestionResponse response = new TestQuestionResponse();

		response.setTestQuestionId(question.getTestQuestionId());

		if (question.getTestSet() != null) {

			response.setTestSetId(question.getTestSet().getTestSetId());

			response.setTestName(question.getTestSet().getTestName());
		}

		response.setQuestionGroupId(question.getQuestionGroupId());

		response.setQuestionText(question.getQuestionText());

		response.setQuestionType(question.getQuestionType() != null ? question.getQuestionType().name() : null);

		response.setCorrectAnswer(question.getCorrectAnswer());

		response.setTimeLimitSeconds(question.getTimeLimitSeconds());

		response.setMarks(question.getMarks());

		response.setDisplayOrder(question.getDisplayOrder());

		response.setCreatedAt(question.getCreatedAt());

		response.setUpdatedAt(question.getUpdatedAt());

		if (question.getQuestionOptions() != null) {

			List<QuestionOptionResponse> options = question.getQuestionOptions().stream().map(this::mapOptionToResponse)
					.collect(Collectors.toList());

			response.setOptions(options);
		}

		return response;
	}

	private QuestionOptionResponse mapOptionToResponse(QuestionOption option) {

		QuestionOptionResponse response = new QuestionOptionResponse();

		response.setQuestionOptionId(option.getQuestionOptionId());

		if (option.getTestQuestion() != null) {

			response.setTestQuestionId(option.getTestQuestion().getTestQuestionId());
		}

		response.setOptionText(option.getOptionText());

		response.setOptionLabel(option.getOptionLabel());

		response.setIsCorrect(option.getIsCorrect());

		response.setDisplayOrder(option.getDisplayOrder());

		return response;
	}
}