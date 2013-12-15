/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jaivox.ui.gengram;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author rj
 */
public interface WnLink {

	static Map<String, String[]> dbsyns = new TreeMap<String, String[]> ();

	public String[] getsynonyms (String word);

	public String[] synsget (String word, String form);

	public void synsput (String word, String[] words, String form);

	public void synsremove (String word, String syn, String form);

	public void addtablecolumn (String filename, String sep, int columns, int column);

	public void dumpSynonyms ();

	public void createsyns ();
}
