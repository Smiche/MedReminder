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
		return DatabaseConnector.getSinglePackage(description);
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
		return DatabaseConnector.getPackagesDB();
	}

	@Override
	public String scheduleMessages(ArrayList<Message> messages,
			String patientPhone) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Array size: "+messages.size());
		int success = 0;
		for(Message m:messages){
			System.out.println("inserting message info: "+m.title+" phone is:"+patientPhone);
			DatabaseConnector.insertSchedule(m, patientPhone);
			success++;
		}
		// scheduling logic from the arraylist
		return ""+success;
	}

	@Override
	public void addPackage(String name) throws IllegalArgumentException {
		// add a new package to the database with that title ->name
		DatabaseConnector.addPackagetoDB(name);
		
	}

	@Override
	public void addMessage(Message msg, String packageName)
			throws IllegalArgumentException {
		DatabaseConnector.addMessagetoDB(msg, packageName);
		//add a new message to table messages
		//use packageName to get foreign  package_id
		
	}

	@Override
	public void removeMessage(String title, String text)
			throws IllegalArgumentException {
		DatabaseConnector.removeMessageDB(title, text);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePackage(String title) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		DatabaseConnector.removePackageDB(title);
	}

}