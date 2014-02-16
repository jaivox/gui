package com.jaivox.ui.jvxdroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class QListFragment extends Fragment {
	
	public QListFragment() {
		super();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate( R.layout.fragment_jvx_qlist, container, false);
		AssetManager assets = rootView.getContext().getAssets();
		StringBuffer sb = new StringBuffer();
		Reader reader = null;
		try {
			reader = new InputStreamReader(assets.open("console/dialog.tree"));
			BufferedReader br = new BufferedReader(reader);
			String line = "";
			while (null != (line = br.readLine())) {
				sb.append(line).append('\n');
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        finally { 
        	if(reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
        }
		
        TextView txtResult = (TextView) rootView.findViewById(R.id.qlistTextView);
		txtResult.setText(sb);
		return rootView;
	}
}

