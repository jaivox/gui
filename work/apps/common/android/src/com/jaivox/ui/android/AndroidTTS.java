package com.jaivox.ui.android;

import java.util.Locale;
import android.content.Context;
import android.speech.tts.TextToSpeech;

import android.util.Log;

public class AndroidTTS extends com.jaivox.synthesizer.Synthesizer 
									implements TextToSpeech.OnInitListener {
	private TextToSpeech tts;
	Locale loc = null;
    
	public AndroidTTS (String lang, Context c) {
		loc = lang == null ? Locale.getDefault() : new Locale(lang);
		initializeTts (c);
		Log.i("AndroidSynthesizer", "Synthesizer created: " + tts.getLanguage());
	}
	private void initializeTts(Context c) {
		tts = new TextToSpeech(c, this);
	}
	public boolean speak (String message) {
		tts.speak(message, TextToSpeech.QUEUE_ADD, null);
		return true;
	}
	public boolean speak (String message, final int qmode) {
		tts.speak(message, qmode, null);
		return true;
	}
	@Override
	public void onInit(int stat) {
		if (stat == TextToSpeech.SUCCESS) {
			 
            int result = tts.setLanguage(loc);
 
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("AndroidSynthesizer", "This Language is not supported");
            } 
            
        } else {
            Log.e("AndroidSynthesizer", "Initilization Failed!");
        }
	}
	public void stop() {
		tts.stop();
		tts.shutdown();
	}
}
