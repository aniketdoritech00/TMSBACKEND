//package com.doritech.tmsservice.serviceImpl;
//
//import java.security.SecureRandom;
//import java.time.LocalDateTime;
//import java.util.Base64;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.doritech.tmsservice.enums.SupportVideoShareStatus;
//import com.doritech.tmsservice.response.SupportVideoShareResponse;
//import com.doritech.tmsservice.service.SupportVideoShareService;
//import com.doritech.tmsservice.tms.entity.SupportVideoShare;
//import com.doritech.tmsservice.tms.repository.SupportVideoShareRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class SupportVideoShareServiceImpl implements SupportVideoShareService {
//
//	private final SupportVideoShareRepository supportVideoShareRepository;
//
//	private static final SecureRandom SECURE_RANDOM = new SecureRandom();
//
//	@Override
//	public SupportVideoShareResponse shareVideo(SupportVideoShareRequest request, Long sharedBy) {
//
//		if (request == null || request.getVideoId() == null) {
//			throw new IllegalArgumentException("Video ID is required");
//		}
//
//		Long videoId = request.getVideoId();
//
//		Long supportRequestId = getSupportRequestId(videoId);
//		Long userId = getRecipientUserId(videoId);
//
//		String shareToken = generateSecureToken();
//
//		SupportVideoShare share = SupportVideoShare.builder().supportRequestId(supportRequestId).videoId(videoId)
//				.userId(userId).sharedBy(sharedBy).sharedAt(LocalDateTime.now()).status(ShareStatus.SHARED)
//				.shareToken(shareToken)
//
//				// Example: link valid for 7 days
//				.expiresAt(LocalDateTime.now().plusDays(7))
//
//				.build();
//
//		SupportVideoShare saved = supportVideoShareRepository.save(share);
//
//		return mapToResponse(saved);
//	}
//
//	@Override
//	@Transactional(readOnly = true)
//	public SupportVideoShareResponse getSharedVideoByToken(String shareToken, Long userId) {
//
//		SupportVideoShare share = supportVideoShareRepository.findByShareToken(shareToken)
//				.orElseThrow(() -> new RuntimeException("Shared video not found"));
//
//		validateAccess(share, userId);
//
//		return mapToResponse(share);
//	}
//
//	@Override
//	public void markVideoViewed(String shareToken, Long userId) {
//
//		SupportVideoShare share = supportVideoShareRepository.findByShareToken(shareToken)
//				.orElseThrow(() -> new RuntimeException("Shared video not found"));
//
//		validateAccess(share, userId);
//
//		if (share.getStatus() == SupportVideoShareStatus.SHARED) {
//
//			share.setStatus(SupportVideoShareStatus.VIEWED);
//			share.setViewedAt(LocalDateTime.now());
//
//			supportVideoShareRepository.save(share);
//		}
//	}
//
//	@Override
//	public void markVideoCompleted(String shareToken, Long userId) {
//
//		SupportVideoShare share = supportVideoShareRepository.findByShareToken(shareToken)
//				.orElseThrow(() -> new RuntimeException("Shared video not found"));
//
//		validateAccess(share, userId);
//
//		share.setStatus(SupportVideoShareStatus.COMPLETED);
//		share.setCompletedAt(LocalDateTime.now());
//
//		supportVideoShareRepository.save(share);
//	}
//
//	private void validateAccess(SupportVideoShare share, Long userId) {
//
//		if (!share.getUserId().equals(userId)) {
//			throw new RuntimeException("You do not have permission to view this video");
//		}
//
//		if (share.getExpiresAt() != null && share.getExpiresAt().isBefore(LocalDateTime.now())) {
//
//			throw new RuntimeException("Video sharing link has expired");
//		}
//	}
//
//	private String generateSecureToken() {
//
//		byte[] randomBytes = new byte[32];
//
//		SECURE_RANDOM.nextBytes(randomBytes);
//
//		return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
//	}
//
//	private SupportVideoShareResponse mapToResponse(SupportVideoShare share) {
//
//		String shareUrl = "https://yourdomain.com/shared-video/" + share.getShareToken();
//
//		return SupportVideoShareResponse.builder().supportVideoShareId(share.getSupportVideoShareId())
//				.videoId(share.getVideoId()).userId(share.getUserId()).sharedBy(share.getSharedBy())
//				.sharedAt(share.getSharedAt()).status(share.getStatus()).shareUrl(shareUrl)
//				.expiresAt(share.getExpiresAt()).viewedAt(share.getViewedAt()).completedAt(share.getCompletedAt())
//				.build();
//	}
//
//	private Long getSupportRequestId(Long videoId) {
//		return 1L;
//	}
//
//	private Long getRecipientUserId(Long videoId) {
//		return 1L;
//	}
//
//	@Override
//	public SupportVideoShareResponse shareVideo(Long videoId, Long sharedBy) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//}