/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaivox.ui.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

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

        //a group of JMenuItems
        menuItem = new JMenuItem(MenuAction.MA_NEW);
        menu.add(menuItem);
        menuItem = new JMenuItem(MenuAction.MA_DIALOG_TREE);
        menu.add(menuItem);

        menuItem = new JMenuItem(MenuAction.MA_DATA_FILE);
        menu.add(menuItem);
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
        else if (action.equals(MA_DATA_FILE.getValue(NAME))) {
            
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