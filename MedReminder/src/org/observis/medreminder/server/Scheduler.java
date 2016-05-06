package org.observis.medreminder.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Scheduler {

	ArrayList<DeliveryTask> tasks = new ArrayList<DeliveryTask>();
	ArrayList<Boolean> weekDays = new ArrayList<Boolean>();
	
	public Scheduler(String text, String weekdays, String times,
			Date curDate, Date finalDate, String patientPhone){
		
		
		
		
		
		Calendar curCal = Calendar.getInstance();
		Calendar finalCal = Calendar.getInstance();
		
		curCal.setTime(curDate);
		finalCal.setTime(finalDate);
		
		int duration = 0;
		
		if(finalCal.get(Calendar.DAY_OF_YEAR) - curCal.get(Calendar.DAY_OF_YEAR) > 0){
		duration = finalCal.get(Calendar.DAY_OF_YEAR) - curCal.get(Calendar.DAY_OF_YEAR);
		} else {
			duration = (finalCal.get(Calendar.DAY_OF_YEAR)+curCal.getActualMaximum(Calendar.DAY_OF_YEAR)) - curCal.get(Calendar.DAY_OF_YEAR);
		}
		
		String[] timeArray = times.split(",");
		
		int dayCount = duration;
		
		String[] weekDaysString = weekdays.split(",");
		for(int i =0;i<7;i++){
			if(Integer.parseInt(weekDaysString[i]) == 1){
				weekDays.add(true);
			} else {
				weekDays.add(false);
			}
		}
		
		//swap days to match Calendar.DAY_OF_WEEK
		ArrayList<Boolean> temp = new ArrayList<Boolean>();
		temp.add(weekDays.get(6));
		for(int i = 0;i<6;i++){
			temp.add(weekDays.get(i));
		}
		weekDays = temp;
		
		Calendar dueCal = curCal;

		for(int i = 0;i<dayCount;i++){
			if(weekDays.get(dueCal.get(Calendar.DAY_OF_WEEK)-1)){
				System.out.println("Iterated day of week:"+dueCal.get(Calendar.DAY_OF_WEEK)+" iterated day of month: "+dueCal.get(Calendar.DAY_OF_MONTH));
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
