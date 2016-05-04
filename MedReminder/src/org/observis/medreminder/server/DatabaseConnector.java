package org.observis.medreminder.server;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class DatabaseConnector {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://192.168.8.101:3306/patients";
	static Connection conn = null;
	static Statement stmt = null;
	// Database credentials
	static final String USER = "root";
	static final String PASS = "rootroot";

	public static void openConnection() { //opens connection to the server
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to a selected database...");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connected database successfully...");

		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
		}
		
	}// end main
	public static void closeConnection(){ //close connection to the server
		try{
			if(conn!=null)
				conn.close();
			System.out.println("Connection is closed");
		}catch(SQLException se){
			se.printStackTrace();
		}
		
	}

	public static String returnPatient(String doctorName) { //returns patients numbers for exact doctor name
		openConnection();
		ResultSet rs = null;
		String doctorID = "";
		String resultString = "";
		try {
			stmt = conn.createStatement();
			String doctorSql = "SELECT doctor_id FROM doctors WHERE username LIKE '" + doctorName + "'";
			//get doctor_id from doctors database to find patients numbers for this exact doctor
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
			System.out.println(resultString);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
		return resultString;
		
	}
	
	public static void addPatientRecord(String name, String phone, String username){
		ResultSet rs = null;
		openConnection();
		String doctorID ="";
		try {
			stmt = conn.createStatement();
			String getDoctorId = "SELECT doctor_id FROM doctors WHERE username LIKE '" + username + "'";
			//get doctor_id from doctors database to find patients numbers for this exact doctor
			rs = stmt.executeQuery(getDoctorId);
			while (rs.next()) {
				doctorID = rs.getString("doctor_id");
			}
			System.out.println(doctorID);
			String addPatientSQL = "INSERT INTO patients (doctor_id, number) VALUES ('"+doctorID+"','"+phone+"')";
			//Using the same string variable to execute another SQL statement
			stmt.execute(addPatientSQL);
			//new patient record created
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		closeConnection();
		return;
	}
	
	public static boolean checkLogin(String username, String password){
		openConnection();
		String result = "";
		ResultSet rs = null;
		String checkLoginSQL = "SELECT password FROM doctors WHERE username LIKE '"+username+"'";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(checkLoginSQL);
			while (rs.next()) {
				result = rs.getString("password");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeConnection();
		
		byte[] bytesOfMessage = null;

		MessageDigest md =null;
		try {
			bytesOfMessage = result.getBytes("UTF-8");
			md = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] thedigest = md.digest(bytesOfMessage);
		String hash = new String(thedigest);
		if (result.equals(hash)){
			return true;
		}else{
			return false;
		}
	}

}
