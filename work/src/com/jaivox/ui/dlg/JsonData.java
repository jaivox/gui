package com.jaivox.ui.dlg;


import java.io.BufferedReader;
import java.io.FileReader;
import com.google.gson.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class JsonData {
	
	QAnode Root;
	
	public JsonData (String filename) {
		String message = readFile (filename);
		JsonElement je = new JsonParser ().parse (message);
		// System.out.println ("Json parse result:");
		// System.out.println (je.toString ());
		JsonObject rootObject = je.getAsJsonObject ();
		JsonArray rules = rootObject.getAsJsonArray ("rules");
		// Debug ("root has "+rules.size ()+" elements");
		Root = new QAnode ();
		insertDetails (Root, rules);
	}
	public QAnode getRoot() {
    return Root;
  }
	void Debug (String s) {
		System.out.println ("[JsonData]" + s);
	}
	
	String readFile (String filename) {
		try {
			StringBuilder sb = new StringBuilder ();
			BufferedReader in = new BufferedReader (new FileReader (filename));
			String line;
			while ((line = in.readLine ()) != null) {
				sb.append (line);
				sb.append ('\n');
			}
			in.close ();
			String result = new String (sb);
			return result;
		}
		catch (Exception e) {
			e.printStackTrace ();
			return null;
		}
	}
	
	void insertDetails (QAnode node, JsonArray objects) {
		try {
			int dialog = 0;
			for (Iterator <JsonElement> it = objects.iterator (); it.hasNext (); ) {
				JsonElement je = it.next ();
				if (je.isJsonNull ()) continue;
				JsonObject jo = je.getAsJsonObject ();
				if (jo.isJsonNull ()) continue;
				String dkey = "dialog"+dialog;
				dialog++;
				JsonObject qas = jo.getAsJsonObject (dkey);
				// System.out.println (qas.toString ());
				// System.out.println ();
				JsonArray qs = qas.getAsJsonArray ("Qs");	
				JsonArray as = qas.getAsJsonArray ("As");
				if (qs == null || as == null) continue;
				// Debug ("qs, as not null");
				if (qs.isJsonNull () || as.isJsonNull ()) continue;
				// Debug ("qs, as not json null");
				if (qs.size () == 0 || as.size () == 0) continue;
				// Debug ("qs, as have things in it ");
				ArrayList <String> questions = new ArrayList <String> ();
				ArrayList <String> answers = new ArrayList <String> ();
				int nq = qs.size ();
				int na = as.size ();
				// Debug ("Inserting nq="+nq+" na="+na);
				for (int i=0; i<nq; i++) {
					String key = "q"+i;
					JsonElement qo = qs.get (i);
					if (qo.isJsonNull ()) continue;
					JsonObject qoo = qo.getAsJsonObject ();
					JsonPrimitive qf = qoo.getAsJsonPrimitive (key);
					String question = qf.getAsString ();
					questions.add (question);
				}
				for (int i=0; i<na; i++) {
					String key = "a"+i;
					JsonElement ao = as.get (i);
					if (ao.isJsonNull ()) continue;
					JsonObject aoo = ao.getAsJsonObject ();
					JsonPrimitive af = aoo.getAsJsonPrimitive (key);
					String answer = af.getAsString ();
					answers.add (answer);
				}
				QAnode child = new QAnode (questions, answers);
				node.addFollowup (child);
				// Debug ("Added rule nq="+nq+" na="+na);
				JsonArray fs = qas.getAsJsonArray ("Fs");
				if (fs != null && fs.size () > 0) {
					insertDetails (child, fs);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace ();
		}
	}
	
	public void writeAsTabbed (String filename) {
		try {
			PrintWriter out = new PrintWriter (new FileWriter (filename));
			out.println ("// "+filename);
			out.println ();
			ArrayList <QAnode> rules = Root.getFollowups ();
			if (rules != null) {
				for (Iterator <QAnode> it = rules.iterator (); it.hasNext ();) {
					QAnode node = it.next ();
					out.println (node.writeAsTabbed (0));
				}
			}
			out.close ();
			Debug ("Wrote "+filename);
		}
		catch (Exception e) {
			e.printStackTrace ();
		}
	}
	
}

