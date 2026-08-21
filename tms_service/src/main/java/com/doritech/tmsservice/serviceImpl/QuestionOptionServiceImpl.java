package com.doritech.tmsservice.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.entity.QuestionOption;
import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.TestQuestionRepository;
import com.doritech.tmsservice.request.QuestionOptionRequest;
import com.doritech.tmsservice.response.QuestionOptionResponse;
import com.doritech.tmsservice.response.QuestionOptionSecureResponse;
import com.doritech.tmsservice.service.QuestionOptionRepository;
import com.doritech.tmsservice.service.QuestionOptionService;

@Service
public class QuestionOptionServiceImpl implements QuestionOptionService {

	private static final Logger log = LoggerFactory.getLogger(QuestionOptionServiceImpl.class);

	@Autowired
	private QuestionOptionRepository questionOptionRepository;

	@Autowired
	private TestQuestionRepository testQuestionRepository;

	@Override
	public ResponseEntity createQuestionOptions(List<QuestionOptionRequest> requestList) {

		log.info("createQuestionOptions :: request received with size={}",
				requestList == null ? 0 : requestList.size());

		if (requestList == null || requestList.isEmpty()) {
			log.error("createQuestionOptions :: request list is empty");
			throw new BadRequestException("Options list must not be empty");
		}

		
		Long testQuestionId = requestList.get(0).getTestQuestionId();

		boolean allSameQuestion = requestList.stream().allMatch(r -> testQuestionId.equals(r.getTestQuestionId()));

		if (!allSameQuestion) {
			log.error("createQuestionOptions :: options belong to different test questions");
			throw new BadRequestException("All options in a single request must belong to the same test question");
		}

		if (!testQuestionRepository.existsById(testQuestionId)) {
			log.error("createQuestionOptions :: test question not found for id={}", testQuestionId);
			throw new ResourceNotFoundException("Test question not found with id: " + testQuestionId);
		}

		boolean hasCorrectAnswer = requestList.stream().anyMatch(r -> Boolean.TRUE.equals(r.getIsCorrect()));

		if (!hasCorrectAnswer) {
			log.error("createQuestionOptions :: no option marked as correct for testQuestionId={}", testQuestionId);
			throw new BadRequestException("At least one option must be marked as correct");
		}

		List<QuestionOption> optionList = requestList.stream().map(request -> {
			QuestionOption option = new QuestionOption();
			option.setTestQuestionId(request.getTestQuestionId());
			option.setOptionText(request.getOptionText());
			option.setOptionLabel(request.getOptionLabel());
			option.setIsCorrect(request.getIsCorrect());
			option.setDisplayOrder(request.getDisplayOrder());
			return option;
		}).collect(Collectors.toList());

		List<QuestionOption> savedList;
		try {
			savedList = questionOptionRepository.saveAll(optionList);
		} catch (Exception e) {
			log.error("createQuestionOptions :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving question options");
		}

		List<QuestionOptionResponse> responseList = savedList.stream().map(this::mapToFullResponse)
				.collect(Collectors.toList());

		log.info("createQuestionOptions :: {} options saved successfully for testQuestionId={}", responseList.size(),
				testQuestionId);

		return new ResponseEntity("Question options saved successfully", HttpStatus.CREATED.value(), responseList);
	}

	@Override
	public ResponseEntity getQuestionOptionById(Long id) {

		log.info("getQuestionOptionById :: request received for id={}", id);

		if (id == null) {
			log.error("getQuestionOptionById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		QuestionOption option = questionOptionRepository.findById(id).orElseThrow(() -> {
			log.error("getQuestionOptionById :: not found for id={}", id);
			return new ResourceNotFoundException("Question option not found with id: " + id);
		});

		log.info("getQuestionOptionById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), mapToFullResponse(option));
	}

	@Override
	public ResponseEntity getOptionsByQuestionId(Long testQuestionId) {

		log.info("getOptionsByQuestionId :: request received for testQuestionId={} (ADMIN - includes isCorrect)",
				testQuestionId);

		if (testQuestionId == null) {
			log.error("getOptionsByQuestionId :: testQuestionId is null");
			throw new BadRequestException("Test Question ID can not be null");
		}

		List<QuestionOption> optionList = questionOptionRepository
				.findByTestQuestionIdOrderByDisplayOrderAsc(testQuestionId);

		// ADMIN VIEW: isCorrect included - use only for review/edit screens, never for
		// student attempt
		List<QuestionOptionResponse> responseList = optionList.stream().map(this::mapToFullResponse)
				.collect(Collectors.toList());

		log.info("getOptionsByQuestionId :: {} options fetched for testQuestionId={}", responseList.size(),
				testQuestionId);

		return new ResponseEntity("Question option fetch successfully", HttpStatus.OK.value(), responseList);
	}

	@Override
	public ResponseEntity getOptionsForAttempt(Long testQuestionId) {

		log.info("getOptionsForAttempt :: request received for testQuestionId={} (STUDENT - isCorrect hidden)",
				testQuestionId);

		if (testQuestionId == null) {
			log.error("getOptionsForAttempt :: testQuestionId is null");
			throw new BadRequestException("Test Question ID can not be null");
		}

		List<QuestionOption> optionList = questionOptionRepository
				.findByTestQuestionIdOrderByDisplayOrderAsc(testQuestionId);

		List<QuestionOptionSecureResponse> responseList = optionList.stream().map(this::mapToSecureResponse)
				.collect(Collectors.toList());

		log.info("getOptionsForAttempt :: {} options fetched for testQuestionId={}", responseList.size(),
				testQuestionId);

		return new ResponseEntity("Question option fetch successfully", HttpStatus.OK.value(), responseList);
	}

	@Override
	public ResponseEntity deleteQuestionOption(Long id) {

		log.info("deleteQuestionOption :: request received for id={}", id);

		if (id == null) {
			log.error("deleteQuestionOption :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		QuestionOption option = questionOptionRepository.findById(id).orElseThrow(() -> {
			log.error("deleteQuestionOption :: not found for id={}", id);
			return new ResourceNotFoundException("Question option not found with id: " + id);
		});

		try {
			questionOptionRepository.delete(option);
		} catch (Exception e) {
			log.error("deleteQuestionOption :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Cannot delete question option, it may be linked to other records");
		}

		log.info("deleteQuestionOption :: deleted successfully for id={}", id);

		return new ResponseEntity("Question option deleted successfully", HttpStatus.OK.value(), null);
	}

	// ADMIN mapping - includes isCorrect
	private QuestionOptionResponse mapToFullResponse(QuestionOption entity) {
		QuestionOptionResponse response = new QuestionOptionResponse();
		response.setQuestionOptionId(entity.getQuestionOptionId());
		response.setTestQuestionId(entity.getTestQuestionId());
		response.setOptionText(entity.getOptionText());
		response.setOptionLabel(entity.getOptionLabel());
		response.setIsCorrect(entity.getIsCorrect());
		response.setDisplayOrder(entity.getDisplayOrder());
		return response;
	}

	// STUDENT mapping - isCorrect excluded
	private QuestionOptionSecureResponse mapToSecureResponse(QuestionOption entity) {
		return new QuestionOptionSecureResponse(entity.getQuestionOptionId(), entity.getTestQuestionId(),
				entity.getOptionText(), entity.getOptionLabel(), entity.getDisplayOrder());
	}
}