package com.connector.db;

import java.sql.*;

public class MySQLConnector {

	private String userid = "root";
	private String password = "hell";
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost:3306/openmrs197";

	private Connection con;
	// constructor
	public MySQLConnector() {
		getConnection(); //Create Connection to the MySQL Database
	}
	public Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (java.lang.ClassNotFoundException e) {
			System.err.print("ClassNotFoundException: ");
			System.err.println(e.getMessage());
		}
		try {
			con = DriverManager.getConnection(DB_URL, userid, password);
		} catch (SQLException ex) {
			System.err.println("MySQLException: " + ex.getMessage());
		}
		return con;
	}
	
	public String getQueryResult(String query) throws SQLException {
		
		Statement stmt = con.createStatement();
	    ResultSet rs = stmt.executeQuery(query);
	    rs.next();
	    Integer i = rs.getInt(1);
		return i.toString();
		
	}
	
	
}// end class 