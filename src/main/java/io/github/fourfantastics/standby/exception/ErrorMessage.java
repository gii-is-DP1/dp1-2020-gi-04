package io.github.fourfantastics.standby.exception;

public class ErrorMessage {
	public ErrorMessage(Integer error, String message) {
		super();
		this.error = error;
		this.message = message;
	}
	public Integer getError() {
		return error;
	}
	public void setError(Integer error) {
		this.error = error;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	private Integer error;
	private String message;
}
