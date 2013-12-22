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

import java.awt.CardLayout;
import java.awt.Component;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;

/**
 *
 * @author rj
 */
public class WizardDialog extends javax.swing.JDialog {

	Map<String, Object> cardInfo = new HashMap<String, Object> ();
	String currentCard = null;

	public Object getCardValue (String key) {
		return cardInfo.get (key);
	}

	Component currentPanel () {
		synchronized (cardsPanel.getTreeLock ()) {
			int ncomponents = cardsPanel.getComponentCount ();
			for (int i = 0; i < ncomponents; i++) {
				Component comp = cardsPanel.getComponent (i);
				if (comp.isVisible ()) {
					return comp;
				}
			}
		}
		return null;
	}

	void displayNextCard () {
		CardLayout cl = (CardLayout) cardsPanel.getLayout ();
		String cur = currentPanel ().getName ();
		String nextCard = cur;

		if (cur == null) {
			nextCard = this.firstCardPanel.getName ();
			backButton.setEnabled (false);
		} else if (cur.equals (this.firstCardPanel.getName ())) {
			if (this.optionExisting.isSelected ()) {
				nextCard = this.chooseDialogPanel.getName ();
				this.createButton.setEnabled (false);
				backButton.setEnabled (true);
			} else if (this.optionNew.isSelected ()) {
				nextCard = this.firstCardPanel.getName ();
				this.createButton.setEnabled (true);
			}
		} else if (cur.equals (this.chooseDialogPanel.getName ())) {
			nextCard = this.firstCardPanel.getName ();
			backButton.setEnabled (false);
		}

		cl.show (cardsPanel, nextCard);
		currentCard = nextCard;
	}

	/**
	 * Creates new form WizardDialog
	 */
	public WizardDialog (java.awt.Frame parent, boolean modal) {
		super (parent, modal);
		initComponents ();
		//displayNextCard();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings ("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogOptionGrp = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        backButton = new javax.swing.JToggleButton();
        jPanel4 = new javax.swing.JPanel();
        nextButton = new javax.swing.JToggleButton();
        jPanel5 = new javax.swing.JPanel();
        cancellButton = new javax.swing.JToggleButton();
        createButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        appNameText = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        appFolderText = new javax.swing.JTextField();
        fileChooserButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        cardsPanel = new javax.swing.JPanel();
        firstCardPanel = new javax.swing.JPanel();
        optionExisting = new javax.swing.JRadioButton();
        optionNew = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        chooseDialogPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        datfileChooserButton = new javax.swing.JButton();
        datFileText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dialogFileText = new javax.swing.JTextField();
        fileChooserButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Jaivox Appmaker Wizard");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 221, Short.MAX_VALUE)
        );

        jPanel2.setBorder(null);

        backButton.setText("<");
        backButton.setEnabled(false);
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(backButton))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(41, Short.MAX_VALUE)
                .addComponent(backButton, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67))
        );

        jPanel4.setBorder(null);

        nextButton.setText(">");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(nextButton, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(51, Short.MAX_VALUE)
                .addComponent(nextButton, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57))
        );

        jPanel5.setBorder(null);

        cancellButton.setText("Cancel");
        cancellButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancellButtonActionPerformed(evt);
            }
        });

        createButton.setText("Create");
        createButton.setEnabled(false);
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(cancellButton, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addComponent(createButton)
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancellButton, createButton});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancellButton)
                    .addComponent(createButton))
                .addGap(0, 0, 0))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cancellButton, createButton});

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Application Name and Folder"));

        jPanel8.setBorder(null);

        jLabel3.setText("App Folder:");

        appFolderText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appFolderTextActionPerformed(evt);
            }
        });

        fileChooserButton1.setText("...");
        fileChooserButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileChooserButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addGap(1, 1, 1)
                .addComponent(appFolderText)
                .addGap(1, 1, 1)
                .addComponent(fileChooserButton1))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(appFolderText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileChooserButton1))
                .addGap(0, 6, Short.MAX_VALUE))
        );

        jLabel4.setText("Name:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(appNameText))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appNameText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        cardsPanel.setLayout(new java.awt.CardLayout());

        firstCardPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Create Dialog From..."));
        firstCardPanel.setName("1"); // NOI18N

        dialogOptionGrp.add(optionExisting);
        optionExisting.setText("Existing File");

        dialogOptionGrp.add(optionNew);
        optionNew.setText("Create Dialog");

        dialogOptionGrp.add(jRadioButton5);
        jRadioButton5.setText("Using DB Report");

        javax.swing.GroupLayout firstCardPanelLayout = new javax.swing.GroupLayout(firstCardPanel);
        firstCardPanel.setLayout(firstCardPanelLayout);
        firstCardPanelLayout.setHorizontalGroup(
            firstCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(firstCardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(firstCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(firstCardPanelLayout.createSequentialGroup()
                        .addComponent(optionExisting)
                        .addGap(44, 44, 44)
                        .addComponent(optionNew))
                    .addComponent(jRadioButton5))
                .addContainerGap(77, Short.MAX_VALUE))
        );
        firstCardPanelLayout.setVerticalGroup(
            firstCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(firstCardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(firstCardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(optionExisting)
                    .addComponent(optionNew))
                .addGap(34, 34, 34)
                .addComponent(jRadioButton5)
                .addContainerGap(100, Short.MAX_VALUE))
        );

        cardsPanel.add(firstCardPanel, "1");

        chooseDialogPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Choose Dialog File"));
        chooseDialogPanel.setName("1.1"); // NOI18N

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        datfileChooserButton.setText("...");
        datfileChooserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                datfileChooserButtonActionPerformed(evt);
            }
        });

        datFileText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                datFileTextActionPerformed(evt);
            }
        });

        jLabel2.setText("Choose Data File:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(datFileText))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, 0)
                        .addComponent(datfileChooserButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(datfileChooserButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(datFileText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Choose Dialog File:");

        dialogFileText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogFileTextActionPerformed(evt);
            }
        });

        fileChooserButton.setText("...");
        fileChooserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileChooserButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(dialogFileText))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, 0)
                        .addComponent(fileChooserButton)
                        .addGap(0, 170, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fileChooserButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dialogFileText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout chooseDialogPanelLayout = new javax.swing.GroupLayout(chooseDialogPanel);
        chooseDialogPanel.setLayout(chooseDialogPanelLayout);
        chooseDialogPanelLayout.setHorizontalGroup(
            chooseDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chooseDialogPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(chooseDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        chooseDialogPanelLayout.setVerticalGroup(
            chooseDialogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chooseDialogPanelLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        cardsPanel.add(chooseDialogPanel, "1.1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cardsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(0, 0, 0)))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cardsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
		// TODO add your handling code here:
		//CardLayout cl = (CardLayout)cardsPanel.getLayout();
		//cl.next(cardsPanel);
		displayNextCard ();
		//this.backButton.setEnabled(true);
    }//GEN-LAST:event_nextButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
		// TODO add your handling code here:
		CardLayout cl = (CardLayout) cardsPanel.getLayout ();
		displayNextCard ();
    }//GEN-LAST:event_backButtonActionPerformed

    private void dialogFileTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogFileTextActionPerformed
		// TODO add your handling code here:
		if (dialogFileText.getText ().trim ().length () > 0) {
			this.createButton.setEnabled (true);
		} else {
			this.createButton.setEnabled (false);
		}
    }//GEN-LAST:event_dialogFileTextActionPerformed

    private void createButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createButtonActionPerformed
		// TODO add your handling code here:
		JvxConfiguration.theConfig ().setAppName (this.appNameText.getText ());
		JvxConfiguration.theConfig ().setDialogFile (this.dialogFileText.getText ());
		JvxConfiguration.theConfig ().setAppFolder (this.appFolderText.getText ());
		JvxConfiguration.theConfig ().setDataFile (this.datFileText.getText ());
		this.setVisible (false);
		this.dispose ();
    }//GEN-LAST:event_createButtonActionPerformed

    private void fileChooserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileChooserButtonActionPerformed
		// TODO add your handling code here:
		String s = fileDialog (JFileChooser.FILES_ONLY, "", "Choose Dialog file");
		this.dialogFileText.setText (s);
		if (s.length () > 0) {
			this.createButton.setEnabled (true);
		}
    }//GEN-LAST:event_fileChooserButtonActionPerformed

    private void cancellButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancellButtonActionPerformed
		// TODO add your handling code here:
		cardInfo.clear ();
		this.dispose ();
    }//GEN-LAST:event_cancellButtonActionPerformed

    private void datfileChooserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_datfileChooserButtonActionPerformed
		// TODO add your handling code here:
		String s = fileDialog (JFileChooser.FILES_ONLY, "", "Choose data File");
		this.datFileText.setText (s);
    }//GEN-LAST:event_datfileChooserButtonActionPerformed

    private void datFileTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_datFileTextActionPerformed
		// TODO add your handling code here:
    }//GEN-LAST:event_datFileTextActionPerformed

    private void appFolderTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appFolderTextActionPerformed
		// TODO add your handling code here:
    }//GEN-LAST:event_appFolderTextActionPerformed

    private void fileChooserButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileChooserButton1ActionPerformed
		// TODO add your handling code here:
		String s = fileDialog (JFileChooser.DIRECTORIES_ONLY, "", "Choose Application Location(Folder)");
		this.appFolderText.setText (s);
    }//GEN-LAST:event_fileChooserButton1ActionPerformed
	public String fileDialog (int option, String startAt, String title) {
		final JFileChooser fc = new JFileChooser (new File (JvxConfiguration.theConfig ().getAppFolder ()));
		String loc = null;
		fc.setDialogTitle (title);
		fc.setFileSelectionMode (option);
		fc.setCurrentDirectory (new File (startAt));
		int returnVal = fc.showOpenDialog (this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			loc = fc.getSelectedFile ().getAbsolutePath ();
		}
		return loc;
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
			java.util.logging.Logger.getLogger (WizardDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger (WizardDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger (WizardDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger (WizardDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the dialog */
		java.awt.EventQueue.invokeLater (new Runnable () {
			public void run () {
				WizardDialog dialog = new WizardDialog (new javax.swing.JFrame (), true);
				dialog.addWindowListener (new java.awt.event.WindowAdapter () {
					@Override
					public void windowClosing (java.awt.event.WindowEvent e) {
						System.exit (0);
					}
				});
				dialog.setVisible (true);
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField appFolderText;
    private javax.swing.JTextField appNameText;
    private javax.swing.JToggleButton backButton;
    private javax.swing.JToggleButton cancellButton;
    private javax.swing.JPanel cardsPanel;
    private javax.swing.JPanel chooseDialogPanel;
    private javax.swing.JButton createButton;
    private javax.swing.JTextField datFileText;
    private javax.swing.JButton datfileChooserButton;
    private javax.swing.JTextField dialogFileText;
    private javax.swing.ButtonGroup dialogOptionGrp;
    private javax.swing.JButton fileChooserButton;
    private javax.swing.JButton fileChooserButton1;
    private javax.swing.JPanel firstCardPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JToggleButton nextButton;
    private javax.swing.JRadioButton optionExisting;
    private javax.swing.JRadioButton optionNew;
    // End of variables declaration//GEN-END:variables
}
