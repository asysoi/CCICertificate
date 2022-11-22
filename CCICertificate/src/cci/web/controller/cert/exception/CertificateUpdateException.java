package cci.web.controller.cert.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (HttpStatus.BAD_REQUEST)
public class CertificateUpdateException extends RuntimeException {

	private static final long serialVersionUID = 2L;

	public  CertificateUpdateException(String err) {
		super("Error certificate update: " + err);
       }

}

