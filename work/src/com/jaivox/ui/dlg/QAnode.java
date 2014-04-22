package com.jaivox.ui.dlg;


import java.util.ArrayList;
import java.util.Iterator;

public class QAnode {
	
	ArrayList <String> questions;
	ArrayList <String> answers;
	ArrayList <QAnode> followups;
	
	public QAnode (ArrayList <String> qs, ArrayList <String> as) {
		questions = qs;
		answers = as;
		followups = null;
	}
	
	public QAnode () {
		questions = new ArrayList <String> ();
		answers = new ArrayList <String> ();
		followups = null;
	}
	
	public String info () {
		StringBuilder sb = new StringBuilder ();
		int nq = questions.size ();
		int na = answers.size ();
		sb.append ("QA: nq ");
		sb.append (nq);
		sb.append (" na ");
		sb.append (na);
		if (nq > 0) {
			String q = questions.get (0);
			int len = Math.min (q.length (), 20);
			sb.append (' ');
			sb.append (q.substring (0, len));
			sb.append (' ');
		}
		if (followups != null) {
			int nf = followups.size ();
			sb.append (" nf ");
			sb.append (nf);
			if (nf > 0) {
				sb.append ('\n');
				for (int i=0; i<nf; i++) {
					QAnode fnode = followups.get (i);
					sb.append (fnode.info ());
				}
			}
		}
		String result = new String (sb);
		return result;
	}
	
	public ArrayList <String> getQuestions () {
		return questions;
	}
	
	public ArrayList <String> getAnswers () {
		return answers;
	}
	
	public ArrayList <QAnode> getFollowups () {
		return followups;
	}
	
	public void addQuestion (String question) {
		questions.add (question);
	}
	
	public boolean removeQuestion (String question) {
		boolean done = questions.remove (question);
		return done;
	}
	
	public void addAnswer (String answer) {
		answers.add (answer);
	}
	
	public boolean removeAnswer (String answer) {
		boolean done = questions.remove (answer);
		return done;
	}
	
	public void addFollowup (QAnode followup) {
		if (followups == null) followups = new ArrayList <QAnode> ();
		followups.add (followup);
	}
	
	public boolean removeFollowup (QAnode followup) {
		if (followups == null) return false;
		int n = followups.size ();
		for (int i=0; i<n; i++) {
			QAnode f = followups.get (i);
			if (f.equals (followup)) {
				followups.remove (i);
				return true;
			}
		}
		return false;
	}
	
	public void setFollowups (ArrayList <QAnode> fs) {
		followups = fs;
	}
	
	public boolean equals (QAnode other) {
		if (!listEquals (questions, other.questions)) return false;
		if (!listEquals (answers, other.answers)) return false;
		if (followups == null) return (other.followups == null);
		if (followups != null && other.followups == null) return false;
		int n = followups.size ();
		if (n != other.followups.size ()) return false;
		for (int i=0; i<n; i++) {
			QAnode f1 = followups.get (i);
			QAnode f2 = other.followups.get (i);
			if (!f1.equals (f2)) return false;
		}
		return true;
	}
	
	public static boolean listEquals (ArrayList <String> one, ArrayList <String> two) {
		int n = one.size ();
		if (n != two.size ()) return false;
		for (int i=0; i<n; i++) {
			String s1 = one.get (i);
			String s2 = two.get (i);
			if (!s1.equals (s2)) return false;
		}
		return true;
	}
	
/*
[
{"dialog": {
   "Qs:"
   [
     {  "are the freeways smooth at this time?" },
   ]
   "As:"
   [
     {   },
   ]
   "children": {
     [dialog]
   }
},*/	
	public String writeAsJson (String spaces, int num) {
		StringBuilder sb = new StringBuilder ();
		sb.append (spaces + "{\"dialog"+num+"\": \n");
		sb.append (spaces +" { \"Qs\": [\n");
		int n = questions.size ();
		for (int i=0; i<n; i++) {
			String question = questions.get (i);
			sb.append (spaces+"  ");
			sb.append ("{q"+i+": \"" + question + "\"},\n");
		}
		sb.append (spaces + " ],\n");
		sb.append (spaces +"   \"As\": [\n");
		n = answers.size ();
		for (int i=0; i<n; i++) {
			String answer = answers.get (i);
			sb.append (spaces+"  ");
			sb.append ("{a"+i+": \"" + answer + "\"},\n");
		}
		if (followups == null) {
			sb.append (spaces + " ]\n");
		}
		if (followups != null) {
			sb.append (spaces + " ],\n");
			sb.append (spaces +"   \"Fs\": [\n");
			String newspace = spaces + "    ";
			n = followups.size ();
			for (int i=0; i<n; i++) {
				QAnode f = followups.get (i);
				String s = f.writeAsJson (newspace, i);
				sb.append (s);
			}
			sb.append (spaces + "   ]\n");
		}
		sb.append (spaces + " }\n");
		sb.append (spaces + "},\n");
		String result = new String (sb);
		return result;
	}
	
	public String writeAsTabbed (int level) {
		StringBuilder tb = new StringBuilder ();
		for (int i=0; i<level; i++) {
			tb.append ('\t');
		}
		String spaces = new String (tb);
		String more = spaces + "\t";
		
		StringBuilder sb = new StringBuilder ();
		int nq = questions.size ();
		for (int i=0; i<nq; i++) {
			String q = questions.get (i);
			if (q == null || q.trim ().length () == 0) continue;
			sb.append (spaces);
			sb.append (q);
			sb.append ('\n');
		}
		int na = answers.size ();
		for (int i=0; i<na; i++) {
			String a = answers.get (i);
			if (a == null || a.trim ().length () == 0) continue;
			sb.append (more);
			sb.append (a);
			sb.append ('\n');
		}
		// sb.append ('\n');
		if (followups != null) {
			int nf = followups.size ();
			int nextlevel = level+2;
			for (Iterator<QAnode> it = followups.iterator (); it.hasNext ();) {
				QAnode followup = it.next ();
				ArrayList <String> fqs = followup.getQuestions ();
				if (fqs.size () == 0) continue;
				String s = followup.writeAsTabbed (nextlevel);
				sb.append (s);
			}
		}
		String result = new String (sb);
		return result;
	}

}

