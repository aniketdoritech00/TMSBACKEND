package com.doritech.tmsservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.entity.UserVideo;

@Repository
public interface UserVideoRepository extends JpaRepository<UserVideo, Long> {

	boolean existsByUserIdAndVideoId(Long userId, Long videoId);

	Optional<UserVideo> findByUserIdAndVideoId(Long userId, Long videoId);

	List<UserVideo> findByUserId(Long userId);

	List<UserVideo> findByVideoId(Long videoId);
}