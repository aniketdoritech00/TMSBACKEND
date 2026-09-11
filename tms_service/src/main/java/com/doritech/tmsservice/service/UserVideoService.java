package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.UserVideoRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface UserVideoService {

	ResponseEntity createUserVideo(UserVideoRequest request);

	ResponseEntity getUserVideoById(Long id);

	ResponseEntity getAllUserVideos();

	ResponseEntity getUserVideosByUserId(Long userId);

	ResponseEntity getUserVideosByTrainingAssignmentId(Long trainingAssignmentId);

	ResponseEntity updateWatchStatus(Long id);

	ResponseEntity updateWatchProgress(Long id, Integer watchedSeconds);

	ResponseEntity getVideoCompletionStatus(Long id);

	ResponseEntity deleteUserVideo(Long id);

	ResponseEntity getAllUserVideos(int page, int size, String sortBy, String sortDir);

	ResponseEntity getUserVideosByUserId(Long userId, int page, int size, String sortBy, String sortDir);

	ResponseEntity getUserVideosByTrainingAssignmentId(Long trainingAssignmentId, int page, int size, String sortBy,
			String sortDir);
}