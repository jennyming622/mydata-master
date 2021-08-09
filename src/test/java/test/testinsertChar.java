package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class testinsertChar {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
        String sql = "";
        String sql1= "";
        String sql2= "";
        Statement stmt = null;
        int rs = 0;
        int personal_total = 0;
        int great_total = 0;
        int total = 0;
		Properties props = new Properties();
	    props.put("user", "postgres");
	    props.put("password", "riease1qaz");
		try {
			Class.forName("org.postgresql.Driver");
			//Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
 
		}
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://192.168.1.212:5432/mydata?charSet=UTF-8", props);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
 
		}
		File f1 = new File("/Users/mac/Desktop/tmp/13000.1.csv");
		try {
			BufferedReader br = new BufferedReader(new FileReader(f1));
		    String line;
		    while ((line = br.readLine()) != null) {
		    	//System.out.println(line);
		    	String[] s1 = line.split("[,]+");
		    	System.out.println(s1[0]+" - " + s1[1]+ " - " + s1[2]);
		    	try {
		    		stmt = connection.createStatement();
		    		sql = "insert into chinese_characters(id,word,stroke) values('"+ s1[0] +"','" + s1[1] + "','"+ s1[2] +"')";
					rs = stmt.executeUpdate(sql);
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    }
		}catch (Exception e) {
			//System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}finally{
			try {
				stmt.close();
			}catch (SQLException e) {}
		}
		
	}
}
