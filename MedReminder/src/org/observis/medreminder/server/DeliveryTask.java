package org.observis.medreminder.server;

public class DeliveryTask {
	String patient;
	String text;
	String date;
	String time;
	public DeliveryTask(String patient, String text, String date, String time){
		this.patient = patient;
		this.text = text;
		this.date = date;
		this.time = time;
	}
	
}
