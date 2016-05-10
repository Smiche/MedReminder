package org.observis.medreminder.client;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommunicationServiceAsync {
	void addPatient(String input, String phone, AsyncCallback<String> callback) throws IllegalArgumentException;
	void getPackage(String description, AsyncCallback<ArrayList<Message>> callback)throws IllegalArgumentException;
	void addMessage(Message msg,String packageName, AsyncCallback<Void> callback)throws IllegalArgumentException;
	void addPackage(String title, AsyncCallback<Void> callback)throws IllegalArgumentException;
	void removePackage(String title, AsyncCallback<Void> callback) throws IllegalArgumentException;
	void getPackagesList(AsyncCallback<String> callback) throws IllegalArgumentException;
	void getPatients(AsyncCallback<String> callback)throws IllegalArgumentException;
	void removePatient(String phone,AsyncCallback<Void> callback)throws IllegalArgumentException;
	void getDeliveries(String phone, AsyncCallback<ArrayList<Delivery>> callback)throws IllegalArgumentException;
	void scheduleMessages(ArrayList<Message> messages,String patientPhone, AsyncCallback<String> callback)throws IllegalArgumentException;
	void removeMessage(String title, String text, AsyncCallback<Void> callback)throws IllegalArgumentException;
}
