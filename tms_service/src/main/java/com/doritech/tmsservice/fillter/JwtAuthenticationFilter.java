package com.doritech.tmsservice.fillter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.doritech.tmsservice.config.UserPrincipal;
import com.doritech.tmsservice.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (authorization == null || !authorization.startsWith("Bearer ")) {

			filterChain.doFilter(request, response);
			return;
		}

		String token = authorization.substring(7);

		try {

			if (!jwtUtil.validateJwtToken(token)) {
				filterChain.doFilter(request, response);
				return;
			}

			String username = jwtUtil.getUserNameFromJwtToken(token);

			Long userId = jwtUtil.getUserIdFromJwtToken(token);

			if (username != null && userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				UserPrincipal principal = new UserPrincipal(userId, username, null, Collections.emptyList());

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
						null, principal.getAuthorities());

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}

		} catch (Exception e) {
			SecurityContextHolder.clearContext();
		}

		filterChain.doFilter(request, response);
	}
}