
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

	static String outfile = "test.dlg";
	static String questions = "test.quest";
	static String errors = "errors.txt";
	static boolean overwrite = true;
	static String patIndicator = "PAT";

	public static void main (String args[]) {
		generate ();
	}

	static void generate () {

		Rule2Fsm rf = new Rule2Fsm ();
		Gui2Gram gg = new Gui2Gram ();
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
			String destination = conf.getProperty ("destination");
			String cpsrc = conf.getProperty ("cpsrc");
			String cfile = conf.getProperty ("common_words");
			String efile = conf.getProperty ("error_dlg");

			if (destination == null | cpsrc == null) {
			}
			if (!destination.endsWith (File.separator)) {
				destination += File.separator;
			}
			copyFile (cpsrc + "/" + cfile, destination + "/" + cfile);
			copyFile (cpsrc + "/" + efile, destination + "/" + efile);

			errors = cpsrc + "/" + "errors.txt";
			outfile = destination + project + ".dlg";
			questions = destination + project + ".quest";
			Gui2Gram.dlgtree = destination + project + ".tree";
			Rule2Fsm.dir = "";
			Rule2Fsm.name = destination + "dialog" + ".tree";
			Gui2Gram.gram = destination + "dialog" + ".tree";
			GuiPrep.generate ();

			if (conf.getProperty ("console", "false").equalsIgnoreCase ("true")) {
				StringBuffer code = new StringBuffer ();
				copyFile (cpsrc + "/console.java", destination + "console.java");
				String clz = buildAppCode (code, "console", project);
				PrintWriter out = new PrintWriter (new FileWriter (destination + clz + ".java"));
				out.println (code.toString ());
				out.close ();
			}
			if (isRecognizerEnabled (conf, "google")) {
				StringBuffer code = new StringBuffer ();
				copyFile (cpsrc + "/runapp.java", destination + "runapp.java");
				String clz = buildAppCode (code, "runapp", project);
				PrintWriter out = new PrintWriter (new FileWriter (destination + clz + ".java"));
				out.println (code.toString ());
				out.close ();
			}
			if (isRecognizerEnabled (conf, "sphinx")) {
				StringBuffer code = new StringBuffer ();
				doSphinxStuff (Gui2Gram.dlgtree, conf);
				copyFile (cpsrc + "/runapp.java", destination + "runapp.java");
				copyFile (cpsrc + "/runappsphinx.java", destination + "runappsphinx.java");
				copyFile (cpsrc + "/ccs.ccs", destination + "/ccs.ccs");
				String clz = buildAppCode (code, "runappsphinx", project);
				PrintWriter out = new PrintWriter (new FileWriter (destination + clz + ".java"));
				out.println (code.toString ());
				out.close ();

				//usingJvGen(conffile);
			}
			System.out.println ("Application Generated: Path: " + destination);

		} catch (Exception ex) {
			ex.printStackTrace ();
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
		String destination = conf.getProperty ("destination");
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

		generateFile (conf, cpsrc, destination, "project.config.xml");
		generateFile (conf, cpsrc, destination, "lmgen.sh");
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