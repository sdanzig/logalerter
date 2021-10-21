package com.sdanzig.logalerter.common.dto;

public class Log {
	private Long id;
	private String description;
	private Long logType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getLogType() {
		return logType;
	}

	public void setLogType(Long logType) {
		this.logType = logType;
	}
}
