package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.VideoSubProductRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface VideoSubProductService {

    ResponseEntity assignVideoToSubProduct(VideoSubProductRequest videoSubProductRequest);

    ResponseEntity getVideosBySubProductId(Long subProductId);

    ResponseEntity removeVideoFromSubProduct(Long videoId, Long subProductId);
}