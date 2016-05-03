package org.observis.medreminder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface CommunicateServiceAsync {
	void addPatient(String input, AsyncCallback<String> callback) throws IllegalArgumentException;
}
