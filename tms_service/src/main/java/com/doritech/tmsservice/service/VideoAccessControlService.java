package com.doritech.tmsservice.service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.request.VideoAccessControlRequest;

public interface VideoAccessControlService {

	ResponseEntity createVideoAccessControl(VideoAccessControlRequest request);

	ResponseEntity getVideoAccessControlById(Long id);

	ResponseEntity getAllVideoAccessControl(int page, int size, String sortBy, String sortDir);

	ResponseEntity getAccessByUserId(Long userId);

	ResponseEntity checkAccess(Long videoId, Long userId);

	ResponseEntity revokeAccess(Long id);

	ResponseEntity deleteVideoAccessControl(Long id);
}