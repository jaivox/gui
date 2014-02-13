package com.jaivox.ui.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import android.content.Context;
import android.util.Log;
import com.jaivox.interpreter.Command;
import com.jaivox.interpreter.Interact;

public class JvxInteract {
	Interact inter;
	String lang = null; 
	
	public String syncAssets(String path, Context context) {
		String appDir = null;
		try {
            File f = AndroidFileUtil.syncAssets(context, path);
            appDir = new File(f, path).getPath();
            if(!appDir.endsWith("/")) appDir = appDir + "/";
            Log.e(this.getClass().getSimpleName(), appDir);
        } catch (IOException e) {
        	e.printStackTrace();
        	return null;
        }
		return appDir;
	}
	
	public void createApp(Context c) throws IOException {
		new com.jaivox.util.Log ();
		com.jaivox.util.Log.setLevelByName ("info");
                
                String appname = "PATproject";
		String basedir = syncAssets(appname, c);
		
		Properties kv = new Properties ();
		kv.load( new FileInputStream(new File(basedir, appname+".conf").getPath()) );
		String app = kv.getProperty("project");
		kv.setProperty("Base", basedir);
		kv.setProperty ("common_words", "common_en.txt");
		kv.setProperty ("questions_file", app + ".quest");
		kv.setProperty ("grammar_file", app + ".dlg");
		//if( !appname.equals("app) )
                
		Command cmd = new Command ();
		inter = new Interact (basedir, kv, cmd);
        
		lang = kv.getProperty ("ttslang");
	}
	public String getLanguage() {
		return lang;
	}
	public String processSpeech (String speech) {
		if(inter == null) return null;
		String response = inter.execute (speech);
		return response;
	}
}
