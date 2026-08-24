package com.doritech.microservice.AuthenticationService.Service;

import com.doritech.microservice.AuthenticationService.Entity.UserMaster;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration:3600000}")
    private long expiration;

    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateTokenWithUserData(UserMaster user, String employeeName,Integer compId,Integer siteId)  {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("loginId", user.getLoginId());
        claims.put("roleId", user.getRole() != null ? user.getRole().getRoleId() : null);
        claims.put("userType", user.getUserType());
        claims.put("employeeName", employeeName);
        claims.put("compId", compId);
        claims.put("siteId", siteId);
        

        return Jwts.builder()
                .claims(claims)
                .subject(user.getLoginId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}