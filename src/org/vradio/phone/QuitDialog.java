package org.vradio.phone;

	import org.vradio.R;

	import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

	public class QuitDialog extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        
	        builder.setMessage(R.string.quit1)
	               .setPositiveButton(R.string.quit2, new DialogInterface.OnClickListener() {
	                   @Override
					public void onClick(DialogInterface dialog, int id) {
	                	   if(!Start.getStart().startURLFlag) {
							Start.getStart();
							Start.getStart().startURL=Start.streamSelected;
						}
		        			Start.getStart();
							if(Start.streamSelected.startsWith(Start.getStart().getResources().getString(R.string.save_tx3)))Start.getStart().startURL="";
		        			Start.getStart().stopPlay(); Start.getStart().putSaved();
		        			Start.getStart();
							//if(Start.sad){
		        			 //Intent i = new Intent(Start.this, ad.class);
		        				//Start.getStart().adshowing=true;
		        				//Start.getStart().startActivity(Start.getStart().adi);	
		        				//try {
									//Start.getStart().audioad.loadAd();
								//} catch (Exception e) {}
		        			//}else{		     
			        			Start.getStart();
								if(Start.hts!=null){
								Start.hts.stop();
								Start.hts=null;}
			        			Start.getStart().finish();
		        			//}
	                   }
	               })
	               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	                   @Override
					public void onClick(DialogInterface dialog, int id) {
		           			
	                   }
	               })
	               .setNeutralButton(R.string.quit3, new DialogInterface.OnClickListener() {
	                   @Override
					public void onClick(DialogInterface dialog, int id) {
	                	   Start.getStart().startURL="";
	                	   Start.getStart().stopPlay(); Start.getStart().putSaved();
		        			Start.getStart();
							//if(Start.sad){
		        			 //Intent i = new Intent(Start.this, ad.class);
		        				//Start.getStart().adshowing=true;
		        				//try {
								//	Start.getStart().audioad.loadAd();
							//	} catch (Exception e) {}	
		        			//}else{
			        			Start.getStart();
								if(Start.hts!=null){
								Start.hts.stop();
								Start.hts=null;}
			        			Start.getStart().finish();
		        			//}
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }
	}