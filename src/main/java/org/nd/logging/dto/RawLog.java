package org.nd.logging.dto;

public class RawLog {

	private String message;
	private String remoteAddress;

	public RawLog() {
		super();
	}

	public RawLog(String message, String remoteAddress) {
		super();
		this.message = message;
		this.remoteAddress = remoteAddress;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

}
