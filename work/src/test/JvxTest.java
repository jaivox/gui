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

import com.jaivox.ui.gui.JvxMainFrame;
import com.jaivox.ui.gui.JvxMainFrame;
import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class JvxTest {
    private static JFrame parent = null;
    static Robot bot = null;

    public JvxTest(JvxMainFrame aThis) {
        parent = aThis;
    }
    public void simpletest() {
        try {
            bot = new Robot();
            bot.setAutoDelay(100);
            new Thread()
            {
                public void run() {
                    // open dialog file
                    menuClick("File", "Open Dialog Tree");
                    System.out.println("menu clicked");
                    bot.delay(3*1000);
                    
                    // focus text field
                    for(int i = 0; i < 7; i++) sendKey(KeyEvent.VK_TAB);
                    keyin("samples/quick/turing/manusfood");
                    sendOK();
                    
                    // click Run
                    bot.delay(6 * 1000);
                    buttonClick("Run");
                    bot.delay(1000);
                    sendOK();
                    bot.delay(1000);
                    sendOK();
                    bot.delay(2*1000);
                    
                    // the questions...
                    typeAndSend("What is your favorite food");
                    typeAndSend("how do you not know");
                    typeAndSend("so");
                    typeAndSend("do you like sports");
                    typeAndSend("do you like to read");
                    typeAndSend("What are your hobbies");
                    typeAndSend("Why did you answer my question with a question");
                    typeAndSend("why are you so annoying");
                }
            }.start();
        } catch (AWTException ex) {
            Logger.getLogger(JvxTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    static void typeAndSend(String speech) {
        bot.delay(1*1000);
        keyin(speech);
        bot.delay(1*1000);
        sendMnemonic(KeyEvent.VK_S);
        bot.delay(5*1000);
        sendMnemonic(KeyEvent.VK_S);
        bot.delay(1*1000);
    }
    static void keyin(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection sel = new StringSelection( text );
        clipboard.setContents(sel, sel);
        bot.keyPress(KeyEvent.VK_CONTROL);
        bot.keyPress(KeyEvent.VK_V);
        bot.keyRelease(KeyEvent.VK_V);
        bot.keyRelease(KeyEvent.VK_CONTROL);
    }
    static void sendOK() {
        bot.keyPress(KeyEvent.VK_ENTER);
        bot.keyRelease(KeyEvent.VK_ENTER);
    }
    static void sendMnemonic(int key) {
        bot.keyPress(KeyEvent.VK_ALT);
        sendKey(key);
        bot.keyRelease(KeyEvent.VK_ALT);
    }
    static void sendKey(int key) {
        bot.keyPress(key); 
        bot.keyRelease(key);
    }
    
    static void buttonClick(String text) {
        for (Component c : getAllComponents (parent)) {
            if (c instanceof AbstractButton) {
                final AbstractButton btn = (AbstractButton) c;
                if (btn != null) {
                    if(btn.getText().equals(text)) {
                        new Thread()
                        {
                            public void run() {
                                btn.doClick();
                            }
                        }.start();
                        
                        System.out.println(text + " button clicked");
                    }
                }
            }
        }
    }
    
    static void menuClick(final String menu, final String item) {
        final JMenuBar bar = parent.getJMenuBar();
        new Thread()
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