
//package com.doritech.api_gateway.util;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class JwtUtil {
//    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
//
//    @Value("${app.jwtSecret}")
//    private String jwtSecret;
//
//    private SecretKey key;
//
//    @PostConstruct
//    public void init() {
//        // Use the SAME secret as auth service
//        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//        System.out.println("API Gateway using same JWT Key. Length: " + keyBytes.length + " bytes");
//    }
//
//    public String getUserNameFromJwtToken(String token) {
//        try {
//            return Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody()
//                    .getSubject();
//        } catch (Exception e) {
//            logger.error("Error extracting username: {}", e.getMessage());
//            return null;
//        }
//    }
//
//    public boolean validateJwtToken(String authToken) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
//            return true;
//        } catch (SecurityException | MalformedJwtException e) {
//            logger.error("Invalid JWT signature: {}", e.getMessage());
//        } catch (ExpiredJwtException e) {
//            logger.error("JWT token is expired: {}", e.getMessage());
//        } catch (UnsupportedJwtException e) {
//            logger.error("JWT token is unsupported: {}", e.getMessage());
//        } catch (IllegalArgumentException e) {
//            logger.error("JWT claims string is empty: {}", e.getMessage());
//        }
//        return false;
//    }
//}

package com.doritech.api_gateway.util;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	private SecretKey key;

	@PostConstruct
	public void init() {
		byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		System.out.println("API Gateway using same JWT Key. Length: " + keyBytes.length + " bytes");
	}

	public String getUserNameFromJwtToken(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
		} catch (Exception e) {
			logger.error("Error extracting username: {}", e.getMessage());
			return null;
		}
	}

	public Long getUserIdFromJwtToken(String token) {
		try {
			Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

			Object userIdObj = claims.get("userId");
			if (userIdObj instanceof Integer) {
				return ((Integer) userIdObj).longValue();
			} else if (userIdObj instanceof Long) {
				return (Long) userIdObj;
			}
			return null;
		} catch (Exception e) {
			logger.error("Error extracting userId: {}", e.getMessage());
			return null;
		}
	}

	public Claims getAllClaimsFromToken(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		} catch (Exception e) {
			logger.error("Error extracting claims: {}", e.getMessage());
			return null;
		}
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}
}