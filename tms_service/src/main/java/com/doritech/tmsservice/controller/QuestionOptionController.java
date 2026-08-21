package com.doritech.tmsservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.QuestionOptionRequest;
import com.doritech.tmsservice.serviceImpl.QuestionOptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tms/question-options")
public class QuestionOptionController {

    private static final Logger log = LoggerFactory.getLogger(QuestionOptionController.class);

    @Autowired
    private QuestionOptionService questionOptionService;

    @PostMapping("/createQuestionOptions")
    public ResponseEntity createQuestionOptions(@Valid @RequestBody List<QuestionOptionRequest> requestList) {
        log.info("createQuestionOptions :: request received with size={}", requestList.size());
        return questionOptionService.createQuestionOptions(requestList);
    }

    @GetMapping("/getQuestionOptionById/{id}")
    public ResponseEntity getQuestionOptionById(@PathVariable("id") Long id) {
        log.info("getQuestionOptionById :: request received for id={}", id);
        return questionOptionService.getQuestionOptionById(id);
    }

    // ADMIN endpoint - includes isCorrect - use for review/edit screens only
    @GetMapping("/getOptionsByQuestionId/{testQuestionId}")
    public ResponseEntity getOptionsByQuestionId(@PathVariable("testQuestionId") Long testQuestionId) {
        log.info("getOptionsByQuestionId :: request received for testQuestionId={}", testQuestionId);
        return questionOptionService.getOptionsByQuestionId(testQuestionId);
    }

    // STUDENT endpoint - isCorrect hidden - use for test attempt screens
    @GetMapping("/getOptionsForAttempt/{testQuestionId}")
    public ResponseEntity getOptionsForAttempt(@PathVariable("testQuestionId") Long testQuestionId) {
        log.info("getOptionsForAttempt :: request received for testQuestionId={}", testQuestionId);
        return questionOptionService.getOptionsForAttempt(testQuestionId);
    }

    @DeleteMapping("/deleteQuestionOption/{id}")
    public ResponseEntity deleteQuestionOption(@PathVariable("id") Long id) {
        log.info("deleteQuestionOption :: request received for id={}", id);
        return questionOptionService.deleteQuestionOption(id);
    }
}