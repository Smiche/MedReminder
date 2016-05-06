package org.observis.medreminder.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Scheduler {

	ArrayList<DeliveryTask> tasks = new ArrayList<DeliveryTask>();
	ArrayList<Boolean> weekDays = new ArrayList<Boolean>();
	
	public Scheduler(String text, String weekdays, String times,
			String duration, String patientPhone){
		
		String[] timeArray = times.split(",");
		int dayCount = Integer.parseInt(duration);
		
		String[] weekDaysString = weekdays.split(",");
		for(int i =0;i<7;i++){
			if(Integer.parseInt(weekDaysString[i]) == 1){
				weekDays.add(true);
			} else {
				weekDays.add(false);
			}
		}
		
		Calendar dueCal = Calendar.getInstance();
		dueCal.setTime(new Date());
		
		
		for(int i = 0;i<dayCount;i++){
			if(weekDays.get(dueCal.get(Calendar.DAY_OF_WEEK)-1)){
				for(int j=0;j<timeArray.length;j++){
					//String[] singleTime = timeArray[j].split(":");
				    int year = dueCal.get(Calendar.YEAR);
				    int month = dueCal.get(Calendar.MONTH);
				    int day = dueCal.get(Calendar.DAY_OF_MONTH);
					//dueCal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(singleTime[0]));
					//dueCal.set(Calendar.MINUTE, Integer.parseInt(singleTime[1]));
					tasks.add(new DeliveryTask(patientPhone, text, day+"-"+month+"-"+year, timeArray[j]));
				}
			}	
			dueCal.add(Calendar.DATE, 1);
		}		
	}
	
	
	public ArrayList<DeliveryTask> getDeliveries(){
		return tasks;
	}
}
