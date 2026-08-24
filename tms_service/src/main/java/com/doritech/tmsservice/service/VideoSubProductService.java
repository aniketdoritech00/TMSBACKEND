package com.doritech.tmsservice.service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.VideoSubProductRequest;

public interface VideoSubProductService {

    ResponseEntity assignVideoToSubProduct(VideoSubProductRequest videoSubProductRequest);

    ResponseEntity getVideosBySubProductId(Long subProductId);

    ResponseEntity removeVideoFromSubProduct(Long videoId, Long subProductId);
}