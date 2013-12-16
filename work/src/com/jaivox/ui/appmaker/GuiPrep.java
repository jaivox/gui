
package com.jaivox.ui.appmaker;

import com.jaivox.interpreter.Utils;
import com.jaivox.tools.Generator;
import com.jaivox.ui.gui.JvxMainFrame;
import com.jaivox.ui.gui.RunDialog;
import com.jaivox.util.Log;
import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GuiPrep {

	static String errors = "errors.txt";
	static boolean overwrite = true;
	static String patIndicator = "PAT";
	public static boolean Running = false;

	static void generate (Rule2Fsm rf, Gui2Gram gg, String outfile, String questions) {
		try {
			PrintWriter out = new PrintWriter (new FileWriter (outfile));
			rf.writeRules (out);
			gg.writeRules (out);
			out.close ();

			out = new PrintWriter (new FileWriter (questions));
			BufferedReader in = new BufferedReader (new FileReader (errors));
			String line;
			while ((line = in.readLine ()) != null) {
				String s = line.trim ().toLowerCase ();
				if (s.length () == 0) {
					continue;
				}
				out.println (s + "\t" + s + "\t(_,_,_,_,_,_,_)");
			}
			gg.writeQuestions (out);
			out.close ();

		} catch (Exception e) {
			e.printStackTrace ();
		}
	}

	//java 7
	public static void copyFile (String s, String t) throws IOException {
		File src = new File (s);
		File targ = new File (t);
		Files.copy (src.toPath (), targ.toPath (), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
	}

	static boolean isRecognizerEnabled (Properties conf, String name) {
		return conf.getProperty ("recognizer_" + name, "false").equalsIgnoreCase ("true");
	}

	static boolean isSynthesizerEnabled (Properties conf, String name) {
		return conf.getProperty ("synthesizer_" + name, "false").equalsIgnoreCase ("true");
	}

	static boolean isPlatformEnabled (Properties conf, String name) {
		return conf.getProperty ("platform_" + name, "false").equalsIgnoreCase ("true");
	}

	public static void generateApp (String conffile) {
		try {
			Properties conf = new Properties ();
			conf.load (new FileInputStream (conffile));

			String project = conf.getProperty ("project");
			String appfolder = conf.getProperty ("appfolder");
			String cpsrc = conf.getProperty ("cpsrc");
			String cfile = conf.getProperty ("common_words");
			String efile = conf.getProperty ("error_dlg");

			if (appfolder == null | cpsrc == null) {
			}
			if (!appfolder.endsWith (File.separator)) {
				appfolder += File.separator;
			}
			copyFile (cpsrc + "/" + cfile, appfolder + "/" + cfile);
			copyFile (cpsrc + "/" + efile, appfolder + "/" + efile);

			errors = cpsrc + "/" + "errors.txt";
			String outfile = appfolder + project + ".dlg";
			String questions = appfolder + project + ".quest";
			// Gui2Gram.dlgtree = appfolder + project + ".tree";
			Rule2Fsm rf = new Rule2Fsm (appfolder, "dialog.tree");
			Gui2Gram gg = new Gui2Gram (appfolder, "dialog.tree", project + ".tree");
			// Rule2Fsm.name = appfolder + "dialog" + ".tree";
			// Gui2Gram.gram = appfolder + "dialog" + ".tree";
			GuiPrep.generate (rf, gg, outfile, questions);

			// Generator gen = new Generator (conffile);
			// batch version used from Gui console
			Generator gen = new Generator (conf);
			gen.createQuestions ();
			String asr = conf.getProperty ("recognizer");
			if (asr.equals ("sphinx")) {
				// change to create live version also
				conf.setProperty ("input", "live");
				conf.setProperty ("live", project);
				File cf = new File (appfolder + "live.conf");
				cf.createNewFile ();
				BufferedWriter bf = new BufferedWriter (new FileWriter (cf));
				conf.store (bf, "Live version");
				// Generator gen2 = new Generator (conf);
				// gen2.createQuestions ();
				System.out.println ("live.conf generated for live sphinx version");
			}

			System.out.println ("Application Generated: Path: " + appfolder);

		} catch (Exception ex) {
			ex.printStackTrace ();
		}
	}

	public static void runApp (String conffile) throws FileNotFoundException, IOException {
		new Log ();
		Log.setLevelByName ("info");
		Properties conf = new Properties ();
		conf.load (new FileInputStream (conffile));
		String recognizer = conf.getProperty ("recognizer");
		System.out.println ("runApp: recognizer is " + recognizer);
		if (recognizer.equals ("console")) {
		}
		if (recognizer.equals ("web")) {
			System.out.println ("Going to google recognizer");
			Running = true;
			//AppWeb app = new AppWeb (conf);

		}
		if (recognizer.equals ("sphinx")) {
			// make sure lmgen.sh is run on the sphinx destination
			// before calling app
			String result = generateLm (conf);
			if (result == null) {
				System.out.println ("Could not run lmgensh");
				return;
			}
			System.out.println ("Going to the Sphinx recognizer");
			Running = true;
			AppSphinx app = new AppSphinx (conf);
		}
		RunDialog.runDialog (conffile, JvxMainFrame.getInstance ());
	}

	public static void stopRunning () {
		Running = false;
	}

	static String generateLm (Properties conf) throws IOException {
		String appDir = conf.getProperty ("destination");
		String Sep = System.getProperty ("file.separator");
		if (!appDir.endsWith (Sep)) {
			appDir = appDir + Sep;
		}
		String shellScript = appDir + "lmgen.sh";

		// fixperms (shellScript, "rxw");
		fixperms (shellScript, "x");
		System.out.println ("running " + shellScript);
		String result = runcommand (shellScript);
		return result;
	}

	static void fixperms (String file, String perms) {
		try {
			runcommand ("chmod a+" + perms + " " + file);
		} catch (IOException ex) {
			Logger.getLogger (GuiPrep.class.getName ()).log (Level.SEVERE, null, ex);
		}
	}

	static String runcommand (String input) throws IOException {
		Process p = Runtime.getRuntime ().exec (input);
		StringBuffer buffer = new StringBuffer ();
		InputStream in = p.getInputStream ();
		BufferedInputStream d = new BufferedInputStream (in);
		do {
			int ch = d.read ();
			if (ch == -1) {
				break;
			}
			buffer.append ((char) ch);
		} while (true);
		in.close ();
		String temp = new String (buffer);
		return temp;
	}

	//temp code
	static void doSphinxStuff (String treefile, Properties conf) throws FileNotFoundException, IOException {
		String project = conf.getProperty ("project");
		String appfolder = conf.getProperty ("appfolder");
		String cpsrc = conf.getProperty ("cpsrc");

		String sentfile = treefile.substring (0, treefile.lastIndexOf ('.')) + ".sent";
		BufferedReader in = new BufferedReader (new FileReader (treefile));
		PrintWriter out = new PrintWriter (new FileWriter (sentfile));

		String line;
		while ((line = in.readLine ()) != null) {
			String upper = line.trim ().toUpperCase ();
			if (upper.length () == 0) {
				continue;
			}
			if (upper.charAt (0) == '*') {
				upper = upper.substring (1);
			}
			if (!Character.isLetterOrDigit (upper.charAt (upper.length () - 1))) {
				upper = upper.substring (0, upper.length () - 2);
			}
			out.println ("<s> " + upper + " </s>");
		}
		in.close ();
		out.close ();

		generateFile (conf, cpsrc, appfolder, "project.config.xml");
		generateFile (conf, cpsrc, appfolder, "lmgen.sh");
	}

	static boolean generateFile (Properties kv, String src, String dest, String name) {
		String common = src;
		try {
			Set keys = kv.stringPropertyNames ();
			int n = keys.size ();
			String okeys[] = new String[n];
			Point op[] = new Point[n];
			int pi = 0;
			for (Iterator<String> it = keys.iterator (); it.hasNext ();) {
				String key = it.next ();
				okeys[pi] = key;
				op[pi] = new Point (pi, -key.length ());
				pi++;
			}
			Utils.quicksortpointy (op, 0, n - 1);
			String filename = common + name;
			String destname = dest + name;
			if (!okOverwrite (destname)) {
				Log.severe (destname + " exists. To overwrite, set overwrite_files to yes");
				return true;
			}
			String text = loadFile (filename);
			String changed = text;

			// replace longest keys first
			for (int i = 0; i < n; i++) {
				Point p = op[i];
				String key = okeys[p.x];
				String val = kv.getProperty (key);
				if (name.startsWith (key)) {
					String newname = name.replaceFirst (key, val);
					// Log.fine ("Destination name: "+newname);
					destname = dest + newname;
					if (!okOverwrite (destname)) {
						Log.severe (destname + " exists. To overwrite, set overwrite_files to yes");
						return true;
					}
				}
				String pat = patIndicator + key;
				if (text.indexOf (pat) != -1) {
					// Log.fine ("replacing "+pat+" with "+val+" in "+name);
					if (val != null) {
						changed = changed.replace (pat, val);
					} else {
						Log.info ("Missing value for " + pat);
					}
				}
			}
			if (changed.indexOf (patIndicator) != -1) {
				changed = fixmisses (filename, changed);
			}
			if (!writeFile (destname, changed)) {
				return false;
			}
			Log.info ("wrote: " + destname);
			return true;
		} catch (Exception e) {
			e.printStackTrace ();
			return false;
		}
	}

	static String loadFile (String filename) {
		try {
			BufferedReader in = new BufferedReader (new FileReader (filename));
			String line;
			StringBuffer sb = new StringBuffer ();
			while ((line = in.readLine ()) != null) {
				sb.append (line + "\n");
			}
			in.close ();
			String text = new String (sb);
			return text;
		} catch (Exception e) {
			e.printStackTrace ();
			return null;
		}
	}

	static String fixmisses (String filename, String text) {
		try {
			Log.info ("Commenting out lines containing missing patterns with //");
			Log.info ("Plese check output for syntax errors.");
			StringTokenizer st = new StringTokenizer (text, "\n", true);
			StringBuffer sb = new StringBuffer ();
			while (st.hasMoreTokens ()) {
				String token = st.nextToken ();
				if (token.indexOf (patIndicator) != -1 && token.indexOf ("CLASSPATH") == -1) {
					Log.warning ("Missing value in " + token + " in file " + filename);
					token = "// " + token;
				}
				sb.append (token);
			}
			return new String (sb);
		} catch (Exception e) {
			e.printStackTrace ();
			Log.severe ("Errors while trying to remove missing tags in " + filename);
			return text;
		}
	}

	static boolean writeFile (String filename, String text) {
		try {
			PrintWriter out = new PrintWriter (new FileWriter (filename));
			out.print (text);
			out.close ();
			return true;
		} catch (Exception e) {
			e.printStackTrace ();
			return false;
		}
	}

	static boolean okOverwrite (String filename) {

		try {
			File f = new File (filename);
			if (f.exists ()) {
				if (!overwrite) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace ();
			return false;
		}
	}
}
