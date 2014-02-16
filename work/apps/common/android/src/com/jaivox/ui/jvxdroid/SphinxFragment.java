package com.jaivox.ui.jvxdroid;

import java.io.File;
import java.util.Properties;

import com.jaivox.interpreter.Command;
import com.jaivox.interpreter.Interact;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SphinxFragment extends JvxAppFragment implements RecognitionListener {
	
	final int REQUEST_CODE  = 1;
	
	public SphinxFragment() {
		super();
	}
	public void init() {
		syncAssets(rootView.getContext(), "sphinx");	// sphinx models etc.
		
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
		speak(speech);
		
		if(inter == null) createApp( getBaseDirectory() );
		String result = processSpeech(speech);

		TextView txtResult = (TextView) rootView.findViewById(R.id.txtResult);
		txtResult.setText(result);
		speak(result);
	}
	public void speakButtonClicked(View view) {
		// 	TODO sphinx
		Button btn = (Button) rootView.findViewById(R.id.btnSpeak);
	    
		initSphinx(rootView.getContext());
		
		if(btn.getText().equals("Speak")) {
			recognizer.addListener(this);
			recognizer.startListening();
			
			btn.setText("Stop");
    	}
    	else {
    		recognizer.stopListening();
            recognizer.removeListener(this);
    		btn.setText("Speak");
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
	public void onPause() {
		super.onPause();
		if(recognizer != null) recognizer.stopListening();
		recognizer = null;
	}

	protected int getFragmentId() {
		return R.layout.fragment_jvx_native;	//
	}
	
	
	///
	static {
        System.loadLibrary("pocketsphinx_jni");
    }
    
	SpeechRecognizer recognizer = null;
	
    private static String joinPath(File dir, String path) {
        return new File(dir, path).getPath();
    }
    void initSphinx(Context c) {
    	if(recognizer != null) return;
    	
    	File base = new File(sphinxFolder());
        Config config = Decoder.defaultConfig();
        
        config.setString("-hmm", joinPath(base, "models/hmm/en_US/hub4wsj_sc_8k"));
        //config.setString("-hmm", joinPath(base, "models/hmm/en-us-semi"));
        
        //config.setString("-dict", joinPath(base, "models/lm/en_US/cmu07a.dic"));
        config.setString("-dict", joinPath(base, "models/lm/en_US/hub4.5000.dic"));
        
        //config.setString("-lm", joinPath(base, "test.arpabo.DMP"));
        config.setString("-lm", joinPath(base, "models/lm/en_US/hub4.5000.DMP"));
        
        config.setString("-rawlogdir", base.getPath());
        config.setInt("-maxhmmpf", 10000);
        config.setBoolean("-fwdflat", false);
        config.setBoolean("-bestpath", false);
        config.setFloat("-kws_threshold", 1e-5);
        
        
        recognizer = new SpeechRecognizer(config);
        
        //NGramModel lm = new NGramModel(joinPath(base, "test.arpabo.DMP"));
        //recognizer.setLm(SphinxFragment.class.getSimpleName(), lm);
    }
    String sphinxFolder() {
    	String s = getBaseDirectory();
    	int ind = s.indexOf(getAppName());
    	if(ind != -1) {
    		s = s.substring(0, ind);
    		s = s + "sphinx/";
    	}
    	//s.replace(getAppName(), "sphinx");
    	Log.i(this.getClass().getSimpleName(), s);
    	return s;
    }
    @Override
    public void onPartialResult(Hypothesis hypothesis) {
    	EditText st = (EditText) rootView.findViewById(R.id.txtSpeech);
		st.setText(hypothesis.getHypstr());
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        String speech = hypothesis.getHypstr();
        EditText st = (EditText) rootView.findViewById(R.id.txtSpeech);
		st.setText(speech);
    }

    ///
	Interact inter;
	
	public void createApp(String basedir) {
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
		return "console";		// use console for native
	}
}
