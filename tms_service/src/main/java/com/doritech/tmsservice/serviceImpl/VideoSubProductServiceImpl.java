package com.doritech.tmsservice.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.entity.ResponseEntity;
import com.doritech.tmsservice.entity.VideoSubProduct;
import com.doritech.tmsservice.entity.VideoSubProduct.VideoSubProductId;
import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.exception.DatabaseOperationException;
import com.doritech.tmsservice.exception.ResourceAlreadyExistsException;
import com.doritech.tmsservice.exception.ResourceNotFoundException;
import com.doritech.tmsservice.repository.VideoSubProductRepository;
import com.doritech.tmsservice.request.VideoSubProductRequest;
import com.doritech.tmsservice.response.VideoSubProductResponse;
import com.doritech.tmsservice.service.VideoSubProductService;

@Service
public class VideoSubProductServiceImpl implements VideoSubProductService {

	private static final Logger log = LoggerFactory.getLogger(VideoSubProductServiceImpl.class);

	@Autowired
	private VideoSubProductRepository videoSubProductRepository;

	@Override
	public ResponseEntity assignVideoToSubProduct(VideoSubProductRequest request) {

		log.info("assignVideoToSubProduct :: videoId={}, subProductId={}", request.getVideoId(),
				request.getSubProductId());

		VideoSubProductId id = new VideoSubProductId(request.getVideoId(), request.getSubProductId());

		if (videoSubProductRepository.existsById(id)) {
			log.error("assignVideoToSubProduct :: mapping already exists for videoId={}, subProductId={}",
					request.getVideoId(), request.getSubProductId());
			throw new ResourceAlreadyExistsException("Video is already assigned to this sub product");
		}

		VideoSubProduct mapping = new VideoSubProduct();
		mapping.setId(id);
		mapping.setAssignedBy(request.getAssignedBy());

		VideoSubProduct saved;
		try {
			saved = videoSubProductRepository.save(mapping);
		} catch (Exception e) {
			log.error("assignVideoToSubProduct :: error while saving - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while assigning video to sub product");
		}

		log.info("assignVideoToSubProduct :: assigned successfully videoId={}, subProductId={}", request.getVideoId(),
				request.getSubProductId());

		return new ResponseEntity("Video assigned to sub product successfully", HttpStatus.CREATED.value(),
				mapToResponse(saved));
	}

	@Override
	public ResponseEntity getVideosBySubProductId(Long subProductId) {

		log.info("getVideosBySubProductId :: request received for subProductId={}", subProductId);

		if (subProductId == null) {
			log.error("getVideosBySubProductId :: subProductId is null");
			throw new BadRequestException("Sub Product ID can not be null");
		}

		List<VideoSubProduct> mappings = videoSubProductRepository.findByIdSubProductId(subProductId);

		List<VideoSubProductResponse> responseList = mappings.stream().map(this::mapToResponse)
				.collect(Collectors.toList());

		log.info("getVideosBySubProductId :: {} mappings fetched for subProductId={}", responseList.size(),
				subProductId);

		return new ResponseEntity("Video list fetch successfully", HttpStatus.OK.value(), responseList);
	}

	@Override
	public ResponseEntity removeVideoFromSubProduct(Long videoId, Long subProductId) {

		log.info("removeVideoFromSubProduct :: videoId={}, subProductId={}", videoId, subProductId);

		if (videoId == null || subProductId == null) {
			log.error("removeVideoFromSubProduct :: videoId or subProductId is null");
			throw new BadRequestException("Video ID and Sub Product ID can not be null");
		}

		VideoSubProductId id = new VideoSubProductId(videoId, subProductId);

		VideoSubProduct mapping = videoSubProductRepository.findById(id).orElseThrow(() -> {
			log.error("removeVideoFromSubProduct :: mapping not found for videoId={}, subProductId={}", videoId,
					subProductId);
			return new ResourceNotFoundException("Mapping not found for given video and sub product");
		});

		try {
			videoSubProductRepository.delete(mapping);
		} catch (Exception e) {
			log.error("removeVideoFromSubProduct :: error while deleting - {}", e.getMessage(), e);
			throw new DatabaseOperationException("Something went wrong while removing the mapping");
		}

		log.info("removeVideoFromSubProduct :: removed successfully videoId={}, subProductId={}", videoId,
				subProductId);

		return new ResponseEntity("Video removed from sub product successfully", HttpStatus.OK.value(), null);
	}

	private VideoSubProductResponse mapToResponse(VideoSubProduct entity) {
		return new VideoSubProductResponse(entity.getId().getVideoId(), entity.getId().getSubProductId(),
				entity.getAssignedAt(), entity.getAssignedBy());
	}
}