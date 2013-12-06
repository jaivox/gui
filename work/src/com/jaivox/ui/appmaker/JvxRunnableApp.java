/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaivox.ui.appmaker;

import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker;


/**
 *
 * @author rj
 */
public interface JvxRunnableApp {
    public void process(String speech);
    public void processSpeech(String speech);
    public String getSpeechFile();
    public void speak(String speech);

    public void setPropertyChangeListener(PropertyChangeListener l);

    public boolean isDone();

    public void cancel(boolean b);

    public void done();
}
abstract class JvxApp implements JvxRunnableApp {
    protected JvxAppWorker worker = null;
    protected PropertyChangeListener propertyChangeListener = null;
    protected String speechFile = null;

    @Override
    public String getSpeechFile() {
        return this.speechFile;
    }
    
    public PropertyChangeListener getPropertyChangeListener() {
        return propertyChangeListener;
    }

    @Override
    public void setPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
        this.propertyChangeListener = propertyChangeListener;
    }
    protected void firePropertyChange(String prop, String value) {
        worker.firePropertyChange(prop, "", value);
    }
    
    public boolean isCancelled() {
        return worker == null ? true : worker.isCancelled();
    }
    public boolean isDone() {
        return worker == null ? true : worker.isDone();
    }
    public void cancel(boolean b) {
        if(worker != null) worker.cancel(b);
    }
    public void done() {
        worker = null;
    }
    @Override
    public void process(String speech) {
        speechFile = speech;
        worker = new JvxAppWorker(this);
        worker.addPropertyChangeListener( propertyChangeListener );
        worker.execute();
    }
}

class JvxAppWorker extends SwingWorker<Void, Void> {
    private JvxRunnableApp theApp = null;

    public JvxAppWorker(JvxRunnableApp theApp) {
        this.theApp = theApp;
    }

    public JvxRunnableApp getTheApp() {
        return theApp;
    }

    public void setTheApp(JvxRunnableApp theApp) {
        this.theApp = theApp;
    }
    
    @Override
    protected Void doInBackground() throws Exception {
        theApp.processSpeech( theApp.getSpeechFile() );
        return null;
    }
    @Override
    public void done() {
        theApp.done();
    }
}