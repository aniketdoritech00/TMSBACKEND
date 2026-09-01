package com.doritech.tmsservice.service;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.request.VideoRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface VideoService {
	
	ResponseEntity uploadVideAndThumbnail(VideoRequest request, MultipartFile videoFile, MultipartFile thumbnailFile);

    ResponseEntity getVideoDetailsById(Long id);

    ResponseEntity getAllVideo(int page, int size, String sortBy, String sortDir);

    ResponseEntity deleteVideo(Long id);

	byte[] getThumbnailByPath(String path) throws IOException;

//	org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> streamVideo(Long videoId);

	org.springframework.http.ResponseEntity<Resource> downloadVideo(Long videoId);

	void streamVideo(Long videoId, HttpServletRequest request, HttpServletResponse response) throws IOException;

	ResponseEntity uploadVideo(VideoRequest request, MultipartFile videoFile);
	
}