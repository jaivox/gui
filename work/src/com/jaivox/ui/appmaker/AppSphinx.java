
package com.jaivox.ui.appmaker;

import com.jaivox.interpreter.Command;
import com.jaivox.interpreter.Interact;
import com.jaivox.synthesizer.web.Synthesizer;
import com.jaivox.util.Log;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import java.util.Properties;


public class AppSphinx extends Thread {

	String project = "test";
	String basedir = "./";
	String asrLang = "en-US";
	static String config = "live.xml";
	Interact inter;
	Synthesizer speaker;

	public AppSphinx (Properties kv) {
		// Log.setLevelByName (kv.getProperty ("log_level"));
		initializeInterpreter (kv);
		if (!asrLang.equals ("en-US")) {
			Log.severe ("Sphinx recognizer implemented only for English-US");
			return;
		}
		processSpeech ();
	}

	void initializeInterpreter (Properties kv) {
		/*
		Properties kv = new Properties ();
		kv.setProperty ("data_file", "PATdata_file");
		kv.setProperty ("common_words", "PATcommon_words");
		kv.setProperty ("specs_file", "PATspecs_file");
		kv.setProperty ("questions_file", "PATquestions_file");
		kv.setProperty ("grammar_file", "PATgrammar_file");
		kv.setProperty ("language", "PATttslang");
		*/
		basedir = kv.getProperty ("Base");
		project = kv.getProperty ("project");
		asrLang = kv.getProperty ("lang");
		config = kv.getProperty ("recognizer_config_file");
		// speaker will get ttsLang = kv.getProperty ("ttslang");
		Command cmd = new Command ();
		inter = new Interact (basedir, kv, cmd);
		speaker = new Synthesizer (kv);
	}

	void processSpeech () {
		ConfigurationManager cm = new ConfigurationManager (
				AppSphinx.class.getResource (config));
		// allocate the recognizer
		Log.info ("Loading...");
		Recognizer recognizer = (Recognizer) cm.lookup ("recognizer");
		recognizer.allocate ();

		// start the microphone or exit if the programm if this is not possible
		Microphone microphone = (Microphone) cm.lookup ("microphone");
		if (!microphone.startRecording ()) {
			Log.severe ("Cannot start microphone.");
			recognizer.deallocate ();
			System.exit (1);
		}

		System.out.println ("Sample questions are in lm_training_file");

		try {
			// loop the recognition until the programm exits.
			while (GuiPrep.Running) {
				System.out.println ("Start speaking. Press Ctrl-C to quit.\n");

				Result result = recognizer.recognize ();
				String recognized = null;
				String response = null;

				if (result != null) {
					recognized = result.getBestResultNoFiller ();
					System.out.println ("You said: " + recognized + '\n');
				} else {
					System.out.println ("I can't hear what you said.");
					continue;
				}
				if (recognized != null) {
					response = inter.execute (recognized);
					System.out.println ("Reply: " + response);
					Thread.sleep (4000);
					speaker.speak (response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}
}
