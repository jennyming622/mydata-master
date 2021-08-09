package test;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public class TestMD5 {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	private static String getFileSizeMegaBytes(File file) {
		return (double) file.length() / (1024 * 1024) + " mb";
	}
	
	private static String getFileSizeKiloBytes(File file) {
		return (double) file.length() / 1024 + "  kb";
	}

	private static String getFileSizeBytes(File file) {
		return file.length() + " bytes";
	}
	public static void main(String[] args) {
		System.out.println("起始時間："+sdf.format(new Date()));
		String ret = null;
		 try {
			 // 生成一個MD5加密計算摘要
			 MessageDigest md = MessageDigest.getInstance("SHA-512");
			 // 計算md5函數
			 File f1 = new File("/Users/mac/Desktop/mydata-example/CLI.IjktHSvUPU.zip");
			 System.out.println("檔案名稱："+f1.getName());
			 System.out.println("檔案大小："+getFileSizeKiloBytes(f1));
			 md.update(FileUtils.readFileToByteArray(f1));
			 // digest()最後確定返回md5 hash值，返回值為8為字符串。因為md5 hash值是16位的hex值，實際上就是8位的字符
			 // BigInteger函數則將8位的字符串轉換成16位hex值，用字符串來表示；得到字符串形式的hash值
			 ret = new BigInteger(1, md.digest()).toString(16);
		 } catch (Exception e) {
			 //throw new SpeedException("MD5加密出現錯誤");
			 e.printStackTrace();
		 }
		 System.out.println("SHA-512 Digest："+ret);
		 System.out.println("結束時間："+sdf.format(new Date()));
	}
}
