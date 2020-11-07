package io.github.fourfantastics.standby;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class StandbyApplication {
	public static void main(String[] args) {
		SpringApplication.run(StandbyApplication.class, args);
	}
}
