package com.jaivox.ui.appmaker;


import java.io.*;
import java.util.*;

public class Gui2Gram {

	static String gram = "data/road1.tree";
	static String dlgtree = "data/dlgtree.tree";
	String keys [];
	String questions [];
	LinkedHashMap <String, ArrayList<String>> rules;
	static String terms = " \t\r\n~`!@#$%^&*()+={}[]|\\:;<>,.?/\"\'";
	
	public Gui2Gram () {
		loadGram ();
		loadDlg ();
	}
	
	void Debug (String s) {
		System.out.println ("[Gui2Gram]" + s);
	}
	
	void loadGram () {
		try {
			BufferedReader in = new BufferedReader (new FileReader (gram));
			// no recursion needed here, just get the strings in ()
			rules = new LinkedHashMap <String, ArrayList<String>> ();
			String line;
			ArrayList<String> hold = new ArrayList <String> ();
			ArrayList<String> quests = new ArrayList <String> ();
			while ((line = in.readLine ()) != null) {
				String lower = line.trim ().toLowerCase ();
				if (lower.length () == 0) continue;
				int p1 = lower.indexOf ("(");
				if (p1 == -1) continue;
				int p2 = lower.indexOf (")", p1);
				if (p2 == -1) continue;
				int p3 = lower.indexOf ("(", p2);
				if (p3 == -1) continue;
				int p4 = lower.indexOf (")", p3);
				if (p4 == -1) continue;
				String first = lower.substring (p1+1, p2).trim ();
				String second = lower.substring (p3+1, p4).trim ();
				/*
				StringTokenizer st = new StringTokenizer (lower, "()");
				if (st.countTokens () < 2) continue;
				String first = st.nextToken ().trim ();
				String second = st.nextToken ().trim ();
				*/
				hold.add (first);
				quests.add (first);
				hold.add (second);
				rules.put (first, new ArrayList <String> ());
				rules.put (second, new ArrayList <String> ());
			}
			in.close ();
			int n = hold.size ();
			keys = hold.toArray (new String [n]);
			int m = quests.size ();
			questions = quests.toArray (new String [m]);
			Debug ("Added "+n+" keys, "+m+" questions");
		}
		catch (Exception e) {
			e.printStackTrace ();
		}
	}
	
	void loadDlg () {
		try {
			BufferedReader in = new BufferedReader (new FileReader (dlgtree));
			String line;
			int now = 0;
			int n = keys.length;
			String current = keys [now];
			String next = keys [now+1];
			ArrayList <String> hold = new ArrayList <String> ();
			while ((line = in.readLine ()) != null) {
				String lower = line.trim ().toLowerCase ();
				if (lower.length () == 0) continue;
				hold.add (lower);
			}
			in.close ();
			int m = hold.size ();
			String lines [] = hold.toArray (new String [m]);
			int starts [] = new int [n+1];
			starts [0] = 0;
			outer: for (int i=1; i<n; i++) {
				for (int j=0; j<m; j++) {
					line = lines [j];
					if (line.startsWith ("*")) {
						line = line.substring (1);
						if (keys [i].equals (line)) {
							starts [i] = j;
							continue outer;
						}
					}
				}
				if (starts [i] == 0) {
					Debug ("No location found for keys "+i+": "+keys [i]);
				}
			}
			starts [n] = m;
			for (int i=0; i<n; i++) {
				System.out.println (""+i+" "+starts [i]+": "+keys [i]);
			}

			for (int i=0; i<n-1; i++) {
				String key = keys [i];
				int start = starts [i];
				int end = starts [i+1];
				hold = new ArrayList <String> ();
				for (int j=start; j<end; j++) {
					hold.add (filter (lines [j]));
				}
				rules.put (key, hold);
			}
		}
		catch (Exception e) {
			e.printStackTrace ();
		}
	}
	
	static String filter (String line) {
		StringTokenizer st = new StringTokenizer (line.toLowerCase (), terms);
		StringBuffer sb = new StringBuffer ();
		while (st.hasMoreTokens ()) {
			String token = st.nextToken ();
			sb.append (token);
			if (st.hasMoreTokens ()) sb.append (' ');
		}
		String t = new String (sb).trim ();
		return t;
	}
		
	
	void writeRules (PrintWriter out) {
		try {
			Set <String> heads = rules.keySet ();
			for (Iterator<String> it = heads.iterator (); it.hasNext (); ) {
				String head = it.next ();
				String filt = filter (head);
				String key = filt.replaceAll (" ", ".");
				out.println ("{");
				out.println (key);
				out.println ("\t"+filt+" ;");
				ArrayList <String> vals = rules.get (head);
				int n = vals.size ();
				for (String val: vals) {
					if (val.startsWith ("*")) continue;
					out.println ("\t"+val+" ;");
				}
				out.println ("}\n");
			}
		}
		catch (Exception e) {
			e.printStackTrace ();
		}
	}
	
	void writeQuestions (PrintWriter out) {
		try {
			for (int i=0; i<questions.length; i++) {
				String head = questions [i];
				ArrayList <String> vals = rules.get (head);
				int n = vals.size ();
				String filt = filter (head);
				out.println (filt+"\t"+filt+"\t(_,_,_,_,_,_,_)");
				for (String val: vals) {
					if (val.startsWith ("*")) continue;
					out.println (val+"\t"+val+"\t(_,_,_,_,_,_,_)");
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace ();
		}
	}
			
		
		
}			
		
		
