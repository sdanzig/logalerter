package com.sdanzig.logalerter.client;

import org.apache.commons.io.input.*;
import org.slf4j.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

import com.sdanzig.logalerter.common.dto.Alert;
import com.sdanzig.logalerter.common.dto.NewAlertOccurrenceData;

/**
 * LogTailerListener responds to events sent from a tailer reporting new lines added to
 * a specified log file.
 *
 * NOTE: The synchronized keyword is used to protect outgoing newAlertOccurrenceData
 * populated by the listener. The client will periodically pull/clear the data, so this
 * data must be locked when written to, and when pulled from.
 *
 * If other data owned by this class needs to be protected in the future, the
 * synchronized methods should instead use locks specific to the resources.
 */
public class LogTailerListener extends TailerListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(LogTailerListener.class);
    private final LogAlerterClientController logAlerterClientController;
    private final long idOfLogToMonitor;
    private Map<Long, NewAlertOccurrenceData> newAlertOccurrenceData = new HashMap<>();

    public LogTailerListener(LogAlerterClientController logAlerterClientController, long idOfLogToMonitor) {
        this.logAlerterClientController = logAlerterClientController;
        this.idOfLogToMonitor = idOfLogToMonitor;
    }

    public void handle(String line) {
        // Must be thread-safe
        List<Alert> alerts = logAlerterClientController.getAlertsForLog(idOfLogToMonitor);
        for (Alert alert : alerts) {
            Matcher matcher = alert.getRegex().matcher(line);
            boolean matchFound = matcher.find();
            if (matchFound) {
                incrementOccurrencesOfAlert(alert.getId());
                log.info("Detected regex {} in line [{}]", alert.getRegex().toString(), line);
            }
        }
    }

    synchronized void incrementOccurrencesOfAlert(Long alertId) {
        NewAlertOccurrenceData occurrenceData = newAlertOccurrenceData.get(alertId);
        if(occurrenceData == null) {
            occurrenceData = new NewAlertOccurrenceData(alertId, idOfLogToMonitor);
            newAlertOccurrenceData.put(alertId, occurrenceData);
        }
        occurrenceData.setLastOccurrence(new Date());
        occurrenceData.incrementNewOccurrences(1);
    }

    synchronized Map<Long, NewAlertOccurrenceData> pullAlertOccurrenceData() {
        Map<Long, NewAlertOccurrenceData> dataToSend = new HashMap<>(newAlertOccurrenceData);
        newAlertOccurrenceData.clear();
        return dataToSend;
    }
}