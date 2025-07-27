package com.pirshayan.domain.model;

public abstract class GeneralException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final String code;
	private final String message;

	public GeneralException(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public abstract String getErrorDetails();
}
