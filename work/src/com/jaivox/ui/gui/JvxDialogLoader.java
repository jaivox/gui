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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JTree;

import com.jaivox.tools.*;
import com.jaivox.ui.db.JvxDBMgr;
import com.jaivox.ui.dlg.*;
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
    
    String dfile = dlgfile;
    if(dlgfile.endsWith(".json")) {
      String f = new File(dlgfile).getName();
      f.replace(".json", ".txt");
      f = new File("/tmp/", f).getAbsolutePath();
      new JsonData (dlgfile).writeAsTabbed(f);
      dfile = f;
    }
		gen.load (dfile, datfile);
		gen.generate (dfile);
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
    
    QAnode findAndCreateQANode(String file) {
      QAnode root = null;
      if(file.endsWith(".json")) root = new JsonData (file).getRoot();
      else if(file.endsWith(".tree")) root = new ListData (file).getRoot();
      
      if(root == null) root = new  TabbedData(file).getRoot();
      return root;
    }
    public DefaultMutableTreeNode readDialogTree(String file, DefaultMutableTreeNode root) {
      QAnode qa_root = findAndCreateQANode(file);
      if(qa_root == null) return null;
      DefaultMutableTreeNode node = loadFromQANode(qa_root, root);
      if(node != null) root.add(node);
      return root;
    }
    DefaultMutableTreeNode loadFromQANode(QAnode qa_root, DefaultMutableTreeNode root) {
      SentenceX sx = null;
      DefaultMutableTreeNode node = createGuiNode( qa_root.getQuestions() );
      if(node != null) {
        DefaultMutableTreeNode asn = createGuiNode( qa_root.getAnswers() );
        if(asn != null) node.add(asn);
      }
      if(qa_root.getFollowups() == null) return node;
      
      for(QAnode qan : qa_root.getFollowups()) {
         DefaultMutableTreeNode child = loadFromQANode(qan, null);
         if(child != null) {
           if(root != null) root.add(child);
           else if(node != null) node.add(child);
           else if(node == null) node = child;
         }
      }
      return node;
    }
    DefaultMutableTreeNode createGuiNode(ArrayList<String> al) {
      DefaultMutableTreeNode node = null;
      SentenceX sx = null;
      if(al != null) {
        for(String s : al) {
          if(sx == null) sx = createSentence(s);
          else sx.alternateSentences.add(s);
          if(sx == null) continue;
          if(node == null) node = new DefaultMutableTreeNode(sx);
        }
      }
      return node;
    }
    SentenceX createSentence(String sq) {
      String line = Parse.padQuotes (GrammarGenerator.regxQuoted, sq);
      line = line.trim ();
      if (line.length () == 0) {
          return null;
      }
      if (!line.endsWith ("?") && !line.endsWith (".")) {
          line = line + ".";
      }
      Sentence c = gen.getSentence (line);
      return c == null ? null : new SentenceX (c);
    }

    public DefaultMutableTreeNode readConversation (String filename, String rootName) {
         getGrammarGenerator ();
         BufferedReader in = null;
         DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootName);
         readDialogTree(filename, root);
         return root;
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
