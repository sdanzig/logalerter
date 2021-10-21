package com.sdanzig.logalerter.server.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sdanzig.logalerter.common.dto.UpdateFromClient;
import com.sdanzig.logalerter.common.dto.UpdateFromServer;
import com.sdanzig.logalerter.server.AlertUpdateService;
import com.sdanzig.logalerter.server.entities.AlertEntity;
import com.sdanzig.logalerter.server.entities.AlertUpdateEntity;
import com.sdanzig.logalerter.server.exception.ResourceNotFoundException;
import com.sdanzig.logalerter.server.repository.AlertRepository;

/**
 * AlertController handles the REST API functionality directly relating to alerts.
 *
 * NOTE: Updating Alerts and AlertUpdates with the latest data from a client is
 * marked as Transactional so the alert updates will reflect the new client
 * data.
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class AlertController {
	Logger logger = LoggerFactory.getLogger(AlertController.class);

	private final AlertRepository alertRepository;
	private final AlertUpdateService alertUpdateService;

	@Autowired
	public AlertController(AlertRepository alertRepository, AlertUpdateService alertUpdateService) {
		this.alertRepository = alertRepository;
		this.alertUpdateService = alertUpdateService;
	}

	@GetMapping("/alerts/{alertId}")
	public AlertEntity getAlert(@PathVariable Long alertId) {
		return alertRepository.findById(alertId).orElseThrow(() ->
				new ResourceNotFoundException("AlertEntity not found with ID specified."));
	}

	@GetMapping("/alerts")
	public List<AlertEntity> getAlerts() {
		return alertRepository.findAll();
	}

	@PostMapping("/alerts")
	public UpdateFromServer syncAlertsWithClient(@RequestBody UpdateFromClient fromClient)
			throws ExecutionException, InterruptedException {
		CompletableFuture<UpdateFromServer> futureServerData =
				alertUpdateService.applyClientDataAndGetLatestServerData(fromClient,
						fromClient.getAlertOccurrenceData());
		CompletableFuture.allOf(futureServerData).join();
		UpdateFromServer serverData = futureServerData.get();
		return serverData;
	}

	@PostMapping("/alerts/{logId}")
	public AlertEntity addAlert(@PathVariable Long logId,
			@RequestParam("label") String label,
			@RequestParam("regex") String regex,
			@RequestParam("severity") int severity) {
		AlertEntity alert = new AlertEntity();
		alert.setLabel(label);
		alert.setRegex(regex);
		alert.setLogId(logId);
		alert.setSeverity(severity);
		AlertEntity createdAlert = alertRepository.save(alert);
		alertUpdateService.createAddAlertUpdate(logId, createdAlert);
		return createdAlert;
	}

	@PutMapping("/alerts/{alertId}")
	public AlertEntity updateAlert(@PathVariable Long alertId,
			@Valid @RequestBody AlertEntity alertRequest) {
		return alertRepository.findById(alertId)
				.map(alert -> {
					alert.setLabel(alertRequest.getLabel());
					alert.setRegex(alertRequest.getRegex());
					alert.setLogId(alertRequest.getLogId());
					alert.setSeverity(alertRequest.getSeverity());
					return alertRepository.save(alert);
				}).orElseThrow(() -> new ResourceNotFoundException("AlertEntity not found with id " + alertId));
	}

	@DeleteMapping("/alerts/{alertId}")
	public ResponseEntity<?> deleteAlert(@PathVariable Long alertId) {
		return alertRepository.findById(alertId)
				.map(alert -> {
					alertRepository.delete(alert);
					return ResponseEntity.ok().build();
				}).orElseThrow(() -> new ResourceNotFoundException("AlertEntity not found with id " + alertId));
	}
}