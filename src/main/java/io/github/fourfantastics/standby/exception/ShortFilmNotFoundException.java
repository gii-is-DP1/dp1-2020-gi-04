package io.github.fourfantastics.standby.exception;

public class ShortFilmNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 7032225739416486941L;

	public ShortFilmNotFoundException(Long id) {
		super(String.format("Couldn't find short film with ID %d", id));
	}
}
