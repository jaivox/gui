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
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 *
 * @author rj
 */
class SynonymsTableModel extends AbstractTableModel {

	String sentence = null;
	SentenceX theSentence = null;
	private ArrayList<String> names = null;
	private ArrayList<ArrayList<Object>> values;
	private ArrayList<Boolean> colSelected = null;
	private int maxSynSelections = -1;
	TableModelListener tabListener = null;

	public TableModelListener getTabListener () {
		return tabListener;
	}

	public void setTabListener (TableModelListener tabListener) {
		this.tabListener = tabListener;
	}

	public int getMaxSynSelections () {
		return maxSynSelections;
	}

	public void setMaxSynSelections (int maxSynSelections) {
		this.maxSynSelections = maxSynSelections;
	}

	public SynonymsTableModel () {
		init ();
	}

	private void init () {
		values = new ArrayList<ArrayList<Object>> ();
		names = new ArrayList<String> ();
		colSelected = new ArrayList<Boolean> ();
	}

	public void setSentence (String s, SentenceX sx) {
		sentence = s;
		this.theSentence = sx;
	}

	public String getSentence () {
		return sentence;
	}

	public SentenceX getSentenceX () {
		return theSentence;
	}

	public ArrayList<String> getNames () {
		return names;
	}

	public void setNames (ArrayList<String> names) {
		this.names = names;
	}

	public ArrayList<ArrayList<Object>> getValues () {
		return values;
	}

	public void setValues (ArrayList<ArrayList<Object>> values) {
		this.values = values;
	}

	@Override
	public int getRowCount () {
		return values.size ();
	}

	@Override
	public int getColumnCount () {
		return names.size ();
	}

	static Object defaultValue (int row, int col) {
		return "";
		//return new SynsData(false, "");
	}

	String getHeader (int col) {
		// UC tick mark
		String qm = colSelected.get (col) ? Character.toString ('\u2714') : Character.toString ('\u2713');
		return qm + names.get (col);
	}

	@Override
	public Object getValueAt (int row, int col) {
		//if(row == -1) return heads.get(col);
		if (row == -1) {
			return getHeader (col);
		}
		if (row >= getRowCount ()) {
			return defaultValue (row, col);
		}
		if (col >= getColumnCount ()) {
			return new SynsData (false, "");
		}

		ArrayList rows = values == null ? null : values.get (row);

		return (rows == null || col >= rows.size ()) ? defaultValue (row, col) : rows.get (col);
	}

	@Override
	public void setValueAt (Object value, int row, int col) {
		//System.out.println("setValueAt: (" + row +", "+ col +") "+ value.getClass() +" "+ value.toString());

		if (row == -1 && value == null) {
			selectAll (col);
			return;
		}
		if (row >= getRowCount ()) {
			return;
		}
		if (col >= getColumnCount ()) {
			return;
		}
		if (value == null) {
			return;
		}
		SynsData prev = new SynsData (Boolean.FALSE, "");
		if (value instanceof String) {
			String v = (String) value;
			if (v.length () > 0) {
				values.get (row).set (col, new SynsData (Boolean.TRUE, v, true));
			}
		} else if (value instanceof Boolean) {
			SynsData d = (SynsData) values.get (row).get (col);
			prev.setSelected (d.getSelected ());
			prev.setValue (d.getValue ());

			d.setSelected ((Boolean) value);
			if (!d.getSelected ()) {
				this.colSelected.set (col, Boolean.FALSE);
			}
		} else if (value instanceof SynsData) {   // undo
			SynsData d = (SynsData) value;
			values.get (row).set (col, new SynsData (d.getSelected (), d.getValue (), d.isUserWord ()));
		}
		if (!prev.equals (values.get (row).get (col))) {
			this.fireTableCellUpdated (row, col);
			//fireTableRowsUpdated(row, col);
			//this.fireTableDataChanged();
		}
	}

	@Override
	public Class<?> getColumnClass (int col) {
		return SynsData.class;
		//return getValueAt(0, col).getClass();
	}

	@Override
	public String getColumnName (int col) {
		//return names.get(col);
		String s = getHeader (col);
		return s;
	}

	public String getColumnAt (int col) {
		return names.get (col);
	}

	@Override
	public boolean isCellEditable (int row, int col) {
		return true;
	}

	void setDataVector (Object[][] rows, String[] words) {
		names = new ArrayList<String> ();
		colSelected = new ArrayList<Boolean> ();

		for (String word : words) {
			names.add (word);
			colSelected.add (Boolean.FALSE);
		}

		if (rows != null) {
			values = new ArrayList<ArrayList<Object>> ();

			int rowcount = 0;
			for (Object[] row : rows) {
				ArrayList<Object> rd = new ArrayList ();
				int col = 0;
				for (Object cell : row) {
					String v = (String) cell;
					if (v == null || v.length () <= 0) {
						rd.add (defaultValue (0, 0));
					} else {
						if (!names.get (col).equals (v)) {
							boolean f = theSentence.isExcluded (v) ? false : true;
							if (maxSynSelections >= 0 && rowcount >= maxSynSelections) {
								f = false;
								theSentence.addExclusion (v);
								System.out.println ("Selection false for " + v);
							}
							rd.add (new SynsData (f, (String) cell));
							colSelected.set (col, f);
						} else {
							rd.add (defaultValue (0, 0));
						}
					}
					col++;
				}
				values.add (rd);
				rowcount++;
			}
			for (Iterator<ArrayList<Object>> it = values.iterator (); it.hasNext ();) {
				boolean empty = true;
				ArrayList<Object> row = it.next ();
				for (Object cell : row) {
					if (cell != null && !cell.equals (defaultValue (0, 0))) {
						empty = false;
						break;
					}
				}
				if (empty) {
					it.remove ();
				}
				empty = true;
			}
		}
		maxSynSelections = -1;
		this.fireTableStructureChanged ();
	}

	public void insertRow (int at) {
		ArrayList<Object> rd = new ArrayList ();
		for (int i = 0; i < names.size (); i++) {
			rd.add ("");
		}
		values.add (rd);
		this.fireTableRowsInserted (at, at);
		//this.fireTableChanged(null);
	}

	public void removeRow (int at) {
		int n = values.size ();
		if (n > 0) {
			values.remove (n - 1);
		}
		this.fireTableRowsDeleted (at, at);
		System.out.println ("Removed row at " + at);
	}

	public void deleteColumn (int row, int col) {
		this.setValueAt (Boolean.FALSE, row, col);  // unselect - required to regen the options
		values.get (row).set (col, defaultValue (0, 0));  // actually delete the cell text
		this.fireTableChanged (new TableModelEvent (this, row, row, col, TableModelEvent.DELETE));
	}

	void debug () {
		StringBuffer sb = new StringBuffer ();
		for (String name : names) {
			sb.append (name).append (", ");
		}
		System.out.println (sb.toString ());

		for (ArrayList cells : values) {
			for (Object cell : cells) {
				if (cell instanceof SynsData) {
					((SynsData) cell).debug ();
				} else {
					System.out.println ("<String>");
				}
			}
		}
	}

	boolean isInColumn (String line, int col) {
		return findRow (col, line) != -1;
	}

	@Override
	public int findColumn (String columnName) {
		for (int i = 0; i < getColumnCount (); i++) {
			if (columnName.equals (names.get (i))) {
				return i;
			}
		}
		return -1;
	}

	int findRow (int col, String syn) {
		int i = 0;
		for (ArrayList cells : values) {
			if (cells.get (col) instanceof SynsData) {
				SynsData sd = (SynsData) cells.get (col);
				if (sd.getValue ().equals (syn)) {
					return i;
				}
			}
			i++;
		}
		return -1;
	}

	void selectAll (int col) {
		TableActionHandler l = (TableActionHandler) tabListener;
		Boolean b = !colSelected.get (col);
		int lastrow = 0, i = 0;
		for (ArrayList ar : values) {
			Object s = ar.get (col);
			if (s instanceof SynsData) {
				SynsData v = (SynsData) s;
				v.setSelected (b);
				lastrow = i;
			}
			i++;
		}

		colSelected.set (col, b);
		this.fireTableChanged (new TableModelEvent (this, 0, lastrow, col));
	}

	void updateValue (int row, int col, boolean selected) {
		Object o = getValueAt (row, col);
		if (o instanceof SynsData) {
			//SynsData sd = new SynsData(selected, ((SynsData)o).getValue());
			this.setValueAt (selected, row, col);
		}
	}
}

class SynsData implements Comparable<SynsData> {

	private Boolean selected;
	private String value;
	private Boolean userWord = false;

	public SynsData (Boolean selected, String value) {
		this.selected = selected;
		this.value = value;
	}

	public SynsData (Boolean selected, String value, Boolean uw) {
		this.selected = selected;
		this.value = value;
		userWord = uw;
	}

	@Override
	public String toString () {
		return value.toString () + " - " + selected.toString ();
	}

	void debug () {
		System.out.println ("SynsData (" + value + "," + selected.toString () + ")");
	}

	public Boolean getSelected () {
		return selected;
	}

	public void setSelected (Boolean selected) {
		this.selected = selected;
	}

	public String getValue () {
		return value;
	}

	public void setValue (String value) {
		this.value = value;
	}

	@Override
	public int compareTo (SynsData v) {
		return this.value.compareTo (v.value);
	}

	@Override
	public boolean equals (Object v) {
		return v instanceof SynsData && this.selected == ((SynsData) v).selected
				&& this.value.equals (((SynsData) v).value);
	}

	@Override
	public int hashCode () {
		return this.value.hashCode ();
	}

	public Boolean isUserWord () {
		return userWord;
	}

	public void setIsUserWord (Boolean userWord) {
		this.userWord = userWord;
	}
}

class ComboRenderer implements TableCellRenderer {

	public Component getTableCellRendererComponent (
			JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int col) {

		System.out.println ("ComboRenderer: (" + row + ", " + col + ") " + value.getClass () + " " + value.toString ());
		JPanel jpCell = new JPanel ();
		SynsData v = (SynsData) value;
		jpCell.add (new JTextField (v.getValue ()));
		if (v.getValue ().trim ().length () > 0) {
			jpCell.add (new JCheckBox ("", v.getSelected ().booleanValue ()));
		}
		return jpCell;
	}
}

class SynsDataRenderer extends JCheckBox implements TableCellRenderer {

	public SynsDataRenderer () {
		super ();
		setOpaque (true);
	}

	@Override
	public Component getTableCellRendererComponent (
			JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int col) {
		//System.out.println("SynsDataRenderer: (" + row +", "+ col +") "+ value.getClass() +" "+ value.toString());
		if (row == -1) {
			JTableHeader header = table.getTableHeader ();
			if (header != null) {
				setForeground (header.getForeground ());
				setBackground (header.getBackground ());
				setFont (header.getFont ());
				SynonymsTableModel model = (SynonymsTableModel) table.getModel ();
				SynsData d = (SynsData) model.getValueAt (row, col);
				if (d != null) {
					this.setSelected (d.getSelected ());
					this.setText (d.getValue ());
				}
			}
			return this;
		}
		if (value instanceof String) {
			Component c = (Component) table.getDefaultRenderer (String.class)
					.getTableCellRendererComponent (table, value, isSelected, hasFocus, row, col);
			c.setBackground (table.getBackground ());
			return c;
			//return new DefaultTableCellRenderer().getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		}
		if (value instanceof SynsData) {
			SynsData v = (SynsData) value;
			this.setSelected (v.getSelected ());
			this.setText (v.getValue ());
			SynonymsTableModel model = (SynonymsTableModel) table.getModel ();
			SynsData d = (SynsData) model.getValueAt (row, col);
			if (d != null) {
				this.setText (d.getValue ());
			}
		}

		if (isSelected) {
			setForeground (table.getSelectionForeground ());
			setBackground (table.getSelectionBackground ());
		} else {
			setForeground (table.getForeground ());
			setBackground (table.getBackground ());
		}

		return this;
	}
}

class SynsDataEditor extends AbstractCellEditor implements TableCellEditor, ItemListener {

	SynsDataRenderer rend = null;
	private TableCellEditor editor = null;

	public SynsDataEditor () {
		rend = new SynsDataRenderer ();
		rend.addItemListener (this);
	}

	@Override
	public Object getCellEditorValue () {
		if (editor != null) {
			return editor.getCellEditorValue ();
		}

		return rend.isSelected ();
	}

	@Override
	public Component getTableCellEditorComponent (JTable table,
			Object value, boolean isSelected, int row, int col) {

		//System.out.println("SynsDataEditor: (" + row +", "+ col +","+ isSelected +") "+ value.getClass() +" "+ value.toString());

		if (value instanceof String) {
			JTextField tf = new JTextField ();
			tf.addActionListener (new java.awt.event.ActionListener () {
				public void actionPerformed (java.awt.event.ActionEvent evt) {
					JTextField etf = (JTextField) evt.getSource ();
					if (etf.getText ().length () > 0) {
						SynsDataEditor.this.stopCellEditing ();
					}
				}
			});
			Rectangle r = table.getCellRect (row, col, true);
			tf.setBorder (javax.swing.BorderFactory.createEmptyBorder ());
			tf.setBounds (r);
			editor = new DefaultCellEditor (tf);
			//editor = table.getDefaultEditor(String.class);
			return editor.getTableCellEditorComponent (table, value, false, row, col);
		}
		editor = null;
		SynsData v = (SynsData) value;
		rend.setSelected (v.getSelected ());
		rend.setText (v.getValue ());

		return rend;
	}

	@Override
	public void itemStateChanged (ItemEvent e) {
		this.fireEditingStopped ();
	}
}

class RowChange extends AbstractUndoableEdit {

	SynonymsTableModel model_;
	int position_ = 0;

	public RowChange (SynonymsTableModel model, int rowPosition) {
		model_ = model;
		position_ = rowPosition;
	}

	public void undo () throws CannotUndoException {
		// save information at the row
		model_.removeRow (position_);
	}

	public void redo () throws CannotRedoException {
		model_.insertRow (position_);
	}

	public boolean canUndo () {
		return true;
	}

	public boolean canRedo () {
		return true;
	}

	public String getPresentationName () {
		return "Add Row";
	}
}

class CellChange extends AbstractUndoableEdit {

	SynonymsTableModel model_;
	Object val = null;
	int row = -1;
	int col = -1;

	public CellChange (SynonymsTableModel model, Object cell, int row, int col) {
		model_ = model;
		this.row = row;
		this.col = col;
		this.val = cell;
	}

	public void undo () throws CannotUndoException {
		// save information at the row
		SynsData sv = (SynsData) val;
		SynsData v = new SynsData (sv.getSelected (), sv.getValue (), sv.isUserWord ());
		model_.setValueAt (v, row, col);
	}

	public void redo () throws CannotRedoException {
		model_.deleteColumn (row, col);
	}

	public boolean canUndo () {
		return true;
	}

	public boolean canRedo () {
		return true;
	}

	public String getPresentationName () {
		return "Delete Cell";
	}
}

class CellChangeInsert extends CellChange {

	public CellChangeInsert (SynonymsTableModel model, Object cell, int row, int col) {
		super (model, cell, row, col);
	}

	public void undo () throws CannotUndoException {
		super.redo ();
	}

	public void redo () throws CannotRedoException {
		super.undo ();
	}

	public String getPresentationName () {
		return "Change Cell";
	}
}
