package com.sdanzig.logalerter.server.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sdanzig.logalerter.server.entities.AlertEntity;
import com.sdanzig.logalerter.server.entities.LogEntity;
import com.sdanzig.logalerter.server.exception.ResourceNotFoundException;
import com.sdanzig.logalerter.server.repository.AlertRepository;
import com.sdanzig.logalerter.server.repository.LogRepository;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class LogController {

	private final LogRepository logRepository;
	private final AlertRepository alertRepository;

	@Autowired
	public LogController(LogRepository logRepository, AlertRepository alertRepository) {
		this.logRepository = logRepository;
		this.alertRepository = alertRepository;
	}

	@GetMapping("/logs")
	public List<LogEntity> getLogs() {
		return logRepository.findAll();
	}

	@GetMapping("/logs/{logId}")
	public LogEntity getLog(@PathVariable Long logId) {
		return logRepository.findById(logId).orElseThrow(() ->
				new ResourceNotFoundException("LogEntity not found with ID specified."));
	}
//	public Page<LogEntity> getLogs(Pageable pageable) { return logRepository.findAll(pageable); }

	@PostMapping("/logs")
	public LogEntity createLog(@Valid @RequestBody LogEntity log) {
		return logRepository.save(log);
	}

	@PutMapping("/logs/{logId}")
	public LogEntity updateLog(@PathVariable Long logId,
			@Valid @RequestBody LogEntity logRequest) {
		return logRepository.findById(logId)
				.map(log -> {
					log.setDescription(logRequest.getDescription());
					log.setLogType(logRequest.getLogType());
					return logRepository.save(log);
				}).orElseThrow(() -> new ResourceNotFoundException("LogEntity not found with id " + logId));
	}

	@DeleteMapping("/logs/{logId}")
	public ResponseEntity<?> deleteLog(@PathVariable Long logId) {
		return logRepository.findById(logId)
				.map(log -> {
					logRepository.delete(log);
					return ResponseEntity.ok().build();
				}).orElseThrow(() -> new ResourceNotFoundException("LogEntity not found with id " + logId));
	}


	@GetMapping("/logs/{logId}/alerts")
	public List<AlertEntity> getAlertsByLogId(@PathVariable Long logId) {
		return alertRepository.findAllByLogId(logId);
	}
}