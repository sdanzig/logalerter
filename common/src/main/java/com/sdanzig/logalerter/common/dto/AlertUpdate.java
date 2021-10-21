package com.sdanzig.logalerter.common.dto;

import java.util.Date;

public class AlertUpdate {
	public enum AlertChange {
		ADD,
		UPDATE,
		REMOVE
	}

	private long alertUpdateId;
	private AlertChange changeType;
	private long alertId;
	private long newOccurrences;
	private Date lastOccurrence;
	private long alertLogId;
	private String newAlertLabel;
	private String newAlertRegex;
	private long newAlertSeverity;

	public long getAlertUpdateId() {
		return alertUpdateId;
	}

	public void setAlertUpdateId(long alertUpdateId) {
		this.alertUpdateId = alertUpdateId;
	}

	public AlertChange getChangeType() {
		return changeType;
	}

	public void setChangeType(AlertChange changeType) {
		this.changeType = changeType;
	}

	public long getAlertId() {
		return alertId;
	}

	public void setAlertId(long alertId) {
		this.alertId = alertId;
	}

	public long getNewOccurrences() {
		return newOccurrences;
	}

	public void setNewOccurrences(long newOccurrences) {
		this.newOccurrences = newOccurrences;
	}

	public Date getLastOccurrence() {
		return lastOccurrence;
	}

	public void setLastOccurrence(Date lastOccurrence) {
		this.lastOccurrence = lastOccurrence;
	}

	public long getAlertLogId() {
		return alertLogId;
	}

	public void setAlertLogId(long alertLogId) {
		this.alertLogId = alertLogId;
	}

	public String getNewAlertLabel() {
		return newAlertLabel;
	}

	public void setNewAlertLabel(String newAlertLabel) {
		this.newAlertLabel = newAlertLabel;
	}

	public String getNewAlertRegex() {
		return newAlertRegex;
	}

	public void setNewAlertRegex(String newAlertRegex) {
		this.newAlertRegex = newAlertRegex;
	}

	public long getNewAlertSeverity() {
		return newAlertSeverity;
	}

	public void setNewAlertSeverity(long newAlertSeverity) {
		this.newAlertSeverity = newAlertSeverity;
	}
}