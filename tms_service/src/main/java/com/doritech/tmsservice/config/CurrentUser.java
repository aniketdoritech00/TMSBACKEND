package com.doritech.tmsservice.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class CurrentUser {

	private CurrentUser() {
	}

	public static Long getUserId() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {

			throw new IllegalStateException("No authenticated user found");
		}

		Object principal = authentication.getPrincipal();

		if (!(principal instanceof UserPrincipal)) {

			throw new IllegalStateException("Invalid authenticated principal");
		}

		return ((UserPrincipal) principal).getUserId();
	}

	public static String getUsername() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {

			throw new IllegalStateException("No authenticated user found");
		}

		Object principal = authentication.getPrincipal();

		if (!(principal instanceof UserPrincipal)) {

			throw new IllegalStateException("Invalid authenticated principal");
		}

		return ((UserPrincipal) principal).getUsername();
	}
}