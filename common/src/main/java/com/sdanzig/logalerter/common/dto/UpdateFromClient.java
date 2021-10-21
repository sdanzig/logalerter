package com.sdanzig.logalerter.common.dto;

import java.util.ArrayList;
import java.util.List;

public class UpdateFromClient {
	private String userEmail;
	private List<NewAlertOccurrenceData> alertOccurrenceData;
	private Long latestAlertUpdateIdKnown;

	public UpdateFromClient() {
		alertOccurrenceData = new ArrayList<>();
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public List<NewAlertOccurrenceData> getAlertOccurrenceData() {
		return alertOccurrenceData;
	}

	public void setAlertOccurrenceData(List<NewAlertOccurrenceData> alertOccurrenceData) {
		this.alertOccurrenceData = alertOccurrenceData;
	}

	public Long getLatestAlertUpdateIdKnown() {
		return latestAlertUpdateIdKnown;
	}

	public void setLatestAlertUpdateIdKnown(Long latestAlertUpdateIdKnown) {
		this.latestAlertUpdateIdKnown = latestAlertUpdateIdKnown;
	}
}
