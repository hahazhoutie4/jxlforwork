package com.zhoutong.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBtest {
	/**
	 *@author hahazhoutie4
	 *@website cnblogs.com/hahazhoutie4-blogs/
	 */
	private Connection connection;
	private void getConnection(){
		try {
			Class.forName("com.mysql.jdbc.Driver");
			 connection=DriverManager.getConnection("jdbc:mysql://localhost:3305/forwork","root","jintian123");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("fail to connect mysql");
		}
	}
	public static void main(String[] args) {
		System.out.println(" Â¥²ã"+" not like "+"'"+"_-%"+"'"+" and Â¥²ã"+" <>"+" '"+"»ù´¡²ã"+"'");
	}
}
