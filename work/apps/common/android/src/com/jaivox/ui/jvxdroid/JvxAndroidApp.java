package com.jaivox.ui.jvxdroid;

public interface JvxAndroidApp {
	public String getAppName();
	public void init();
	public void createApp(String basedir);
	public String processSpeech (String speech);
	public String getBaseDirectory();
}
