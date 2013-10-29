package com.farmerboysen.api;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.farmerboysen.Constants;
import com.farmerboysen.models.Customer;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.users.User;

@Api(name = "geeselistapi", version = "v1", clientIds = { Constants.CLIENT_ID }, scopes = { "user.email" })
public class Cutomer extends AbstractService {
	public static ArrayList<Customer> greetings = new ArrayList<Customer>();

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "customer.list", path="customer")
	public List<Customer> getCustomers(User user) throws OAuthRequestException {
		isValidUser(user);

		PersistenceManager pm = getPersistenceManager();
		Query query = pm.newQuery(Customer.class);
		return (List<Customer>) pm.newQuery(query).execute(user);
	}
}
