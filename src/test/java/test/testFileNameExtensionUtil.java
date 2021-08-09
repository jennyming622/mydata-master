package test;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

public class testFileNameExtensionUtil {

	public static void main(String[] args) {
		File f1 = new File("/path/to/file/foo.txt");
		System.out.println(FilenameUtils.getExtension(f1.getAbsolutePath()));
		System.out.println(FilenameUtils.getBaseName(f1.getAbsolutePath()));
		System.out.println(FilenameUtils.getName(f1.getAbsolutePath()));
	}

}
