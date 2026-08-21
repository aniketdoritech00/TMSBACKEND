package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
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

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.entity.TestSet;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceAlreadyExistsException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.TestSetRepository;
import com.doritech.tmsservice.request.TestSetRequest;
import com.doritech.tmsservice.response.TestSetListResponse;
import com.doritech.tmsservice.response.TestSetResponse;
import com.doritech.tmsservice.service.TestSetService;

@Service
public class TestSetServiceImpl implements TestSetService {

	private static final Logger log = LoggerFactory.getLogger(TestSetServiceImpl.class);

	@Autowired
	private TestSetRepository testSetRepository;

	@Override
	public ResponseEntity createTestSet(TestSetRequest request) {

		log.info("createTestSet :: request received for name={}", request.getTestName());

		if (request.getTestCode() != null && testSetRepository.existsByTestCode(request.getTestCode())) {
			log.error("createTestSet :: duplicate testCode={}", request.getTestCode());
			throw new ResourceAlreadyExistsException("Test set already exists with code: " + request.getTestCode());
		}

		if (request.getSetNo() != null && testSetRepository.existsBySetNo(request.getSetNo())) {
			log.error("createTestSet :: duplicate setNo={}", request.getSetNo());
			throw new ResourceAlreadyExistsException("Test set already exists with set no: " + request.getSetNo());
		}

		if (request.getStartDateTime() != null && request.getEndDateTime() != null
				&& request.getEndDateTime().isBefore(request.getStartDateTime())) {
			log.error("createTestSet :: endDateTime before startDateTime");
			throw new BadRequestException("End date time must be after start date time");
		}

		TestSet testSet = new TestSet();
		testSet.setTestName(request.getTestName());
		testSet.setTestDescription(request.getTestDescription());
		testSet.setTestCode(request.getTestCode());
		testSet.setSetNo(request.getSetNo());
		testSet.setTrainingId(request.getTrainingId());
		testSet.setStartDateTime(request.getStartDateTime());
		testSet.setEndDateTime(request.getEndDateTime());
		testSet.setTimeLimitMinutes(request.getTimeLimitMinutes());
		testSet.setPassingPercentage(request.getPassingPercentage());
		testSet.setShuffleQuestions(request.getShuffleQuestions());
		testSet.setShuffleOptions(request.getShuffleOptions());
		testSet.setIsActive(request.getIsActive());
		testSet.setCreatedBy(request.getCreatedBy());

		TestSet saved;
		try {
			saved = testSetRepository.save(testSet);
		} catch (Exception e) {
			log.error("createTestSet :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving test set");
		}

		log.info("createTestSet :: saved successfully with id={}", saved.getTestSetId());

		return new ResponseEntity("Test set saved successfully", HttpStatus.CREATED.value(), mapToFullResponse(saved));
	}

	@Override
	public ResponseEntity getTestSetById(Long id) {

		log.info("getTestSetById :: request received for id={}", id);

		if (id == null) {
			log.error("getTestSetById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		TestSet testSet = testSetRepository.findById(id).orElseThrow(() -> {
			log.error("getTestSetById :: not found for id={}", id);
			return new ResourceNotFoundException("Test set not found with id: " + id);
		});

		log.info("getTestSetById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), mapToFullResponse(testSet));
	}

	@Override
	public ResponseEntity getAllTestSet(int page, int size, String sortBy, String sortDir) {

		log.info("getAllTestSet :: request received with page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy,
				sortDir);

		if (page < 0) {
			log.error("getAllTestSet :: page cannot be negative");
			throw new BadRequestException("Page number can not be negative");
		}

		if (size <= 0) {
			log.error("getAllTestSet :: size must be greater than 0");
			throw new BadRequestException("Page size must be greater than 0");
		}

		if (size > 100) {
			log.error("getAllTestSet :: size exceeds max limit={}", size);
			throw new BadRequestException("Page size can not exceed 100");
		}

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<TestSet> testSetPage;
		try {
			testSetPage = testSetRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			log.error("getAllTestSet :: invalid sort field={}", sortBy);
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			log.error("getAllTestSet :: error while fetching - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while fetching test sets");
		}

		List<TestSetListResponse> responseList = testSetPage.getContent().stream().map(this::mapToListResponse)
				.collect(Collectors.toList());

		Map<String, Object> pageData = new LinkedHashMap<>();
		pageData.put("content", responseList);
		pageData.put("pageNumber", testSetPage.getNumber());
		pageData.put("pageSize", testSetPage.getSize());
		pageData.put("totalElements", testSetPage.getTotalElements());
		pageData.put("totalPages", testSetPage.getTotalPages());
		pageData.put("isLast", testSetPage.isLast());

		log.info("getAllTestSet :: {} of {} test sets fetched successfully", responseList.size(),
				testSetPage.getTotalElements());

		return new ResponseEntity("Test set fetch successfully", HttpStatus.OK.value(), pageData);
	}

	@Override
	public ResponseEntity getTestSetsByTrainingId(Long trainingId) {

		log.info("getTestSetsByTrainingId :: request received for trainingId={}", trainingId);

		if (trainingId == null) {
			log.error("getTestSetsByTrainingId :: trainingId is null");
			throw new BadRequestException("Training ID can not be null");
		}

		List<TestSet> testSetList = testSetRepository.findByTrainingId(trainingId);

		List<TestSetListResponse> responseList = testSetList.stream().map(this::mapToListResponse)
				.collect(Collectors.toList());

		log.info("getTestSetsByTrainingId :: {} test sets fetched for trainingId={}", responseList.size(), trainingId);

		return new ResponseEntity("Test set fetch successfully", HttpStatus.OK.value(), responseList);
	}

	@Override
	public ResponseEntity deleteTestSet(Long id) {

		log.info("deleteTestSet :: request received for id={}", id);

		if (id == null) {
			log.error("deleteTestSet :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		TestSet testSet = testSetRepository.findById(id).orElseThrow(() -> {
			log.error("deleteTestSet :: not found for id={}", id);
			return new ResourceNotFoundException("Test set not found with id: " + id);
		});

		try {
			testSetRepository.delete(testSet);
		} catch (Exception e) {
			log.error("deleteTestSet :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Cannot delete test set, it may be linked to other records");
		}

		log.info("deleteTestSet :: deleted successfully for id={}", id);

		return new ResponseEntity("Test set deleted successfully", HttpStatus.OK.value(), null);
	}

	@Override
	public ResponseEntity publishTestSet(Long id) {

		log.info("publishTestSet :: request received for id={}", id);

		if (id == null) {
			log.error("publishTestSet :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		TestSet testSet = testSetRepository.findById(id).orElseThrow(() -> {
			log.error("publishTestSet :: not found for id={}", id);
			return new ResourceNotFoundException("Test set not found with id: " + id);
		});

		if (testSet.getPublishedAt() != null) {
			log.error("publishTestSet :: already published for id={}", id);
			throw new BadRequestException("Test set is already published");
		}

		testSet.setPublishedAt(LocalDateTime.now());
		testSet.setIsActive(true);

		TestSet saved;
		try {
			saved = testSetRepository.save(testSet);
		} catch (Exception e) {
			log.error("publishTestSet :: error while publishing - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while publishing test set");
		}

		log.info("publishTestSet :: published successfully for id={}", id);

		return new ResponseEntity("Test set published successfully", HttpStatus.OK.value(), mapToFullResponse(saved));
	}

	private TestSetResponse mapToFullResponse(TestSet entity) {
		TestSetResponse response = new TestSetResponse();
		response.setTestSetId(entity.getTestSetId());
		response.setTestName(entity.getTestName());
		response.setTestDescription(entity.getTestDescription());
		response.setTestCode(entity.getTestCode());
		response.setSetNo(entity.getSetNo());
		response.setTrainingId(entity.getTrainingId());
		response.setStartDateTime(entity.getStartDateTime());
		response.setEndDateTime(entity.getEndDateTime());
		response.setTimeLimitMinutes(entity.getTimeLimitMinutes());
		response.setPassingPercentage(entity.getPassingPercentage());
		response.setShuffleQuestions(entity.getShuffleQuestions());
		response.setShuffleOptions(entity.getShuffleOptions());
		response.setIsActive(entity.getIsActive());
		response.setCreatedBy(entity.getCreatedBy());
		response.setCreatedAt(entity.getCreatedAt());
		response.setUpdatedAt(entity.getUpdatedAt());
		response.setPublishedAt(entity.getPublishedAt());
		return response;
	}

	private TestSetListResponse mapToListResponse(TestSet entity) {
		return new TestSetListResponse(entity.getTestSetId(), entity.getTestName(), entity.getTestCode(),
				entity.getTrainingId(), entity.getIsActive());
	}
}