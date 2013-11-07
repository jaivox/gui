/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaivox.ui.gengram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

/**
 *
 * @author lin
 */
public class GrammarGenerator {
    public static String DLG_DLIM = "()\r\n";
    static parse P = null;;
    static wnlink W;

    static String tests [];

    public GrammarGenerator(String dataFolder) {
        //W.synsfile = dataFolder + W.synsfile;
        parse.penntags = dataFolder + P.penntags;
        
        //W = new wnlinkDb ();
        W = new wnlinkJWNL ();
        W.createsyns ();
    }
    public void load(String dlgFile, String datFile) {
        if(datFile != null) {
            W.addtablecolumn (datFile, ",\r\n", 3, 0);
        }
        
        if(dlgFile != null)  {
            P = new parse (dlgFile);
            if (P.Valid) P.createsentences ();
        }
    }
    public String[] getSynonyms(String word) {
        return W.getsynonyms (word);
    }
    public String[] getSynonyms(String word, String form) {
        return W.synsget (word, form);
    }
    public sentence getSentence(String key) {
        return P.sentences.get(key);
    }
    public ArrayList <String> getParsedStatements() {
        return P.statements;
    }
    public void generate (String filename) {
        if(P == null || !P.Valid) return;
        TreeMap <String, sentence> sentences = P.sentences;
        Set <String> keys = sentences.keySet ();
        int n = keys.size ();
        tests = keys.toArray (new String [n]);
        for (int i=0; i<n; i++) {
            String key = tests [i];
            sentence s = sentences.get (key);
            // s.show (""+i+" ");
            // s.findmultiwords (W);
            s.multiwordsubs (P, W);
        }
/*
        // generate using okays instead of subs
        for (int i=0; i<n; i++) {
            String key = tests [i];
            sentence s = sentences.get (key);
            System.out.println ("Sentence "+i+" Generating okays for: "+key);
            s.generateokays ();
        }
*/    }

    public ArrayList<String> parseDialog(String dlg) {
        ArrayList<String> sents = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer (dlg, DLG_DLIM);
        while (st.hasMoreTokens ()) {
            String token = st.nextToken ().trim ();
            if (token.length () == 0) continue;
            if (!token.endsWith ("?") && !token.endsWith (".")) {
                    token = token + ".";
            }
            sents.add(token);
        }
        return sents;
    }
    
    public boolean addSynonyms(String word, String[] addsyns, String tag) {
        int news = 0;
        
        String[] ar = W.synsget(word, tag);
        ArrayList<String> arl = new ArrayList<String>();
        if(ar != null) arl.addAll(Arrays.asList(ar));
        for(String k : addsyns) {
            if(!arl.contains(k)) {
                arl.add(k);
                news++;
            }
        }
        //arl.addAll(Arrays.asList(addsyns));
        ar = arl.toArray(new String[0]);
        W.synsput(word, ar, tag);

        ar = W.dbsyns.get(word);
        if(ar != null) {
            arl = new ArrayList<String>();
            if(ar != null) arl.addAll(Arrays.asList(ar));
            for(String k : addsyns) {
                if(!arl.contains(k)) {
                    arl.add(k);
                }
            }
            //arl.addAll(Arrays.asList(addsyns));
            ar = arl.toArray(new String[0]);
            W.dbsyns.put(word, ar);
        }
        //W.dumpSynonyms();
        return news > 0;
    }
    public sentence generateAlts(String key) {
        TreeMap <String, sentence> sentences = P.sentences;
        sentence old = sentences.get (key);
        if(old == null) return null;
        sentence sent = new sentence(old.orig, old.form, old.tree);
        sent.setSelectionhandler(old.getSelectionhandler());
        sent.multiwordsubs (P, W);
        sent.generateokays ();
        sentences.put(key, sent);
        return sent;
    }
    public static SentenceX createSentence(String statement) {
        if(P == null) P = new parse();
        sentence sent = P.doparse (statement);
        if (sent != null) {
            P.sentences.put (sent.orig, sent);
            sent.multiwordsubs (P, W);
            sent.generateokays ();
        }
        SentenceX sx = sent == null ? null : new SentenceX( sent );
        return sx;
    }
    public static void removeSentence(Object key) {
        P.sentences.remove(key);
    }
}
