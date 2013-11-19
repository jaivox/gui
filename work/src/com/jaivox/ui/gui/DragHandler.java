/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaivox.ui.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.undo.UndoableEdit;


/**
 *
 * @author rj
 */
class DragHandler extends TransferHandler {
    JvxMainFrame theFrame = null;
    Component dropTarget = null;
    String[] dropItem = null;

    public DragHandler() {
        super();
        theFrame = JvxMainFrame.getInstance();
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
        dropTarget = support.getComponent();
        Transferable transferable = support.getTransferable();
        try {
            String s = (String) transferable.getTransferData(DataFlavor.stringFlavor);
            dropItem = s.split("\n");
        } catch (Exception e) {
            return false;
        }
        handleDrop(support);
        return true;
    }
    
    void fireMouseclick(Point p) {
        JTree tree = theFrame.getDialogTree();
        //TreePath tpath = tree.getSelectionPath();
        if (p != null) {
            MouseEvent me = new MouseEvent(tree, 0, 0, 0, (int) p.getX(), (int) p.getY(), 1, false);
            for (MouseListener ml : tree.getMouseListeners()) {
                ml.mousePressed(me);
            }
        }
    }

    protected void handleDrop(TransferSupport support) {
        
    }
    
}

class SynsTabDNDHandler extends DragHandler {
    protected void handleDrop(TransferSupport support) {
        JTable table = theFrame.getSynsTab();
        SynonymsTableModel model = (SynonymsTableModel) table.getModel();
        
        JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
        int row = dl.getRow();
        int col = dl.getColumn();
        WaitCursor c = new WaitCursor();
        
        for(String item : dropItem) {
            row = theFrame.synsHelper.dropSynonym(item, row, col);
            Object v = model.getValueAt(row, col);
            if (v instanceof SynsData) {
                SynsData sv = (SynsData) v;
                UndoableEdit change = new CellChangeInsert(model, 
                        new SynsData(sv.getSelected(), sv.getValue(), sv.isUserWord()), 
                        row, col);
                JvxMainFrame.getInstance().postUndoableEdit(change);
            }
            row++;
        }
        c.go();
    }
}

class DialogTreeDNDHandler extends DragHandler {
    protected void handleDrop(TransferSupport support) {
        JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
        for (String item : dropItem) {
            if (!item.isEmpty()) {
                //DefaultMutableTreeNode node = (DefaultMutableTreeNode)dl.getPath().getLastPathComponent();
                Point p = dl.getDropPoint();
                DefaultMutableTreeNode dropnode = theFrame.getMouseOnNode((int) p.getX(), (int) p.getY());
                DefaultMutableTreeNode dragnode = new DefaultMutableTreeNode(item);
                dropnode.add(dragnode);
                ((DefaultTreeModel) theFrame.getDialogTree().getModel()).reload(dropnode);
                if (!dragnode.isRoot() && dragnode.getLevel() > 1) {
                    Object sx = JvxDialogHelper.createSentence(item);
                    if (sx != null) {
                        dragnode.setUserObject(sx);
                    }
                }
                JvxDialogHelper.registerUndoAddNode(dragnode, theFrame.getDialogTree());
            }
        }
        if (dropItem != null && dropItem.length > 0) {
            fireMouseclick(dl.getDropPoint());
        }
    }
}