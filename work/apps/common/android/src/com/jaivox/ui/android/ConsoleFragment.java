package com.jaivox.ui.android;

import com.jaivox.ui.PATproject.R;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConsoleFragment extends JvxAppFragment {
	
	public ConsoleFragment() {
		super();
	}
	public void init() {
		Button b = (Button) rootView.findViewById(R.id.btnSend);
        b.setOnClickListener(this);
        
        b = (Button) rootView.findViewById(R.id.btnClear);
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
		speak(speech);
		
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
        case R.id.btnSend:
        	sendButtonClicked(v);
        	break;
        case R.id.btnClear:
        	clearButtonClicked(v);
        	break;
		}
	}
	protected int getFragmentId() {
		return R.layout.fragment_jvx_console;
	}
}
