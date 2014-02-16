package com.jaivox.ui.jvxdroid;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class JvxAppFragment extends Fragment implements JvxAndroidApp, OnClickListener {
	protected View rootView = null;
	protected String appDir = "";
	protected String lang = "";
	
	protected AndroidTTS  tts = null;
	
	void syncAssets(Context context, String path) {
		try {
            File f = AndroidFileUtil.syncAssets(context, path);
            appDir = new File(f, getAppName()).getPath();
            if(!appDir.endsWith("/")) appDir = appDir + "/";
            Log.e(this.getClass().getSimpleName(), appDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(getFragmentId(), container, false);
		syncAssets(rootView.getContext(), getAppName());
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
		new Thread() {
			public void run() {
				tts.speak(txt);
			}
		}.start();
	}
	
	protected abstract int getFragmentId();
}
