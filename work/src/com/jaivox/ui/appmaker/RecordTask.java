/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaivox.ui.appmaker;

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


/**
 *
 * @author rj
 */
public class RecordTask extends SwingWorker<Void, Void> {

    TargetDataLine channel;
    AudioFormat wavformat;
    DataLine.Info info;
    Recorder sampler;
    boolean chanelopen = false;
    String samplefile;
    
    public RecordTask() {
        wavformat = new AudioFormat (8000.0f, 16, 1, true, false);
        info = new DataLine.Info (TargetDataLine.class, wavformat);
        try {
            channel = (TargetDataLine) AudioSystem.getLine (info);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(RecordTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void record() {
        try {
            sampler = new Recorder(channel, wavformat, samplefile);
            firePropertyChange("info", "stop", "Start speaking...");
            //new Thread(sampler).start();
            sampler.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
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

    static String runcommand (String input) {
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

    /*
     * Executed in event dispatching thread
     */
    @Override
    public void done() {
        closechannel();
    }

    @Override
    protected Void doInBackground() {
        record();
        return null;
    }
}
class Recorder implements Runnable {

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

	public void run () {
		try {
			File stream = new File (filename);
			channel.open (wavformat);
			channel.start ();
			AudioInputStream incoming = new AudioInputStream (channel);
			AudioSystem.write (incoming, AudioFileFormat.Type.WAVE, stream);
		}
		catch (Exception e) {
			Debug ("Error recording: "+e.toString ());
		}
	}

};