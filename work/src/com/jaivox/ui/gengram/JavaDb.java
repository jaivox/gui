/*
   Jaivox Application Generator (JAG) version 0.2 March 2014
   Copyright 2010-2014 by Bits and Pixels, Inc.

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

package com.jaivox.ui.gengram;

// simplified version to send a query and get results
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;

public class JavaDb {

	static String driver = "org.gjt.mm.mysql.Driver";	// mysql linux
	// static String driver = "com.mysql.jdbc.Driver";	// works for windows
	// static String driver = "org.postgresql.Driver";	// for postgres
	String protocol = "jdbc:mysql://";					// for mysql
	// String protocol = "jdbc:postgresql://";			// for postgres
	String user = "root";
	String password = "";
	String dsnstub = "localhost:3306/";
	Connection con;
	// String focusquery = "select * from sales";
	int ncols;
	boolean Valid = false;

	public JavaDb (String dbname) {
		try {
			Class.forName (driver);
			String dsn = dsnstub + dbname;
			String url = protocol + dsn;
			con = DriverManager.getConnection (url, user, password);
			Statement s = con.createStatement ();
			Valid = true;
		} catch (Exception e) {
			e.printStackTrace ();
			return;
		}
	}

	void Debug (String s) {
		System.out.println ("[javadb]" + s);
	}

	void close () {
		try {
			con.close ();
			System.out.println ("Database connection closed.");
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}

	void getClassInfo (String focusquery) {
		try {
			Statement st = con.createStatement ();
			ResultSet rs = st.executeQuery (focusquery);

			ResultSetMetaData rsMetaData = rs.getMetaData ();

			ncols = rsMetaData.getColumnCount ();
			System.out.println ("Schema: ncols = " + ncols);

			for (int i = 1; i <= ncols; i++) {
				String name = rsMetaData.getColumnName (i);
				System.out.println ("column " + i + ": " + name);
			}
		} catch (Exception e) {
			e.printStackTrace ();
		}
	}

	String[] execute (String query) {
		try {
			Statement st = con.createStatement ();
			ResultSet rs = st.executeQuery (query);
			ResultSetMetaData rsMetaData = rs.getMetaData ();
			int n = rsMetaData.getColumnCount ();
			int i = 0;
			ArrayList<String> results = new ArrayList<String> ();
			while (rs.next ()) {
				StringBuffer sb = new StringBuffer ();
				for (int j = 1; j <= n; j++) {
					String s = rs.getString (j);
					sb.append (s);
					if (j < n) {
						sb.append ("\t");
					}
				}
				String line = new String (sb);
				results.add (line);
				i++;
			}
			int sz = results.size ();
			if (sz == 0) {
				return null;
			}
			String values[] = results.toArray (new String[sz]);
			if (values == null) {
				return null;
			} else if (values.length > 0) {
				if (values[0] == null) {
					return null;
				}
			}
			return values;
		} catch (Exception e) {
			Debug ("Query: " + query);
			e.printStackTrace ();
			return null;
		}
	}

	public static void main (String args[]) {
		JavaDb j = new JavaDb (args[0]);
		if (j.Valid) {
			j.getClassInfo (args[1]);
			j.execute (args[1]);
			j.close ();
		}
	}
};
