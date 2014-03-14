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

import com.jaivox.ui.gengram.SentenceX;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author rj
 */
class DialogMenuAction implements ActionListener {

	public void actionPerformed (ActionEvent ae) {
		JTree dialogTree = JvxMainFrame.getInstance ().getDialogTree ();
		DefaultTreeModel model = (DefaultTreeModel) dialogTree.getModel ();
		TreePath tpath = dialogTree.getSelectionPath ();
		DefaultMutableTreeNode rightClickedNode = (DefaultMutableTreeNode) tpath.getLastPathComponent ();
		String action = ae.getActionCommand ();
		System.out.println ("DialogMenuAction: " + action);
		TreePath newpath = null;
		// TODO - may be a confirm action here
		if (action.equals ("Add")) {

			DefaultMutableTreeNode anotherNode = new DefaultMutableTreeNode ("");
			rightClickedNode.add (anotherNode);
			model.reload (rightClickedNode);
			TreeNode[] nodes = model.getPathToRoot (anotherNode);
			newpath = new TreePath (nodes);
			//dialogTree.scrollPathToVisible(tpath);
			dialogTree.expandPath (newpath);
			dialogTree.setSelectionPath (newpath);
			//dialogTree.startEditingAtPath(tpath);
			JvxDialogHelper.registerUndoAddNode (anotherNode, dialogTree);

		} else if (rightClickedNode != null && action.equals ("Delete")) {
			if (rightClickedNode.isRoot () && rightClickedNode.isLeaf ()) {
				return;
			}
			rightClickedNode = (DefaultMutableTreeNode) (rightClickedNode.isRoot () ? rightClickedNode.getChildAt (0) : rightClickedNode);
			UndoableEdit rowChange = new DialogTreeNodeUndoableDelete (dialogTree, rightClickedNode, (MutableTreeNode) rightClickedNode.getParent ());
			if (rightClickedNode.isRoot ()) {
				//rightClickedNode.removeAllChildren();
				//model.nodeStructureChanged(rightClickedNode); // update tree
				model.removeNodeFromParent ((MutableTreeNode) rightClickedNode.getChildAt (0));
			} else {
				model.removeNodeFromParent (rightClickedNode); // model calls nodesWereRemoved
			}
			JvxMainFrame.getInstance ().postUndoableEdit (rowChange);
			// clear the right side table and list
			JvxMainFrame.getInstance ().getGrammarList ().setListData (new String[] {});
			JvxMainFrame.getInstance ().getSynsHelper ().populateSynonymsTab ("");
		} else if (action.equals ("Edit")) {
			dialogTree.startEditingAtPath (dialogTree.getSelectionPath ());
		}
		if (action.equals ("Synonyms")) {
			Object sx = rightClickedNode.getUserObject ();
			if (sx instanceof SentenceX) {
			}
		}
		model.reload (rightClickedNode);
		if (newpath != null) {
			dialogTree.startEditingAtPath (newpath);
		}
	}
}
