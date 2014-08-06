/*
   Jaivox Application Generator (JAG) version 0.2 March 2014
   Copyright 2010-2014 by Bits and Pixels, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

Please see work/licenses for licenses to other components included with
this package.
*/

package com.jaivox.ui.appmaker;

import com.jaivox.interpreter.Command;
import com.jaivox.interpreter.Interact;
import com.jaivox.recognizer.web.SpeechInput;
import com.jaivox.recognizer.web.Mike;
import com.jaivox.synthesizer.Synthesizer;
import com.jaivox.ui.gui.JvxMainFrame;
import java.util.Properties;

public class AppWeb extends JvxApp {
        
    String project = "test";
	String basedir = "./";
	String type = ".wav";
	Interact inter;
	Synthesizer speaker;
	String asrLang = "en-US";
	static String apiKey = "";
	static int wait = 10; // maximum length of input in seconds
        
	public AppWeb (Properties kv) {
		// Log.setLevelByName (kv.getProperty ("log_level"));
		initializeInterpreter (kv);
		System.out.println ("Interpreter initialized, starting recognition");
		//processSpeech ();
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
		apiKey = kv.getProperty ("googleapikey");
		if (apiKey == null) apiKey = JvxMainFrame.apiKey;
		if (apiKey == null) apiKey = "";
        // speaker will get ttsLang = kv.getProperty ("ttslang");
		Command cmd = new Command ();
		inter = new Interact (basedir, kv, cmd);
		// String tts = kv.getProperty ("synthesizer").toLowerCase ();
		// System.out.println ("Synthesizer is "+tts);
		// if (tts.equals ("espeak"))
		String synth = kv.getProperty ("synthesizer");
		if (synth.equals ("espeak")) {
			speaker = new com.jaivox.synthesizer.espeak.Synthesizer (kv);
		}
		else if (synth.equals ("freetts")) {			
			speaker = new com.jaivox.synthesizer.freetts.Synthesizer (kv);
		}
		else { // if synth.equals ("web")) {
			speaker = new com.jaivox.synthesizer.web.Synthesizer (kv);
		}
	}

	void processSpeech () {
		SpeechInput R = new SpeechInput (apiKey);
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
        
    public void processSpeech (String speech) {
        speech = RecordTask.wavToflac(speech);
        SpeechInput R = new SpeechInput (apiKey);
        int empty = 0;
        int maxempty = 5;
        if (speech != null) {
            firePropertyChange("info", "sending: " + speech +" ...");
            String result = R.recognize (speech, asrLang);
            System.out.println ("result:" + result);
            firePropertyChange("info", "recognized: " + result);
            
            if(isCancelled()) return;
            
            String response = "";
            if (result != null) {
                    if (result.trim ().equals ("")) {
                            empty++;
                            if (empty >= maxempty) return;
                    }
                    
                    response = inter.execute (result);
                    
                    System.out.println ("Reply: " + response);
                    firePropertyChange("result", response);
            }
            if(isCancelled()) return;
            
            try {
                Thread.sleep (4000);
                // can't always play flac
                // speaker.speak (response);
            } catch (Exception e) {
                    e.printStackTrace ();
                    return;
            }
        }
    }

    public static boolean testSpeech (String speech) {
        // speech = RecordTask.wavToflac(speech);
        SpeechInput R = new SpeechInput (JvxMainFrame.apiKey);
        int empty = 0;
        int maxempty = 5;
        if (speech != null) {
            String result = R.recognize (speech, "en-US");
            System.out.println ("google api result:" + result);
            if (result.trim ().length () > 0) return true;
			else return false;
        }
		else return false;
    }

	
	public void speak(String speech) {
        speaker.speak(speech);
    }
}
