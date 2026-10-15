package com.doritech.tmsservice.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.doritech.tmsservice.exception.BadRequestException;
import com.doritech.tmsservice.request.VideoMetadata;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class VideoMetadataService {

	private static final Logger log = LoggerFactory.getLogger(VideoMetadataService.class);

	private static final Pattern FFMPEG_DURATION_PATTERN =
			Pattern.compile("Duration:\\s*(\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{2})");

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

		JsonNode root = runFfprobe(videoPath);

		JsonNode formatNode = root.path("format");
		JsonNode streamsNode = root.path("streams");

		String videoFormat = null;
		if (formatNode.has("format_name")) {
			videoFormat = formatNode.get("format_name").asText();
		}

		String resolution = null;
		JsonNode primaryVideoStream = null;
		if (streamsNode.isArray()) {
			for (JsonNode stream : streamsNode) {
				if ("video".equals(stream.path("codec_type").asText())) {
					primaryVideoStream = stream;
					int width = stream.path("width").asInt();
					int height = stream.path("height").asInt();
					if (width > 0 && height > 0) {
						resolution = width + "x" + height;
					}
					break;
				}
			}
		}

		Integer durationSeconds = parseDurationSeconds(formatNode.get("duration"));

		if (durationSeconds == null && primaryVideoStream != null) {
			durationSeconds = parseDurationSeconds(primaryVideoStream.get("duration"));
		}

		if (durationSeconds == null) {
			durationSeconds = parseTagDuration(formatNode.path("tags"));
		}
		if (durationSeconds == null && primaryVideoStream != null) {
			durationSeconds = parseTagDuration(primaryVideoStream.path("tags"));
		}

		if (durationSeconds == null) {
			log.warn("No header duration found for file={}, falling back to ffmpeg decode scan", videoPath);
			durationSeconds = computeDurationViaDecodeScan(videoPath);
		}

		System.out.println("Total duration of the video is " + durationSeconds);

		if (durationSeconds == null || durationSeconds <= 0) {
			log.error("Could not determine duration for file={} by any method", videoPath);
			throw new BadRequestException(
					"Unable to determine video duration — file may be corrupt or unsupported: "
							+ videoPath.getFileName());
		}

		VideoMetadata metadata = new VideoMetadata();
		metadata.setDurationSeconds(durationSeconds);
		metadata.setVideoFormat(videoFormat);
		metadata.setResolution(resolution);
		return metadata;
	}

	private JsonNode runFfprobe(Path videoPath) {
		String json;
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("ffprobe", "-v", "quiet", "-print_format", "json",
					"-show_format", "-show_streams", videoPath.toString());
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				json = reader.lines().collect(Collectors.joining());
			}
			int exitCode = process.waitFor();
			if (exitCode != 0) {
				log.warn("ffprobe exited with code {} for file={}", exitCode, videoPath);
				throw new BadRequestException("Unable to read video metadata");
			}
		} catch (BadRequestException e) {
			throw e;
		} catch (Exception e) {
			log.error("Failed to run ffprobe for file={}", videoPath, e);
			throw new BadRequestException("Failed to extract video metadata");
		}

		if (json == null || json.isBlank()) {
			log.warn("ffprobe returned empty output for file={}", videoPath);
			throw new BadRequestException("Video metadata could not be read — file may be corrupt or empty");
		}

		try {
			return objectMapper.readTree(json);
		} catch (Exception e) {
			log.error("Failed to parse ffprobe JSON for file={} json={}", videoPath, json, e);
			throw new BadRequestException("Failed to parse video metadata");
		}
	}

	private Integer computeDurationViaDecodeScan(Path videoPath) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(
					"ffmpeg", "-i", videoPath.toString(), "-f", "null", "-");
			processBuilder.redirectErrorStream(true);
			Process process = processBuilder.start();

			String output;
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				output = reader.lines().collect(Collectors.joining("\n"));
			}
			process.waitFor();

			Matcher matcher = FFMPEG_DURATION_PATTERN.matcher(output);
			if (matcher.find()) {
				int hours = Integer.parseInt(matcher.group(1));
				int minutes = Integer.parseInt(matcher.group(2));
				int seconds = Integer.parseInt(matcher.group(3));
				int centiseconds = Integer.parseInt(matcher.group(4));

				double totalSeconds = hours * 3600 + minutes * 60 + seconds + centiseconds / 100.0;
				if (totalSeconds > 0) {
					return (int) Math.round(totalSeconds);
				}
			}
			log.warn("ffmpeg decode scan did not find a duration for file={}", videoPath);
			return null;
		} catch (Exception e) {
			log.error("ffmpeg decode scan failed for file={}", videoPath, e);
			return null;
		}
	}

	private Integer parseDurationSeconds(JsonNode durationNode) {
		if (durationNode == null || durationNode.isMissingNode() || durationNode.isNull()) {
			return null;
		}
		String raw = durationNode.asText();
		if (raw == null || raw.isBlank() || raw.equalsIgnoreCase("N/A")) {
			return null;
		}
		try {
			double duration = Double.parseDouble(raw);
			if (duration <= 0) {
				return null;
			}
			return (int) Math.round(duration);
		} catch (NumberFormatException e) {
			log.warn("Unparseable duration value from ffprobe: '{}'", raw);
			return null;
		}
	}

	private Integer parseTagDuration(JsonNode tagsNode) {
		if (tagsNode == null || tagsNode.isMissingNode() || !tagsNode.has("DURATION")) {
			return null;
		}
		String raw = tagsNode.get("DURATION").asText();
		if (raw == null || raw.isBlank()) {
			return null;
		}
		try {
			String[] parts = raw.split(":");
			if (parts.length != 3) {
				return null;
			}
			int hours = Integer.parseInt(parts[0]);
			int minutes = Integer.parseInt(parts[1]);
			double seconds = Double.parseDouble(parts[2]);
			double totalSeconds = hours * 3600 + minutes * 60 + seconds;
			return totalSeconds > 0 ? (int) Math.round(totalSeconds) : null;
		} catch (Exception e) {
			log.warn("Unparseable DURATION tag from ffprobe: '{}'", raw);
			return null;
		}
	}
}