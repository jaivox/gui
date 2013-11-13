
import com.jaivox.interpreter.Command;
import com.jaivox.interpreter.Interact;
import com.jaivox.util.Log;
import com.jaivox.recognizer.web.SpeechInput;
import com.jaivox.recognizer.web.Mike;
import com.jaivox.synthesizer.web.Synthesizer;
import java.util.Properties;

public class runapp {

	static String basedir = "./";
	static String type = ".wav";
	Interact inter;
	Synthesizer speaker;
	static int wait = 10; // maximum length of input in seconds

	public runapp () {
		Log log = new Log ();
		log.setLevelByName ("info");
		initializeInterpreter ();
		processSpeech ();
	}

	void initializeInterpreter () {
		Properties kv = new Properties ();
		kv.setProperty ("common_words", "common_en.txt");
		kv.setProperty ("questions_file", "test.quest");
		kv.setProperty ("grammar_file", "test.dlg");
		kv.setProperty ("ttslang", "en");
		Command cmd = new Command ();
		inter = new Interact (basedir, kv, cmd);
		speaker = new Synthesizer (basedir, kv);
	}

	void processSpeech () {
		SpeechInput R = new SpeechInput ();
		Mike mike = new Mike ("test", type);
		int empty = 0;
		int maxempty = 5;
		while (true) {
			String flac = mike.nextsample (type, wait);
			mike.showtime ("result is "+flac);
			if (flac != null) {
				String result = R.recognize (flac, "en_US");
				System.out.println ("Recognized: " + result);
				String response = "";
				if (result != null) {
					if (result.trim ().equals ("")) {
						empty++;
						if (empty >= maxempty) return;
					}
					response = inter.execute (result);
					System.out.println ("Reply: " + response);
				}
				try {
					Thread.sleep (4000);
					// can't always play flac
					speaker.speak (response);
				} catch (Exception e) {
					e.printStackTrace ();
					break;
				}
			}
		}
	}

	public static void main (String args []) {
		new runapp ();
	}
}

