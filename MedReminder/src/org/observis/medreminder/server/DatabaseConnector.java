package org.observis.medreminder.server;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.observis.medreminder.client.Delivery;
import org.observis.medreminder.client.Message;

public class DatabaseConnector {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://192.168.0.100:3306/patients";
	static Connection conn = null;
	static Statement stmt = null;
	// Database credentials
	static final String USER = "root";
	static final String PASS = "rootroot";

	public static void openConnection() { // opens connection to the server
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			// System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			// System.out.println("Connected database successfully...");

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
		}

	}// end main

	public static void closeConnection() { // close connection to the server
		try {
			if (conn != null)
				conn.close();
				stmt = null;
			// System.out.println("Connection is closed");
		} catch (SQLException se) {
			se.printStackTrace();
		}

	}

	public static String returnPatient(String doctorName) { // returns patients
															// numbers for exact														// doctor name
		openConnection();
		ResultSet rs = null;
		String doctorID = "";
		String resultString = "";
		try {
			stmt = conn.createStatement();
			String doctorSql = "SELECT doctor_id FROM doctors WHERE username LIKE '" + doctorName + "'";
			// get doctor_id from doctors database to find patients numbers for
			// this exact doctor
			rs = stmt.executeQuery(doctorSql);
			while (rs.next()) {
				doctorID = rs.getString("doctor_id");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String getNumbers = "SELECT number FROM patients WHERE doctor_id LIKE '" + doctorID + "' ORDER BY number";

		try {
			rs = stmt.executeQuery(getNumbers);
			while (rs.next()) {
				if (resultString.length() < 2) {
					resultString += rs.getString("number");
				} else {
					resultString += "," + rs.getString("number");
				}
			}
			// System.out.println(resultString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
		return resultString;

	}

	public static void addPatientRecord(String name, String phone, String username) {
		ResultSet rs = null;
		openConnection();
		String doctorID = "";
		try {
			stmt = conn.createStatement();
			String getDoctorId = "SELECT doctor_id FROM doctors WHERE username LIKE '" + username + "'";
			// get doctor_id from doctors database to find patients numbers for
			// this exact doctor
			rs = stmt.executeQuery(getDoctorId);
			while (rs.next()) {
				doctorID = rs.getString("doctor_id");
			}
			// System.out.println(doctorID);
			String addPatientSQL = "INSERT INTO patients (doctor_id, number) VALUES ('" + doctorID + "','" + phone
					+ "')";
			// Using the same string variable to execute another SQL statement
			stmt.execute(addPatientSQL);
			// new patient record created
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		closeConnection();
		return;
	}

	public static boolean checkLogin(String username, String password) {
		openConnection();
		String result = "";
		ResultSet rs = null;
		String checkLoginSQL = "SELECT password FROM doctors WHERE username LIKE '" + username + "'";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(checkLoginSQL);
			if (rs == null) {
				return false;

			}
			while (rs.next()) {
				result = rs.getString("password");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();

		// System.out.println(MD5(password)+" "+result);
		if (result.equals(MD5(password))) {
			return true;
		} else {
			return false;
		}
	}

	public static String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}



	public static void insertSchedule(Message msg, String patientPhone) {
		openConnection();
		Date curDate = new Date();
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(curDate);
		curCal.add(Calendar.DATE, Integer.parseInt(msg.day) - 1);
		ResultSet rs;
		String patient_id = "";
		String sqlSelect = "SELECT patient_id FROM patients WHERE number LIKE '" + patientPhone + "'";
		String day = "" + curCal.get(Calendar.DAY_OF_MONTH);
		String month = "" + (curCal.get(Calendar.MONTH) + 1);
		String year = "" + curCal.get(Calendar.YEAR);
		String date = day + "-" + month + "-" + year;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlSelect);
			while (rs.next()) {
				patient_id = rs.getString("patient_id");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		String sqlInsert = "INSERT INTO `delivery`(patient_id, text, date, time, sent) VALUES ('" + patient_id
				+ "', '" + msg.text + "', '" + date + "', '" + msg.time + "', '0') ";
		
		try {
			stmt = conn.createStatement();
			stmt.execute(sqlInsert);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Insert into db delivery -> delivered -> false, phone -> patientPhone,
		// text -> msg.text time -> msg.time, patient_id -> from patient phone
		// with query
		// date -> cal.get(Calendar.DAY or DATE or DATE_OF_YEAR + YEAR..
		closeConnection();

	}

	public static String getPackagesDB() {
		openConnection();
		String packageList = "";
		String sqlQuery = "SELECT title FROM packages ORDER BY title";
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			while (rs.next()) {
				if (packageList.length() < 2) {
					packageList += rs.getString("title");
				} else {
					packageList += "," + rs.getString("title");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
		return packageList;

	}

	public static ArrayList<Message> getSinglePackage(String title) {
		openConnection();
		ArrayList<Message> messageList = new ArrayList<Message>();
		String sqlSelect = "SELECT messages.id, messages.title, messages.time, messages.day, messages.text FROM packages Left join messages ON packages.id = messages.package_id WHERE packages.title LIKE '"+title+"' ORDER BY day, time";
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlSelect);
			while (rs.next()) {
				messageList.add(new Message(rs.getString("title"),rs.getString("text"),rs.getString("time"),rs.getString("day")));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
		return messageList;
	}
	
	public static void addPackagetoDB(String title){
		openConnection();
		String sqlInsert = "INSERT INTO packages (title) VALUES ('"+title+"')";
		try {
			stmt = conn.createStatement();
			stmt.execute(sqlInsert);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
	
	}
	public static void addMessagetoDB(Message msg, String package_title){
		openConnection();
		String package_id = "";
		String sqlSelect = "SELECT id FROM packages WHERE title LIKE '"+package_title+"'";
		ResultSet rs = null;
	
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlSelect);
			while(rs.next()){
				package_id = rs.getString("id");
			}
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sqlInsert = "INSERT INTO messages(title,text,time,day,package_id) VALUES ('"+msg.title+"', '"+msg.text+"', '"+msg.time+"','"+msg.day+"','"+package_id+"')";
		try {
			stmt = conn.createStatement();
			stmt.execute(sqlInsert);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
	}
	public static void removeMessageDB (String title, String text){
		openConnection();
		String sqlRemove = "DELETE FROM messages WHERE title = '"+title+"' AND text = '"+text+"'";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sqlRemove);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
		}
	public static void removePackageDB (String title){
		openConnection();
		String sqlRemove = "DELETE FROM packages WHERE title = '"+title+"'";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sqlRemove);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
		
	}
	public static void updateMessageDB (String oldTitle, String newTitle, String newText, String newTime, String newDay){
		openConnection();
		String sqlUpdate = "UPDATE messages SET title='"+newTitle+"',text='"+newText+"', time ='"+newTime+"', day ='"+newDay+"' WHERE title LIKE '"+oldTitle+"'";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sqlUpdate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
	}
	
	public static void updatePackageDB (String oldTitle, String newTitle) {
		openConnection();
		String sqlUpdate = "UPDATE packages SET title='"+newTitle+"' WHERE title LIKE '"+oldTitle+"'";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sqlUpdate);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
		
	}
	public static ArrayList<Delivery> returnDeliveryDB(String phone){
		openConnection();
		ArrayList<Delivery> deliveryList = new ArrayList<Delivery>();
		String patient_id = "";
		String sqlGetid = "SELECT patient_id FROM patients WHERE number = '"+phone+"'";
		
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			stmt.execute(sqlGetid);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String sqlSelect = "SELECT  text, date, time, sent FROM delivery WHERE patient_id = '"+patient_id+"' SORT BY date, time";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlSelect);
			while(rs.next()){
				deliveryList.add(new Delivery(phone, rs.getString("text"),rs.getString("date"),rs.getString("time"),rs.getString("sent")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		closeConnection();
		return deliveryList;
	}
	
	public static void removePatientDB(String phone){
		openConnection();
		String sqlRemove = "DELETE FROM patients WHERE number = '"+phone+"'";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(sqlRemove);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
		
	}
}
