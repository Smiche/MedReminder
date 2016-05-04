package org.observis.medreminder.server;

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

	public static void main() {
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
		// finally block used to close resources
		// try{
		// if(conn!=null)
		// conn.close();
		// }catch(SQLException se){
		// se.printStackTrace();
		// }//end finally try
		// }//end try
		// System.out.println("Goodbye!");
	}// end main

	public static String returnPatient(String doctorName) {
		main();
		ResultSet rs = null;
		String doctorID = "";
		String resultString = "";
		try {
			stmt = conn.createStatement();
			String doctorSql = "SELECT doctor_id FROM doctors WHERE username LIKE '" + doctorName + "'";
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

		return resultString;

	}

}
