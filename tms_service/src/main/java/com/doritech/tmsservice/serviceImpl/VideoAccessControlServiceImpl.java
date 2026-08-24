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

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.entity.VideoAccessControl;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceAlreadyExistsException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.VideoAccessControlRepository;
import com.doritech.tmsservice.request.VideoAccessControlRequest;
import com.doritech.tmsservice.response.VideoAccessControlListResponse;
import com.doritech.tmsservice.response.VideoAccessControlResponse;
import com.doritech.tmsservice.service.VideoAccessControlService;

@Service
public class VideoAccessControlServiceImpl implements VideoAccessControlService {

	private static final Logger log = LoggerFactory.getLogger(VideoAccessControlServiceImpl.class);

	@Autowired
	private VideoAccessControlRepository videoAccessControlRepository;

	
	@Override
	public ResponseEntity createVideoAccessControl(VideoAccessControlRequest request) {

		log.info("createVideoAccessControl :: request received for videoId={}, userId={}", request.getVideoId(),
				request.getUserId());

		if (videoAccessControlRepository.existsByVideoIdAndUserId(request.getVideoId(), request.getUserId())) {
			log.error("createVideoAccessControl :: duplicate access for videoId={}, userId={}", request.getVideoId(),
					request.getUserId());
			throw new ResourceAlreadyExistsException("Access already exists for this video and user");
		}

		VideoAccessControl access = new VideoAccessControl();
		access.setVideoId(request.getVideoId());
		access.setUserId(request.getUserId());
		access.setAccessDurationDays(request.getAccessDurationDays());
		access.setIsActive(request.getIsActive());

		VideoAccessControl saved;
		try {
			saved = videoAccessControlRepository.save(access);
		} catch (Exception e) {
			log.error("createVideoAccessControl :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while saving video access control");
		}

		log.info("createVideoAccessControl :: saved successfully with id={}", saved.getVideoAccessId());

		return new ResponseEntity("Video access control saved successfully", HttpStatus.CREATED.value(),
				mapToFullResponse(saved));
	}

	@Override
	public ResponseEntity getVideoAccessControlById(Long id) {

		log.info("getVideoAccessControlById :: request received for id={}", id);

		if (id == null) {
			log.error("getVideoAccessControlById :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		VideoAccessControl access = videoAccessControlRepository.findById(id).orElseThrow(() -> {
			log.error("getVideoAccessControlById :: not found for id={}", id);
			return new ResourceNotFoundException("Video access control not found with id: " + id);
		});

		log.info("getVideoAccessControlById :: fetched successfully for id={}", id);

		return new ResponseEntity("Fetch Data By Id", HttpStatus.OK.value(), mapToFullResponse(access));
	}

	@Override
	public ResponseEntity getAllVideoAccessControl(int page, int size, String sortBy, String sortDir) {

		log.info("getAllVideoAccessControl :: request received with page={}, size={}, sortBy={}, sortDir={}", page,
				size, sortBy, sortDir);

		if (page < 0) {
			log.error("getAllVideoAccessControl :: page cannot be negative");
			throw new BadRequestException("Page number can not be negative");
		}

		if (size <= 0) {
			log.error("getAllVideoAccessControl :: size must be greater than 0");
			throw new BadRequestException("Page size must be greater than 0");
		}

		if (size > 100) {
			log.error("getAllVideoAccessControl :: size exceeds max limit={}", size);
			throw new BadRequestException("Page size can not exceed 100");
		}

		Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		Pageable pageable = PageRequest.of(page, size, sort);

		Page<VideoAccessControl> accessPage;
		try {
			accessPage = videoAccessControlRepository.findAll(pageable);
		} catch (PropertyReferenceException e) {
			log.error("getAllVideoAccessControl :: invalid sort field={}", sortBy);
			throw new BadRequestException("Invalid sort field: " + sortBy);
		} catch (Exception e) {
			log.error("getAllVideoAccessControl :: error while fetching - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while fetching video access controls");
		}

		List<VideoAccessControlListResponse> responseList = accessPage.getContent().stream()
				.map(this::mapToListResponse).collect(Collectors.toList());

		Map<String, Object> pageData = new LinkedHashMap<>();
		pageData.put("content", responseList);
		pageData.put("pageNumber", accessPage.getNumber());
		pageData.put("pageSize", accessPage.getSize());
		pageData.put("totalElements", accessPage.getTotalElements());
		pageData.put("totalPages", accessPage.getTotalPages());
		pageData.put("isLast", accessPage.isLast());

		log.info("getAllVideoAccessControl :: {} of {} access records fetched successfully", responseList.size(),
				accessPage.getTotalElements());

		return new ResponseEntity("Video access control fetch successfully", HttpStatus.OK.value(), pageData);
	}

	@Override
	public ResponseEntity getAccessByUserId(Long userId) {

		log.info("getAccessByUserId :: request received for userId={}", userId);

		if (userId == null) {
			log.error("getAccessByUserId :: userId is null");
			throw new BadRequestException("User ID can not be null");
		}

		List<VideoAccessControl> accessList = videoAccessControlRepository.findByUserId(userId);

		List<VideoAccessControlListResponse> responseList = accessList.stream().map(this::mapToListResponse)
				.collect(Collectors.toList());

		log.info("getAccessByUserId :: {} access records fetched for userId={}", responseList.size(), userId);

		return new ResponseEntity("Video access control fetch successfully", HttpStatus.OK.value(), responseList);
	}

	@Override
	public ResponseEntity checkAccess(Long videoId, Long userId) {

		log.info("checkAccess :: request received for videoId={}, userId={}", videoId, userId);

		if (videoId == null || userId == null) {
			log.error("checkAccess :: videoId or userId is null");
			throw new BadRequestException("Video ID and User ID can not be null");
		}

		VideoAccessControl access = videoAccessControlRepository.findByVideoIdAndUserId(videoId, userId)
				.orElseThrow(() -> {
					log.error("checkAccess :: no access record for videoId={}, userId={}", videoId, userId);
					return new ResourceNotFoundException("No access record found for this video and user");
				});

		boolean hasAccess = Boolean.TRUE.equals(access.getIsActive())
				&& (access.getAccessExpiryDate() == null || access.getAccessExpiryDate().isAfter(LocalDateTime.now()));

		log.info("checkAccess :: access={} for videoId={}, userId={}", hasAccess, videoId, userId);

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("hasAccess", hasAccess);
		result.put("accessExpiryDate", access.getAccessExpiryDate());

		return new ResponseEntity("Access check completed", HttpStatus.OK.value(), result);
	}

	@Override
	public ResponseEntity revokeAccess(Long id) {

		log.info("revokeAccess :: request received for id={}", id);

		if (id == null) {
			log.error("revokeAccess :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		VideoAccessControl access = videoAccessControlRepository.findById(id).orElseThrow(() -> {
			log.error("revokeAccess :: not found for id={}", id);
			return new ResourceNotFoundException("Video access control not found with id: " + id);
		});

		access.setIsActive(false);

		VideoAccessControl saved;
		try {
			saved = videoAccessControlRepository.save(access);
		} catch (Exception e) {
			log.error("revokeAccess :: error while updating - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while revoking access");
		}

		log.info("revokeAccess :: revoked successfully for id={}", id);

		return new ResponseEntity("Video access revoked successfully", HttpStatus.OK.value(), mapToFullResponse(saved));
	}

	@Override
	public ResponseEntity deleteVideoAccessControl(Long id) {

		log.info("deleteVideoAccessControl :: request received for id={}", id);

		if (id == null) {
			log.error("deleteVideoAccessControl :: id is null");
			throw new BadRequestException("ID can not be null");
		}

		VideoAccessControl access = videoAccessControlRepository.findById(id).orElseThrow(() -> {
			log.error("deleteVideoAccessControl :: not found for id={}", id);
			return new ResourceNotFoundException("Video access control not found with id: " + id);
		});

		try {
			videoAccessControlRepository.delete(access);
		} catch (Exception e) {
			log.error("deleteVideoAccessControl :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException(
					"Cannot delete video access control, it may be linked to other records");
		}

		log.info("deleteVideoAccessControl :: deleted successfully for id={}", id);

		return new ResponseEntity("Video access control deleted successfully", HttpStatus.OK.value(), null);
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
}