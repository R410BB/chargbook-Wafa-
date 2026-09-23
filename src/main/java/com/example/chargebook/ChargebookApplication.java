package com.example.chargebook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChargebookApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChargebookApplication.class, args);
	}
}
