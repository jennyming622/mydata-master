package johntest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class DpDataTest {

    static Logger logger = LoggerFactory.getLogger(DpDataTest.class);

    public static void main(String[] args) throws IOException {

//        File targetFile = new File("/Users/zhanqianjin/Downloads/API.KvvyRZSc5K.zip");
////        File destDir = new File(targetFile.getParentFile().getPath() + "/" + FilenameUtils.getBaseName(targetFile.getName()));
//        File destDir = targetFile.getParentFile();
//
//        unzip(targetFile,destDir);

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
        ZipFile zip = new ZipFile(sourceFile.getAbsoluteFile());
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries();

        logger.debug("source file name -> {}" ,sourceFile.getName());
        while (entries.hasMoreElements()) {

            entry = entries.nextElement();
            entryFilePath = targetDir.getAbsolutePath() + File.separator + entry.getName();

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

            if(!FilenameUtils.getBaseName(sourceFile.getName()).equals(entryFile.getName())
               && !entryFile.isDirectory()){
                bos = new BufferedOutputStream(new FileOutputStream(entryFile));
                bis = new BufferedInputStream(zip.getInputStream(entry));
                while ((count = bis.read(buffer, 0, bufferSize)) != -1) {
                    bos.write(buffer, 0, count);
                }
                bos.flush();
                bos.close();
            }
        }

    }
}
