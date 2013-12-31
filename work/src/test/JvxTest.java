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
package test;

import com.jaivox.ui.gui.JvxConfiguration;
import com.jaivox.ui.gui.JvxMainFrame;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class JvxTest {
    private static JFrame parent = null;
    static Robot bot = null;
    String appfolder = "";
    int procdelay = 6;

    public JvxTest(JvxMainFrame aThis) {
        parent = aThis;
        try {
            bot = new Robot();
        } catch (AWTException ex) {
            Logger.getLogger(JvxTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        bot.setAutoDelay(100);
        appfolder = new File(JvxConfiguration.theConfig().getAppFolder()).getAbsolutePath();
    }
    void showmsg(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }
    public void simpleVoiceTest() {
        showmsg("About to start Automated testing... Please dont change the window focus!");
        new Thread()
        {
            public void run() {
                System.out.println("Start Testing...");
                
                // open dialog file
                menuClick("File", "Open Dialog Tree");
                bot.delay(1 * 1000);

                // focus text field. May not work on other lookNfeel or if the file dialog has different accessory layout
                for(int i = 0; i < 7; i++) sendKey(KeyEvent.VK_TAB);
                keyin("samples/quick/turing/manusfood");    // dialog file
                bot.delay(2 * 1000); 
                sendOK();
                bot.delay(procdelay * 1000);            // wait for dialog load
                
                // enable Google
                buttonClick("Google");
                bot.delay(1 * 1000);
                
                // click Run
                buttonClick("Run");
                bot.delay(1000);
                sendOK();
                bot.delay(1000);
                for(int i = 0; i < 7; i++) sendKey(KeyEvent.VK_TAB);
                sendKey(KeyEvent.VK_CONTROL, KeyEvent.VK_A);
                keyin(appfolder);                                 // App folder
                bot.delay(2 * 1000);
                sendOK();
                bot.delay((procdelay/2) * 1000);

                // the questions...
                speakAndSend();
                speakAndSend();
                speakAndSend();
                
                showmsg("Testing completed!");
            }
        }.start();
    }
    public void simpletest() {
        showmsg("About to start Automated testing... Please dont change the window focus!");
        new Thread()
        {
            public void run() {
                
                buttonClick("Expand Synonyms");
                bot.delay(500);
                procdelay = 60;     // more wait for exapand
                
                // open dialog file
                menuClick("File", "Open Dialog Tree");
                System.out.println("menu clicked");
                bot.delay(1 * 1000);

                // focus text field. May not work on other lookNfeel or if the file dialog has different accessory layout
                for(int i = 0; i < 7; i++) sendKey(KeyEvent.VK_TAB);
                keyin("samples/quick/turing/manusfood");    // dialog file
                bot.delay(2 * 1000); 
                sendOK();
                bot.delay(procdelay * 1000);            // wait for dialog load
                
                // click Run
                buttonClick("Run");
                bot.delay(1000);
                sendOK();
                bot.delay(1000);
                for(int i = 0; i < 7; i++) sendKey(KeyEvent.VK_TAB);
                sendKey(KeyEvent.VK_CONTROL, KeyEvent.VK_A);
                keyin(appfolder);                                 // App folder
                bot.delay(2 * 1000);
                sendOK();
                bot.delay((procdelay/2) * 1000);

                // the questions...
                typeAndSend("What is your favorite food");
                typeAndSend("how do you not know");
                typeAndSend("so");
                typeAndSend("do you like sports");
                typeAndSend("do you like to read");
                typeAndSend("What are your hobbies");
                typeAndSend("Why did you answer my question with a question");
                typeAndSend("why are you so annoying");
                
                showmsg("Testing completed!");
            }
        }.start();
    }
    
    static void speakAndSend() {
        bot.delay(1*1000);
        sendMnemonic(KeyEvent.VK_K);
        bot.delay(1*1000);
        sendMnemonic(KeyEvent.VK_S);
        bot.delay(5*1000);
    }
    static void typeAndSend(String speech) {
        bot.delay(1*1000);
        keyin(speech);
        bot.delay(1*1000);
        sendMnemonic(KeyEvent.VK_S);
        bot.delay(3*1000);
        sendMnemonic(KeyEvent.VK_S);
        bot.delay(1*1000);
    }
    static void keyin(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection sel = new StringSelection( text );
        clipboard.setContents(sel, sel);
        sendKey(KeyEvent.VK_CONTROL, KeyEvent.VK_V);
    }
    static void sendOK() {
        bot.keyPress(KeyEvent.VK_ENTER);
        bot.keyRelease(KeyEvent.VK_ENTER);
    }
    static void sendMnemonic(int key) {
        sendKey(KeyEvent.VK_ALT, key);
    }
    static void sendKey(int key) {
        bot.keyPress(key); 
        bot.keyRelease(key);
    }
    static void sendKey(int modkey, int key) {
        bot.keyPress(modkey);
        sendKey(key);
        bot.keyRelease(modkey);
    }
    static boolean buttonClick(String text) {
        boolean b = false;
        for (Component c : getAllComponents (parent)) {
            if (c instanceof AbstractButton) {
                final AbstractButton btn = (AbstractButton) c;
                if (btn != null) {
                    if(btn.getText().equals(text)) {
                        new Thread()    // otherwise the confirm msgbox will block the testing thread!
                        {
                            public void run() {
                                btn.doClick();
                            }
                        }.start();
                        b = true;
                    }
                }
            }
        }
        System.out.println(text + " button " + (b ? "clicked" : "NOT Found!!!"));
        return b;
    }
    
    static void menuClick(final String menu, final String item) {
        final JMenuBar bar = parent.getJMenuBar();
        new Thread()  // otherwise the file dialog will block the testing thread!
        {
            public void run() {
                for(int i = 0; i < bar.getMenuCount(); i++) {
                    JMenu m = bar.getMenu(i);
                    if(m.getText().equals(menu)) {
                        for(int j = 0; j < m.getItemCount(); j++) {
                            JMenuItem it = m.getItem(j);
                            if(it.getText().equals(item)) {
                                it.doClick();
                            }
                        }
                    }
                }
            }
        }.start();
    }
    static List<Component> getAllComponents (final Container c) {
        Component[] comps = c.getComponents ();
        List<Component> compList = new ArrayList<Component> ();
        for (Component comp : comps) {
                compList.add (comp);
                if (comp instanceof Container) {
                        compList.addAll (getAllComponents ((Container) comp));
                }
        }
        return compList;
    }
}