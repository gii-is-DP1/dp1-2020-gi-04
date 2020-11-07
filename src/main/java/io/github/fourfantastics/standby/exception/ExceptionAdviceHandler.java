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
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage invalidHandler(Exception e) {
		return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}
	
	@ExceptionHandler(ShortFilmNotFoundException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage notFoundHandler(ShortFilmNotFoundException e) {
		return new ErrorMessage(HttpStatus.NOT_FOUND.value(), e.getMessage());
	}
}
