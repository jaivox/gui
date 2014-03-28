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

package com.jaivox.ui.gui;

import bitpix.list.LispTree;
import bitpix.list.basicNode;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JTree;

import com.jaivox.tools.*;
import com.jaivox.ui.db.JvxDBMgr;
import com.jaivox.ui.gengram.GrammarGenerator;
import com.jaivox.ui.gengram.Parse;
import com.jaivox.ui.gengram.SentenceX;
import com.jaivox.ui.gengram.Sentence;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 *
 * @author lin
 */
public class JvxDialogLoader {

	static String datadir = JvxConfiguration.datadir;
	private String dlgfile = "";    // tree file
	private String datfile = "";
	private static GrammarGenerator gen = null;
	JvxMainFrame theFrame = null;

	public static synchronized GrammarGenerator getGrammarGenerator () {
		if (gen == null) {
			gen = new GrammarGenerator (datadir);
		}
		gen.setWLink (JvxMainFrame.getInstance ().wordsToBeExpanded ()
				? "com.jaivox.ui.gengram.WnLinkJWNL"
				: "com.jaivox.ui.gengram.WnLinkBasic");   // TODO from a wlink factory
		return gen;
	}

	public JvxDialogLoader (JvxMainFrame frame) {
		File dataFolder = new File (datadir);
		System.out.println ("datadir path: " + dataFolder.getAbsolutePath ());

		loadDoNotExpandwords ();
		//gen.generate(filename);
		theFrame = frame;
	}

	public void reInit () {
		getGrammarGenerator ();
		JvxMainFrame.getInstance ().setDirtyFlag (false);
		datadir = JvxConfiguration.theConfig ().getDataFolder ();
		dlgfile = JvxConfiguration.theConfig ().getDialogFile ();
		datfile = JvxConfiguration.theConfig ().getDataFile ();

		File dataFolder = new File (datadir);
		System.out.println ("datadir path: " + dataFolder.getAbsolutePath ());
		System.out.println ("Dialog file: " + dlgfile);
		System.out.println ("Data file: " + datfile);
		gen.load (dlgfile, datfile);
		gen.generate (dlgfile);
	}

	public void loadDialogFile (String file) {
		WaitCursor wait = new WaitCursor ();
		JvxConfiguration.theConfig ().setDialogFile (file);
		reInit ();
		loadDialogs (theFrame.getDialogTree ());
		wait.go ();
	}

	public void loadDialogs (JTree dialogTree) {
		loadDialogs (dialogTree, dlgfile);
	}

	public void loadDialogs (JTree dialogTree, String filename) {
		if (dialogTree == null || filename == null) {
			return;
		}
		File f = new File (filename);
		System.out.println (f.getAbsolutePath ());
		DefaultMutableTreeNode node1 = readConversation (filename, f.getName ());

		//DefaultMutableTreeNode node2 = readConversation(datadir + "eattemplate.txt", "restaurant");
		//readExpressions(datadir + "road.tree", node2);

		DefaultMutableTreeNode root = new DefaultMutableTreeNode ("Dialogs");
		root.add (node1);
		//root.add(node2);
		DefaultTreeModel model = (DefaultTreeModel) dialogTree.getModel ();
		model.setRoot (root);
		for (int i = 0; i < dialogTree.getRowCount (); i++) {
			dialogTree.expandRow (i);
		}
	}

	void readExpressions (String file, DefaultMutableTreeNode root) {
		QaList qs = new QaList (file);
		Vector<String[]> hold = new Vector<String[]> ();
		Set<String> keys = qs.getLookup ().keySet ();
		for (Iterator<String> it = keys.iterator (); it.hasNext ();) {
			String key = it.next ();
			QaNode node = qs.getLookup ().get (key);
			String tail[] = node.getTail ();
			DefaultMutableTreeNode knode = new DefaultMutableTreeNode (key);
			for (String s : tail) {
				knode.add (new DefaultMutableTreeNode (s));
			}
			root.add (knode);
		}
	}
        public DefaultMutableTreeNode readDialogTree(String file, DefaultMutableTreeNode root) {
            LispTree tree = new LispTree(file);
            tree.PrintList(tree.Root);
            for (int i=0; i < tree.Root.ListChild.size(); i++) {
                basicNode pnode = (basicNode)(tree.Root.ListChild.elementAt(i));
                DefaultMutableTreeNode node = readDialogNode(pnode);
                if(node != null) root.add(node);
            }
            return root;
        }
        DefaultMutableTreeNode readDialogNode(basicNode root) {
            DefaultMutableTreeNode node = null;
            for (int i=0; i < root.ListChild.size(); i++) {
                basicNode pnode = (basicNode)(root.ListChild.elementAt(i));
                DefaultMutableTreeNode tn = readDialogQA(pnode, i);
                if(tn != null) {
                    if(node == null) node = tn;
                    else node.add(tn);
                } 
            }
            return node;
        }
        DefaultMutableTreeNode readDialogQA(basicNode root, int ord) {
            SentenceX sx = null;
            DefaultMutableTreeNode tn = null;
            for (int i=0; i < root.ListChild.size(); i++) {
                StringBuilder sb = new StringBuilder();
                basicNode pnode = (basicNode)(root.ListChild.elementAt(i));
                getNodeString(pnode, sb);
                System.out.println((ord%2 == 0 ? "Q" : "A") +"---"+ sb );
                String line = Parse.padQuotes (GrammarGenerator.regxQuoted, sb.toString());
                line = line.trim ();
                if (line.length () == 0) {
                    continue;
                }
                if (!line.endsWith ("?") && !line.endsWith (".")) {
                    line = line + ".";
                }
                if(sx == null) {
                    Sentence c = gen.getSentence (line);
                    sx = c == null ? null : new SentenceX (c);
                }
                else {
                    sx.alternateSentences.add(line);
                }
            }
            if(sx != null) tn = new DefaultMutableTreeNode (sx);
            return tn;
        }
        void getNodeString(basicNode node, StringBuilder sb) {
            if (node.ListChild == null || node.ListChild.size() == 0) {
                sb.append(node.Tag).append(' ');
            }
            else {
                if (!node.Tag.equals ("List")) sb.append(node.Tag).append(' ');
                for (int i=0; i<node.ListChild.size(); i++) {
                    basicNode pnode = (basicNode)(node.ListChild.elementAt(i));
                       getNodeString(pnode, sb); 
                }
            }
        }
        public DefaultMutableTreeNode readConversation (String filename, String rootName) {
             getGrammarGenerator ();
             BufferedReader in = null;
             DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootName);
             readDialogTree(filename, root);
             return root;
        }
        public DefaultMutableTreeNode readConversation_0 (String filename, String rootName) {
		getGrammarGenerator ();
		BufferedReader in = null;
		int level = 0;
		DefaultMutableTreeNode node[] = new DefaultMutableTreeNode[10];
		node[0] = new DefaultMutableTreeNode (rootName);

		try {
			in = new BufferedReader (new FileReader (filename));
			String line;
			boolean skip = false;

			while ((line = in.readLine ()) != null) {
				line = Parse.padQuotes (GrammarGenerator.regxQuoted, line);
				String tabline = line;
				line = line.trim ();
				if (line.length () <= 0) {
					continue;
				}
				if (line.startsWith ("{")) {
					skip = true;
				}
				if (line.startsWith ("}")) {
					skip = false;
					continue;
				}
				if (skip) {
					continue;
				}
				level = 1;
				for (int i = 0; tabline.charAt (i) == '\t'; i++) {
					level++;
				}
				DefaultMutableTreeNode tn = null;
				int ntoks = 0;
                                SentenceX sx = null;
				StringTokenizer st = new StringTokenizer (line, GrammarGenerator.DLG_DLIM);
                                if(st.countTokens() == 1) {
                                    String token = st.nextToken ().trim ();
                                    if (token.length () > 0) {
                                        if(node[level] != null) {
                                            sx = (SentenceX) node[level].getUserObject();
                                            if(sx != null) sx.alternateSentences.add(token);
                                        }
                                    }
                                    continue;
                                }
                                        
                                while (st.hasMoreTokens ()) {
					String token = st.nextToken ().trim ();
					if (token.length () == 0) {
						continue;
					}
					if (!token.endsWith ("?") && !token.endsWith (".")) {
						token = token + ".";
					}
                                        if(ntoks++ > 1) {                   //  (Q?) (A.) (A1.) (A2.)
                                            sx.alternateSentences.add(token);
                                            continue;
                                        }
                                        
					Sentence c = gen.getSentence (token);
					sx = c == null ? null : new SentenceX (c);
					if (tn == null) {
						tn = new DefaultMutableTreeNode (sx == null ? token : sx);
					} else {
						tn.add (new DefaultMutableTreeNode (sx == null ? token : sx));
					}
				}

				node[level] = tn;
				node[level - 1].add (tn);

				System.out.println (line);
			}
		} catch (Exception e) {
			e.printStackTrace ();
		} finally {
			try {
				if (in != null) {
					in.close ();
				}
			} catch (Exception ex) {
				ex.printStackTrace ();
			}
		}
		return node[0];
	}

	public void loadDoNotExpandwords () {
		String f = JvxConfiguration.theConfig ().getDoNotExpandWords ();
		try {
			BufferedReader in = new BufferedReader (new FileReader (f));
			String line;
			while ((line = in.readLine ()) != null) {
				Sentence.basicVerbs.add (line.trim ());
			}
			in.close ();
		} catch (Exception e) {
			System.out.println ("loadDoNotExpandwords: " + e.getMessage ());
		}
	}

	public String[] loadGrammar () {
		ArrayList<String> lines = new ArrayList<String> ();
		try {
			BufferedReader in = new BufferedReader (new FileReader (datadir + "grammar.txt"));
			String line;
			while ((line = in.readLine ()) != null) {
				lines.add (line);
			}
			in.close ();
			int n = lines.size ();
			String array[] = lines.toArray (new String[n]);
			return array;
		} catch (Exception e) {
			e.printStackTrace ();
			String array[] = new String[1];
			array[0] = "Error loading grammar samples.";
			return array;
		}
	}

	public String[][] loadQualData () {
		ArrayList<String> lines = new ArrayList<String> ();
		try {
			BufferedReader in = new BufferedReader (new FileReader (datadir + "road.qdb"));
			String line;
			while ((line = in.readLine ()) != null) {
				if (line.trim ().length () == 0) {
					continue;
				}
				lines.add (line);
			}
			in.close ();
			int n = lines.size ();
			String line0 = lines.get (0);
			StringTokenizer st = new StringTokenizer (line0, ",");
			int cols = st.countTokens ();
			String table[][] = new String[n - 1][cols + 1];
			for (int i = 1; i < n; i++) {
				int j = i - 1;
				String row = lines.get (i);
				st = new StringTokenizer (row, ",");
				if (st.countTokens () != cols) {
					continue;
				}
				table[j][0] = "" + i;
				for (int k = 0; k < cols; k++) {
					String word = st.nextToken ().trim ();
					table[j][k + 1] = word;
				}
			}
			return table;
		} catch (Exception e) {
			e.printStackTrace ();
			String table[][] = new String[1][4];
			table[0][1] = "error";
			table[0][2] = "no value";
			table[0][3] = "no value";
			return table;
		}
	}
	private static JvxDBMgr dbInter = null;
	private static boolean dataLoaded = false;

	public static boolean isDataLoaded () {
		return dataLoaded;
	}

	public void interfaceDialogs (ActionEvent evt) {
		if (evt.getActionCommand ().equals ("DBInterface")) {
			if (dbInter != null) {
				List<String> tabs = dbInter.getSeletedTabs ();
				System.out.println ("interfaceDialogs: done: tabs: " + tabs.size ());
				dbInter.dispose ();
				if (tabs.size () > 0) {
					List<List<String>> rows = dbInter.getDBMgr ().queryTab (tabs.get (0));

					Object vals[][] = new Object[rows.size ()][];
					int i = 0;
					for (List row : rows) {
						vals[i++] = row.toArray ();
						if (!dataLoaded) {
							dataLoaded = true;
						}
					}
					try {
						this.theFrame.getQualdbTable ().setModel (
								new javax.swing.table.DefaultTableModel (vals,
								dbInter.getDBMgr ().getTableFields (tabs.get (0)).toArray ()));
					} catch (SQLException ex) {
						Logger.getLogger (JvxDialogLoader.class.getName ()).log (Level.SEVERE, null, ex);
					}
				}
			}
			dbInter = null;
		} else {
			java.awt.EventQueue.invokeLater (new Runnable () {
				public void run () {
					dbInter = new JvxDBMgr (theFrame, true);
					dbInter.addWindowListener (new java.awt.event.WindowAdapter () {
						@Override
						public void windowClosing (java.awt.event.WindowEvent e) {
							System.out.println ("interfaceDialogs: db inteface closing");
						}

						@Override
						public void windowClosed (java.awt.event.WindowEvent e) {
							System.out.println ("interfaceDialogs: db inteface closed");
						}
					});
					dbInter.setVisible (true);
				}
			});
		}
	}
}
