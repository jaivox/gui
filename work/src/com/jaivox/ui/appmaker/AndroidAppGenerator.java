/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaivox.ui.appmaker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rj
 */
class AndroidAppGenerator {
    Properties conf = null;
    static String[] cfiles = { "AndroidFileUtil.java", "JvxInteract.java" };
    
    public AndroidAppGenerator(Properties conf) {
        this.conf = conf;
    }

    void generate() {
        String project = conf.getProperty ("project");
        String appfolder = conf.getProperty ("appfolder");
        String cpsrc = conf.getProperty ("cpsrc");
        
        if (!appfolder.endsWith (File.separator)) {
                appfolder += File.separator;
        }
        try {
            PrintWriter out = new PrintWriter(appfolder + "assets.lst");
            File f = new File(appfolder);
            String files[] = f.list();
            for(String s : files) {
                if(s.endsWith(".java")) continue;
                createCheckSum(appfolder, s);
                out.println(project +"/" + s);
            }
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(AndroidAppGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
        for(String f  : cfiles) {
            GuiPrep.generateFile (conf, cpsrc, appfolder, f);
        }
    }
    void createCheckSum(String folder, String file) {
        MessageDigest md;
        try {
            File f = new File(folder, file);
            md = MessageDigest.getInstance("MD5");
            InputStream is = new FileInputStream(f);
            DigestInputStream dis = new DigestInputStream(is, md);
            while (dis.read() != -1);
            byte[] digest = md.digest();
            new FileOutputStream( new File(folder, file + ".md5") ).write(digest);
        } catch( Exception e) { e.printStackTrace(); return; }
    }
    
}
