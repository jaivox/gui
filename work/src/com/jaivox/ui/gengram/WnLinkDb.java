/*
   Jaivox Application Generator (JAG) version 0.2 March 2014
   Copyright 2010-2014 by Bits and Pixels, Inc.

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

package com.jaivox.ui.gengram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class WnLinkDb implements WnLink {

	JavaDb db;
	static String synsfile = "synonyms.txt";

	public WnLinkDb () {
		db = new JavaDb ("wordnet30");
		createsyns ();
	}

	void Debug (String s) {
		System.out.println ("[wnlink]" + s);
	}
	String s1 = "select w2.lemma from sense ";
	String s2 = "left join word as w2 on w2.wordid=sense.wordid ";
	String s3 = "where sense.synsetid in (select sense.synsetid from word as w1 ";
	String s4 = "left join sense on w1.wordid=sense.wordid ";
	String s5 = "where w1.lemma=";
	String s6 = ") and w2.lemma<>";

	public String[] getsynonyms (String word) {
		String q = "\'";
		if (word.indexOf (q) != -1) {
			return null;
		}
		if (word.indexOf (" ") != -1) {
			q = "\"";
		}
		String query = s1 + s2 + s3 + s4 + s5 + q + word + q + " " + s6 + q + word + q;
		// Debug (query);
		String results[] = db.execute (query);
		return results;
	}

	void test (String word) {
		System.out.println ("Testing synonyms for " + word);
		String syns[] = getsynonyms (word);
		if (syns == null) {
			System.out.println ("No results");
			return;
		}
		for (int i = 0; i < syns.length; i++) {
			System.out.print (syns[i] + " ");
		}
		System.out.println ();
	}
	// words with synsetid's
	String w1 = "select w1.lemma from sense left join word as w1 on w1.wordid=sense.wordid";
	TreeMap<String, String[]> syns;

	public void createsyns () {
		File f = new File (synsfile);
		if (f.exists ()) {
			loadsynsfile ();
			return;
		}
		syns = new TreeMap<String, String[]> ();
		String words[] = db.execute (w1);
		int n = words.length;
		Debug ("Got " + n + " words with synsetid's");
		for (int i = 0; i < n; i++) {
			String s[] = getsynonyms (words[i]);
			if (s == null) {
				continue;
			}
			syns.put (words[i], s);
			if (i > 0 && i % 10000 == 0) {
				Debug ("processed " + i + " words");
			}
		}
		writesynsfile ();
	}

	void loadsynsfile () {
		try {
			Debug ("Loading syns from " + synsfile);
			BufferedReader in = new BufferedReader (new FileReader (synsfile));
			syns = new TreeMap<String, String[]> ();
			String line;
			while ((line = in.readLine ()) != null) {
				int pos = line.indexOf (":");
				if (pos == -1) {
					continue;
				}
				String key = line.substring (0, pos);
				String rest = line.substring (pos + 1);
				String vals[] = rest.split (",");
				syns.put (key, vals);
			}
			in.close ();
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}

	void writesynsfile () {
		try {
			PrintWriter out = new PrintWriter (new FileWriter (synsfile));
			Set<String> keys = syns.keySet ();
			for (Iterator<String> it = keys.iterator (); it.hasNext ();) {
				String key = it.next ();
				String vals[] = syns.get (key);
				StringBuffer sb = new StringBuffer ();
				sb.append (key);
				sb.append (':');
				int n = vals.length;
				for (int i = 0; i < n; i++) {
					sb.append (vals[i]);
					if (i < n - 1) {
						sb.append (',');
					}
				}
				out.println (new String (sb));
			}
			out.close ();
			Debug ("Wrote syns file " + synsfile);
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}

	public void addtablecolumn (String filename, String sep, int columns, int column) {
		try {
			BufferedReader in = new BufferedReader (new FileReader (filename));
			String line;
			ArrayList<String> hold = new ArrayList<String> ();
			while ((line = in.readLine ()) != null) {
				StringTokenizer st = new StringTokenizer (line, ",");
				if (st.countTokens () != columns) {
					continue;
				}
				for (int i = 0; i < column; i++) {
					String discard = st.nextToken ();
				}
				String word = st.nextToken ().trim ();
				hold.add (word);
			}
			in.close ();
			int n = hold.size ();
			String words[] = hold.toArray (new String[n]);
			for (int i = 0; i < n; i++) {
				syns.put (words[i], words);
				dbsyns.put (words[i], words);
			}
			Debug ("Added " + n + " database records");
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}

	public String[] synsget (String word, String form) {
		return syns.get (word);
	}

	public void synsput (String word, String[] words, String form) {
		syns.put (word, words);
	}

	public void dumpSynonyms () {
	}

	@Override
	public void synsremove (String word, String syn, String form) {
		throw new UnsupportedOperationException ("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
