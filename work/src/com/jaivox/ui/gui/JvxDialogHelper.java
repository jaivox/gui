/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaivox.ui.gui;

import com.jaivox.ui.appmaker.guiprep;
import com.jaivox.ui.gengram.GrammarGenerator;
import com.jaivox.ui.gengram.SentenceX;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;



/**
 *
 * @author lin
 */
public class JvxDialogHelper {
    static String datadir = JvxConfiguration.datadir;
    JvxMainFrame theFrame = null;
    DlgTreeModelListener treeListener;
    
    public JvxDialogHelper(JvxMainFrame frame) {
        super();
        theFrame = frame;
    }
    public JPopupMenu createPopup( DefaultMutableTreeNode rightClickedNode) {
        final DialogMenuAction menuAction = new DialogMenuAction(theFrame.getDialogTree(), rightClickedNode);
        
        JPopupMenu popup = new JPopupMenu();
        JMenuItem addMenuItem = new JMenuItem("Add");
        JMenuItem delMenuItem = new JMenuItem("Delete");
        JMenuItem editMenuItem = new JMenuItem("Edit");
        //JMenuItem synMenuItem = new JMenuItem("Synonyms");
        
        addMenuItem.addActionListener(menuAction);
        delMenuItem.addActionListener(menuAction);
        editMenuItem.addActionListener(menuAction);
        //synMenuItem.addActionListener(menuAction);
        
       
        
        popup.add(addMenuItem);
        popup.add(delMenuItem);
        popup.add(editMenuItem);
        //popup.add(createSynonymsMenu(rightClickedNode.getUserObject()));
        popup.add(theFrame.getSynsHelper().createOkaysSynonymsMenu(rightClickedNode.getUserObject()));
        
        return popup;
    }
    
    public void dialogTreeRClicked(java.awt.event.MouseEvent evt) {
        JTree tree = (JTree)evt.getSource();
            
        if( evt.isPopupTrigger() ) {
            int x = evt.getX();
            int y = evt.getY();
            
            TreePath path = tree.getPathForLocation(x, y);
            if (path == null) return;
            
            DefaultMutableTreeNode rightClickedNode =
                            (DefaultMutableTreeNode)path.getLastPathComponent();

            TreePath[] selectionPaths = tree.getSelectionPaths();
            //check if node was selected
            boolean isSelected = false;
            if (selectionPaths != null) {
                for (TreePath selectionPath : selectionPaths) {
                    if (selectionPath.equals(path)) {
                        isSelected = true;
                    }
                }
            }
            //if clicked node was not selected, select it
            if(!isSelected){
                tree.setSelectionPath(path);
            }
            //if(rightClickedNode.isLeaf()){
                JPopupMenu popup = createPopup(rightClickedNode);
                popup.show(tree, x, y);
           // }
        }
        else {
        }
    }
    public void dumpTreeToFile(String fname) throws IOException {
        java.io.FileOutputStream out = new FileOutputStream(fname);
        //out.write(dumpTree(theFrame.getDialogTree()).getBytes());
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)theFrame.getDialogTree().getModel().getRoot();
        String buf = dumpTree(theFrame.getDialogTree().getModel(), root);
        out.write(buf.getBytes());
    }
    
    private String dumpTree(TreeModel model, DefaultMutableTreeNode node) {
        StringBuffer tdump = new StringBuffer();
        Object sx = node.getUserObject();
        if(sx instanceof SentenceX) {
            String s = ((SentenceX)sx).dump(node.getLevel() - 2); // skip the dialog.road
            tdump.append(s);
        }
        else {
            if(node.getLevel() > 2) tdump.append("\t").append(sx.toString()).append("\n");
        }
        for (int i = 0; i < model.getChildCount(node); i++) {
            String s = dumpTree(model, (DefaultMutableTreeNode)model.getChild(node, i));
            tdump.append( s );
        }
        return tdump.toString();
    }
    public void dumpDialogToFile(String fname) throws IOException {
        java.io.FileOutputStream out = new FileOutputStream(fname);
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)theFrame.getDialogTree().getModel().getRoot();
        StringBuffer tdump = new StringBuffer();
        
        dumpDialog(root, tdump);
        out.write(tdump.toString().getBytes());
    }
    public void dumpDialog(DefaultMutableTreeNode node, StringBuffer tdump) {
        int level = 0;
        while(node != null) {
            level = node.getLevel();
            //System.out.println(level +": "+ node);
            if(level < 2) { 
                node = node.getNextNode();  // skil dialog.road
                continue;
            }
            for(int i = 0; i < level-2; i++) tdump.append('\t');
            tdump.append('(').append(node.toString()).append(')');
            if(!node.isLeaf()) {
                node = (DefaultMutableTreeNode)node.getNextNode();
                tdump.append(" (").append(node.toString()).append(')');
            }
            tdump.append('\n');
            node = node.getNextNode();
        }
    }
    void debugTree(JTree tree) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
        for(Enumeration e = root.breadthFirstEnumeration(); e.hasMoreElements();) {
            DefaultMutableTreeNode nd = (DefaultMutableTreeNode)e.nextElement();
            System.out.println("---" + nd.getLevel() +"---");
            Object sx = nd.getUserObject();
            if(sx instanceof SentenceX) {
                ((SentenceX)sx).debug();
            }
            System.out.println("---" + nd.getLevel() +"---");
            
        }
    }
    void dialogTreeMouseClicked(MouseEvent evt) {
        JTree tree = (JTree)evt.getSource();
        //debugTree(tree);
        if(evt.getClickCount() == 1 || evt.getClickCount() == 2) {
            TreePath tpath = tree.getSelectionPath();
            if(tpath != null) {
                ArrayList<String> oks = new ArrayList<String>();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)tpath.getLastPathComponent();
                System.out.println(node);
                for(Enumeration e = node.breadthFirstEnumeration(); e.hasMoreElements();) {
                    DefaultMutableTreeNode nd = (DefaultMutableTreeNode)e.nextElement();
                    ArrayList<String> al = null;
                    Object sx = nd.getUserObject();
                    if(sx instanceof SentenceX) {
                        al = new ArrayList<String> ();
                        ((SentenceX)sx).generateokays(al);
                    }
                    if(al != null) {
                        oks.addAll(al);
                    }
                    break; ///?
                }
                theFrame.getGrammarList().setListData(oks.toArray());
                theFrame.getSynsHelper().populateSynonymsTab(node.getUserObject());
            }
                
        }
    }

    void generateApp(JvxMainFrame ui) {
        guiprep.generateApp(JvxConfiguration.getConfFile());
    }
}
class DialogMenuAction implements ActionListener {

    private JTree dialogTree = null;
    DefaultMutableTreeNode rightClickedNode = null;
    
    DialogMenuAction(JTree tree, DefaultMutableTreeNode rNode) {
        dialogTree = tree;
        rightClickedNode = rNode;
    }
    
    public void actionPerformed(ActionEvent ae) {
        DefaultTreeModel model = (DefaultTreeModel)dialogTree.getModel();
        JMenuItem mi = (JMenuItem)ae.getSource();
        String action = mi.getText();
        System.out.println("Menu: " + action);
        // TODO - may be a confirm action here
        if(action.equals("Add")) {
            DefaultMutableTreeNode anotherNode = new DefaultMutableTreeNode(" ");
            rightClickedNode.add(anotherNode);
            model.reload(rightClickedNode);
            TreeNode[] nodes = ((DefaultTreeModel) dialogTree.getModel()).getPathToRoot(anotherNode);
            TreePath tpath = new TreePath(nodes);
            //dialogTree.scrollPathToVisible(tpath);
            dialogTree.expandPath(tpath);
            dialogTree.setSelectionPath(tpath);
            //dialogTree.startEditingAtPath(tpath);
        }
        else if(action.equals("Delete")) {
            rightClickedNode.removeAllChildren();
            model.removeNodeFromParent(rightClickedNode);
            rightClickedNode = null;
        }
        else if(action.equals("Edit")) {
            dialogTree.startEditingAtPath(dialogTree.getSelectionPath());
        }
        if(action.equals("Synonyms")) {
            TreePath tpath = dialogTree.getSelectionPath();
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)tpath.getLastPathComponent();
            
            Object sx = node.getUserObject();
            if(sx instanceof SentenceX) {
   
                
            }
        }
        model.reload(rightClickedNode);
    }
}
class DragHandler extends TransferHandler {
    JvxMainFrame theFrame = null;
    public DragHandler(JvxMainFrame frame)
    {
        super();
        theFrame = frame;
    }
    public boolean canImport(TransferSupport support) {
         if (!support.isDrop()) {
             return false;
         }

         return support.isDataFlavorSupported(DataFlavor.stringFlavor);
     }

     public boolean importData(TransferSupport support) {
         if (!canImport(support)) {
           return false;
         }
         Component targ = support.getComponent();
         Transferable transferable = support.getTransferable();
         String line;
         try {
           line = (String) transferable.getTransferData(DataFlavor.stringFlavor);
         } catch (Exception e) {
           return false;
         }
         if(targ instanceof JTable) {
             JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
             theFrame.synsHelper.dropSynonym(line, dl.getRow(), dl.getColumn());
         }
         JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
         
         String[] data = line.split("\n");
         for (String item: data) {
             if (!item.isEmpty()) {
                //DefaultMutableTreeNode node = (DefaultMutableTreeNode)dl.getPath().getLastPathComponent();
                Point p = dl.getDropPoint();
                DefaultMutableTreeNode dropnode = theFrame.getMouseOnNode((int)p.getX(), (int)p.getY());
                DefaultMutableTreeNode dragnode = new DefaultMutableTreeNode(item);
                dropnode.add(dragnode);
                ((DefaultTreeModel)theFrame.getDialogTree().getModel()).reload(dropnode);
                
                Object sx = GrammarGenerator.createSentence(item);
                if(sx != null) dragnode.setUserObject(sx);
             }
         }
         if(data != null && data.length > 0) fireMouseclick(dl.getDropPoint());
         return true;
     }
     void fireMouseclick(Point p) {
        JTree tree = theFrame.getDialogTree();
        //TreePath tpath = tree.getSelectionPath();
        if(p != null) {
            MouseEvent me = new MouseEvent(tree, 0, 0, 0, (int)p.getX(), (int)p.getY(), 1, false);
            for(MouseListener ml: tree.getMouseListeners()) {
                ml.mousePressed(me);
            }
        }
    }
}
class DlgTreeModelListener implements TreeModelListener {
    JvxMainFrame theFrame = null;
    public DlgTreeModelListener(JvxMainFrame frame) {
        theFrame = frame;
    }
    void handleChange(TreeModelEvent e) {
        DefaultMutableTreeNode node;
        node = (DefaultMutableTreeNode)
                 (e.getTreePath().getLastPathComponent());
        try {
            int inds[] = e.getChildIndices();
            for(int i : inds) {
                node = (DefaultMutableTreeNode)(node.getChildAt(i));
                //System.out.println("New value: " + node.getUserObject());
        
                String s = (String) node.getUserObject();
                Object sx = GrammarGenerator.createSentence(s);

                if(sx != null) node.setUserObject(sx);
            }
        } catch (NullPointerException exc) {}
      fireMouseclick(e.getTreePath());

    }
    public void treeNodesChanged(TreeModelEvent e) {
        //System.out.println("treeNodesChanged");
        
        handleChange(e);
    }
    public void treeNodesInserted(TreeModelEvent e) {
        //System.out.println("TtreeNodesInserted");
    }
    public void treeNodesRemoved(TreeModelEvent e) {
        System.out.println("treeNodesRemoved");
    }
    public void treeStructureChanged(TreeModelEvent e) {
        //System.out.println("treeStructureChanged");
    }
    void fireMouseclick(TreePath tpath) {
        JTree tree = theFrame.getDialogTree();
        //TreePath tpath = tree.getSelectionPath();
        if(tpath != null) {
            Rectangle rec = tree.getPathBounds(tpath);
            MouseEvent me = new MouseEvent(tree, 0, 0, 0, rec.y, rec.y, 1, false);
            for(MouseListener ml: tree.getMouseListeners()) {
                ml.mousePressed(me);
            }
        }
    }
}
