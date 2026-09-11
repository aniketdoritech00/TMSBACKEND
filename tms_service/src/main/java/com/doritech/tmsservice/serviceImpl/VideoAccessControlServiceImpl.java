package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.request.VideoAccessControlRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.VideoAccessControlListResponse;
import com.doritech.tmsservice.response.VideoAccessControlResponse;
import com.doritech.tmsservice.service.VideoAccessControlService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.VideoAccessControl;
import com.doritech.tmsservice.tms.repository.VideoAccessControlRepository;

@Service
public class VideoAccessControlServiceImpl implements VideoAccessControlService {

	private final VideoAccessControlRepository videoAccessControlRepository;

	public VideoAccessControlServiceImpl(VideoAccessControlRepository videoAccessControlRepository) {

		this.videoAccessControlRepository = videoAccessControlRepository;
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity createVideoAccessControl(VideoAccessControlRequest request) {

		try {

			if (request == null) {
				return new ResponseEntity("Video access control data is required", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			if (request.getVideoId() == null || request.getVideoId() <= 0) {
				return new ResponseEntity("Valid video id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getUserId() == null || request.getUserId() <= 0) {
				return new ResponseEntity("Valid user id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			boolean alreadyExists = videoAccessControlRepository.existsByVideoIdAndUserId(request.getVideoId(),
					request.getUserId());

			if (alreadyExists) {
				return new ResponseEntity("Access already exists for this video and user", HttpStatus.CONFLICT.value(),
						null);
			}

			VideoAccessControl access = new VideoAccessControl();

			access.setVideoId(request.getVideoId());
			access.setUserId(request.getUserId());
			access.setAccessDurationDays(request.getAccessDurationDays());
			access.setIsActive(request.getIsActive());

			VideoAccessControl savedAccess = videoAccessControlRepository.save(access);

			return new ResponseEntity("Video access control saved successfully", HttpStatus.CREATED.value(),
					mapToFullResponse(savedAccess));

		} catch (DataIntegrityViolationException e) {

			return new ResponseEntity("Video access control already exists or violates database constraints",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to save video access control: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getVideoAccessControlById(Long id) {

		try {

			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid video access control id", HttpStatus.BAD_REQUEST.value(), null);
			}

			VideoAccessControl access = videoAccessControlRepository.findById(id).orElse(null);

			if (access == null) {
				return new ResponseEntity("Video access control not found", HttpStatus.NOT_FOUND.value(), null);
			}

			return new ResponseEntity("Video access control fetched successfully", HttpStatus.OK.value(),
					mapToFullResponse(access));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch video access control: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllVideoAccessControl(int page, int size, String sortBy, String sortDir) {

		try {

			if (page < 0) {
				page = 0;
			}

			if (size <= 0) {
				size = 10;
			}

			if (size > 100) {
				size = 100;
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "videoAccessId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<VideoAccessControl> accessPage = videoAccessControlRepository.findAll(pageable);

			List<VideoAccessControlListResponse> response = accessPage.getContent().stream()
					.map(this::mapToListResponse).collect(Collectors.toList());

			PageResponse<VideoAccessControlListResponse> pageResponse = new PageResponse<>(response,
					accessPage.getNumber(), accessPage.getSize(), accessPage.getTotalElements(),
					accessPage.getTotalPages(), accessPage.isLast());

			return new ResponseEntity("Video access controls fetched successfully", HttpStatus.OK.value(),
					pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch video access controls: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAccessByUserId(Long userId) {

		try {

			if (userId == null || userId <= 0) {
				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
			}

			List<VideoAccessControl> accessList = videoAccessControlRepository.findByUserId(userId);

			List<VideoAccessControlListResponse> response = accessList.stream().map(this::mapToListResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("Video access controls fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch video access controls: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity checkAccess(Long videoId, Long userId) {

		try {

			if (videoId == null || videoId <= 0) {
				return new ResponseEntity("Valid video id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (userId == null || userId <= 0) {
				return new ResponseEntity("Valid user id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			VideoAccessControl access = videoAccessControlRepository.findByVideoIdAndUserId(videoId, userId)
					.orElse(null);

			if (access == null) {
				return new ResponseEntity("No access record found for this video and user",
						HttpStatus.NOT_FOUND.value(), null);
			}

			boolean hasAccess = Boolean.TRUE.equals(access.getIsActive()) && (access.getAccessExpiryDate() == null
					|| access.getAccessExpiryDate().isAfter(LocalDateTime.now()));

			AccessCheckResponse result = new AccessCheckResponse();

			result.setHasAccess(hasAccess);
			result.setAccessExpiryDate(access.getAccessExpiryDate());

			return new ResponseEntity("Access check completed", HttpStatus.OK.value(), result);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to check video access: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity revokeAccess(Long id) {

		try {

			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid video access control id", HttpStatus.BAD_REQUEST.value(), null);
			}

			VideoAccessControl access = videoAccessControlRepository.findById(id).orElse(null);

			if (access == null) {
				return new ResponseEntity("Video access control not found", HttpStatus.NOT_FOUND.value(), null);
			}

			access.setIsActive(false);

			VideoAccessControl savedAccess = videoAccessControlRepository.save(access);

			return new ResponseEntity("Video access revoked successfully", HttpStatus.OK.value(),
					mapToFullResponse(savedAccess));

		} catch (DataIntegrityViolationException e) {

			return new ResponseEntity("Unable to revoke video access because of database constraints",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to revoke video access: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity deleteVideoAccessControl(Long id) {

		try {

			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid video access control id", HttpStatus.BAD_REQUEST.value(), null);
			}

			VideoAccessControl access = videoAccessControlRepository.findById(id).orElse(null);

			if (access == null) {
				return new ResponseEntity("Video access control not found", HttpStatus.NOT_FOUND.value(), null);
			}

			videoAccessControlRepository.delete(access);

			return new ResponseEntity("Video access control deleted successfully", HttpStatus.OK.value(), null);

		} catch (DataIntegrityViolationException e) {

			return new ResponseEntity("Video access control cannot be deleted because it is being used",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to delete video access control: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private VideoAccessControlResponse mapToFullResponse(VideoAccessControl entity) {

		VideoAccessControlResponse response = new VideoAccessControlResponse();

		response.setVideoAccessId(entity.getVideoAccessId());
		response.setVideoId(entity.getVideoId());
		response.setUserId(entity.getUserId());
		response.setAccessDurationDays(entity.getAccessDurationDays());
		response.setAccessStartDate(entity.getAccessStartDate());
		response.setAccessExpiryDate(entity.getAccessExpiryDate());
		response.setIsActive(entity.getIsActive());
		response.setCreatedAt(entity.getCreatedAt());
		response.setUpdatedAt(entity.getUpdatedAt());

		return response;
	}

	private VideoAccessControlListResponse mapToListResponse(VideoAccessControl entity) {

		return new VideoAccessControlListResponse(entity.getVideoAccessId(), entity.getVideoId(), entity.getUserId(),
				entity.getAccessExpiryDate(), entity.getIsActive());
	}

	private static class AccessCheckResponse {

		private boolean hasAccess;
		private LocalDateTime accessExpiryDate;

		public boolean isHasAccess() {
			return hasAccess;
		}

		public void setHasAccess(boolean hasAccess) {
			this.hasAccess = hasAccess;
		}

		public LocalDateTime getAccessExpiryDate() {
			return accessExpiryDate;
		}

		public void setAccessExpiryDate(LocalDateTime accessExpiryDate) {
			this.accessExpiryDate = accessExpiryDate;
		}
	}
}