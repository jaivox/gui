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
/*
 Jaivox version 0.5 August 2013
 Copyright 2010-2013 by Bits and Pixels, Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

/**
 * consoleTest can be used to test only the interpreter, without involving a
 * speech recognizer or synthesizer.
 */
package com.jaivox.ui.appmaker;

import com.jaivox.interpreter.Command;
import com.jaivox.interpreter.Interact;
import com.jaivox.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

public class AppConsole extends JvxApp {

	static String basedir = "./";
	Interact inter;

	public AppConsole (Properties kv) {
		Log log = new Log ();
		log.setLevelByName ("info");
		initializeInterpreter (kv);
		//processQuestions ();
	}

	void initializeInterpreter (Properties kv) {
		basedir = kv.getProperty ("Base");
		Command cmd = new Command ();
		inter = new Interact (basedir, kv, cmd);
	}

	void processQuestions () {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
			do {
				System.out.print ("> ");
				String line = in.readLine ();
				String response = inter.execute (line);
				System.out.println (": " + response);
			} while (true);
		} catch (Exception e) {
			e.printStackTrace ();
			return;
		}
	}

	public void processSpeech (String speech) {
		System.out.println ("processSpeech: " + speech);
		String response = inter.execute (speech);
		firePropertyChange ("info", "reply: " + response);
	}

	public void speak (String speech) {
	}
}
