package com.doritech.tmsservice.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.enums.AssignmentStatus;
import com.doritech.tmsservice.enums.UserVideoStatus;
import com.doritech.tmsservice.request.UserVideoRequest;
import com.doritech.tmsservice.response.PageResponse;
import com.doritech.tmsservice.response.UserVideoResponse;
import com.doritech.tmsservice.service.UserVideoService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.TrainingAssignment;
import com.doritech.tmsservice.tms.entity.UserVideo;
import com.doritech.tmsservice.tms.entity.Video;
import com.doritech.tmsservice.tms.repository.TrainingAssignmentRepository;
import com.doritech.tmsservice.tms.repository.UserBatchRepository;
import com.doritech.tmsservice.tms.repository.UserVideoRepository;
import com.doritech.tmsservice.tms.repository.VideoRepository;

@Service
public class UserVideoServiceImpl implements UserVideoService {

	private final UserVideoRepository userVideoRepository;
	private final VideoRepository videoRepository;
	private final TrainingAssignmentRepository trainingAssignmentRepository;
	private final UserBatchRepository userBatchRepository;

	public UserVideoServiceImpl(UserVideoRepository userVideoRepository, VideoRepository videoRepository,
			TrainingAssignmentRepository trainingAssignmentRepository, UserBatchRepository userBatchRepository) {

		this.userVideoRepository = userVideoRepository;
		this.videoRepository = videoRepository;
		this.trainingAssignmentRepository = trainingAssignmentRepository;
		this.userBatchRepository = userBatchRepository;
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity createUserVideo(UserVideoRequest request) {

		try {

			if (request == null) {
				return new ResponseEntity("User video data is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getUserId() == null || request.getUserId() <= 0) {
				return new ResponseEntity("Valid user id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getVideoId() == null || request.getVideoId() <= 0) {
				return new ResponseEntity("Valid video id is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			Video video = videoRepository.findById(request.getVideoId()).orElse(null);

			if (video == null) {
				return new ResponseEntity("Video not found", HttpStatus.NOT_FOUND.value(), null);
			}

			boolean alreadyExists = userVideoRepository.existsByUserIdAndVideo_VideoId(request.getUserId(),
					request.getVideoId());

			if (alreadyExists) {
				return new ResponseEntity("Video is already assigned to this user", HttpStatus.CONFLICT.value(), null);
			}

			TrainingAssignment trainingAssignment = null;

			if (request.getTrainingAssignmentId() != null && request.getTrainingAssignmentId() > 0) {

				trainingAssignment = trainingAssignmentRepository.findById(request.getTrainingAssignmentId())
						.orElse(null);

				if (trainingAssignment == null) {
					return new ResponseEntity("Training assignment not found", HttpStatus.NOT_FOUND.value(), null);
				}

				Long assignmentUserId = trainingAssignment.getUserId();

				Long assignmentBatchId = null;

				if (trainingAssignment.getBatch() != null) {
					assignmentBatchId = trainingAssignment.getBatch().getBatchId();
				}

				if (assignmentUserId != null && assignmentUserId.equals(request.getUserId())) {

					// Direct user assignment is valid.
				}

				else if (assignmentBatchId != null && assignmentBatchId > 0) {

					boolean userBelongsToBatch = userBatchRepository.existsByUserIdAndBatch_BatchId(request.getUserId(),
							assignmentBatchId);

					if (!userBelongsToBatch) {
						return new ResponseEntity("User does not belong to the batch assigned to this training",
								HttpStatus.BAD_REQUEST.value(), null);
					}
				}

				else {

					return new ResponseEntity("Training assignment does not belong to the specified user or batch",
							HttpStatus.BAD_REQUEST.value(), null);
				}
			}

			UserVideo userVideo = new UserVideo();

			userVideo.setUserId(request.getUserId());

			userVideo.setVideo(video);

			userVideo.setTrainingAssignment(trainingAssignment);

			userVideo.setStatus(UserVideoStatus.ASSIGNED);

			userVideo.setWatchedCount(0);

			userVideo.setWatchedSeconds(0);

			userVideo.setAssignedAt(LocalDateTime.now());

			userVideo.setExpiryDate(request.getExpiryDate());

			Long currentUserId = CurrentUser.getUserId();

			if (currentUserId != null) {
				userVideo.setAssignedBy(currentUserId);
			}

			UserVideo savedUserVideo = userVideoRepository.save(userVideo);

			return new ResponseEntity("User video assigned successfully", HttpStatus.CREATED.value(),
					mapToResponse(savedUserVideo));

		} catch (DataIntegrityViolationException e) {

			return new ResponseEntity("User video assignment already exists or violates database constraints",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to assign video to user: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getUserVideoById(Long id) {

		try {

			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid user video id", HttpStatus.BAD_REQUEST.value(), null);
			}

			UserVideo userVideo = userVideoRepository.findById(id).orElse(null);

			if (userVideo == null) {
				return new ResponseEntity("User video not found", HttpStatus.NOT_FOUND.value(), null);
			}

			return new ResponseEntity("User video fetched successfully", HttpStatus.OK.value(),
					mapToResponse(userVideo));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch user video: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllUserVideos() {

		try {

			List<UserVideo> userVideos = userVideoRepository.findAll();

			List<UserVideoResponse> response = userVideos.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("User videos fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch user videos: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getUserVideosByUserId(Long userId) {

		try {

			if (userId == null || userId <= 0) {
				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
			}

			List<UserVideo> userVideos = userVideoRepository.findByUserId(userId);

			List<UserVideoResponse> response = userVideos.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("User videos fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch user videos: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getUserVideosByTrainingAssignmentId(Long trainingAssignmentId) {

		try {

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {

				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			List<UserVideo> userVideos = userVideoRepository
					.findByTrainingAssignment_TrainingAssignmentId(trainingAssignmentId);

			List<UserVideoResponse> response = userVideos.stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			return new ResponseEntity("User videos fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch user videos: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

//	@Override
//	@Transactional("tmsTransactionManager")
//	public ResponseEntity updateWatchStatus(Long id) {
//
//		try {
//
//			if (id == null || id <= 0) {
//				return new ResponseEntity("Invalid user video id", HttpStatus.BAD_REQUEST.value(), null);
//			}
//
//			UserVideo userVideo = userVideoRepository.findById(id).orElse(null);
//
//			if (userVideo == null) {
//				return new ResponseEntity("User video not found", HttpStatus.NOT_FOUND.value(), null);
//			}
//
//			LocalDateTime now = LocalDateTime.now();
//
//			if (userVideo.getStatus() == UserVideoStatus.ASSIGNED) {
//
//				userVideo.setStatus(UserVideoStatus.IN_PROGRESS);
//				userVideo.setLastWatchedAt(now);
//
//				if (userVideo.getWatchedCount() == null) {
//					userVideo.setWatchedCount(0);
//				}
//
//				userVideo.setWatchedCount(userVideo.getWatchedCount() + 1);
//
//			} else if (userVideo.getStatus() == UserVideoStatus.IN_PROGRESS) {
//
//				userVideo.setStatus(UserVideoStatus.COMPLETED);
//				userVideo.setLastWatchedAt(now);
//				userVideo.setCompletedAt(now);
//
//				if (userVideo.getWatchedCount() == null) {
//					userVideo.setWatchedCount(0);
//				}
//
//				userVideo.setWatchedCount(userVideo.getWatchedCount() + 1);
//
//			} else if (userVideo.getStatus() == UserVideoStatus.COMPLETED) {
//
//				userVideo.setLastWatchedAt(now);
//
//				if (userVideo.getWatchedCount() == null) {
//					userVideo.setWatchedCount(0);
//				}
//
//				userVideo.setWatchedCount(userVideo.getWatchedCount() + 1);
//			}
//
//			UserVideo savedUserVideo = userVideoRepository.save(userVideo);
//
//			return new ResponseEntity("Watch status updated successfully", HttpStatus.OK.value(),
//					mapToResponse(savedUserVideo));
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//
//			return new ResponseEntity("Failed to update watch status: " + e.getMessage(),
//					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
//		}
//	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity updateWatchStatus(Long id) {

		try {

			if (id == null || id <= 0) {

				return new ResponseEntity("Invalid user video id", HttpStatus.BAD_REQUEST.value(), null);
			}

			UserVideo userVideo = userVideoRepository.findById(id).orElse(null);

			if (userVideo == null) {

				return new ResponseEntity("User video not found", HttpStatus.NOT_FOUND.value(), null);
			}

			LocalDateTime now = LocalDateTime.now();

			if (userVideo.getStatus() == UserVideoStatus.ASSIGNED) {

				userVideo.setStatus(UserVideoStatus.IN_PROGRESS);
				userVideo.setLastWatchedAt(now);

				if (userVideo.getWatchedCount() == null) {
					userVideo.setWatchedCount(0);
				}

				userVideo.setWatchedCount(userVideo.getWatchedCount() + 1);
			}

			else if (userVideo.getStatus() == UserVideoStatus.IN_PROGRESS) {

				userVideo.setStatus(UserVideoStatus.COMPLETED);
				userVideo.setLastWatchedAt(now);
				userVideo.setCompletedAt(now);

				if (userVideo.getWatchedCount() == null) {
					userVideo.setWatchedCount(0);
				}

				userVideo.setWatchedCount(userVideo.getWatchedCount() + 1);
			}

			else if (userVideo.getStatus() == UserVideoStatus.COMPLETED) {

				userVideo.setLastWatchedAt(now);

				if (userVideo.getWatchedCount() == null) {
					userVideo.setWatchedCount(0);
				}

				userVideo.setWatchedCount(userVideo.getWatchedCount() + 1);
			}

			UserVideo savedUserVideo = userVideoRepository.save(userVideo);

			TrainingAssignment trainingAssignment = savedUserVideo.getTrainingAssignment();

			if (trainingAssignment == null) {

				return new ResponseEntity("Training assignment not found for user video", HttpStatus.NOT_FOUND.value(),
						null);
			}

			Long trainingAssignmentId = trainingAssignment.getTrainingAssignmentId();

			Long userId = savedUserVideo.getUserId();

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {

				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (userId == null || userId <= 0) {

				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
			}

			long totalVideos = userVideoRepository.countVideosByTrainingAssignmentAndUser(trainingAssignmentId, userId);

			long completedVideos = userVideoRepository
					.countCompletedVideosByTrainingAssignmentAndUser(trainingAssignmentId, userId);

			BigDecimal progressPercentage;

			if (totalVideos == 0) {

				progressPercentage = BigDecimal.ZERO;

			} else {

				progressPercentage = BigDecimal.valueOf(completedVideos).multiply(BigDecimal.valueOf(100))
						.divide(BigDecimal.valueOf(totalVideos), 2, RoundingMode.HALF_UP);
			}

			trainingAssignment.setProgressPercentage(progressPercentage);

			if (progressPercentage.compareTo(BigDecimal.ZERO) == 0) {

				trainingAssignment.setStatus(AssignmentStatus.NOT_STARTED);

			}

			else if (progressPercentage.compareTo(BigDecimal.valueOf(100)) == 0) {

				trainingAssignment.setStatus(AssignmentStatus.COMPLETED);

				trainingAssignment.setCompletionDate(now);

			}

			else {

				trainingAssignment.setStatus(AssignmentStatus.IN_PROGRESS);

				if (trainingAssignment.getStartedAt() == null) {

					trainingAssignment.setStartedAt(now);
				}
			}

			TrainingAssignment savedAssignment = trainingAssignmentRepository.save(trainingAssignment);

			java.util.Map<String, Object> response = new java.util.HashMap<>();

			response.put("userVideo", mapToResponse(savedUserVideo));

			response.put("trainingAssignmentId", savedAssignment.getTrainingAssignmentId());

			response.put("userId", savedAssignment.getUserId());

			response.put("totalVideos", totalVideos);

			response.put("completedVideos", completedVideos);

			response.put("progressPercentage", savedAssignment.getProgressPercentage());

			response.put("status", savedAssignment.getStatus());

			response.put("startedAt", savedAssignment.getStartedAt());

			response.put("completionDate", savedAssignment.getCompletionDate());

			return new ResponseEntity("Watch status and training progress updated successfully", HttpStatus.OK.value(),
					response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to update watch status and training progress: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity updateWatchProgress(Long id, Integer watchedSeconds) {

		try {

			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid user video id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (watchedSeconds == null || watchedSeconds < 0) {
				return new ResponseEntity("Valid watched seconds is required", HttpStatus.BAD_REQUEST.value(), null);
			}

			UserVideo userVideo = userVideoRepository.findById(id).orElse(null);

			if (userVideo == null) {
				return new ResponseEntity("User video not found", HttpStatus.NOT_FOUND.value(), null);
			}

			if (userVideo.getStatus() == UserVideoStatus.ASSIGNED) {
				userVideo.setStatus(UserVideoStatus.IN_PROGRESS);
			}

			userVideo.setLastWatchedAt(LocalDateTime.now());
			userVideo.setWatchedSeconds(watchedSeconds);

			UserVideo savedUserVideo = userVideoRepository.save(userVideo);

			return new ResponseEntity("Watch progress updated successfully", HttpStatus.OK.value(),
					mapToResponse(savedUserVideo));

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to update watch progress: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getVideoCompletionStatus(Long id) {

		try {

			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid user video id", HttpStatus.BAD_REQUEST.value(), null);
			}

			UserVideo userVideo = userVideoRepository.findById(id).orElse(null);

			if (userVideo == null) {
				return new ResponseEntity("User video not found", HttpStatus.NOT_FOUND.value(), null);
			}

			boolean completed = userVideo.getStatus() == UserVideoStatus.COMPLETED;

			return new ResponseEntity("Video completion status fetched successfully", HttpStatus.OK.value(), completed);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch video completion status: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional("tmsTransactionManager")
	public ResponseEntity deleteUserVideo(Long id) {

		try {

			if (id == null || id <= 0) {
				return new ResponseEntity("Invalid user video id", HttpStatus.BAD_REQUEST.value(), null);
			}

			UserVideo userVideo = userVideoRepository.findById(id).orElse(null);

			if (userVideo == null) {
				return new ResponseEntity("User video not found", HttpStatus.NOT_FOUND.value(), null);
			}

			userVideoRepository.delete(userVideo);

			return new ResponseEntity("User video deleted successfully", HttpStatus.OK.value(), null);

		} catch (DataIntegrityViolationException e) {

			return new ResponseEntity("User video cannot be deleted because it is being used",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to delete user video: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getAllUserVideos(int page, int size, String sortBy, String sortDir) {

		try {

			if (page < 0) {
				page = 0;
			}

			if (size <= 0) {
				size = 10;
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "userVideoId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<UserVideo> userVideoPage = userVideoRepository.findAll(pageable);

			List<UserVideoResponse> response = userVideoPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<UserVideoResponse> pageResponse = new PageResponse<>(response, userVideoPage.getNumber(),
					userVideoPage.getSize(), userVideoPage.getTotalElements(), userVideoPage.getTotalPages(),
					userVideoPage.isLast());

			return new ResponseEntity("User videos fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch user videos: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getUserVideosByUserId(Long userId, int page, int size, String sortBy, String sortDir) {

		try {

			if (userId == null || userId <= 0) {
				return new ResponseEntity("Invalid user id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (page < 0) {
				page = 0;
			}

			if (size <= 0) {
				size = 10;
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "userVideoId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<UserVideo> userVideoPage = userVideoRepository.findByUserId(userId, pageable);

			List<UserVideoResponse> response = userVideoPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<UserVideoResponse> pageResponse = new PageResponse<>(response, userVideoPage.getNumber(),
					userVideoPage.getSize(), userVideoPage.getTotalElements(), userVideoPage.getTotalPages(),
					userVideoPage.isLast());

			return new ResponseEntity("User videos fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch user videos: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(value = "tmsTransactionManager", readOnly = true)
	public ResponseEntity getUserVideosByTrainingAssignmentId(Long trainingAssignmentId, int page, int size,
			String sortBy, String sortDir) {

		try {

			if (trainingAssignmentId == null || trainingAssignmentId <= 0) {
				return new ResponseEntity("Invalid training assignment id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (page < 0) {
				page = 0;
			}

			if (size <= 0) {
				size = 10;
			}

			if (sortBy == null || sortBy.trim().isEmpty()) {
				sortBy = "userVideoId";
			}

			Sort.Direction direction = sortDir != null && sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC
					: Sort.Direction.DESC;

			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			Page<UserVideo> userVideoPage = userVideoRepository
					.findByTrainingAssignment_TrainingAssignmentId(trainingAssignmentId, pageable);

			List<UserVideoResponse> response = userVideoPage.getContent().stream().map(this::mapToResponse)
					.collect(Collectors.toList());

			PageResponse<UserVideoResponse> pageResponse = new PageResponse<>(response, userVideoPage.getNumber(),
					userVideoPage.getSize(), userVideoPage.getTotalElements(), userVideoPage.getTotalPages(),
					userVideoPage.isLast());

			return new ResponseEntity("User videos fetched successfully", HttpStatus.OK.value(), pageResponse);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Failed to fetch user videos: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private UserVideoResponse mapToResponse(UserVideo userVideo) {

		UserVideoResponse response = new UserVideoResponse();

		response.setUserVideoId(userVideo.getUserVideoId());

		response.setUserId(userVideo.getUserId());

		if (userVideo.getVideo() != null) {

			response.setVideoId(userVideo.getVideo().getVideoId());

			response.setVideoTitle(userVideo.getVideo().getVideoTitle());
		}

		if (userVideo.getTrainingAssignment() != null) {

			TrainingAssignment assignment = userVideo.getTrainingAssignment();

			response.setTrainingAssignmentId(assignment.getTrainingAssignmentId());
		}

		response.setStatus(userVideo.getStatus());

		response.setWatchedCount(userVideo.getWatchedCount());

		response.setLastWatchedAt(userVideo.getLastWatchedAt());

		response.setCompletedAt(userVideo.getCompletedAt());

		response.setExpiryDate(userVideo.getExpiryDate());

		response.setAssignedAt(userVideo.getAssignedAt());

		response.setAssignedBy(userVideo.getAssignedBy());
		response.setWatchedCount(userVideo.getWatchedCount());

		return response;
	}
}