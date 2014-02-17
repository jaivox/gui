package com.jaivox.ui.android;

public interface JvxAndroidApp {
	public String getAppName();
	public void init();
	public String processSpeech (String speech);
	public String getBaseDirectory();
}
