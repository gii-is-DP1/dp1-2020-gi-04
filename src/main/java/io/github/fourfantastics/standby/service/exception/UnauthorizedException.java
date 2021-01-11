package io.github.fourfantastics.standby.service.exception;

import java.util.Set;

import lombok.Getter;

@Getter
public class UnauthorizedException extends Exception {

	private static final long serialVersionUID = 1L;

	private Set<String> reasons;

	public UnauthorizedException(String message) {
		super(message);
	}

	public UnauthorizedException(String message, Set<String> reasons) {
		super(message);
		this.reasons = reasons;
	}
}
