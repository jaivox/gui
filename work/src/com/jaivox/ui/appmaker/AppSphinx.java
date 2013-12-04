
package com.jaivox.ui.appmaker;

import com.jaivox.interpreter.Command;
import com.jaivox.interpreter.Interact;
import com.jaivox.synthesizer.web.Synthesizer;
import com.jaivox.util.Log;
import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AppSphinx extends JvxApp {

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
                speechFile = kv.getProperty("speech_file");
	
		//processSpeech ();
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
                config = kv.getProperty("appfolder") + config;
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
    
    public void processSpeech (String speech) {
        ConfigurationManager cm = null;
        Log.info ("Loading...");
        URL audioURL = null;
        try {
            cm = new ConfigurationManager (new File(config).toURI().toURL());
        // allocate the recognizer
            audioURL = new File("test.wav").toURI().toURL();
        } catch (MalformedURLException ex) {
            Logger.getLogger(AppSphinx.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        firePropertyChange("info", "initializing Sphinx...");
            
        Recognizer recognizer = null;
        try {
            recognizer = (Recognizer) cm.lookup ("recognizer");
            recognizer.allocate ();
        
            AudioFileDataSource dataSource = (AudioFileDataSource) cm.lookup("audioFileDataSource");
            dataSource.setAudioFile(audioURL, null);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        System.out.println ("Sample questions are in lm_training_file");

        try {
            firePropertyChange("result", "sending: " + speech +" ...");
            
            Result result = recognizer.recognize ();
            String recognized = null;
            String response = null;

            if (result != null) {
                    recognized = result.getBestResultNoFiller ();
                    firePropertyChange("result", "recognized: " + recognized);
                    
                    System.out.println ("You said: " + recognized + '\n');
            } else {
                    System.out.println ("I can't hear what you said.");
                    firePropertyChange("result", "I can't hear what you said.");
            }
            if (recognized != null) {
                    response = inter.execute (recognized);
                    firePropertyChange("result", "reply: " + response);
                    
                    System.out.println ("Reply: " + response);
                    Thread.sleep (4000);
                    speaker.speak (response);
            }
        } catch (Exception e) {
                e.printStackTrace ();
        }
    }
    public void speak(String speech) {
        speaker.speak(speech);
    }
}
