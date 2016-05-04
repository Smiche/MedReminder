package org.observis.medreminder.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.observis.medreminder.client.CommunicationService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CommunicationServiceImpl extends RemoteServiceServlet implements CommunicationService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
		//hook to db connector
		
		//return in format:
		//template[0] = template text
		//template[1] = "1,0,1,0,0,0,1" - 7 elements
		//template[2] = "00:00,01:00,02:12,00:12,...."
		//template[3] = duration
		//return template;
		return null;
	}

	@Override
	public String getTemplateList() throws IllegalArgumentException {
		
		
		//hook to dbc
		
		//return in format "Templatename1,TemplateName2...."
		return null;
	}
	

}