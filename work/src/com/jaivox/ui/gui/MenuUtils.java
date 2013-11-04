/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaivox.ui.gui;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author rj
 */
public class MenuUtils {

    public static JMenuBar setMenuBarForFrame(JvxMainFrame frame) {
        JMenuBar menuBar;
        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        JMenu menu;
        JMenuItem menuItem;
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("Choose Dialog Input Files");
        menuBar.add(menu);

        menuItem = new JMenuItem(MenuAction.MA_NEW);
        menu.add(menuItem);
        menuItem = new JMenuItem(MenuAction.MA_DIALOG_TREE);
        menu.add(menuItem);
        menuItem = new JMenuItem(MenuAction.MA_DATA_FILE);
        menu.add(menuItem);

        final JMenu edmenu = new JMenu("Edit");
        edmenu.setMnemonic(KeyEvent.VK_E);
        edmenu.getAccessibleContext().setAccessibleDescription("Edit");
        edmenu.addMenuListener(
                new MenuListener() { 
                    @Override
                    public void menuSelected(MenuEvent e) {
                        edmenu.getItem(0).setEnabled(JvxMainFrame.undoManager_.canUndo ());
                        edmenu.getItem(1).setEnabled(JvxMainFrame.undoManager_.canRedo ());
                    }
                    @Override
                    public void menuDeselected(MenuEvent e) {
                    }
                    @Override
                    public void menuCanceled(MenuEvent e) {
                    }
                });
        menuBar.add(edmenu);
        addUndoRedoMenus(edmenu.getPopupMenu());
        
        // Help
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menuItem = new JMenuItem(HelpAction.MA_Help);
        menu.add(menuItem);
        menuItem = new JMenuItem(HelpAction.MA_ONLINE);
        menu.add(menuItem);
        menuItem = new JMenuItem(HelpAction.MA_ABOUT);
        menu.add(menuItem);
        menuBar.add(menu);
        
        frame.setJMenuBar(menuBar);
        return menuBar;
    }
    public static void openDialogFileMenu(java.awt.event.MouseEvent evt) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem item = new JMenuItem(MenuAction.MA_NEW);
        popup.add(item);
        item = new JMenuItem(MenuAction.MA_DIALOG_TREE);
        popup.add(item);
        popup.show(evt.getComponent(), evt.getX(), evt.getY());
    }

    public static void addUndoRedoMenus(JPopupMenu parent) {
        JMenuItem undoMenuItem = new JMenuItem(UndoRedoAction.MA_UNDO);
        JMenuItem redoMenuItem = new JMenuItem(UndoRedoAction.MA_REDO);
        
        undoMenuItem.setEnabled ( JvxMainFrame.undoManager_.canUndo () );
        parent.add(undoMenuItem);
        
        redoMenuItem.setEnabled ( JvxMainFrame.undoManager_.canRedo () );
        parent.add(redoMenuItem);
    }
}
class MenuAction extends AbstractAction {
    public static MenuAction MA_NEW = new MenuAction("New", KeyEvent.VK_T);
    public static MenuAction MA_DIALOG_TREE = new MenuAction("Open Dialog Tree", KeyEvent.VK_T);
    public static MenuAction MA_DATA_FILE = new MenuAction("Open Data File", KeyEvent.VK_T);
    
    public MenuAction(String text, Integer mnemonic) {
        super(text);
        putValue(SHORT_DESCRIPTION, text);
        putValue(MNEMONIC_KEY, mnemonic);
    }
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        System.out.println("MenuAction: " + e.getActionCommand());
        
        JvxMainFrame xframe = JvxMainFrame.getInstance();
        if(action.equals(MA_DIALOG_TREE.getValue(NAME))) {
            String s = fileDialog(xframe, JFileChooser.FILES_ONLY, "", "Choose Dialog file");
            
            if(s != null) xframe.dlgLoader.loadDialogFile(s);
        }
        else if (action.equals(MA_NEW.getValue(NAME))) {
            JvxMainFrame.getInstance().dlgLoader.newDialog();
        }
    }
    
    public String fileDialog(Component parent, int option, String startAt, String title) {
        String appfolder = JvxConfiguration.theConfig().getAppFolder();
        final JFileChooser fc = new JFileChooser(new File(appfolder == null ? "" : appfolder));
        String loc = null;
        fc.setDialogTitle(title);
        fc.setFileSelectionMode(option);
        fc.setCurrentDirectory(new File(startAt));
        int returnVal = fc.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            loc = fc.getSelectedFile().getAbsolutePath();
        } 
        return loc;
    }
}


class UndoRedoAction extends AbstractAction {
    public static UndoRedoAction MA_UNDO = new UndoRedoAction("Undo");
    public static UndoRedoAction MA_REDO = new UndoRedoAction("Redo");
    
    public UndoRedoAction(String text) {
        super(text);
        putValue(SHORT_DESCRIPTION, text);
        //putValue(MNEMONIC_KEY, mnemonic);
    }
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        JMenuItem mi = (JMenuItem)e.getSource();
        
        System.out.println("UndoRedoAction: " + e.getActionCommand());
        
        JvxMainFrame xframe = JvxMainFrame.getInstance();
        if(action.equals(MA_UNDO.getValue(NAME))) {
            JvxMainFrame.undoManager_.undo ();
            mi.setEnabled (JvxMainFrame.undoManager_.canUndo ());
        }
        else if (action.equals(MA_REDO.getValue(NAME))) {
            JvxMainFrame.undoManager_.redo ();
            mi.setEnabled (JvxMainFrame.undoManager_.canRedo ());
        }
    }
}
class HelpAction extends AbstractAction {
    public static HelpAction MA_Help = new HelpAction("Help Contents", KeyEvent.VK_H);
    public static HelpAction MA_ONLINE = new HelpAction("Online Docs", KeyEvent.VK_D);
    public static HelpAction MA_ABOUT = new HelpAction("About", KeyEvent.VK_A);
    
    public HelpAction(String text, Integer mnemonic) {
        super(text);
        putValue(SHORT_DESCRIPTION, text);
        putValue(MNEMONIC_KEY, mnemonic);
    }
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        System.out.println("HelpAction: " + e.getActionCommand());
        
        JvxMainFrame xframe = JvxMainFrame.getInstance();
        if(action.equals(MA_Help.getValue(NAME))) {
        }
        else if (action.equals(MA_ONLINE.getValue(NAME))) {
            try {
                Desktop.getDesktop().browse(new URL("http://www.jaivox.com/documentation.html").toURI());
            } catch (Exception ex) {
                Logger.getLogger(HelpAction.class.getName()).log(Level.SEVERE, null, ex);
            }
      }
    }
}
class UndoAdapter implements UndoableEditListener {
     public void undoableEditHappened (UndoableEditEvent evt) {
     	UndoableEdit edit = evt.getEdit();
     	JvxMainFrame.undoManager_.addEdit( edit );
     	//refreshUndoRedo();
     }
  }