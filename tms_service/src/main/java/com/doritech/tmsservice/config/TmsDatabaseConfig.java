package com.doritech.tmsservice.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableJpaRepositories(basePackages = { "com.doritech.tmsservice.tms.repository",
		"com.doritech.tmsservice.service" }, entityManagerFactoryRef = "tmsEntityManagerFactory", transactionManagerRef = "tmsTransactionManager")
public class TmsDatabaseConfig {

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource tmsDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean tmsEntityManagerFactory(
			@Qualifier("tmsDataSource") DataSource dataSource) {

		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();

		emf.setDataSource(dataSource);

		// TMS entities
		emf.setPackagesToScan("com.doritech.tmsservice.tms.entity");

		emf.setPersistenceUnitName("tms");

		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

		emf.setJpaVendorAdapter(vendorAdapter);

		Map<String, Object> properties = new HashMap<>();

		properties.put("hibernate.hbm2ddl.auto", "update");

		properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

		properties.put("hibernate.format_sql", false);

		emf.setJpaPropertyMap(properties);

		return emf;
	}

	@Bean
	public PlatformTransactionManager tmsTransactionManager(
			@Qualifier("tmsEntityManagerFactory") EntityManagerFactory entityManagerFactory) {

		return new JpaTransactionManager(entityManagerFactory);
	}
}
