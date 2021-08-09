package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;

import org.apache.commons.io.FilenameUtils;

import com.riease.common.helper.SequenceHelper;
import com.riease.common.helper.ValidatorHelper;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class testHandleZipPath {

	public static void main(String[] args) throws IOException, ZipException {
		File f1 = new File("/Users/mac/Desktop/tmp/tmp/所得資料(RAW+DATA)20210518102958.zip");
		System.out.println(f1.getAbsolutePath());
		File f2 = new File(f1.getParentFile().getAbsolutePath()+"/unzipdir");
		File f3 = new File("/Users/mac/Desktop/tmp/tmp/所得資料(RAW+DATA).zip");
		f1 = handleSpecPath(f1);
		System.out.println(f1.getAbsolutePath());
	}
    /**
     * 解壓縮
     * @param sourceFile
     * @param targetDir
     * @throws IOException 
     */
    public static void unzipSpec(File sourceFile, File targetDir) throws IOException {
        if (!targetDir.exists() || !targetDir.isDirectory()) {
        		targetDir.mkdirs();
        }
        ZipEntry entry = null;
        String entryFilePath = null, entryDirPath = null;
        File entryFile = null, entryDir = null;
        int index = 0, count = 0, bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        java.util.zip.ZipFile zip = new java.util.zip.ZipFile(sourceFile.getAbsoluteFile());
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();
        while (entries.hasMoreElements()) {
            entry = entries.nextElement();
            entryFilePath = ValidatorHelper.removeSpecialCharacters(targetDir.getAbsolutePath() + File.separator + entry.getName().replace("META-INFO\\", ""));
            index = entryFilePath.lastIndexOf(File.separator);
            if (index != -1) {
                entryDirPath = entryFilePath.substring(0, index);
            } else {
                entryDirPath = "";
            }
            entryDir = new File(entryDirPath);
            if (!entryDir.exists() || !entryDir.isDirectory()) {
                entryDir.mkdirs();
            }
            entryFile = new File(entryFilePath);
            if(!entryFile.isDirectory()) {
	            bos = new BufferedOutputStream(new FileOutputStream(entryFile));
	            bis = new BufferedInputStream(zip.getInputStream(entry));
	            while ((count = bis.read(buffer, 0, bufferSize)) != -1) {
	                bos.write(buffer, 0, count);
	            }
            }
            try {
	            bos.flush();
	            bos.close();
            }catch(Exception ex) {
            		System.out.println(ex);
            }
        }
    }	
    
    public static File packZipSpec(File output, ArrayList<File> sources, ArrayList<File> metas)
			throws IOException, ZipException {
		if (!output.getParentFile().exists()) {
			output.getParentFile().mkdirs();
		}
		ZipFile zipFile = new ZipFile(output);
		ZipParameters parameters = new ZipParameters();
		/**
		 * COMP_STORE = 0;（僅打包，不壓縮） （對應好壓的儲存） 
		 * COMP_DEFLATE = 8;（預設） （對應好壓的標準） 
		 * COMP_AES_ENC = 99;
		 */
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		if(sources!=null&&sources.size()>0) {
			zipFile.addFiles(sources, parameters);
		}
		parameters.setRootFolderInZip("META-INFO");
		zipFile.addFiles(metas, parameters);
		return output;
    }
    public static File handleSpecPath(File file) throws IOException, ZipException {
    	String randomId = SequenceHelper.createUUID();
    	String fname = FilenameUtils.getBaseName(file.getAbsolutePath());
    	String ext = FilenameUtils.getExtension(file.getAbsolutePath());
    	File out = new File(file.getParentFile().getAbsolutePath()+"/"+fname+randomId+"."+ext);
    	File unzipdir = new File(file.getParentFile().getAbsolutePath()+"/unzipdir");
    	unzipSpec(file,unzipdir);
		ArrayList<File> sources = new ArrayList<File>();
		List<String> sourcesexts = Arrays.asList("pdf", "json","p7b");
		ArrayList<File> metas = new ArrayList<File>();
		List<String> metasexts = Arrays.asList("cer", "sha256withrsa","xml");
		File[] allFiles = unzipdir.listFiles();
		if(allFiles!=null&&allFiles.length>0) {
			for(File f:allFiles) {
				String extension = FilenameUtils.getExtension(f.getAbsolutePath()).toLowerCase(Locale.ENGLISH);
				if(sourcesexts.contains(extension)) {
					sources.add(f);
				}
				if(metasexts.contains(extension)) {
					metas.add(f);
				}
			}
		}
		packZipSpec(out,sources,metas);
    	return out;
    }
}
