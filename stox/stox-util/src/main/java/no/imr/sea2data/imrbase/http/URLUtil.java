/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.imrbase.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author aasmunds
 */
public class URLUtil {

    /**
     * Copy files in url directory to local path
     *
     * @param urlDir
     * @param outPath
     */
    public static void copyFiles(String urlDir, String outPath) {
        copyFiles(urlDir, outPath, false);
    }

    public static void copyFiles(String urlDir, String outPath, boolean force) {
        getLinesLastElement(urlDir + "/").forEach(f -> {
            copyFile(urlDir + "/" + f, outPath + "/" + f, force);
        });
    }

    public static URLConnection openConnection(String urlPath) {
        try {
            URL url = new URL(urlPath);
            return url.openConnection();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static Boolean copyFile(String urlPath, String outFile) {
        return copyFile(urlPath, urlPath, false);
    }

    public static Boolean copyFile(String urlPath, String outFile, boolean force) {
        if (!force && new File(outFile).exists()) {
            return false;
        }
        try {
            URL url = new URL(urlPath);
            URLConnection con = url.openConnection();
            Files.copy(con.getInputStream(), Paths.get(outFile), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * Use this against a ftp directory to get listing of file names
     *
     * @param urlPath
     * @return
     */
    public static List<String> getLinesLastElement(String urlPath) {
        return getLines(urlPath).stream().map(s -> {
            String[] elms = s.split("\\s+");
            return elms[elms.length - 1];
        }).collect(Collectors.toList());
    }

    /**
     * Get content of file
     *
     * @param urlPath
     * @return
     */
    public static List<String> getLines(String urlPath) {
        try {
            URL url = new URL(urlPath); // Directory listing
            URLConnection con = url.openConnection();
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                return buffer.lines().collect(Collectors.toList());
            }
        } catch (IOException ex) {
            // Unknown host or other network errors returns empty collection
            // Checking if online should be done in the application before
        }
        return new ArrayList<>();
    }
}
