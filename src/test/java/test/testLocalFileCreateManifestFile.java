package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class testLocalFileCreateManifestFile {

	public static void main(String[] args) throws IOException, ZipException {
		ArrayList<File> localFiles = new ArrayList<File>();
		File f1 = new File("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ/API.UZQkKbsOpz.zip");
		File f2 = new File("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ/API.zH584wn59r.zip");
		//File f3 = new File("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ/META-INFO/manifest.xml");
		localFiles.add(f1);
		localFiles.add(f2);
		//localFiles.add(f3);
		packZip(new File("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ_2.zip"),localFiles);
	}

	private static File packZip(File output, ArrayList<File> sources)
			throws IOException, ZipException {
		if (!output.getParentFile().exists()) {
			output.getParentFile().mkdirs();
		}
		ZipFile zipFile = new ZipFile(output);
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		//parameters.setEncryptFiles(true);
		//parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
		//parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
		zipFile.addFiles(sources, parameters);
		parameters.setRootFolderInZip("META-INFO");
		File f3 = new File("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ/META-INFO/manifest.xml");
		ArrayList<File> localFiles = new ArrayList<File>();
		localFiles.add(f3);
		zipFile.addFiles(localFiles, parameters);
		return output;
	}
}
