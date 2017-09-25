package no.imr.stox.functions.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author aasmunds
 */
public class RFolder {

    public static String getRFolderFile() {
        return RUtils.getTmpDir() + "RFolder.txt";
    }

    public static void writeRFolderToFile(String folder) {
        try {
            List<String> lines = Arrays.asList(folder);
            Path file = Paths.get(getRFolderFile());
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException ex) {
        }

    }

    public static String getRFolderFromFile() {
        Path file = Paths.get(getRFolderFile());
        if (!file.toFile().exists()) {
            return "";
        }
        try {
            List<String> l = Files.readAllLines(file);
            return l.size() == 1 ? l.get(0) : "";
        } catch (IOException ex) {
            return ex.getMessage();
        }
    }
}
