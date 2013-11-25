
package com.jaivox.ui.appmaker;

import com.jaivox.interpreter.Utils;
import com.jaivox.tools.Generator;
import com.jaivox.util.Log;
import java.awt.Point;
import java.io.*;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

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
			String questions = appfolder + project +".quest";
			// Gui2Gram.dlgtree = appfolder + project + ".tree";
			Rule2Fsm rf = new Rule2Fsm (appfolder, "dialog.tree");
			Gui2Gram gg = new Gui2Gram (appfolder, "dialog.tree", project+".tree");
			// Rule2Fsm.name = appfolder + "dialog" + ".tree";
			// Gui2Gram.gram = appfolder + "dialog" + ".tree";
			GuiPrep.generate (rf, gg, outfile, questions);
			
			// Generator gen = new Generator (conffile);
			Generator gen = new Generator (conf);
			gen.createQuestions ();


			System.out.println ("Application Generated: Path: " + appfolder);

		} catch (Exception ex) {
			ex.printStackTrace ();
		}
	}
	
	public static void runApp (String conffile) {
		try {
			Properties conf = new Properties ();
			conf.load (new FileInputStream (conffile));
			String recognizer = conf.getProperty ("recognizer");
			System.out.println ("runApp: recognizer is "+recognizer);
			if (recognizer.equals ("console")) {
				// pop up a console and run?
				System.out.println ("Console option to be implemented");
			}
			if (recognizer.equals ("web")) {
				System.out.println ("Going to google recognizer");
				Running = true;
				AppWeb app = new AppWeb (conf);
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
		}
		catch (Exception e) {
			e.printStackTrace ();
		}
	}

	public static void stopRunning () {
		Running = false;
	}

	static String generateLm (Properties conf) {
		try {
			String appDir = conf.getProperty ("destination");
			String Sep = System.getProperty ("file.separator");
			if (!appDir.endsWith (Sep)) appDir = appDir + Sep;
			String shellScript = appDir + "lmgen.sh";
			String result = runcommand (shellScript);
			return result;
		} catch (Exception e) {
			e.printStackTrace ();
			return null;
		}

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

	
	//temp code
	static String buildAppCode (StringBuffer code, String type, String appname) {
		String clz = Character.toUpperCase (appname.charAt (0)) + appname.substring (1)
				+ Character.toUpperCase (type.charAt (0)) + type.substring (1);

		code.append ("import com.jaivox.interpreter.Command;\nimport com.jaivox.interpreter.Interact;\n");
		code.append ("import com.jaivox.synthesizer.web.Synthesizer;\n\n");
		code.append ("import java.util.Properties;\nimport java.io.*;\n\n");
		code.append ("public class ").append (clz);
		code.append (" {\n");
		code.append ("\tpublic static void main(String[] args) {\n");
		if (type.equals ("runappsphinx")) {
			code.append ("\t\trunappsphinx.config=\"").append (appname)
					.append (".config.xml").append ("\";\n");
		}
		code.append ("\t\t").append (type).append (" c = new ").append (type);
		code.append ("() {\n\t\t\t@Override\n\t\t\tvoid initializeInterpreter () {\n");
		code.append ("\t\t\ttry {\n");
		code.append ("\t\t\tProperties kv = new Properties ();\n");
		code.append ("\t\t\tkv.load(new FileInputStream(\"").append (appname).append (".conf\"));\n");
		code.append ("\t\t\tCommand cmd = new Command ();\n");
		code.append ("\t\t\tinter = new Interact (basedir, kv, cmd);\n");
		if (!type.equals ("console")) {
			code.append ("\t\t\tspeaker = new Synthesizer (basedir, kv);\n");
		}
		code.append ("\t\t\t}catch(Exception e) {e.printStackTrace(); }\n");
		code.append ("\t\t\t}\n\t\t\t};\n");
		code.append ("\t}").append ("\n}");
		return clz;
	}

	static void doSphinxStuff (String treefile, Properties conf) throws FileNotFoundException, IOException {
		new Log ();
		Log.setLevelByName ("FINEST");
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
