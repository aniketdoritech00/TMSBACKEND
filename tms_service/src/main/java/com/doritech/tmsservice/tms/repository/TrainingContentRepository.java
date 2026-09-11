package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.enums.ContentType;
import com.doritech.tmsservice.tms.entity.TrainingContent;

public interface TrainingContentRepository extends JpaRepository<TrainingContent, Long> {

	boolean existsByTraining_TrainingIdAndContentTypeAndContentReferenceId(Long trainingId, ContentType contentType,
			Long contentReferenceId);

	boolean existsByTraining_TrainingIdAndContentTypeAndContentReferenceIdAndTrainingContentIdNot(Long trainingId,
			ContentType contentType, Long contentReferenceId, Long trainingContentId);

	Page<TrainingContent> findByTraining_TrainingId(Long trainingId, Pageable pageable);

	List<TrainingContent> findByTraining_TrainingId(Long trainingId);

	boolean existsByTraining_TrainingIdAndContentTypeAndContentReferenceIdAndTrainingContentIdNot(Long trainingId,
			String contentType, Long contentReferenceId, Long id);
}