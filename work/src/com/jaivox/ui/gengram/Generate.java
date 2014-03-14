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

package com.jaivox.ui.gengram;

import java.util.Set;
import java.util.TreeMap;

public class Generate {

	static Parse P;
	static WnLink W;
	static String tests[];

	public Generate (String filename) {
		P = new Parse (filename);
		if (P.Valid) {
			P.createsentences ();
		}
		W = new WnLinkJWNL ();
		//W = new wnlinkDb ();
		W.createsyns ();
		W.addtablecolumn ("work/apps/common/road1.data", ",\r\n", 3, 0);
		// W.test ("house");

		TreeMap<String, Sentence> sentences = P.sentences;
		Set<String> keys = sentences.keySet ();
		int n = keys.size ();
		tests = keys.toArray (new String[n]);
		for (int i = 0; i < n; i++) {
			String key = tests[i];
			Sentence s = sentences.get (key);
			// s.show (""+i+" ");
			// s.findmultiwords (W);
			s.multiwordsubs (P, W);
		}

		// Generate using okays instead of subs
		for (int i = 0; i < n; i++) {
			String key = tests[i];
			Sentence s = sentences.get (key);
			Debug ("Sentence " + i + " Generating okays for: " + key);
			s.generateokays ();
		}
	}

	static void Debug (String s) {
		System.out.println ("[generate]" + s);
	}

	public static void main (String args[]) {
		//new Generate (args [0]);
		Parse.penntags = "work/apps/common/" + Parse.penntags;
		new Generate ("work/apps/common/road1.tree");
	}
}
