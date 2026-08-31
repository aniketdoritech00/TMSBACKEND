package com.doritech.tmsservice.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.request.VideoMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VideoMetadataService {

	private final ObjectMapper objectMapper;

	public VideoMetadataService(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public VideoMetadata extractMetadata(Path videoPath) {
		if (videoPath == null) {
			throw new BadRequestException("Video path is null");
		}
		if (!Files.exists(videoPath)) {
			throw new BadRequestException("Video file does not exist: " + videoPath);
		}
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("ffprobe", "-v", "quiet", "-print_format", "json",
					"-show_format", "-show_streams", videoPath.toString());
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();
			String json;
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				json = reader.lines().collect(Collectors.joining());
			}
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				throw new BadRequestException("Unable to read video metadata");
			}
			JsonNode root = objectMapper.readTree(json);
			String videoFormat = null;
			JsonNode formatNode = root.path("format");
			if (formatNode.has("format_name")) {
				videoFormat = formatNode.get("format_name").asText();
			}
			Integer durationSeconds = null;
			if (formatNode.has("duration")) {
				double duration = formatNode.get("duration").asDouble();
				durationSeconds = (int) Math.ceil(duration);
			}

			String resolution = null;
			JsonNode streams = root.path("streams");
			if (streams.isArray()) {
				for (JsonNode stream : streams) {
					String codecType = stream.path("codec_type").asText();
					if ("video".equals(codecType)) {
						int width = stream.path("width").asInt();
						int height = stream.path("height").asInt();
						if (width > 0 && height > 0) {
							resolution = width + "x" + height;
						}
						break;
					}
				}
			}
			VideoMetadata metadata = new VideoMetadata();
			metadata.setDurationSeconds(durationSeconds);
			metadata.setVideoFormat(videoFormat);
			metadata.setResolution(resolution);
			return metadata;
		} catch (BadRequestException e) {
			throw e;
		} catch (Exception e) {
			throw new BadRequestException("Failed to extract video metadata");
		}
	}
}