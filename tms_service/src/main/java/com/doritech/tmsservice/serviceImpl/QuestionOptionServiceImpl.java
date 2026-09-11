package com.doritech.tmsservice.serviceImpl;

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

import com.doritech.tmsservice.request.QuestionOptionRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.QuestionOptionResponse;
import com.doritech.tmsservice.service.QuestionOptionService;
import com.doritech.tmsservice.tms.entity.QuestionOption;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TestQuestion;
import com.doritech.tmsservice.tms.repository.QuestionOptionRepository;
import com.doritech.tmsservice.tms.repository.TestQuestionRepository;

@Service
public class QuestionOptionServiceImpl implements QuestionOptionService {

	private final QuestionOptionRepository questionOptionRepository;
	private final TestQuestionRepository testQuestionRepository;

	public QuestionOptionServiceImpl(QuestionOptionRepository questionOptionRepository,
			TestQuestionRepository testQuestionRepository) {

		this.questionOptionRepository = questionOptionRepository;

		this.testQuestionRepository = testQuestionRepository;
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity createQuestionOption(QuestionOptionRequest request) {
		try {
			if (request == null) {
				return new ResponseEntity("Question option data is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTestQuestionId() == null || request.getTestQuestionId() <= 0) {
				return new ResponseEntity("Valid test question id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getOptionText() == null || request.getOptionText().trim().isEmpty()) {
				return new ResponseEntity("Option text is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestQuestion> optionalQuestion = testQuestionRepository.findById(request.getTestQuestionId());
			if (optionalQuestion.isEmpty()) {
				return new ResponseEntity("Test question not found with id: " + request.getTestQuestionId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			boolean alreadyExists = questionOptionRepository.existsByTestQuestion_TestQuestionIdAndOptionText(
					request.getTestQuestionId(), request.getOptionText().trim());

			if (alreadyExists) {

				return new ResponseEntity("Question option already exists", HttpStatus.CONFLICT.value(), null);
			}

			QuestionOption option = new QuestionOption();

			option.setTestQuestion(optionalQuestion.get());

			option.setOptionText(request.getOptionText().trim());

			option.setOptionLabel(request.getOptionLabel());

			option.setIsCorrect(request.getIsCorrect() != null ? request.getIsCorrect() : false);

			option.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

			QuestionOption savedOption = questionOptionRepository.save(option);

			return new ResponseEntity("Question option created successfully", HttpStatus.CREATED.value(),
					mapToResponse(savedOption));

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("Question option already exists or violates database constraints",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getQuestionOptionById(Long id) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid question option id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<QuestionOption> optionalOption = questionOptionRepository.findById(id);

			if (optionalOption.isEmpty()) {

				return new ResponseEntity("Question option not found with id: " + id, HttpStatus.NOT_FOUND.value(),
						null);
			}

			return new ResponseEntity("Question option fetched successfully", HttpStatus.OK.value(),
					mapToResponse(optionalOption.get()));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllQuestionOptions() {

		try {

			List<QuestionOption> options = questionOptionRepository
					.findAll(Sort.by(Sort.Direction.ASC, "displayOrder"));

			List<QuestionOptionResponse> response = options.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Question options fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllQuestionOptions(int page, int size, String sortBy, String sortDir) {

		try {

			if (page < 0) {
				page = 0;
			}

			if (size <= 0) {
				size = 10;
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "questionOptionId";
			}

			Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<QuestionOption> optionPage = questionOptionRepository.findAll(pageable);

			List<QuestionOptionResponse> content = optionPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<QuestionOptionResponse> pageResponse = new PageResponse<>(content, optionPage.getNumber(),
					optionPage.getSize(), optionPage.getTotalElements(), optionPage.getTotalPages(),
					optionPage.isLast());

			return new ResponseEntity("Question options fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getQuestionOptionsByTestQuestionId(Long testQuestionId) {

		try {

			if (testQuestionId == null || testQuestionId <= 0) {

				return new ResponseEntity("Invalid test question id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestQuestion> optionalQuestion = testQuestionRepository.findById(testQuestionId);

			if (optionalQuestion.isEmpty()) {

				return new ResponseEntity("Test question not found with id: " + testQuestionId,
						HttpStatus.NOT_FOUND.value(), null);
			}

			List<QuestionOption> options = questionOptionRepository
					.findByTestQuestion_TestQuestionIdOrderByDisplayOrderAsc(testQuestionId);

			List<QuestionOptionResponse> response = options.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Question options fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getQuestionOptionsByTestQuestionId(Long testQuestionId, int page, int size, String sortBy,
			String sortDir) {

		try {

			if (testQuestionId == null || testQuestionId <= 0) {

				return new ResponseEntity("Invalid test question id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<TestQuestion> optionalQuestion = testQuestionRepository.findById(testQuestionId);

			if (optionalQuestion.isEmpty()) {

				return new ResponseEntity("Test question not found with id: " + testQuestionId,
						HttpStatus.NOT_FOUND.value(), null);
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

			Page<QuestionOption> optionPage = questionOptionRepository.findByTestQuestion_TestQuestionId(testQuestionId,
					pageable);

			List<QuestionOptionResponse> content = optionPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<QuestionOptionResponse> pageResponse = new PageResponse<>(content, optionPage.getNumber(),
					optionPage.getSize(), optionPage.getTotalElements(), optionPage.getTotalPages(),
					optionPage.isLast());

			return new ResponseEntity("Question options fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity updateQuestionOption(Long id, QuestionOptionRequest request) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid question option id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request == null) {

				return new ResponseEntity("Question option data is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTestQuestionId() == null || request.getTestQuestionId() <= 0) {

				return new ResponseEntity("Valid test question id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getOptionText() == null || request.getOptionText().trim().isEmpty()) {

				return new ResponseEntity("Option text is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<QuestionOption> optionalOption = questionOptionRepository.findById(id);

			if (optionalOption.isEmpty()) {

				return new ResponseEntity("Question option not found with id: " + id, HttpStatus.NOT_FOUND.value(),
						null);
			}

			Optional<TestQuestion> optionalQuestion = testQuestionRepository.findById(request.getTestQuestionId());

			if (optionalQuestion.isEmpty()) {

				return new ResponseEntity("Test question not found with id: " + request.getTestQuestionId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			boolean alreadyExists = questionOptionRepository
					.existsByTestQuestion_TestQuestionIdAndOptionTextAndQuestionOptionIdNot(request.getTestQuestionId(),
							request.getOptionText().trim(), id);

			if (alreadyExists) {

				return new ResponseEntity("Another question option with the same text already exists",
						HttpStatus.CONFLICT.value(), null);
			}

			QuestionOption option = optionalOption.get();

			option.setTestQuestion(optionalQuestion.get());

			option.setOptionText(request.getOptionText().trim());

			option.setOptionLabel(request.getOptionLabel());

			if (request.getIsCorrect() != null) {
				option.setIsCorrect(request.getIsCorrect());
			}

			if (request.getDisplayOrder() != null) {
				option.setDisplayOrder(request.getDisplayOrder());
			}

			QuestionOption updatedOption = questionOptionRepository.save(option);

			return new ResponseEntity("Question option updated successfully", HttpStatus.OK.value(),
					mapToResponse(updatedOption));

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("Question option already exists or violates database constraints",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity deleteQuestionOption(Long id) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid question option id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Optional<QuestionOption> optionalOption = questionOptionRepository.findById(id);

			if (optionalOption.isEmpty()) {

				return new ResponseEntity("Question option not found with id: " + id, HttpStatus.NOT_FOUND.value(),
						null);
			}

			questionOptionRepository.delete(optionalOption.get());

			return new ResponseEntity("Question option deleted successfully", HttpStatus.OK.value(), null);

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("Cannot delete question option because it is being used",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private QuestionOptionResponse mapToResponse(QuestionOption option) {

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