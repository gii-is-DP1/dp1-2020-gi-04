package io.github.fourfantastics.standby.service.exceptions;

import java.util.Set;

import lombok.Getter;

@Getter
public class DataMismatchException extends Exception {
	private static final long serialVersionUID = 1L;

	private Set<String> reasons;

	public DataMismatchException(String message) {
		super(message);
	}

	public DataMismatchException(String message, Set<String> reasons) {
		super(message);
		this.reasons = reasons;
	}
}
