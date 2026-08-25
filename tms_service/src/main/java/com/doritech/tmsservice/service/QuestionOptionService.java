package com.doritech.tmsservice.service;

import java.util.List;

import com.doritech.tmsservice.request.QuestionOptionRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface QuestionOptionService {

    ResponseEntity createQuestionOptions(List<QuestionOptionRequest> requestList);

    ResponseEntity getQuestionOptionById(Long id);

    ResponseEntity getOptionsByQuestionId(Long testQuestionId);

    ResponseEntity getOptionsForAttempt(Long testQuestionId);

    ResponseEntity deleteQuestionOption(Long id);
}