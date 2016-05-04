package org.observis.medreminder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommunicationServiceAsync {
	void addPatient(String input, String phone, AsyncCallback<String> callback) throws IllegalArgumentException;
	void addTemplate(String name,String day,String time, AsyncCallback<Void> callback)throws IllegalArgumentException;
	void getPatients(AsyncCallback<String> callback)throws IllegalArgumentException;
	
}
