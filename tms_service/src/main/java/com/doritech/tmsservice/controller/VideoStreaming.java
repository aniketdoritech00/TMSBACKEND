package com.doritech.tmsservice.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.service.VideoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/tms/stream")
public class VideoStreaming {

	private final VideoService videoService;

	public VideoStreaming(VideoService videoService) {
		this.videoService = videoService;
	}

	@GetMapping(value = "/streamVideo", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public void streamVideo(@RequestParam("videoId") Long videoId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		videoService.streamVideo(videoId, request, response);
	}
}
