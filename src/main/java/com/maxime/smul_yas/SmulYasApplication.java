package com.maxime.smul_yas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmulYasApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmulYasApplication.class, args);
	}

}
