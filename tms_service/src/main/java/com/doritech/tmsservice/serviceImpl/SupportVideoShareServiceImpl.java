package com.doritech.tmsservice.serviceImpl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.enums.SupportVideoShareStatus;
import com.doritech.tmsservice.response.SupportVideoShareResponse;
import com.doritech.tmsservice.service.SupportVideoShareService;
import com.doritech.tmsservice.tms.entity.SupportVideoShare;
import com.doritech.tmsservice.tms.repository.SupportVideoShareRepository;

@Service
@Transactional(transactionManager = "tmsTransactionManager")
public class SupportVideoShareServiceImpl implements SupportVideoShareService {

	private final SupportVideoShareRepository supportVideoShareRepository;

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	public SupportVideoShareServiceImpl(SupportVideoShareRepository supportVideoShareRepository) {
		this.supportVideoShareRepository = supportVideoShareRepository;
	}

	@Override
	public SupportVideoShareResponse shareVideo(Long videoId, Long sharedBy) {

		if (videoId == null) {
			throw new IllegalArgumentException("Video ID is required");
		}

		if (sharedBy == null) {
			throw new IllegalArgumentException("Logged-in user ID is required");
		}

		Long supportRequestId = getSupportRequestId(videoId);

		Long userId = getRecipientUserId(videoId);

		Optional<SupportVideoShare> existingShare = supportVideoShareRepository.findByVideoIdAndUserId(videoId, userId);

		if (existingShare.isPresent()) {

			SupportVideoShare existing = existingShare.get();

			if (existing.getExpiresAt() == null || existing.getExpiresAt().isAfter(LocalDateTime.now())) {

				return mapToResponse(existing);
			}
		}

		String shareToken = generateSecureToken();

		SupportVideoShare share = new SupportVideoShare();

		share.setSupportRequestId(supportRequestId);

		share.setVideoId(videoId);

		share.setUserId(userId);

		share.setSharedBy(sharedBy);

		share.setSharedAt(LocalDateTime.now());

		share.setStatus(SupportVideoShareStatus.SHARED);

		share.setShareToken(shareToken);

		share.setExpiresAt(LocalDateTime.now().plusDays(7));

		SupportVideoShare saved = supportVideoShareRepository.save(share);

		return mapToResponse(saved);
	}

	@Override
	@Transactional(readOnly = true)
	public SupportVideoShareResponse getSharedVideoByToken(String shareToken, Long userId) {

		if (shareToken == null || shareToken.isBlank()) {
			throw new IllegalArgumentException("Share token is required");
		}

		if (userId == null) {
			throw new IllegalArgumentException("User ID is required");
		}
		SupportVideoShare share = supportVideoShareRepository.findByShareToken(shareToken)
				.orElseThrow(() -> new RuntimeException("Shared video not found"));

		validateAccess(share, userId);

		return mapToResponse(share);
	}

	@Override
	public void markVideoViewed(String shareToken, Long userId) {

		if (shareToken == null || shareToken.isBlank()) {
			throw new IllegalArgumentException("Share token is required");
		}

		if (userId == null) {
			throw new IllegalArgumentException("User ID is required");
		}

		SupportVideoShare share = supportVideoShareRepository.findByShareToken(shareToken)
				.orElseThrow(() -> new RuntimeException("Shared video not found"));

		validateAccess(share, userId);

		if (share.getStatus() == SupportVideoShareStatus.SHARED) {

			share.setStatus(SupportVideoShareStatus.VIEWED);

			share.setViewedAt(LocalDateTime.now());

			supportVideoShareRepository.save(share);
		}
	}

	@Override
	public void markVideoCompleted(String shareToken, Long userId) {

		if (shareToken == null || shareToken.isBlank()) {
			throw new IllegalArgumentException("Share token is required");
		}

		if (userId == null) {
			throw new IllegalArgumentException("User ID is required");
		}

		SupportVideoShare share = supportVideoShareRepository.findByShareToken(shareToken)
				.orElseThrow(() -> new RuntimeException("Shared video not found"));

		validateAccess(share, userId);

		if (share.getViewedAt() == null) {

			share.setViewedAt(LocalDateTime.now());
		}

		share.setStatus(SupportVideoShareStatus.COMPLETED);

		share.setCompletedAt(LocalDateTime.now());

		supportVideoShareRepository.save(share);
	}

	private void validateAccess(SupportVideoShare share, Long userId) {
		if (!share.getUserId().equals(userId)) {

			throw new RuntimeException("You do not have permission to view this video");
		}

		if (share.getExpiresAt() != null && share.getExpiresAt().isBefore(LocalDateTime.now())) {

			throw new RuntimeException("Video sharing link has expired");
		}
	}

	private String generateSecureToken() {

		byte[] randomBytes = new byte[32];

		SECURE_RANDOM.nextBytes(randomBytes);

		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
	}

	private SupportVideoShareResponse mapToResponse(SupportVideoShare share) {

		String shareUrl =
	            "http://192.168.1.44:9091/api/tms/support-video-share/"
	                    + share.getShareToken();

		SupportVideoShareResponse response = new SupportVideoShareResponse();

		response.setSupportVideoShareId(share.getSupportVideoShareId());

		response.setSupportRequestId(share.getSupportRequestId());

		response.setVideoId(share.getVideoId());

		response.setUserId(share.getUserId());

		response.setSharedBy(share.getSharedBy());

		response.setSharedAt(share.getSharedAt());

		response.setStatus(share.getStatus());

		response.setShareUrl(shareUrl);

		response.setExpiresAt(share.getExpiresAt());

		response.setViewedAt(share.getViewedAt());

		response.setCompletedAt(share.getCompletedAt());

		return response;
	}

	private Long getSupportRequestId(Long videoId) {
		return 1L;
	}

	private Long getRecipientUserId(Long videoId) {
		return 1L;
	}
}