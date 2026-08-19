package com.doritech.tmsservice.service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.VideoRequest;

public interface VideoService {

	ResponseEntity createVideo(VideoRequest videoRequest);

	ResponseEntity getVideoById(Long id);

	ResponseEntity getAllVideo(int page, int size, String sortBy, String sortDir);

	ResponseEntity deleteVideo(Long id);
}