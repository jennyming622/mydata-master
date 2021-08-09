package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Base64;

public class ToHash {
	final static Base64.Decoder decoder = Base64.getDecoder();
	final static Base64.Encoder encoder = Base64.getEncoder();
	public static void main(String[] args) {
	       // The name of the file to open.
        String fileName = "/Users/mac/Desktop/Eclipse_Work/workspace_oxygen/mydata/build.gradle";
        BufferedReader br = null;

        try {

            String sCurrentLine;
            br = new BufferedReader(new FileReader(fileName));
            while ((sCurrentLine = br.readLine()) != null) {
                byte[] data = sCurrentLine.getBytes("UTF8");
                System.out.println(encoder.encodeToString(data));
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
	}

}
