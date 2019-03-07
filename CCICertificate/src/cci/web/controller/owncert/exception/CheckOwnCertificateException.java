package cci.web.controller.owncert.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CheckOwnCertificateException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CheckOwnCertificateException(String err) {
		super(err);
	}
}
