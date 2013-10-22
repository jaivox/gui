
import com.jaivox.interpreter.Command;
import com.jaivox.interpreter.Interact;
import com.jaivox.util.Log;
import java.io.*;
import java.util.Properties;

public class console {

	static String basedir = "./";
	static String type = ".wav";
	Interact inter;
	static int wait = 10; // maximum length of input in seconds

	public console () {
		Log log = new Log ();
		log.setLevelByName ("fine");
		initializeInterpreter ();
		processQuestions ();
	}

	void initializeInterpreter () {
		Properties kv = new Properties ();
		kv.setProperty ("common_words", "common_en.txt");
		kv.setProperty ("questions_file", "test.quest");
		kv.setProperty ("grammar_file", "test.dlg");
		Command cmd = new Command ();
		inter = new Interact (basedir, kv, cmd);
	}

	void processQuestions () {
		try {
			BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
			do {
				System.out.print ("> ");
				String line = in.readLine ();
				String response = inter.execute (line);
				System.out.println (": "+response);
			} while (true);
		}
		catch (Exception e) {
			e.printStackTrace ();
			return;
		}
	}

	public static void main (String args []) {
		new console ();
	}
}

