package com.example.aresclientresttemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.web.client.RestTemplate;

@EnableHypermediaSupport(type=EnableHypermediaSupport.HypermediaType.HAL)
@SpringBootApplication
public class AresClientRestTemplateApplication {

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(AresClientRestTemplateApplication.class, args);
	}

}
