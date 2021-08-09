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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class emsgF03AIChange {


	public static void main(String[] args) throws FileNotFoundException, IOException, SQLException {
	    
        String sql = "";
        String sql1= "";
        String sql2= "";
        String sql3= "";
        String sql4= "";
        Statement stmt = null;
        int rs;
		ResultSet rs1 = null,rs2 = null,rs3 = null;
        int personal_total = 0;
        int great_total = 0;
        int total = 0;
		String keyLineTmp;
		String keyLine1Tmp;
		
		Properties props = new Properties();
	    props.put("user", "postgres");
	    props.put("password", "riease1qaz");
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
 
		}
		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://192.168.1.212:5432/emsg?currentSchema=public&charSet=UTF-8&loginTimeout=10&connectTimeout=10&socketTimeout=20", props);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
 
		}
		int array_index = 0;
		String msg_id_str="";
		if (connection != null) {
			File f1 = new File("/Users/mac/Desktop/emsg/data_final_6_data2.csv");
			try (BufferedReader br = new BufferedReader(new FileReader(f1))) {
			    String line;
			    while ((line = br.readLine()) != null) {
				    	total = total+1;
				    	try {
					    	stmt = connection.createStatement();
					    	System.out.println(line);
					    	String[] s1 = line.split("[\\s]+");
					    	if(msg_id_str!=null&&msg_id_str.length()>0){
					    		msg_id_str=msg_id_str + ","+ s1[0];
					    	}else{
					    		msg_id_str=s1[0];
					    	}
					    	System.out.println(s1[0]+"-"+s1[1]+"-"+s1[2]+","+s1[3]+"-"+s1[4]);
					    	sql = "update f03_message set p_groups='"+ s1[1]+","+ s1[3] +"' where msg_id="+ s1[0] ;
					    	System.out.println(sql);
					    	rs = stmt.executeUpdate(sql);
					    	sql1 = "delete from f03_profile_groups where msg_id="+ s1[0] ;
					    	rs = stmt.executeUpdate(sql1);
					    	sql2 = "insert into f03_profile_groups(msg_id,groups,score) values('"+s1[0]+"','"+s1[1]+"','"+s1[2]+"')";
					    	System.out.println(sql2);
					    	rs = stmt.executeUpdate(sql2);
					    	sql3 = "insert into f03_profile_groups(msg_id,groups,score) values('"+s1[0]+"','"+s1[3]+"','"+s1[4]+"')";
					    	System.out.println(sql3);
					    	rs = stmt.executeUpdate(sql3);
					    	/**
					    	 * 關鍵字處理
					    	 */
						String patternStr = "101:\\[[\\s\\S]*\\]";
						Pattern pattern = Pattern.compile(patternStr);
						Matcher matcher = pattern.matcher(line);
						while (matcher.find()) {
							keyLineTmp = matcher.group();
							System.out.println(keyLineTmp);
							System.out.println("------------------------------");
							String[] s2 = keyLineTmp.split("\\sK");
							for(String s2tmp:s2) {
								String[] s3 = s2tmp.split("[:]");
								System.out.println(s3[1]);
								String[] s4 = s3[1].split("[,]");
								for(String s3tmp:s4) {
									/**
									 * 中文:[\u4e00-\u9fa5] 
									 * 英文字母:[a-zA-Z] 
									 * 数字:[0-9] 
									 * 匹配中文，英文字母和数字及_:^[\u4e00-\u9fa5_a-zA-Z0-9]+$
									 */
									String patternStr1 = "\\'[\\s\\S^\\,]+\\'";
									Pattern pattern1 = Pattern.compile(patternStr1);
									Matcher matcher1 = pattern1.matcher(s3tmp);
									while (matcher1.find()) {
										keyLine1Tmp = matcher1.group();
										System.out.println(keyLine1Tmp.replaceAll("'", ""));
										String keyword = keyLine1Tmp.replaceAll("'", "");
										if(keyword!=null&&keyword.trim().length()>0) {
											try {
												sql4 = "insert into f03_message_keyword(msg_id,keyword) values('"+s1[0]+"','"+keyword+"')";
												rs = stmt.executeUpdate(sql4);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								}
							}
						}						
					    	stmt.close();
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}finally{
						try {
							stmt.close();
						}catch (SQLException e) {}
					}
			    }
			}
		}
		System.out.println("===============================================");
		System.out.println("==total=="+total);
		System.out.println(msg_id_str);
	}


}
