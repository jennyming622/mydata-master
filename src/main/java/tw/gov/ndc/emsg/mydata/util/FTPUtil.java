package tw.gov.ndc.emsg.mydata.util;

import java.io.*;
import java.net.URI;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FTPUtil {
    private static final Logger log= LoggerFactory.getLogger(FTPUtil.class.getName());

    private FTPUtil() {
    }

    /**
     * 下載檔案
     * @param ftpServer URI FTP Server位置
     * @param ftpAccount String FTP Server登入帳號
     * @param ftpPass String FTP Server登入密碼
     * @param files String[] 欲下載檔案
     * @param targetDir File 本地端檔案儲存目錄
     * @throws Exception
     */
    public static void download(URI ftpServer ,String ftpAccount ,String ftpPass,
            String[] files,File targetDir) throws Exception {

        FTPClient client=new FTPClient();

        boolean ftpStatus=false;
        try {
            log.info("connect to "+ftpServer);
            client.connect(ftpServer.getHost(),ftpServer.getPort());

            // login
            ftpStatus=client.login(ftpAccount,ftpPass);

            if (!ftpStatus){
                throw new Exception("Connect server error: Reason:"+client.getReplyString());
            }

            // passive mode transfer 被動模式
            client.enterLocalPassiveMode();

            // set binary transfer mode
            ftpStatus=client.setFileType(FTP.BINARY_FILE_TYPE);
            log.info("download files.......");
            java.io.File tf=null;

            FileOutputStream fos=null;
            for (int i = 0; i < files.length; i++) {
                int sp=files[i].lastIndexOf("/");
                String remotePath=(sp==-1)?"/":files[i].substring(0,sp);

                tf=new File (targetDir,files[i].substring(sp+1));
                log.info("download "+files[i] +" --> "+tf.getName()+"...");
                fos=new java.io.FileOutputStream(tf);
                ftpStatus=client.retrieveFile(files[i],fos);
                if(fos!=null) {
                	fos.close();
                }
                if (!ftpStatus) {
                    throw new Exception("Download error. Reason: "+client.getReplyString());
                }
            }
        } finally {
            client.logout();
        }
    }

    /**
     * 上傳檔案
     * @param ftpServer URI FTP Server
     * @param ftpUser String FTP Server登入帳號
     * @param ftpPass String FTP Server登入密碼
     * @param remotePath String Server端目錄
     * @param files File[] 欲上傳檔案
     * @throws Exception
     */
    public static void upload(URI ftpServer,String ftpUser,String ftpPass,String remotePath, java.io.File[] files)
                throws Exception {

        boolean bs=false;
        //InetAddress ftpServer = null;
        FTPClient client = new FTPClient();
        //ftpServer = InetAddress.getByName(ftpHost);
        client.connect(ftpServer.getHost(),ftpServer.getPort());
        //client.connect(ftpServer, ftpPort);
        client.enterLocalPassiveMode();
        client.login(ftpUser, ftpPass);
        client.setFileType(FTP.BINARY_FILE_TYPE);

        java.io.File tf = null;
        log.info("change remote dir to :"+remotePath);
        bs=client.changeWorkingDirectory(remotePath);
        if (!bs){
            log.info(client.getReplyString());
        }
        log.info("upload files.......");

        FileInputStream fis = null;

        for (int i = 0; i < files.length; i++) {
            tf = files[i];
            log.info("-->" + tf.getName() + "...");
            fis = new java.io.FileInputStream(tf);
            client.storeFile(tf.getName(), fis);
            fis.close();
        }
        client.logout();
    }
}
