package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.QuestionOptionRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface QuestionOptionService {

	ResponseEntity createQuestionOption(QuestionOptionRequest request);

	ResponseEntity getQuestionOptionById(Long id);

	ResponseEntity getAllQuestionOptions();

	ResponseEntity getAllQuestionOptions(int page, int size, String sortBy, String sortDir);

	ResponseEntity getQuestionOptionsByTestQuestionId(Long testQuestionId);

	ResponseEntity getQuestionOptionsByTestQuestionId(Long testQuestionId, int page, int size, String sortBy,
			String sortDir);

	ResponseEntity updateQuestionOption(Long id, QuestionOptionRequest request);

	ResponseEntity deleteQuestionOption(Long id);
}