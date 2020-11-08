package io.github.fourfantastics.standby.exception;

public class ErrorMessage {
	private Integer error;
	private String message;
	
	public ErrorMessage(Integer error, String message) {
		super();
		this.error = error;
		this.message = message;
	}
	
	public ErrorMessage() {
		
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((error == null) ? 0 : error.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ErrorMessage other = (ErrorMessage) obj;
		if (error == null) {
			if (other.error != null)
				return false;
		} else if (!error.equals(other.error))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ErrorMessage [error=" + error + ", message=" + message + "]";
	}
}
