package com.my_hourly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyHourlyApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyHourlyApplication.class, args);
	}

}
