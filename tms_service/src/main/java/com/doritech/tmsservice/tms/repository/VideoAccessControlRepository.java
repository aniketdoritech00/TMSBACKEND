package com.doritech.tmsservice.tms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.VideoAccessControl;

@Repository
public interface VideoAccessControlRepository extends JpaRepository<VideoAccessControl, Long> {

	boolean existsByVideoIdAndUserId(Long videoId, Long userId);

	Optional<VideoAccessControl> findByVideoIdAndUserId(Long videoId, Long userId);

	List<VideoAccessControl> findByUserId(Long userId);

	@Modifying
	@Query(value = "DELETE FROM video_access_control WHERE video_id = :videoId", nativeQuery = true)
	int deleteByVideoId(@Param("videoId") Long videoId);
}