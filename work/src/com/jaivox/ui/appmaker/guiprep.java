package com.jaivox.ui.appmaker;


import java.io.*;
import java.nio.file.Files;
import java.util.Properties;

public class guiprep {
	
	static String outfile = "test.dlg";
	static String questions = "test.quest";
	static String errors = "data/errors.txt";
        
        public static void main (String args []) {
            generate();
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
                            if (s.length () == 0) continue;
                            out.println (s+"\t"+s+"\t(_,_,_,_,_,_,_)");
                    }
                    gg.writeQuestions (out);
                    out.close ();
            }
            catch (Exception e) {
                    e.printStackTrace ();
            }
	}
    
    //java 7
    public static void copyFile(String s, String t) throws IOException {
        File src = new File(s);
        File targ = new File(t);
        Files.copy(src.toPath(), targ.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
    static boolean isRecognizerEnabled(Properties conf, String name) {
        return conf.getProperty("recognizer_"+name, "false").equalsIgnoreCase("true");
    }
    static boolean isSynthesizerEnabled(Properties conf, String name) {
        return conf.getProperty("synthesizer_"+name, "false").equalsIgnoreCase("true");
    }
    static boolean isPlatformEnabled(Properties conf, String name) {
        return conf.getProperty("platform_"+name, "false").equalsIgnoreCase("true");
    }
    public static void generateApp(String conffile) {
        try {
            Properties conf = new Properties();
            conf.load(new FileInputStream(conffile));
            
            String project = conf.getProperty("project");
            String destination = conf.getProperty("destination");
            String cpsrc = conf.getProperty("cpsrc");
            String cfile = conf.getProperty("common_words");
            String efile = conf.getProperty("error_dlg");
            
            if(destination == null | cpsrc == null) {
                
            }
            if(!destination.endsWith(File.separator)) destination += File.separator;
            copyFile(cpsrc +"/"+ cfile, destination +"/"+ cfile);
            copyFile(cpsrc +"/"+ efile, destination +"/"+ efile);
            
            errors = cpsrc +"/"+ "errors.txt";
            outfile = destination + project +".dlg";
            questions = destination + project +".quest";
            Gui2Gram.dlgtree = destination + project +".tree";
            Rule2Fsm.dir = "";
            Rule2Fsm.name = destination + "dialog" +".tree";
            Gui2Gram.gram = destination + "dialog" +".tree";
            guiprep.generate();

            if(conf.getProperty("console", "false").equalsIgnoreCase("true")) {
                StringBuffer code = new StringBuffer();
                copyFile(cpsrc+ "/console.j", destination + "console.java");
                String clz = buildAppCode(code, "console", project);
                PrintWriter out = new PrintWriter (new FileWriter (destination + clz + ".java"));
                out.println(code.toString());
                out.close ();
            }
            if(isRecognizerEnabled(conf, "google")) {
                StringBuffer code = new StringBuffer();
                copyFile(cpsrc +"/runapp.j", destination + "runapp.java");
                String clz = buildAppCode(code, "runapp", project);
                PrintWriter out = new PrintWriter (new FileWriter (destination + clz + ".java"));
                out.println(code.toString());
                out.close ();
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    //temp code
    static String buildAppCode(StringBuffer code, String type, String appname) {
        String clz = Character.toUpperCase(appname.charAt(0)) + appname.substring(1) + 
                        Character.toUpperCase(type.charAt(0)) + type.substring(1);
            
        code.append("import com.jaivox.interpreter.Command;\nimport com.jaivox.interpreter.Interact;\n");
        code.append("import com.jaivox.synthesizer.web.Synthesizer;\n\n");
        code.append("import java.util.Properties;\nimport java.io.*;\n\n");
        code.append("public class ").append(clz);
        code.append(" {\n");
        code.append("\tpublic static void main(String[] args) {\n");
        code.append("\t\t").append(type).append(" c = new ").append(type);
        code.append("() {\n\t\t\t@Override\n\t\t\tvoid initializeInterpreter () {\n");
        code.append("\t\t\ttry {\n");
        code.append("\t\t\tProperties kv = new Properties ();\n");
        code.append("\t\t\tkv.load(new FileInputStream(\"").append(appname).append(".conf\"));\n");
        code.append("\t\t\tCommand cmd = new Command ();\n");
        code.append("\t\t\tinter = new Interact (basedir, kv, cmd);\n");
        if(!type.equals("console")) code.append("\t\t\tspeaker = new Synthesizer (basedir, kv);\n");
        code.append("\t\t\t}catch(Exception e) {e.printStackTrace(); }\n");
        code.append("\t\t\t}\t\t\t\n};\n");
        code.append("\t}").append("\n}");
        return clz;    
    }
}
		
