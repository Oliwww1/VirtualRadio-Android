package org.vradio.phone;

import org.vradio.R;

import com.giantrabbit.nagare.DownloadThread;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

public class CacheDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        

	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();
	    View layout = inflater.inflate(R.layout.cachedialog, null, false);
		
		Button b1=(Button) layout.findViewById(R.id.cachealways);
		b1.setOnClickListener(new OnClickListener(){public void onClick(View arg0) {
			Start.getStart().scache=false;
			Start.getStart().new DownloadFilesTask().execute(""); CacheDialog.this.dismiss();
		}});
		
		Button b2=(Button) layout.findViewById(R.id.cacheonce);
		b2.setOnClickListener(new OnClickListener(){public void onClick(View arg0) {
			Start.getStart().scache=true;
			Start.getStart().vronce=true;
			Start.getStart().new DownloadFilesTask().execute(""); 
			CacheDialog.this.dismiss();
		}});
		Button b3=(Button) layout.findViewById(R.id.cachenever);
		b3.setOnClickListener(new OnClickListener(){public void onClick(View arg0) {
			Start.getStart().scache=true; 
			
			Start.getStart().new DownloadFilesTask().execute("");CacheDialog.this.dismiss();
		}});    
		
        if(Start.getStart().sshowagain)((CheckBox) layout.findViewById(R.id.showagain)).setChecked(true);
        ((CheckBox) layout.findViewById(R.id.showagain)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Start.getStart().sshowagain=isChecked;
				Start.getStart().putSaved();
			}});
		
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(layout);
               
        // Create the AlertDialog object and return it
        return builder.create();
    }
}