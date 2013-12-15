
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
