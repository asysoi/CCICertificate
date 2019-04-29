package cci.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LogInterceptor implements HandlerInterceptor {
	private static final Logger LOG = Logger.getLogger(LogInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Authentication aut = SecurityContextHolder.getContext().getAuthentication();
		long startTime = System.currentTimeMillis();
		request.setAttribute("startTime", startTime);

		String action = request.getRequestURI().substring(request.getContextPath().length() + 1);
		LOG.info("Action Portal: [" + action + "] by [" + request.getRemoteAddr() + "]");
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		long executeTime = System.currentTimeMillis() - (Long) request.getAttribute("startTime");
		LOG.info("ExecuteTime of action: " + executeTime + "ms");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// super.afterCompletion(request, response, handler, ex);
	}

}