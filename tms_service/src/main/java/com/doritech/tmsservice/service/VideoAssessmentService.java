package com.doritech.tmsservice.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.request.VideoAssessmentRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface VideoAssessmentService {

	ResponseEntity uploadVideoAssessment(VideoAssessmentRequest request, MultipartFile videoFile);

	ResponseEntity getMyVideoAssessments(int page, int size, String sortBy, String sortDir);

	ResponseEntity getVideoAssessmentById(Long videoAssessmentId);

	void streamVideo(Long videoId, HttpServletRequest request, HttpServletResponse response) throws IOException;

	ResponseEntity getStatusByTrainingAssignmentId(Long trainingAssignmentId);

	ResponseEntity getByTrainingAssignmentId(Long trainingAssignmentId);
}