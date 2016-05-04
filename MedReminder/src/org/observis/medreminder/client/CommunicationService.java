package org.observis.medreminder.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("communication")
public interface CommunicationService extends RemoteService {
	String addPatient(String name,String phone) throws IllegalArgumentException;
	void addTemplate(String text,String weekDays,String time,String duration)throws IllegalArgumentException;
	String[] getTemplate(String text,String weekDays,String time,String duration)throws IllegalArgumentException;
	String getTemplateList()throws IllegalArgumentException;
	String getPatients()throws IllegalArgumentException;
}
