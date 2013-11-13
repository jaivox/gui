
import com.jaivox.util.Log;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class runappsphinx extends runapp {
        static String config = "batch.xml";
        
	public runappsphinx () {
		super();
	}

        void processSpeech () {
		ConfigurationManager cm = new ConfigurationManager (
				getClass().getResource (config));
		// allocate the recognizer
		Log.info ("Loading...");
		Recognizer recognizer = (Recognizer) cm.lookup ("recognizer");
		recognizer.allocate ();

		// start the microphone or exit if the programm if this is not possible
		Microphone microphone = (Microphone) cm.lookup ("microphone");
		if (!microphone.startRecording ()) {
			Log.severe ("Cannot start microphone.");
			recognizer.deallocate ();
			System.exit (1);
		}

		System.out.println ("Sample questions are in lm_training_file");

		try {
			// loop the recognition until the programm exits.
			while (true) {
				System.out.println ("Start speaking. Press Ctrl-C to quit.\n");

				Result result = recognizer.recognize ();
				String recognized = null;
				String response = null;

				if (result != null) {
					recognized = result.getBestResultNoFiller ();
					System.out.println ("You said: " + recognized + '\n');
				} else {
					System.out.println ("I can't hear what you said.");
					continue;
				}
				if (recognized != null) {
					response = inter.execute (recognized);
					System.out.println ("Reply: " + response);
					Thread.sleep (4000);
					speaker.speak (response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}
	public static void main (String args []) {
		new runapp ();
	}
}

