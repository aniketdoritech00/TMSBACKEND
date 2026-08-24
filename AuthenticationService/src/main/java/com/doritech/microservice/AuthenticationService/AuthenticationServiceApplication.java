package com.doritech.microservice.AuthenticationService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AuthenticationServiceApplication {
	    public static void main(String[] args) {
	        ConfigurableApplicationContext context = SpringApplication.run(AuthenticationServiceApplication.class, args);
	        
	        String[] beans = context.getBeanNamesForType(
	            org.springframework.security.web.SecurityFilterChain.class
	        );
	        System.out.println("SecurityFilterChain beans found: " + beans.length);
	        for (String bean : beans) {
	            System.out.println("   - " + bean);
	        }
	    }
}
