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
		return null;
	}

	@Override
	public void addTemplate(String name, String day, String time)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

}
