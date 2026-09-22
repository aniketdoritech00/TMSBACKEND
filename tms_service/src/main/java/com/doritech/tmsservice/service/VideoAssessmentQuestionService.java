package com.doritech.tmsservice.service;

import java.util.List;

import com.doritech.tmsservice.request.VideoAssessmentQuestionRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface VideoAssessmentQuestionService {

	ResponseEntity getVideoAssessmentQuestionById(Long videoAssessmentQuestionId);

	ResponseEntity getAllVideoAssessmentQuestions(int page, int size, String sortBy, String sortDir);

	ResponseEntity getVideoAssessmentQuestionsByTrainingId(Long trainingId, int page, int size, String sortBy,
			String sortDir);

	ResponseEntity updateVideoAssessmentQuestion(Long videoAssessmentQuestionId,
			VideoAssessmentQuestionRequest request);

	ResponseEntity deleteVideoAssessmentQuestion(Long videoAssessmentQuestionId);

	ResponseEntity getAllVideoAssessmentQuestionsWithoutPaginatioan();

	ResponseEntity createVideoAssessmentQuestion(List<VideoAssessmentQuestionRequest> requests);
}