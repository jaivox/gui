/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaivox.ui.appmaker;

import javax.swing.SwingWorker;

/**
 *
 * @author rj
 */
public abstract class JvxRunnableApp extends SwingWorker<Void, Void> {
    protected abstract void processSpeech(String speech);
    protected abstract String getSpeechFile();
    public abstract void speak(String speech);
    
    @Override
    protected Void doInBackground() throws Exception {
        processSpeech( getSpeechFile() );
        return null;
    }
}
