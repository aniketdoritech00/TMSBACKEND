package com.doritech.tmsservice.service;

import java.util.List;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.QuestionOptionRequest;

public interface QuestionOptionService {

    ResponseEntity createQuestionOptions(List<QuestionOptionRequest> requestList);

    ResponseEntity getQuestionOptionById(Long id);

    ResponseEntity getOptionsByQuestionId(Long testQuestionId);

    ResponseEntity getOptionsForAttempt(Long testQuestionId);

    ResponseEntity deleteQuestionOption(Long id);
}