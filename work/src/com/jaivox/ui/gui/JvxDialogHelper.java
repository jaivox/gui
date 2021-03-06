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

import bitpix.list.basicNode;
import com.jaivox.ui.appmaker.GuiPrep;
import com.jaivox.ui.dlg.QAnode;
import com.jaivox.ui.gengram.SentenceX;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author lin
 */
public class JvxDialogHelper {

	static String datadir = JvxConfiguration.datadir;
	JvxMainFrame theFrame = null;
	DlgTreeModelListener treeListener;

	public JvxDialogHelper (JvxMainFrame frame) {
		super ();
		theFrame = frame;
	}

	public JPopupMenu createPopup (DefaultMutableTreeNode rightClickedNode) {
		final DialogMenuAction menuAction = new DialogMenuAction ();

		JPopupMenu popup = new JPopupMenu ();
		JMenuItem addMenuItem = new JMenuItem ("Add");
		JMenuItem delMenuItem = new JMenuItem ("Delete");
		JMenuItem editMenuItem = new JMenuItem ("Edit");
		//JMenuItem synMenuItem = new JMenuItem("Synonyms");

		addMenuItem.addActionListener (menuAction);
		delMenuItem.addActionListener (menuAction);
		editMenuItem.addActionListener (menuAction);
		//synMenuItem.addActionListener(menuAction);



		popup.add (addMenuItem);
		popup.add (delMenuItem);
		popup.add (editMenuItem);
		//popup.add(createSynonymsMenu(rightClickedNode.getUserObject()));
		popup.add (theFrame.getSynsHelper ().createOkaysSynonymsMenu (rightClickedNode.getUserObject ()));

		MenuUtils.addUndoRedoMenus (popup);

		return popup;
	}

	public void newDialog () {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode ("Dialogs");

		DefaultMutableTreeNode node = new DefaultMutableTreeNode ("New App Root");
		root.add (node);
		DefaultTreeModel model = (DefaultTreeModel) JvxMainFrame.getInstance ().getDialogTree ().getModel ();
		model.setRoot (root);

		registerUndoAddNode (node, JvxMainFrame.getInstance ().getDialogTree ());
	}

	public void dialogTreeRClicked (java.awt.event.MouseEvent evt) {
		JTree tree = (JTree) evt.getSource ();

		if (evt.isPopupTrigger ()) {
			int x = evt.getX ();
			int y = evt.getY ();

			TreePath path = tree.getPathForLocation (x, y);
			if (path == null) {
				return;
			}

			DefaultMutableTreeNode rightClickedNode =
					(DefaultMutableTreeNode) path.getLastPathComponent ();

			TreePath[] selectionPaths = tree.getSelectionPaths ();
			//check if node was selected
			boolean isSelected = false;
			if (selectionPaths != null) {
				for (TreePath selectionPath : selectionPaths) {
					if (selectionPath.equals (path)) {
						isSelected = true;
					}
				}
			}
			//if clicked node was not selected, select it
			if (!isSelected) {
				tree.setSelectionPath (path);
				dialogTreeMouseClicked (evt);    // update the tab and list
			}
			//if(rightClickedNode.isLeaf()){
			JPopupMenu popup = createPopup (rightClickedNode);
			popup.show (tree, x, y);
			// }
		} else {
		}
	}
  void saveTreeAsTabbedFile(String fname) throws IOException {
    java.io.FileOutputStream out = new FileOutputStream (fname);
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) theFrame.getDialogTree ().getModel ().getRoot ();
		StringBuilder tdump = new StringBuilder ();

		saveAsTabbed (0, root, tdump);
		out.write (tdump.toString ().getBytes ());
  }
  void saveAsTabbed(int level, DefaultMutableTreeNode node, StringBuilder sb) {
    //int level = node.getLevel();
    if(node.getLevel() >= 2)  {
      StringBuilder tabs = new StringBuilder();
      for(int i = 2; i < level; i++) tabs.append('\t');
      sb.append(tabs);
      Object sx = node.getUserObject ();
      sb.append(sx.toString()).append("\n");
      if (sx instanceof SentenceX) {
        for(String s : ((SentenceX) sx).alternateSentences) {
          sb.append(tabs);
          sb.append(s).append("\n");
        }
      }
    }
    int il = 1;
    for(Enumeration e = node.children(); e.hasMoreElements();) {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode) e.nextElement();
      saveAsTabbed(level + il, child, sb);
      if(node.getLevel() >= 2) il++;
    }
  }
	public void dumpTreeToFile (String fname) throws IOException {
		java.io.FileOutputStream out = new FileOutputStream (fname);
		//out.write(dumpTree(theFrame.getDialogTree()).getBytes());
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) theFrame.getDialogTree ().getModel ().getRoot ();
		String buf = dumpTree (theFrame.getDialogTree ().getModel (), root);
		out.write (buf.getBytes ());
	}

	private String dumpTree (TreeModel model, DefaultMutableTreeNode node) {
		StringBuffer tdump = new StringBuffer ();
		Object sx = node.getUserObject ();
		if (sx instanceof SentenceX) {
			String s = ((SentenceX) sx).dump (node.getLevel () - 2); // skip the dialog.road
			tdump.append (s);
		} else {
			if (node.getLevel () > 2) {
				tdump.append ("\t").append (sx.toString ()).append ("\n");
			}
		}
		for (int i = 0; i < model.getChildCount (node); i++) {
			String s = dumpTree (model, (DefaultMutableTreeNode) model.getChild (node, i));
			tdump.append (s);
		}
		return tdump.toString ();
	}

	public void dumpDialogToFile (String fname) throws IOException {
		java.io.FileOutputStream out = new FileOutputStream (fname);
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) theFrame.getDialogTree ().getModel ().getRoot ();
		StringBuffer tdump = new StringBuffer ();

		dumpDialog (root, tdump);
		out.write (tdump.toString ().getBytes ());
	}

  public void dumpDialog (DefaultMutableTreeNode node, StringBuffer tdump) {
		int level = 0;
		while (node != null) {
			level = node.getLevel ();
			//System.out.println(level +": "+ node);
			if (level < 2) {
				node = node.getNextNode ();  // skil dialog.road
				continue;
			}
			for (int i = 0; i < level - 2; i++) {
				tdump.append ('\t');
			}
			tdump.append ('(').append (node.toString ()).append (')');
  		if (!node.isLeaf ()) {
				node = (DefaultMutableTreeNode) node.getNextNode ();
				tdump.append (" (").append (node.toString ()).append (')');
			}
			tdump.append ('\n');
			node = node.getNextNode ();
		}
	}

	void debugTree (JTree tree) {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel ().getRoot ();
		for (Enumeration e = root.breadthFirstEnumeration (); e.hasMoreElements ();) {
			DefaultMutableTreeNode nd = (DefaultMutableTreeNode) e.nextElement ();
			System.out.println ("---" + nd.getLevel () + "---");
			Object sx = nd.getUserObject ();
			if (sx instanceof SentenceX) {
				((SentenceX) sx).debug ();
			}
			System.out.println ("---" + nd.getLevel () + "---");

		}
	}

	void dialogTreeMouseClicked (MouseEvent evt) {
		JTree tree = (JTree) evt.getSource ();
		//debugTree(tree);
		if (evt.getClickCount () == 1 || evt.getClickCount () == 2) {
			TreePath tpath = tree.getSelectionPath ();
			if (tpath != null) {
				ArrayList<String> oks = new ArrayList<String> ();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tpath.getLastPathComponent ();
				if (node.isRoot () || node.getLevel () <= 1) {
                                        JvxMainFrame.getInstance ().getGrammarList ().setListData (new String[] {});
					return;
				}

				//System.out.println (node);

				ArrayList<String> al = null;
				Object sx = node.getUserObject ();

				if (sx instanceof String && sx.toString ().trim ().length () > 0) {
					al = new ArrayList<String> ();
					sx = JvxDialogHelper.createSentence (sx.toString ().trim ());
					if (sx != null) {
						node.setUserObject (sx);
					}
				}
				theFrame.getSynsHelper ().populateSynonymsTab (node.getUserObject ());

				al = new ArrayList<String> ();
				if (sx != null && sx instanceof SentenceX) {
					((SentenceX) sx).generateokays (al);
					oks.addAll (al);
				}

				theFrame.getGrammarList ().setListData (oks.toArray ());
				JvxMainFrame.undoManager_.discardAllEdits ();
			} else {
				// Open file dialog
			}
		}
	}

	void generateApp (JvxMainFrame ui) {
		GuiPrep.generateApp (JvxConfiguration.theConfig ().getConfFile ());
	}

	void runApp (JvxMainFrame ui) throws FileNotFoundException, IOException {
		GuiPrep.runApp (JvxConfiguration.theConfig ().getConfFile ());
	}

	static void registerUndoAddNode (DefaultMutableTreeNode node, JTree tree) {
		node = (DefaultMutableTreeNode) (node.isRoot () ? node.getChildAt (0) : node);
		UndoableEdit rowChange = new DialogTreeNodeUndoableInsert (tree, node, (MutableTreeNode) node.getParent ());
		JvxMainFrame.getInstance ().postUndoableEdit (rowChange);
	}

	static Object createSentence (String text) {
		WaitCursor c = new WaitCursor ();
		Object o = JvxDialogLoader.getGrammarGenerator ().createSentence (text);
		c.go ();
		return o;
	}

	void dumpSynonymSelections (String name) throws FileNotFoundException, IOException {
		java.io.FileOutputStream out = new FileOutputStream (name + ".savx");
		Properties p = new Properties ();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) theFrame.getDialogTree ().getModel ().getRoot ();
		for (Enumeration e = root.breadthFirstEnumeration (); e.hasMoreElements ();) {
			DefaultMutableTreeNode nd = (DefaultMutableTreeNode) e.nextElement ();
			Object sx = nd.getUserObject ();
			if (sx instanceof SentenceX) {
				((SentenceX) sx).dumpSynonymExclusions (p);
			}
		}
		p.store (out, "---Saved user synonyms---");
		out.close ();
	}

	public static void dumpUserSynonyms (String name) throws FileNotFoundException, IOException {
		java.io.FileOutputStream out = new FileOutputStream (name + ".savu");
		Properties p = new Properties ();
		for (Entry e : SentenceX.usersyns.entrySet ()) {
			java.util.List<String> al = (java.util.List) e.getValue ();
			StringBuffer sb = new StringBuffer ();
			for (String s : al) {
				sb.append (s).append (';');
			}
			p.put (e.getKey (), sb.toString ());
		}
		p.store (out, "---Saved user synonyms---");
		out.close ();
	}

	void readUserSynonyms (String name) throws FileNotFoundException, IOException {
		java.io.FileInputStream in = new FileInputStream (name + ".savu");
		Properties p = new Properties ();
		p.load (in);
		in.close ();
		for (Entry e : p.entrySet ()) {
			String s = (String) e.getKey ();
			String ws = (String) e.getValue ();
			String[] toks = ws.split (";");
			SentenceX.usersyns.put (s, Arrays.asList (toks));
			String keys[] = s.split ("@");
			for (String t : toks) {
				JvxDialogLoader.getGrammarGenerator ().addSynonyms (keys[0], new String[] {t}, keys[1]);
				SentenceX.addUserWord (t);
			}
		}
	}

	void readSynonymSelections (String name) throws FileNotFoundException, IOException {
		java.io.FileInputStream in = new FileInputStream (name + ".savx");
		Properties p = new Properties ();
		p.load (in);
		in.close ();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) theFrame.getDialogTree ().getModel ().getRoot ();
		for (Enumeration e = root.breadthFirstEnumeration (); e.hasMoreElements ();) {
			DefaultMutableTreeNode nd = (DefaultMutableTreeNode) e.nextElement ();
			Object ox = nd.getUserObject ();
			if (ox instanceof SentenceX) {
				SentenceX sx = (SentenceX) ox;
				sx.readSyns (p);

				JvxDialogLoader.getGrammarGenerator ().generateAlts (sx.getSentenceKey ());
				sx.setTheSentence (JvxDialogLoader.getGrammarGenerator ().getSentence (sx.getSentenceKey ()));
			}
		}

	}
}

class DlgTreeModelListener implements TreeModelListener {

	JvxMainFrame theFrame = null;

	public DlgTreeModelListener (JvxMainFrame frame) {
		theFrame = frame;
	}

	void handleChange (TreeModelEvent e) {
		DefaultMutableTreeNode node;
		node = (DefaultMutableTreeNode) (e.getTreePath ().getLastPathComponent ());

		try {
			int inds[] = e.getChildIndices ();
			for (int i : inds) {
				node = (DefaultMutableTreeNode) (node.getChildAt (i));
				//System.out.println("New value: " + node.getUserObject());
				if (node.isRoot () || node.getLevel () <= 1) {
					continue;
				}

				String s = (String) node.getUserObject ();
				Object sx = JvxDialogHelper.createSentence (s.trim ());

				if (sx != null) {
					node.setUserObject (sx);
				}
			}
		} catch (NullPointerException exc) {
		}
		fireMouseclick (e.getTreePath ());

	}

	public void treeNodesChanged (TreeModelEvent e) {
		//System.out.println("treeNodesChanged");

		handleChange (e);
	}

	public void treeNodesInserted (TreeModelEvent e) {
		//System.out.println("TtreeNodesInserted");
	}

	public void treeNodesRemoved (TreeModelEvent e) {
		//System.out.println("treeNodesRemoved");
		// can not register undo here, as this is called post delete
	}

	public void treeStructureChanged (TreeModelEvent e) {
		//System.out.println("treeStructureChanged");
		DefaultMutableTreeNode node;
		node = (DefaultMutableTreeNode) (e.getTreePath ().getLastPathComponent ());
		//JvxDialogHelper.registerUndo(node, theFrame.getDialogTree());
	}

	void fireMouseclick (TreePath tpath) {
		JTree tree = theFrame.getDialogTree ();
		//TreePath tpath = tree.getSelectionPath();
		if (tpath != null) {
			Rectangle rec = tree.getPathBounds (tpath);
			MouseEvent me = new MouseEvent (tree, 0, 0, 0, rec.y, rec.y, 1, false);
			for (MouseListener ml : tree.getMouseListeners ()) {
				ml.mousePressed (me);
			}
		}
	}
}

class WaitCursor {

	Cursor oldC = null;
	boolean b = false;
	JFrame f = null;
	//final static MouseAdapter ma =  new MouseAdapter() {};

	WaitCursor () {
		// TODO - may need a thread to work around a blocked EDT
		f = JvxMainFrame.getInstance ();
		b = JvxMainFrame.getInstance ().getGlassPane ().isVisible ();
		oldC = f.getGlassPane ().getCursor ();
		f.getGlassPane ().setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
		//f.getGlassPane().addMouseListener(ma);
		f.getGlassPane ().setVisible (b ? b : !b);
	}

	void go () {
		f.getGlassPane ().setCursor (oldC);
		f.getGlassPane ().setVisible (b);
		//f.getGlassPane().removeMouseListener(ma);
	}
}

class DialogTreeNodeUndoableDelete extends AbstractUndoableEdit {

	JTree tree_;
	MutableTreeNode node_ = null;
	MutableTreeNode parent_ = null;
	int childIndex_ = 0;

	public DialogTreeNodeUndoableDelete (JTree tree, MutableTreeNode node, MutableTreeNode parent) {
		tree_ = tree;
		node_ = node;
		parent_ = parent;
		childIndex_ = parent_ == null ? 0 : parent_.getIndex (node_);
		basicNode bn = save ((DefaultMutableTreeNode) node_, null);
		bn.SaveAsText (0);
	}

	public void undo () throws CannotUndoException {
		DefaultTreeModel model = (DefaultTreeModel) tree_.getModel ();
		if (parent_ == null) {
		} else {
			DefaultMutableTreeNode p = (DefaultMutableTreeNode) parent_;
			p.insert ((MutableTreeNode) node_, childIndex_);
		}
		model.reload (parent_);
	}

	public void redo () throws CannotRedoException {
		DefaultTreeModel model = (DefaultTreeModel) tree_.getModel ();
		if (parent_ == null) {
		} else {
			DefaultMutableTreeNode p = (DefaultMutableTreeNode) parent_;
			p.remove (node_);
		}
		model.reload (parent_);
	}

	public boolean canUndo () {
		return true;
	}

	public boolean canRedo () {
		return true;
	}

	public String getPresentationName () {
		return "Delete Node";
	}

	basicNode save (DefaultMutableTreeNode node, basicNode root) {
		if (root == null) {
			root = new basicNode (node.getUserObject ());
		}
		for (int i = 0; i < node.getChildCount (); i++) {
			DefaultMutableTreeNode n = (DefaultMutableTreeNode) node.getChildAt (i);
			basicNode child = root.CreateChildNode (n.getUserObject ());
			save (n, child);
		}
		return root;
	}
}

class DialogTreeNodeUndoableInsert extends DialogTreeNodeUndoableDelete {

	public DialogTreeNodeUndoableInsert (JTree tree, MutableTreeNode node, MutableTreeNode parent) {
		super (tree, node, parent);
	}

	public void undo () throws CannotUndoException {
		super.redo ();
	}

	public void redo () throws CannotRedoException {
		super.undo ();
	}

	public String getPresentationName () {
		return "Insert Node";
	}
}
