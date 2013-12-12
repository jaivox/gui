/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaivox.ui.appmaker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.SwingWorker;
import javax.swing.Timer;


/**
 *
 * @author rj
 */
public class RecordTask implements ActionListener{

    Timer timer;
    int delay = 10000; // in milliseconds
    TargetDataLine channel;
    AudioFormat wavformat;
    DataLine.Info info;
    boolean chanelopen = false;
    String samplefile;
    
    Recorder sampler;
    protected PropertyChangeListener propertyChangeListener = null;
        
    public RecordTask(int recorderTimeout) {
        wavformat = new AudioFormat (8000.0f, 16, 1, true, false);
        info = new DataLine.Info (TargetDataLine.class, wavformat);
        delay = recorderTimeout;
        timer = new Timer(delay, this);
            
        try {
            channel = (TargetDataLine) AudioSystem.getLine (info);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(RecordTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void record(String file) { 
        setSampleFile(file);
        record();
    }
    
    public void record() {
        try {
            sampler = new Recorder(channel, wavformat, samplefile);
            sampler.addPropertyChangeListener(propertyChangeListener);
            
            //new Thread(sampler).start();
            sampler.execute();
            timer.start();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void closechannel() {
        try {
            channel.stop();
            channel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeListener = l;
    }
    public String getSampleFile() {
        return samplefile;
    }
    public void setSampleFile(String f) {
        samplefile = f;
    }
    public static void play(String filename)
    {
        try
        {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(filename)));
            clip.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static String wavToflac(String file) {
        return convert(file);
    }
    static String convert (String wavfile) {
		int n = wavfile.length ();
		String flac = wavfile.substring (0, n-4) + ".flac";
		String cmd = "sox "+wavfile+" "+flac+" rate 16k";
		String result = runcommand (cmd);
		if (result != null)
			return flac;
		else return "error";
	}

/**
 * Run a command in the current operating system using the Runtime class.
 * @param input
 * @return 
 */
    public static String runcommand (String input) {
		try {
			Process p = Runtime.getRuntime ().exec (input);
			StringBuffer buffer = new StringBuffer ();
			InputStream in = p.getInputStream ();
			BufferedInputStream d = new BufferedInputStream (in);
			do {
				int ch = d.read ();
				if (ch == -1)
					break;
				buffer.append ((char) ch);
			} while (true);
			in.close ();
			String temp = new String (buffer);
			return temp;
		} catch (Exception e) {
			e.printStackTrace ();
			return null;
		}
	}
    public void actionPerformed (ActionEvent e) {   // timer
        closechannel();
    }
    public void stopRecording() {
        if(sampler != null) {
            closechannel();
            sampler.cancel(true);
        }
        timer.stop();
    }

    public boolean isDone() {
        return sampler == null ? true : sampler.isDone();
    }
}
class Recorder extends SwingWorker<Void, Void> {

	String filename;
	TargetDataLine channel;
        AudioFormat wavformat;
	

	public Recorder (TargetDataLine channel, AudioFormat wavformat, String name) {
		this.channel = channel;
                this.wavformat = wavformat;
	
		filename = name;
	}

	void Debug (String s) {
		System.out.println ("[Recorder] "+s);
	}

        @Override
	public Void doInBackground () {
		try {
			File stream = new File (filename);
			channel.open (wavformat);
                        
                        firePropertyChange("info", "", "Start speaking...");
            
			channel.start ();
			AudioInputStream incoming = new AudioInputStream (channel);
			AudioSystem.write (incoming, AudioFileFormat.Type.WAVE, stream);
		}
		catch (Exception e) {
			Debug ("Error recording: "+e.toString ());
		}
            return null;
	}
    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() {
        try {
            channel.stop();
            channel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
};