package com.jaivox.ui.dlg;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;


// this is the simple list format with tab
/*
(are the freeways smooth at this time?) (that is the case.)
	(is "Old Mill Road" smooth?) (no it is not.)
		(is "Dearborn Court" quick?) (yes it is.)
			(what else?) (for example "Skidmore Place".)
(are the roads slow?) (that seems to be true.)
	(is "Elmwood Avenue" slow?) (no it seems to be quick.)
		(how about "15th Avenue"?) (that road is slow.)
*/

public class ListData {

	public static String comment = "//";

	String lines [];
	int levels [];
	int N;
	QAnode Root;

	public ListData (String filename) {
		Root = new QAnode ();
		ArrayList <QAnode> rules = readTabbedData (filename);
		Root.setFollowups (rules);
	}
		public QAnode getRoot() {
    return Root;
  }
	void Debug (String s) {
		System.out.println ("[ListData]"+s);
	}
	
	public ArrayList <QAnode> readTabbedData (String filename) {
		try {
			BufferedReader in = new BufferedReader (new FileReader (filename));
			ArrayList <String> hold = new ArrayList <> ();
			String line;
			while ((line = in.readLine ()) != null) {
				String trim = new String (line).trim ();
				if (trim.startsWith (comment)) continue;
				if (trim.length () == 0) continue; // remove blank lines
				hold.add (line);
			}
			in.close ();
			N = hold.size ();
			lines = new String [N];
			levels = new int [N];
			for (int i=0; i<N; i++) {
				line = hold.get (i);
				levels [i] = countLevels (line);
				lines [i] = line.trim ();
			}

			ArrayList <QAnode> children = new ArrayList <> ();
			for (int i=0; i<N; i++) {
				if (levels [i] == 0) {
					QAnode child = createQAnode (i, 0);
					if (child != null) children.add (child);
				}
			}
			Debug ("Read "+filename);
			return children;
		}
		catch (Exception e) {
			e.printStackTrace ();
			return null;
		}
	}
	
	int countLevels (String line) {
		int n = line.length ();
		int level = 0;
		for (int i=0; i<n; i++) {
			char c = (char)line.charAt (i);
			if (c != '\t') return level;
			else level++;
		}
		return 0;
	}

	QAnode createQAnode (int start, int level) {
		// each line is a question answer pair
		String line = lines [start];
		int p1 = line.indexOf ("(");
		int p2 = line.indexOf (")" ,p1);
		int p3 = line.indexOf ("(", p2);
		int p4 = line.indexOf (")", p3);
		if (p1 == -1) {
			Debug ("No starting ( for question in line "+start);
			Debug ("Line is "+line);
			return null;
		}
		if (p2 == -1) {
			Debug ("No ) after ( for question in line "+start);
			Debug ("Line is "+line);
			return null;
		}
		if (p3 == -1) {
			Debug ("No ( for answer in line "+start);
			Debug ("Line is "+line);
			return null;
		}
		if (p1 == -1) {
			Debug ("No ending ( for answer in line "+start);
			Debug ("Line is "+line);
			return null;
		}
		ArrayList <String> qs = new ArrayList <> ();
		ArrayList <String> as = new ArrayList <> ();
		String q = line.substring (p1+1, p2).trim ();
		qs.add (q);
		String a = line.substring (p3+1, p4).trim ();
		as.add (a);
		QAnode result = new QAnode (qs, as);
		int next = start+1;
		if (next >= N) return result;
		int nextlevel = level+1;
		if (levels [next] < nextlevel) return result;
		for (int i=next; i<N; i++) {
			if (levels [i] == nextlevel) {
				QAnode followup = createQAnode (i, nextlevel);
				if (followup != null) result.addFollowup (followup);
			}
			else if (levels [i] <= level) break;
		}
		return result;
	}
	
	void writeAsTabbed (String filename) {
		try {
			PrintWriter out = new PrintWriter (new FileWriter (filename));
			out.println ("// "+filename);
			out.println ();
			ArrayList <QAnode> rules = Root.getFollowups ();
			if (rules != null) {
				for (Iterator <QAnode> it = rules.iterator (); it.hasNext ();) {
					QAnode node = it.next ();
					out.println (node.writeAsTabbed (0));
				}
			}
			out.close ();
			Debug ("Wrote "+filename);
		}
		catch (Exception e) {
			e.printStackTrace ();
		}
	}

}


