package com.jaivox.ui.android;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class JvxAppFragment extends Fragment implements JvxAndroidApp, OnClickListener {
	protected View rootView = null;
	protected String appDir = "";
	protected String lang = "";
	protected boolean initized = false;
	
	protected AndroidTTS  tts = null;
	
	InputStream openAssetsFile(String name) throws IOException {
		return rootView.getContext().getAssets().open(name);
	}
	InputStream openConfigFile(String path) throws IOException {
		String files[] = rootView.getContext().getAssets().list(path);
		for(String s : files) {
			if(s.endsWith(".conf")) {
				return openAssetsFile(new File(path, s).getPath());
			}
		}
		return null;
	}
	boolean syncAssets(Context context, String path) {
		try {
            File f = AndroidFileUtil.syncAssets(context, path);
            appDir = new File(f, getAppName()).getPath();
            if(!appDir.endsWith("/")) appDir = appDir + "/";
            Log.e(this.getClass().getSimpleName(), appDir);
        } catch (IOException e) {
        	e.printStackTrace();
        	onStop();
        	Toast.makeText(rootView.getContext(), "Unable to sync "+ path +" data files!", Toast.LENGTH_SHORT).show();
        	return false;
        }
		return true;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(getFragmentId(), container, false);
		createApp();
		init();
		return rootView;
	}
	@Override
	public void onStop() {
		super.onStop();
		if(tts != null) tts.stop();
		tts = null;
	}

	@Override
	public void onStart() {
		super.onStart();
		initTTS(lang);
	}
	void initTTS(String lang) {
		// let the specializations init, if needed
	}
	
	void speak(final String txt) {
		if(tts == null || txt == null || txt.trim().length() <= 0) return;
		new Thread() {
			public void run() {
				tts.speak(txt);
			}
		}.start();
	}
	
	protected abstract int getFragmentId();
	
	///
	JvxInteract intf = null;
	
	public void createApp () {
		if(intf == null) {
			intf = new JvxInteract();
			try {
				intf.createApp(rootView.getContext());
				initTTS( getLang() );
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public String processSpeech (String speech) {
		createApp();
		return intf.processSpeech(speech);
	}
	public String getBaseDirectory() {
		return appDir;
	}
	@Override
	public String getAppName() {
		return "console";
	}
	public String getLang() {
		return intf.getLanguage();
	}
}

