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
		
		//	
		
		return "Patient added:"+name+phone;
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
        try{
        	DatabaseConnector.returnPatient("doctorR");
        } catch(Exception e){
        	e.printStackTrace();
        }
        //get all patients from doctor id
		//
		return "kkkkk,kkkkk,kk,kkk";
	}
	

}
