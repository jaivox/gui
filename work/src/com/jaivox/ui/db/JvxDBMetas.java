/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jaivox.ui.db;

import com.jaivox.ui.gui.JvxConfiguration;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author rj
 */
public class JvxDBMetas {
    //gengram.javadb jdb;
    public static List<String> dbs = null; /*Arrays.asList(
                                        new String[]{  "Select DB Type", "mysql_X", "mysql_Win", "postgresql" }
                                            ); */	
    public static Properties dbGlobals = new Properties();

    Connection con = null;
    int selection = -1;
    String selDb = null;
    
    boolean valid = false;
    Map<String, List<String>> tabNCols = null;
    
    static {
        try {
            dbGlobals.load(new FileInputStream(JvxConfiguration.datadir + "dbglobals.properties"));
        } catch (IOException ex) {
            Logger.getLogger(JvxDBMetas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static List<String> getKeys(String type) {
        ArrayList<String> keys = new ArrayList<String>();
        for(Entry es : dbGlobals.entrySet()) {
            String k = (String) es.getKey();
            if(k.startsWith(type)) {
                String key[] = k.split("\\.");
                if(key!= null && key. length > 1) keys.add(key[key.length-1]);
            }
        }
        return keys;
    }
    public static Map<String, String> getKeysVals(String type) {
        Map<String, String> kv = new HashMap<String, String>();
        for(Entry es : dbGlobals.entrySet()) {
            String k = (String) es.getKey();
            if(k.startsWith(type)) {
                String key[] = k.split("\\.");
                kv.put(key[key.length-1], (String) es.getValue());
            }
        }
        return kv;
    }
    public static String[] getDbTypes() {
        if(dbs != null) return dbs.toArray(new String[dbs.size()]);
        dbs = new ArrayList<String>();
        dbs.add("Select DB...");
        for(Object es : dbGlobals.keySet()) {
            String k = (String) es;
            String key[] = k.split("\\.");
            if(!dbs.contains(key[0])) dbs.add(key[0]);
        }
        return dbs.toArray(new String[dbs.size()]);
    }
    public static String getDbGlobalValue(String type, String key) {
        return dbGlobals.getProperty(type +"."+ key);
    }
    public static int getDBTypeIndex(String type) {
        return dbs.indexOf(type);
    }
    public Properties getParams(String type) {
        Properties params = new Properties();
        for(Entry es : dbGlobals.entrySet()) {
            String k = (String) es.getKey();
            if(k.startsWith(type + ".param")) {
                String key[] = k.split("\\.");
                if(key!= null && key. length > 1) params.put(key[2], es.getValue());
            }
        }
        return params;
    }
    public JvxDBMetas (String sel) throws Exception {
        selDb = sel;
        valid = false;
        //if(con != null) con.close();
        Class.forName (getDbGlobalValue(selDb, "driver"));//(driver.get(selection));
        String url = getDbGlobalValue(selDb, "protocol") + getDbGlobalValue(selDb, "subprotocol");//protocol.get(selection) + subprotocol.get(selection) ;
        con = DriverManager.getConnection (url, getParams(selDb));
        Statement s = con.createStatement ();
        valid = true;
    }

    void debug (String s) {
            System.out.println ("[JvxDBMetas]: " + s);
    }

    void close () {
        try {
                if(con != null) con.close ();
                System.out.println ("Database connection closed.");
        }
        catch (Exception e) {
                e.printStackTrace ();
        }
    }
    public List<String> getTablesAndViews(List<String> tables) throws SQLException {
        if(!valid) return tables;
        if(tables == null) tables = new ArrayList<String>();

        if(tabNCols != null && tabNCols.size() > 0) {
            tables.addAll(tabNCols.keySet());
            return tables;
        }
        DatabaseMetaData databaseMetaData = con.getMetaData();
        String   catalog          = null;
        String   schemaPattern    = null;
        String   tableNamePattern = null;
        String[] types            = null;
        
        ResultSet result = databaseMetaData.getTables(
            catalog, schemaPattern, tableNamePattern, types );

        while(result.next()) {
            String tableName = result.getString("TABLE_NAME");
            tables.add(tableName);
            //debug("getTablesViews: " + tableName);
        }
        if(result != null) result.close();
        return tables;
    }
    public List<String> getTableFields(String tab) throws SQLException {
        if(!valid) return null;
        List<String> cols = null;
        if(tabNCols != null && tabNCols.size() > 0) {
            cols = tabNCols.get(tab);
            if(cols != null) return cols;
        }
        if(tabNCols == null) tabNCols = new HashMap();
        cols = new ArrayList<String>();
        String   catalog           = null;
        String   schemaPattern     = null;
        String   tableNamePattern  = tab;
        String   columnNamePattern = null;
        DatabaseMetaData databaseMetaData = con.getMetaData();
        
        ResultSet result = databaseMetaData.getColumns(
            catalog, schemaPattern,  tableNamePattern, columnNamePattern);

        while(result.next()){
            String columnName = result.getString("COLUMN_NAME");
            //int    columnType = result.getInt(5);
            cols.add(columnName);
        }
        if(result != null) result.close();
        tabNCols.put(tab, cols);
        return cols;
    }
    public Map<String, List<String>> getTableMetaData() throws SQLException {
        if(!valid) return null;
        if(tabNCols == null) tabNCols = new HashMap();
        List<String> tabs = getTablesAndViews(null);
        for(String t : tabs) {
            if(tabNCols.get(t) != null) continue;
            List<String> cols = getTableFields(t);
            tabNCols.put(t, cols);
        }
        return tabNCols;
    }
    void getClassInfo (String focusquery) {
        try {
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery (focusquery);

                ResultSetMetaData rsMetaData = rs.getMetaData ();

                int ncols = rsMetaData.getColumnCount ();
                System.out.println ("Schema: ncols = " + ncols);

                for (int i=1; i<=ncols; i++) {
                        String name = rsMetaData.getColumnName (i);
                        System.out.println("column " + i +": "+name);
                }
        }
        catch (Exception e) {
                e.printStackTrace ();
        }
    }
    String buildSelectQuery(String s) {
        return "SELECT * FROM " + s +";";
    }
    public List<List<String>> queryTab (String tab) {
        try {
            if(!valid) return null;
        
            Statement st = con.createStatement ();
            ResultSet rs = st.executeQuery (buildSelectQuery(tab));
            List<List<String>> rows = new ArrayList();
            List<String> flds = tabNCols.get(tab);

            while (rs.next ()) {
                ArrayList <String> results = new ArrayList <String> ();
                for(int i = 0; i < flds.size(); i++) {
                    String s = rs.getString( flds.get(i) );
                    results.add (s == null ? "" : s);
                }
                rows.add(results);
            }
            return rows;
        }
        catch (Exception e) {
                e.printStackTrace ();
                return null;
        }
    }
    public static void main (String args []) {
        try {
            JvxDBMetas j = new JvxDBMetas ("mysqlX");
            if (j.valid) {
                //j.getTablesAndViews(null);
                j.getTableMetaData();
                //j.getTableMetaData();
                j.close ();
            }
        } catch (Exception ex) {
            Logger.getLogger(JvxDBMetas.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
class JvxDbTypes {
    String name;
    String url;
}