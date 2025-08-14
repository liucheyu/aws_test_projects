package com.example.loginservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LoginserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoginserviceApplication.class, args);
	}

}
