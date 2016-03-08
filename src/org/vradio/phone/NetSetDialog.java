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
import android.widget.LinearLayout;

public class NetSetDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        builder.setMessage(R.string.nocon+"\n"+DownloadThread.m_errors)
               .setPositiveButton(R.string.nocon2, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
       				Intent intent=new Intent(Settings.ACTION_SETTINGS);
    				intent.addCategory(Intent.CATEGORY_LAUNCHER);           
    				
    				startActivity(intent);
    				Start.sgoset=true;Start.nsdialogopen=false;DownloadThread.m_errors="";
                   }
               })
               .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   Start.nsdialogopen=false;DownloadThread.m_errors="";
                   }
               });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}