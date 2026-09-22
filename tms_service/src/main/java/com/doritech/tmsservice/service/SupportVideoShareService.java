package com.doritech.tmsservice.service;

import com.doritech.tmsservice.response.SupportVideoShareResponse;

public interface SupportVideoShareService {

	SupportVideoShareResponse shareVideo(Long videoId, Long sharedBy);

	SupportVideoShareResponse getSharedVideoByToken(String shareToken, Long userId);

	void markVideoViewed(String shareToken, Long userId);

	void markVideoCompleted(String shareToken, Long userId);
}