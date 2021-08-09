package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

public class testLocalFileCreateManifestFile1 {

	public static void main(String[] args) throws IOException {
		File f1 = new File("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ/API.UZQkKbsOpz.zip");
		File f2 = new File("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ/API.zH584wn59r.zip");
		ArrayList<File> localFiles = new ArrayList<File>();
		localFiles.add(f1);
		localFiles.add(f2);
		File f3 = new File("/Users/mac/Desktop/mydata-example/CLI.klmZFqH5SQ/META-INFO/manifest.xml");
		String f3Str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		f3Str += "<files>";
		for(File f:localFiles) {
			f3Str += "<file>";
			//filename
			f3Str += "<filename>";
			f3Str += f.getName();
			f3Str += "</filename>";
			//resource_id
			f3Str += "<resource_id>";
			f3Str += f.getName();
			f3Str += "</resource_id>";
			//resource_name
			f3Str += "<resource_name>";
			f3Str += "中文測試中文資料集";
			f3Str += "</resource_name>";
			f3Str += "</file>";
		}
		f3Str += "</files>";
		
		FileUtils.writeStringToFile(f3, f3Str,"UTF-8");
	}

}
