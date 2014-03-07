/*
   Jaivox Application Generator (JAG) version 0.1 December 2013
   Copyright 2010-2013 by Bits and Pixels, Inc.

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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jaivox.ui.gengram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author lin
 */
public class SentenceX implements SelectionHandler {

	private Sentence theSentence = null;    // hasA for convinience, than isA
	private ArrayList<ArrayList<Object>> tabModvalues = null;
	private ArrayList<String> excludes = new ArrayList<String> ();
	public static Set<String> userWords = new TreeSet<String> ();
	public static Map<String, List<String>> usersyns = new HashMap ();

	public void setTheSentence (Sentence theSentence) {
		this.theSentence = theSentence;
		this.theSentence.setSelectionhandler (this);
		//tabModvalues = null;
	}

	public SentenceX (Sentence c) {
		setTheSentence (c);
	}

	public String getTagFormAt (int col) {
		return theSentence.forms[col];
	}

	public Sentence getSentence () {
		return theSentence;
	}

	public String getSentenceKey () {
		return theSentence.orig;
	}

	public String[] getWords () {
		return this.theSentence.words;
	}

	public String[][] getOkayWords () {
		return this.theSentence.okay;
	}

	public String toString () {
		return this.theSentence.orig;
	}

	public void generateokays (ArrayList<String> oks) {
		theSentence.setSelectionhandler (this);
		if (tabModvalues == null || theSentence.alts == null) {
			theSentence.generateokays ();
		}
		if (theSentence.alts != null) {
			oks.addAll (theSentence.alts);
		}
	}

	public Object[] getSentenceOptions () {
		return theSentence.alts.toArray ();
	}

	public static Object[][] transpose (Object[][] mat) {
		int rows = mat.length;
		int cols = 0;
		for (Object[] o : mat) {
			cols = o != null ? Math.max (cols, o.length) : cols;
		}
		Object[][] tpose = new Object[cols][rows];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (mat[i] == null || mat[i].length <= j) {
					tpose[j][i] = "";
				} else {
					tpose[j][i] = mat[i][j];
				}
			}
		}
		return tpose;
	}

	public Object[][] getWordOptions () {
		String[][] okwords = getOkayWords ();
		return transpose (okwords);
	}

	public void debug () {
		theSentence.Debug (" --- SentenceX ---");
		if (tabModvalues == null) {
			System.out.println ("null");
			return;
		}
		for (ArrayList cells : tabModvalues) {
			for (Object cell : cells) {
				if (cells.toString ().trim ().length () > 0) {
					System.out.println (cell);
				}
			}
		}
	}

	public boolean isExcluded (String word) {
		return excludes.contains (word);
	}

	public void addExclusion (String s) {
		excludes.add (s);
	}

	public void removeExclusion (String s) {
		excludes.remove (s);
	}

	public ArrayList<ArrayList<Object>> getTabModvalues () {
		return tabModvalues;
	}

	public void setTabModvalues (ArrayList<ArrayList<Object>> tabModvalues) {
		this.tabModvalues = tabModvalues;
	}
        
        static String breaks = "[~`!@#$%^&*\\(\\)+=\\{\\}\\[\\]|\\:;\'\"<>,.?/ \t\r\n]";
	public String dump (int level) {
		StringBuffer sb = new StringBuffer ();
		if (tabModvalues == null) {
			theSentence.generateokays ();
		}
		Object[] alts = getSentenceOptions ();
		sb.append ('*').append (this.theSentence.orig).append ('\n');
		for (Object alt : alts) {
			String sent = (String) alt;
                        sent = sent.replaceAll(breaks, "");
                        String s = this.theSentence.orig.replaceAll(breaks, "");
                        if(sent.equals (s)) {
			//if (sent.equals (this.theSentence.orig)) {
				continue;
			}
			boolean sel = true;
			String pad = "";
			for (String ex : excludes) {
				if (sent.contains (" " + ex.trim ())) {
					sel = false;
					break;
				}
			}
			if (level > 1) {
				String format = "%" + (level - 1) + "s";
				pad = String.format (format, " ").replace (' ', '\t');
			}
			pad = "\t";
			if (sel) {
				sb.append (pad).append (alt).append ('\n');
			}
		}
		return sb.toString ();
	}

	public void dumpSynonymExclusions (Properties p) {
		int i = 0;
		for (String x : excludes) {
			p.put (getSentenceKey () + "@exludes." + i, x);
			i++;
		}
	}

	public boolean readSyns (Properties p) {
		for (int i = 0; i < 100; i++) {
			String k = getSentenceKey () + "@exludes." + i;
			String s = p.getProperty (k);
			if (s == null) {
				break;
			}
			excludes.add (s);
		}
		return (excludes.isEmpty ());
	}

	@Override
	public String[] filterUnSelected (String[] all) {
		if (all == null || all.length <= 0) {
			return all;
		}
		int pre = all.length;
		List<String> allsyns = new ArrayList<String> ();
		allsyns.addAll (Arrays.asList (all));
		allsyns.removeAll (this.excludes);
		//System.out.println("filterUnSelected: "+ pre +"---"+ allsyns.size());
		return allsyns.toArray (new String[allsyns.size ()]);
	}

	public boolean isUserWord (String word) {
		return userWords.contains (word);
	}

	@Override
	public boolean skipPOSFormMatch (String word) {
		return userWords.contains (word);
	}

	public static void addUserWord (String s) {
		userWords.add (s);
	}

	public static void removeUserWord (String s) {
		userWords.remove (s);
	}

	public static void addUserSynonym (String word, String tag, String syn) {
		List<String> al = usersyns.get (word + "@" + tag);
		if (al == null) {
			al = new ArrayList<String> ();
		}
		if (!al.contains (syn)) {
			al.add (syn);
		}
		usersyns.put (word + "@" + tag, al);
	}

	public static void removeUserSynonym (String word, String tag, String syn) {
		List<String> al = usersyns.get (word + "@" + tag);
		if (al == null) {
			return;
		}
		al.remove (syn);
	}
}
