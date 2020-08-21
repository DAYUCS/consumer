package com.eximbills.consumer;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ConsumerApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerApplication.class);

	private final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");

	private static final String HOSTNAME = System.getenv().getOrDefault("HOSTNAME", "unknow").replace("\n", " - ")
			.trim();

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

	@PostMapping("/")
	public String consume(@RequestBody String cloudEvent) {
		JSONObject response = new JSONObject().put("cloudEvent", cloudEvent).put("host", HOSTNAME).put("time",
				SDF.format(new Date()));
		LOGGER.info("Event Message Received \n {}", response.toString());
		return response.toString();
	}

	@GetMapping("/healthz")
	public String health() {
		return "OK";
	}
}
