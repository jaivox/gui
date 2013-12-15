/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jaivox.ui.gui;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
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

	public static JMenuBar setMenuBarForFrame (JvxMainFrame frame) {
		JMenuBar menuBar;
		//Create the menu bar.
		menuBar = new JMenuBar ();

		//Build the first menu.
		JMenu menu;
		JMenuItem menuItem;
		menu = new JMenu ("File");
		menu.setMnemonic (KeyEvent.VK_F);
		menu.getAccessibleContext ().setAccessibleDescription ("Choose Dialog Input Files");
		menuBar.add (menu);

		menuItem = new JMenuItem (MenuAction.MA_NEW);
		menu.add (menuItem);
		menuItem = new JMenuItem (MenuAction.MA_DIALOG_TREE);
		menu.add (menuItem);
		// menuItem = new JMenuItem(MenuAction.MA_DATA_FILE);
		// menu.add(menuItem);

		RecentFileAction.MENU_RECENT.addMenuListener (RecentFileAction.MA_RECENT);
		menu.add (RecentFileAction.MENU_RECENT);

		menuItem = new JMenuItem (RealodAction.MA_RELOAD);
		menu.add (menuItem);


		final JMenu edmenu = new JMenu ("Edit");
		edmenu.setMnemonic (KeyEvent.VK_E);
		edmenu.getAccessibleContext ().setAccessibleDescription ("Edit");
		edmenu.addMenuListener (
				new MenuListener () {
			@Override
			public void menuSelected (MenuEvent e) {
				edmenu.getItem (0).setEnabled (JvxMainFrame.undoManager_.canUndo ());
				edmenu.getItem (1).setEnabled (JvxMainFrame.undoManager_.canRedo ());
			}

			@Override
			public void menuDeselected (MenuEvent e) {
			}

			@Override
			public void menuCanceled (MenuEvent e) {
			}
		});
		menuBar.add (edmenu);
		addUndoRedoMenus (edmenu.getPopupMenu ());

		// Help
		menu = new JMenu ("Help");
		menu.setMnemonic (KeyEvent.VK_H);
		menuItem = new JMenuItem (HelpAction.MA_Help);
		menu.add (menuItem);
		menuItem = new JMenuItem (HelpAction.MA_ONLINE);
		menu.add (menuItem);
		menuItem = new JMenuItem (HelpAction.MA_ABOUT);
		menu.add (menuItem);
		menuBar.add (menu);

		frame.setJMenuBar (menuBar);
		return menuBar;
	}

	public static void openDialogFileMenu (java.awt.event.MouseEvent evt) {
		JPopupMenu popup = new JPopupMenu ();
		JMenuItem item = new JMenuItem (MenuAction.MA_NEW);
		popup.add (item);
		item = new JMenuItem (MenuAction.MA_DIALOG_TREE);
		popup.add (item);
		popup.show (evt.getComponent (), evt.getX (), evt.getY ());
	}

	public static void addUndoRedoMenus (JPopupMenu parent) {
		JMenuItem undoMenuItem = new JMenuItem (UndoRedoAction.MA_UNDO);
		JMenuItem redoMenuItem = new JMenuItem (UndoRedoAction.MA_REDO);

		undoMenuItem.setEnabled (JvxMainFrame.undoManager_.canUndo ());
		parent.add (undoMenuItem);

		redoMenuItem.setEnabled (JvxMainFrame.undoManager_.canRedo ());
		parent.add (redoMenuItem);
	}

	public static String fileDialog (Component parent, int option, String startAt, String title) {
		String appfolder = JvxConfiguration.theConfig ().getAppFolder ();
		final JFileChooser fc = new JFileChooser (new File (appfolder == null ? "" : appfolder));
		String loc = null;
		fc.setDialogTitle (title);
		fc.setFileSelectionMode (option);
		fc.setCurrentDirectory (new File (startAt));
		int returnVal = fc.showOpenDialog (parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			loc = fc.getSelectedFile ().getAbsolutePath ();
		}
		return loc;
	}

	public static JMenuItem[] addSubMenus (List subs, JMenu parent, ActionListener l) {
		if (subs == null || subs.size () <= 0) {
			return null;
		}
		JMenuItem[] mis = new JMenuItem[subs.size ()];
		int i = 0;
		for (Object o : subs) {
			if (o == null) {
				continue;
			}
			String s = o.toString ().trim ();
			if (s.length () <= 0) {
				continue;
			}
			mis[i] = new JMenuItem (s);
			mis[i].addActionListener (l);
			parent.add (mis[i]);
			i++;
		}
		return mis;
	}
}

class MenuAction extends AbstractAction {

	public static MenuAction MA_NEW = new MenuAction ("New", KeyEvent.VK_N);
	public static MenuAction MA_DIALOG_TREE = new MenuAction ("Open Dialog Tree", KeyEvent.VK_T);
	//  public static MenuAction MA_DATA_FILE = new MenuAction("Open Data File", KeyEvent.VK_D);

	public MenuAction (String text, Integer mnemonic) {
		super (text);
		putValue (SHORT_DESCRIPTION, text);
		putValue (MNEMONIC_KEY, mnemonic);
	}

	public void actionPerformed (ActionEvent e) {
		String action = e.getActionCommand ();
		System.out.println ("MenuAction: " + e.getActionCommand ());

		JvxMainFrame xframe = JvxMainFrame.getInstance ();
		if (action.equals (MA_DIALOG_TREE.getValue (NAME))) {
			String s = MenuUtils.fileDialog (xframe, JFileChooser.FILES_ONLY, "", "Choose Dialog file");

			if (s != null) {
				RecentFileHistory.getHistory ().add (s);
				xframe.dlgLoader.loadDialogFile (s);
			}
		} else if (action.equals (MA_NEW.getValue (NAME))) {
			JvxMainFrame.getInstance ().dlgHelper.newDialog ();
		}
	}
}

class RecentFileAction extends AbstractAction implements MenuListener {

	public static RecentFileAction MA_RECENT = new RecentFileAction ("Open Recent File", KeyEvent.VK_F);
	public static JMenu MENU_RECENT = new JMenu (MA_RECENT);

	public RecentFileAction (String text, Integer mnemonic) {
		super (text);
		putValue (SHORT_DESCRIPTION, text);
		putValue (MNEMONIC_KEY, mnemonic);
	}

	public void actionPerformed (ActionEvent e) {
		String action = e.getActionCommand ();
		System.out.println ("RecentFileAction: " + e.getActionCommand ());

		JvxMainFrame xframe = JvxMainFrame.getInstance ();
		File hf = new File (action);
		if (hf.exists ()) {
			RecentFileHistory.getHistory ().add (action);
			xframe.dlgLoader.loadDialogFile (action);
		}
	}

	@Override
	public void menuSelected (MenuEvent e) {
		MENU_RECENT.removeAll ();
		List files = Arrays.asList (RecentFileHistory.getHistory ().get ());
		Collections.reverse (files);
		MenuUtils.addSubMenus (files, MENU_RECENT, MA_RECENT);
	}

	@Override
	public void menuDeselected (MenuEvent e) {
	}

	@Override
	public void menuCanceled (MenuEvent e) {
	}
}

class RealodAction extends AbstractAction {

	public static RealodAction MA_RELOAD = new RealodAction ("Reload Dialog", KeyEvent.VK_R);

	public RealodAction (String text, Integer mnemonic) {
		super (text);
		putValue (SHORT_DESCRIPTION, text);
		putValue (MNEMONIC_KEY, mnemonic);
	}

	public void actionPerformed (ActionEvent e) {
		String action = e.getActionCommand ();
		System.out.println ("RealodAction: " + e.getActionCommand ());

		JvxMainFrame xframe = JvxMainFrame.getInstance ();
		String s = MenuUtils.fileDialog (xframe, JFileChooser.FILES_ONLY, "", "Choose Dialog file to Realod");
		if (s == null) {
			return;
		}
		xframe.dlgLoader.loadDialogFile (s);
		String synsfile = s.substring (0, s.lastIndexOf ('.'));
		try {
			xframe.dlgHelper.readUserSynonyms (synsfile);
			xframe.dlgHelper.readSynonymSelections (synsfile);
		} catch (Exception ex) {
			ex.printStackTrace ();
		}
	}
}

class UndoRedoAction extends AbstractAction {

	public static UndoRedoAction MA_UNDO = new UndoRedoAction ("Undo");
	public static UndoRedoAction MA_REDO = new UndoRedoAction ("Redo");

	public UndoRedoAction (String text) {
		super (text);
		putValue (SHORT_DESCRIPTION, text);
		//putValue(MNEMONIC_KEY, mnemonic);
	}

	public void actionPerformed (ActionEvent e) {
		String action = e.getActionCommand ();
		JMenuItem mi = (JMenuItem) e.getSource ();

		System.out.println ("UndoRedoAction: " + e.getActionCommand ());

		JvxMainFrame xframe = JvxMainFrame.getInstance ();
		if (action.equals (MA_UNDO.getValue (NAME))) {
			JvxMainFrame.undoManager_.undo ();
			mi.setEnabled (JvxMainFrame.undoManager_.canUndo ());
		} else if (action.equals (MA_REDO.getValue (NAME))) {
			JvxMainFrame.undoManager_.redo ();
			mi.setEnabled (JvxMainFrame.undoManager_.canRedo ());
		}
	}
}

class HelpAction extends AbstractAction {

	public static HelpAction MA_Help = new HelpAction ("Help Contents", KeyEvent.VK_H);
	public static HelpAction MA_ONLINE = new HelpAction ("Online Docs", KeyEvent.VK_D);
	public static HelpAction MA_ABOUT = new HelpAction ("About", KeyEvent.VK_A);

	public HelpAction (String text, Integer mnemonic) {
		super (text);
		putValue (SHORT_DESCRIPTION, text);
		putValue (MNEMONIC_KEY, mnemonic);
	}

	public void actionPerformed (ActionEvent e) {
		String action = e.getActionCommand ();
		System.out.println ("HelpAction: " + e.getActionCommand ());

		JvxMainFrame xframe = JvxMainFrame.getInstance ();
		if (action.equals (MA_Help.getValue (NAME))) {
			String urlPath = JvxMainFrame.urlDirectory + JvxConfiguration.getHelpURL (null);
			JvxMainFrame.showHelp (urlPath);
		} else if (action.equals (MA_ONLINE.getValue (NAME))) {
			try {
				Desktop.getDesktop ().browse (new URL ("http://www.jaivox.com/documentation.html").toURI ());
			} catch (Exception ex) {
				Logger.getLogger (HelpAction.class.getName ()).log (Level.SEVERE, null, ex);
			}
		}
	}
}

class UndoAdapter implements UndoableEditListener {

	public void undoableEditHappened (UndoableEditEvent evt) {
		UndoableEdit edit = evt.getEdit ();
		JvxMainFrame.undoManager_.addEdit (edit);
		//refreshUndoRedo();
	}
}

class RecentFileHistory {

	private static RecentFileHistory history = null;

	public static RecentFileHistory getHistory () {
		if (history == null) {
			history = new RecentFileHistory ();
		}
		return history;
	}

	public static void loadHistory () throws BackingStoreException {
		Preferences rfh = Preferences.userNodeForPackage (RecentFileHistory.class);
		for (String s : rfh.keys ()) {
			RecentFileHistory.getHistory ().files.push (s);
		}
	}

	public static void flush () throws BackingStoreException {
		Preferences rfh = Preferences.userNodeForPackage (RecentFileHistory.class);
		rfh.clear ();
		for (String s : RecentFileHistory.getHistory ().files) {
			rfh.put (s, s);
		}
		rfh.flush ();
	}
	private Stack<String> files = new Stack<String> ();

	public void add (String f) {
		if (files.contains (f)) {
			files.remove (f);
		}
		files.push (f);
		if (files.size () > 5) {
			files.setSize (5);
		}
	}

	public String[] get () {
		return files.toArray (new String[1]);
	}
}