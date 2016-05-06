package org.observis.medreminder.server;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.observis.medreminder.client.CommunicationService;
import org.observis.medreminder.client.Message;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CommunicationServiceImpl extends RemoteServiceServlet implements
		CommunicationService {

	/**
	 * 
	 */

	@Override
	public String addPatient(String name, String phone)
			throws IllegalArgumentException {
		HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
		HttpSession session = httpServletRequest.getSession(true);
		String username = (String) session.getAttribute("user");
		System.out.println("Username attempint to add:" + username);
		// TODO Auto-generated method stub
		DatabaseConnector.addPatientRecord(name, phone, username);
		//

		return "Patient added:" + name + phone + username;
	}

	@Override
	// client adding template to server
	public void addTemplate(String text, String weekDays, String time,
			String duration, String description)
			throws IllegalArgumentException {
		DatabaseConnector.addTemplateRecord(text, weekDays, time, duration,
				description);
		// TODO Auto-generated method stub

	}

	@Override
	public String getPatients() throws IllegalArgumentException {

		HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
		HttpSession session = httpServletRequest.getSession(true);
		String doctorName = (String) session.getAttribute("user");
		//
		// get all patients from doctor id
		//
		return DatabaseConnector.returnPatient(doctorName);
	}

	@Override
	// client getting template t
	public ArrayList<Message> getPackage(String description)
			throws IllegalArgumentException {

		// array list to return all messages
		return null;
		// hook to db connector

		// return in format:
		// template[0] = template text
		// template[1] = "1,0,1,0,0,0,1" - 7 elements
		// template[2] = "00:00,01:00,02:12,00:12,...."
		// template[3] = duration
		// return template;

	}

	@Override
	public String getPackagesList() throws IllegalArgumentException {

		// need to change to getPackagesList
		// return DatabaseConnector.getTemplatesList();
		//
		return null;
	}

	@Override
	public String scheduleMessages(ArrayList<Message> messages,
			String patientPhone) throws IllegalArgumentException {
		// TODO Auto-generated method stub

		// scheduling logic from the arraylist

		return null;
	}

}