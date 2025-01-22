package com.jeanbarcellos.project110;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class Project110Application {

	public static void main(String[] args) {
		SpringApplication.run(Project110Application.class, args);
	}

}
