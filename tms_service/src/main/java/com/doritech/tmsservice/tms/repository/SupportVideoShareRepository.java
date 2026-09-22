package com.doritech.tmsservice.tms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.SupportVideoShare;

@Repository
public interface SupportVideoShareRepository extends JpaRepository<SupportVideoShare, Long> {

	Optional<SupportVideoShare> findByShareToken(String shareToken);

	Optional<SupportVideoShare> findByShareTokenAndUserId(String shareToken, Long userId);

	Optional<SupportVideoShare> findByVideoIdAndUserId(Long videoId, Long userId);

	boolean existsByVideoIdAndUserId(Long videoId, Long userId);
}