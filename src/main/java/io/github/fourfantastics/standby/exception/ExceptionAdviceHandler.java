package io.github.fourfantastics.standby.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdviceHandler {
	@ExceptionHandler(InvalidShortFilmException.class)
	@ResponseBody
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public String invalidHandler(Exception e) {
		return e.getMessage();
	}
	
	@ExceptionHandler(ShortFilmNotFoundException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String notFoundHandler(ShortFilmNotFoundException e) {
		return e.getMessage();
	}
}
