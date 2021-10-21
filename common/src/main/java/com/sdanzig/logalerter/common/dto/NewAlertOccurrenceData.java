package com.sdanzig.logalerter.common.dto;

import java.util.Date;

public class NewAlertOccurrenceData {
	private long alertId;
	private int newOccurrences;
	private Date lastOccurrence;

	private long logId;

	public NewAlertOccurrenceData() { }

	public NewAlertOccurrenceData(long alertId, long logId) {
		this.alertId = alertId;
		this.logId = logId;
	}

	public long getAlertId() {
		return alertId;
	}

	public int getNewOccurrences() {
		return newOccurrences;
	}

	public void setNewOccurrences(int newOccurrences) {
		this.newOccurrences = newOccurrences;
	}

	public void incrementNewOccurrences(int newOccurrencesToAdd) {
		this.newOccurrences += newOccurrencesToAdd;
	}

	public Date getLastOccurrence() {
		return lastOccurrence;
	}

	public void setLastOccurrence(Date lastOccurrence) {
		this.lastOccurrence = lastOccurrence;
	}

	public long getLogId() { return logId; }

	public void setLogId(long logId) { this.logId = logId; }
}
