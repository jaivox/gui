package com.jaivox.ui.gengram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;

import com.jaivox.util.Log;


public class Parse {

	static String penntags = "penn.txt";
	static String breaks = "~`!@#$%^&*()_+={}[]|\\:;\"<>,.?/ \t\r\n";

	static TreeMap <String, String> tags;
	ArrayList <String> statements;
	
	int testlimit = 10;
	LexicalizedParser lp = null;
	
	boolean Valid = false;
	
	TreeMap <String, Sentence> sentences;

	public Parse (String filename) {
		tags = new TreeMap <String, String> ();
		statements = new ArrayList <String> ();
		sentences = new TreeMap <String, Sentence> ();
		boolean ok = loadtags ();
		if (!ok) {
			Debug ("Could not load tags");
			return;
		}
		ok = loadstatements (filename);
		if (!ok) {
			Debug ("Could not load statements");
			return;
		}
		if(lp == null) {
			ok = loadparser ();
			if (!ok) Debug ("Could not load parser");
		}
		if (!ok) return;
		Debug ("Parser is valid");
		Valid = true;
	}
    
	public Parse () {
		tags = new TreeMap <String, String> ();
		statements = new ArrayList <String> ();
		sentences = new TreeMap <String, Sentence> ();
		boolean ok = loadtags ();
		if (!ok) {
			Debug ("Could not load tags");
			return;
		}
		if(lp == null) {
			ok = loadparser ();
			if (!ok) Debug ("Could not load parser");
		}
		if (!ok) return;
		Valid = true;
	}
	void Debug (String s) {
		Log.finest ("[parse]" + s);
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
				Sentence sent = doparse (statement);
				if (sent != null) sentences.put (sent.orig, sent);
			}
			return true;
		}
		catch (Exception e) {
			e.printStackTrace ();
			return false;
		}
	}
			
	Sentence doparse (String statement) {
		try {
			Tree ptree = lp.parse (statement);
			if (ptree == null) return null;
			String flat = ptree.toString ();
			StringTokenizer st = new StringTokenizer (statement, breaks);
			int n = st.countTokens ();
			String words [] = new String [n];
			for (int i=0; i<n; i++) {
				words [i] = st.nextToken ();
			}
			// find the tag-word pairs
			String pairs [] = getPairs (flat);
			if (pairs == null) {
				Debug ("Could not get pairs from "+flat);
				return null;
			}
			int m = pairs.length;
			
			// split the pairs
			String vpairs [][] = new String [m][];
			for (int i=0; i<m; i++) {
				vpairs [i] = pairs [i].split (" ");
				if (vpairs [i].length != 2) {
					Debug ("Pair "+pairs [i]+" does not have two tokens");
					return null;
				}
			}

			StringBuffer sb = new StringBuffer ();
			int ip = -1;
			outer: for (int i=0; i<n; i++) {
				String word = words [i];
				// find pair ending with word
				for (int j=ip+1; j<m; j++) {
					String [] vpair = vpairs [j];
					if (vpair [1].equals (word)) {
						sb.append (vpair [0]+" ");
						ip = j;
						continue outer;
					}
				}
				sb.append ("XX ");
			}
			String all = new String (sb).trim ();
			Sentence S = new Sentence (statement, all, flat);
			Debug (statement+"\n"+flat+"\n"+all);		
			return S;
		}
		catch (Exception e) {
			e.printStackTrace ();
			return null;
		}
	}
	
	String [] getPairs (String flat) {
		ArrayList <String> hold = new ArrayList <String> ();
		StringBuffer sb = new StringBuffer ();
		char chars [] = flat.toCharArray ();
		int n = chars.length;
		int left = -1;
		for (int i=0; i<n; i++) {
			char c = chars [i];
			if (c == '(') {
				left = i;
				continue;
			}
			if (c == ')') {
				if (left != -1) {
					String s = flat.substring (left+1, i);
					hold.add (s);
					sb.append (s);
					sb.append (" | ");
					left = -1;
					continue;
				}
				
			}
		}
		int m = hold.size ();
		String pairs [] = hold.toArray (new String [m]);
		String all = new String (sb);
		Debug ("pairs: "+all);
		return pairs;
	}
	
}


