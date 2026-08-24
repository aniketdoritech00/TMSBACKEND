package com.doritech.tmsservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.entity.VideoSubProduct;
import com.doritech.tmsservice.entity.VideoSubProduct.VideoSubProductId;
@Repository
public interface VideoSubProductRepository extends JpaRepository<VideoSubProduct, VideoSubProductId> {

	List<VideoSubProduct> findByIdSubProductId(Long subProductId);

	List<VideoSubProduct> findByIdVideoId(Long videoId);
}