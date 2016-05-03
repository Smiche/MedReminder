package org.observis.medreminder.server;

import	java.sql.*;


public class DatabaseConnector {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
	static final String DB_URL = "jdbc:mysql://192.168.8.110//patiens";
	//url to database
	 //  Database credentials
	static final String USER = "root";
	static final String PASS = "rootroot";

	public static void main(String[] args) {
		Connection conn = null;
		try {
			//driver registration 
			Class.forName("com.mysql.jdbc.Driver");
			//opening a connection
			 System.out.println("Connecting to a selected database...");
		     conn = DriverManager.getConnection(DB_URL, USER, PASS);
		     System.out.println("Connected database successfully...");
		} catch(SQLException se){
			//Handle errors for JDBC
		      se.printStackTrace();
		} catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   } finally{
			   try{
				   if(conn!=null)
					   conn.close();
			   
			   }catch(SQLException se){
				   se.printStackTrace();
			   }//end finally try
		   }//end try
		System.out.println("it's done");
		

	}//end main

}//end
