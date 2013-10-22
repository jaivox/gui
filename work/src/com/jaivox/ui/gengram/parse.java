package com.jaivox.ui.gengram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;


public class parse {

	static final String penntags = "data/penntags.txt";

	TreeMap <String, String> tags;
	ArrayList <String> statements;
	
	int testlimit = 10;
	LexicalizedParser lp;
	
	boolean Valid = false;
	
	TreeMap <String, sentence> sentences;

	public parse (String filename) {
		tags = new TreeMap <String, String> ();
		statements = new ArrayList <String> ();
		sentences = new TreeMap <String, sentence> ();
		boolean ok = loadtags ();
		if (!ok) return;
		ok = loadstatements (filename);
		if (!ok) return;
		ok = loadparser ();
		if (!ok) return;
		Valid = true;
	}

	void Debug (String s) {
		System.out.println ("[parse]" + s);
	}

	boolean loadtags () {
		try {
			BufferedReader in = new BufferedReader (new FileReader (penntags));
			String line;
			while ((line = in.readLine ()) != null) {
				if (line.startsWith ("%")) continue;
				StringTokenizer st = new StringTokenizer (line, "\t");
				if (st.countTokens () != 3) {
					Debug ("invalid tag line: "+line);
					continue;
				}
				String number = st.nextToken ();
				String tag = st.nextToken ().trim ();
				String description = st.nextToken ().trim ();
				tags.put (tag, description);
			}
			in.close ();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace ();
			return false;
		}
	}
	
	boolean loadstatements (String filename) {
		try {
			BufferedReader in = new BufferedReader (new FileReader (filename));
			String line;
			while ((line = in.readLine ()) != null) {
				String trim = line.trim ();
				if (trim.length () == 0) continue;
				StringTokenizer st = new StringTokenizer (trim, "()\r\n");
				while (st.hasMoreTokens ()) {
					String token = st.nextToken ().trim ();
					if (token.length () == 0) continue;
					if (!token.endsWith ("?") && !token.endsWith (".")) {
						token = token + ".";
					}
					Debug ("Adding statement: "+token);
					statements.add (token);
				}
			}
			in.close ();
			return true;
		}
		catch (Exception e) {
			e.printStackTrace ();
			return false;
		}
	}
	
	boolean loadparser () {
		try {
			String optionargs [] = new String [2];
			optionargs [0] = "-outputFormat";
			optionargs [1] = "penn";
			lp = LexicalizedParser.loadModel ("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz", optionargs);
			Debug ("Parser initialized");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace ();
			return false;
		}
	}
	
	boolean createsentences () {
		try {
			for (String statement: statements) {
				sentence sent = doparse (statement);
				if (sent != null) sentences.put (sent.orig, sent);
			}
			return true;
		}
		catch (Exception e) {
			e.printStackTrace ();
			return false;
		}
	}
			
	sentence doparse (String statement) {
		try {
			Tree ptree = lp.parse (statement);
			if (ptree == null) return null;
			String flat = ptree.toString ();
			// Debug (statement+":"+flat);
			String filter = filtertags (flat);
			String form = createform (filter);
			// Debug (statement+":1 "+flat+":2 "+filter+":3 "+form);
			if (form == null) return null;
			sentence s = new sentence (statement, form, flat);
			if (!s.Valid) return null;
			// Debug (statement+"\n"+form);
			return s;
		}
		catch (Exception e) {
			e.printStackTrace ();
			return null;
		}
	}

	String filtertags (String parse) {
		StringBuffer sb = new StringBuffer ();
		StringTokenizer st = new StringTokenizer (parse, "() \t\r\n");
		while (st.hasMoreTokens ()) {
			String token = st.nextToken ().trim ();
			if (token.length () == 0) continue;
			sb.append (token + " ");
		}
		String all = new String (sb).trim ();
		return all;
	}

	String createform (String filtered) {
		if (filtered == null) return null;
		if (filtered.trim ().length () == 0) return null;
		
		StringTokenizer st = new StringTokenizer (filtered);
		int n = st.countTokens ();
		String toks [] = new String [n];
		for (int i=0; i<n; i++) {
			toks [i] = st.nextToken ();
		}
		StringBuffer sb = new StringBuffer ();
		for (int i=0; i<n-1; i++) {
			String before = toks [i];
			String after = toks [i+1];
			if (!allcaps (before)) continue;
			if (!allcaps (after)) {
				sb.append (before);
				sb.append (' ');
			}
		}

		String form = new String (sb).trim ();
		return form;
	}
	
	boolean allcaps (String word) {
		for (int i=0; i<word.length (); i++) {
			char c = (char)word.charAt (i);
			if (c < 'A' || c > 'Z') return false;
		}
		return true;
	}


}


