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

package com.jaivox.ui.gui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author dev
 */
public class JvxHelpFrame extends JFrame
		implements ActionListener, HyperlinkListener {

	public String currentUrl;
	static ArrayList<String> urls;
	static int currentPosition;

	/**
	 * Creates new form JvxHelpFrame
	 */
	public JvxHelpFrame () {
		initComponents ();
		urls = new ArrayList<String> ();
		currentPosition = -1;
		setDefaultCloseOperation (HIDE_ON_CLOSE);
	}

	public JvxHelpFrame (String url) {
		initComponents ();
		urls = new ArrayList<String> ();
		currentUrl = url;
		currentPosition = 0;
		urls.add (url);
		setDefaultCloseOperation (HIDE_ON_CLOSE);
	}

	public void setHelpPage (String url) {
		currentUrl = url;
		currentPosition++;
		urls.add (currentPosition, url);
		try {
			URL next = new URL (currentUrl);
			String protocol = next.getProtocol ();
			// System.out.println ("hyperlink protocol: "+protocol);
			int n = urls.size ();
			// System.out.println ("Current Position: "+currentPosition+" Total "+n);
			HelpField.setPage (next);
			NextButton.setEnabled (false);
			PreviousButton.setEnabled (false);
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings ("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        HomeButton = new javax.swing.JButton();
        NextButton = new javax.swing.JButton();
        PreviousButton = new javax.swing.JButton();
        HelpArea = new javax.swing.JScrollPane();
        HelpField = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        HomeButton.setText("Home");
        HomeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HomeButtonActionPerformed(evt);
            }
        });

        NextButton.setText("Next");
        NextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NextButtonActionPerformed(evt);
            }
        });

        PreviousButton.setText("Previous");
        PreviousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PreviousButtonActionPerformed(evt);
            }
        });

        HelpArea.setViewportView(HelpField);
        HelpField.setEditable (false);
        HelpField.addHyperlinkListener (this);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(HelpArea)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(HomeButton)
                        .addGap(66, 66, 66)
                        .addComponent(PreviousButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(NextButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(HomeButton)
                    .addComponent(NextButton)
                    .addComponent(PreviousButton))
                .addGap(18, 18, 18)
                .addComponent(HelpArea, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void HomeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomeButtonActionPerformed
		// TODO add your handling code here:
		if (urls.size () == 0) {
			return;
		}
		currentUrl = urls.get (0);
		currentPosition = 0;
		try {
			HelpField.setPage (new URL (currentUrl));
			int n = urls.size ();
			// System.out.println ("Current Position: "+currentPosition+" Total "+n);
			if (currentPosition < n - 1) {
				NextButton.setEnabled (true);
			} else {
				NextButton.setEnabled (false);
			}
			if (currentPosition > 0) {
				PreviousButton.setEnabled (true);
			} else {
				PreviousButton.setEnabled (false);
			}
		} catch (Exception e) {
			e.printStackTrace ();
		}
    }//GEN-LAST:event_HomeButtonActionPerformed

    private void NextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NextButtonActionPerformed
		int n = urls.size ();
		if (currentPosition < n - 1) {
			currentPosition++;
			currentUrl = urls.get (currentPosition);
			try {
				HelpField.setPage (new URL (currentUrl));
				n = urls.size ();
				// System.out.println ("Current Position: "+currentPosition+" Total "+n);
				if (currentPosition < n - 1) {
					NextButton.setEnabled (true);
				} else {
					NextButton.setEnabled (false);
				}
				if (currentPosition > 0) {
					PreviousButton.setEnabled (true);
				} else {
					PreviousButton.setEnabled (false);
				}
			} catch (Exception e) {
				e.printStackTrace ();
			}
		}
    }//GEN-LAST:event_NextButtonActionPerformed

    private void PreviousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PreviousButtonActionPerformed
		int n = urls.size ();
		if (currentPosition > 0) {
			currentPosition--;
			currentUrl = urls.get (currentPosition);
			try {
				HelpField.setPage (new URL (currentUrl));
				n = urls.size ();
				// System.out.println ("Current Position: "+currentPosition+" Total "+n);
				if (currentPosition < n - 1) {
					NextButton.setEnabled (true);
				} else {
					NextButton.setEnabled (false);
				}
				if (currentPosition > 0) {
					PreviousButton.setEnabled (true);
				} else {
					PreviousButton.setEnabled (false);
				}
			} catch (Exception e) {
				e.printStackTrace ();
			}
		}
    }//GEN-LAST:event_PreviousButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
		// clear the urls
		urls = new ArrayList<String> ();
		currentPosition = -1;
		setVisible (false);
    }//GEN-LAST:event_formWindowClosing

	public void actionPerformed (ActionEvent event) {
		if (currentUrl == null) {
			return;
		}
		if (currentUrl.equals ("")) {
			return;
		}
		try {
			HelpField.setPage (new URL (currentUrl));
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}

	public void hyperlinkUpdate (HyperlinkEvent event) {
		if (event.getEventType () == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				URL next = event.getURL ();
				// System.out.println ("hyperlink: "+currentUrl);
				String protocol = next.getProtocol ();
				// System.out.println ("hyperlink protocol: "+protocol);
				if (protocol.equals ("file")) {
					currentUrl = next.toString ();
					currentPosition++;
					urls.add (currentPosition, currentUrl);
					HelpField.setPage (next);
					int n = urls.size ();
					// System.out.println ("Current Position: "+currentPosition+" Total "+n);
					if (currentPosition < n - 1) {
						NextButton.setEnabled (true);
					} else {
						NextButton.setEnabled (false);
					}
					if (currentPosition > 0) {
						PreviousButton.setEnabled (true);
					} else {
						PreviousButton.setEnabled (false);
					}
				} else if (protocol.startsWith ("http")) {
					if (!Desktop.isDesktopSupported ()) {
						System.out.println ("No desktop browser support for " + protocol);
						System.out.println ("while trying to open " + next.toString ());
					}
					Desktop desktop = Desktop.getDesktop ();
					if (desktop.isSupported (Desktop.Action.BROWSE)) {
						desktop.browse (next.toURI ());
					}
				} else {
					System.out.println ("Invalid protocol " + protocol);
					System.out.println ("while trying to open " + next.toString ());
				}
			} catch (Exception e) {
				e.printStackTrace ();
			}
		}
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
			java.util.logging.Logger.getLogger (JvxHelpFrame.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger (JvxHelpFrame.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger (JvxHelpFrame.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger (JvxHelpFrame.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater (new Runnable () {
			public void run () {
				new JvxHelpFrame ().setVisible (true);
			}
		});
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane HelpArea;
    private javax.swing.JEditorPane HelpField;
    private javax.swing.JButton HomeButton;
    private javax.swing.JButton NextButton;
    private javax.swing.JButton PreviousButton;
    // End of variables declaration//GEN-END:variables
}
