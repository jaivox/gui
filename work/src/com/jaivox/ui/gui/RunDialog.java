/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jaivox.ui.gui;

import com.jaivox.ui.appmaker.AppConsole;
import com.jaivox.ui.appmaker.AppSphinx;
import com.jaivox.ui.appmaker.AppWeb;
import com.jaivox.ui.appmaker.JvxRunnableApp;
import com.jaivox.ui.appmaker.RecordTask;
import java.awt.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;

/**
 *
 * @author dev
 */
public class RunDialog extends javax.swing.JDialog 
                       implements PropertyChangeListener, ActionListener {
    
        String configFile = null;
        Properties conf = null;
        
        RecordTask recorder = null;
        Timer timer;
        static int delay = 10000; // in milliseconds
        
        String recognizer = null;
        String speechfile = null;
        int curSpeech = 0;
        
        JvxRunnableApp app = null;
        
        final boolean customizedTA = false;
        DocumentFilter docl = new LineFilter();
        
	public void setConfigFile(String f) {
            configFile = f;
        }
	public RunDialog (String file, java.awt.Frame parent, boolean modal) {
            super (parent, modal);
            new com.jaivox.util.Log();
            this.setConfigFile(file);
            initComponents ();
            
            ((AbstractDocument)this.queryArea.getDocument()).setDocumentFilter(null);
            
            conf = new Properties ();
            try {
                conf.load (new FileInputStream (configFile));
            } catch (Exception ex) {
                Logger.getLogger(RunDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
            recognizer = conf.getProperty ("recognizer");
            
            if (recognizer.equals ("console")) {
                this.speakButton.setEnabled(false);
                
                if(customizedTA) {
                    ((AbstractDocument)this.queryArea.getDocument()).setDocumentFilter(docl);
                }
            }
			
            timer = new Timer(delay, this);
            
        }
        public JvxRunnableApp getJvxApp(String recognizer) {
            //String clz = conf.getProperty(recognizer + ".class");
            //Class.forName(clz).newInstance();
            JvxRunnableApp jap = null;
            
            if (recognizer.equals ("web")) {
                jap = new AppWeb(conf);
            }
            else if (recognizer.equals ("sphinx")) {
                jap = new AppSphinx(conf);
            }
            else if (recognizer.equals ("console")) {
                this.speakButton.setEnabled(false);
                jap = new AppConsole(conf);
            }
            if(jap != null) jap.addPropertyChangeListener(this);
            return jap;
        }
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings ("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        queryArea = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        answerArea = new javax.swing.JTextArea();
        speakButton = new javax.swing.JButton();
        sendButton = new javax.swing.JButton();
        quitButton = new javax.swing.JButton();
        playButton = new javax.swing.JButton();
        voiceCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Test Dialog");

        queryArea.setColumns(20);
        queryArea.setLineWrap(true);
        queryArea.setRows(5);
        queryArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(queryArea);

        answerArea.setColumns(20);
        answerArea.setRows(5);
        jScrollPane2.setViewportView(answerArea);

        speakButton.setText("Speak");
        speakButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speakButtonActionPerformed(evt);
            }
        });

        sendButton.setText("Send");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        quitButton.setText("Quit");
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitButtonActionPerformed(evt);
            }
        });

        playButton.setText("Play");
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });

        voiceCheckBox.setText("Voice directions");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(speakButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(83, 83, 83)
                .addComponent(voiceCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
                .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(46, 46, 46))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addComponent(jScrollPane2))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(playButton, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(quitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sendButton)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(speakButton)
                        .addComponent(voiceCheckBox)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(playButton)
                    .addComponent(quitButton)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void speakButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speakButtonActionPerformed
        // TODO add your handling code here:
        //setInfoText("Speak, click on Send or wait for 10 seconds for processing ...");
        String sf = getNextSpeechFile();
        
        recorder = new RecordTask();
        recorder.addPropertyChangeListener(this);
        recorder.setSampleFile(sf);
        
        conf.put("speech_file", sf);
        app = getJvxApp(recognizer);
        
        recorder.execute();
        timer.start();
        speakButton.setEnabled(false);
    }//GEN-LAST:event_speakButtonActionPerformed

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        // TODO add your handling code here:
        if(!customizedTA && recognizer.equals ("console")) 
        if(evt.getActionCommand().equals("Clear")) {
            this.queryArea.setText("");
            this.sendButton.setText("Send");
            return;
        }
        else this.sendButton.setText("Clear");
        
        stopRecording();    // stop capture
        
        if (recognizer.equals ("console")) {
            conf.put("speech_file", getConsoleQuery());
            app = getJvxApp(recognizer);
        }
        if(app != null) app.execute();
        
    }//GEN-LAST:event_sendButtonActionPerformed
    String getConsoleQuery() {
        String text = null;
        try {
            Document doc = this.queryArea.getDocument();
            Element root = doc.getDefaultRootElement();
            int numLines = root.getElementCount() - 1;
            while(numLines >= 0) {
                Element el = root.getElement(numLines);
                int lineStart = el.getStartOffset();
                int lineEnd = el.getEndOffset();
                text = doc.getText(lineStart, lineEnd - lineStart);
                text = text.trim();
                if(text.length() <=0) numLines--;
                else break;
            }
            if(customizedTA && text != null && text.charAt(0) == '>') text = text.substring(1);
            
        } catch (BadLocationException ex) {
            Logger.getLogger(LineFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return text;
    }
    boolean isAppRunning() {
        return app == null ? false : !app.isDone();
    }
    boolean isRecorderRunning() {
        return recorder == null ? false : !recorder.isDone();
    }
    private void quitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitButtonActionPerformed
        // TODO add your handling code here:
        setInfoText("Closing App...");
        stopRecording();
        stopJvxApp();
        try {
            while( isAppRunning() || isRecorderRunning() ) {
                setInfoText("Waiting for Thread to close...");
                Thread.sleep(100);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(RunDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.dispose();
        this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_quitButtonActionPerformed

    private void playButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        // TODO add your handling code here:
        stopRecording();
        String sf = getSpeechFile();
        RecordTask.play(sf == null ? "test.wav" : sf);
    }//GEN-LAST:event_playButtonActionPerformed

    public void setInfoText(String info) {
        String s = queryArea.getText();
        if(s.length() > 0) s = s + "\n";
        s = s + info;
        queryArea.setText(s);
        
        if(voiceCheckBox.isSelected()) app.speak(info);
    }
    public void setResultText(String result) {
        String s = answerArea.getText();
        if(s.length() > 0) s = s + "\n";
        s = s + result;
        answerArea.setText(s);
        
        if(voiceCheckBox.isSelected()) app.speak(result);
    }
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("propertyChange: " + evt.getPropertyName() +" "+ evt.getNewValue());
        if ("info".equals(evt.getPropertyName())) {
            setInfoText((String) evt.getNewValue());
        }
        if ("result".equals(evt.getPropertyName())) {
            setResultText((String) evt.getNewValue());
        }
    }
    public void actionPerformed (ActionEvent e) {   // timer
        stopRecording();
    }
    void stopRecording() {
        if(recorder != null) {
            recorder.cancel(true);
            timer.stop();
            speakButton.setEnabled(true);
        }
    }
    void stopJvxApp() {
        if(app != null) {
            app.cancel(true);
        }
    }
    public String getNextSpeechFile() {
        curSpeech++;
        String base = conf.getProperty("appfolder");
        return base + "test_" + curSpeech + ".wav";
    }
    public String getSpeechFile() {
        String base = conf.getProperty("appfolder");
        return curSpeech == 0 ? null : (base + "test_" + curSpeech + ".wav");
    }
    public static void runDialog(final String conf, final Frame parent) {
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater (new Runnable () {
                public void run () {
                        RunDialog dialog = new RunDialog (conf, (Frame) parent, true);

                        //dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                        dialog.addWindowListener (new java.awt.event.WindowAdapter () {
                                @Override
                                public void windowClosing (java.awt.event.WindowEvent e) {
                                    JvxMainFrame.getInstance().setRunEnabled(true);
                                        //System.exit (0);
                                }
                        });
                        dialog.setVisible (true);
                }
        });
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
			java.util.logging.Logger.getLogger (RunDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger (RunDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger (RunDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger (RunDialog.class.getName ()).log (java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the dialog */
		java.awt.EventQueue.invokeLater (new Runnable () {
			public void run () {
				RunDialog dialog = new RunDialog ("", new javax.swing.JFrame (), true);
                                //dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
    private javax.swing.JTextArea answerArea;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JButton playButton;
    private javax.swing.JTextArea queryArea;
    private javax.swing.JButton quitButton;
    private javax.swing.JButton sendButton;
    private javax.swing.JButton speakButton;
    private javax.swing.JCheckBox voiceCheckBox;
    // End of variables declaration//GEN-END:variables

    
}

class LineFilter extends DocumentFilter {
    public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr)
            throws BadLocationException {
        //if ( canEdit(fb, offset) ) {
            super.insertString(fb, offset, string, attr);
        //}
    }

    public void remove(final FilterBypass fb, final int offset, final int length) throws BadLocationException {
        
        if ( canEdit(fb, offset) ) {
            super.remove(fb, offset, length);
        }
    }

    public void replace(final FilterBypass fb, final int offset, final int length, final String text, final AttributeSet attrs)
            throws BadLocationException {
        if ( canEdit(fb, offset) ) {
            String s = addCmdChar(fb, offset, text);
            super.replace(fb, offset, length, s, attrs);
        }
    }
    private String addCmdChar(final FilterBypass fb, int offset, String text) {
        Document doc = fb.getDocument();
        Element root = doc.getDefaultRootElement();
        int numLines = root.getElementCount();
        Element el = root.getElement(numLines - 1);
        int lineStart = el.getStartOffset();
        int lineEnd = el.getEndOffset();
        if(offset > lineStart) return text;
        if((lineEnd - lineStart) > 1) return text;
        return ">" + text;
    }
    private boolean canEdit(final FilterBypass fb, int offset) {
        try {
            Document doc = fb.getDocument();
            Element root = doc.getDefaultRootElement();
            int index = root.getElementIndex(offset);
            int numLines = root.getElementCount() - 1;
            while(numLines > 0) {
                Element el = root.getElement(numLines);
                int lineStart = el.getStartOffset();
                int lineEnd = el.getEndOffset();
                String s = doc.getText(lineStart, lineEnd - lineStart);
                if(s.trim().length() <=0) numLines--;
                else break;
            }
            return index >= numLines;
        } catch (BadLocationException ex) {
            Logger.getLogger(LineFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
