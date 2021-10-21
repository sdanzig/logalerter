package com.sdanzig.logalerter.server;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogAlerterMainRestController {

	@RequestMapping("/api")
	public String index() {
		return "Greetings from Spring Boot!";
	}

}