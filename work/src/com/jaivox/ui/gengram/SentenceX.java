/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaivox.ui.gengram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author lin
 */
public class SentenceX implements SelectionHandler {

    private sentence theSentence = null;
    private ArrayList<ArrayList<Object>> tabModvalues = null;
    private ArrayList<String> excludes = new ArrayList<String>();
    public static Set<String> userWords = new TreeSet<String>();
    
    public void setTheSentence(sentence theSentence) {
        this.theSentence = theSentence;
        this.theSentence.setSelectionhandler(this);
        tabModvalues = null;
    }
    public SentenceX(sentence c)
    {
        setTheSentence(c);
    }
    public sentence getSentence() {
        return theSentence;
    }
    public String getSentenceKey() {
        return theSentence.orig;
    }
    public String[] getWords() {
        return this.theSentence.words;
    }
    public String[][] getOkayWords() {
        return this.theSentence.okay;
    }
    public String toString() {
        return this.theSentence.orig;
    }
    public void generateokays (ArrayList <String> oks) {
        if(tabModvalues == null)
            theSentence.generateokays();
        oks.addAll(theSentence.alts);
    }
    public Object[] getSentenceOptions () {
        return theSentence.alts.toArray();
    }
    public static Object[][] transpose(Object [][] mat) {
        int rows = mat.length;
        int cols = 0;
        for(Object[] o : mat) cols = o != null ? Math.max(cols, o.length) : cols;
        Object [][] tpose = new Object[cols][rows];
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < cols; j++) {
                if(mat[i] == null || mat[i].length <= j) tpose[j][i] = "";
                else tpose[j][i] = mat[i][j];
            }
        }
        return tpose;
    }
    public Object[][] getWordOptions() {
        String[][] okwords = getOkayWords();
        return transpose(okwords); 
    }
    public void debug() {
        theSentence.Debug(" --- SentenceX ---");
        if(tabModvalues == null) {System.out.println("null"); return; }
        for(ArrayList cells : tabModvalues) {
            for(Object cell : cells) {
                if(cells.toString().trim().length() > 0) System.out.println(cell);
            }
        }
    }
    public boolean isExcluded(String word) {
        return excludes.contains(word);
    }
    public void addExclusion(String s) {
        excludes.add(s);
    }
    public void removeExclusion(String s) {
        excludes.remove(s);
    }
    public ArrayList<ArrayList<Object>> getTabModvalues() {
        return tabModvalues;
    }

    public void setTabModvalues(ArrayList<ArrayList<Object>> tabModvalues) {
        this.tabModvalues = tabModvalues;
    }
    
    public String dump(int level) {
        StringBuffer sb = new StringBuffer();
        if(tabModvalues == null) theSentence.generateokays();
        Object[] alts = getSentenceOptions();
        sb.append('*').append(this.theSentence.orig).append('\n');
        for(Object alt : alts) {
            String sent = (String)alt;
            if(sent.equals(this.theSentence.orig)) continue;
            boolean sel = true;
            String pad = "";
            for(String ex : excludes) {
                if(sent.contains(" "+ ex.trim())) {
                    sel = false; break;
                }
            }
            if(level > 1) {
                String format = "%"+(level - 1)+"s";
                pad = String.format(format, " ").replace(' ', '\t');
            }
            pad = "\t";
            if(sel) sb.append(pad).append(alt).append('\n');
        }
        return sb.toString();
    }

    @Override
    public String[] filterUnSelected(String[] all) {
        if(all == null || all.length <=0) return all;
        int pre = all.length;
        List<String> allsyns = new ArrayList<String>();
        allsyns.addAll(Arrays.asList(all));
        allsyns.removeAll(this.excludes);
        //System.out.println("filterUnSelected: "+ pre +"---"+ allsyns.size());
        return allsyns.toArray(new String[allsyns.size()]);
    }

    @Override
    public boolean skipPOSFormMatch(String word) {
        return userWords.contains(word);
    }
    public void addUserWord(String s) {
        userWords.add(s);
    }
    public void removeUserWord(String s) {
        userWords.remove(s);
    }
}
