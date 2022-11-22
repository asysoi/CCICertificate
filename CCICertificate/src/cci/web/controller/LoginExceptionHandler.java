package cci.web.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

@Service("loginExceptionHandler")
public class LoginExceptionHandler implements AuthenticationFailureHandler{
	private static final Logger LOG=Logger.getLogger(LoginExceptionHandler.class);
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException ex)
			throws IOException, ServletException {
		LOG.info(ex.toString());
		String eMessage = "";
		
		if (ex instanceof SessionAuthenticationException) {
			eMessage = "Пользователь <b>" + req.getParameter("j_username") + "</b> уже вошел в систему ранее. Не допускается более одного открытого сеанса для пользователя.";
		} else if (ex instanceof BadCredentialsException) {	
			eMessage = "Имя пользователя или пароль не прошли проверку.";
		}
		req.getSession().setAttribute("eMessage", eMessage);
		res.sendRedirect("login.do");
	}
}
