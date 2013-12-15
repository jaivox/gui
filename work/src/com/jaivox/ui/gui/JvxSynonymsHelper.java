/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jaivox.ui.gui;

import com.jaivox.ui.gengram.SentenceX;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JPopupMenu;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoableEdit;

public class JvxSynonymsHelper {

	static String datadir = JvxConfiguration.datadir;
	JvxMainFrame theFrame = null;
	static int defaultMaxSyns = 3;

	public JvxSynonymsHelper (JvxMainFrame frame) {
		super ();
		theFrame = frame;
	}

	public JMenu createSynonymsMenu (Object ox) {
		JMenu submenu = new JMenu ("Synonyms");

		if (ox instanceof SentenceX) {
			SentenceX sx = (SentenceX) ox;
			String words[] = sx.getWords ();
			//String syns[] = JvxDialogLoader.gen.getSynonyms( words[words.length/2]);
			int i = 0;
			for (String word : words) {
				String syns[] = JvxDialogLoader.getGrammarGenerator ().getSynonyms (word, sx.getTagFormAt (i));
				if (syns != null) {
					JMenu syMenu = new JMenu (word);
					for (String s : syns) {
						JMenuItem menuItem = new JMenuItem (s);
						syMenu.add (menuItem);
					}
					submenu.add (syMenu);
				}
				i++;
			}

		}
		return submenu;
	}

	public JMenu createOkaysSynonymsMenu (Object ox) {
		JMenu submenu = new JMenu ("Synonyms");

		if (ox instanceof SentenceX) {
			SentenceX sx = (SentenceX) ox;
			String words[] = sx.getWords ();
			SynonymsTableModel model = (SynonymsTableModel) theFrame.getSynsTab ().getModel ();
			int j = 0;
			for (String word : words) {
				int rows = model.getRowCount ();
				JMenu syMenu = null;
				final PopUpMenuAction menuAction = new PopUpMenuAction (theFrame, word);

				for (int i = 0; i < rows; i++) {
					Object v = model.getValueAt (i, j);
					if (v instanceof SynsData) {
						SynsData d = (SynsData) v;
						String s = d.getValue ();
						if (s == null || s.length () <= 0 || word.equals (s)) {
							continue;
						}
						if (syMenu == null) {
							syMenu = new JMenu (word);
						}
						JCheckBox menuItem = new JCheckBox (s);
						menuItem.setSelected (d.getSelected () && !sx.isExcluded (s));
						menuItem.addActionListener (menuAction);
						syMenu.add (menuItem);
					}
				}
				j++;
				if (syMenu != null) {
					submenu.add (syMenu);
				}
			}
			return submenu;
		}
		if (ox instanceof SentenceX) {   // to be removed
			SentenceX sx = (SentenceX) ox;
			String words[] = sx.getWords ();
			String[][] okwords = sx.getOkayWords ();
			for (String word : words) {
				String[] syns = null;
				if (word == null) {
					continue;
				}
				for (String[] oks : okwords) {
					if (oks == null) {
						continue;
					}
					for (String ok : oks) {
						if (ok == null) {
							continue;
						}
						if (ok.equals (word) && oks.length > 1) {
							syns = oks;
							break;
						}
					}
					if (syns != null) {
						break;
					}
				}
				if (syns != null) {
					JMenu syMenu = new JMenu (word);
					final PopUpMenuAction menuAction = new PopUpMenuAction (theFrame, word);

					for (String s : syns) {
						if (word.equals (s)) {
							continue;
						}
						//JCheckBoxMenuItem menuItem = new StayOpenCheckBoxMenuItem(s, true);
						JCheckBox menuItem = new JCheckBox (s);
						menuItem.setSelected (!sx.isExcluded (s));
						menuItem.addActionListener (menuAction);
						syMenu.add (menuItem);
					}
					submenu.add (syMenu);
				}
			}

		}
		return submenu;
	}

	void populateSynonymsTab (Object ox) {
		final JTable table = theFrame.getSynsTab ();

		// disable any custom handlers
		for (TableModelListener l : ((AbstractTableModel) table.getModel ()).getTableModelListeners ()) {
			if (l instanceof TableActionHandler) {
				table.getModel ().removeTableModelListener (l);
			}
		}
		for (MouseListener l : table.getTableHeader ().getMouseListeners ()) {
			if (l instanceof HeaderActionHandler) {
				table.getTableHeader ().removeMouseListener (l);
			}
		}

		if (ox instanceof SentenceX) {
			SentenceX sx = (SentenceX) ox;
			//model.setRowCount(0);
			table.setDefaultRenderer (SynsData.class, new SynsDataRenderer ());
			table.setDefaultEditor (SynsData.class, new SynsDataEditor ());
			SynonymsTableModel model = (SynonymsTableModel) table.getModel ();


			model.setSentence (sx.getSentenceKey (), sx);
			if (sx.getTabModvalues () != null) {
				model.setValues (sx.getTabModvalues ());
				model.setDataVector (null, sx.getWords ());
			} else {
				model.setMaxSynSelections (defaultMaxSyns);
				model.setDataVector (sx.getWordOptions (), sx.getWords ());
				sx.setTabModvalues (model.getValues ());
			}

			table.getTableHeader ().setToolTipText ("Click to Select All");
			table.getTableHeader ().addMouseListener (new HeaderActionHandler () {
				@Override
				public void mouseClicked (MouseEvent e) {
					int viewColumn = table.getTableHeader ().columnAtPoint (e.getPoint ());
					int modelColumn = table.getColumnModel ().getColumn (viewColumn).getModelIndex ();
					//SynsData d  = (SynsData) table.getModel().getValueAt(-1, viewColumn);
					//String s = d.toString();
					//d.setSelected(!d.getSelected());
					table.getModel ().setValueAt (null, -1, modelColumn);
					TableColumn tc = table.getColumnModel ().getColumn (viewColumn);
					tc.setHeaderValue (table.getModel ().getColumnName (modelColumn));
					//System.out.println("TableHeader:mouseClicked: "+ viewColumn +"---"+ s +"---"+ d);
				}
			});

			//model.debug();
			table.setShowGrid (true);
			if (table.getRowCount () == 0) {
				model.insertRow (0);   // atleast one row for the right click to work
			}
			//theFrame.getSynsTab().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			for (int col = 0; col < table.getColumnCount (); col++) {
				int width = 0;
				for (int row = 0; row < table.getRowCount (); row++) {
					TableCellRenderer renderer = table.getCellRenderer (row, col);
					Component comp = table.prepareRenderer (renderer, row, col);
					width = Math.max (comp.getPreferredSize ().width, width);
				}
				TableColumn tc = table.getColumnModel ().getColumn (col);
				if (width > 0) {
					tc.setPreferredWidth (width);
				}
			}
			model.fireTableDataChanged ();
			model.setTabListener (new TableActionHandler (theFrame));
			model.addTableModelListener (model.getTabListener ());  // skip the first fire
		} else {
			SynonymsTableModel model = (SynonymsTableModel) table.getModel ();
			model.setDataVector (new String[][] {}, new String[] {});
		}
	}

	void synsTabMouseRClicked (MouseEvent evt) {
		final PopUpMenuAction menuAction = new PopUpMenuAction (theFrame);

		if (evt.isPopupTrigger ()) {
			int x = evt.getX ();
			int y = evt.getY ();
			JTable table = (JTable) evt.getSource ();
			int row = table.rowAtPoint (evt.getPoint ());
			int column = table.columnAtPoint (evt.getPoint ());

			if (!table.isRowSelected (row)) {
				table.changeSelection (row, column, false, false);
			}
			JPopupMenu popup = new JPopupMenu ();
			JMenuItem selectMenuItem = new JMenuItem ("(Un)Select All");
			JMenuItem addMenuItem = new JMenuItem ("Add Row");
			JMenuItem delMenuItem = new JMenuItem ("Delete");
			JMenuItem editMenuItem = new JMenuItem ("Edit Row");
			JMenuItem dbLinkMenuItem = null;

			selectMenuItem.addActionListener (menuAction);
			addMenuItem.addActionListener (menuAction);
			delMenuItem.addActionListener (menuAction);
			editMenuItem.addActionListener (menuAction);

			popup.add (selectMenuItem);
			popup.add (addMenuItem);
			popup.add (delMenuItem);
			popup.add (editMenuItem);

			// add a message about dragging and dropping database entries
			boolean dataLoaded = JvxDialogLoader.isDataLoaded ();
			if (dataLoaded) {
				// System.out.println ("dataLoaded is true");
				dbLinkMenuItem = new JMenuItem ("Add from database");
				dbLinkMenuItem.addActionListener (menuAction);
				popup.add (dbLinkMenuItem);
			}

			MenuUtils.addUndoRedoMenus (popup);

			popup.show (table, x, y);
		}
	}

	int dropSynonym (String line, int row, int col) {
		int newRow = -1;
		SynonymsTableModel model = (SynonymsTableModel) theFrame.getSynsTab ().getModel ();
		if (row >= model.getRowCount ()) {
			row = model.getRowCount ();
			model.insertRow (row);
		}
		if (!(model.getValueAt (row, col) instanceof SynsData)) {
			if (!model.isInColumn (line, col)) {
				model.setValueAt (line, row, col);
				newRow = row;
			}
		}
		return newRow;
	}
}

class HeaderActionHandler extends MouseAdapter {
}

class TableActionHandler implements TableModelListener {

	JvxMainFrame theFrame = null;
	String columnName = null;
	SynonymsTableModel model = null;
	SynsData data = null;
	boolean regenerate = true;

	public void setRegenerate (boolean regenerate) {
		this.regenerate = regenerate;
	}

	TableActionHandler (JvxMainFrame frame) {
		theFrame = frame;
	}

	@Override
	public void tableChanged (TableModelEvent e) {
		int firstrow = e.getFirstRow ();
		int col = e.getColumn ();
		int lastrow = e.getLastRow ();
		if (col < 0) {
			return;
		}
		model = (SynonymsTableModel) e.getSource ();
		String key = model.getSentence ();
		if (model.getRowCount () <= 0) {
			return;
		}
		boolean regen = false;

		for (int i = firstrow; i <= lastrow; i++) {
			columnName = model.getColumnAt (col);
			Object value = model.getValueAt (i, col);
			System.out.println ("TableModelListener: (" + columnName + ": " + firstrow + "-" + lastrow + ", " + col + ") " + model.getSentence () + " - " + value.toString ());
			if (value instanceof SynsData) {
				data = (SynsData) value;
				String word = data.getValue ().trim ();
				boolean selected = data.getSelected ();

				if (selected && word.length () > 0) {     // add, select
					if (data.isUserWord ()) {             // add
						String tag = model.getSentenceX ().getTagFormAt (col);
						regen = JvxDialogLoader.getGrammarGenerator ().addSynonyms (columnName, new String[] {word}, tag);
						model.getSentenceX ().addUserWord (word);
						model.getSentenceX ().addUserSynonym (columnName, tag, word);
					} else {                              // select
						model.getSentenceX ().removeExclusion (word);
					}
				} else {                                  // unselect
					if (data.isUserWord ()) {
						String tag = model.getSentenceX ().getTagFormAt (col);
						model.getSentenceX ().removeUserWord (word);
						model.getSentenceX ().removeUserSynonym (columnName, tag, word);
						JvxDialogLoader.getGrammarGenerator ().removeSynonym (columnName, word, tag);
					} else {
						model.getSentenceX ().addExclusion (word);
					}
				}
				regen = true;
			} else {
				regen = true;
				System.out.println ("TableModelListener: (" + firstrow + "-" + lastrow + ", " + col + ") " + value.getClass () + " " + value.toString ());
			}
		}
		if (regen) {
			JvxDialogLoader.getGrammarGenerator ().generateAlts (key);
			model.getSentenceX ().setTheSentence (JvxDialogLoader.getGrammarGenerator ().getSentence (key));
			theFrame.getGrammarList ().setListData (model.getSentenceX ().getSentenceOptions ());
		}
	}

	void fireMouseclick () {
		JTree tree = theFrame.getDialogTree ();
		TreePath tpath = tree.getSelectionPath ();
		if (tpath != null) {
			Rectangle rec = tree.getPathBounds (tpath);
			MouseEvent me = new MouseEvent (tree, 0, 0, 0, rec.y, rec.y, 1, false);
			for (MouseListener ml : tree.getMouseListeners ()) {
				ml.mousePressed (me);
			}
		}
	}
}

class PleaseWaitDialog {

	void pleaseWait (final JvxMainFrame parent) {
		final Timer timer = new Timer (100, null);
		timer.setRepeats (true);
		timer.addActionListener (new ActionListener () {
			private int alpha = 255;

			@Override
			public void actionPerformed (ActionEvent e) {
				timer.stop ();
			}
		});

		timer.start ();
	}
}

class PopUpMenuAction implements ActionListener {

	private JvxMainFrame theFrame = null;
	private String word = null;             // an easy way to identify the tab:col

	public PopUpMenuAction (JvxMainFrame frame) {
		theFrame = frame;
	}

	public PopUpMenuAction (JvxMainFrame frame, String w) {
		theFrame = frame;
		word = w;
	}

	public void actionPerformed (ActionEvent ae) {
		JTable table = theFrame.getSynsTab ();
		SynonymsTableModel model = (SynonymsTableModel) table.getModel ();

		if (ae.getSource () instanceof JCheckBox) {
			JCheckBox cb = (JCheckBox) ae.getSource ();
			String syn = cb.getText ().trim ();
			SentenceX sx = (SentenceX) theFrame.getSelectedNode ().getUserObject ();
			if (!cb.isSelected ()) {
				sx.addExclusion (syn);
			}
			{
				int col = model.findColumn (word);
				int row = model.findRow (col, syn);
				model.updateValue (row, col, cb.isSelected ());
				System.out.println ("PopUpMenuAction: Excluded: " + sx.getSentenceKey () + " - " + word + " " + col + " " + cb.getText () + " " + cb.isSelected ());
			}
			return;
		}
		JMenuItem mi = (JMenuItem) ae.getSource ();
		String action = mi.getText ();
		System.out.println ("Menu: " + action);
		// TODO - may be a confirm action here
		if (action.equals ("Add Row")) {
			int rowCount = model.getColumnCount ();
			model.insertRow (rowCount);
			UndoableEdit rowChange = new RowChange (model, rowCount);
			JvxMainFrame.getInstance ().postUndoableEdit (rowChange);
		} else if (action.equals ("Delete")) {
			int r = table.getSelectedRow ();
			int c = table.getSelectedColumn ();
			Object v = model.getValueAt (r, c);
			if (v instanceof SynsData) {  // only if the cell has some data
				SynsData sv = (SynsData) v;
				UndoableEdit change = new CellChange (model, new SynsData (sv.getSelected (), sv.getValue (), sv.isUserWord ()), r, c);

				model.deleteColumn (r, c);
				JvxMainFrame.getInstance ().postUndoableEdit (change);
			}
		} else if (action.equals ("Edit")) {
		} else if (action.equals ("(Un)Select All")) {
			model.selectAll (table.getSelectedColumn ());
		} else if (action.equals ("Add from database")) {
			JOptionPane.showMessageDialog (theFrame, "Drag and drop from data into synonyms table.");
		}
	}
}
