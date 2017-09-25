/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.core.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 *
 * @author aasmunds
 */
public class FTPUtil {

    /**
     * Download a single file from the FTP server
     *
     * @param ftpClient an instance of org.apache.commons.net.ftp.FTPClient
     * class.
     * @param remoteFilePath path of the file on the server
     * @param localFile path of directory where the file will be stored
     * @return true if the file was downloaded successfully, false otherwise
     * @throws IOException if any network or IO error occurred.
     */
    public static boolean retrieveFile(FTPClient ftpClient, String remoteFilePath, String localFile) throws IOException {
        File downloadFile = new File(localFile);
        File parentDir = downloadFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdir();
        }
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile))) {
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            return ftpClient.retrieveFile(remoteFilePath, outputStream);
        } catch (IOException ex) {
            throw ex;
        }
    }

    /**
     * Download a whole directory from a FTP server.
     *
     * @param ftpClient an instance of org.apache.commons.net.ftp.FTPClient
     * class.
     * @param remoteParentDir Path of the parent directory of the current directory
     * being downloaded.
     * @param currentDir Path of the current directory being downloaded.
     * @param localParentDir path of directory where the whole remote directory will be
     * downloaded and saved.
     * @throws IOException if any network or IO error occurred.
     */
    public static void retrieveDir(FTPClient ftpClient, String remoteParentDir, String localParentDir) throws IOException {
        FTPFile[] ftpFiles = ftpClient.listFiles(remoteParentDir);
        if (ftpFiles == null || ftpFiles.length == 0) {
            return;
        }
        for (FTPFile ftpFile : ftpFiles) {
            String ftpFileName = ftpFile.getName();
            if (ftpFileName.equals(".") || ftpFileName.equals("..")) {
                // skip parent directory and the directory itself
                continue;
            }
            String localFile = localParentDir + "/" + ftpFileName;
            String remoteFile = remoteParentDir + "/" + ftpFileName;
            if (ftpFile.isDirectory()) {
                // create the directory in saveDir
                File newDir = new File(localFile);
                newDir.mkdirs();
                // download the sub directory
                retrieveDir(ftpClient, remoteFile, localFile);
            } else {
                // download the file
                retrieveFile(ftpClient, remoteFile, localFile);
            }
        }
    }
}
