package com.riease.common.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Locale;

//import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.io.Files;
import com.riease.common.bean.FileInfo;


/**
 * 圖檔與Base64編碼之Helper
 * @author wesleyzhuang
 *
 */
public class ImageBase64Helper {

	
	/**
	 * 把圖檔轉換成 html img.src 可使用的base64字串。
	 * @param fname
	 * @param data
	 * @return
	 */
	public static String htmlBase64String(String fname, byte[] data) {
		final Base64.Encoder encoder = Base64.getEncoder();
		if(fname.toLowerCase(Locale.ENGLISH).endsWith(".jpg")) {
			return "data:image/jpg;base64,"+encoder.encodeToString(data);
		}else if(fname.toLowerCase(Locale.ENGLISH).endsWith(".jpeg")) {
			return "data:image/jpeg;base64,"+encoder.encodeToString(data);
		}else if(fname.toLowerCase(Locale.ENGLISH).endsWith(".png")) {
			return "data:image/png;base64,"+encoder.encodeToString(data);
		}else if(fname.toLowerCase(Locale.ENGLISH).endsWith(".gif")) {
			return "data:image/gif;base64,"+encoder.encodeToString(data);
		}else if(fname.toLowerCase(Locale.ENGLISH).endsWith(".bmp")) {
			return "data:image/bmp;base64,"+encoder.encodeToString(data);
		}else {
			String ext = FilenameUtils.getExtension(fname);
			return "data:image/"+ext+";base64,"+encoder.encodeToString(data);
		}
	}
	
	/**
	 * 將base64編碼後的資料寫入目標檔案。
	 * @param targetFile
	 * @param base64encodedBinary
	 * @throws IOException
	 */
	public static void base64ToFile(File targetFile, String base64encodedBinary) throws IOException {
		if(targetFile != null && StringUtils.isNotEmpty(base64encodedBinary)) {
			final Base64.Decoder decoder = Base64.getDecoder();
			//data:image/jpeg;base64,
			if(base64encodedBinary.startsWith("data:")) {
				int pos = base64encodedBinary.indexOf(";base64,")+";base64,".length();
				String base64 = base64encodedBinary.substring(pos);
				//decoder.de
				if(targetFile.exists()) targetFile.delete();
				if(targetFile.createNewFile()) {
					Files.write(decoder.decode(base64), targetFile);
				}
			}
		}
	}
	
	/**
	 * 將base64編碼後的資料寫入目標檔案。
	 * @param targetFile
	 * @param fileInfo
	 * @throws IOException
	 */
	public static void base64ToFile(File targetFile, FileInfo fileInfo) throws IOException {
		base64ToFile(targetFile, fileInfo.getBase64Data());
	}
	
	
}
