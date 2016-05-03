package org.observis.medreminder.server;
import java.sql.*;

public class DatabaseConnector {
	// JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://192.168.8.110:3306/patients";

	   //  Database credentials
	   static final String USER = "root";
	   static final String PASS = "rootroot";
	   
	   public static void main(String[] args) {
		   Connection conn = null;
		   Statement stmnt = null;
		   try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to a selected database...");
		      conn = DriverManager.getConnection(DB_URL, USER, PASS);
		      System.out.println("Connected database successfully...");
		      System.out.println("Insert Values");
		      stmnt = conn.createStatement();
		      String sql = "INSERT INTO templates " + "VALUES (2, 'SAMPLE TEXT')";
		     // stmnt.executeUpdate(sql);
		      System.out.println("done");
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   //System.out.println("Goodbye!");
		}//end main

}
