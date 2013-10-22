package com.jaivox.ui.appmaker;


import java.io.*;
import java.util.*;
import bitpix.list.*;

public class Rule2Fsm {

	static String dir = "./";
	basicTree tree;
	TreeMap <String, String> states;
	TreeMap <String, String> tags;
	
	static String name = "data/road1.tree";
	static String yes = "yes";
	String startState = "def";
	static String casedefault = "(default) (def)";
	static basicNode casedefaultnode;
	Vector <String> store;

	public Rule2Fsm () {
		String filename = dir + name;
		startState = startState;
		tree = new basicTree (filename);
		// tree.WriteTree ();
		states = new TreeMap <String, String> ();
		tags = new TreeMap <String, String> ();
		Vector <bitpix.list.basicNode> list = tree.Root.ListChild;
		casedefaultnode = new basicNode (casedefault);
		store = new Vector <String> ();
		store.add ("\n#include errors.dlg\n");
		for (int i=0; i<list.size (); i++) {
			basicNode child = list.elementAt (i);
			gt (child, startState);
		}
		int pos = filename.lastIndexOf (".");
		String outfile = filename.substring (0, pos+1) + "dlg";
		// writefile (outfile, store);
	}

	void Debug (String s) {
		System.out.println ("[Rule2Fsm]" + s);
	}

	void gt (basicNode node, String sofar) {
		Vector <bitpix.list.basicNode> list = node.ListChild;
		if (list == null || list.size () == 0) {
			// emit a state with def
			emit (node, sofar, "def");
		}
		else {
			String nextstate = createNextState (node);
			String morefar = sofar + " " + nextstate;
			emit (node, sofar, nextstate);
			list.add (casedefaultnode);
			for (int i=0; i<list.size (); i++) {
				basicNode child = list.elementAt (i);
				gt (child, morefar);
			}
		}
	}

	void emit (basicNode node, String sofar, String next) {
		int pos = sofar.lastIndexOf (" ");
		pos++;
		String last = sofar.substring (pos);
		String tag = sofar.replaceAll (" ", "_");
		tag = tag + "_" + next;
		tag = getuniquetag (tag);
		StringBuffer sb = new StringBuffer ();
		sb.append ("{\n["+tag+"]\n");
		String t = (String)node.Tag;
		if (t.trim ().length () == 0) return;
		StringTokenizer st = new StringTokenizer (t, "()");
		if (st.countTokens () < 2) {
			Debug ("Don't have two tokens from "+t);
			return;
		}
		String input = filter (st.nextToken ()).trim ();
		String output = filter (st.nextToken ()).trim ();
		while (output.length () == 0)
			output = filter (st.nextToken ()).trim ();
		// Debug ("tag="+t+" / input="+input+" output="+output);
		// sb.append ("\t"+sofar+" ;\n");
		
		// with Gui2Gram, convert input and output to use dotted head tag form
		String indot = input.replaceAll (" ", ".");
		String outdot = output.replaceAll (" ", ".");
		sb.append ("\t"+last+" ;\n");
		// sb.append ("\t"+input+" ;\n");
		// sb.append ("\t"+output+" ;\n");
		sb.append ("\t"+indot+" ;\n");
		sb.append ("\t"+outdot+" ;\n");
		sb.append ("\t"+next+" ;\n");
		sb.append ("}\n");
		String all = new String (sb);
		store.add (all);
		// System.out.println (all);
	}

	
	static String filter (String line) {
		return Gui2Gram.filter (line);
	}
		


	String createNextState (basicNode node) {
		String tag = (String)(node.Tag);
		StringTokenizer st = new StringTokenizer (tag, "()");
		if (st.countTokens () < 2) {
			Debug ("don't have two tokens in "+tag);
			return "def";
		}
		String input = st.nextToken ().trim ();
		String output = st.nextToken ().trim ();
		while (output.length () == 0)
			output = st.nextToken ().trim ();
		StringTokenizer tt = new StringTokenizer (output);
		int n = tt.countTokens ();
		StringBuffer sb = new StringBuffer ();
		for (int i=0; i<Math.min (n, 3); i++) {
			String token = tt.nextToken ();
			sb.append (token.charAt (0));
		}
		if (n < 3) {
			for (int j=n; j<3; j++) {
				sb.append ('x');
			}
		}
		String s = new String (sb);
		String test = states.get (s);
		if (test != null) {
			for (int i=1; i<10; i++) {
				String next = s + i;
				if (states.get (next) == null) {
					s = next;
					break;
				}
			}
		}
		states.put (s, yes);
		return s;
	}

	String getuniquetag (String in) {
		if (tags.get (in) == null) {
			tags.put (in, yes);
			return in;
		}
		else {
			for (int i=1; i<99; i++) {
				String next = in+"_"+i;
				if (tags.get (next) != null) {
					continue;
				}
				tags.put (next, yes);
				return next;
			}
			Debug ("More than 99 tags starting with "+in);
			return "error";
		}
	}

	void writeRules (PrintWriter out) {
		try {
			for (int i=0; i<store.size (); i++) {
				out.println (store.elementAt (i));
			}
		}
		catch (Exception e) {
			e.printStackTrace ();
		}
	}

}


