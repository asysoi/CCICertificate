package cci.web.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import oracle.net.aso.e;



public class LogInterceptor implements HandlerInterceptor {
	private static final Logger LOG = Logger.getLogger(LogInterceptor.class);
	@Autowired
	private SessionRegistry sessionRegistry;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Authentication aut = SecurityContextHolder.getContext().getAuthentication();
		long startTime = System.currentTimeMillis();
		request.setAttribute("startTime", startTime);
		String action = request.getRequestURI().substring(request.getContextPath().length() + 1);
		LOG.info("Action: [" + action + "] " + request.getMethod() + " from [" + request.getRemoteAddr() + "] by [" + aut.getName() + "]");
		// LOG.info("Parameter Number: " + request.getParameter("number"));
		// LOG.info("HTTP Header:");
		
		Enumeration names = request.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement(); 
			LOG.info( name + " : " + request.getHeader(name));	
		}
		
		List<Object> principals = sessionRegistry.getAllPrincipals();
		for (Object principal : principals) {
			String username = (principal instanceof User) ? ((User) principal).getUsername() : principal.toString();
			LOG.info("Principal: " + username);
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		String action = request.getRequestURI().substring(request.getContextPath().length() + 1);
		long executeTime = System.currentTimeMillis() - (Long) request.getAttribute("startTime");
		LOG.info("ExecuteTime of action " + request.getMethod() + " [" + action + "]: " + executeTime + "ms");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

		// super.afterCompletion(request, response, handler, ex);
	}
}