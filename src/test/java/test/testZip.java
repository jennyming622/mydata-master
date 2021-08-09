package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class testZip {

	public static void main(String[] args) {
		File zf = new File("/Users/mac/download/1.zip");
		File rf = new File("/Users/mac/mydata/temp/20200720/0991f1cebff343e8b23c1b8d9ba7a87c");
		ArrayList<File> sources = new ArrayList<File>();
		File[] allFiles = rf.listFiles();
		if(allFiles!=null&&allFiles.length>0) {
			for(File f:allFiles) {
				System.out.println(f.getAbsolutePath());
				if(f.isDirectory()) {
					File[] nextFiles = f.listFiles();
					if(nextFiles!=null&&nextFiles.length>0) {
						for(File f1:nextFiles) {
							System.out.println(f1.getAbsolutePath());
							if(f1.isDirectory()) {
								//UNDO
							}else {
								/**
								 * add to zip file
								 */
								sources.add(f1);
							}
						}
					}					
				}else {
					/**
					 * add to zip file
					 */
					sources.add(f);
				}
			}
			try {
				packZipWithPassword(zf,sources,"H120983053");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ZipException e) {
				e.printStackTrace();
			}
		}		
	}
	private static File packZipWithPassword(File output, ArrayList<File> sources, String password)
			throws IOException, ZipException {
		if (!output.getParentFile().exists()) {
			output.getParentFile().mkdirs();
		}
		ZipFile zipFile = new ZipFile(output);
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		parameters.setEncryptFiles(true);
		parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
		parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
		parameters.setIncludeRootFolder(false);
		parameters.setPassword(password);
		//zipFile.addFiles(sources, parameters);
		if(sources!=null&&sources.size()>0) {
			for(File f:sources) {
				System.out.println("ORG:"+f.getAbsolutePath());
				if(f.getAbsolutePath().indexOf("META-INFO")>-1) {
					System.out.println("META-INFO:"+f.getAbsolutePath());
					parameters.setRootFolderInZip("META-INFO");
					zipFile.addFile(f, parameters);
				}else {
					System.out.println("ROOT:"+f.getAbsolutePath());
					parameters.setRootFolderInZip("");
					zipFile.addFile(f, parameters);
				}
			}
		}
		return output;
	}
}
