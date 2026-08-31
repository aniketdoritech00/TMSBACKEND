package com.doritech.tmsservice.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.request.VideoRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface VideoService {
	
	ResponseEntity uploadVideAndThumbnail(VideoRequest request, MultipartFile videoFile, MultipartFile thumbnailFile);

    ResponseEntity getVideoById(Long id);

    ResponseEntity getAllVideo(int page, int size, String sortBy, String sortDir);

    ResponseEntity deleteVideo(Long id);

	byte[] getThumbnailByPath(String path) throws IOException;

	org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> streamVideo(Long videoId);
	
}