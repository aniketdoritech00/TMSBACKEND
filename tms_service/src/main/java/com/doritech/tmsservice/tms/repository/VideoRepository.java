package com.doritech.tmsservice.tms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.doritech.tmsservice.tms.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query(value = """
        SELECT
            (SELECT COUNT(*) FROM in_training_questions WHERE video_id = :videoId) +
            (SELECT COUNT(*) FROM video_sub_products WHERE video_id = :videoId) +
            (SELECT COUNT(*) FROM user_videos WHERE video_id = :videoId) +
            (SELECT COUNT(*) FROM video_access_control WHERE video_id = :videoId) +
            (SELECT COUNT(*) FROM support_video_shares WHERE video_id = :videoId)
        """, nativeQuery = true)
    Long countVideoMappings(@Param("videoId") Long videoId);
}