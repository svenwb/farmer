package com.farmerboysen.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AccessFilter implements Filter {

	private static final Logger log = Logger.getLogger(AccessFilter.class.getName());

	private void verify(User user) throws ServletException, IOException {
		if (user == null) {
			throw new ServletException("No access to Signavio SDK documentation.");
		}

		verifyEmail(user.getEmail());
	}

	public static void verifyEmail(String email) throws ServletException, IOException {
		if (email == null || email.trim().length() == 0) {
			throw new ServletException("No access to Geese List.");
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				AccessFilter.class.getResourceAsStream("./resources/access.secret")));
		email = email.trim().toLowerCase();
		String line;
		while ((line = br.readLine()) != null) {
			if (line != null && email != null
					&& (line.toLowerCase().equalsIgnoreCase(email) || isDomainWildcardMatch(line, email))) {
				log.log(Level.WARNING, "Match found");
				return;
			}
		}

		throw new ServletException("No access to Geese List.");
	}

	private static boolean isDomainWildcardMatch(String line, String email) {
		if (!line.startsWith("*@")) {
			return false;
		}

		return getDomain(line).equalsIgnoreCase(getDomain(email));

	}

	private static String getDomain(String email) {
		int i = email.lastIndexOf('@');
		return email.substring(i + 1);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException,
			ServletException {

		HttpServletRequest hsr = (HttpServletRequest) request;
		UserService us = UserServiceFactory.getUserService();
		/*
		 * if(hsr.getServletPath().startsWith("/_ah")) { filterChain.doFilter(request, response); return; }
		 */

		// if(hsr.getUserPrincipal(). != null) {
		// response.getWriter().write("success");
		// return;
		// }

		if (!us.isUserLoggedIn()) {
			response.getWriter().println(
					"<p>Please <a href=\"" + us.createLoginURL(hsr.getRequestURI())
							+ "\">sign in</a> with your Google account to access the Signavio SDk Documentation.</p>");
			return;
		}

		log.log(Level.WARNING, "user: " + us.getCurrentUser().getEmail());
		try {
			verify(us.getCurrentUser());
		} catch (Exception e) {
			log.log(Level.WARNING, e.getMessage(), e);
			throw new ServletException(e);
		}

		filterChain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}