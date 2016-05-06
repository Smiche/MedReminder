package org.observis.medreminder.client;

import java.util.Date;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("communication")
public interface CommunicationService extends RemoteService {
	String addPatient(String name,String phone) throws IllegalArgumentException;
	void addTemplate(String text,String weekDays,String time,String duration,String description)throws IllegalArgumentException;
	String[] getTemplate(String description)throws IllegalArgumentException;
	String getTemplateList()throws IllegalArgumentException;
	String getPatients()throws IllegalArgumentException;
	String sendTask(String text, String weekdays,String times, Date curDate,Date finalDate,String patientPhone)throws IllegalArgumentException;
}
