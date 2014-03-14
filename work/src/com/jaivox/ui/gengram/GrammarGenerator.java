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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jaivox.ui.gengram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 *
 * @author lin
 */
public class GrammarGenerator {

	public static String DLG_DLIM = "()\r\n";
	static Parse P = null;
	;
    static WnLink W;
	private static Map<String, WnLink> synRepos = null;
	static String tests[];
	public static final String quoted = "([^\"]\\S*|\".+?\")\\s*";
	public static final Pattern regxQuoted = Pattern.compile (quoted);

	public GrammarGenerator (String dataFolder) {
		//W.synsfile = dataFolder + W.synsfile;
		Parse.penntags = dataFolder + P.penntags;
		synRepos = new HashMap ();
	}

	public void setWLink (String key) {
		try {
			WnLink wl = synRepos.get (key);
			if (wl == null) {
				wl = (WnLink) Class.forName (key).newInstance ();
			}
			synRepos.put (key, wl);
			W = wl;
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}

	public void load (String dlgFile, String datFile) {
		if (datFile != null) {
			W.addtablecolumn (datFile, ",\r\n", 3, 0);
		}

		if (dlgFile != null) {
			if (P == null) {
				P = new Parse (dlgFile);
			}
			if (P.Valid) {
				P.createsentences ();
			}
		}
	}

	public String[] getSynonyms (String word) {
		return W.getsynonyms (word);
	}

	public String[] getSynonyms (String word, String form) {
		return W.synsget (word, form);
	}

	public Sentence getSentence (String key) {
		return P.sentences.get (key);
	}

	public ArrayList<String> getParsedStatements () {
		return P.statements;
	}

	public void generate (String filename) {
		if (P == null || !P.Valid) {
			return;
		}
		TreeMap<String, Sentence> sentences = P.sentences;
		Set<String> keys = sentences.keySet ();
		int n = keys.size ();
		tests = keys.toArray (new String[n]);
		for (int i = 0; i < n; i++) {
			String key = tests[i];
			Sentence s = sentences.get (key);
			// s.show (""+i+" ");
			// s.findmultiwords (W);
			s.multiwordsubs (P, W);
		}
		/*
		 // generate using okays instead of subs
		 for (int i=0; i<n; i++) {
		 String key = tests [i];
		 Sentence s = sentences.get (key);
		 System.out.println ("Sentence "+i+" Generating okays for: "+key);
		 s.generateokays ();
		 }
		 */	}

	public ArrayList<String> parseDialog (String dlg) {
		ArrayList<String> sents = new ArrayList<String> ();
		StringTokenizer st = new StringTokenizer (dlg, DLG_DLIM);
		while (st.hasMoreTokens ()) {
			String token = st.nextToken ().trim ();
			if (token.length () == 0) {
				continue;
			}
			if (!token.endsWith ("?") && !token.endsWith (".")) {
				token = token + ".";
			}
			sents.add (token);
		}
		return sents;
	}

	public boolean addSynonyms (String word, String[] addsyns, String tag) {
		int news = 0;

		String[] ar = W.synsget (word, tag);
		ArrayList<String> arl = new ArrayList<String> ();
		if (ar != null) {
			arl.addAll (Arrays.asList (ar));
		}
		for (String k : addsyns) {
			if (!arl.contains (k)) {
				arl.add (k);
				news++;
			}
		}
		//arl.addAll(Arrays.asList(addsyns));
		ar = arl.toArray (new String[0]);
		W.synsput (word, ar, tag);

		ar = W.dbsyns.get (word);
		if (ar != null) {
			arl = new ArrayList<String> ();
			if (ar != null) {
				arl.addAll (Arrays.asList (ar));
			}
			for (String k : addsyns) {
				if (!arl.contains (k)) {
					arl.add (k);
				}
			}
			//arl.addAll(Arrays.asList(addsyns));
			ar = arl.toArray (new String[0]);
			W.dbsyns.put (word, ar);
		}
		//W.dumpSynonyms();
		return news > 0;
	}

	public Sentence generateAlts (String key) {
		TreeMap<String, Sentence> sentences = P.sentences;
		Sentence old = sentences.get (key);
		if (old == null) {
			return null;
		}
		Sentence sent = new Sentence (old.orig, old.form, old.tree);
		sent.setSelectionhandler (old.getSelectionhandler ());
		sent.multiwordsubs (P, W);
		sent.generateokays ();
		sentences.put (key, sent);
		return sent;
	}

	public SentenceX createSentence (String statement) {
		if (P == null) {
			P = new Parse ();
		}
		SentenceX sx = null;
		statement = Parse.padQuotes (GrammarGenerator.regxQuoted, statement);
		Sentence sent = P.doparse (statement);
		if (sent != null) {
			sx = sent == null ? null : new SentenceX (sent);
			P.sentences.put (sent.orig, sent);
			sent.multiwordsubs (P, W);
			//sent.generateokays ();
		}

		return sx;
	}

	public static void removeSentence (Object key) {
		P.sentences.remove (key);
	}

	public void removeSynonym (String word, String syn, String tag) {
		W.synsremove (word, syn, word);

		String[] ar = W.dbsyns.get (word);
		if (ar == null) {
			return;
		}
		ArrayList<String> arl = new ArrayList<String> ();
		arl.addAll (Arrays.asList (ar));
		arl.remove (syn);
		ar = arl.toArray (new String[0]);
		W.dbsyns.put (word, ar);
	}
}
