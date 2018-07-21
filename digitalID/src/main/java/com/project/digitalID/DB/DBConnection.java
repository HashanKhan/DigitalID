package com.project.digitalID.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnection {
	public static void main(String [] args) {
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/cdap", "root", "");
			System.out.println("Connection Success");
			
			String query = "SELECT * FROM users";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				System.out.println("Name :"+ rs.getString("name")+ "Photo :"+ rs.getBlob("photo"));
			}
		}catch(Exception e) {
			System.err.println(e);
		}
	}
}
