package org.observis.medreminder.client;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = -5828108890651522661L;
	public String title = "";
	public String text = "";
	public String time = "";
	public String day = "";
	
	public Message(String title, String text, String time, String day){
		this.title = title;
		this.text = text;
		this.time = time;
		this.day = day;		
	}
	
	public Message(){
		
	}
	
}
