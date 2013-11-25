
package com.jaivox.ui.appmaker;

import com.jaivox.interpreter.Command;
import com.jaivox.interpreter.Interact;
import com.jaivox.recognizer.web.SpeechInput;
import com.jaivox.recognizer.web.Mike;
import com.jaivox.synthesizer.web.Synthesizer;
import com.jaivox.util.Log;
import java.util.Properties;

public class AppWeb {

	String project = "test";
	String basedir = "./";
	String type = ".wav";
	Interact inter;
	Synthesizer speaker;
	String asrLang = "en-US";
	static int wait = 10; // maximum length of input in seconds

	public AppWeb (Properties kv) {
		// Log.setLevelByName (kv.getProperty ("log_level"));
		initializeInterpreter (kv);
		System.out.println ("Interpreter initialized, starting recognition");
		processSpeech ();
	}

	void initializeInterpreter (Properties kv) {
		/*
		Properties kv = new Properties ();
		kv.setProperty ("common_words", "common_en.txt");
		kv.setProperty ("questions_file", "test.quest");
		kv.setProperty ("grammar_file", "test.dlg");
		kv.setProperty ("ttslang", "en");
		*/
		basedir = kv.getProperty ("Base");
		project = kv.getProperty ("project");
		asrLang = kv.getProperty ("lang");
		// speaker will get ttsLang = kv.getProperty ("ttslang");
		Command cmd = new Command ();
		inter = new Interact (basedir, kv, cmd);
		speaker = new Synthesizer (kv);
	}

	void processSpeech () {
		SpeechInput R = new SpeechInput ();
		Mike mike = new Mike (project, type);
		int empty = 0;
		int maxempty = 5;
		while (GuiPrep.Running) {
			System.out.println ("Speak, wait for 10 seconds for processing ...");
			String flac = mike.nextsample (type, wait);
			mike.showtime ("result is "+flac);
			if (flac != null) {
				String result = R.recognize (flac, asrLang);
				System.out.println ("Recognized: " + result);
				String response = "";
				if (result != null) {
					if (result.trim ().equals ("")) {
						empty++;
						if (empty >= maxempty) return;
					}
					response = inter.execute (result);
					System.out.println ("Reply: " + response);
				}
				try {
					Thread.sleep (4000);
					// can't always play flac
					speaker.speak (response);
				} catch (Exception e) {
					e.printStackTrace ();
					break;
				}
			}
		}
	}
}

