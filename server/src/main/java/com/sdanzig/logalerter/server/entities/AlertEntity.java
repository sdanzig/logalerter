package com.sdanzig.logalerter.server.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "tbl_alert")
public class AlertEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "label", nullable = true)
	private String label;

	@Column(name = "regex", nullable = true)
	private String regex;

	@Column(name = "log_id", nullable = false)
	private long logId;

	@Column(name = "severity")
	private int severity;

	@Column(name = "occurrences", nullable = false)
	private int occurrences;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_occurrence")
	private Date lastOccurrence;

	@Column(name = "last_user_email")
	private String lastUserEmail;

	public long getId() { return id; }

	public void setId(long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public long getLogId() {
		return logId;
	}

	public void setLogId(long logId) {
		this.logId = logId;
	}

	public int getSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public int getOccurrences() {
		return occurrences;
	}

	public void setOccurrences(int occurrences) { this.occurrences = occurrences; }

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