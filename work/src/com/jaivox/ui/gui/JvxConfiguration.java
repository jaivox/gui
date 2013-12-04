/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaivox.ui.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author lin
 */
public class JvxConfiguration {
    private static JvxConfiguration theConfig = null;
    public static String datadir = "work/apps/common/";
    private static String appFolder = "work/apps/";
    public static String WNconfig = "work/config/file_properties.xml";
    
    private static Properties conf = null; //new Properties();
    private static String appName = "";
    
    public synchronized  static JvxConfiguration theConfig() {
        return theConfig != null ? theConfig : newconfig();
    }
    private static JvxConfiguration newconfig() { 
        theConfig = new JvxConfiguration(); 
        return theConfig;
    }
    
    private JvxConfiguration() {
        final String pat= "(\\{[a-zA-Z0-9]*\\})";
        final Pattern pattern = Pattern.compile(pat);
                        
        conf = new Properties() {
            @Override
            public Object put(Object key, Object value) {
                String o = null;
                String v = (String)value;
                
                
                Matcher matcher = pattern.matcher(v);
                // check all occurance
                while (matcher.find()) {
                    String t = matcher.group();
                    String st = t.substring(1, t.length() - 1);
                    
                    if(st.trim().length() > 0) o = (String) get(st);
                    if(o != null && o.trim().length() > 0) {
                        v = v.replace(t, o);
                    }
                }
                value = v;
                
                return super.put(key, value);
            }
        };
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(datadir + "template.conf"));
            conf.load(bf);
        } catch (FileNotFoundException ex) {
            Reader reader = new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream(datadir + "template.conf"));
            bf = new BufferedReader(reader);
        } catch (Exception e) { e.printStackTrace(); }
        finally {
            try{ if(bf != null) bf.close(); } catch (Exception ex) { ex.printStackTrace(); }
        }
        setDataFolder(datadir);
        setAppFolder(appFolder);
    }
    
    public String getConfFile() {
        return getAppFolder() + appName + ".conf";
    }
    void save(JvxMainFrame theFrame) {
        if(appName == null) return;
        File targFolder = new File(appFolder);
        targFolder.mkdirs();
        System.out.println("save: path: "+ targFolder.getAbsolutePath());
        
		File Datadir = new File (datadir);
		String absolute = Datadir.getAbsolutePath ();
        conf.put("common", absolute);
        conf.put("source", getAppFolder ());
		conf.put ("Base", getAppFolder ());
		conf.put ("destination", getAppFolder ());
		conf.put ("appfolder", getAppFolder ());
		conf.put ("onedirectory", "yes");
        
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
    //java 7
    public static void copyFile(String s, String t) throws IOException {
        File src = new File(s);
        File targ = new File(t);
        Files.copy(src.toPath(), targ.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
    
    private void setContentSpec(JvxMainFrame theFrame) {
        try {
            theFrame.dlgHelper.dumpTreeToFile(appFolder + appName + ".tree");
            theFrame.dlgHelper.dumpDialogToFile(appFolder + "dialog" + ".tree");
            theFrame.dlgHelper.dumpUserSynonyms(appFolder + appName);
            theFrame.dlgHelper.dumpSynonymSelections(appFolder + appName);
            copyFile(appFolder + "dialog" + ".tree", appFolder + appName + ".sav");
        } catch (IOException ex) {
            Logger.getLogger(JvxConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setMisc(JvxMainFrame theFrame) {
        // conf.put("destination", getAppFolder());
        conf.put("cpsrc", datadir);
    }

    private void setTargetSpec(JvxMainFrame theFrame) {
        conf.put("project", appName);
        for(Object k : conf.keySet()) {
            conf.put(k, conf.get(k));       // easy way for now...
        }
        
        String rs = theFrame.getRecognizer();
        if(rs != null) conf.put ("recognizer", rs);
        
        rs = theFrame.getSynthesizer();
        if(rs != null) conf.put ("synthesizer", rs);

        String asr = theFrame.getAsrLanguage ();
        conf.put ("lang", asr);
        String tts = theFrame.getTtsLanguage ();
        conf.put ("ttslang", tts);

        if(theFrame.getCbConsole()) conf.put("input", "console");
        if(!theFrame.getCbSphinx()) conf.remove("lm_training_file");
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
    public String getAppFolder() {
        return conf.getProperty("app_folder");
    }
    public void setAppFolder(String s) {
        conf.put("app_folder", s);
        appFolder = s;
    }
    
    public void setAppName1(String s) {
        conf.put("app_name", s);
        appName = s;
    }
    public String getDialogFile() {
        return conf.getProperty("dlg_tree_file");
    }
    public void setDialogFile(String s) {
        conf.put("dlg_tree_file", s);
    }
    public String getDataFile() {
        return conf.getProperty("dlg_data_file");
    }
    public void setDataFile(String s) {
        conf.put("dlg_data_file", s);
    }
    public String getDataFolder() {
        return conf.getProperty("data_folder");
    }
    public void setDataFolder(String s) {
        conf.put("data_folder", s);
        datadir = s;
    }
    public String getDoNotExpandWords() {
        return conf.getProperty("do_not_expand");
    }    
    
    /// Help
    static Locale locale = new Locale("en", "US");
    static String helpDirectory = "/work/data/help/";
    static Properties helpTips = new Properties();
    
    static String getResourceValue(String key) {
        String val = null;
        val = helpTips.getProperty(key);
        if(val != null) return val;
        try {
            String workingDirectory = System.getProperty ("user.dir");
            File file = new File(workingDirectory + helpDirectory 
                            +"/"+ locale.getLanguage() +"/"+ key);
            if(file.exists()) {
                Properties p = new Properties();
                p.load(new FileInputStream(file));
                val = p.getProperty("tip");
                if(val != null) helpTips.put(key, val);
            }
        } catch (Exception ex) {
            Logger.getLogger(JvxConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
        return val;
    }
    public static String getHelpText(String key) {
        String tip = null;
        try {
            tip = getResourceValue(key);
        } catch (Exception e) {
            return null;
        }
        return tip;
    }
    public static String getHelpToolTip(String key) {
        return getHelpText(key + ".tt");
    }
    public static String getHelpURL(String key) {
        String hfile = "allHelp.html";
        if(key != null && key.trim().length() > 0) {
            key = key + ".html";
            String workingDirectory = System.getProperty ("user.dir");
            File file = new File(workingDirectory + helpDirectory 
                            +"/"+ locale.getLanguage() +"/"+ key);
            if(file.exists()) {
                hfile = key;
            }
        }
        return locale.getLanguage() +"/"+ hfile;
    }
    
    public static void main(String[] args) {
        String pat= "(\\{[a-zA-Z0-9]*\\})";
        String s = "{appfolder}{project}.sent";
        //s = s.replaceAll(pat, "test");
        System.out.println(s);
        
        Pattern pattern = Pattern.compile(pat);
        Matcher matcher = pattern.matcher(s);
        // check all occurance
        while (matcher.find()) {
          System.out.print("Start index: " + matcher.start());
          System.out.print(" End index: " + matcher.end() + " ");
          System.out.println(matcher.group());
        }
        
        
    }
}
