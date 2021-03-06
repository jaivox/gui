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

import com.jaivox.ui.gui.JvxConfiguration;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.didion.jwnl.JWNLException;

public class WnLinkJWNL implements WnLink {

	private static Map<String, String[]> syns = new TreeMap<String, String[]> ();

	public WnLinkJWNL () {
		try {
			WordnetUtils.initialize (JvxConfiguration.WNconfig);
		} catch (JWNLException ex) {
			Logger.getLogger (WnLinkJWNL.class.getName ()).log (Level.SEVERE, null, ex);
		} catch (Exception ex) {
			Logger.getLogger (WnLinkJWNL.class.getName ()).log (Level.SEVERE, null, ex);
		}
	}

	String findPOSTag (String form) {
		String pos = null;
		if (form != null) {
			String f = Parse.tags.get (form);
			if (f != null) {
				f = f.toLowerCase ();
			}
			pos = f != null ? f.contains ("noun") ? "noun" : f.contains ("verb") ? "verb"
					: f.contains ("adjective") ? "adjective" : f.contains ("adverb") ? "adverb" : null : null;
		}
		return pos;
	}

	public String[] synsget (String word, String form) {
		//POS type
		String pos = findPOSTag (form);
		if (pos == null) {
//            System.out.println("synsget: " + word +"---"+form +"---"+ Parse.tags.get(form));
//            return null;  // trial - avoid Determiner, Preposition, conjunction etc.
		}
		return getsynonyms (word, pos);
	}

	public void synsput (String word, String[] words, String form) {
		//POS type
		String pos = findPOSTag (form);

		if (words.length > 0) {
			syns.put (word + "@" + pos, words);
		}
	}

	public void synsremove (String word, String syn, String form) {
		//POS type
		String pos = findPOSTag (form);

		String[] ts = getsynonyms (word, pos);
		if (ts != null) {
			List<String> al = Arrays.asList (ts);
			al.remove (syn);
			if (al.size () > 0) {
				syns.put (word + "@" + pos, al.toArray (new String[al.size ()]));
			} else {
				syns.remove (word + "@" + pos);
			}
		}
	}

	public void addSynonym (String word, String[] newsyns) {
		String[] ts = syns.get (word);
		if (ts != null) {
			List<String> al = Arrays.asList (ts);
			al.addAll (Arrays.asList (newsyns));
			newsyns = al.toArray (new String[al.size ()]);
		}
		if (newsyns != null && newsyns.length > 0) {
			syns.put (word, newsyns);
		}
	}

	void Debug (String s) {
		System.out.println ("[wnlink]" + s);
	}

	public String[] getsynonyms (String word) {
		return getsynonyms (word, null);
	}

	String[] getsynonyms (String word, String tag) {
		String[] ts = syns.get (word + "@" + tag);
		if (ts != null) {
			return ts;
		}

		try {
			ts = WordnetUtils.getSynonyms (word, tag);
		} catch (JWNLException ex) {
			Logger.getLogger (WnLinkJWNL.class.getName ()).log (Level.SEVERE, null, ex);
		}
		if (ts != null && ts.length > 0) {
			syns.put (word + "@" + tag, ts);
		}
		return ts;
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
				addSynonym (words[i], words);
				dbsyns.put (words[i], words);
			}
			Debug ("Added " + n + " database records");
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}

	public void dumpSynonyms () {
		StringBuffer sb = new StringBuffer ();
		for (Map.Entry es : syns.entrySet ()) {
			String k = (String) es.getKey ();
			String[] words = (String[]) es.getValue ();
			sb.append (k).append (": ");
			for (String w : words) {
				sb.append (w).append (", ");
			}
			sb.append ("\n");
		}
		System.out.println ("dumpSynonyms:---------\n" + sb.toString ());
	}

	public void createsyns () {
	}

	public static void main (String[] args) {
		new WnLinkJWNL ().test ("slow");
	}
}
