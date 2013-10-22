/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaivox.ui.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lin
 */
public class JvxConfiguration {
    
   public static final String genFolder = "work/apps/common/";
   	public static final String datadir = "work/apps/common/";
  	// public static final String datadir = "../../../../data/";
	// public static final String datadir = "data/";
    public static String appFolder = "./";
    
    static Properties conf = null; //new Properties();
    static String appName = null;
    JvxConfiguration(String text) {
        conf = new Properties() {
            @Override
            public Object put(Object key, Object value) {
                Object o = null;
                String s = (String)value;
                if(s != null && s.trim().startsWith("{") /*&& s.trim().endsWith("}")*/) {
                    int len = s.indexOf('}', 1);
                    s = s.substring(1, len);
                    if(s.length() > 0) o = get(s);
                    if(o != null && ((String)o).trim().length() > 0) {
                        String v = (String)value;
                        v = v.replace("{"+s+"}", appName);
                        value = v;
                    }
                }
                return super.put(key, value);
            }
        };
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(datadir + "template.conf"));
            conf.load(bf);
        } catch (Exception e) { e.printStackTrace(); }
        finally {
            try{ if(bf != null) bf.close(); } catch (Exception ex) { ex.printStackTrace(); }
        }
        appName = text;
    }
    public static String getAppFolder() {
        return appFolder.endsWith(File.separator) ? appFolder : (appFolder + File.separator);
    }
    public static String getConfFile() {
        return getAppFolder() + appName + ".conf";
    }
    void save(JvxMainFrame theFrame) {
        if(appName == null) return;
        File targFolder = new File(appFolder);
        targFolder.mkdirs();
        System.out.println("save: path: "+ targFolder.getAbsolutePath());
        
        setTargetSpec(theFrame);
        setMisc(theFrame);
        validatefields(theFrame);
        BufferedWriter bf = null;
        try {
            if(conf.get("overwrite_files").equals("yes")) {
                for(File file: targFolder.listFiles()) file.delete();
            }
            File cf = new File(getAppFolder() + appName + ".conf");
            cf.createNewFile();
            
            bf = new BufferedWriter(new FileWriter(cf));
            conf.store(bf, "Jvgen-" + new Date());
        
            setContentSpec(theFrame);
        }
        catch (Exception e) { e.printStackTrace(); }
        finally {
            try{ if(bf != null) bf.close(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void setContentSpec(JvxMainFrame theFrame) {
        try {
            theFrame.dlgHelper.dumpTreeToFile(appFolder + appName + ".tree");
            theFrame.dlgHelper.dumpDialogToFile(appFolder + "dialog" + ".tree");
        } catch (IOException ex) {
            Logger.getLogger(JvxConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setMisc(JvxMainFrame theFrame) {
        conf.put("destination", getAppFolder());
        conf.put("cpsrc", "data");
    }

    private void setTargetSpec(JvxMainFrame theFrame) {
        conf.put("project", appName);
        for(Object k : conf.keySet()) {
            conf.put(k, conf.get(k));       // easy way for now...
        }
        String[] rs = theFrame.getRecognizers();
        for(String r : rs) {
            conf.put("recognizer_"+r, "true");
        }
        
        rs = theFrame.getSynthesizers();
        for(String r : rs) {
            conf.put("synthesizer_"+r, "true");
        }
        conf.put("console", theFrame.getCbConsole() ? "true" : "false");
    }

    private boolean validatefields(JvxMainFrame theFrame) {
        if(appName == null || appName.equals("")) return false;
        return true;
    }


    public Properties getConf() {
        return conf;
    }

    public void setConf(Properties conf) {
        this.conf = conf;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
    
}
