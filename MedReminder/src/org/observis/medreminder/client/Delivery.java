package org.observis.medreminder.client;

import java.io.Serializable;

public class Delivery implements Serializable{
	/**
	 * 
	 */
	public String patientPhone = "";
	public String text = "";
	public String date = "";
	public String time = "";
	public String sent =  "";
	private static final long serialVersionUID = 1000003333L;

	public Delivery(){
	}
	
	public Delivery(String patientPhone, String text, String date, String time, String sent){
		this.patientPhone = patientPhone;
		this.text = text;
		this.date = date;
		this.time = time;
		this.sent = sent;
	}
}
