package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.doritech.tmsservice.tms.entity.UserVideo;

public interface UserVideoRepository extends JpaRepository<UserVideo, Long> {

	boolean existsByUserIdAndVideo_VideoId(Long userId, Long videoId);

	List<UserVideo> findByUserId(Long userId);

	Page<UserVideo> findByUserId(Long userId, Pageable pageable);

	List<UserVideo> findByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);

	Page<UserVideo> findByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId, Pageable pageable);
}