package com.doritech.tmsservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.doritech.tmsservice.config.CurrentUser;

@RestController
@RequestMapping("/tmsService/api/test")
public class TestController {

	@GetMapping("/test")
	public ResponseEntity<?> test() {

		Long userId = CurrentUser.getUserId();
		System.out.println("UserId --->" + userId);
		return ResponseEntity.ok(userId);
	}
}