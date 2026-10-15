package com.doritech.tmsservice.tms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

	@Query(value = """
			SELECT
			    (SELECT COUNT(*)
			     FROM in_training_questions
			     WHERE video_id = :videoId) +

			    (SELECT COUNT(*)
			     FROM user_videos
			     WHERE video_id = :videoId) +

			    (SELECT COUNT(*)
			     FROM support_video_shares
			     WHERE video_id = :videoId)
			""", nativeQuery = true)
	Long countVideoMappings(@Param("videoId") Long videoId);

	//Page<Video> findAllVideosWithFilter(Long productId, Long subProductId, Pageable pageable);
	
	@Query("""
	    SELECT DISTINCT v
	    FROM Video v
	    JOIN VideoSubProduct vsp
	        ON vsp.id.videoId = v.videoId
	    JOIN SubProduct sp
	        ON sp.subProductId = vsp.id.subProductId
	    JOIN sp.product p
	    WHERE (:categoryId IS NULL
	           OR p.productCategory.productCategoryId = :categoryId)
	      AND (:productId IS NULL
	           OR p.productId = :productId)
	      AND (:subProductId IS NULL
	           OR sp.subProductId = :subProductId)
	""")
	Page<Video> findAllVideosWithFilter(
	        @Param("categoryId") Long categoryId,
	        @Param("productId") Long productId,
	        @Param("subProductId") Long subProductId,
	        Pageable pageable
	);

	
}