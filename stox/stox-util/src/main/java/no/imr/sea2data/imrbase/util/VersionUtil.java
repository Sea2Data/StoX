/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.imrbase.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 *
 * @author aasmunds
 */
public class VersionUtil {

    public static String getManifestAttribute(InputStream in, String attr) {
        try {
            Manifest manifest = new Manifest(in);
            Attributes attributes = manifest.getMainAttributes();
            return attributes.getValue(attr);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
    public static final String APPVERSION = "appversion";

    public static void transferAppVersionFromManifestToSystemProperties(InputStream in) {
        setAppVersion(getManifestAttribute(in, APPVERSION));
    }

    public static void setAppVersion(String appVer) {
        setAppVersion(appVer, "");
    }

    public static void setAppVersion(String appVer, String add) {
        System.setProperty("netbeans.buildnumber", getAppTitleFromVer(appVer, add));
        System.setProperty(APPVERSION, appVer);
    }

    private static String getAppTitleFromVer(String appVer, String add) {
        String[] elms = appVer.split("-");
        if (elms.length == 2) {
            String code = elms[1];
            if (code.startsWith("SR")) {
                add += " Service release " + code.substring(2);
            } else if (code.startsWith("A")) {
                add += " Alpha " + code.substring(1);
            }
        }
        return elms[0] + add;
    }

    public static Boolean isVersionLessThan(String v1, String v2) {
        String sepexp = "\\.";
        String[] s1 = v1.split(sepexp);
        String[] s2 = v2.split(sepexp);
        for (int i = 0; i < 3; i++) {
            if (i >= s1.length || i >= s2.length) {
                return false;
            }
            String si1 = s1[i];
            String si2 = s2[i];

            if (si1.contains("-A")) {
                // si2 will never contain alpha
                // this means to update from alpha to release
                // thus compare with the lowest number 0
                si1 = "0";
            }
            Integer add1 = 0;
            Integer add2 = 0;
            if (i == 2) {
                if (si1.contains("SR")) {
                    add1 = Conversion.safeStringtoIntegerNULL(si1.substring(si1.indexOf("-SR") + 3));
                    si1 = si1.substring(0, si1.indexOf("-SR"));
                }
                if (si2.contains("-SR")) {
                    add2 = Conversion.safeStringtoIntegerNULL(si2.substring(si2.indexOf("-SR") + 3));
                    si2 = si2.substring(0, si2.indexOf("-SR"));
                }
            }
            Integer ni1 = Conversion.safeStringtoIntegerNULL(si1);
            Integer ni2 = Conversion.safeStringtoIntegerNULL(si2);
            if (ni1 == null || ni2 == null) {
                return false;
            }
            if (i == 2) {
                ni1 *= 1000 + add1;
                ni2 *= 1000 + add2;
            }
            if (ni1 > ni2) {
                return false;
            }
            if (ni1 < ni2) {
                return true;
            }
        }
        return false;
    }
}
