package com.jaivox.ui.gengram;

import java.util.Set;
import java.util.TreeMap;

public class generate {

	static parse P;
	static wnlink W;
	
	static String tests [];

	public generate (String filename) {
		P = new parse (filename);
		if (P.Valid) P.createsentences ();
		W = new wnlink ();
		W.createsyns ();
		W.addtablecolumn ("road.data", ",\r\n", 3, 0);
		// W.test ("house");
		
		TreeMap <String, sentence> sentences = P.sentences;
		Set <String> keys = sentences.keySet ();
		int n = keys.size ();
		tests = keys.toArray (new String [n]);
		for (int i=0; i<n; i++) {
			String key = tests [i];
			sentence s = sentences.get (key);
			// s.show (""+i+" ");
			// s.findmultiwords (W);
			s.multiwordsubs (P, W);
		}

		// generate using okays instead of subs
		for (int i=0; i<n; i++) {
			String key = tests [i];
			sentence s = sentences.get (key);
			Debug ("Sentence "+i+" Generating okays for: "+key);
			s.generateokays ();
		}
	}
	
	static void Debug (String s) {
		System.out.println ("[generate]" + s);
	}

	public static void main (String args []) {
		//new generate (args [0]);
		new generate ("road.tree");
	}
}
