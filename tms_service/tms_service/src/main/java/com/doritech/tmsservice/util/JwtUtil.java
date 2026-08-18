package com.doritech.tmsservice.util;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	private SecretKey key;

	@PostConstruct
	public void init() {
		byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public Long getUserIdFromJwtToken(String token) {

		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

		Number userId = claims.get("userId", Number.class);

		return userId.longValue();
	}

	public String getUserNameFromJwtToken(String token) {

		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

		return claims.getSubject();
	}

	public Claims getAllClaimsFromToken(String token) {

		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	public boolean validateJwtToken(String token) {

		try {

			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);

			return true;

		} catch (SecurityException | MalformedJwtException e) {

			return false;

		} catch (ExpiredJwtException e) {

			return false;

		} catch (UnsupportedJwtException e) {

			return false;

		} catch (IllegalArgumentException e) {

			return false;
		}
	}
}