package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Base64.Encoder;

public class ToHash1 {
	final static Base64.Decoder decoder = Base64.getDecoder();
	final static Base64.Encoder encoder = Base64.getEncoder();
	public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
	       byte[] data = "test".getBytes("UTF8");
	       String filecontent = "";
	       // The name of the file to open.
        String fileName = "/Users/mac/Desktop/Eclipse_Work/workspace_oxygen/mydata/build.gradle";
        BufferedReader br = null;

	        try {
	
	            String sCurrentLine;
	            br = new BufferedReader(new FileReader(fileName));
	            while ((sCurrentLine = br.readLine()) != null) {
	                //byte[] data = sCurrentLine.getBytes("UTF8");
	            		filecontent += sCurrentLine;
	            }
	
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (br != null) {
	                    br.close();
	                }
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
	        System.out.println(filecontent);
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(filecontent.getBytes("UTF8"));
	        System.out.println(encoder.encode(hash));
	}

}
