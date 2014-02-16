package com.jaivox.ui.jvxdroid;

import java.util.Properties;

import com.jaivox.interpreter.Command;
import com.jaivox.interpreter.Interact;
import com.jaivox.util.Log;

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
		
		if(inter == null) createApp( getBaseDirectory() );
		String result = processSpeech(speech);

		TextView txtResult = (TextView) rootView.findViewById(R.id.txtResult);
		txtResult.setText(result);
		speak(result);
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
	
	///
	Interact inter;
	
	public void createApp(String basedir) {
		new Log ();
		Log.setLevelByName ("info");
		Properties kv = new Properties ();
		kv.setProperty("Base", basedir);
		kv.setProperty ("common_words", "common_en.txt");
		kv.setProperty ("questions_file", "test.quest");
		kv.setProperty ("grammar_file", "test.dlg");
		Command cmd = new Command ();
		inter = new Interact (basedir, kv, cmd);
        
		lang = kv.getProperty ("ttslang");
        initTTS(lang);
	}
	public String processSpeech (String speech) {
		String response = inter.execute (speech);
		return response;
	}
	public String getBaseDirectory() {
		return appDir;
	}
	@Override
	public String getAppName() {
		return "console";
	}
}
