package com.jaivox.ui.dlg;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;


public class TabbedData {
	
	public static String comment = "//";
	
	String lines [];
	int source [];
	int levels [];
	int N;
	QAnode Root;
	
	public TabbedData (String filename) {
		Root = new QAnode ();
		ArrayList <QAnode> children = readTabbedData (filename);
		Root.setFollowups (children);
	}
	public QAnode getRoot() {
    return Root;
  }
	void Debug (String s) {
		System.out.println ("[TabbedData]" + s);
	}
	
	void Info (String s) {
		// System.out.println ("[TabbedData]info-"+s);
	}
	
	public ArrayList <QAnode> readTabbedData (String filename) {
		try {
			BufferedReader in = new BufferedReader (new FileReader (filename));
			ArrayList <String> hold = new ArrayList <> ();
			String line;
			N = 0;
			while ((line = in.readLine ()) != null) {
				hold.add (line);
				String trim = new String (line).trim ();
				if (!trim.startsWith (comment) && trim.length () > 0) N++;
			}
			in.close ();
			lines = new String [N];
			source = new int [N];
			levels = new int [N];
			int linenum = 0;
			for (int i=0; i<hold.size (); i++) {
				line = hold.get (i);
				int level = countLevels (line);
				String trim = line.trim ();
				if (trim.startsWith (comment) || trim.length () == 0) continue;
				levels [linenum] = level;
				lines [linenum] = trim;
				source [linenum] = i+1;
				linenum++;
			}
			
			// diagnose
			for (int i=0; i<N; i++) {
				Info ("L "+source[i]+"."+i+" "+levels [i]+" "+lines [i]);
			}
			
			boolean tabsOk = tabCheck ();
			if (!tabsOk) {
				Debug ("Wrong tab indentation, stopping reading ");
			}

			ArrayList <QAnode> children = new ArrayList <> ();
			for (int i=0; i<N; i++) {
				if (levels [i] == 0) {
					QAnode child = createQAnode (i, 0);
					if (child != null) children.add (child);
					// go past the questions
					int j = i+1;
					for (; j<N; j++) {
						if (levels [j] != 0) break;
					}
					i = j - 1;
				}
			}
			Info ("Read "+filename);
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
	
	boolean tabCheck () {
		// check if increasing by one where it does
		for (int i=0; i<N-1; i++) {
			int l1 = levels [i];
			int l2 = levels [i+1];
			if (l2 > l1) {
				if (l2 != l1+1) {
					Debug ("Line "+source [i]+" has "+l1+" tabs");
					Debug ("Line "+source [i+1]+" expects "+(l1+1)+" tabs, but has "+l2);
					return false;
				}
			}
		}
		// check even decrease if decreasing
		for (int i=0; i<N-1; i++) {
			int l1 = levels [i];
			int l2 = levels [i+1];
			if (l2 < l1) {
				int dif = l1 - l2;
				if (dif%2 != 1) {
					Debug ("Line "+source [i]+" has "+l1+" tabs");
					Debug ("Line "+source [i+1]+" expects odd difference but has "+dif);
					return false;
				}
			}
		}
		return true;
		
	}
	
	QAnode createQAnode (int start, int level) {
		int astart = -1;
		int aend = -1;
		int nextlevel = level + 1;
		Info ("Create: start "+start+":"+source [start]+" level "+level+" "+lines [start]);
		for (int i=start; i<N; i++) {
			if (levels [i] < level) {
				Info ("Questions starting with "+level+" tabs need answers at next level.");
				Info ("at line "+source [i]+" found "+levels [i]+" before finding answers, returning null");
				return null;
			}
			else if (levels [i] > level) {
				astart = i;
				if (levels [i] != nextlevel) {
					Info ("Question starting at "+start+" should have answers at level "+nextlevel);
					Info ("at line "+source [i]+" found next level as "+levels [i]+" returning null");
					return null;
				}
				break;
			}
		}
		if (astart == -1) {
			Debug ("Could not find answers following line "+source [start]+" returning null");
			return null;
		}
		for (int i=astart; i<N; i++) {
			if (levels [i] != nextlevel) {
				aend = i;
				break;
			}
		}
		if (aend == -1) aend = N;
		ArrayList <String> qs = new ArrayList <> ();
		ArrayList <String> as = new ArrayList <> ();
		for (int i=start; i<astart; i++) {
			qs.add (lines [i]);
		}
		for (int i=astart; i<aend; i++) {
			as.add (lines [i]);
		}
		QAnode result = new QAnode (qs, as);
		if (aend == N) return result;
		int flevel = levels [aend];
		if (flevel < level+2) return result;
		for (int i=aend; i<N; i++) {
			if (levels [i] == level+2) {
				QAnode followup = createQAnode (i, level+2);
				if (followup != null) {
					Info ("Created start "+start+" at "+i+":"+source [i]);
					result.addFollowup (followup);
				}
				// go past the level+2
				int j = i+1;
				for (; j<N; j++) {
					if (levels [j] != level+2) break;
				}
				i = j-1;
			}
			if (levels [i] <= level) break;
		}
		return result;
	}
	
	void writeAsJson (String filename) {
		try {
			PrintWriter out = new PrintWriter (new FileWriter (filename));
			out.println ("{Root: \""+filename+"\",\n rules: [");
			ArrayList <QAnode> children = Root.getFollowups ();
			int n = children.size ();
			String space = "  ";
			for (int i=0; i<n; i++) {
				QAnode child = children.get (i);
				String s = child.writeAsJson (space, i);
				out.println (s);
			}
			out.println (" ]\n");
			out.println ("}\n");
			out.close ();
			Debug ("Wrote "+filename);
		}
		catch (Exception e) {
			e.printStackTrace ();
		}
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
	
	void showInfo () {
		String s = Root.info ();
		System.out.println (s);
	}
	
}


