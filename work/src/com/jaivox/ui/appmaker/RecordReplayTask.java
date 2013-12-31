/*
   Jaivox Application Generator (JAG) version 0.1 December 2013
   Copyright 2010-2013 by Bits and Pixels, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

Please see work/licenses for licenses to other components included with
this package.
*/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jaivox.ui.appmaker;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author rj
 */
public class RecordReplayTask extends RecordTask {
    static String records_base = "work/apps/samples/quick/turing/audio/";
    String tag = "00";
    String filebase = "road";

    public RecordReplayTask () {
        super();
    }

    public void record () {
        try {
            String s = samplefile.substring(samplefile.lastIndexOf("/") + 1);
            /*
            s = samplefile.substring(
                                    samplefile.lastIndexOf("_") + 1,
                                    samplefile.lastIndexOf("."));
            s = String.format(filebase + "%02d.wav", Integer.parseInt(s));
            */
            System.out.println("RecordReplayTask: " + records_base + s);
            
            copyFile(records_base + s, samplefile);
        } catch (IOException ex) {
            Logger.getLogger(RecordReplayTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void copyFile (String s, String t) throws IOException {
        File src = new File (s);
        File targ = new File (t);
        Files.copy (src.toPath (), targ.toPath (), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    public void closechannel () {
    }

    public void actionPerformed (ActionEvent e) {
    }

    public void stopRecording () {
    }

    public boolean isDone () {
        return true;
    }
}
