package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.doritech.tmsservice.tms.entity.UserVideo;

public interface UserVideoRepository extends JpaRepository<UserVideo, Long> {

	boolean existsByUserIdAndVideo_VideoId(Long userId, Long videoId);

	List<UserVideo> findByUserId(Long userId);

	Page<UserVideo> findByUserId(Long userId, Pageable pageable);

	List<UserVideo> findByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId);

	Page<UserVideo> findByTrainingAssignment_TrainingAssignmentId(Long trainingAssignmentId, Pageable pageable);

	@Query("""
			    SELECT COUNT(uv)
			    FROM UserVideo uv
			    WHERE uv.trainingAssignment.trainingAssignmentId = :trainingAssignmentId
			      AND uv.userId = :userId
			""")
	long countVideosByTrainingAssignmentAndUser(@Param("trainingAssignmentId") Long trainingAssignmentId,
			@Param("userId") Long userId);

	@Query("""
			    SELECT COUNT(uv)
			    FROM UserVideo uv
			    WHERE uv.trainingAssignment.trainingAssignmentId = :trainingAssignmentId
			      AND uv.userId = :userId
			      AND uv.status = com.doritech.tmsservice.enums.UserVideoStatus.COMPLETED
			""")
	long countCompletedVideosByTrainingAssignmentAndUser(@Param("trainingAssignmentId") Long trainingAssignmentId,
			@Param("userId") Long userId);
}