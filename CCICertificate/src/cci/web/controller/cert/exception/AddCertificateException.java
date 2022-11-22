package cci.web.controller.cert.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (HttpStatus.BAD_REQUEST)
public class AddCertificateException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public AddCertificateException(String err) {
		   super("Error certificate adding: " + err);
       }
}
