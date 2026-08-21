package com.doritech.tmsservice.service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.UserVideoRequest;

public interface UserVideoService {

    ResponseEntity createUserVideo(UserVideoRequest request);

    ResponseEntity getUserVideoById(Long id);

    ResponseEntity getAllUserVideo(int page, int size, String sortBy, String sortDir);

    ResponseEntity getVideosByUserId(Long userId);

    ResponseEntity markVideoWatched(Long id);

    ResponseEntity deleteUserVideo(Long id);
}