package com.jaivox.ui.jvxdroid;

import java.util.Locale;
import android.content.Context;
import android.speech.tts.TextToSpeech;

import android.util.Log;

public class AndroidTTS extends com.jaivox.synthesizer.Synthesizer 
									implements TextToSpeech.OnInitListener {
	private TextToSpeech tts;
    
	public AndroidTTS (String lang, Context c) {
		Log.i("AndroidSynthesizer", "Synthesizer created");
		
		initializeTts (c);
	}
	private void initializeTts(Context c) {
		tts = new TextToSpeech(c, this);
	}
	public boolean speak (String message) {
		tts.speak(message, TextToSpeech.QUEUE_ADD, null);
		return true;
	}
	@Override
	public void onInit(int stat) {
		if (stat == TextToSpeech.SUCCESS) {
			 
            int result = tts.setLanguage(Locale.US);  // TODO based on config ttslang
 
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
