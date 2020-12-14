package io.github.fourfantastics.standby.service.exceptions;

import java.util.Set;

import lombok.Getter;

@Getter
public class NotFoundException extends Exception {
	private static final long serialVersionUID = 8869846933485274617L;

	private Set<String> reasons;

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String message, Set<String> reasons) {
		super(message);
		this.reasons = reasons;
	}
}
