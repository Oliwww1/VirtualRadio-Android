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
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class StartDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        

	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(inflater.inflate(R.layout.startdialog, null))
	    // Add action buttons
               .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   
                   }
               });
               
        // Create the AlertDialog object and return it
        return builder.create();
    }
}