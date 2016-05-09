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

import org.observis.medreminder.client.Message;

public class DatabaseConnector {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://192.168.0.107:3306/patients";
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
			// System.out.println("Connection is closed");
		} catch (SQLException se) {
			se.printStackTrace();
		}

	}

	public static String returnPatient(String doctorName) { // returns patients
															// numbers for exact
															// doctor name
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

		String getNumbers = "SELECT number FROM patients WHERE doctor_id LIKE '" + doctorID + "'";

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

	public static void addTemplateRecord(String text, String days, String time, String duration, String description) {
		// inserting a template record to the db
		openConnection();
		String sqlQuery = "INSERT INTO templates( `text`, `days`, `time`, `duration`, `template_descr`) VALUES ('"
				+ text + "','" + days + "','" + time + "','" + duration + "','" + description + "')";
		try {

			stmt = conn.createStatement();

			stmt.execute(sqlQuery);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();

	}

	public static String[] getTemplateRecord(String description) {
		openConnection();
		System.out.println("Description is:" + description);
		String sqlQuery = "SELECT * FROM templates WHERE template_desc LIKE '" + description + "'";
		String[] templateRecord = new String[5];
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			while (rs.next()) {
				templateRecord[0] = rs.getString("text");
				templateRecord[1] = rs.getString("days");
				templateRecord[2] = rs.getString("time");
				templateRecord[3] = rs.getString("duration");
				templateRecord[4] = rs.getString("template_desc");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
		return templateRecord;

	}

	public static String getTemplatesList() {
		openConnection();
		String templateList = "";
		String sqlQuery = "SELECT template_desc FROM templates";
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			while (rs.next()) {
				// System.out.println(rs.getString("template_desc"));
				if (templateList.length() < 2) {
					templateList += rs.getString("template_desc");
				} else {
					templateList += "," + rs.getString("template_desc");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		closeConnection();
		return templateList;
	}

	public static void insertSchedule(Message msg, String patientPhone) {
		openConnection();
		System.out.println("trying to insert a schedule to db");
		Date curDate = new Date();
		Calendar curCal = Calendar.getInstance();
		System.out.println("Date, calendar set, day is: "+msg.day);
		
		System.out.println("Day is: "+Integer.parseInt(msg.day));
		curCal.setTime(curDate);
		curCal.add(Calendar.DATE, Integer.parseInt(msg.day) - 1);
		System.out.println("Date acquired");
		ResultSet rs;
		String sqlSelect = "SELECT patient_id FROM patients WHERE number LIKE '" + patientPhone + "'";
		String patient_id = "";
		String day = "" + curCal.get(Calendar.DAY_OF_MONTH);
		String month = "" + (curCal.get(Calendar.MONTH) + 1);
		String year = "" + curCal.get(Calendar.YEAR);
		System.out.println("Current year is: "+curCal.get(Calendar.YEAR));
		String date = day + "-" + month + "-" + year;
		System.out.println("Attempting to get patient ID");
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
		
		System.out.println("Patient ID is: "+patient_id);
		String sqlInsert = "INSERT INTO `delivery`(patient_id, text, date, time, sent) VALUES ('" + patient_id
				+ "', '" + msg.text + "', '" + date + "', '" + msg.time + "', '0') ";
		
		try {
			stmt = conn.createStatement();
			stmt.execute(sqlInsert);
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Succesfully inserted?");
		// Insert into db delivery -> delivered -> false, phone -> patientPhone,
		// text -> msg.text time -> msg.time, patient_id -> from patient phone
		// with query
		// date -> cal.get(Calendar.DAY or DATE or DATE_OF_YEAR + YEAR..
		closeConnection();

	}

	public static String getPackagesDB() {
		openConnection();
		String packageList = "";
		String sqlQuery = "SELECT title FROM packages";
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
		String sqlSelect = "SELECT messages.id, messages.title, messages.time, messages.day, messages.text FROM packages Left join messages ON packages.id = messages.package_id WHERE packages.title LIKE '"+title+"'";
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
		String sqlSelect = "SELECT id FROM packages WHERE title LIKE '"+package_title+"'";
		ResultSet rs = null;
		String package_id = "";
		String sqlInsert = "INSERT INTO messages(title,text,time,day,package_id) WHERE package_id LIKE '"+package_id+"' VALUES ('"+msg.title+"', '"+msg.text+", '"+msg.time+"','"+msg.day+"','"+package_id+"')";
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
		try {
			stmt = conn.createStatement();
			stmt.execute(sqlInsert);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
	}
}
