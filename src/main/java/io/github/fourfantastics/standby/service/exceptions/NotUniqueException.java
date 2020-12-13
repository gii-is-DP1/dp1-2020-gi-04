package io.github.fourfantastics.standby.service.exceptions;

import java.util.Set;

import lombok.Getter;

@Getter
public class NotUniqueException extends Exception {
	private static final long serialVersionUID = 8869846933485274617L;

	private Set<String> reasons;

	public NotUniqueException(String message) {
		super(message);
	}

	public NotUniqueException(String message, Set<String> reasons) {
		super(message);
		this.reasons = reasons;
	}
}
