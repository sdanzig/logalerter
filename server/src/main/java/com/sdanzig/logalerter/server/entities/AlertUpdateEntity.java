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
@Table(name = "tbl_alert_update")
public class AlertUpdateEntity {
	public enum AlertChange {
		ADD,
		UPDATE,
		REMOVE
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "change_type")
	private String changeType;

	@Column(name = "alert_id")
	private Long alertId;

	@Column(name = "new_occurrences", nullable = false)
	private long newOccurrences;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_occurrence")
	private Date lastOccurrence;

	@Column(name = "user_email")
	private String userEmail;

	@Column(name = "log_id")
	private Long logId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getChangeType() {
		return changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	public Long getAlertId() {
		return alertId;
	}

	public void setAlertId(Long alertId) {
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

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Long getLogId() {
		return logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}
}