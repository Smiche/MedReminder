package org.observis.medreminder.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.observis.medreminder.client.CommunicationService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CommunicationServiceImpl extends RemoteServiceServlet implements CommunicationService{

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
	public void addTemplate(String name, String day, String time)
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
	

}
