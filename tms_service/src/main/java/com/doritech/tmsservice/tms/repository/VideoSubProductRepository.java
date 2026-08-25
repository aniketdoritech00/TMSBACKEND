package com.doritech.tmsservice.tms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.VideoSubProduct;
import com.doritech.tmsservice.tms.entity.VideoSubProduct.VideoSubProductId;
@Repository
public interface VideoSubProductRepository extends JpaRepository<VideoSubProduct, VideoSubProductId> {

	List<VideoSubProduct> findByIdSubProductId(Long subProductId);

	List<VideoSubProduct> findByIdVideoId(Long videoId);
}