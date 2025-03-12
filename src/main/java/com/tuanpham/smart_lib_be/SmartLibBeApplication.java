package com.tuanpham.smart_lib_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartLibBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartLibBeApplication.class, args);
	}

}
