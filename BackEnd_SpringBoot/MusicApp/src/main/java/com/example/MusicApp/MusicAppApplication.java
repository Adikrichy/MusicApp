package com.example.MusicApp;

import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication //(scanBasePackages = "com.example.MusicApp")
@EnableScheduling
public class MusicAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicAppApplication.class, args);
		
	}

}
