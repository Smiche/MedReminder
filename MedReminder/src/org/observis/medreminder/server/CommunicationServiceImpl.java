package org.observis.medreminder.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.observis.medreminder.client.CommunicationService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CommunicationServiceImpl extends RemoteServiceServlet implements CommunicationService{

	/**
	 * 
	 */


	@Override
	public String addPatient(String name, String phone)
			throws IllegalArgumentException {
        HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
        HttpSession session = httpServletRequest.getSession(true);
        String username = (String) session.getAttribute("user");
		System.out.println("Username attempint to add:" +username);
		// TODO Auto-generated method stub
		DatabaseConnector.addPatientRecord(name, phone, username);
		//	
		
		return "Patient added:"+name+phone+username;
	}

	@Override
	// client adding template to server
	public void addTemplate(String text, String weekDays, String time,String duration, String description)
			throws IllegalArgumentException {
		DatabaseConnector.addTemplateRecord(text, weekDays, time, duration, description);
		// TODO Auto-generated method stub
		
	}

	@Override

	public String getPatients() throws IllegalArgumentException {
		
        HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
        HttpSession session = httpServletRequest.getSession(true);
        String doctorName = (String) session.getAttribute("user");
        //
        //get all patients from doctor id
		//
		return DatabaseConnector.returnPatient(doctorName);
	}

	@Override 
	//client getting template t
	public String[] getTemplate(String description) throws IllegalArgumentException {
		String[] template = new String[4];
		return DatabaseConnector.getTemplateRecord(description);
		//hook to db connector
		
		//return in format:
		//template[0] = template text
		//template[1] = "1,0,1,0,0,0,1" - 7 elements
		//template[2] = "00:00,01:00,02:12,00:12,...."
		//template[3] = duration
		//return template;
		
	}

	@Override
	public String getTemplateList() throws IllegalArgumentException {
		
		return DatabaseConnector.getTemplatesList();
		//hook to dbc
		
		//return in format "Templatename1,TemplateName2...."
		
	}

	@Override
	public String sendTask(String text, String weekdays, String times,
			String duration, String patientPhone)
			throws IllegalArgumentException {
		//Call scheduler class (or create instance?)
		Scheduler newScheduler = new Scheduler(text,weekdays,times,duration,patientPhone);	
		//handle db
		
		for(int i =0;i<newScheduler.getDeliveries().size();i++){
			DatabaseConnector.insertSchedules(newScheduler.getDeliveries().get(i));
		}
	
		//return the amount of added tasks
		return "Added "+newScheduler.getDeliveries().size()+" tasks.";
	}
	

}