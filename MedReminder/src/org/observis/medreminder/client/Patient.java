package org.observis.medreminder.client;

public class Patient {
	public String name;
	public String description;
	public String address;
	public String phoneNumber;
	
	public Patient(String name,String info,String address,String phoneNumber){
		this.name = name;
		this.description = info;
		this.address = address;
		this.phoneNumber = phoneNumber;
		
	}
}
