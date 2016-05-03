package org.observis.medreminder.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService{
	
	Integer getUserSessionTimeout();
	Boolean isSessionAlive();
	void ping();
	boolean checkLoggedIn() throws IllegalArgumentException;
	boolean logIn(String username, String password) throws IllegalArgumentException;
}
