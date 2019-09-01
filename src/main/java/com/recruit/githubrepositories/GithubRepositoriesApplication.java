package com.recruit.githubrepositories;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GithubRepositoriesApplication {

	public static void main(String[] args) {
		SpringApplication.run(GithubRepositoriesApplication.class, args);
	}

}
