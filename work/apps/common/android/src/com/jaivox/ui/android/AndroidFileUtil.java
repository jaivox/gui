package com.jaivox.ui.android;

import java.io.*;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import static android.os.Environment.getExternalStorageState;

// Adapted from PocketSphinx Android 

public class AndroidFileUtil {

    private static final String TAG = AndroidFileUtil.class.getSimpleName();
    public static final boolean quick = true; 

    public static File syncAssets(Context context, String dir) throws IOException {
        AssetManager assets = context.getAssets();
        Reader reader = new InputStreamReader(assets.open("assets.lst"));
        BufferedReader br = new BufferedReader(reader);
        File extDir = getApplicationDir(context);
        String path;

        while (null != (path = br.readLine())) {
        	if(dir != null && !path.startsWith(dir)) continue;
            File extFile = new File(extDir, path);
            String md5Path = path + ".md5";
            File extHash = new File(extDir, md5Path);
            extFile.getParentFile().mkdirs();
        	boolean cp = true;

            if(quick) {
            	BufferedReader hashreader = null;
            	BufferedReader exthashreader = null;
            	cp = false;
            	
	            try {
	                // Read asset hash.
	            	// Read file hash and compare.
	                hashreader = new BufferedReader(
	            			new InputStreamReader(assets.open(md5Path)) );
	            	exthashreader = new BufferedReader( 
	                		new InputStreamReader(new FileInputStream(extHash)) );
	                String hash = hashreader.readLine();
	                String exthash = hashreader.readLine();
	                if (!hash.equals(exthash)) cp =true;

	            } catch (IOException e) {
	            	// copy any way
	            	cp = true;
	            }
	            finally {
	            	if(hashreader != null) hashreader.close();
	            	if(exthashreader != null) exthashreader.close();
	            }
            }
            if(cp) {
            	Log.i(TAG, "copy " + path + " to " + extFile);
                copyStream(assets.open(path), new FileOutputStream(extFile));
                InputStream hashStream = assets.open(md5Path);
                copyStream(hashStream, new FileOutputStream(extHash));
            }
        }
        reader.close();
        
        return extDir;
    }

    /**
     * Returns external files directory for the application.
     *
     * Returns path to directory on external storage which is guaranteed to be
     * unique for the running application.
     *
     * @param content Application context
     *
     * @returns Path to application directory or null if it does not exists
     *
     * @see android.content.Context#getExternalFilesDir
     * @see android.os.Environment#getExternalStorageState
     */
    public static File getApplicationDir(Context context)
        throws IOException
    {
        File dir = context.getExternalFilesDir(null);
        if (null == dir)
            throw new IOException("cannot get external files dir, " +
                                  "external storage state is " +
                                  getExternalStorageState());
        return dir;
    }

    /**
     * Copies raw asset resources to external storage of the device.
     *
     * Copies raw asset resources to external storage of the device.
     * Implementation is borrowed from Apache Commons.
     */
    private static void copyStream(InputStream source, OutputStream dest)
        throws IOException
    {
        byte[] buffer = new byte[1024];
        int nread;

        while ((nread = source.read(buffer)) != -1) {
            if (nread == 0) {
                nread = source.read();
                if (nread < 0)
                    break;

                dest.write(nread);
                continue;
            }

            dest.write(buffer, 0, nread);
        }
    }
}

/* vim: set ts=4 sw=4: */
