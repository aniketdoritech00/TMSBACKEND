package com.doritech.api_gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.doritech.api_gateway.util.JwtUtil;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter
		extends
			AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

	@Autowired
	private JwtUtil jwtUtil;

	public JwtAuthenticationFilter() {
		super(Config.class);
	}

	@Override
	public GatewayFilter apply(Config config) {

		return (exchange, chain) -> {

			ServerHttpRequest request = exchange.getRequest();
			String path = request.getURI().getPath();

			System.out.println("Processing request: " + path);

			if (request.getMethod() == HttpMethod.OPTIONS) {
				return chain.filter(exchange);
			}

			if (path.startsWith("/auth/") || path.contains("/actuator/health")
					|| path.contains("/actuator/info")) {

				System.out.println(
						"Skipping authentication for public endpoint: " + path);
				return chain.filter(exchange);
			}

			if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "No Authorization header",
						HttpStatus.UNAUTHORIZED);
			}

			String authHeader = request.getHeaders()
					.getFirst(HttpHeaders.AUTHORIZATION);
			String token = null;

			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				token = authHeader.substring(7);
			} else {
				return onError(exchange, "Invalid Authorization header format",
						HttpStatus.UNAUTHORIZED);
			}

			if (token == null || token.isBlank()) {
				return onError(exchange, "Token is empty",
						HttpStatus.UNAUTHORIZED);
			}

			if (!jwtUtil.validateJwtToken(token)) {
				return onError(exchange, "Invalid or expired JWT token",
						HttpStatus.UNAUTHORIZED);
			}

			String username = jwtUtil.getUserNameFromJwtToken(token);
			Long userId = jwtUtil.getUserIdFromJwtToken(token);

			if (username == null || userId == null) {
				return onError(exchange, "Invalid token payload",
						HttpStatus.UNAUTHORIZED);
			}

			ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
					.header("X-User-Id", String.valueOf(userId))
					.header("X-Username", username)
					.header("X-Authenticated", "true").build();

			return chain
					.filter(exchange.mutate().request(modifiedRequest).build());
		};
	}

	private Mono<Void> onError(ServerWebExchange exchange, String error,
			HttpStatus status) {

		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(status);

		response.getHeaders().add("Content-Type", "application/json");

		String errorBody = String.format("{\"error\": \"%s\", \"status\": %d}",
				error, status.value());

		return response.writeWith(
				Mono.just(response.bufferFactory().wrap(errorBody.getBytes())));
	}

	public static class Config {
	}
}