package com.doritech.tmsservice.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.request.UserResponseRequest;
import com.doritech.tmsservice.service.UserResponseService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

@RestController
@RequestMapping("/api/tms/user-response")
public class UserResponseController {

    private final UserResponseService userResponseService;

    public UserResponseController(
            UserResponseService userResponseService) {

        this.userResponseService = userResponseService;
    }

    @PostMapping("/submitResponse")
    public ResponseEntity submitResponse(
            @RequestBody UserResponseRequest request) {

        return userResponseService.submitResponse(
                request);
    }

    @GetMapping("/getResponseById/{userResponseId}")
    public ResponseEntity getResponseById(
            @PathVariable Long userResponseId) {

        return userResponseService.getResponseById(
                userResponseId);
    }

    @GetMapping("/getAllResponses")
    public ResponseEntity getAllResponses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userResponseId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        return userResponseService.getAllResponses(
                page,
                size,
                sortBy,
                sortDir);
    }

    @GetMapping("/getResponsesByTestAttemptId/{testAttemptId}")
    public ResponseEntity getResponsesByTestAttemptId(
            @PathVariable Long testAttemptId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userResponseId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        return userResponseService.getResponsesByTestAttemptId(
                testAttemptId,
                page,
                size,
                sortBy,
                sortDir);
    }

    @GetMapping("/getResponseByAttemptAndQuestion")
    public ResponseEntity getResponseByAttemptAndQuestion(
            @RequestParam Long testAttemptId,
            @RequestParam Long testQuestionId) {

        return userResponseService
                .getResponseByAttemptAndQuestion(
                        testAttemptId,
                        testQuestionId);
    }

    @PutMapping("/updateResponse/{userResponseId}")
    public ResponseEntity updateResponse(
            @PathVariable Long userResponseId,
            @RequestBody UserResponseRequest request) {

        return userResponseService.updateResponse(
                userResponseId,
                request);
    }

    @DeleteMapping("/deleteResponse/{userResponseId}")
    public ResponseEntity deleteResponse(
            @PathVariable Long userResponseId) {

        return userResponseService.deleteResponse(
                userResponseId);
    }

    @DeleteMapping("/deleteResponsesByTestAttemptId/{testAttemptId}")
    public ResponseEntity deleteResponsesByTestAttemptId(
            @PathVariable Long testAttemptId) {

        return userResponseService
                .deleteResponsesByTestAttemptId(
                        testAttemptId);
    }

    @GetMapping("/getCorrectResponses/{testAttemptId}")
    public ResponseEntity getCorrectResponses(
            @PathVariable Long testAttemptId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "userResponseId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        return userResponseService.getCorrectResponses(
                testAttemptId,
                page,
                size,
                sortBy,
                sortDir);
    }

    @GetMapping("/getResponseCount/{testAttemptId}")
    public ResponseEntity getResponseCount(
            @PathVariable Long testAttemptId) {

        return userResponseService.getResponseCount(
                testAttemptId);
    }

    @GetMapping("/getCorrectAnswerCount/{testAttemptId}")
    public ResponseEntity getCorrectAnswerCount(
            @PathVariable Long testAttemptId) {

        return userResponseService.getCorrectAnswerCount(
                testAttemptId);
    }
}