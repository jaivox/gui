package com.jaivox.ui.android;

import java.util.ArrayList;

import com.jaivox.ui.jvxdroid.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NativeSpeechFragment extends JvxAppFragment {
	
	final int REQUEST_CODE  = 1;
	
	public NativeSpeechFragment() {
		super();
	}
	public void init() {
		Button b = (Button) rootView.findViewById(R.id.btnSend);
        b.setOnClickListener(this);
        
        b = (Button) rootView.findViewById(R.id.btnClear);
        b.setOnClickListener(this);
        
        b = (Button) rootView.findViewById(R.id.btnSpeak);
        b.setOnClickListener(this);
	}
	void initTTS(String lang) {
		if(tts == null) tts = new AndroidTTS(lang, rootView.getContext());
	}
	public void clearButtonClicked(View view) {
		EditText st = (EditText) rootView.findViewById(R.id.txtSpeech);
		st.setText("");
	}
	public void sendButtonClicked(View view) {
		EditText st = (EditText) rootView.findViewById(R.id.txtSpeech);
		String speech = st.getText().toString();
		//speak(speech);
		
		if(speech.trim().length() > 0 ) {
			String result = processSpeech(speech);
			Log.i(this.getClass().getSimpleName(), "speech-result: "+ speech +" - "+ result);
			
			if(result != null) {
				TextView txtResult = (TextView) rootView.findViewById(R.id.txtResult);
				txtResult.setText(result == null ? "" : result);
				speak(result);
			}
		}
	}
	public void speakButtonClicked(View view) {
		Intent intent = new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM); //TODO lang
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please ask your Question...");
		
        try {
        	startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
        	Log.e(this.getClass().getSimpleName(), a.getMessage());
            Toast.makeText(rootView.getContext(), "Error initializing native Speech Recognizer!", Toast.LENGTH_LONG).show();
        }
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
        case R.id.btnSend:
        	sendButtonClicked(v);
        	break;
        case R.id.btnClear:
        	clearButtonClicked(v);
        	break;
        case R.id.btnSpeak:
        	speakButtonClicked(v);
        	break;
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case REQUEST_CODE: 
            if (resultCode == Activity.RESULT_OK && null != data) {
 
                ArrayList<String> text = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
 
                EditText st = (EditText) rootView.findViewById(R.id.txtSpeech);
        		st.setText(text.get(0));
            break;
        }
 
        }
    }
    
	protected int getFragmentId() {
		return R.layout.fragment_jvx_native;
	}
}
