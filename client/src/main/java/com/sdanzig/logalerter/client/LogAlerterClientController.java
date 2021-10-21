package com.sdanzig.logalerter.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import org.apache.commons.io.input.Tailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.sdanzig.logalerter.common.dto.Alert;
import com.sdanzig.logalerter.common.dto.AlertUpdate;
import com.sdanzig.logalerter.common.dto.Log;
import com.sdanzig.logalerter.common.dto.NewAlertOccurrenceData;
import com.sdanzig.logalerter.common.dto.UpdateFromClient;
import com.sdanzig.logalerter.common.dto.UpdateFromServer;

@Component
public class LogAlerterClientController implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(LogAlerterClientController.class);

	volatile boolean keepRunning = true;

	private static final long SERVER_UPDATE_DELAY_IN_MILLIS = 5000;

	/**
	 * Gets info for all logs used by the client. The current implementation is
	 * thread-safe because the configuration which dictates logs available is only
	 * read in at startup.
	 * @return a map of log IDs to Log objects
	 */
	public Map<Long, Log> getLogInfo() {
		Map<Long, Log> logInfo = new HashMap<>();
		ResponseEntity<Log[]> response = restTemplate.getForEntity(
				serverUrl+"/logs", Log[].class);
		Log[] logArray = response.getBody();
		List<Log> logs = Arrays.asList(logArray);
		for(Log retrievedLog : logs) {
			if (logIdToAlertsMap.containsKey(retrievedLog.getId())) {
				logInfo.put(retrievedLog.getId(), retrievedLog);
			}
		}
		return logInfo;
	}

	/**
	 * logIdToAlertsMap is a map that tracks all alerts for each log. TailerListeners will use this
	 * information asynchronously when scanning logs.
	 *
	 * Keys are log IDs. Values are maps of alert ID to alert, rather than a simple list of alerts,
	 * for easy lookup of alerts by ID.
	 *
	 * NOTE: The synchronized keyword is used to protect logIdToAlertsMap which contains
	 * alert data received by the client from the server.  The tailer listeners will periodically
	 * poll for the alerts for their specific logs, doing a deep copy of them, and must lock
	 * the map while doing so.
	 *
	 * If other data owned by this class needs to be protected in the future, the
	 * synchronized methods should instead use locks specific to the resources.
	 */
	private Map<Long, Map<Long, Alert>> logIdToAlertsMap = new HashMap<>();

	/**
	 * A list of listeners that receive events from tailing threads, each scanning one log listed
	 * in the config file.
	 */
	private List<LogTailerListener> tailers = new Vector<>();

	/**
	 * latestAlertUpdateIdKnown is the last alert update received from the server.
	 * The server will return a list of alert updates with higher IDs.
	 * The initial value is 0.
	 */
	private Long latestAlertUpdateIdKnown = 0L;

	private static final long MILLIS_BETWEEN_LOG_CHECKS = 1000L;

	@Autowired
	private RestTemplate restTemplate;

	private String serverUrl;
	private String emailAddress;
	private Map<File, Long> filesToLogIdMap;
	private List<AlertUpdate> pendingUpdates = null;

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setFilesToLogIdMap(Map<File, Long> filesToLogIdMap) {
		this.filesToLogIdMap = filesToLogIdMap;
	}

	public void startThread() {
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		startTailingAllLogs();
		Map<Long, NewAlertOccurrenceData> updatesToSend = new HashMap<>();
		delayForNextUpdate();
		while(keepRunning) {
			log.debug("keepRunning = {}", keepRunning);
			for(LogTailerListener tailer : tailers) {
				Map<Long, NewAlertOccurrenceData> occurrenceDataFromLogTailerForAllAlerts = tailer.pullAlertOccurrenceData();
				for(Long alertId : occurrenceDataFromLogTailerForAllAlerts.keySet()) {
					NewAlertOccurrenceData updatesToSendForAlert = updatesToSend.get(alertId);
					NewAlertOccurrenceData occurrenceDataFromLogTailerForOneAlert = occurrenceDataFromLogTailerForAllAlerts.get(alertId);
					if(updatesToSendForAlert == null) {
						updatesToSendForAlert = new NewAlertOccurrenceData(alertId, occurrenceDataFromLogTailerForOneAlert.getLogId());
						updatesToSend.put(alertId, updatesToSendForAlert);
						updatesToSendForAlert.setNewOccurrences(occurrenceDataFromLogTailerForOneAlert.getNewOccurrences());
						updatesToSendForAlert.setLastOccurrence(occurrenceDataFromLogTailerForOneAlert.getLastOccurrence());
					} else {
						updatesToSendForAlert.incrementNewOccurrences(
								occurrenceDataFromLogTailerForOneAlert.getNewOccurrences());
						updateLastOccurrenceIfNewer(updatesToSendForAlert, occurrenceDataFromLogTailerForOneAlert);
					}
				}
			}
			UpdateFromClient update = new UpdateFromClient();
			update.setUserEmail(emailAddress);
			update.setAlertOccurrenceData(new ArrayList<>(updatesToSend.values()));
			update.setLatestAlertUpdateIdKnown(latestAlertUpdateIdKnown);
			ResponseEntity<UpdateFromServer> updateFromServer = restTemplate.postForEntity(
					serverUrl+"/alerts",
					update,
					UpdateFromServer.class
			);
			processAlertUpdates(updateFromServer.getBody().getAlertUpdates());
			updatesToSend.clear();
			delayForNextUpdate();
		}
		log.info("Application exiting.");
		System.exit(0);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	private void delayForNextUpdate() {
		try {
			Thread.sleep(SERVER_UPDATE_DELAY_IN_MILLIS);
		}
		catch (InterruptedException e) {
			log.warn("LogAlerter client thread interrupted.", e);
			keepRunning = false;
		}
	}

	private void startTailingAllLogs() {
		for(File file : filesToLogIdMap.keySet()) {
			Long logId = filesToLogIdMap.get(file);
			if(!logIdToAlertsMap.containsKey(logId)) {
				logIdToAlertsMap.put(logId, new HashMap<>());
			}
			LogTailerListener listener = new LogTailerListener(this, logId);
			Tailer tailer = Tailer.create(file, listener, MILLIS_BETWEEN_LOG_CHECKS, true, true);
			tailers.add(listener);
			log.info("Started tailing log file {}, mapped to log ID {}", file.getAbsolutePath(), logId);
		}
	}

	private void initializeLogToAlertMapWithDataFromServer(List<Alert> retrievedAlerts) {
		for(Alert alert : retrievedAlerts) {
			long alertId = alert.getId();
			long logId = alert.getLogId();
			Map<Long, Alert> alertsForLog = logIdToAlertsMap.get(logId);
			if(alertsForLog == null) {
				alertsForLog = new HashMap<>();
				logIdToAlertsMap.put(logId, alertsForLog);
			}
			alertsForLog.put(alertId, alert);
			log.info(alert.toString());
		}
	}

	private void updateLastOccurrenceIfNewer(NewAlertOccurrenceData dataToMaybeUpdate,
			NewAlertOccurrenceData newData) {
		Date latestSoFar = dataToMaybeUpdate.getLastOccurrence();
		Date latestFromNewData = newData.getLastOccurrence();
		dataToMaybeUpdate.setLastOccurrence(maxDate(latestSoFar, latestFromNewData));
	}

	/**
	 * Returns the maximum of two Dates
	 */
	private Date maxDate(Date first, Date second) {
		if(first.compareTo(second) == 1) {
			return first;
		}
		return second;
	}

	public synchronized void processAlertUpdates(List<AlertUpdate> alertUpdates) {
		if(alertUpdates.isEmpty()) {
			return;
		}
		for(AlertUpdate update : alertUpdates) {
			long updateAlertLogId = update.getAlertLogId();
			Map<Long, Alert> alertsForLog = logIdToAlertsMap.get(updateAlertLogId);
			if(alertsForLog == null) {
				// This client is not monitoring a log using this ID. Skip.
				continue;
			}
			switch(update.getChangeType()) {
			case ADD: {
				Alert newAlert = new Alert();
				newAlert.setId(update.getAlertId());
				newAlert.setOccurrences(update.getNewOccurrences());
				newAlert.setLastOccurrence(update.getLastOccurrence());
				newAlert.setLogId(updateAlertLogId);
				newAlert.setLabel(update.getNewAlertLabel());
				newAlert.setRegex(Pattern.compile(update.getNewAlertRegex()));
				newAlert.setSeverity(update.getNewAlertSeverity());
				// Needs thread safety
				alertsForLog.put(update.getAlertId(), newAlert);
				break;
			}
			case UPDATE: {
				Alert alertToUpdate = alertsForLog.get(update.getAlertId());
				if(alertToUpdate != null) {
					alertToUpdate.setOccurrences(alertToUpdate.getOccurrences() + update.getNewOccurrences());
					alertToUpdate.setLastOccurrence(update.getLastOccurrence());
				} else {
					log.error("Update received for unknown alert ID {}", updateAlertLogId);
				}
				break;
			}
			case REMOVE: {
				Alert alertToRemove = alertsForLog.get(updateAlertLogId);
				if(alertToRemove != null) {
					// Needs thread safety
					alertsForLog.remove(updateAlertLogId);
				} else {
					log.error("Remove update received for unknown alert ID {}", updateAlertLogId);
				}
				break;
			}
			default: {
				log.error("Alert update has unknown type, {}", update.getChangeType());
			}
			}
			latestAlertUpdateIdKnown = Math.max(latestAlertUpdateIdKnown, update.getAlertUpdateId());
		}
		AlertUpdate lastAlertUpdate = alertUpdates.get(alertUpdates.size() - 1);

	}

	public synchronized List<Alert> getAlertsForLog(long logId) {
		List<Alert> copyOfAlerts = new ArrayList<>();
		for(Alert alert : logIdToAlertsMap.get(logId).values()) {
			copyOfAlerts.add(new Alert(alert));
		}
		return copyOfAlerts;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void shutdownThread() {
		keepRunning = false;
	}

	public String getServerUrl() {
		return serverUrl;
	}
}
