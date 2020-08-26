package com.eximbills.consumer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.cloudevents.CloudEvent;
import io.cloudevents.v03.AttributesImpl;
import io.cloudevents.v03.http.Unmarshallers;

@SpringBootApplication
@RestController
public class ConsumerApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

	@PostMapping("/")
	public ResponseEntity<Void> consume(@RequestHeader Map<String, Object> headers, @RequestBody String body) {
		LOGGER.info("Received request header: " + headers);
		LOGGER.info("Received request body: " + body);
		// This doesn't make sense. Knative tranfer CloudEvent to HTTP Request. Here we transfer HTTP Request to
		// CloudEvent again.
		try {
			Object contentTypeVal = headers.get("Content-Type");
			if (contentTypeVal == null
					|| MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(contentTypeVal.toString())) {
				@SuppressWarnings("rawtypes")
				CloudEvent<AttributesImpl, Map> cloudEvent = Unmarshallers.binary(Map.class).withHeaders(() -> headers)
						.withPayload(() -> body).unmarshal();
			    AttributesImpl attributesImpl = cloudEvent.getAttributes();
				LOGGER.info("Received CloudEvent: " + cloudEvent);
				LOGGER.info("ID: " + attributesImpl.getId());
				LOGGER.info("Spec Version: " + attributesImpl.getSpecversion());
				LOGGER.info("Subject: " + attributesImpl.getSubject());
				LOGGER.info("Time: " + attributesImpl.getTime());
				LOGGER.info("Source:" + attributesImpl.getSource());
				LOGGER.info("Type: " + attributesImpl.getType());
			}
			return ResponseEntity.accepted().build();
		} catch (Exception e) {
			String errMsg = "ERROR: Exception processing received event";
			LOGGER.error(errMsg, e);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errMsg, e);
		}
	}

	@GetMapping("/healthz")
	public String health() {
		return "OK";
	}
}
