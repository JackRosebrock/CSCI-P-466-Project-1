package com.example.Project1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.komamitsu.spring.data.sqlite.EnableSqliteRepositories;

@SpringBootApplication
@EnableSqliteRepositories
public class Project1Application {

	public static void main(String[] args) {
		SpringApplication.run(Project1Application.class, args);
	}

}
