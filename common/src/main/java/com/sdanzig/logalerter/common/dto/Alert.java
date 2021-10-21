package com.sdanzig.logalerter.common.dto;

import java.util.Date;
import java.util.regex.Pattern;

public class Alert {
	public static final int ALERT_IGNORABLE = 0;
	public static final int ALERT_WARNING = 1;
	public static final int ALERT_CRITICAL = 2;

	private long id;
	private String label;
	private Pattern regex;
	private Long logId;
	private long severity;
	private long occurrences;
	private Date lastOccurrence;
	private String lastUserEmail;

	public Alert() {
	}

	public Alert(Alert other) {
		this.id = other.id;
		this.label = other.label;
		this.regex = other.regex;
		this.logId = other.logId;
		this.severity = other.severity;
		this.occurrences = other.occurrences;
		this.lastOccurrence = other.lastOccurrence;
		this.lastUserEmail = other.lastUserEmail;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Pattern getRegex() {
		return regex;
	}

	public void setRegex(Pattern regex) {
		this.regex = regex;
	}

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public long getSeverity() {
		return severity;
	}

	public void setSeverity(long severity) {
		this.severity = severity;
	}

	public long getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(long occurrences) {
		this.occurrences = occurrences;
	}

	public Date getLastOccurrence() {
		return lastOccurrence;
	}

	public void setLastOccurrence(Date lastOccurrence) {
		this.lastOccurrence = lastOccurrence;
	}

	public String getLastUserEmail() {
		return lastUserEmail;
	}

	public void setLastUserEmail(String lastUserEmail) {
		this.lastUserEmail = lastUserEmail;
	}
}