package io.github.fourfantastics.standby.service.exception;

import java.util.Set;

import lombok.Getter;

@Getter
public class NotUniqueException extends Exception {
	private static final long serialVersionUID = 1L;

	private Set<String> reasons;

	public NotUniqueException(String message) {
		super(message);
	}

	public NotUniqueException(String message, Set<String> reasons) {
		super(message);
		this.reasons = reasons;
	}
}
