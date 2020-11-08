package io.github.fourfantastics.standby.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdviceHandler {
	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage invalidHttpMessageNotReadableHandler(Exception e) {
		return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), "Http request is malformed!");
	}
	
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	public ErrorMessage invalidHttpRequestMethodNotSupportedHandler(Exception e) {
		return new ErrorMessage(HttpStatus.METHOD_NOT_ALLOWED.value(), "Http request method is not allowed!");
	}
	
	@ExceptionHandler(InvalidShortFilmException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorMessage invalidShortFilmHandler(Exception e) {
		return new ErrorMessage(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}
	
	@ExceptionHandler(ShortFilmNotFoundException.class)
	@ResponseBody
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorMessage shortFilmNotFoundHandler(ShortFilmNotFoundException e) {
		return new ErrorMessage(HttpStatus.NOT_FOUND.value(), e.getMessage());
	}
}
