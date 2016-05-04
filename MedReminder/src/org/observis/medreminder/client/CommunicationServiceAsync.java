package org.observis.medreminder.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommunicationServiceAsync {
	void addPatient(String input, String phone, AsyncCallback<String> callback) throws IllegalArgumentException;
	void addTemplate(String text,String weekDays,String time,String duration,String description, AsyncCallback<Void> callback)throws IllegalArgumentException;
	void getTemplate(String description, AsyncCallback<String[]> callback)throws IllegalArgumentException;
	void getTemplateList(AsyncCallback<String> callback) throws IllegalArgumentException;
	void getPatients(AsyncCallback<String> callback)throws IllegalArgumentException;
	
}
