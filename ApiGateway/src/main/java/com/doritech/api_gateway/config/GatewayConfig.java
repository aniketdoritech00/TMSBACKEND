//package com.doritech.api_gateway.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.doritech.api_gateway.filter.JwtAuthenticationFilter;
//
//@Configuration
//public class GatewayConfig {
//
//	@Autowired
//	private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//	@Bean
//	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//		return builder.routes()
//				.route("AuthenticationService", r -> r.path("/auth/**")
//						.filters(f -> f.rewritePath("/auth/(?<segment>.*)",
//								"/AuthenticationService/auth/${segment}"))
//						.uri("http://localhost:8001"))
//
//				.route("EmployeeService",
//						r -> r.path("/employee/api/**").filters(f -> f
//								.filter(jwtAuthenticationFilter.apply(
//										new JwtAuthenticationFilter.Config()))
//								.rewritePath("/employee/api/(?<segment>.*)",
//										"/EmployeeService/employee/api/${segment}"))
//								.uri("http://localhost:8001"))
//
//				.route("CustomerService",
//						r -> r.path("/customer/api/**").filters(f -> f
//								.filter(jwtAuthenticationFilter.apply(
//										new JwtAuthenticationFilter.Config()))
//								.rewritePath("/customer/api/(?<segment>.*)",
//										"/CustomerService/customer/api/${segment}"))
//								.uri("http://localhost:8001"))
//
//				.route("ItemService",
//						r -> r.path("/item/api/**").filters(f -> f
//								.filter(jwtAuthenticationFilter.apply(
//										new JwtAuthenticationFilter.Config()))
//								.rewritePath("/item/api/(?<segment>.*)",
//										"/ItemService/item/api/${segment}"))
//								.uri("http://localhost:8001"))
//
//				.build();
//	}
//
//}
//
package com.doritech.api_gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.doritech.api_gateway.filter.JwtAuthenticationFilter;

@Configuration
public class GatewayConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("auth-service", r -> r.path("/auth/**")
						.filters(f -> f.rewritePath("/auth/(?<segment>.*)",
								"/auth-service/auth/${segment}"))
						.uri("http://localhost:9093"))

				.route("employee-service",
						r -> r.path("/employee/api/**").filters(f -> f
								.filter(jwtAuthenticationFilter.apply(
										new JwtAuthenticationFilter.Config()))
								.rewritePath("/employee/api/(?<segment>.*)",
										"/employee/api/${segment}"))
								.uri("http://localhost:9090"))

				.route("customer-service",
						r -> r.path("/customer/api/**").filters(f -> f
								.filter(jwtAuthenticationFilter.apply(
										new JwtAuthenticationFilter.Config()))
								.rewritePath("/customer/api/(?<segment>.*)",
										"/customer/api/${segment}"))
								.uri("http://localhost:9092"))

				.route("item-service",
						r -> r.path("/item/api/**").filters(f -> f
								.filter(jwtAuthenticationFilter.apply(
										new JwtAuthenticationFilter.Config()))
								.rewritePath("/item/api/(?<segment>.*)",
										"/item-service/item/api/${segment}"))
								.uri("http://localhost:8080"))
				.route("tms-service",
				        r -> r.path("/tmsService/api/**")
				                .filters(f -> f
				                        .filter(jwtAuthenticationFilter.apply(
				                                new JwtAuthenticationFilter.Config())))
				                .uri("http://localhost:9091"))

				.build();
	}

}