package io.github.fourfantastics.standby.service.exceptions;

import java.util.Set;

import lombok.Getter;

@Getter
public class TooBigException extends Exception {
	private static final long serialVersionUID = 1L;

	private Set<String> reasons;

	public TooBigException(String message) {
		super(message);
	}

	public TooBigException(String message, Set<String> reasons) {
		super(message);
		this.reasons = reasons;
	}
}
