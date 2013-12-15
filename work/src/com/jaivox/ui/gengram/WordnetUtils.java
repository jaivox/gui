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
