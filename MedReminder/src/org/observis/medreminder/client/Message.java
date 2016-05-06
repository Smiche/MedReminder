package org.observis.medreminder.client;

public class Message {
	public String title;
	public String text;
	public String time;
	public String day;
	
	public Message(String title, String text, String time, String day){
		this.title = title;
		this.text = text;
		this.time = time;
		this.day = day;
	}
	
}
