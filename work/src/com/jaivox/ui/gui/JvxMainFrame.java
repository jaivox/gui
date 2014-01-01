/*
   Jaivox Application Generator (JAG) version 0.1 December 2013
   Copyright 2010-2013 by Bits and Pixels, Inc.

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

import com.jaivox.synthesizer.web.Synthesizer;
import com.jaivox.ui.appmaker.AppSphinx;
import com.jaivox.ui.appmaker.AppWeb;
import com.jaivox.ui.appmaker.GuiPrep;
import com.jaivox.ui.appmaker.RecordTask;
import test.JvxTest;
import java.awt.Component;
import java.awt.Container;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

/**
 *
 * @author lin
 */
public class JvxMainFrame extends javax.swing.JFrame implements ActionListener {

	private static JvxMainFrame theApp = null;
        private static final boolean testMode = false;
        
	JvxDialogLoader dlgLoader = null;
	JvxDialogHelper dlgHelper = null;
	JvxSynonymsHelper synsHelper = null;
	static boolean dirty_flag = false;
	static boolean generated_flag = false;
	static String recognizer = null;
	static UndoManager undoManager_;
	static UndoableEditSupport undoSupport_;
	static JvxHelpFrame helpFrame = null;
	static String helpDirectory = "/work/data/help/"; // use file.separator property?
	static String urlDirectory = null;
	String qualData[][] = null;
	String headers[] = new String[4];
	DragSource ds;
	StringSelection transferable;
	private String dlgFile;
	private String dataFile;
	ButtonGroup Recognizers;
	ButtonGroup Synthesizers;
	static String Languages[] = {
		"English-US",
		"Catalan",
		"Croatian",
		"Dutch",
		"French",
		"German",
		"Indonesian",
		"Italian",
		"Malay",
		"Mandarin-China",
		"Russian",
		"Spanish-Mexico",
		"Swedish",
		"Turkish"
	};
	static TreeMap<String, String> ttsCodes;
	static TreeMap<String, String> asrCodes;

	/**
	 * Creates new form JvxMainFrame
	 */
	public JvxMainFrame () {
		theApp = this;
		undoManager_ = new UndoManager ();
		dlgLoader = new JvxDialogLoader (this);
		dlgHelper = new JvxDialogHelper (this);
		synsHelper = new JvxSynonymsHelper (this);
		/*
		 qualData = dlgLoader.loadQualData ();
        
		 headers [0] = "Num";
		 headers [1] = "Road";
		 headers [2] = "Fast";
		 headers [3] = "Smooth";
		 */
		// help system
		String workingDirectory = System.getProperty ("user.dir");
		// System.out.println ("Current directory: "+workingDirectory);
		urlDirectory = "file://" + workingDirectory + helpDirectory;

		initComponents ();
		expandYNButton.setSelected (false);
		expandYNButton.setText ("Expand Synonyms");
		
                testButton.setEnabled(isTesting());
                testButton.setVisible(isTesting());
                // turn off while testing
                if(!isTesting()) {
                    checkInstalled ();
                }
		initLanguages ();
		initLanguageCodes ();
		createButtonGroups ();
		setAllToolTip ();
		registerF1Help ();
		new MenuUtils ().setMenuBarForFrame (this);

		try {
			RecentFileHistory.loadHistory ();
		} catch (BackingStoreException ex) {
			Logger.getLogger (JvxMainFrame.class.getName ()).log (Level.SEVERE, null, ex);
		}

		DefaultTreeModel model = (DefaultTreeModel) dialogTree.getModel ();
		model.addTreeModelListener (new DlgTreeModelListener (this));

		this.dialogTree.setTransferHandler (new DialogTreeDNDHandler ());
		this.synsTab.setTransferHandler (new SynsTabDNDHandler ());

		undoSupport_ = new UndoableEditSupport ();
		undoSupport_.addUndoableEditListener (new UndoAdapter ());
		//refreshUndoRedo ();
	}

	public static List<Component> getAllComponents (final Container c) {
		Component[] comps = c.getComponents ();
		List<Component> compList = new ArrayList<Component> ();
		for (Component comp : comps) {
			compList.add (comp);
			if (comp instanceof Container) {
				compList.addAll (getAllComponents ((Container) comp));
			}
		}
		return compList;
	}

	void createButtonGroups () {
		Recognizers = new ButtonGroup ();
		Synthesizers = new ButtonGroup ();
		Recognizers.add (cbConsole);
		Recognizers.add (cbGoogleRecognizer);
		Recognizers.add (cbSphinx);
		Synthesizers.add (cbFreetts);
		Synthesizers.add (cbGoogletts);
		Synthesizers.add (cbFestival);
		Synthesizers.add (cbEspeak);
	}

	void setAllToolTip () {
		for (Component c : getAllComponents (this)) {
			if (c instanceof JComponent) {
				String k = c.getName ();
				if (k != null) {
					String tip = JvxConfiguration.getHelpToolTip (k);
					//System.out.println("setAllToolTip:" + k +": "+ tip);
					if (tip != null) {
						((JComponent) c).setToolTipText (tip);
					}
					//if(tip != null) registerFocusHandler((JComponent) c);
				}
			}
		}
	}

	public static JvxMainFrame getInstance () {
		return theApp;
	}

	public JvxDialogLoader getDlgLoader () {
		return dlgLoader;
	}

	public JvxDialogHelper getDlgHelper () {
		return dlgHelper;
	}

	public JvxSynonymsHelper getSynsHelper () {
		return synsHelper;
	}

	public void postUndoableEdit (UndoableEdit change) {
		undoSupport_.postEdit (change);
		dirty_flag = true;
	}
        public static boolean isTesting() {
            return testMode;
        }
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings ("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        contentSpecPanel = new javax.swing.JPanel();
        langPanel = new javax.swing.JPanel();
        langCombo = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        expandYNButton = new javax.swing.JToggleButton();
        dgdPanel = new javax.swing.JPanel();
        primaryVSplitPane = new javax.swing.JSplitPane();
        dlgSynsHSplitPane = new javax.swing.JSplitPane();
        jPanel5 = new javax.swing.JPanel();
        dlgTreeScrollPane = new javax.swing.JScrollPane();
        dialogTree = new javax.swing.JTree();
        synsPreviewHSplitPane = new javax.swing.JSplitPane();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        synsTab = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        grammarList = new javax.swing.JList();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        selectDbButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        qualdbTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        appName = new javax.swing.JTextField();
        testButton = new javax.swing.JButton();
        targetSpecPanel = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        cbGoogleRecognizer = new javax.swing.JToggleButton();
        cbSphinx = new javax.swing.JToggleButton();
        jPanel7 = new javax.swing.JPanel();
        cbFreetts = new javax.swing.JCheckBox();
        cbGoogletts = new javax.swing.JCheckBox();
        cbFestival = new javax.swing.JCheckBox();
        cbEspeak = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        osList = new javax.swing.JComboBox();
        cbConsole = new javax.swing.JCheckBox();
        btnGenerate = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnRun = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Jaivox App Gen");

        mainPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        contentSpecPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Content Specification", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(29, 103, 229)));

        langPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        langCombo.setEditable(true);
        langCombo.setForeground(new java.awt.Color(112, 125, 209));
        langCombo.setMaximumRowCount(100);
        langCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "English - US", "French", "Hindi", "Spanish" }));
        langCombo.setName("langCombo"); // NOI18N
        langCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                langComboActionPerformed(evt);
            }
        });

        jLabel1.setText("Select Language:");

        expandYNButton.setSelected(true);
        expandYNButton.setText("Expand Synonyms");
        expandYNButton.setName ("expandYNButton");

        javax.swing.GroupLayout langPanelLayout = new javax.swing.GroupLayout(langPanel);
        langPanel.setLayout(langPanelLayout);
        langPanelLayout.setHorizontalGroup(
            langPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, langPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addComponent(langCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(expandYNButton)
                .addContainerGap(81, Short.MAX_VALUE))
        );
        langPanelLayout.setVerticalGroup(
            langPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(langPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(langCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(expandYNButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        dgdPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "                   Dialogs                                                                          Synonyms                                                               Alt Sentence preview", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(151, 149, 198)));

        primaryVSplitPane.setBorder(new javax.swing.border.MatteBorder(null));
        primaryVSplitPane.setDividerLocation(350);
        primaryVSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        primaryVSplitPane.setContinuousLayout(true);

        dlgSynsHSplitPane.setBorder(new javax.swing.border.MatteBorder(null));
        dlgSynsHSplitPane.setDividerLocation(250);
        dlgSynsHSplitPane.setContinuousLayout(true);

        dlgTreeScrollPane.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                dlgTreeScrollPaneFocusLost(evt);
            }
        });

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        dialogTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        dialogTree.setDragEnabled(true);
        dialogTree.setDropMode(javax.swing.DropMode.INSERT);
        dialogTree.setEditable(true);
        dialogTree.setName("dialogTree"); // NOI18N
        dialogTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dialogTreeMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                dialogTreeMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                dialogTreeMouseReleased(evt);
            }
        });
        dialogTree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dialogTreeKeyPressed(evt);
            }
        });
        dlgTreeScrollPane.setViewportView(dialogTree);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dlgTreeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dlgTreeScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
        );

        dlgSynsHSplitPane.setLeftComponent(jPanel5);

        synsPreviewHSplitPane.setBorder(null);
        synsPreviewHSplitPane.setDividerLocation(300);
        synsPreviewHSplitPane.setDividerSize(4);
        synsPreviewHSplitPane.setContinuousLayout(true);

        synsTab.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(127, 197, 234)));
        synsTab.setModel(new SynonymsTableModel());
        synsTab.setCellSelectionEnabled(true);
        synsTab.setDragEnabled(true);
        synsTab.setDropMode(javax.swing.DropMode.INSERT);
        synsTab.setGridColor(new java.awt.Color(77, 131, 236));
        synsTab.setName("synsTab"); // NOI18N
        synsTab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                synsTabMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(synsTab);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
        );

        synsPreviewHSplitPane.setLeftComponent(jPanel9);

        grammarList.setToolTipText("");
        grammarList.setDragEnabled(true);
        grammarList.setName("grammarList"); // NOI18N
        grammarList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                grammarListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(grammarList);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE)
        );

        synsPreviewHSplitPane.setRightComponent(jPanel4);

        dlgSynsHSplitPane.setRightComponent(synsPreviewHSplitPane);

        primaryVSplitPane.setTopComponent(dlgSynsHSplitPane);

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        selectDbButton.setText("Select DB");
        selectDbButton.setName("selectDbButton"); // NOI18N
        selectDbButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectDbButtonActionPerformed(evt);
            }
        });

        qualdbTable.setModel(new javax.swing.table.DefaultTableModel(
            qualData,
            headers
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        qualdbTable.setCellSelectionEnabled(true);
        qualdbTable.setDragEnabled(true);
        qualdbTable.setName("qualdbTable"); // NOI18N
        jScrollPane4.setViewportView(qualdbTable);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(selectDbButton, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(selectDbButton)
                .addContainerGap(116, Short.MAX_VALUE))
            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("DB Interface", jPanel1);

        jPanel3.setEnabled(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 825, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 175, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("", jPanel3);

        primaryVSplitPane.setRightComponent(jTabbedPane1);

        javax.swing.GroupLayout dgdPanelLayout = new javax.swing.GroupLayout(dgdPanel);
        dgdPanel.setLayout(dgdPanelLayout);
        dgdPanelLayout.setHorizontalGroup(
            dgdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, dgdPanelLayout.createSequentialGroup()
                .addComponent(primaryVSplitPane)
                .addGap(2, 2, 2))
        );
        dgdPanelLayout.setVerticalGroup(
            dgdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(primaryVSplitPane)
        );

        jLabel4.setText("App Name");

        appName.setText("Type Name ...");
        appName.setName("appName"); // NOI18N
        appName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                appNameMousePressed(evt);
            }
        });
        appName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appNameActionPerformed(evt);
            }
        });

        testButton.setText("Test");
        testButton.setName ("testButton");
        testButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout contentSpecPanelLayout = new javax.swing.GroupLayout(contentSpecPanel);
        contentSpecPanel.setLayout(contentSpecPanelLayout);
        contentSpecPanelLayout.setHorizontalGroup(
            contentSpecPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentSpecPanelLayout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(appName, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(testButton, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(langPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(dgdPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        contentSpecPanelLayout.setVerticalGroup(
            contentSpecPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentSpecPanelLayout.createSequentialGroup()
                .addGroup(contentSpecPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(langPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, contentSpecPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(appName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)
                        .addComponent(testButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dgdPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 14, Short.MAX_VALUE))
        );

        targetSpecPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Target Specification", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, new java.awt.Color(210, 90, 90)));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Recognizer Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Abyssinica SIL", 0, 8))); // NOI18N

        cbGoogleRecognizer.setText("Google");
        cbGoogleRecognizer.setName("cbGoogleRecognizer"); // NOI18N

        cbSphinx.setText("Sphinx");
        cbSphinx.setName("cbSphinx"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(cbGoogleRecognizer, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbSphinx, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbGoogleRecognizer)
                    .addComponent(cbSphinx))
                .addContainerGap(61, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "TTS options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Abyssinica SIL", 0, 8))); // NOI18N

        cbFreetts.setText("FreeTTS");
        cbFreetts.setName("cbFreetts"); // NOI18N

        cbGoogletts.setText("Google TTS");
        cbGoogletts.setName("cbGoogletts"); // NOI18N

        cbFestival.setText("Festival");
        cbFestival.setEnabled(false);
        cbFestival.setName("cbFestival"); // NOI18N

        cbEspeak.setText("Espeak");
        cbEspeak.setEnabled(false);
        cbEspeak.setName("cbEspeak"); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbFreetts)
                    .addComponent(cbGoogletts)
                    .addComponent(cbFestival)
                    .addComponent(cbEspeak))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(cbFreetts)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbGoogletts)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbFestival)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbEspeak)
                .addGap(0, 16, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Platform Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Abyssinica SIL", 0, 8))); // NOI18N

        jLabel2.setText("OS: ");

        osList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Linux-RH", "Linux-Debian", "Windows", "Mac", "Tab-Android", "Tab-iOS" }));
        osList.setEnabled(false);
        osList.setName("osList"); // NOI18N

        cbConsole.setSelected(true);
        cbConsole.setText("Console Version");
        cbConsole.setName("cbConsole"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(4, 4, 4)
                        .addComponent(osList, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cbConsole, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(osList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cbConsole)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        btnGenerate.setText("Generate");
        btnGenerate.setName ("btnGenerate");
        btnGenerate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout targetSpecPanelLayout = new javax.swing.GroupLayout(targetSpecPanel);
        targetSpecPanel.setLayout(targetSpecPanelLayout);
        targetSpecPanelLayout.setHorizontalGroup(
            targetSpecPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(targetSpecPanelLayout.createSequentialGroup()
                .addGroup(targetSpecPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(targetSpecPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnGenerate))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        targetSpecPanelLayout.setVerticalGroup(
            targetSpecPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(targetSpecPanelLayout.createSequentialGroup()
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGenerate)
                .addContainerGap())
        );

        btnSave.setText("Save");
        btnSave.setName("btnSave"); // NOI18N
        btnSave.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).
        put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0), "F1");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnRun.setText("Run");
        btnRun.setMaximumSize(new java.awt.Dimension(42, 27));
        btnRun.setMinimumSize(new java.awt.Dimension(42, 27));
        btnRun.setName("btnRun"); // NOI18N
        btnRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRunActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(contentSpecPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(targetSpecPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRun, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(targetSpecPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnRun, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(contentSpecPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 10, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * Check whether a program is installed by running the program with -h
	 * option. Also check if the result is longer than 100 characters, since the
	 * help obtained is usually much longer.
	 *
	 * @param application
	 * @return
	 */
	public static boolean installed (String command) {
		String result = RecordTask.runcommand (command);
		// System.out.println ("command: "+command+"\n"+result);
		if (result == null) {
			return false;
		}
		if (result.toLowerCase ().trim ().endsWith ("not found...")) {
			return false;
		}
		if (result.length () < 100) {
			return false;
		}
		// System.out.println (result.substring (0, 100));
		System.out.println (command + " is installed");
		return true;
	}

	public static boolean checkNetAccess (String url, int port) {
		try {
			InetSocketAddress address = new InetSocketAddress (url, port);
			SocketChannel channel = SocketChannel.open ();
			channel.configureBlocking (false);
			boolean ok = channel.connect (address);
			if (ok) {
				if (channel.isOpen ()) {
					channel.close ();
				}
				return ok;
			} else {
				return false;
			}
		} catch (Exception e) {
			System.out.println ("checkNetAccess: " + e.toString ());
			return false;
		}
	}

	/**
	 * Check for jar file in class path
	 *
	 * @param content
	 * @return
	 */
	public static boolean checkClasspath (String content) {
		String classpath = System.getProperty ("java.class.path");
		System.out.println ("classpath=" + classpath);
		int pos = classpath.indexOf (content);
		if (pos != -1) {
			System.out.println (content + " is in the classpath");
			return true;
		} else {
			System.out.println (content + " is not in the classpath");
			return false;
		}
	}

	void checkInstalled () {
		cbEspeak.setEnabled (installed ("espeak -h"));
		// cbEspeak.setEnabled (false);
		// cbFestival.setEnabled (installed ("festival -h"));
		cbFestival.setEnabled (false);
		cbGoogleRecognizer.setEnabled (installed ("sox"));
		cbFreetts.setEnabled (checkClasspath ("freetts.jar"));
		// cbFreetts.setEnabled (false);
		cbGoogleRecognizer.setEnabled (AppWeb.testSpeech ("work/apps/common/test.flac"));
		Synthesizer speaker = new Synthesizer ();
		cbGoogletts.setEnabled (speaker.speak ("testing google text to speech"));

		// sphinx works only in linux
		// check this last, any failure returns
		String os = System.getProperty ("os.name");
		if (os.startsWith ("Windows")) {
			System.out.println ("Sphinx Operating system: " + os);
			cbSphinx.setEnabled (false);
			return;
		}

		/*
		 // The following check is needed, but did not work
		 // check for sphinx_lm_convert
		 if (!installed ("/usr/local/bin/sphinx_lm_convert -help")) {
		 System.out.println ("sphinx_lm_convert missing");
		 cbSphinx.setEnabled (false);
		 return;
		 }
		 */
		File f = new File ("/usr/local/bin/sphinx_lm_convert");
		if (!f.exists ()) {
			System.out.println ("Could not find sphinx_lm_convert");
			cbSphinx.setEnabled (false);
			return;
		}
		if (!AppSphinx.testSpeech ("work/apps/common/test.wav")) {
			System.out.println ("sphinx test speech not recognized");
			cbSphinx.setEnabled (false);
			return;
		}
		cbSphinx.setEnabled (true);


		// check some urls, does not seem to work right
		/*
		 boolean okwebasr = 
		 checkNetAccess ("http://www.google.com/speech-api/v1/recognize?lang=en-US&client=chromium", 80);
		 cbGoogleRecognizer.setEnabled (okwebasr);
		 boolean okwebtts =
		 checkNetAccess ("http://translate.google.com/translate_tts?tl=en&&ie=UTF-8&q=test", 80);
		 cbGoogletts.setEnabled (okwebtts);
		 */
	}

	void initLanguages () {
		langCombo.setModel (new javax.swing.DefaultComboBoxModel (Languages));
	}

	void initLanguageCodes () {
		ttsCodes = new TreeMap<String, String> ();
		asrCodes = new TreeMap<String, String> ();

		asrCodes.put ("English-US", "en-US");
		ttsCodes.put ("English-US", "en");

		asrCodes.put ("Catalan", "ca-ES");
		ttsCodes.put ("Catalan", "ca");

		asrCodes.put ("Croatian", "hr_HR");
		ttsCodes.put ("Croatian", "hr");

		asrCodes.put ("Dutch", "nl-NL");
		ttsCodes.put ("Dutch", "nl");

		asrCodes.put ("French", "fr-FR");
		ttsCodes.put ("French", "fr");

		asrCodes.put ("German", "de-DE");
		ttsCodes.put ("German", "de");

		asrCodes.put ("Indonesian", "id-ID");
		ttsCodes.put ("Indonesian", "id");

		asrCodes.put ("Italian", "it-IT");
		ttsCodes.put ("Italian", "it");

		asrCodes.put ("Malay", "ms-MY");
		ttsCodes.put ("Malay", "ms");

		asrCodes.put ("Mandarin-China", "cmn-Hans-CN");
		ttsCodes.put ("Mandarin-China", "zh-CN");

		asrCodes.put ("Russian", "ru-RU");
		ttsCodes.put ("Russian", "ru");

		asrCodes.put ("Spanish-Mexico", "es-MX");
		ttsCodes.put ("Spanish-Mexico", "es");

		asrCodes.put ("Swedish", "sv-SE");
		ttsCodes.put ("Swedish", "sv");

		asrCodes.put ("Turkish", "tr-TR");
		ttsCodes.put ("Turkish", "tr");
	}

	public String getAsrLanguage () {
		int langid = langCombo.getSelectedIndex ();
		String lang = Languages[langid];
		String asr = asrCodes.get (lang);
		if (asr != null) {
			return asr;
		} else {
			return "en-US";
		}
	}

	public String getTtsLanguage () {
		int langid = langCombo.getSelectedIndex ();
		String lang = Languages[langid];
		String tts = ttsCodes.get (lang);
		if (tts != null) {
			return tts;
		} else {
			return "en";
		}

	}

	public void actionPerformed (java.awt.event.ActionEvent evt) {
		if (evt.getActionCommand ().equals ("DBInterface")) {
			this.dlgLoader.interfaceDialogs (evt);
		}
	}

	public DefaultMutableTreeNode getMouseOnNode (int x, int y) {
		TreePath path = this.dialogTree.getPathForLocation (x, y);
		if (path == null) {
			return null;
		}

		return (DefaultMutableTreeNode) path.getLastPathComponent ();
	}

	public DefaultMutableTreeNode getSelectedNode () {
		TreePath tpath = dialogTree.getSelectionPath ();
		return tpath == null ? null : (DefaultMutableTreeNode) tpath.getLastPathComponent ();
	}

	public String getAppName () {
		String proj = appName.getText ().trim ();
		if (proj.length () <= 0 || proj.equals ("Type Name ...")) {
			proj = null;
		}
		return proj;
	}

	public String fileDialog (String appname) {
		// final JFileChooser fc = new JFileChooser (new File (JvxConfiguration.theConfig ().getAppFolder ()));
		final JFileChooser fc = new JFileChooser ();
		String loc = null;
		fc.setDialogTitle ("Choose Application Location(Folder)");
		fc.setFileSelectionMode (JFileChooser.DIRECTORIES_ONLY);
		fc.setCurrentDirectory (new File (appname));
		int returnVal = fc.showOpenDialog (this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			loc = fc.getSelectedFile ().getAbsolutePath ();
		}
		return loc;
	}

	public boolean save () {
		String proj = appName.getText ().trim ();

		if (proj.length () <= 0 || proj.equals ("Type Name ...")) {
			JOptionPane.showMessageDialog (null,
					"Missing: Application Name\nUsing name \"test\"", "Error Massage",
					JOptionPane.ERROR_MESSAGE);

			proj = "test";
			appName.setText (proj);
			//return false;
		}
		JvxConfiguration.theConfig ().setAppName (appName.getText ());
		String f = fileDialog (appName.getText ());
		if (f != null) {
			String s = f + File.separatorChar + appName.getText ()
					+ File.separatorChar;
			JvxConfiguration.theConfig ().setAppFolder (s);
		}
		if (f != null) {
			JvxConfiguration.theConfig ().save (this);
		}
		return (f != null);
	}
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
		// TODO add your handling code here:
		save ();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void appNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appNameActionPerformed
		// TODO add your handling code here:
		JTextField apn = (JTextField) evt.getSource ();
		JvxConfiguration.theConfig ().setAppName (apn.getText ());
    }//GEN-LAST:event_appNameActionPerformed

    private void appNameMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_appNameMousePressed
		// TODO add your handling code here:
		JTextField apn = (JTextField) evt.getSource ();
		if (apn.getText ().equals ("Type Name ...")) {
			apn.setText ("");
		}
    }//GEN-LAST:event_appNameMousePressed

    private void dlgTreeScrollPaneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dlgTreeScrollPaneFocusLost
		// TODO add your handling code here:
		//dialogTreeFocusLost(evt);
    }//GEN-LAST:event_dlgTreeScrollPaneFocusLost

    private void dialogTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dialogTreeMouseClicked
		// TODO add your handling code here:
		// init and set the Grammar panel
		//JTree tree = (JTree)evt.getSource();
		//DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
    }//GEN-LAST:event_dialogTreeMouseClicked

    private void dialogTreeMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dialogTreeMouseReleased
		// TODO add your handling code here:
		//if( !evt.isPopupTrigger() ) return;
		//overlapDialog(evt, false);
		dlgHelper.dialogTreeRClicked (evt);
		//rightClickedNode = null;
    }//GEN-LAST:event_dialogTreeMouseReleased

    private void dialogTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dialogTreeMousePressed
		// TODO add your handling code here:
		JTree tree = (JTree) evt.getSource ();
		TreeModel model = tree.getModel ();
		DefaultMutableTreeNode node = null;
		if (model != null && model.getRoot () != null) {
			node = (DefaultMutableTreeNode) model.getRoot ();
		}
		if (model == null || model.getRoot () == null
				|| node.getChildCount () < 1
				|| model.getChildCount (model.getRoot ()) < 1) {
			MenuUtils.openDialogFileMenu (evt);
		}

		if (!evt.isPopupTrigger ()) {
			dlgHelper.dialogTreeMouseClicked (evt);
		} else {
			dlgHelper.dialogTreeRClicked (evt);
		}
    }//GEN-LAST:event_dialogTreeMousePressed

    private void synsTabMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_synsTabMousePressed
		if (evt.isPopupTrigger ()) {
			synsHelper.synsTabMouseRClicked (evt);
		}
    }//GEN-LAST:event_synsTabMousePressed

    private void grammarListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_grammarListMouseClicked
		// TODO add your handling code here:
		if (evt.isPopupTrigger ()) {
			return;
		}
		JList grams = (JList) evt.getSource ();
    }//GEN-LAST:event_grammarListMouseClicked

    private void selectDbButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectDbButtonActionPerformed
		// TODO add your handling code here:
		this.dlgLoader.interfaceDialogs (evt);
    }//GEN-LAST:event_selectDbButtonActionPerformed

    private void btnRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRunActionPerformed
		// TODO add your handling code here:
		try {
			if (dirty_flag) {
				generated_flag = false;
			}
			checkTargetOptions ();

			if (GuiPrep.Running) {
				GuiPrep.stopRunning ();
				//return;
			}
			if (!generated_flag) {
				if (!save ()) {
					return;
				}
			}

			if (!generated_flag) {
				this.dlgHelper.generateApp (this);
				generated_flag = true;
			}

			if (generated_flag) {
				this.dlgHelper.runApp (this);
				setRunEnabled (false);
			}
		} catch (Exception e) {
			generated_flag = false;
			setRunEnabled (true);
			e.printStackTrace ();
			return;
		}
    }//GEN-LAST:event_btnRunActionPerformed
	void setRunEnabled (boolean b) {
		btnRun.setEnabled (b);
	}

	void checkTargetOptions () {
		if (generated_flag && (recognizer != null)) {
			if (!recognizer.equals (this.getRecognizer ())) {
				generated_flag = false;     // regenerate for the selection
			}
		}
		recognizer = this.getRecognizer ();
	}
    private void dialogTreeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dialogTreeKeyPressed
		// TODO add your handling code here:
		if (evt.getKeyCode () == java.awt.event.KeyEvent.VK_F3) {
			TreePath path = dialogTree.getSelectionPath ();

			DefaultMutableTreeNode node = null;
			if (path != null) {
				node = (DefaultMutableTreeNode) path.getLastPathComponent ();
			} else {
				node = (DefaultMutableTreeNode) dialogTree.getModel ().getRoot ();
				node = !(node.isRoot () || node.isLeaf ()) ? node.getNextNode () : node;
				path = new TreePath (node);
			}
			if (node == null) {
				return;
			}
			TreePath[] selectionPaths = dialogTree.getSelectionPaths ();
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
				dialogTree.setSelectionPath (path);
			}

			new DialogMenuAction ().actionPerformed (
					new ActionEvent (dialogTree, ActionEvent.ACTION_PERFORMED, "Add"));
		}
    }//GEN-LAST:event_dialogTreeKeyPressed

    private void langComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_langComboActionPerformed
		// TODO add your handling code here:
		String lang = (String) langCombo.getSelectedItem ();
		if (lang.equals ("English-US")) {
			this.expandYNButton.setEnabled (true);
			expandYNButton.setSelected (true);
		} else {
			this.expandYNButton.setEnabled (false);
			expandYNButton.setSelected (false);
		}
    }//GEN-LAST:event_langComboActionPerformed

    private void btnGenerateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerateActionPerformed
		// TODO add your handling code here:
		// TODO add your handling code here:
		if (!save ()) {
			return;
		}
		if (dirty_flag) {
			generated_flag = false;
		}
		if (!generated_flag) {
			this.dlgHelper.generateApp (this);
			generated_flag = true;
		}
    }//GEN-LAST:event_btnGenerateActionPerformed

    private void testButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testButtonActionPerformed
        // TODO add your handling code here:
        new JvxTest(this).simpletest();
        //new JvxTest(this).simpleVoiceTest();
    }//GEN-LAST:event_testButtonActionPerformed

	public boolean wordsToBeExpanded () {
		return (this.expandYNButton.isEnabled () && expandYNButton.isSelected ());
	}

	void registerF1Help () {
		// in alphabetical order depending on contents of help directory
		JComponent cl[] = {
			appName, btnRun, btnSave, btnGenerate, 
			cbConsole, cbEspeak,
			cbFestival, cbFreetts, cbGoogleRecognizer,
			cbGoogletts, cbSphinx, dialogTree, 
			expandYNButton,
			grammarList, osList, selectDbButton,
			synsTab, testButton, qualdbTable };
		for (JComponent c : cl) {
			registerFocusHandler (c);
		}
	}

	void registerFocusHandler (final JComponent c) {
		c.getInputMap (JComponent.WHEN_IN_FOCUSED_WINDOW).
				put (KeyStroke.getKeyStroke (java.awt.event.KeyEvent.VK_F1, 0), "F1");
		c.addMouseListener (new java.awt.event.MouseAdapter () {
			public void mouseEntered (java.awt.event.MouseEvent evt) {
				//System.out.println ("registerFocusHandler: mouseEntered: " + c.getName());
				c.grabFocus ();
			}
		});
		c.addKeyListener (new java.awt.event.KeyAdapter () {
			public void keyPressed (java.awt.event.KeyEvent evt) {
				handleHelpKey (evt);
			}
		});
	}

	public JTree getDialogTree () {
		return dialogTree;
	}

	public JList getGrammarList () {
		return grammarList;
	}

	public JTable getSynsTab () {
		return synsTab;
	}

	public JTable getQualdbTable () {
		return qualdbTable;
	}

	public String getRecognizer () {
		if (this.cbGoogleRecognizer.isSelected ()) {
			return "web";
		}
		if (this.cbSphinx.isSelected ()) {
			return "sphinx";
		}
		if (this.cbConsole.isSelected ()) {
			return "console";
		}
		return null;
	}

	public String getSynthesizer () {
		if (this.cbFestival.isSelected ()) {
			return "festival";
		}
		if (this.cbGoogletts.isSelected ()) {
			return "web";
		}
		if (this.cbFreetts.isSelected ()) {
			return "freetts";
		}
		if (this.cbEspeak.isSelected ()) {
			return "espeak";
		}
		return null;
	}

	public boolean getCbConsole () {
		return cbConsole.isSelected ();
	}

	public boolean getCbEspeak () {
		return cbEspeak.isSelected ();
	}

	public boolean getCbFestival () {
		return cbFestival.isSelected ();
	}

	public boolean getCbFreetts () {
		return cbFreetts.isSelected ();
	}

	public boolean getCbGoogleRecognizer () {
		return cbGoogleRecognizer.isSelected ();
	}

	public boolean getCbGoogletts () {
		return cbGoogletts.isSelected ();
	}

	public boolean getCbSphinx () {
		return cbSphinx.isSelected ();
	}

	void startWizard () {
		final JFrame frame = this;
		final WizardDialog dialog = new WizardDialog (frame, true);
		dialog.setVisible (true);
		/*
		 java.awt.EventQueue.invokeLater(new Runnable() {
		 public void run() {
		 dialog.addWindowListener(new java.awt.event.WindowAdapter() {
		 @Override
		 public void windowClosing(java.awt.event.WindowEvent e) {
		 dialog.setVisible(false);
		 }
		 });
		 dialog.setVisible(true);
		 }
		 });
		 * */
		//this.appName.setText( dialog.getCardValue("app_name").toString() );
		this.dlgFile = (String) dialog.getCardValue ("dialog_file");
		this.dataFile = (String) dialog.getCardValue ("data_file");
		System.out.println ("startWizard: " + dlgFile);
	}

	static void createHelpFrame () {
		if (helpFrame == null) {
			helpFrame = new JvxHelpFrame ();
		}
		helpFrame.setVisible (true);
	}

	static void showHelp (String url) {
		createHelpFrame ();
		helpFrame.setHelpPage (url);
	}

	void handleHelpKey (java.awt.event.KeyEvent evt) {
		if (evt.getKeyCode () == java.awt.event.KeyEvent.VK_F1) {
			//System.out.println("F1 pressed");
			JComponent c = (JComponent) evt.getSource ();
			String key = c.getName ();
			String urlPath = urlDirectory + JvxConfiguration.getHelpURL (key);
			showHelp (urlPath);
		}
	}

	public void setDirtyFlag (boolean flag) {
		dirty_flag = flag;
	}

	private boolean confirmExit () {

		try {
			RecentFileHistory.flush ();
		} catch (BackingStoreException ex) {
			Logger.getLogger (JvxMainFrame.class.getName ()).log (Level.SEVERE, null, ex);
		}

		if (dirty_flag) {
			Object[] options = {"Yes, please",
				"No, thanks",
				"Cancel"};
			int n = JOptionPane.showOptionDialog (this,
					"Your unsaved changes will be lost on Exit!\n"
					+ "Would you like to Save the Changes and Exit?",
					"Confirm Exit",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					null,
					options,
					options[0]);
			if (n == JOptionPane.YES_OPTION) {
				save ();
			} else if (n == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main (String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels ()) {
				if ("Nimbus".equals (info.getName ())) {
					javax.swing.UIManager.setLookAndFeel (info.getClassName ());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger (JvxMainFrame.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger (JvxMainFrame.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger (JvxMainFrame.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger (JvxMainFrame.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater (new Runnable () {
			public void run () {
				final JvxMainFrame jf = new JvxMainFrame ();
				JvxMainFrame.theApp = jf;
				jf.addWindowListener (new java.awt.event.WindowAdapter () {
					@Override
					public void windowClosing (java.awt.event.WindowEvent e) {
						try {
							if (!jf.confirmExit ()) {
								return;
							}
						} catch (Exception ex) {
							ex.printStackTrace ();
						}
						System.exit (0);
					}
				});
				jf.setDefaultCloseOperation (DO_NOTHING_ON_CLOSE);
				jf.setVisible (true);
				//jf.startWizard();
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField appName;
    private javax.swing.JButton btnGenerate;
    private javax.swing.JButton btnRun;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox cbConsole;
    private javax.swing.JCheckBox cbEspeak;
    private javax.swing.JCheckBox cbFestival;
    private javax.swing.JCheckBox cbFreetts;
    private javax.swing.JToggleButton cbGoogleRecognizer;
    private javax.swing.JCheckBox cbGoogletts;
    private javax.swing.JToggleButton cbSphinx;
    private javax.swing.JPanel contentSpecPanel;
    private javax.swing.JPanel dgdPanel;
    private javax.swing.JTree dialogTree;
    private javax.swing.JSplitPane dlgSynsHSplitPane;
    private javax.swing.JScrollPane dlgTreeScrollPane;
    private javax.swing.JToggleButton expandYNButton;
    private javax.swing.JList grammarList;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox langCombo;
    private javax.swing.JPanel langPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JComboBox osList;
    private javax.swing.JSplitPane primaryVSplitPane;
    private javax.swing.JTable qualdbTable;
    private javax.swing.JButton selectDbButton;
    private javax.swing.JSplitPane synsPreviewHSplitPane;
    private javax.swing.JTable synsTab;
    private javax.swing.JPanel targetSpecPanel;
    private javax.swing.JButton testButton;
    // End of variables declaration//GEN-END:variables
}
