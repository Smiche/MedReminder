package org.observis.medreminder.server;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.observis.medreminder.client.CommunicationService;
import org.observis.medreminder.client.Message;

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
	public ArrayList<Message> getPackage(String description) throws IllegalArgumentException {
		String[] template = new String[4];
		
		//array list to return all messages
		return null;
		//hook to db connector
		
		//return in format:
		//template[0] = template text
		//template[1] = "1,0,1,0,0,0,1" - 7 elements
		//template[2] = "00:00,01:00,02:12,00:12,...."
		//template[3] = duration
		//return template;
		
	}

	@Override
	public String getPackagesList() throws IllegalArgumentException {
		
		//need to change to getPackagesList
		//return DatabaseConnector.getTemplatesList();
		//
		return null;
	}

	@Override
	public String sendTask(String text, String weekdays, String times,
			Date curDate, Date finalDate, String patientPhone)
			throws IllegalArgumentException {
		//Call scheduler class (or create instance?)
		Scheduler newScheduler = new Scheduler(text,weekdays,times,curDate,finalDate,patientPhone);	
		//handle db
		
		for(int i =0;i<newScheduler.getDeliveries().size();i++){
			//DatabaseConnector.insertSchedules(newScheduler.getDeliveries().get(i));
			System.out.println("Printing a task:");
			System.out.println(newScheduler.getDeliveries().get(i).date);
			System.out.print(" "+newScheduler.getDeliveries().get(i).text);
			System.out.print(" "+newScheduler.getDeliveries().get(i).patient);
			System.out.print(" "+newScheduler.getDeliveries().get(i).time);
			
		}
		
		
		//return the amount of added tasks
		return "Added "+newScheduler.getDeliveries().size()+" tasks.";
	}
	

}