package com.doritech.tmsservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.entity.VideoAccessControl;

@Repository
public interface VideoAccessControlRepository extends JpaRepository<VideoAccessControl, Long> {

	boolean existsByVideoIdAndUserId(Long videoId, Long userId);

	Optional<VideoAccessControl> findByVideoIdAndUserId(Long videoId, Long userId);

	List<VideoAccessControl> findByUserId(Long userId);
}