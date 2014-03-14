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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.didion.jwnl.JWNL;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.data.Word;

/**
 *
 * @author rj
 */
public class WordnetUtils {

	static PointerUtils ptUtils = PointerUtils.getInstance ();
	static Dictionary dictionary = Dictionary.getInstance ();

	public static String[] getSynonyms (String word, String postype) throws JWNLException {
		String sy[] = getSynonyms1 (word, postype);
		String ss = "";

		if (sy != null) {
			for (String s : sy) {
				ss += s + ", ";
			}
		}
		//System.out.println("[WordnetUtils]:getSynonyms: " + word +"--"+postype+"---"+ ss);
		return sy;
	}

	public static String[] getSynonyms1 (String word, String postype) throws JWNLException {
		String[] ts = null;

		List<POS> allPos = POS.getAllPOS ();
		Set<String> synonyms = new HashSet<String> ();
		if (postype != null) {
			POS pos = POS.getPOSForLabel (postype);
			if (pos != null) {
				allPos = new ArrayList<POS> ();
				allPos.add (pos);
			}
		}
		for (POS pos : allPos) {
			IndexWord iw = Dictionary.getInstance ().getIndexWord (pos, word);
			if (iw != null) {
				Synset[] senses = iw.getSenses ();
				for (Synset syns : senses) {
					Word[] words = syns.getWords ();
					for (Word w : words) {
						if (!w.getLemma ().trim ().equalsIgnoreCase (word)) {
							synonyms.add (w.getLemma ().trim ());
						}
					}
					PointerType pt[] = {PointerType.HYPONYM, PointerType.HYPERNYM};
					for (PointerType type : pt) {
						Pointer[] ptrs = syns.getPointers (type);
						for (Pointer p : ptrs) {
							Synset s = p.getTargetSynset ();
							Word[] ws = s.getWords ();
							for (Word w : ws) {
								//System.out.println("----" + w.getLemma().trim() +"----"+ pos.getLabel() +", "+ type.getLabel());
								//if(!w.getLemma().trim().equalsIgnoreCase(word)) synonyms.add(w.getLemma().trim());
							}
						}
					}
				}
			}
		}
		if (synonyms.size () > 0) {
			ts = synonyms.toArray (new String[synonyms.size ()]);
		}
		return ts;
	}

	public static void initialize (String conffile) throws JWNLException {
		try {
			JWNL.initialize (new FileInputStream (conffile));
		} catch (FileNotFoundException ex) {
			JWNL.initialize (WordnetUtils.class.getClassLoader ().getResourceAsStream (conffile));
		}
	}
}
