/*
   Jaivox Application Generator (JAG) version 0.2 March 2014
   Copyright 2010-2014 by Bits and Pixels, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

Please see work/licenses for licenses to other components included with
this package.
*/
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
    static String[] cfiles = { "JvxInteract.java" };
    
    public AndroidAppGenerator(Properties conf) {
        this.conf = conf;
    }

    void generate() {
        String project = conf.getProperty ("project");
        String appfolder = conf.getProperty ("appfolder");
	String assets = conf.getProperty ("assets");
        String cpsrc = conf.getProperty ("cpsrc");
        
        if (!appfolder.endsWith (File.separator)) {
                appfolder += File.separator;
        }
        try {
            PrintWriter out = new PrintWriter(appfolder + "assets/assets.lst");
            File f = new File(assets);
            String files[] = f.list();
            for(String s : files) {
                if(s.endsWith(".java")) continue;
                createCheckSum(assets, s);
                out.println(project +"/"+ s);
            }
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(AndroidAppGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
	String dest = appfolder + "src/com/jaivox/ui/android/";
        for(String f  : cfiles) {
            GuiPrep.generateFile (conf, cpsrc, dest, f);
        }
    }
    void createCheckSum(String folder, String file) {
        MessageDigest md;
        try {
            File f = new File(folder, file);
			if (f.isDirectory ()) return;
            md = MessageDigest.getInstance("MD5");
            InputStream is = new FileInputStream(f);
            DigestInputStream dis = new DigestInputStream(is, md);
            while (dis.read() != -1);
            byte[] digest = md.digest();
            new FileOutputStream( new File(folder, file + ".md5") ).write(digest);
        } catch( Exception e) { e.printStackTrace(); return; }
    }
    
}
