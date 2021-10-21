package com.sdanzig.logalerter.common.dto;

import java.util.List;

public class UpdateFromServer {
	private List<AlertUpdate> alertUpdates;

	public List<AlertUpdate> getAlertUpdates() {
		return alertUpdates;
	}

	public void setAlertUpdates(List<AlertUpdate> alertUpdates) {
		this.alertUpdates = alertUpdates;
	}
}
