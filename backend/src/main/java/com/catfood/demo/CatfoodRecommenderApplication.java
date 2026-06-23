package com.catfood.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CatfoodRecommenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatfoodRecommenderApplication.class, args);
	}

}
