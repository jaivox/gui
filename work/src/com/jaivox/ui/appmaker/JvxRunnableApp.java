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

package com.jaivox.ui.appmaker;

import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker;

/**
 *
 * @author rj
 */
public interface JvxRunnableApp {

	public void process (String speech);

	public void processSpeech (String speech);

	public String getSpeechFile ();

	public void speak (String speech);

	public void setPropertyChangeListener (PropertyChangeListener l);

	public boolean isDone ();

	public void cancel (boolean b);

	public void done ();
}

abstract class JvxApp implements JvxRunnableApp {

	protected JvxAppWorker worker = null;
	protected PropertyChangeListener propertyChangeListener = null;
	protected String speechFile = null;

	@Override
	public String getSpeechFile () {
		return this.speechFile;
	}

	public PropertyChangeListener getPropertyChangeListener () {
		return propertyChangeListener;
	}

	@Override
	public void setPropertyChangeListener (PropertyChangeListener propertyChangeListener) {
		this.propertyChangeListener = propertyChangeListener;
	}

	protected void firePropertyChange (String prop, String value) {
		worker.firePropertyChange (prop, "", value);
	}

	public boolean isCancelled () {
		return worker == null ? true : worker.isCancelled ();
	}

	public boolean isDone () {
		return worker == null ? true : worker.isDone ();
	}

	public void cancel (boolean b) {
		if (worker != null) {
			worker.cancel (b);
		}
	}

	public void done () {
		worker = null;
	}

	@Override
	public void process (String speech) {
		speechFile = speech;
		worker = new JvxAppWorker (this);
		worker.addPropertyChangeListener (propertyChangeListener);
		worker.execute ();
	}
}

class JvxAppWorker extends SwingWorker<Void, Void> {

	private JvxRunnableApp theApp = null;

	public JvxAppWorker (JvxRunnableApp theApp) {
		this.theApp = theApp;
	}

	public JvxRunnableApp getTheApp () {
		return theApp;
	}

	public void setTheApp (JvxRunnableApp theApp) {
		this.theApp = theApp;
	}

	@Override
	protected Void doInBackground () throws Exception {
		theApp.processSpeech (theApp.getSpeechFile ());
		return null;
	}

	@Override
	public void done () {
		theApp.done ();
	}
}
