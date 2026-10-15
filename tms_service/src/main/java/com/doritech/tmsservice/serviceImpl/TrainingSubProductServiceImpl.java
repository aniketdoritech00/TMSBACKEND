package com.doritech.tmsservice.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.doritech.tmsservice.config.CurrentUser;
import com.doritech.tmsservice.request.TrainingSubProductRequest;
import com.doritech.tmsservice.response.TrainingSubProductResponse;
import com.doritech.tmsservice.service.TrainingSubProductService;
import com.doritech.tmsservice.tms.entity.ResponseEntity;
import com.doritech.tmsservice.tms.entity.SubProduct;
import com.doritech.tmsservice.tms.entity.Training;
import com.doritech.tmsservice.tms.entity.TrainingSubProduct;
import com.doritech.tmsservice.tms.entity.TrainingSubProductId;
import com.doritech.tmsservice.tms.repository.SubProductRepository;
import com.doritech.tmsservice.tms.repository.TrainingRepository;
import com.doritech.tmsservice.tms.repository.TrainingSubProductRepository;

@Service
public class TrainingSubProductServiceImpl implements TrainingSubProductService {

	private final TrainingRepository trainingRepository;
	private final SubProductRepository subProductRepository;
	private final TrainingSubProductRepository trainingSubProductRepository;

	public TrainingSubProductServiceImpl(TrainingRepository trainingRepository,
			SubProductRepository subProductRepository, TrainingSubProductRepository trainingSubProductRepository) {

		this.trainingRepository = trainingRepository;
		this.subProductRepository = subProductRepository;
		this.trainingSubProductRepository = trainingSubProductRepository;
	}

	@Override
	@Transactional(transactionManager = "tmsTransactionManager")
	public ResponseEntity assignSubProductToTraining(TrainingSubProductRequest request) {

		try {

			if (request == null) {
				return new ResponseEntity("Request cannot be null", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getTrainingId() == null || request.getTrainingId() <= 0) {

				return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
			}

			if (request.getSubProductId() == null || request.getSubProductId() <= 0) {

				return new ResponseEntity("Invalid sub product id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Training training = trainingRepository.findById(request.getTrainingId()).orElse(null);

			if (training == null) {

				return new ResponseEntity("Training not found with id: " + request.getTrainingId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			SubProduct subProduct = subProductRepository.findById(request.getSubProductId()).orElse(null);

			if (subProduct == null) {

				return new ResponseEntity("Sub product not found with id: " + request.getSubProductId(),
						HttpStatus.NOT_FOUND.value(), null);
			}

			boolean alreadyAssigned = trainingSubProductRepository
					.existsByTraining_TrainingIdAndSubProduct_SubProductId(request.getTrainingId(),
							request.getSubProductId());

			if (alreadyAssigned) {

				return new ResponseEntity("Sub product is already assigned to this training",
						HttpStatus.CONFLICT.value(), null);
			}

			// Create composite primary key
			TrainingSubProductId id = new TrainingSubProductId(request.getTrainingId(), request.getSubProductId());

			// Create mapping entity
			TrainingSubProduct mapping = new TrainingSubProduct();

			mapping.setId(id);

			// These are only for relationship/navigation
			mapping.setTraining(training);
			mapping.setSubProduct(subProduct);

			mapping.setAssignedAt(LocalDateTime.now());
			mapping.setAssignedBy(CurrentUser.getUserId());

			TrainingSubProduct saved = trainingSubProductRepository.save(mapping);

			// Build response while transaction is active
			TrainingSubProductResponse response = new TrainingSubProductResponse();

			response.setTrainingId(training.getTrainingId());
			response.setTrainingName(training.getTrainingName());
			response.setTrainingCode(training.getTrainingCode());

			response.setSubProductId(subProduct.getSubProductId());
			response.setSubProductName(subProduct.getSubProductName());
			response.setSubProductCode(subProduct.getSubProductCode());

			response.setAssignedAt(saved.getAssignedAt());
			response.setAssignedBy(saved.getAssignedBy());

			return new ResponseEntity("Sub product assigned to training successfully", HttpStatus.CREATED.value(),
					response);

		} catch (DataIntegrityViolationException e) {

			e.printStackTrace();

			return new ResponseEntity("Sub product could not be assigned to training because of a database constraint",
					HttpStatus.CONFLICT.value(), null);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(transactionManager = "tmsTransactionManager")
	public ResponseEntity getSubProductsByTrainingId(Long trainingId) {

		try {

			if (trainingId == null || trainingId <= 0) {

				return new ResponseEntity("Invalid training id", HttpStatus.BAD_REQUEST.value(), null);
			}

			Training training = trainingRepository.findById(trainingId).orElse(null);

			if (training == null) {

				return new ResponseEntity("Training not found with id: " + trainingId, HttpStatus.NOT_FOUND.value(),
						null);
			}

			List<TrainingSubProduct> mappings = trainingSubProductRepository.findByTraining_TrainingId(trainingId);

			List<TrainingSubProductResponse> response = mappings.stream().map(this::mapToResponse).toList();

			return new ResponseEntity("Sub products fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	@Transactional(transactionManager = "tmsTransactionManager")
	public ResponseEntity getTrainingsBySubProductId(Long subProductId) {

		try {

			if (subProductId == null || subProductId <= 0) {
				return new ResponseEntity("Invalid sub product id", HttpStatus.BAD_REQUEST.value(), null);
			}

			SubProduct subProduct = subProductRepository.findById(subProductId).orElse(null);

			if (subProduct == null) {
				return new ResponseEntity("Sub product not found with id: " + subProductId,
						HttpStatus.NOT_FOUND.value(), null);
			}

			List<TrainingSubProduct> mappings = trainingSubProductRepository
					.findBySubProductIdWithDetails(subProductId);

			List<TrainingSubProductResponse> response = mappings.stream().map(this::mapToResponse).toList();

			return new ResponseEntity("Trainings fetched successfully", HttpStatus.OK.value(), response);

		} catch (Exception e) {

			e.printStackTrace();

			return new ResponseEntity("Internal server error!", HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	@Override
	public ResponseEntity deleteTrainingSubProduct(Long trainingId, Long subProductId) {
		try {
			if (trainingId == null || trainingId <= 0 || subProductId == null || subProductId <= 0) {
				return new ResponseEntity("Invalid training id or sub product id", HttpStatus.BAD_REQUEST.value(),
						null);
			}

			boolean exists = trainingSubProductRepository
					.existsByTraining_TrainingIdAndSubProduct_SubProductId(trainingId, subProductId);
			if (!exists) {
				return new ResponseEntity("Training and sub product mapping not found", HttpStatus.NOT_FOUND.value(),
						null);
			}

//			trainingSubProductRepository.deleteByTraining_TrainingIdAndSubProduct_SubProductId(trainingId,
//					subProductId);
			return new ResponseEntity("Sub product removed from training successfully", HttpStatus.OK.value(), null);
		} catch (Exception e) {
			e.printStackTrace();

			return new ResponseEntity("Something went wrong while removing sub product from training",
					HttpStatus.INTERNAL_SERVER_ERROR.value(), null);
		}
	}

	private TrainingSubProductResponse mapToResponse(TrainingSubProduct entity) {

		TrainingSubProductResponse response = new TrainingSubProductResponse();

		response.setTrainingId(entity.getTraining().getTrainingId());
		response.setSubProductId(entity.getSubProduct().getSubProductId());

		response.setTrainingName(entity.getTraining().getTrainingName());
		response.setTrainingCode(entity.getTraining().getTrainingCode());

		response.setSubProductName(entity.getSubProduct().getSubProductName());
		response.setSubProductCode(entity.getSubProduct().getSubProductCode());

		response.setAssignedAt(entity.getAssignedAt());
		response.setAssignedBy(entity.getAssignedBy());

		return response;
	}
}