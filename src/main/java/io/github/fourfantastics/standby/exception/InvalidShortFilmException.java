package io.github.fourfantastics.standby.exception;

public class InvalidShortFilmException extends RuntimeException {
	private static final long serialVersionUID = 3504578881327189780L;

	public InvalidShortFilmException() {
		super("Invalid short film");
	}
}
