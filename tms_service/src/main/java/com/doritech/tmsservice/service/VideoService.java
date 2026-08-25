package com.doritech.tmsservice.service;

import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.request.VideoRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface VideoService {

    ResponseEntity createVideo(VideoRequest videoRequest, MultipartFile file);

    ResponseEntity getVideoById(Long id);

    ResponseEntity getAllVideo(int page, int size, String sortBy, String sortDir);

    ResponseEntity deleteVideo(Long id);
}