package com.doritech.tmsservice.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.doritech.tmsservice.request.VideoRequest;
import com.doritech.tmsservice.request.VideoSubProductRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

import jakarta.validation.Valid;

public interface VideoSubProductService {

    ResponseEntity assignVideoToSubProduct(VideoSubProductRequest videoSubProductRequest);

    ResponseEntity getVideosBySubProductId(Long subProductId);

    ResponseEntity removeVideoFromSubProduct(Long videoId, Long subProductId);

	ResponseEntity uploadVideAndThumbnail(@Valid VideoRequest request, MultipartFile videoFile,
			MultipartFile thumbnailFile, List<Long> subProductIds);
}