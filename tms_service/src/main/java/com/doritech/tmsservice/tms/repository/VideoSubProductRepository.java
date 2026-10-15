package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.doritech.tmsservice.tms.entity.VideoSubProduct;
import com.doritech.tmsservice.tms.entity.VideoSubProduct.VideoSubProductId;


public interface VideoSubProductRepository extends JpaRepository<VideoSubProduct, VideoSubProductId> {

	List<VideoSubProduct> findByIdSubProductId(Long subProductId);

	List<VideoSubProduct> findByIdVideoId(Long videoId);

	@Query("SELECT v.id.subProductId " + "FROM VideoSubProduct v " + "WHERE v.id.videoId = :videoId")
	List<Long> getSubProductIdsByVideoId(@Param("videoId") Long videoId);

}