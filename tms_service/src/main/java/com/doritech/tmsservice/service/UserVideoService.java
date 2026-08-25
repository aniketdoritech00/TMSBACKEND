package com.doritech.tmsservice.service;

import com.doritech.tmsservice.request.UserVideoRequest;
import com.doritech.tmsservice.tms.entity.ResponseEntity;

public interface UserVideoService {

    ResponseEntity createUserVideo(UserVideoRequest request);

    ResponseEntity getUserVideoById(Long id);

    ResponseEntity getAllUserVideo(int page, int size, String sortBy, String sortDir);

    ResponseEntity getVideosByUserId(Long userId);

    ResponseEntity markVideoWatched(Long id);

    ResponseEntity deleteUserVideo(Long id);
}