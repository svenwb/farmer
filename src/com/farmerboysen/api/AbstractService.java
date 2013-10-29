package com.farmerboysen.api;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;

import com.farmerboysen.filter.AccessFilter;
import com.farmerboysen.models.PMF;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

public abstract class AbstractService {
	
	protected void isValidUser(User user) throws OAuthRequestException {
		if(user == null) {
			throw new OAuthRequestException("API access by invalid user");
		} else {
			try {
				AccessFilter.verifyEmail(user.getEmail());
			} catch (ServletException | IOException e) {
				throw new OAuthRequestException("No valid email to access Geese List");
			}
		}
	}
	
	protected PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
	
}
