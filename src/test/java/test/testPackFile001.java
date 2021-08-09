package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;

import com.riease.common.helper.SequenceHelper;
import com.riease.common.helper.ValidatorHelper;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class testPackFile001 {

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, ZipException {
		//String fileName = "/Users/mac/Desktop/mydata-example/tmp/work/e管家我的生活消費20201012135949.zip";
		String fileName = "/Users/mac/Desktop/mydata-example/tmp/work/API.UbwT0ojLZ1.zip";
		File orifile = new File(fileName);
		//File targetFile = filterSpFile(orifile);
		File targetFile = filterDpFile(orifile);
		System.out.println(targetFile);
	}
	
	public static File filterSpFile(File orifile) throws IOException, ParserConfigurationException, SAXException, ZipException {
		File targetFile = null;
		if(orifile.exists()) {
			File f1Parent = orifile.getParentFile();
			String randomId = SequenceHelper.createUUID();
			File targetDir = new File(f1Parent.getAbsoluteFile()+"/"+randomId);
			targetFile = new File(f1Parent.getAbsoluteFile()+"/"+randomId+".zip");
			unzip(orifile,targetDir);
			//manifest.xml
			File mf = new File(targetDir.getAbsoluteFile()+"/META-INFO/manifest.xml");
			if(mf.exists()) {
				DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
				DocumentBuilder dfb = df.newDocumentBuilder();
				Document doc = dfb.parse(mf.getAbsoluteFile());
				NodeList nList = doc.getElementsByTagName("file");
				ArrayList<File> sources = new ArrayList<>();
				ArrayList<File> manifestSource = new ArrayList<>();
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						File tof = new File(targetDir.getAbsoluteFile()+"/"+getTagValue("resource_id", eElement)+".zip");
						File tnf = new File(targetDir.getAbsoluteFile()+"/"+getTagValue("resource_name", eElement)+".zip");
						File uziptnfDir = new File(tnf.getParentFile()+"/"+getTagValue("resource_name", eElement));
						File uziptnf =  new File(tnf.getParentFile()+"/"+getTagValue("resource_name", eElement)+".zip");
						ArrayList<File> dpsources = new ArrayList<>();
						ArrayList<File> dpmanifestSource = new ArrayList<>();
						if(tof.exists()) {
							unzip(tof,uziptnfDir);
							File[] unzipfiles = uziptnfDir.listFiles();
							if(unzipfiles!=null&&unzipfiles.length>0) {
								for(File f:unzipfiles) {
									if(f.isFile()) {
										String extension = FilenameUtils.getExtension(f.getName());
										if(!extension.equalsIgnoreCase("pdf")) {
											f.deleteOnExit();
										}else {
											dpsources.add(f);
										}
									}else if(f.isDirectory()) {
										if(f.listFiles().length>0) {
											for(File ft:f.listFiles()) {
												dpmanifestSource.add(ft);
											}
										}
									}
								}
							}
							packDpZip(uziptnf,dpsources,dpmanifestSource);
							sources.add(uziptnf);
						}
					}
				}
				manifestSource.add(mf);
				packDpZip(targetFile,sources,manifestSource);
				FileUtils.deleteDirectory(targetDir);
			}else {
				System.out.println("mf not found!");
			}
		}else {
			System.out.println("orifile not found!");
		}
		return targetFile;
	}
	
	
	private static String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = (Node) nlList.item(0);
		return nValue.getNodeValue();
	}

	private static File packDpZip(File output, ArrayList<File> sources, ArrayList<File> manifestSource)
			throws IOException, ZipException {
		if (!output.getParentFile().exists()) {
			output.getParentFile().mkdirs();
		}
		ZipFile zipFile = new ZipFile(output);
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		zipFile.addFiles(sources, parameters);
		parameters.setRootFolderInZip("META-INFO");
		zipFile.addFiles(manifestSource, parameters);
		return output;
	}	
	
	public static File filterDpFile(File orifile) throws IOException, ParserConfigurationException, SAXException, ZipException {
		File targetFile = null;
		if(orifile.exists()) {
			File f1Parent = orifile.getParentFile();
			String randomId = SequenceHelper.createUUID();
			File targetDir = new File(f1Parent.getAbsoluteFile()+"/"+randomId);
			targetFile = new File(f1Parent.getAbsoluteFile()+"/"+randomId+".zip");
			unzip(orifile,targetDir);
			File[] unzipfiles = targetDir.listFiles();
			ArrayList<File> dpsources = new ArrayList<>();
			ArrayList<File> dpmanifestSource = new ArrayList<>();
			if(unzipfiles!=null&&unzipfiles.length>0) {
				for(File f:unzipfiles) {
					if(f.isFile()) {
						String extension = FilenameUtils.getExtension(f.getName());
						if(!extension.equalsIgnoreCase("pdf")) {
							f.deleteOnExit();
						}else {
							dpsources.add(f);
						}
					}else if(f.isDirectory()) {
						if(f.listFiles().length>0) {
							for(File ft:f.listFiles()) {
								dpmanifestSource.add(ft);
							}
						}
					}
				}
			}
			packDpIncludeMetaInfoZip(targetFile,dpsources,dpmanifestSource);
			FileUtils.deleteDirectory(targetDir);
		}else {
			System.out.println("orifile not found!");
		}
		return targetFile;
	}	
	
    /**
     * 解壓縮
     * @param sourceFile
     * @param targetDir
     * @throws IOException 
     */
    public static void unzip(File sourceFile, File targetDir) throws IOException {
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
            entryFilePath = ValidatorHelper.removeSpecialCharacters(targetDir.getAbsolutePath() + File.separator + entry.getName());
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
    
	private static File packDpIncludeMetaInfoZip(File output, ArrayList<File> sources, ArrayList<File> manifestSource)
			throws IOException, ZipException {
		if (!output.getParentFile().exists()) {
			output.getParentFile().mkdirs();
		}
		ZipFile zipFile = new ZipFile(output);
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		zipFile.addFiles(sources, parameters);
		if(manifestSource!=null&&manifestSource.size()>0) {
			parameters.setRootFolderInZip("META-INFO");
			zipFile.addFiles(manifestSource, parameters);
		}
		return output;
	}	   
}
