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

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Sentence {

	String orig;
	String form;
	String tree;
	String words[];
	String forms[];
	int lens[];
	int N;
	String subs[][];
	String okay[][];
	boolean Valid = false;

	public Sentence (String o, String f, String t) {
		try {
			orig = o;
			form = f;
			tree = t;
			StringTokenizer st = new StringTokenizer (orig, Parse.breaks);
			int n = st.countTokens ();
			words = new String[n];
			for (int i = 0; i < n; i++) {
				words[i] = st.nextToken ();
			}
			// words = orig.split (" ");
			forms = form.split (" ");
			N = forms.length;
			if (n != N) {
				Debug ("Something wrong N=" + N + " " + n + "\n" + orig + "\n" + tree + "\n" + form);
				return;
			}
			Valid = true;
		} catch (Exception e) {
			e.printStackTrace ();
			Valid = false;
		}

	}

	void Debug (String s) {
		System.out.println (orig + ":" + s);
	}

	void show (String id) {
		System.out.println (id + " " + orig + "\n" + tree + "\n" + form);
	}
	int maxlen = 4;

	void multiwordsubs (Parse P, WnLink link) {
		ArrayList<String> modwords = new ArrayList<String> ();
		ArrayList<String> modforms = new ArrayList<String> ();
		ArrayList<Integer> modlens = new ArrayList<Integer> ();
		Map<String, String[]> dbsyns = link.dbsyns;
		Integer One = new Integer (1);
		for (int i = 0; i < N; i++) {
      if(words == null || words.length == 0) continue;
			String w = "";
			int best = -1;
			String bestw = null;
			for (int j = 0; j < maxlen; j++) {
				if (i + j >= N) {
					break;
				}
        w += (" " + words[i + j]);
				w = w.trim ();
				if (link.synsget (w, forms[i + j]) != null) {
					best = j;
					bestw = w;
				}
			}
			if (best > -1) {
				modwords.add (bestw);
				modforms.add (forms[i]);
				modlens.add (new Integer (best + 1));
				i = i + best;
			} else {
				modwords.add (words[i]);
				modforms.add (forms[i]);
				modlens.add (One);
			}
		}
		N = modwords.size ();
		words = modwords.toArray (new String[N]);
		forms = modforms.toArray (new String[N]);
		lens = new int[N];
		for (int i = 0; i < N; i++) {
			Integer I = modlens.get (i);
			lens[i] = I.intValue ();
		}

		/*
		 // show
		 String show = "";
		 for (int i=0; i<N; i++) {
		 show += ("("+forms [i]+" "+words [i]+") ");
		 }
		 Debug (show);
		 */

		// now generate the ok subs
		okay = new String[N][];
		int total = 0;
		for (int i = 0; i < N; i++) {
			String word = words[i];
			if (basicVerbs.contains (word)) {
				continue;
			}

			String syn[] = link.synsget (word, forms[i]);
			// if (lens [i] > 1) Debug ("Checking in syns "+word);
			if (syn == null) {
				continue;
			}
			// if (lens [i] > 1) Debug ("Checking in dbsys "+word);
			String dbsyn[] = dbsyns.get (word);

			if (selectionHandler != null) {
				syn = selectionHandler.filterUnSelected (syn);
				dbsyn = selectionHandler.filterUnSelected (dbsyn);
			}

			if (dbsyn != null) {
				// Debug ("dbsyn match for "+word);
				okay[i] = dbsyn;
				total += dbsyn.length;
				continue;
			} else {
				int n = syn.length;
				ArrayList<String> hold = new ArrayList<String> ();
				hold.add (word);
				outer:
				for (int j = 0; j < n; j++) {
					String w = syn[j];
					for (int k = 0; k < hold.size (); k++) {
						String test = hold.get (k);
						if (test.equals (w)) {
							continue outer;
						}
					}
					// if (hold.contains (w)) continue;
					StringBuffer sb = new StringBuffer ();
					for (int k = 0; k < N; k++) {
						if (k != i) {
							sb.append (words[k] + " ");
						} else {
							sb.append (w + " ");
						}
					}
					if (sb.length () > 0 && (orig.endsWith ("?") || orig.endsWith ("."))) { /// wrk arnd to retain the "?"
						sb.setCharAt (sb.length () - 1, orig.charAt (orig.length () - 1));
						//sb.append(orig.charAt(orig.length()-1));
					}
					String all = new String (sb).trim ();
					Sentence s = P.doparse (all);
					if (s == null) {
						continue;
					}
					if (s.form == null) {
						continue;
					}

					boolean posMatch = false;
					posMatch = matches (s.form);
					// Debug (w +" --- "+ sb);
					if (!posMatch) {
						if (selectionHandler != null) {
							posMatch = selectionHandler.skipPOSFormMatch (w);
						}
					}

					if (posMatch) {
						hold.add (w);
					}
				}
				int m = hold.size ();
				if (m > 0) {
					okay[i] = hold.toArray (new String[m]);
					total += m;
				}
				/*
				 String diag = word+":";
				 for (int j=0; j<okay [i].length; j++) {
				 diag += (okay [i][j]+",");
				 }
				 Debug (diag);
				 */
			}
		}
		// Debug (""+total+" okay substitutions");
	}

	boolean matches (String ss) {
		String sform[] = ss.split (" ");
		int m = sform.length;
		int cumulative = 0;
		for (int i = 0; i < N; i++) {
			String f = forms[i];
			if (cumulative >= m) {
				Debug ("Cumulative: " + cumulative + " i:" + i);
				return false;
			}
			String g = sform[cumulative];
			if (!f.equals (g)) {
				return false;
			}
			cumulative += lens[i];
		}
		return true;
	}
	ArrayList<String> alts;
	int combocount;

	void generateokays () {
		alts = new ArrayList<String> ();
		combocount = 0;
		String blank[] = new String[N];
		generateokay (blank, 0);
	}

	void generateokay (String blank[], int stage) {
		if (stage >= N) {
			StringBuffer sb = new StringBuffer ();
			for (int i = 0; i < N; i++) {
				sb.append (blank[i]);
				if (i < N - 1) {
					sb.append (" ");
				}
			}
			//sb.append (".");
			if (sb.length () > 0 && (this.orig.endsWith ("?") || orig.endsWith ("."))) {
				sb.append (this.orig.charAt (orig.length () - 1));
			}
			String statement = new String (sb);
			combocount++;
			//System.out.println (""+combocount+": "+statement);
			alts.add (statement);
		} else {
			String sub[] = okay[stage];
			if (sub == null) {
				blank[stage] = words[stage];
				generateokay (blank, stage + 1);
			} else {
				if (selectionHandler != null) {
					sub = selectionHandler.filterUnSelected (sub);
				}
				for (int i = 0; i < sub.length; i++) {
					blank[stage] = sub[i];
					generateokay (blank, stage + 1);
				}
			}
		}
	}

	public void setSelectionhandler (SelectionHandler handler) {
		selectionHandler = handler;
	}

	public SelectionHandler getSelectionhandler () {
		return selectionHandler;
	}
	///
	SelectionHandler selectionHandler = null;
	public static Set<String> basicVerbs = new TreeSet<String> ();

	static {
		basicVerbs.add ("be");
		basicVerbs.add ("do");
	}
}

interface SelectionHandler {

	public String[] filterUnSelected (String[] all);

	public boolean skipPOSFormMatch (String word);
}
