package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.entity.UserVideo;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceAlreadyExistsException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.UserVideoRepository;
import com.doritech.tmsservice.request.UserVideoRequest;
import com.doritech.tmsservice.response.UserVideoListResponse;
import com.doritech.tmsservice.response.UserVideoResponse;
import com.doritech.tmsservice.service.UserVideoService;

@Service
public class UserVideoServiceImpl implements UserVideoService {

	private static final Logger log = LoggerFactory.getLogger(UserVideoServiceImpl.class);

	@Autowired
	private UserVideoRepository userVideoRepository;

	@Override
	public ResponseEntity createUserVideo(UserVideoRequest request) {

		log.info("createUserVideo :: request received for userId={}, videoId={}", request.getUserId(),
				request.getVideoId());

		if (userVideoRepository.existsByUserIdAndVideoId(request.getUserId(), request.getVideoId())) {
			log.error("createUserVideo :: duplicate mapping for userId={}, videoId={}", request.getUserId(),
					request.getVideoId());
			throw new ResourceAlreadyExistsException("This video is already assigned to the user");
		}

		Long currentUserId = CurrentUser.getUserId();

		UserVideo userVideo = new UserVideo();
		userVideo.setUserId(request.getUserId());
		userVideo.setVideoId(request.getVideoId());
		userVideo.setTrainingAssignmentId(request.getTrainingAssignmentId());
		userVideo.setExpiryDate(request.getExpiryDate());
		userVideo.setAssignedBy(currentUserId);

		UserVideo saved;
		try {
			saved = userVideoRepository.save(userVideo);
		} catch (Exception e) {
			log.error("createUserVideo :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving user video");
		}

		log.info("createUserVideo :: saved successfully with id={} by userId={}", saved.getUserVideoId(),
				currentUserId);

		return new ResponseEntity("User video saved successfully", HttpStatus.CREATED.value(),
				mapToFullResponse(saved));
	}

	@Override
	public ResponseEntity getUserVideoById(Long id) {

		log.info("getUserVideoById :: request received for id={}", id);

		if (id == null) {
			log.error("getUserVideoById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		UserVideo userVideo = userVideoRepository.findById(id).orElseThrow(() -> {
			log.error("getUserVideoById :: not found for id={}", id);
			return new ResourceNotFoundException("User video not found with id: " + id);
		});

		log.info("getUserVideoById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), mapToFullResponse(userVideo));
	}

	@Override
	public ResponseEntity getAllUserVideo(int page, int size, String sortBy, String sortDir) {

		log.info("getAllUserVideo :: request received with page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy,
				sortDir);

		if (page < 0) {
			log.error("getAllUserVideo :: page cannot be negative");
			throw new BadRequestException("Page number can not be negative");
		}

		if (size <= 0) {
			log.error("getAllUserVideo :: size must be greater than 0");
			throw new BadRequestException("Page size must be greater than 0");
		}

		if (size > 100) {
			log.error("getAllUserVideo :: size exceeds max limit={}", size);
			throw new BadRequestException("Page size can not exceed 100");
		}

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<UserVideo> userVideoPage;
		try {
			userVideoPage = userVideoRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			log.error("getAllUserVideo :: invalid sort field={}", sortBy);
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			log.error("getAllUserVideo :: error while fetching - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while fetching user videos");
		}

		List<UserVideoListResponse> responseList = userVideoPage.getContent().stream().map(this::mapToListResponse)
				.collect(Collectors.toList());

		Map<String, Object> pageData = new LinkedHashMap<>();
		pageData.put("content", responseList);
		pageData.put("pageNumber", userVideoPage.getNumber());
		pageData.put("pageSize", userVideoPage.getSize());
		pageData.put("totalElements", userVideoPage.getTotalElements());
		pageData.put("totalPages", userVideoPage.getTotalPages());
		pageData.put("isLast", userVideoPage.isLast());

		log.info("getAllUserVideo :: {} of {} user videos fetched successfully", responseList.size(),
				userVideoPage.getTotalElements());

		return new ResponseEntity("User video fetch successfully", HttpStatus.OK.value(), pageData);
	}

	@Override
	public ResponseEntity getVideosByUserId(Long userId) {

		log.info("getVideosByUserId :: request received for userId={}", userId);

		if (userId == null) {
			log.error("getVideosByUserId :: userId is null");
			throw new BadRequestException("User ID can not be null");
		}

		List<UserVideo> userVideoList = userVideoRepository.findByUserId(userId);

		List<UserVideoListResponse> responseList = userVideoList.stream().map(this::mapToListResponse)
				.collect(Collectors.toList());

		log.info("getVideosByUserId :: {} videos fetched for userId={}", responseList.size(), userId);

		return new ResponseEntity("User video fetch successfully", HttpStatus.OK.value(), responseList);
	}

	@Override
	public ResponseEntity markVideoWatched(Long id) {

		log.info("markVideoWatched :: request received for id={}", id);

		if (id == null) {
			log.error("markVideoWatched :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		UserVideo userVideo = userVideoRepository.findById(id).orElseThrow(() -> {
			log.error("markVideoWatched :: not found for id={}", id);
			return new ResourceNotFoundException("User video not found with id: " + id);
		});

		userVideo.setWatchedCount(userVideo.getWatchedCount() == null ? 1 : userVideo.getWatchedCount() + 1);
		userVideo.setLastWatchedAt(LocalDateTime.now());

		if (userVideo.getStatus() == UserVideo.Status.ASSIGNED) {
			userVideo.setStatus(UserVideo.Status.WATCHING);
		}

		UserVideo saved;
		try {
			saved = userVideoRepository.save(userVideo);
		} catch (Exception e) {
			log.error("markVideoWatched :: error while updating - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while updating user video");
		}

		log.info("markVideoWatched :: updated successfully for id={}", id);

		return new ResponseEntity("User video updated successfully", HttpStatus.OK.value(), mapToFullResponse(saved));
	}

	@Override
	public ResponseEntity deleteUserVideo(Long id) {

		log.info("deleteUserVideo :: request received for id={}", id);

		if (id == null) {
			log.error("deleteUserVideo :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		UserVideo userVideo = userVideoRepository.findById(id).orElseThrow(() -> {
			log.error("deleteUserVideo :: not found for id={}", id);
			return new ResourceNotFoundException("User video not found with id: " + id);
		});

		try {
			userVideoRepository.delete(userVideo);
		} catch (Exception e) {
			log.error("deleteUserVideo :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Cannot delete user video, it may be linked to other records");
		}

		log.info("deleteUserVideo :: deleted successfully for id={}", id);

		return new ResponseEntity("User video deleted successfully", HttpStatus.OK.value(), null);
	}

	private UserVideoResponse mapToFullResponse(UserVideo entity) {
		UserVideoResponse response = new UserVideoResponse();
		response.setUserVideoId(entity.getUserVideoId());
		response.setUserId(entity.getUserId());
		response.setVideoId(entity.getVideoId());
		response.setTrainingAssignmentId(entity.getTrainingAssignmentId());
		response.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
		response.setWatchedCount(entity.getWatchedCount());
		response.setLastWatchedAt(entity.getLastWatchedAt());
		response.setCompletedAt(entity.getCompletedAt());
		response.setExpiryDate(entity.getExpiryDate());
		response.setAssignedAt(entity.getAssignedAt());
		response.setAssignedBy(entity.getAssignedBy());
		return response;
	}

	private UserVideoListResponse mapToListResponse(UserVideo entity) {
		return new UserVideoListResponse(entity.getUserVideoId(), entity.getUserId(), entity.getVideoId(),
				entity.getStatus() != null ? entity.getStatus().name() : null);
	}
}