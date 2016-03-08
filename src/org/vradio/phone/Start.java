package org.vradio.phone;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.UUID;
import java.util.Vector;

import org.teleal.cling.android.AndroidUpnpService;
import org.teleal.cling.android.AndroidUpnpServiceImpl;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.model.types.ServiceType;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.registry.RegistryListener;
import org.vradio.R;
import org.vradio.io.Xmlparser;
import org.vradio.server.Httpserver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.Vibrator;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.giantrabbit.nagare.DownloadThread;
import com.giantrabbit.nagare.INagareService;
import com.giantrabbit.nagare.NagareService;
import com.giantrabbit.nagare.ShoutcastFile;
//import com.obmqsfjfthy.AdController;
//import com.obmqsfjfthy.AdListener;
//import com.idrhlpbnwimdosrsvnw.*;

public class Start extends ListActivity implements iUiListener{	
	org.teleal.cling.model.meta.Service serviceip=null;
	org.teleal.cling.model.meta.Service servicedir=null;
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    public Context m_context;
    public Toast ttoast;
    public static final boolean debug=false;
    public static String volumeSelected="";
    public static String groupSelected="",gsel2="";
    public static String streamSelected="";
    public static String urlSelected;
	static String commandSelected;
	public static boolean xmlReady;
	static boolean selected;
	static boolean leave,memo,settingsready;
	public static boolean parsed,sek,shttp,sgoset,sfile,scache=true,sshowagain=true,nsdialogopen,swarnmob=true, ses_swarnmob;
	static Start strt;
	static int sstate=4,slpos,sbgcol=7,sostate,retryupnp;
	private static int iui_state=0;
	final static int STATE_SEARCH=1;
	final static int STATE_CAT=4;
	final static int STATE_SET=7;
	final static int STATE_STATIONS=5;
	final static int STATE_FAVS=6;
	final static int STATE_EDF=9;
	final static int STATE_REC=8;
	final static int STATE_CUT=10;
	final static int STATE_LIKES=11;
	public final static int event_buffer=12;
	public final static int event_stop=2;
	public final static int event_bitrate=6;
	public final static int event_memlo=9;
	public final static int event_nocon=11;
	public final static int event_icy=3;
	public final static int event_play=13;
	static String searchTerm="",searchTerm2="", streamInfo="Buffering...",bitrate="";
	public static INagareService m_nagare_service = null;	
	static final String[] s2 = null;
	static Xmlparser xmp = Xmlparser.getParser();
	public static Hashtable<String, Channel> searchResults, shoutResults, mHt, favht;
	public static Hashtable<String,Device> avtrans_dev;
	public static LinkedHashMap<String, Channel> likes;
	private static float seekfac=0.0f;
	private TextView clview;
	private static Handler update_handler;
	private String[] list_content;
	String[] mFiles;
	static View view, widget,pdeckel,pimib2;
	static TextView tex, tex2, tex3;
	LinearLayout pfield, wrap;
	private EditText et;
	boolean mRun,popen,likeonce, vronce,startURLFlag, startExtFlag,startonceFlag;
	String temptext="", temptext2="", mDelFile, startURL=null,supnpdev=null, mobname="", moburl="";
	Dialog alert, alert2, salert;
	ImageButton cat, ser, recb, my, set;
	ImageButton pbut;
	static ViewGroup main,widgetview,sets;
	String[] emti={""};
	Vibrator vib;
	Dialog currentdialog;
	static String edurl, edname, uuid, mmeta="";
	public static String actualIpAddress="";
	public static boolean ssave, sshake,ssensorwait,supnp,sad=true;
	int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	public static org.vradio.server.Httpserver hts;
	SeekBar skb;
	public int bgdrawid=0, bgdrawcol=R.color.bluegrey, currentad=0;
	AnimationDrawable frameAnimation;
	DisplayMetrics metrics;
	//AdController audioad;
	public org.teleal.cling.model.meta.Service avtrans;
	public URL avtrans_url;
	Drawable[] bg_drawables;
	Drawable[] buttons;
	Drawable[] speaker;
	Intent adi;
	public static int timeout=3000;
	boolean adshowing=false;
	@Override
	public void onResume(){
		super.onResume();
		Log.v("vradioplayer ","onResume "+sstate+" "+sgoset);	
		if(adshowing){
			finish();
			return;
		}
		if(sgoset){
			new DownloadFilesTask().execute(""); 
			sgoset=false;
		}
	}
	
	private DisplayMetrics getMetrics(){
		if(metrics==null){        metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);}
		return metrics;
	}
	private AndroidUpnpService upnpService;
	private RegistryListener registryListener = new BrowseRegistryListener();
	public class BrowseRegistryListener extends DefaultRegistryListener {

	    @Override
	    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
	        deviceAdded(device);
	    }

	    @Override
	    public void remoteDeviceDiscoveryFailed(Registry registry, final RemoteDevice device, final Exception ex) {
	        runOnUiThread(new Runnable() {
	            @Override
				public void run() {
	            	Log.v("Discovery failed of '" + device.getDisplayString() + "': " +
	                                (ex != null ? ex.toString() : "Couldn't retrieve device/service descriptors"),"xxxx");
	                        
	            }
	        });
	        deviceRemoved(device);
	    }

	    @Override
	    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
	        deviceAdded(device);
	    }

	    @Override
	    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
	        deviceRemoved(device);
	    }

	    @Override
	    public void localDeviceAdded(Registry registry, LocalDevice device) {
	        deviceAdded(device);
	    }

	    @Override
	    public void localDeviceRemoved(Registry registry, LocalDevice device) {
	        deviceRemoved(device);
	    }

	    public void deviceAdded(final Device device) {
	        runOnUiThread(new Runnable() {
	            @Override
				public void run() {
	            	if(device.findService(new ServiceType("schemas-upnp-org", "AVTransport"))!=null){
	            	try{	            	
		            	Log.v("...... dev added",device.getDetails().getFriendlyName());
		            	Log.v("...... dev icons",""+device.findIcons());
		            	URL url=((org.teleal.cling.model.meta.RemoteDevice)device).getIdentity().getDescriptorURL();
		        		Log.v(".dev","p:"+url.getPort()+" h:"+url.getHost());
		            	if(avtrans_dev==null)avtrans_dev=new Hashtable<String,Device>();
		            	avtrans_dev.put(device.getDetails().getFriendlyName(), device);
		            	if(device.getDetails().getFriendlyName().equals(supnpdev)){
		            		if(url!=null)avtrans_url=url;
		            		try {
								if(supnp && m_nagare_service!=null && m_nagare_service.state()>=1 && m_nagare_service.state()<=3)postAV("VRADIOSTREAMER"+ShoutcastFile.id+".mp3");
					        } catch (Exception e) {	Log.v("upnp on click ",""+e);}
		            	}
		            	if(sstate==STATE_SET && settingsready){
		            		m_handler.post(ref_settings);
		            	}
	            	}catch(Exception e){}
	            	}
	            }
	        });
	    }

	    public void deviceRemoved(final Device device) {
	        runOnUiThread(new Runnable() {
	            @Override
				public void run() {
	            	Log.v("...... dev removed",""+device.toString());
	            	if(device.findService(new ServiceType("schemas-upnp-org", "AVTransport"))!=null){
	            		try{	            	
	            	Log.v("...... dev removed",device.getDetails().getFriendlyName());
	            	
	            	if(avtrans_dev!=null)avtrans_dev.remove(device.getDetails().getFriendlyName());
	            	
	            	if(sstate==STATE_SET && settingsready){	            		
	            		m_handler.post(ref_settings);
	            	}
	            	}catch(Exception e){}
	            	}
	            }
	        });
	    }
	}
	private ServiceConnection serviceConnection = new ServiceConnection() {

	    @Override
		public void onServiceConnected(ComponentName className, IBinder service) {
	        upnpService = (AndroidUpnpService) service;

	        // Refresh the list with all known devices
	        
	        for (Device device : upnpService.getRegistry().getDevices()) {
	            ((BrowseRegistryListener) registryListener).deviceAdded(device);
	        }

	        // Getting ready for future device advertisements
	        upnpService.getRegistry().addListener(registryListener);

	        // Search asynchronously for all devices
	        upnpService.getControlPoint().search();
	    }

	    @Override
		public void onServiceDisconnected(ComponentName className) {
	        upnpService = null;
	    }
	};
	
	private ServiceConnection m_nagare_service_connection = new ServiceConnection()	{
		@Override
		public void onServiceConnected(ComponentName classname, IBinder service)		{
			m_nagare_service = INagareService.Stub.asInterface(service);
		}
		@Override
		public void onServiceDisconnected(ComponentName name)		{
			m_nagare_service = null;
		}	
	};
	public final Runnable m_stop2 = new Runnable()	{		@Override
	public void run(){
		Log.v(", , , , ,      m_stop2","mstop2");
		stopPlay();
		m_handler.post(m_overlay_refresh);
	}};
	public final Runnable m_stop = new Runnable()	{		@Override
	public void run(){
		Log.v("- - - -     m_stop","mstop");
		stopPlay();
		m_handler.post(m_netsetdialog);
		m_handler.post(m_overlay_refresh);
	}};
	public final Runnable m_finish = new Runnable()	{		@Override
	public void run(){
		Log.v("m_finish","");
		stopPlay(); 
		putSaved(); if(hts!=null){hts.stop();hts=null;}
		finish();
	}};
	private final Runnable m_popen = new Runnable()	{		@Override
	public void run(){
			popen=true;
			m_handler.postDelayed(m_pclose, 3000);
		}};
	private final Runnable m_pclose = new Runnable()		{			@Override
	public void run(){
				if(popen){Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.invplayerani);				  
     			wrap.startAnimation(lani);popen=false;}
			}};
	private final Runnable logodialog = new Runnable()	{		@Override
	public void run()		{//Log.v("m_startdialog","event ");
		final Dialog lalert = new Dialog(Start.this, android.R.style.Theme_Translucent_NoTitleBar);							
				LayoutInflater inflater = (LayoutInflater) m_context.getSystemService(LAYOUT_INFLATER_SERVICE);
	    		View layout = inflater.inflate(R.layout.logodialog, null, false);
	    		ImageButton ibu=(ImageButton) layout.findViewById(R.id.ibu);
	    		Drawable bid=getResources().getDrawable(R.drawable.vra_welcome);
	    		LinearLayout ibp=(LinearLayout)layout.findViewById(R.id.ibuparent);
	    		ibp.setPadding((getMetrics().widthPixels-bid.getIntrinsicWidth())/2, ( getMetrics().heightPixels -bid.getIntrinsicHeight())/2, (getMetrics().widthPixels-bid.getIntrinsicHeight())/2, (getMetrics().heightPixels -bid.getIntrinsicHeight())/2);
	    		ibu.setOnClickListener(new OnClickListener(){@Override
				public void onClick(View arg0) {
						if(lalert!=null)lalert.dismiss();
						
				}});
	    		lalert.addContentView(layout,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT));
	 	       	Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.logoani);
	 			layout.startAnimation(lani);	  
	 			lalert.show();
			}
		};
		private final Runnable m_cachedialog = new Runnable()	{		@Override
		public void run()		{
		    FragmentTransaction ft = getFragmentManager().beginTransaction();
		    Fragment prev = getFragmentManager().findFragmentByTag("cdialog");
		    if (prev != null) {
		        ft.remove(prev);
		    }
		    ft.addToBackStack(null);

		    // Create and show the dialog.
		    CacheDialog cd=new CacheDialog();
		    cd.show(ft, "cdialog");	
		}};
		
/*		private final Runnable m_cachedialog = new Runnable()	{		public void run()		{
			if(alert!=null){
				alert.cancel();alert=null;
			}	
			System.gc();
			final Dialog calert = new Dialog(Start.this, android.R.style.Theme_Translucent_NoTitleBar);						
			LayoutInflater inflater = (LayoutInflater) m_context.getSystemService(LAYOUT_INFLATER_SERVICE);
    		View layout = inflater.inflate(R.layout.cachedialog, null, false);
    		TextView tv=(TextView)layout.findViewById(R.id.cachetx);
    		Button b1=(Button) layout.findViewById(R.id.cachealways);
    		b1.setOnClickListener(new OnClickListener(){public void onClick(View arg0) {
    			if(calert!=null){
    				calert.cancel();
    			}	
					scache=false;
					new DownloadFilesTask().execute(""); 
			}});
    		Button b2=(Button) layout.findViewById(R.id.cacheonce);
    		b2.setOnClickListener(new OnClickListener(){public void onClick(View arg0) {
    			if(calert!=null){
    				calert.cancel();
    			}	scache=true;
					vronce=true;
					new DownloadFilesTask().execute(""); 
			}});
    		Button b3=(Button) layout.findViewById(R.id.cachenever);
    		b3.setOnClickListener(new OnClickListener(){public void onClick(View arg0) {
    			scache=true; 
    			if(calert!=null){
    				calert.cancel();
    			}	
    			new DownloadFilesTask().execute("");
			}});    
    		
            if(sshowagain)((CheckBox) layout.findViewById(R.id.showagain)).setChecked(true);
            ((CheckBox) layout.findViewById(R.id.showagain)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
    			@Override
    			public void onCheckedChanged(CompoundButton buttonView,
    					boolean isChecked) {
    				sshowagain=isChecked;
    				putSaved();
    			}});
    		
    		calert.addContentView(layout,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT));
 	       	Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.quiticker);
 			layout.startAnimation(lani);	  
 			calert.show();
 			
		}
	};	*/
/*	private final Runnable m_startdialog = new Runnable()	{		public void run()		{//Log.v("m_startdialog","event ");
			if(alert!=null){
				alert.cancel();alert=null;
			}		
			System.gc();	
			alert = new Dialog(Start.this, android.R.style.Theme_Translucent_NoTitleBar);						
			LayoutInflater inflater = (LayoutInflater) m_context.getSystemService(LAYOUT_INFLATER_SERVICE);
    		View layout = inflater.inflate(R.layout.startdialog, null, false);
    		//int l=(getMetrics().widthPixels-layout.getMeasuredWidth())/2;
    		//int h=(getMetrics().widthPixels-layout.getMeasuredHeight())/2;
    		//layout.setPadding(l, h, l, h);
    		TextView tv=(TextView)layout.findViewById(R.id.statx);
    		Button b1=(Button) layout.findViewById(R.id.stabu);
    		b1.setOnClickListener(new OnClickListener(){public void onClick(View arg0) {
					if(alert!=null)alert.dismiss();
			}});
    		alert.addContentView(layout,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT));
 	       	Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.quiticker);
 			layout.startAnimation(lani);	  
 			alert.show();
		}
	};	*/
	private final Runnable m_standartdialog = new Runnable()	{		@Override
	public void run()		{
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("sdialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    StartDialog sd=new StartDialog();
	    sd.show(ft, "sdialog");
	}};
	
	public String standartdialogtx;
/*	private final Runnable m_standartdialog = new Runnable()	{		public void run()		{//Log.v("m_standartdialog","run "+standartdialogtx);
		System.gc();
			final Dialog stalert = new Dialog(Start.this, android.R.style.Theme_Translucent_NoTitleBar);			
			Context mContext = getApplicationContext();
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
    		View layout = inflater.inflate(R.layout.startdialog, null, false);
       		//int l=(getMetrics().widthPixels-layout.getMeasuredWidth())/2;
    		//int h=(getMetrics().widthPixels-layout.getMeasuredHeight())/2;
    		//layout.setPadding(l, h, l, h);
    		TextView tv=(TextView)layout.findViewById(R.id.statx);
    		if(standartdialogtx!=null)tv.setText(standartdialogtx);
    		Button b1=(Button) layout.findViewById(R.id.stabu);
    		b1.setOnClickListener(new OnClickListener(){public void onClick(View arg0) {
					if(stalert!=null)stalert.dismiss();
			}});
    		stalert.addContentView(layout,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT));
 	       	Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.quiticker);
 			layout.startAnimation(lani);	  
 			stalert.show();
		}
	};	
	*/
	
	private final Runnable m_netsetdialog = new Runnable()	{		@Override
	public void run()		{//Log.v("m_standartdialog","run "+standartdialogtx);
		//if(nsdialogopen)return;
		//nsdialogopen=true;
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    NetSetDialog nsd=new NetSetDialog();
	    nsd.show(ft, "dialog");
		

	}
};	

private final Runnable m_mobdialog = new Runnable()	{		@Override
public void run()		{//Log.v("m_standartdialog","run "+standartdialogtx);
	
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    Fragment prev = getFragmentManager().findFragmentByTag("mobdialog");
    if (prev != null) {
        ft.remove(prev);
    }
    ft.addToBackStack(null);
    DialogFragment df=new DialogFragment(){
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        

		    // Get the layout inflater
		    LayoutInflater inflater = getActivity().getLayoutInflater();
		    View layout = inflater.inflate(R.layout.mobdialog, null, false);		
		    
	
	Button b1=(Button) layout.findViewById(R.id.mobstabu);
	b1.setOnClickListener(new OnClickListener(){@Override
	public void onClick(View arg0) {
			dismiss();
			ses_swarnmob=false;
			if(play(mobname,moburl)){
				Start.this.setOverlay(main); 
				mobname="";moburl="";
			}	
	    	
	    	
	}});
	Button b2=(Button) layout.findViewById(R.id.mobnetbu);
	b2.setOnClickListener(new OnClickListener(){@Override
	public void onClick(View arg0) {
			
			Intent intent=new Intent(Settings.ACTION_SETTINGS);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);           
			if(findViewById(R.id.nix)!=null && sstate==STATE_STATIONS){
        		((LinearLayout)findViewById(R.id.listparent)).removeView(findViewById(R.id.nix));
        	}
			//ComponentName cName = new ComponentName("com.android.phone","com.android.phone.Settings");
			//intent.setComponent(cName); 
			startActivity(intent);
			sgoset=true;
			dismiss();
	}});
	((CheckBox) layout.findViewById(R.id.mobshowagain)).setChecked(true);
    ((CheckBox) layout.findViewById(R.id.mobshowagain)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			swarnmob=isChecked;Start.this.putSaved();			
		}});
    	builder.setView(layout);
		return builder.create();
		}
	};
	df.show(ft, "mobdialog");
}};	

	private Drawable makeib(int idi){
		try {
			//Log.v("makeib","makeib");
			//Drawable id=getResources().getDrawable(idi);
			Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),idi);
			//Drawable bd=getResources().getDrawable(R.drawable.bg_glit_gold);
			Bitmap bitmap = Bitmap.createBitmap(60, 60,Bitmap.Config.ARGB_8888); 		
	        Paint paint = new Paint();
	        paint.setFilterBitmap(true);
	        RectF rectf = new RectF(0, 0, 60,60);
	        Canvas canvas = new Canvas(bitmap);
	        Path path = new Path();
	        path.addRect(rectf, Path.Direction.CW);
	        canvas.clipPath(path);
	        canvas.drawBitmap( bitmapOrg, new Rect(0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight()),new Rect(0, 0, 60, 60), paint);
			
			return new BitmapDrawable(bitmap);
		} catch (Exception e) {Log.v("makeib"," err "+e);}
		return null;
	}
	
	private Drawable scale_imi(int id, int w){
		try {
			//Log.v("scale_imi","w="+w);
			//Drawable id=getResources().getDrawable(idi);
			Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),id);
			int scaw=bitmapOrg.getWidth()*w/480;
			int scah=bitmapOrg.getHeight()*w/480;
			//Log.v("scale_imi","orgw="+bitmapOrg.getWidth()+" "+scaw);
			//Drawable bd=getResources().getDrawable(R.drawable.bg_glit_gold);
			Bitmap bitmap = Bitmap.createBitmap(scaw, scah,Bitmap.Config.ARGB_8888); 		
	        Paint paint = new Paint();
	        paint.setFilterBitmap(true);
	        RectF rectf = new RectF(0, 0, scaw,scah);
	        Canvas canvas = new Canvas(bitmap);
	        Path path = new Path();
	        path.addRect(rectf, Path.Direction.CW);
	        canvas.clipPath(path);
	        canvas.drawBitmap( bitmapOrg, new Rect(0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight()),new Rect(0, 0, scaw, scah), paint);
			
			return new BitmapDrawable(bitmap);
		} catch (Exception e) {Log.v("scale_imi"," err "+e);}
		return null;
	}
	private Drawable scale_imi(Bitmap bitmapOrg, int w){
		try {
			//Log.v("scale_imi2","w="+w);
			//Drawable id=getResources().getDrawable(idi);
			//Bitmap bitmapOrg=bmap.getBitmap();
			int scaw=bitmapOrg.getWidth()*w/480;
			int scah=bitmapOrg.getHeight()*w/480;
			//Log.v("scale_imi","orgw="+bitmapOrg.getWidth()+" "+scaw);
			//Drawable bd=getResources().getDrawable(R.drawable.bg_glit_gold);
			Bitmap bitmap = Bitmap.createBitmap(scaw, scah,Bitmap.Config.ARGB_8888); 		
	        Paint paint = new Paint();
	        paint.setFilterBitmap(true);
	        RectF rectf = new RectF(0, 0, scaw,scah);
	        Canvas canvas = new Canvas(bitmap);
	        Path path = new Path();
	        path.addRect(rectf, Path.Direction.CW);
	        canvas.clipPath(path);
	        canvas.drawBitmap( bitmapOrg, new Rect(0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight()),new Rect(0, 0, scaw, scah), paint);
			
			return new BitmapDrawable(bitmap);
		} catch (Exception e) {Log.v("scale_imi"," err "+e);}
		return null;
	}	
	private Drawable makeskin(){
	try {
		//Log.v("makeskin","makeskin");
		Drawable id=getResources().getDrawable(R.drawable.player_power);
		Drawable bid=getResources().getDrawable(R.drawable.player_left);
		Bitmap bitm = BitmapFactory.decodeResource(getResources(),R.drawable.player_field);
		Bitmap bitmapOrg = BitmapFactory.decodeResource(getResources(),bgdrawid);
		//Drawable bd=getResources().getDrawable(R.drawable.bg_glit_gold);
		//Log.v("<<<<<<<<<<<<vradioplayer","xxxxxxxxxxxx makeskin imiwidth="+id.getIntrinsicWidth()+" bg_glit_gold="+bid.getIntrinsicWidth()+" bow="+bitmapOrg.getWidth()+" bitm="+bitm.getWidth()+" sw="+bitm.getScaledWidth(10));		 
		//Log.v("makeskin ",""+getMetrics().density+" "+getMetrics().densityDpi+" "+getMetrics().widthPixels+" "+getMetrics().ydpi+" "+getMetrics().xdpi);
/*		 
 * Bitmap bitmap = Bitmap.createBitmap(bitm.getWidth(), bitm.getHeight(),Bitmap.Config.ARGB_8888); 		
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        RectF rectf = new RectF(0, 0, bitm.getWidth(), bitm.getHeight());
        Canvas canvas = new Canvas(bitmap);
        Path path = new Path();
        path.addRect(rectf, Path.Direction.CW);
        canvas.clipPath(path);
        canvas.drawBitmap( bitmapOrg, new Rect(30, 0, 450, 720),new Rect(0, 0, 450,720), paint);
*/
		int fac=getMetrics().widthPixels/480;
		Bitmap bitmap = bitm.copy(Bitmap.Config.ARGB_8888, true); //Bitmap.createBitmap(bitm); 		
		//bitmap.setDensity(bitm.getDensity());
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setDither(true);
        //RectF rectf = new RectF(bid.getIntrinsicWidth(), 0, id.getIntrinsicWidth()*3, id.getIntrinsicHeight());
        Canvas canvas = new Canvas(bitmap);
        //Path path = new Path();
        //path.addRect(rectf, Path.Direction.CW);
        //canvas.clipPath(path);
        //canvas.drawBitmap( bitmapOrg, new Rect(bid.getIntrinsicWidth(), 0, id.getIntrinsicWidth()*3, id.getIntrinsicHeight()),new Rect(0, 0, bitmapOrg.getWidth(), id.getIntrinsicHeight()), paint);	
        //canvas.drawBitmap( bitmapOrg,new Rect(30, 0, 366, 144),new Rect(0,0,335,143),paint);
        Matrix m=new Matrix();
        m.preTranslate(-40.0f*fac, -8.0f*fac);
        canvas.drawBitmap( bitmapOrg, m ,paint);
        //canvas.drawBitmap( bitm,null,new Rect(0,0,335,143),paint);
        canvas.drawBitmap( bitm,new Matrix(),paint);
        //return new BitmapDrawable(bitmap);
        return scale_imi(bitmap, getMetrics().widthPixels);
	} catch (Exception e) {Log.v("makeskin"," err "+e);}
	return null;
}
	private final Runnable m_runfiledel = new Runnable()	{		@Override
	public void run()		{
		
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("deldialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
	    DialogFragment df=new DialogFragment(){
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
		        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		        

			    // Get the layout inflater
			    LayoutInflater inflater = getActivity().getLayoutInflater();
			    View layout = inflater.inflate(R.layout.buydialog, null, false);			

				Button b1=(Button) layout.findViewById(R.id.butok);
				Button b2=(Button) layout.findViewById(R.id.butcanc);
				TextView tv=(TextView) layout.findViewById(R.id.buydialogtx);
				tv.setText(getResources().getString(R.string.save_del)+mDelFile);
				b1.setOnClickListener(new OnClickListener(){@Override
				public void onClick(View arg0) {
						
						File f=new File(Environment.getExternalStorageDirectory() + "/vradio/"+mDelFile);
				    	if(f.exists())f.delete();	
				    	m_handler.post(m_rec);
				    	dismiss();
				    	//setListAdapter(getRecAdapter(getFiles()));
					}});
				b2.setOnClickListener(new OnClickListener(){@Override
				public void onClick(View arg0) {
					dismiss();
				}});
				builder.setView(layout);
				return builder.create();
		}
	};	
	df.show(ft, "deldialog");
	}};
	private final Runnable m_runbuy = new Runnable()	{		@Override
	public void run()		{
		
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("buydialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);
	    DialogFragment df=new DialogFragment(){
			@Override
			public Dialog onCreateDialog(Bundle savedInstanceState) {
		        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		        

			    // Get the layout inflater
			    LayoutInflater inflater = getActivity().getLayoutInflater();
			    View layout = inflater.inflate(R.layout.buydialog, null, false);			

				Button b1=(Button) layout.findViewById(R.id.butok);
				Button b2=(Button) layout.findViewById(R.id.butcanc);
				TextView tv=(TextView) layout.findViewById(R.id.buydialogtx);
				tv.setText(getResources().getString(R.string.buytx1)+" "+streamInfo+" "+getResources().getString(R.string.buytx2));
				b1.setOnClickListener(new OnClickListener(){@Override
				public void onClick(View arg0) {
						
					Uri uri = Uri.parse("http://www.amazon.com/gp/search?ie=UTF8&keywords="+URLEncoder.encode(streamInfo)+"&tag=virtualradio-20&index=digital-music&linkCode=ur2&camp=1789&creative=9325");
	            	 Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	            	 startActivity(intent);
				    	dismiss();
				    	//setListAdapter(getRecAdapter(getFiles()));
					}});
				b2.setOnClickListener(new OnClickListener(){@Override
				public void onClick(View arg0) {
					dismiss();
				}});
				builder.setView(layout);
				return builder.create();
		}
	};	
	df.show(ft, "buydialog");
	}};	
	private final Runnable m_runbuy2 = new Runnable(){	@Override
	public void run()		{//Log.v("m_startdialog","event ");
			alert = new Dialog(Start.this, android.R.style.Theme_Translucent_NoTitleBar);			
			Context mContext = getApplicationContext();
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
    		View layout = inflater.inflate(R.layout.buydialog, null, false);
    		Button b1=(Button) layout.findViewById(R.id.butok);
    		Button b2=(Button) layout.findViewById(R.id.butcanc);
    		TextView tv=(TextView) layout.findViewById(R.id.buydialogtx);
    		tv.setText(getResources().getString(R.string.buytx1)+" "+streamInfo+" "+getResources().getString(R.string.buytx2));
    		b1.setOnClickListener(new OnClickListener(){@Override
			public void onClick(View arg0) {
					alert.dismiss();alert=null;
					Uri uri = Uri.parse("http://www.amazon.com/gp/search?ie=UTF8&keywords="+URLEncoder.encode(streamInfo)+"&tag=virtualradio-20&index=digital-music&linkCode=ur2&camp=1789&creative=9325");
	            	 Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	            	 startActivity(intent);
				}});
    		b2.setOnClickListener(new OnClickListener(){@Override
			public void onClick(View arg0) {
				alert.dismiss();alert=null;
			}});
    		alert.addContentView(layout,new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT));
 	       	Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.quiticker);
 			layout.startAnimation(lani);	  
 			alert.show();
		}
	};	
	private final Runnable ref_settings=new Runnable(){ @Override
	public void run() {
		if(sets==null)return;
		LinearLayout spl=((LinearLayout)sets.findViewById(R.id.speakers));
		spl.removeAllViews();
    	CheckBox tvab=new CheckBox(m_context);
    	//tv.setId(R.id.nix);
    	tvab.setChecked(supnp);
    	tvab.setTextColor(getResources().getColor(R.color.white));
    	tvab.setTextSize(18.0f);
    	//tvab.setPadding(40, 0, 0, 4);
    	//tv.setWidth(getMetrics().widthPixels*4/5);
    	//tv.setBackgroundColor(getResources().getColor(R.color.translucent_dark2));
    	tvab.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
   		tvab.setText(getResources().getString(R.string.upnp1));
		LinearLayout.LayoutParams lap=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT, 1.0f);
		tvab.setLayoutParams(lap);
		tvab.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				supnp=arg1;putSaved();m_handler.post(ref_settings);
		}});
		((LinearLayout.LayoutParams) tvab.getLayoutParams()).gravity = Gravity.CENTER; 
		//tv.setGravity(Gravity.LEFT);
		spl.addView(tvab);       	
		if(supnp){
          if(avtrans_dev!=null && avtrans_dev.size()>0){
        	
        	TextView tva=new TextView(m_context);
        	//tv.setId(R.id.nix);
        	tva.setTextColor(getResources().getColor(R.color.white));
        	tva.setTextSize(18.0f);
        	tva.setPadding(0, 0, 0, 4);
        	//tv.setWidth(getMetrics().widthPixels*4/5);
        	//tv.setBackgroundColor(getResources().getColor(R.color.translucent_dark2));
        	tva.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
       		tva.setText(getResources().getString(R.string.upnp2));
    		LinearLayout.LayoutParams l=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT, 1.0f);
    		tva.setLayoutParams(l);
    		((LinearLayout.LayoutParams) tva.getLayoutParams()).gravity = Gravity.CENTER; 
    		//tv.setGravity(Gravity.LEFT);
    		spl.addView(tva, 1);
    		LinearLayout lil=new LinearLayout(m_context);
    		lil.setOrientation(LinearLayout.VERTICAL);
    		lil.setId(R.id.lil);
    		spl.addView(lil, 2);
        	for(final Device d:avtrans_dev.values()){

        		LinearLayout ll=new LinearLayout(m_context);
        		
        		ll.setOrientation(LinearLayout.HORIZONTAL);
        		LayoutParams lp=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
        		ll.setLayoutParams(lp);
        		ll.setBackgroundColor(R.color.translucent_dark2);
            	TextView tv=new TextView(m_context);
            	//tv.setId(R.id.nix);
            	tv.setTextColor(getResources().getColor(R.color.white));
            	tv.setTextSize(20.0f);
            	tv.setPadding(10, 0, 10, 4);
            	tv.setWidth(getMetrics().widthPixels*4/5);
            	//tv.setBackgroundColor(R.color.translucent_dark2);
            	//tv.setBackgroundColor(getResources().getColor(R.color.translucent_dark2));
            	tv.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
           		tv.setText(d.getDetails().getFriendlyName());
        		LinearLayout.LayoutParams llp=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT, 1.0f);
        		tv.setLayoutParams(llp);
        		((LinearLayout.LayoutParams) tv.getLayoutParams()).gravity = Gravity.LEFT; 
        		tv.setGravity(Gravity.CENTER);
        		ll.addView(tv);
        		final ImageButton rb=new ImageButton(m_context);
        		tv.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						Log.v("upnp radio onclicked",""+arg0);
						if(avtrans_url==null){
							avtrans_url=((org.teleal.cling.model.meta.RemoteDevice)d).getIdentity().getDescriptorURL();
							//supnp=true;
							supnpdev=d.getDetails().getFriendlyName();
							putSaved();
					        try {
								if(m_nagare_service!=null && m_nagare_service.state()>=1 && m_nagare_service.state()<=3)postAV("VRADIOSTREAMER"+ShoutcastFile.id+".mp3");
					        } catch (Exception e) {	Log.v("upnp on click ",""+e);}
							//new getVol().execute("");
							rb.setBackgroundDrawable(speaker[1]);
							rb.invalidate();
							if(sad){
								//Intent i = new Intent(Start.this, ad.class);
						    	//startActivity(adi);
							}
						}else if(avtrans_url==((org.teleal.cling.model.meta.RemoteDevice)d).getIdentity().getDescriptorURL()){
							try {
								new postStop().execute(avtrans_url.toString());
							} catch (Exception e) {	Log.v(".... poststop ",""+e);}
							rb.setBackgroundDrawable(speaker[0]);		
							avtrans_url=null;supnpdev="";Start.this.putSaved();
						}
					}});
        		//RadioGroup rg=new RadioGroup(Start.this);
        		
        		LinearLayout.LayoutParams rl=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT );
        		 
        		rb.setBackgroundDrawable(speaker[0]);
        		tv.setWidth(getMetrics().widthPixels-rb.getMeasuredWidth()-20);
        		rb.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						Log.v("upnp radio onclicked",""+arg0);
						if(avtrans_url==null){
							avtrans_url=((org.teleal.cling.model.meta.RemoteDevice)d).getIdentity().getDescriptorURL();
							//supnp=true;
							supnpdev=d.getDetails().getFriendlyName();
							putSaved();
					        try {
								if(m_nagare_service!=null && m_nagare_service.state()>=1 && m_nagare_service.state()<=3)postAV("VRADIOSTREAMER"+ShoutcastFile.id+".mp3");
					        } catch (Exception e) {	Log.v("upnp on click ",""+e);}
							//new getVol().execute("");
							rb.setBackgroundDrawable(speaker[1]);
							/* if(sad){
								Intent i = new Intent(Start.this, ad.class);
						    	startActivity(i);
							} */
						}else if(avtrans_url==((org.teleal.cling.model.meta.RemoteDevice)d).getIdentity().getDescriptorURL()){
							try {
								new postStop().execute(avtrans_url.toString());
							} catch (Exception e) {	Log.v(".... poststop ",""+e);}
							rb.setBackgroundDrawable(speaker[0]);		
							avtrans_url=null;
							supnpdev="";Start.this.putSaved();
						}
					}});

        		
        		ll.addView(rb);
        		lil.addView(ll);
        		//rg.addView(rb);
        		try {
        			if(supnpdev.equals(d.getDetails().getFriendlyName())){
        				if(supnp)rb.setBackgroundDrawable(speaker[1]);
        				else rb.setBackgroundDrawable(speaker[0]);
        				//rb.setChecked(supnp);	
        				//if(supnp){
        				//LinearLayout lll=new LinearLayout(m_context);
        				//LinearLayout.LayoutParams rll=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT );
        				//lll.setLayoutParams(rll);
        					SeekBar sb= new SeekBar(m_context);
        				    sb.setMinimumHeight(10);
        				    
        				    sb.setPadding(10, 20, 10, 10);
        				    //sb.setThumb(null);
        				    sb.setMax(100);
        			        sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener (){
        						@Override
        						public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        							Log.v("volume change","arg1"+arg1+" "+arg2);
        							
        							if(arg2){
        			            		try {
        									new postVol().execute(""+arg1);
        								} catch (Exception e) {
        									e.printStackTrace();
        								}
        							}
        							}
        						@Override
        						public void onStartTrackingTouch(SeekBar seekBar) {}
        						@Override
        						public void onStopTrackingTouch(SeekBar seekBar) {}
        					});
        				    try {
								sb.setProgress(Integer.parseInt(volumeSelected));
							} catch (Exception e) {}
        				    //lll.addView(sb);
        					lil.addView(sb);
        				//}
        			}	
        		} catch (Exception e) {}
        		
        		
        		lil.invalidate();
        		//rb.setGravity(android.view.Gravity.RIGHT);
        		
        		//sets.addView(ll);
        	}
          }else{
        	TextView tva=new TextView(m_context);
        	//tv.setId(R.id.nix);
        	tva.setTextColor(getResources().getColor(R.color.white));
        	tva.setTextSize(18.0f);
        	tva.setPadding(0, 0, 0, 4);
        	//tv.setWidth(getMetrics().widthPixels*4/5);
        	//tv.setBackgroundColor(getResources().getColor(R.color.translucent_dark2));
        	tva.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
       		tva.setText(getResources().getString(R.string.upnp3));
    		LinearLayout.LayoutParams l=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT, 1.0f);
    		tva.setLayoutParams(l);
    		((LinearLayout.LayoutParams) tva.getLayoutParams()).gravity = Gravity.CENTER; 
    		tva.setGravity(Gravity.CENTER);
    		spl.addView(tva, 1);       	
        	
          }
	    }
        settingsready=true;
	}};
	
	private final Runnable m_settings=new Runnable(){ @Override
	public void run() {
		if(sets!=null){
			((LinearLayout)findViewById(R.id.listparent)).removeView(sets);sets=null;
		}
		LayoutInflater li = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        sets=(ViewGroup) li.inflate(R.layout.settings, null, false);
        Log.v("start m_settings",""+((LinearLayout)findViewById(R.id.listparent)).getChildCount());
        
       ((LinearLayout)findViewById(R.id.listparent)).addView(sets, 0); 
        //sets.requestLayout();
        setListAdapter(null);

        if(ssave)((CheckBox) sets.findViewById(R.id.savecb)).setChecked(true);
        ((CheckBox) sets.findViewById(R.id.savecb)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				ssave=isChecked;Start.this.putSaved();refPlayBut();
				try {
					if(!ssave)pimib2.setBackgroundDrawable(scale_imi(R.drawable.player_rec_on, getMetrics().widthPixels));
					else pimib2.setBackgroundDrawable(scale_imi(R.drawable.player_rec_off, getMetrics().widthPixels));
				} catch (NotFoundException e) {e.printStackTrace();}
			}});
        if(shttp)((CheckBox) sets.findViewById(R.id.servcb)).setChecked(true);
        if(!scache)((CheckBox) sets.findViewById(R.id.cachecb2)).setChecked(true);
        ((CheckBox) sets.findViewById(R.id.servcb)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				shttp=isChecked;
				putSaved();
				/*if(shttp)hts=new org.vradio.server.Httpserver(Environment.getExternalStorageDirectory().getAbsolutePath());
				else try {
						if(hts!=null)hts.stop();
					} catch (Exception e) {	}*/
			}});
        ((CheckBox) sets.findViewById(R.id.cachecb2)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				scache=!isChecked;
				putSaved();
				if(!scache)new DownloadFilesTask().execute("");
			}});
        ((CheckBox) sets.findViewById(R.id.setmocon)).setChecked(swarnmob);
        ((CheckBox) sets.findViewById(R.id.setmocon)).setOnCheckedChangeListener(new OnCheckedChangeListener(){
    		@Override
    		public void onCheckedChanged(CompoundButton buttonView,
    				boolean isChecked) {
    			swarnmob=isChecked;Start.this.putSaved();			
    		}});
        
        sstate=STATE_SET;
        setNaviButtons();
        if(!actualIpAddress.equals("0.0.0.0")){
        	((TextView)sets.findViewById(R.id.httx)).setText(getResources().getString(R.string.http_inf)+actualIpAddress+":"+Httpserver.port+"/vradio");
        }else{
        	((TextView)sets.findViewById(R.id.httx)).setText(getResources().getString(R.string.http_infb));
        }
        ImageButton ib1=(ImageButton)sets.findViewById(R.id.setskb1);
        ImageButton ib2=(ImageButton)sets.findViewById(R.id.setskb2);
        ImageButton ib3=(ImageButton)sets.findViewById(R.id.setskb3);
        ImageButton ib4=(ImageButton)sets.findViewById(R.id.setskb4);
        ImageButton ib8=(ImageButton)sets.findViewById(R.id.setskb8);
        ImageButton ib9=(ImageButton)sets.findViewById(R.id.setskb9);
        //ImageButton ib10=(ImageButton)sets.findViewById(R.id.setskb10);
        //ImageButton ib11=(ImageButton)sets.findViewById(R.id.setskb11);
        ImageButton ib12=(ImageButton)sets.findViewById(R.id.setskb12);
        ImageButton ib13=(ImageButton)sets.findViewById(R.id.setskb13);
        //ImageButton ib14=(ImageButton)sets.findViewById(R.id.setskb14);
        ImageButton ib15=(ImageButton)sets.findViewById(R.id.setskb15);
        Button ib5=(Button)sets.findViewById(R.id.setskb5);
        Button ib6=(Button)sets.findViewById(R.id.setskb6);
        Button ib7=(Button)sets.findViewById(R.id.setskb7);
        ib1.setBackgroundDrawable(bg_drawables[0]);
        ib2.setBackgroundDrawable(bg_drawables[1]);
        ib3.setBackgroundDrawable(bg_drawables[2]);
        ib4.setBackgroundDrawable(bg_drawables[3]);
        ib8.setBackgroundDrawable(bg_drawables[7]);
        ib9.setBackgroundDrawable(bg_drawables[8]);
        //ib10.setBackgroundDrawable(makeib(R.drawable.dotway_grn_1));
        //ib11.setBackgroundDrawable(makeib(R.drawable.train_blu_1));
        ib12.setBackgroundDrawable(bg_drawables[11]);
        ib13.setBackgroundDrawable(bg_drawables[12]);
        //ib14.setBackgroundDrawable(makeib(R.drawable.trope_red_1));
        ib15.setBackgroundDrawable(bg_drawables[14]);
        ib1.invalidate();ib2.invalidate();ib3.invalidate();ib4.invalidate();
        ib1.setOnLongClickListener(new View.OnLongClickListener(){	@Override
		public boolean onLongClick(View v){
        	sbgcol=1;setBg();putSaved();
			return false;}});
        ib2.setOnLongClickListener(new View.OnLongClickListener(){	@Override
		public boolean onLongClick(View v){
            sbgcol=2;setBg();putSaved();
    			return false;}});
        ib3.setOnLongClickListener(new View.OnLongClickListener(){	@Override
		public boolean onLongClick(View v){
        	sbgcol=3;setBg();putSaved();
    			return false;}});
        ib4.setOnLongClickListener(new View.OnLongClickListener(){	@Override
		public boolean onLongClick(View v){
        	sbgcol=4;setBg();putSaved();
    			return false;}});
        ib5.setOnLongClickListener(new View.OnLongClickListener(){	@Override
		public boolean onLongClick(View v){
        	sbgcol=5;setBg();putSaved();
			return false;}});        
    	ib6.setOnLongClickListener(new View.OnLongClickListener(){	@Override
		public boolean onLongClick(View v){
    		sbgcol=6;setBg();putSaved();
    		return false;}});
    	ib7.setOnLongClickListener(new View.OnLongClickListener(){	@Override
		public boolean onLongClick(View v){
    		sbgcol=7;setBg();putSaved();
    		return false;}});
    	ib8.setOnLongClickListener(new View.OnLongClickListener(){	@Override
		public boolean onLongClick(View v){
        	sbgcol=8;setBg();putSaved();
			return false;}});
        ib9.setOnLongClickListener(new View.OnLongClickListener(){	@Override
		public boolean onLongClick(View v){
            sbgcol=9;setBg();putSaved();
    			return false;}});
        /*ib10.setOnLongClickListener(new View.OnLongClickListener(){	public boolean onLongClick(View v){
        	sbgcol=10;setBg();putSaved();
    			return false;}});
        ib11.setOnLongClickListener(new View.OnLongClickListener(){	public boolean onLongClick(View v){
        	sbgcol=11;setBg();putSaved();
    			return false;}});*/
        ib12.setOnLongClickListener(new View.OnLongClickListener(){	@Override
		public boolean onLongClick(View v){
        	sbgcol=12;setBg();putSaved();
			return false;}});
        ib13.setOnLongClickListener(new View.OnLongClickListener(){	@Override
		public boolean onLongClick(View v){
            sbgcol=13;setBg();putSaved();
    			return false;}});
        /*ib14.setOnLongClickListener(new View.OnLongClickListener(){	public boolean onLongClick(View v){
        	sbgcol=14;setBg();putSaved();
    			return false;}});*/
        ib15.setOnLongClickListener(new View.OnLongClickListener(){	@Override
		public boolean onLongClick(View v){
        	sbgcol=15;setBg();putSaved();
    			return false;} } );
        
    	m_handler.post(ref_settings);
	}};
	private final Runnable m_postlike=new Runnable(){ @Override
	public void run() {
		try {
			putSaved();
			Log.v("vradioplayer","postlike url "+edurl);
			HttpURLConnection hc = (HttpURLConnection) new URL("http://ninj.ch/vservices/addvote.jsp?id="+URLEncoder.encode(uuid)+"&url="+URLEncoder.encode(edurl)+"&t="+URLEncoder.encode(edname)).openConnection();
			hc.setUseCaches(false);
			hc.setDoInput(true);
			hc.setRequestProperty("connection", "close");
			InputStream is = hc.getInputStream();
			StringBuffer sb=new StringBuffer();
			int i=is.read();
			while(i!=-1)i=is.read();
			
		} catch (Exception e) {Log.v("vradioplayer","postlike er "+e);}
	}};
	private final Runnable m_edifav=new Runnable(){ @Override
	public void run() {
	    try{
	    	if(findViewById(R.id.nix)!=null){
	    		((LinearLayout)findViewById(R.id.listparent)).removeView(findViewById(R.id.nix));
	    	}
	    }catch(Exception e){}
	    if(sstate==STATE_EDF)return;
	    sstate=STATE_EDF;
        LayoutInflater li = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        view=li.inflate(R.layout.customurl, null, false);
        final EditText et1=(EditText) view.findViewById(R.id.urltx);
        final EditText et2=(EditText) view.findViewById(R.id.titeltx);
        if(edurl!=null)et1.setText(edurl);
        else et1.setText("http://");
        if(edname!=null)et2.setText(edname);
        et1.setWidth(200);
        et2.setWidth(200);
        Button butok=(Button)view.findViewById(R.id.butok);
        Button butcanc=(Button)view.findViewById(R.id.butcanc);
        butok.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	Log.v("edifav click",""+et1.getText().toString());
            	if(et1.getText().toString().toLowerCase().startsWith("http://")){
    		    	//Db.getDb(Start.this).createFavorite(et2.getText().toString(), et1.getText().toString(),Start.this.getResources().getString(R.string.custurltxt));		    	
            		if(et2.getText().toString().equals(""))return;
            		String name=et2.getText().toString();
            		boolean test=true;String br="1";
    				if(name.endsWith(" (<=32kb)")){
    					test=false;br="0";
    					name=name.substring(0,name.indexOf(" (<=32kb)"));
    				}
    				try{
    					if(!mHt.get(name).getBitrate()){
    						br="0";test=false;
    					}
    				}catch(Exception er){}
            		
            		Channel c=new Channel(et2.getText().toString(),et1.getText().toString(),"Custom URL");
            		c.setBitrate(test);
    		    	favht.put(et2.getText().toString(),c);
    		    	putStreamProp(et2.getText().toString(), "", et1.getText().toString(), "1");
    		    	Log.v("edifav click2",""+et2.getText().toString());
	            	m_handler.post(m_favs);    
	            	try{((LinearLayout)findViewById(R.id.listparent)).removeView(view);}catch(Exception e){} 
	            	try{((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(main.getWindowToken(), 0);}catch(Exception erx){}
            	}
            }
        });
        butcanc.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
    	      	m_handler.post(m_favs);
    	      	try{((LinearLayout)findViewById(R.id.listparent)).removeView(view);}catch(Exception e){}   
    	      	try{((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(main.getWindowToken(), 0);}catch(Exception erx){}
            }
        });
        ((LinearLayout)findViewById(R.id.listparent)).addView(view, 0);  
        setListAdapter(null);
        //sstate=STATE_EDF;
        butok.requestFocus();
	}};

	private final Runnable m_ref_stations=new Runnable(){ @Override
	public void run() {       
        setTitle(Start.groupSelected);
        try {
			Log.v("m_ref_stations"," "+mHt.size());
		} catch (Exception e) {
			return;
		}
        Enumeration <Channel> en=mHt.elements();
        list_content=new String[mHt.size()];
        int cnt=0;Start.sstate=STATE_STATIONS;
        setNaviButtons();
        try{
        while(en.hasMoreElements()){
        	Channel c=en.nextElement();
        	if(c.getBitrate())list_content[cnt++]=c.getText();
        	else list_content[cnt++]=c.getText()+" (<=32kb)";
        }}catch(Exception ex){}
        //ListView lv = getListView();
        //lv.removeAllViews();
        ArrayAdapter mya=new MyAdapter(Start.this, R.layout.srow, list_content);       
        setListAdapter(mya);
        Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.ticker);
		getListView().startAnimation(lani);	
        //Log.v("vradioplayer","ar "+ar.getCount()+" "+ar.getItem(0));        
        cnt=mHt.size();       
        //lv.setTextFilterEnabled(true);
        
        TextView tv=new TextView(m_context);
    	tv.setId(R.id.nix);
    	tv.setTextColor(getResources().getColor(R.color.white));
    	tv.setText(Start.groupSelected+":");
    	tv.setTextSize(26);
    	tv.setPadding(0, 0, 0, 20);
    	tv.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
    	((LinearLayout)findViewById(R.id.listparent)).removeView(findViewById(R.id.nix));
    	((LinearLayout)findViewById(R.id.listparent)).addView(tv, 0);
        getListView().setDivider(null);
       
        try{
        	for(int i=0;i<cnt;i++){
        		LinearLayout lila=(LinearLayout)mya.getItem(i);
        		CheckBox cb=(CheckBox) lila.findViewById(R.id.scb);
        		cb.setChecked(true);
        		//Log.v("vradioplayer","cb "+cb);        	
        }}catch(Exception ex){//Log.v("vradioplayer","cb err"+ex);
        }
	}};
	private final Runnable m_ref_likes=new Runnable(){ @Override
	public void run() {       
        setTitle(getResources().getString(R.string.custurlcom7));
        //Log.v("stations oncreate","mHt "+mHt+" "+mHt.size());
        
        Iterator<Channel> en=likes.values().iterator();
        list_content=new String[likes.size()];
        int cnt=0;Start.sstate=STATE_LIKES;
        setNaviButtons();
        try{
        while(en.hasNext()){
        	Channel c=en.next();
        	if(c.getBitrate())list_content[cnt++]=c.getText();
        	else list_content[cnt++]=c.getText()+" (<=32kb)";
        }}catch(Exception ex){}
        ListView lv = getListView();
        ArrayAdapter mya=new MyAdapter(Start.this, R.layout.srow, list_content);       
        setListAdapter(mya);
        Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.ticker);
		getListView().startAnimation(lani);	
        //Log.v("vradioplayer","ar "+ar.getCount()+" "+ar.getItem(0));        
        cnt=likes.size();       
        //lv.setTextFilterEnabled(true);
        
        TextView tv=new TextView(m_context);
    	tv.setId(R.id.nix);
    	tv.setTextColor(getResources().getColor(R.color.white));
    	tv.setText(Start.groupSelected+":");
    	tv.setTextSize(26);
    	tv.setPadding(0, 0, 0, 20);
    	tv.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
    	((LinearLayout)findViewById(R.id.listparent)).addView(tv, 0);
        getListView().setDivider(null);
       
        try{
        	for(int i=0;i<cnt;i++){
        		LinearLayout lila=(LinearLayout)mya.getItem(i);
        		CheckBox cb=(CheckBox) lila.findViewById(R.id.scb);
        		cb.setChecked(true);
        		//Log.v("vradioplayer","cb "+cb);        	
        }}catch(Exception ex){//Log.v("vradioplayer","cb err"+ex);
        }
	}};	
	
	private final Runnable m_favs=new Runnable(){@Override
	public void run() {
        Start.sstate=STATE_FAVS;
        getFHT();

        setNaviButtons();
    	 Log.v("favolist","2 "+favht);
    Enumeration<Channel> en=favht.elements();
    list_content=new String[favht.size()];
    int cnt=0;
    try{
    while(en.hasMoreElements()){
    	Channel ch=en.nextElement();
    	if(ch.getBitrate())list_content[cnt++]=ch.getText();
    	else list_content[cnt++]=ch.getText()+" (<=32kb)";    	
    }}catch(Exception ex){Log.v("fetch all hts","err2="+ex);}
    //Log.v("favolist","3 ");
    try{
    	if(findViewById(R.id.nix)!=null){
    		((LinearLayout)findViewById(R.id.listparent)).removeView(findViewById(R.id.nix));
    	}
    }catch(Exception e){}
    try{((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(main.getWindowToken(), 0);}catch(Exception erx){}
    try{
        if(list_content.length==0){
        	setListAdapter(null);
        	TextView tv=new TextView(m_context);
        	tv.setId(R.id.nix);
        	tv.setTextColor(getResources().getColor(R.color.white));
        	tv.setTextSize(20.0f);
        	tv.setPadding(0, 0, 0, 4);
        	tv.setBackgroundColor(getResources().getColor(R.color.translucent_dark2));
        	tv.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
        	tv.setText(getResources().getString(R.string.favotx));
        	((LinearLayout)findViewById(R.id.listparent)).addView(tv, 0);
        	//getListView().setVisibility(View.GONE);   
        	tv.setOnLongClickListener(new OnLongClickListener(){@Override
			public boolean onLongClick(View v) {
        		edname="";edurl="http://";
            	m_handler.post(m_edifav);
        			return true;
        		}});
        }else{	        
            try{
            	TextView tv=new TextView(m_context);
            	tv.setId(R.id.nix);
            	tv.setTextColor(getResources().getColor(R.color.white));
            	tv.setBackgroundColor(getResources().getColor(R.color.translucent_dark2));
            	tv.setTextSize(20.0f);
            	tv.setPadding(0, 0, 0, 10);
            	tv.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
            	tv.setText(getResources().getString(R.string.custurlcom4));
            	((LinearLayout)findViewById(R.id.listparent)).addView(tv, 0);
            	//getListView().addFooterView(tv);
            	tv.setOnLongClickListener(new OnLongClickListener(){@Override
				public boolean onLongClick(View v) {
            		edname="";edurl="http://";
                	m_handler.post(m_edifav);
            			return true;
            		}});
                }catch(Exception e){}
	    setListAdapter(getFavAdapter(list_content));
	    Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.ticker);
		getListView().startAnimation(lani);	      
	    cnt=favht.size();
	    }
    }catch(Exception er){Log.v("vradioplayer","cb err1"+er);}
	}};
	
	
	private final Runnable showQuitDialog=new Runnable(){@Override
	public void run() {
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("qdialog");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    QuitDialog qd=new QuitDialog();
	    qd.show(ft, "qdialog");
}};
	
ArrayAdapter getFavAdapter(final String[] s){
		return new ArrayAdapter<String>(Start.this, R.layout.save_row, s ) {
	        @Override
			public View getView (final int position, View convertView, final ViewGroup parent){
	        	//Log.v("FavAdapter","getView "+s[position]);
	            LayoutInflater li = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
	            View lview= li.inflate(R.layout.srow, parent, false);
	            ImageButton ib=(ImageButton)lview.findViewById(R.id.scb);
	            //CheckBox cb=(CheckBox)lview.findViewById(R.id.scb);
	            TextView tv=(TextView)lview.findViewById(R.id.srowt);
	            tv.setText(list_content[position]);
	            tv.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
	            tv.setWidth(getMetrics().widthPixels/5*4);tv.invalidate();
	            ImageButton like=(ImageButton)lview.findViewById(R.id.like_on);
	            try{
		        	if(likes.size()>0){
		        		if(likes.get(list_content[position])!=null){
		        			like.setSelected(true);
		        			TextView tv2=(TextView)lview.findViewById(R.id.srowt2);
		        			tv2.setText(""+likes.get(list_content[position]).getT());
		        		}else{like.setSelected(false);}
		        	}
	            }catch(Exception err){}
	            tv.setOnLongClickListener(new View.OnLongClickListener(){
	            	@Override
	            	public boolean onLongClick(View v){
	            		Log.v("favolist","onLongClick "+list_content[position]);
	            		
	        		    FragmentTransaction ft = getFragmentManager().beginTransaction();
	        		    Fragment prev = getFragmentManager().findFragmentByTag("fdialog");
	        		    if (prev != null) {
	        		        ft.remove(prev);
	        		    }
	        		    ft.addToBackStack(null);

	        		    // Create and show the dialog.
	        		    
	        		   
	            		
	            		DialogFragment df = new DialogFragment(){
	            			@Override
							public Dialog onCreateDialog(Bundle savedInstanceState) {
	            		        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	            		        

	            			    // Get the layout inflater
	            			    LayoutInflater inflater = getActivity().getLayoutInflater();
	            			    View layout = inflater.inflate(R.layout.favdialog, null, false);
	            			    
	            		  		((Button)layout.findViewById(R.id.b1)).setOnClickListener(new OnClickListener(){@Override
								public void onClick(View v) {
	            		  			Log.v(".... fav b1 ","onClick ");
	            		   			try {
	            						String s=list_content[position];
	            						if(s.endsWith(" (<=32kb)"))s=s.substring(0,s.indexOf(" (<=32kb)"));
	            						//stopPlay();
	            						if(play(s, favht.get(s).getUrl())){
	            							Start.this.setOverlay(main); 
	            							if(alert!=null)alert.dismiss();alert=null;
	            							dismiss();
	            						}
	            					} catch (Exception e) {if(alert!=null)alert.dismiss();alert=null;}   	            			
	            		   		}});	            	
	            				((Button)layout.findViewById(R.id.b2)).setOnClickListener(new OnClickListener(){@Override
								public void onClick(View v) {
	            					try {
	            						edname=list_content[position];
	            						if(edname.endsWith(" (<=32kb)"))edname=edname.substring(0,edname.indexOf(" (<=32kb)"));
	            						edurl=favht.get(edname).getUrl();
	            						m_handler.post(m_edifav);
	            					} catch (Exception e) {}
	            					if(alert!=null)alert.dismiss();alert=null;
	            					dismiss();
	            				}});	
	            				((Button)layout.findViewById(R.id.b3)).setOnClickListener(new OnClickListener(){@Override
								public void onClick(View v) {
	            					try {
	            						String name=list_content[position];
	            						if(name.endsWith(" (<=32kb)"))name=name.substring(0,name.indexOf(" (<=32kb)"));
	            						delStreamProp(name);
	            						favht.remove(name);
	            						m_handler.post(m_favs);
	            					} catch (Exception e) {}
	            					if(alert!=null)alert.dismiss();alert=null;
	            					dismiss();
	            				}});	
	            				((Button)layout.findViewById(R.id.b4)).setOnClickListener(new OnClickListener(){@Override
								public void onClick(View v) {
	            					edname=list_content[position];
	            					if(edname.endsWith(" (<=32kb)"))edname=edname.substring(0,edname.indexOf(" (<=32kb)"));
	            					edurl=favht.get(edname).getUrl();
	            					if(alert!=null)alert.dismiss();alert=null;
	            					getToastS(Start.getStart().getResources().getString(R.string.custurlcom6)).show();
	            			    	//m_handler.post(m_postlike);
	            					new postLikes().execute("");
	            					dismiss();
	            				}});
	            			    builder.setView(layout);
	            		               
	            		        // Create the AlertDialog object and return it
	            		        return builder.create();
	            			}
	            		};
	            		df.show(ft, "fdialog");
						return true;	
	            	}});
	            		
	       		/*cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
	    			@Override
	    			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
	    				String name=((TextView)(((LinearLayout)arg0.getParent()).findViewById(R.id.srowt))).getText().toString();
	    				Log.v("stations","onCheckedChange"+name+" "+mHt.get(name).getUrl());
	    				if(arg1){
	    					Db.getDb(Start.this).createFavorite(name, mHt.get(name).getUrl(), mHt.get(name).getParent());
	    					favht.put(name, new Channel(name,mHt.get(name).getUrl(),"Custom URL"));
	    					Toast.makeText(getApplicationContext(), Start.getStart().getResources().getString(R.string.addfavorites) , Toast.LENGTH_SHORT).show();
	    				}else{
	    					Db.getDb(Start.this).deleteFavorite(name);
	    					favht.remove(name);
	    					Toast.makeText(getApplicationContext(), Start.getStart().getResources().getString(R.string.remfavorites) , Toast.LENGTH_SHORT).show();
	    				}
	    			}} );*/
	           ib.setSelected(true);
	       		ib.setOnClickListener(new OnClickListener(){

	    			@Override
	    			public void onClick(View v) {
	    				String name=((TextView)(((LinearLayout)v.getParent()).findViewById(R.id.srowt))).getText().toString();
	    				String br="1";boolean test=true;
	    				if(name.endsWith(" (<=32kb)")){
	    					br="0";
	    					name=name.substring(0,name.indexOf(" (<=32kb)"));
	    				}
	    				if(v.isSelected()){
	    					v.setSelected(false);
	    					delStreamProp(name);
	    					favht.remove(name);
	    					getToastS(Start.getStart().getResources().getString(R.string.remfavorites)).show();
	    					//Toast.makeText(getApplicationContext(), Start.getStart().getResources().getString(R.string.remfavorites) , Toast.LENGTH_SHORT).show();
	    				}else{
	    					v.setSelected(true);
	    					putStreamProp(name, "", mHt.get(name).getUrl(), br);
	    					//Db.getDb(Start.this).createFavorite(name, mHt.get(name).getUrl(), mHt.get(name).getParent());
	    					Channel ch=new Channel(name,mHt.get(name).getUrl(),"Custom URL");
	    					ch.setBitrate(test);
	    					favht.put(name, ch);
	    					getToastS(Start.getStart().getResources().getString(R.string.addfavorites)).show();
	    					//Toast.makeText(getApplicationContext(), Start.getStart().getResources().getString(R.string.addfavorites) , Toast.LENGTH_SHORT).show();
	    				}
	    		}} );
	            return lview;
	        }
};
	}	
	private final Runnable m_search=new Runnable(){ @Override
	public void run() {		
		if(findViewById(R.id.nix)!=null){
       		//((LinearLayout)findViewById(R.id.listparent)).removeAllViews();
       	}
        setTitle(getResources().getString(R.string.save));
        //Log.v("stations oncreate","mHt "+mHt+" "+mHt.size());
        et= new EditText(Start.this.getApplicationContext()); 
        sstate=STATE_SEARCH;
        setNaviButtons();
        ((LinearLayout)findViewById(R.id.listparent)).addView(et, 0);
        et.setBackgroundColor(getResources().getColor(R.color.translucent_dark2));
        et.setTextColor(getResources().getColor(R.color.white));
        et.setText(Start.searchTerm);
        et.setImeOptions(EditorInfo.IME_ACTION_UNSPECIFIED);
        et.requestFocus();
        et.setHint(R.string.searchtext);
        et.setHintTextColor(getResources().getColor(R.color.white));
        //et.setPadding(12, 12, 12, 12);
        //et.set
        mRun=true;
        new SearchTask().execute("");
        //new searchShoutTask().execute("");
        String[] ts=Start.searchS();
        if(ts==null){
        	setListAdapter(null);
        }else {
        	setListAdapter(new MyAdapter(Start.this, R.layout.srow, ts));
        }
        InputMethodManager imm = (InputMethodManager) Start.this
        .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
        Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.ticker);
		getListView().startAnimation(lani);	       
	}};

	ArrayAdapter getRecAdapter(final String[] s){
		return new ArrayAdapter<String>(Start.this, R.layout.save_row, s ) {
		    @Override
			public View getView (int position, View convertView, ViewGroup parent){
		    	//Log.v("start.rec.Adapter","getView "+position);
		        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        View vview= li.inflate(R.layout.save_row, parent, false);			        
		        final TextView tv=(TextView)vview.findViewById(R.id.save_row_text1);
		        tv.setText(mFiles[position]);
		        tv.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
		        
		        if(getMetrics().widthPixels==800){
		        	Log.v("..,,..- recadapter w00800","setting w to "+getMetrics().widthPixels/6*5);
		        	tv.setWidth(getMetrics().widthPixels/5*4);
		        	tv.invalidate();
		        }else{
		        	tv.setWidth(getMetrics().widthPixels/4*3);tv.invalidate();
		        }
		        TextView tv2=(TextView)vview.findViewById(R.id.save_row_text2);
		        try {
		        	long tl=(new File(Environment.getExternalStorageDirectory() + "/vradio/"+mFiles[position]).length()/1024);
					if(m_nagare_service!=null && m_nagare_service.file_name()!=null && m_nagare_service.file_name().startsWith(mFiles[position])){
						tv2.setText(getResources().getString(R.string.menu_play));
						vview.findViewById(R.id.save_del).setVisibility(View.INVISIBLE);
						tv2.setTextColor(Color.WHITE);
					}else{
					tv2.setText(""+tl+" KB");
					Button buton=(Button)vview.findViewById(R.id.save_del);
					buton.setOnClickListener(new View.OnClickListener() {
					    @Override
						public void onClick(View v) {
					    	mDelFile=((TextView)((View)v.getParent()).findViewById(R.id.save_row_text1)).getText().toString();	
					    	
					    	m_handler.post(m_runfiledel);
					    	Log.v("save click","del file= "+ mDelFile);}
					}); 	
						       
		        tv.setOnLongClickListener(new View.OnLongClickListener(){
	            	@Override
	            	public boolean onLongClick(View v){ 
		        				Log.v("save","onClick "+v);
		        				//stopPlay();
		        				FragmentTransaction ft = getFragmentManager().beginTransaction();
			        		    Fragment prev = getFragmentManager().findFragmentByTag("sdialog");
			        		    if (prev != null) {
			        		        ft.remove(prev);
			        		    }
			        		    ft.addToBackStack(null);

		        				DialogFragment df=new DialogFragment(){
		        					@Override
									public Dialog onCreateDialog(Bundle savedInstanceState) {
			            		        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			            		        

			            			    // Get the layout inflater
			            			    LayoutInflater inflater = getActivity().getLayoutInflater();
			            			    View layout = inflater.inflate(R.layout.savedialog, null, false);			            		
					            		
				   	            		((Button)layout.findViewById(R.id.sadib2)).setOnClickListener(new OnClickListener(){@Override
										public void onClick(View v) {
				   	            			Log.v("savedialog onclick","play "+((TextView) v).getText()+" "+tv.getText());
				   	            			stopPlay();
				   	            			//hts=new org.vradio.server.Httpserver(Environment.getExternalStorageDirectory().getAbsolutePath());
					        				try {
					        					try {
													Xmlparser.prepareRecLib(tv.getText().toString()+".bin");
												} catch (Exception e) {
													Log.v("save","onClick bin err"+e);
												}
					        					Start.m_nagare_service.play(Environment.getExternalStorageDirectory() + "/vradio/"+tv.getText());
												Start.streamSelected=Start.this.getResources().getString(R.string.save_tx3)+"\nDuration: "+(duration()/60000);
												Start.urlSelected="";bitrate=getBitrate(tv.getText().toString());
												String meta=Xmlparser.getRecString(1L);
												if(meta!=null)Start.streamInfo=meta;
												else Start.streamInfo=(String) tv.getText();
												Start.groupSelected=Start.this.getResources().getString(R.string.save_tx3);
												Start.selected=true;
												Start.this.setOverlay(main,true);	
												m_handler.postDelayed(m_seek_refresh, 100);
					        				} catch (Exception e) {Log.v("save","onClick rerr"+e);
												// TODO Auto-generated catch block
												//e.printStackTrace();
											}
				        						if(alert!=null)alert.dismiss();alert=null;
				        						dismiss();
				        					}});	            	
					            		((Button)layout.findViewById(R.id.sadib1)).setOnClickListener(new OnClickListener(){@Override
										public void onClick(View v) {
					            			stopPlay();
					        				try {										
					        					Intent _in=new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.parse(Environment.getExternalStorageDirectory() + "/vradio/"+tv.getText()), "audio/mp3");
					        					//Intent _in=new Intent("content://org.vradio.phone.mp3provider/"+((TextView) view).getText()); 
					        					startActivity(_in);
					        				} catch (Exception e) {Log.v("save","onClick rerr"+e);
												// TODO Auto-generated catch block
												e.printStackTrace();
											}if(alert!=null)alert.dismiss();alert=null;
											dismiss();
					            		}});	
					            		/*((Button)layout.findViewById(R.id.sadib3)).setOnClickListener(new OnClickListener(){public void onClick(View v) {
					            			stopPlay();
					        				try {	
					        					Start.streamInfo=(String)tv.getText();
					        					//m_handler.post(m_cut);
					        			    	Intent i = new Intent(Start.this, cutter.class);
					        			    	startActivityForResult(i, 0);				        					
					        				} catch (Exception e) {Log.v("save","onClick rerr"+e);
												// TODO Auto-generated catch block
												e.printStackTrace();
											}if(alert!=null)alert.dismiss();alert=null;
					            		}});*/
					            		((Button)layout.findViewById(R.id.sadib4)).setOnClickListener(new OnClickListener(){@Override
										public void onClick(View v) {
					            			stopPlay();
					            			//hts=new org.vradio.server.Httpserver(Environment.getExternalStorageDirectory().getAbsolutePath());
					            			Start.streamInfo=(String)tv.getText();
					            			//m_handler.post(m_cut);
					            			//upnpService.getControlPoint().search();
					            			//Log.v(".... setavtranspuri ",""+tv.getText()+" "+avtrans.getServiceId());
					            			//Log.v(".... setavtranspuri ",""+tv.getText()+" "+avtrans.getAction("GetDeviceCapabilities"));
					            			
					            			postAV(streamInfo);
					            			dismiss();	            			
					            			if(alert!=null)alert.dismiss();alert=null;
					            		}});	
					            	       Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.ticker);
					            			layout.startAnimation(lani);
					            			builder.setView(layout);
			            		               
				            		        // Create the AlertDialog object and return it
				            		        return builder.create();
					            			
		        				}};
			            		
	  
			            			df.show(ft, "sdialog");
			            		return false;
		                      }
		        		}
		        
		        		);
		        
					}
				} catch (Exception e1) {e1.printStackTrace();}	
		        return vview;} };
	}
	public void postAV(String s){
		Log.v(".... postAV ",""+s+ " "+avtrans_url);
		if(!supnp)return;
		if(avtrans_url==null)return;
		try {
			new postAVTransportUri().execute(s);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		Log.v(".... setavtranspuri ",""+e);
	}
	}
	class postAVTransportUri extends AsyncTask<String, Integer, String> {
		
	    @Override
		protected String doInBackground(String...params ) {
	    	Log.v("postAVTransportUri","in doInBackground "+params[0]+" "+avtrans_url);
			if(!supnp)return "";
			if(avtrans_url==null)return "";
	    	try{
				int port=avtrans_url.getPort();
				if(port==-1)port=80;
				Socket socket=new Socket(avtrans_url.getHost(), port);
				OutputStream os=socket.getOutputStream();
				StringBuffer sb=new StringBuffer();
				sb.append("POST /AVTransport/ctrl HTTP/1.1\r\n");
				sb.append("HOST: "+avtrans_url.getHost()+"\r\n");
				sb.append("SOAPACTION: \"urn:schemas-upnp-org:service:AVTransport:1#SetAVTransportURI\"\r\n");
				sb.append("CONTENT-TYPE: text/xml; charset=\"utf-8\"\r\n");
				
				StringBuffer sb2=new StringBuffer();
				sb2.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");				
				sb2.append("<s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n");
				sb2.append("<s:Body>\r\n");
				sb2.append("<u:SetAVTransportURI xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\">\r\n");
				sb2.append("<InstanceID>0</InstanceID>\r\n");
				sb2.append("<CurrentURI>http://"+actualIpAddress+":"+Httpserver.port+"/vradio/"+params[0]+"</CurrentURI>\r\n");
				sb2.append("<CurrentURIMetaData />\r\n");
				sb2.append("</u:SetAVTransportURI>\r\n");
				sb2.append("</s:Body>\r\n");
				sb2.append("</s:Envelope>\r\n");
				byte[] b2=sb2.toString().getBytes("UTF-8");
				sb.append("Content-Length: "+b2.length+"\r\n\r\n");

				os.write(sb.toString().getBytes());
				os.write(b2);
				InputStream is=socket.getInputStream();
				InputStreamReader isr=new InputStreamReader(is);
				StringBuffer sb3=new StringBuffer();
				char b;int cnt=0;
				while(cnt<100 && (b=(char)isr.read() )!=-1){
					//Log.v("POST /AVTransport",""+sb3.toString());
					sb3.append(b);cnt++;
				}
				Log.v("POST /AVTransport",""+sb3.toString());	    	
				
				socket.close();
	    	}catch(Exception err){
	    		Log.v("POST /AVTransport",""+err);
	    		return "no";}
	    	try {
				m_handler.postDelayed(m_play, 800);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return "ok";
	    }

	    @Override
		protected void onProgressUpdate(Integer... progress) {
	        
	    }

	    @Override
		protected void onPostExecute(String result) {
//	    	Log.v("loadlikes onPostEx","1 "+result);
	    }
	}
	class postStop extends AsyncTask<String, Integer, String>{
		@Override
		protected String doInBackground(String... params) {
	    	Log.v("postStop","in doInBackground "+params[0]);
	    	try{
	    		URL au=new URL(params[0]);
				int port=au.getPort();
				if(port==-1)port=80;
				Socket socket=new Socket(au.getHost(), port);					
				OutputStream os=socket.getOutputStream();
				StringBuffer sb=new StringBuffer();
				sb.append("POST /AVTransport/ctrl HTTP/1.1\r\n");
				sb.append("HOST: "+au.getHost()+"\r\n");
				sb.append("SOAPACTION: \"urn:schemas-upnp-org:service:AVTransport:1#Stop\"\r\n");
				sb.append("CONTENT-TYPE: text/xml; charset=\"utf-8\"\r\n");
				//avtrans_url=null;
				StringBuffer sb2=new StringBuffer();
				sb2.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");				
				sb2.append("<s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n");
				sb2.append("<s:Body>\r\n");
				sb2.append("<u:Stop xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\">\r\n");
				sb2.append("<InstanceID>0</InstanceID>\r\n");
				sb2.append("</u:Stop>\r\n");
				sb2.append("</s:Body>\r\n");
				sb2.append("</s:Envelope>\r\n");
				byte[] b2=sb2.toString().getBytes("UTF-8");
				sb.append("Content-Length: "+b2.length+"\r\n\r\n");
				os.write(sb.toString().getBytes());
				os.write(b2);
				InputStream is=socket.getInputStream();
				InputStreamReader isr=new InputStreamReader(is);
				StringBuffer sb3=new StringBuffer();
				char b;int cnt=0;
				while(cnt<100 && (b=(char)isr.read() )!=-1){
					//Log.v("POST /AVTransport",""+sb3.toString());
					sb3.append(b);cnt++;
				}
				Log.v("POST stop /AVTransport",""+sb3.toString());
				socket.close();
				
	    	}catch(Exception err){
	    		Log.v("POST /stop err",""+err);
	    		return "no";}
	        return "ok";
		}
	}
	public Runnable m_play=new Runnable(){@Override
	public void run(){
		try {
			new postPlay().execute("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	};};
	class postPlay extends AsyncTask<String, Integer, String> {
		
	    @Override
		protected String doInBackground(String...params ) {
	    	Log.v("postPlay","in doInBackground ");
	    	try{
				int port=avtrans_url.getPort();
				if(port==-1)port=80;
				Socket socket=new Socket(avtrans_url.getHost(), port);
				OutputStream os=socket.getOutputStream();
				StringBuffer sb=new StringBuffer();
				
				sb.append("POST /AVTransport/ctrl HTTP/1.1\r\n");
				sb.append("HOST: "+avtrans_url.getHost()+"\r\n");
				sb.append("SOAPACTION: \"urn:schemas-upnp-org:service:AVTransport:1#Play\"\r\n");
				sb.append("CONTENT-TYPE: text/xml; charset=\"utf-8\"\r\n");
				
				StringBuffer sb2=new StringBuffer();
				sb2.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");			
				sb2.append("<s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n");
				sb2.append("<s:Body>\r\n");
				sb2.append("<u:Play xmlns:u=\"urn:schemas-upnp-org:service:AVTransport:1\">\r\n");
				sb2.append("<InstanceID>0</InstanceID>\r\n");
				sb2.append("<Speed>1</Speed>\r\n");
				sb2.append("</u:Play>\r\n");
				sb2.append("</s:Body>\r\n");
				sb2.append("</s:Envelope>\r\n");
				byte[] b2=sb2.toString().getBytes("UTF-8");
				sb.append("Content-Length: "+b2.length+"\r\n\r\n");

				os.write(sb.toString().getBytes());
				os.write(b2);
				InputStream is=socket.getInputStream();
				InputStreamReader isr=new InputStreamReader(is);
				StringBuffer sb3=new StringBuffer();
				char b;int cnt=0;
				while(cnt<100 && (b=(char)isr.read() )!=-1){
					//Log.v("POST /AVTransport",""+sb3.toString());
					sb3.append(b);cnt++;

				}
				if(!sb3.toString().startsWith("HTTP/1.1 200 OK")){
					Log.v("POST play not 200",""+sb3.toString());
					if(retryupnp<10)m_handler.postDelayed(m_play, 1000);
					retryupnp++;
				}
				Log.v("POSTplay",""+sb3.toString());	
				socket.close();
	    	}catch(Exception err){
	    		Log.v("POSTplay err",""+err);
	    		return "no";}
	    	try {
				new getVol().execute("");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return "ok";
	    }

	    @Override
		protected void onProgressUpdate(Integer... progress) {
	        
	    }

	    @Override
		protected void onPostExecute(String result) {
//	    	Log.v("loadlikes onPostEx","1 "+result);
	        	
	    }
	}
	


class getVol extends AsyncTask<String, Integer, String> {
					
				    @Override
					protected String doInBackground(String...params ) {
				    	Log.v("getVol",""+params[0]);
				    	String ret="";
				    	String cv="<CurrentVolume>";
				    	String cv2="</CurrentVolume>";
				    	try{
							int port=avtrans_url.getPort();
							if(port==-1)port=80;
							Socket socket=new Socket(avtrans_url.getHost(), port);
							OutputStream os=socket.getOutputStream();
							StringBuffer sb=new StringBuffer();

							sb.append("POST /RenderingControl/ctrl HTTP/1.1\r\n");
							sb.append("HOST: "+avtrans_url.getHost()+"\r\n");
							sb.append("SOAPACTION: \"urn:schemas-upnp-org:service:RenderingControl:1#GetVolume\"\r\n");
							sb.append("CONTENT-TYPE: text/xml; charset=\"utf-8\"\r\n");
							StringBuffer sb2=new StringBuffer();
							sb2.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");			
							sb2.append("<s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n");
							sb2.append("<s:Body>\r\n");
							sb2.append("<u:GetVolume xmlns:u=\"urn:schemas-upnp-org:service:RenderingControl:1\">\r\n");
							sb2.append("<InstanceID>0</InstanceID>\r\n");
							sb2.append("<Channel>Master</Channel>");
							sb2.append("</u:GetVolume>\r\n");
							sb2.append("</s:Body>\r\n");
							sb2.append("</s:Envelope>\r\n");
							byte[] b2=sb2.toString().getBytes("UTF-8");
							sb.append("Content-Length: "+b2.length+"\r\n\r\n");

							os.write(sb.toString().getBytes());
							os.write(b2);
							InputStream is=socket.getInputStream();
							InputStreamReader isr=new InputStreamReader(is);
							StringBuffer sb3=new StringBuffer();
							char b;int cnt=0;
							while(cnt<500 && (b=(char)isr.read() )!=-1){
								//Log.v("POST /AVTransport",""+sb3.toString());
								sb3.append(b);cnt++;
							}
							Log.v("start getVolume",""+sb3.toString());	
							socket.close();
							ret=sb3.toString().substring(sb3.toString().indexOf(cv)+15,sb3.toString().indexOf(cv2));
							volumeSelected=ret;
							putSaved();
			            	if(sstate==STATE_SET && sets!=null){	            		
			            		m_handler.post(ref_settings);
			            	}
							Log.v("start /getVolume",""+ret);
				    	}catch(Exception err){
				    		Log.v("start /getVolume",""+err);
				    		
				    		return "no";}
				        return ret;
				    }

				    @Override
					protected void onProgressUpdate(Integer... progress) {
				        
				    }

				    @Override
					protected void onPostExecute(String result) {
//				    	Log.v("loadlikes onPostEx","1 "+result);
				        	
				    }
				}	
				
	class postVol extends AsyncTask<String, Integer, String> {
		
	    @Override
		protected String doInBackground(String...params ) {
	    	Log.v("postVol",""+params[0]);
	    	try{
				int port=avtrans_url.getPort();
				if(port==-1)port=80;
				Socket socket=new Socket(avtrans_url.getHost(), port);
				OutputStream os=socket.getOutputStream();
				StringBuffer sb=new StringBuffer();
				
				sb.append("POST /RenderingControl/ctrl HTTP/1.1\r\n");
				sb.append("HOST: "+avtrans_url.getHost()+"\r\n");
				sb.append("SOAPACTION: \"urn:schemas-upnp-org:service:RenderingControl:1#SetVolume\"\r\n");
				sb.append("CONTENT-TYPE: text/xml; charset=\"utf-8\"\r\n");
				StringBuffer sb2=new StringBuffer();
				sb2.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n");			
				sb2.append("<s:Envelope s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n");
				sb2.append("<s:Body>\r\n");
				sb2.append("<u:SetVolume xmlns:u=\"urn:schemas-upnp-org:service:RenderingControl:1\">\r\n");
				sb2.append("<InstanceID>0</InstanceID>\r\n");
				sb2.append("<Channel>Master</Channel>");
				sb2.append("<DesiredVolume>"+params[0]+"</DesiredVolume>");
				sb2.append("</u:SetVolume>\r\n");
				sb2.append("</s:Body>\r\n");
				sb2.append("</s:Envelope>\r\n");
				byte[] b2=sb2.toString().getBytes("UTF-8");
				sb.append("Content-Length: "+b2.length+"\r\n\r\n");

				os.write(sb.toString().getBytes());
				os.write(b2);
				InputStream is=socket.getInputStream();
				InputStreamReader isr=new InputStreamReader(is);
				StringBuffer sb3=new StringBuffer();
				char b;int cnt=0;
				while(cnt<100 && (b=(char)isr.read() )!=-1){
					//Log.v("POST /AVTransport",""+sb3.toString());
					sb3.append(b);cnt++;
				}
				Log.v("start post vol",""+sb3.toString());	
				socket.close();
	    	}catch(Exception err){
	    		Log.v("start post vol err",""+err);
	    		return "no";}
	        return "ok";
	    }

	    @Override
		protected void onProgressUpdate(Integer... progress) {
	        
	    }

	    @Override
		protected void onPostExecute(String result) {
//	    	Log.v("loadlikes onPostEx","1 "+result);
	        	
	    }
	}	
	private final Runnable m_rec=new Runnable(){ @Override
	public void run() {       
		mFiles=Start.getFiles();
		sstate=STATE_REC;
		setNaviButtons();
        try {
			if(mFiles!=null && mFiles.length>0)setListAdapter(getRecAdapter(mFiles));
			else{ setListAdapter(null);
			TextView tv=new TextView(m_context);
        	tv.setId(R.id.nix);
        	tv.setTextColor(getResources().getColor(R.color.white));
        	tv.setTextSize(20.0f);
        	tv.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
        	tv.setText(getResources().getString(R.string.save_tx4));
        	((LinearLayout)findViewById(R.id.listparent)).addView(tv, 0);}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.ticker);
		getListView().startAnimation(lani);	       
	}};
	
	private final Runnable m_ref_list = new Runnable()
	{
		@Override
		public void run()
		{
			try {
				if(alert!=null){
					alert.cancel();
					alert=null;
					//if(sshowagain)getToast(Start.getStart().getResources().getString(R.string.parsedat2)).show();
				}
				sstate=STATE_CAT;
				Vector <Channel> v= Xmlparser.getGroup();
				if(v.isEmpty())new DownloadFilesTask().execute("");
		        Enumeration <Channel> en=v.elements();
		        String[] s=new String[v.size()+1];
		        int cnt=1; 
		        try{
		        s[0]=Start.getStart().getResources().getString(R.string.custurlcom7);
		        while(en.hasMoreElements()){
		        	Channel c=en.nextElement();
		        	s[cnt++]=c.getText();
		        	//Log.v("category","group "+c.getText());
		        }}catch(Exception ex){}
		        Log.v("start mrun ref list",s.toString());
		        ListView lv = getListView();
		        setListAdapter(new MyAdapter2(Start.this, R.layout.slist, s));
		        getListView().setSelection(Start.slpos);
		        getListView().setDivider(null);
		        Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.ticker_inv);
				getListView().startAnimation(lani);	
				setNaviButtons();
		        main.invalidate();
			} catch (Exception e) {
				Log.v("start err ref_list",""+e.toString());	
			}
		}
	};	
	private final Runnable m_run_sensor_wait = new Runnable(){@Override
	public void run(){ssensorwait=false;}};	
	public final Handler m_handler = new Handler();
	public static Start getStart(){
		return strt;
	}
	public static void registerHandler(Handler h, View v){
		update_handler=h;
	}
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	  savedInstanceState.putBoolean("Save", ssave);
	  savedInstanceState.putBoolean("Shake", sshake);
	  savedInstanceState.putBoolean("Http", shttp);
	  savedInstanceState.putInt("Lpos", slpos);
	  savedInstanceState.putInt("Bgcol", sbgcol);
	  super.onSaveInstanceState(savedInstanceState);
	}
	
	void setBg(){
		switch(sbgcol){
		case 1:
            main.findViewById(R.id.arlayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_glit_gold));
            bgdrawid=R.drawable.bg_glit_gold;bgdrawcol=0;
            
            try {
    			wrap.setBackgroundDrawable(makeskin());wrap.invalidate();
    		} catch (Exception e) {}
            main.invalidate();
    	break;	
		case 2:
                main.findViewById(R.id.arlayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_glit_purp));
                bgdrawid=R.drawable.bg_glit_purp;bgdrawcol=0;
                try {
                	wrap.setBackgroundDrawable(makeskin());wrap.invalidate();
        		} catch (Exception e) {}	
                main.invalidate();
       break;
		case 3:
                main.findViewById(R.id.arlayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_grow_blue));
                bgdrawid=R.drawable.bg_grow_blue;bgdrawcol=0;
                try {
                	wrap.setBackgroundDrawable(makeskin());wrap.invalidate();
        		} catch (Exception e) {	}
                main.invalidate();
        break;
		case 4:
                main.findViewById(R.id.arlayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_grow_green));
                bgdrawid=R.drawable.bg_grow_green;bgdrawcol=0;
                try {
                	wrap.setBackgroundDrawable(makeskin());wrap.invalidate();
        		} catch (Exception e) {}
                main.invalidate();
        break;
		case 5:
            	main.findViewById(R.id.arlayout).setBackgroundDrawable(null);bgdrawcol=R.color.translucent_dark2;
            	main.findViewById(R.id.arlayout).setBackgroundColor(getResources().getColor(R.color.translucent_dark2));
            	
            	try {wrap.setBackgroundColor(R.color.translucent_dark2);wrap.invalidate();} catch (Exception e) {}
            	
         break;
		case 6:
        		main.findViewById(R.id.arlayout).setBackgroundDrawable(null);bgdrawcol=R.color.redgrey;
        		main.findViewById(R.id.arlayout).setBackgroundColor(getResources().getColor(R.color.redgrey));
        		try {wrap.setBackgroundColor(R.color.redgrey);wrap.invalidate();} catch (Exception e) {	}
        		 break;
		case 7:
        		main.findViewById(R.id.arlayout).setBackgroundDrawable(null);bgdrawcol=R.color.bluegrey;
        		main.findViewById(R.id.arlayout).setBackgroundColor(getResources().getColor(R.color.bluegrey));
        		try {wrap.setBackgroundColor(R.color.bluegrey);wrap.invalidate();} catch (Exception e) {}
        		 break;
		case 8:
            main.findViewById(R.id.arlayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.blackrose_1));
            bgdrawid=R.drawable.blackrose_1;bgdrawcol=0;
            try {
    			wrap.setBackgroundDrawable(makeskin());wrap.invalidate();
    		} catch (Exception e) {}
            main.invalidate();
    	break;	
		case 9:
                main.findViewById(R.id.arlayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.dotway_blu_1));
                bgdrawid=R.drawable.dotway_blu_1;bgdrawcol=0;
                try {
                	wrap.setBackgroundDrawable(makeskin());wrap.invalidate();
        		} catch (Exception e) {}	
                main.invalidate();
       break;
		/*case 10:
                main.findViewById(R.id.arlayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.dotway_grn_1));
                bgdrawid=R.drawable.dotway_grn_1;bgdrawcol=0;
                try {
                	wrap.setBackgroundDrawable(makeskin());wrap.invalidate();
        		} catch (Exception e) {}
                main.invalidate();
        break;
		case 11:
                main.findViewById(R.id.arlayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.train_blu_1));
                bgdrawid=R.drawable.train_blu_1;bgdrawcol=0;
                try {
                	wrap.setBackgroundDrawable(makeskin());wrap.invalidate();
        		} catch (Exception e) {}
                main.invalidate();
        break;*/
		case 12:
            main.findViewById(R.id.arlayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.train_red_1));
            bgdrawid=R.drawable.train_red_1;bgdrawcol=0;
            try {
    			wrap.setBackgroundDrawable(makeskin());wrap.invalidate();
    		} catch (Exception e) {}
            main.invalidate();
    	break;	
		case 13:
                main.findViewById(R.id.arlayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.trope_ora_1));
                bgdrawid=R.drawable.trope_ora_1;bgdrawcol=0;
                try {
                	wrap.setBackgroundDrawable(makeskin());wrap.invalidate();
        		} catch (Exception e) {}	
                main.invalidate();
       break;
	/*	case 14:
                main.findViewById(R.id.arlayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.trope_red_1));
                bgdrawid=R.drawable.trope_red_1;bgdrawcol=0;
                try {
                	wrap.setBackgroundDrawable(makeskin());wrap.invalidate();
        		} catch (Exception e) {}
                main.invalidate();
        break; */
		case 15:
                main.findViewById(R.id.arlayout).setBackgroundDrawable(getResources().getDrawable(R.drawable.wing_red_1));
                bgdrawid=R.drawable.wing_red_1;bgdrawcol=0;
                try {
                	wrap.setBackgroundDrawable(makeskin());wrap.invalidate();
        		} catch (Exception e) {}
                main.invalidate();
        break;
	}}
	@Override
	public void onStop() {
		super.onStop();
	//if(hts!=null){hts.stop();hts=null;}
	//RadioProxyProducer.playing=false;
	}
	
	public boolean hasMobileCon(){
		    ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo info = cm.getActiveNetworkInfo();
		    return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
	}
	
	public boolean checkWiFi(){
		
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        
        if (wifiManager.isWifiEnabled()){
        	WifiInfo wifiInfo = wifiManager.getConnectionInfo();
	        int ipAddress = wifiInfo.getIpAddress();
	        Log.v("ip address="," "+ipAddress);
	        String ipBinary = Integer.toBinaryString(ipAddress);
		      while(ipBinary.length() < 32) {
		          ipBinary = "0" + ipBinary;
		      }
		      String a=ipBinary.substring(0,8); String b=ipBinary.substring(8,16); String c=ipBinary.substring(16,24); String d=ipBinary.substring(24,32);
		      actualIpAddress =Integer.parseInt(d,2)+"."+Integer.parseInt(c,2)+"."+Integer.parseInt(b,2)+"."+Integer.parseInt(a,2);
		      Log.v("ip address="," "+actualIpAddress); 
		     return true;
        }
        return false;
	}
	
	@Override
	public void onDestroy()  { 
	 //audioad.destroyAd(); 
	 super.onDestroy(); 
	 } 
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        //java.util.logging.Logger.getLogger("org.teleal.cling").setLevel(Level.FINE);
        settingsready=false;adshowing=false;
        //audioad = new AdController(this, "139534430"); 
        //audioad = new AdController(this, "314120999");
        //audioad = new AdController(this, "839234085");
        //Log.v("<   <   <   start add","xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        		//audioad = new AdController(this, "136705791");
        //adi = new Intent(Start.this, ad.class);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        /*audioad = new AdController(this, "625418985", new AdListener() { 
        	@Override
			public void onAdLoaded() {Log.v("<   <   <   add loaded,","hallo");} 
        	@Override
			public void onAdClicked() {Log.v("<   <   <   add clicked.","hallo");
        	} 
        	@Override
			public void onAdClosed() {Log.v("<   <   <   add closed..","hallo");
        	try {
        		finish();
        	} catch (Exception e) {}
        	} 
        	@Override
			public void onAdCompleted() {Log.v("<   <   <   add completed...","hallo");} 
        	@Override
			public void onAdFailed() {} 
        	@Override
			public void onAdProgress() {} 
        	@Override
			public void onAdAlreadyCompleted() {} 
        	@Override
			public void onAdPaused() {} 
        	@Override
			public void onAdResumed() {} 
        	@Override
			public void onAdCached() { 
        		Log.v("<   <   <   add cached","hallo");
        		//if(audioad!=null)audioad.loadAd();
        	} 

       	}); 
       audioad.loadAdToCache(); 
        */
        hts=new Httpserver(Environment.getExternalStorageDirectory().getAbsolutePath());
        //m_handler.post(m_cachedialog);
        //m_handler.post(m_mobdialog);
        //audioad.loadAd(); 
        bg_drawables=new Drawable[15];
        buttons=new Drawable[14];
        speaker=new Drawable[2];
		try{
			File dir=new File(Environment.getExternalStorageDirectory() + "/vradio/" );
			dir.mkdirs();
			Log.v("... xmp direxists",""+dir.getAbsolutePath());
			}catch(Exception er){Log.v("... xmp err",""+er);}
        
        if(getSaved()==false && savedInstanceState!=null){
        	Log.v("<   <   <   start","on create getSaved==false "+ssave+" "+sshake);
        	ssave=savedInstanceState.getBoolean("Save");
        	sshake=savedInstanceState.getBoolean("Shake");       	
        	shttp=savedInstanceState.getBoolean("Http");
        	slpos=savedInstanceState.getInt("Lpos");
        	sbgcol=savedInstanceState.getInt("Bgcol");
        	sstate=savedInstanceState.getInt("State");
        	startURL=savedInstanceState.getString("Surl");
        }     
        setProp();
        ses_swarnmob=swarnmob;
        //Log.v("on create getIntent.getUri",""+getIntent().toUri(Intent.URI_INTENT_SCHEME));
		String tempstartURL=null;
        try {
			Uri data = getIntent().getData();
			Log.v(".x.x.x.x.x.x. on create external"," "+data.toString());
			if(data.toString().startsWith("icy")){
				if(data.toString().substring(6).length()>1){
					tempstartURL=data.toString().substring(6);
				}				
			}else{
			if(data.toString().substring(9).length()>1){
				tempstartURL=data.toString().substring(9);
			}
			}
		} catch (Exception e2) {
			Log.v("on create external err"," "+e2);
		}		
		if(tempstartURL!=null){
			if(!tempstartURL.equals(startURL)){
			startURL=tempstartURL;
			putSaved();
			startExtFlag=true;
			
			try {
				if(!startURL.equals(""))sstate=STATE_STATIONS;
			} catch (Exception e3) {}}
		}
        Log.v("wlan",""+checkWiFi());
        if(!checkWiFi()){
        	DownloadThread.m_errors+=getResources().getString(R.string.mocon3);
        	m_handler.post(m_netsetdialog);
        }else{
        
        	//hts=new Httpserver(Environment.getExternalStorageDirectory().getAbsolutePath());
        }
        //if(hasMobileCon() && swarnmob)m_handler.post(m_mobdialog);
        try {
			int wifi=android.provider.Settings.System.getInt(getContentResolver(),
				     android.provider.Settings.System.WIFI_SLEEP_POLICY);
			//Log.v("start","wifi "+wifi);
			//Log.v("start","wifi never "+android.provider.Settings.System.WIFI_SLEEP_POLICY_NEVER);
			//Log.v("start","wifi plugged "+android.provider.Settings.System.WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED);
		} catch (SettingNotFoundException e1) {
			Log.v("start","wifi err"+e1);
		}
        //Log.v("start","on create "+ssave+" "+savedInstanceState);
        favht=new Hashtable<String,Channel>();
        
        //if(mar==null && sshake)mar=new AccelerometerReader();
        
        strt=this;
        xmlReady=false;
        LayoutInflater li = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        main=(ViewGroup) li.inflate(R.layout.main, null, false);
        setContentView(main);
        if(findViewById(R.id.nix)!=null){
    		((LinearLayout)findViewById(R.id.listparent)).removeView(findViewById(R.id.nix));
    	}
        if(findViewById(R.id.tl)!=null){
    		skb=(SeekBar)findViewById(R.id.tl);
    		((ViewGroup)findViewById(R.id.vparent)).removeView(skb);
    	}
        setBg();
        ImageView img = (ImageView)findViewById(R.id.waitani);
        img.setBackgroundResource(R.anim.waitani);

        // Get the background, which has been compiled to an AnimationDrawable object.
        frameAnimation = (AnimationDrawable) img.getBackground();

        // Start the animation (looped playback by default).
        //Log.v("--------start Xmlparser.searchShout",""+Xmlparser.searchShout("xy"));
        if(sshowagain)m_handler.post(m_cachedialog);
        new DownloadFilesTask().execute(""); 
        new getLikes().execute("");
        m_context = getApplicationContext();
        if(m_nagare_service==null)bindService(new Intent(m_context, NagareService.class), m_nagare_service_connection, Context.BIND_AUTO_CREATE);
        //Db.getDb(Start.this);
        
        getApplicationContext().bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
            );

        //widgetview=(ViewGroup)findViewById(R.id.widget);
        //((ViewGroup)findViewById(R.id.vparent)).removeView(widgetview);        
        bg_drawables[0]=makeib(R.drawable.bg_glit_gold);
        bg_drawables[1]=makeib(R.drawable.bg_glit_purp);
        bg_drawables[2]=makeib(R.drawable.bg_grow_blue);
        bg_drawables[3]=makeib(R.drawable.bg_grow_green);
        bg_drawables[7]=makeib(R.drawable.blackrose_1);
        bg_drawables[8]=makeib(R.drawable.dotway_blu_1);
        //ib10.setBackgroundDrawable(makeib(R.drawable.dotway_grn_1));
        //ib11.setBackgroundDrawable(makeib(R.drawable.train_blu_1));
        bg_drawables[11]=makeib(R.drawable.train_red_1);
        bg_drawables[12]=makeib(R.drawable.trope_ora_1);
        //ib14.setBackgroundDrawable(makeib(R.drawable.trope_red_1));
        bg_drawables[14]=makeib(R.drawable.wing_red_1);
        buttons[0]=scale_imi(R.drawable.tab_sea, getMetrics().widthPixels);
        buttons[5]=scale_imi(R.drawable.tabon_sea, getMetrics().widthPixels);
        buttons[1]=scale_imi(R.drawable.tab_opt, getMetrics().widthPixels);
        buttons[6]=scale_imi(R.drawable.tabon_opt, getMetrics().widthPixels);        
        buttons[2]=scale_imi(R.drawable.tab_fav, getMetrics().widthPixels);
        buttons[7]=scale_imi(R.drawable.tabon_fav, getMetrics().widthPixels);
        buttons[3]=scale_imi(R.drawable.tab_rec, getMetrics().widthPixels);
        buttons[8]=scale_imi(R.drawable.tabon_rec, getMetrics().widthPixels);
        buttons[4]=scale_imi(R.drawable.tab_str, getMetrics().widthPixels);
        buttons[9]=scale_imi(R.drawable.tabon_str, getMetrics().widthPixels); 
        buttons[10]=scale_imi(R.drawable.like, getMetrics().widthPixels);
        buttons[11]=scale_imi(R.drawable.likeon, getMetrics().widthPixels);
        buttons[12]=scale_imi(R.drawable.fav, getMetrics().widthPixels);
        buttons[13]=scale_imi(R.drawable.favon, getMetrics().widthPixels);
        speaker[0]=scale_imi(R.drawable.speak, getMetrics().widthPixels);
        speaker[1]=scale_imi(R.drawable.speakon, getMetrics().widthPixels);        
        LinearLayout llp=(LinearLayout)findViewById(R.id.pbgll);
        ImageView ivp=new ImageView(m_context);		
        ivp.setBackgroundDrawable(scale_imi(R.drawable.player_left, getMetrics().widthPixels));
        llp.addView(ivp);        
        ImageView ivp2=new ImageView(m_context);		
        ivp2.setBackgroundDrawable(scale_imi(R.drawable.player_power, getMetrics().widthPixels));
        llp.addView(ivp2);  
        pimib2=new ImageView(m_context);		
        pimib2.setBackgroundDrawable(scale_imi(R.drawable.player_rec_off, getMetrics().widthPixels));
        llp.addView(pimib2);  
        ImageView ivp4=new ImageView(m_context);		
        ivp4.setBackgroundDrawable(scale_imi(R.drawable.player_shop, getMetrics().widthPixels));
        llp.addView(ivp4);  
        
        LinearLayout lln=(LinearLayout)findViewById(R.id.llnavi);
        ImageView iv=new ImageView(m_context);		
        iv.setBackgroundDrawable(scale_imi(R.drawable.tabs_left, getMetrics().widthPixels));
        lln.addView(iv);
        cat=new ImageButton(m_context);
        cat.setBackgroundDrawable(buttons[4]);
        lln.addView(cat);
        my=new ImageButton(m_context);
        my.setBackgroundDrawable(buttons[2]);
        lln.addView(my);
        ser=new ImageButton(m_context);
        ser.setBackgroundDrawable(buttons[0]);
        lln.addView(ser);
        recb=new ImageButton(m_context);
        recb.setBackgroundDrawable(buttons[3]);
        lln.addView(recb);
        set=new ImageButton(m_context);
        set.setBackgroundDrawable(buttons[1]);
        lln.addView(set);
        ImageView iv2=new ImageView(m_context);		
        iv2.setBackgroundDrawable(scale_imi(R.drawable.tabs_right, getMetrics().widthPixels));
        lln.addView(iv2);        
     //lln.invalidate();
     main.invalidate();
        cat.setSelected(true);
       vib=(Vibrator)getSystemService(VIBRATOR_SERVICE);
       
       if(sstate==STATE_REC){
    	   m_handler.post(m_rec);
       }
       else if(sstate==STATE_FAVS){
    	   m_handler.post(m_favs);
       }
       else if(sstate==STATE_SET){
    	   m_handler.post(m_settings);
       }
       else if(sstate==STATE_SEARCH){
    	   m_handler.post(m_search) ; 
       }else{
    	   sostate=sstate;
       }
       
       cat.setOnClickListener(new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			if(sstate==STATE_CAT)return;		
			try{((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(main.getWindowToken(), 0);}catch(Exception erx){}
			try{if(sstate==STATE_SEARCH){
				mRun=false;
				
				((LinearLayout)findViewById(R.id.listparent)).removeView(et);
				}else if(sstate==STATE_EDF){try{((LinearLayout)findViewById(R.id.listparent)).removeViewAt(0);}catch(Exception e){}}}catch(Exception e){Log.v("cat onclick err1",""+e.toString());}
				try{if(sstate==STATE_SET)((LinearLayout)findViewById(R.id.listparent)).removeView(sets);sets=null;}catch(Exception ee){Log.v("cat onclick err2",""+ee.toString());	}
				
				m_handler.post(m_ref_list);   
				
				if(findViewById(R.id.nix)!=null){
	        		((LinearLayout)findViewById(R.id.listparent)).removeView(findViewById(R.id.nix));
	        	}
				sostate=sstate;
				sstate=STATE_CAT;	setNaviButtons();		     
				temptext="";	       	
       }});
       recb.setOnClickListener(new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			if(sstate==STATE_REC)return;
			if(sstate==STATE_CAT)try{slpos=getListView().getFirstVisiblePosition();}catch(Exception e){}
			try{((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(main.getWindowToken(), 0);}catch(Exception erx){}
			try{if(sstate==STATE_SEARCH){
				mRun=false;
				
				((LinearLayout)findViewById(R.id.listparent)).removeView(et);
				}else if(sstate==STATE_EDF){try{((LinearLayout)findViewById(R.id.listparent)).removeViewAt(0);}catch(Exception e){}}
			}catch(Exception e){Log.v("my click","er "+e);}
				try{if(sstate==STATE_SET){((LinearLayout)findViewById(R.id.listparent)).removeView(sets);sets=null;}}catch(Exception e){} 
	              	m_handler.post(m_rec);
	              	sostate=sstate;
	              	sstate=STATE_REC;
	              	setNaviButtons();
	                
	              	if(findViewById(R.id.nix)!=null){
	            		((LinearLayout)findViewById(R.id.listparent)).removeView(findViewById(R.id.nix));
	            	}
	              	try{slpos=getListView().getFirstVisiblePosition();}catch(Exception e){}
       }});
       my.setOnClickListener(new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			if(sstate==STATE_FAVS)return;	
			if(sstate==STATE_CAT)try{slpos=getListView().getFirstVisiblePosition();}catch(Exception e){}
			try{((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(main.getWindowToken(), 0);}catch(Exception erx){}
			try{if(sstate==STATE_SEARCH){
				mRun=false;
				((LinearLayout)findViewById(R.id.listparent)).removeView(et);
				}
			}catch(Exception e){Log.v("my click","er "+e);}
				try{if(sstate==STATE_SET){((LinearLayout)findViewById(R.id.listparent)).removeView(sets);sets=null;}}catch(Exception e){} 
				if(findViewById(R.id.nix)!=null){
            		((LinearLayout)findViewById(R.id.listparent)).removeView(findViewById(R.id.nix));
            	}
				m_handler.post(m_favs);
				sostate=sstate;
				sstate=STATE_FAVS;
				setNaviButtons();
	              	
       }});
       ser.setOnClickListener(new View.OnClickListener(){
		@Override
		public void onClick(View v) {
			if(sstate==STATE_SEARCH)return;		
			if(sstate==STATE_CAT)try{slpos=getListView().getFirstVisiblePosition();}catch(Exception e){}
				try{
					if(sstate==STATE_SET){((LinearLayout)findViewById(R.id.listparent)).removeView(sets);sets=null;}
					else if(sstate==STATE_EDF){try{((LinearLayout)findViewById(R.id.listparent)).removeViewAt(0);}catch(Exception e){}}
				}catch(Exception e){} 
	              	m_handler.post(m_search) ; 
	              	if(findViewById(R.id.nix)!=null){
	            		((LinearLayout)findViewById(R.id.listparent)).removeViewAt(0);
	            	}
	              	sostate=sstate;
	              	sstate=STATE_SEARCH;
	              	setNaviButtons();
       }});
       set.setOnClickListener(new View.OnClickListener(){
   		@Override
   		public void onClick(View v) {
   			if(sstate==STATE_SET)return;
   			if(sstate==STATE_CAT)try{slpos=getListView().getFirstVisiblePosition();}catch(Exception e){}
   			try{((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(main.getWindowToken(), 0);}catch(Exception erx){}
			try{if(sstate==STATE_SEARCH){
				mRun=false;
				
				((LinearLayout)findViewById(R.id.listparent)).removeView(et);
				}else if(sstate==STATE_EDF){try{((LinearLayout)findViewById(R.id.listparent)).removeViewAt(0);}catch(Exception e){}}
			}catch(Exception e){Log.v("my click","er "+e);}			
			m_handler.post(m_settings);
			sostate=sstate;
				sstate=STATE_SET;
				setNaviButtons();
				if(findViewById(R.id.nix)!=null){
            		((LinearLayout)findViewById(R.id.listparent)).removeView(findViewById(R.id.nix));
            	}   	             
          }});      
        try {
			if(m_nagare_service!=null && m_nagare_service.state()>=1 && m_nagare_service.state()<=3)setOverlay(main);
		} catch (RemoteException e) {e.printStackTrace();}	
		try{
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
			//stat.restat(Environment.getExternalStorageDirectory() + "/vradio");
			long bytesAvailable = (long)stat.getAvailableBlocks() *(long)stat.getBlockCount();
			long megAvailable = bytesAvailable / 1048576;
			Log.v("--------start","Megs :"+megAvailable+" bytes:"+bytesAvailable);
			if(megAvailable<100){event(this, event_memlo, null);}else memo=true;
		}catch(Exception ex){}
		
		setNaviButtons();
		
		//Log.v("--------start","get listview"+getListView());
        //lv.setTextFilterEnabled(true);
        //setListAdapter(new ArrayAdapter<String>(this, R.layout.notes_row, s));
        //setListAdapter(new MyAdapter(this, R.layout.notes_row, getResources().getStringArray(R.array.strar)));   
        TelephonyManager tm=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        Log.v("--------start","startUrl"+startURL);
        if(startURL!=null && !startURL.trim().equals("")){
        	m_handler.postDelayed(m_playext, 300);}
    }  //end onCreate
    
    void setNaviButtons(){
    	Log.v("start setNaviButtons","sstate="+sstate);
        switch(sstate){
        case STATE_CAT:
        	ser.setBackgroundDrawable(buttons[0]);
        	set.setBackgroundDrawable(buttons[1]);
        	my.setBackgroundDrawable(buttons[2]);
        	recb.setBackgroundDrawable(buttons[3]);
        	cat.setBackgroundDrawable(buttons[9]);
        	break;
        case STATE_LIKES:
        case STATE_STATIONS:
        	ser.setBackgroundDrawable(buttons[0]);
        	set.setBackgroundDrawable(buttons[1]);
        	my.setBackgroundDrawable(buttons[2]);
        	recb.setBackgroundDrawable(buttons[3]);
        	cat.setBackgroundDrawable(buttons[4]);
        	break;
        case STATE_REC:  
        	ser.setBackgroundDrawable(buttons[0]);
        	set.setBackgroundDrawable(buttons[1]);
        	my.setBackgroundDrawable(buttons[2]);
        	cat.setBackgroundDrawable(buttons[4]);        	
        	recb.setBackgroundDrawable(buttons[8]);break;
        case STATE_FAVS:
        	ser.setBackgroundDrawable(buttons[0]);
        	set.setBackgroundDrawable(buttons[1]);
        	recb.setBackgroundDrawable(buttons[3]);
        	cat.setBackgroundDrawable(buttons[4]);      	
        	my.setBackgroundDrawable(buttons[7]);break;
        case STATE_SET:
        	ser.setBackgroundDrawable(buttons[0]);
        	my.setBackgroundDrawable(buttons[2]);
        	recb.setBackgroundDrawable(buttons[3]);
        	cat.setBackgroundDrawable(buttons[4]);
        	set.setBackgroundDrawable(buttons[6]);break;
        case STATE_SEARCH:	        	
        	set.setBackgroundDrawable(buttons[1]);
        	my.setBackgroundDrawable(buttons[2]);
        	recb.setBackgroundDrawable(buttons[3]);
        	cat.setBackgroundDrawable(buttons[4]);
        	ser.setBackgroundDrawable(buttons[5]);break;
        }
    }
    
	public final Runnable m_playext = new Runnable()	{		
		@Override
		public void run(){
			Log.v("--------m_playext","goingto play "+startURL);
			Channel chan=Xmlparser.searchC(startURL);
			String grp=Xmlparser.getGroup(startURL);
			if(chan==null){
				startURLFlag=true;
				if(play(" ", "http://"+startURL)){
					Start.this.setOverlay(main); 					
				}	
				m_handler.post(m_ref_list); 
			}else if(!startURL.equals("")){
				if(play(chan.getText(), chan.getUrl())){
					Start.this.setOverlay(main); 
				}	
				Start.groupSelected=grp;sstate=STATE_STATIONS;
				mHt= Xmlparser.getChannels(Start.groupSelected);
				m_handler.post(m_ref_stations);
				if(startExtFlag){
				putStreamProp(chan.getText(), "", chan.getUrl(), chan.getBitrate()?"1":"0");
				favht.put(chan.getText(), chan);
				startExtFlag=false;
				}
				//try{((LinearLayout)findViewById(R.id.listparent)).removeViewAt(0);}catch(Exception e){}				
			}
		}
	};
    

    public void setOverlay(final ViewGroup ag, boolean seek){
    	sek=seek;
    	setOverlay(ag);
    }
    	
   public void setOverlay(final ViewGroup ag){
	   	final ViewGroup parent=(ViewGroup)ag.findViewById(R.id.vparent);
	   	Log.v(",.,.,. in setoverlay1","w p="+getMetrics().widthPixels+" d="+getMetrics().density); 
		//((LinearLayout) ag.findViewById(R.id.pbgll)).setPadding(getResources().getDrawable(R.drawable.player_left).getIntrinsicWidth(), 0, 0, 0);
	   		float fac=getMetrics().widthPixels/480.0f;
	       LinearLayout ll=new LinearLayout(m_context);
	       ll.setOrientation(LinearLayout.HORIZONTAL);
	        ImageView ivp=new ImageView(m_context);		
	        ivp.setBackgroundDrawable(scale_imi(R.drawable.player_left, getMetrics().widthPixels));
	        ll.addView(ivp);   
	        wrap=new LinearLayout(m_context);
	    	LinearLayout.LayoutParams lllp=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    	wrap.setLayoutParams(lllp);
	    	wrap.setOrientation(LinearLayout.VERTICAL);
	    	//wrap.setBackgroundColor(Color.TRANSPARENT);
	    	wrap.setBackgroundDrawable(makeskin());
	    	wrap.setWeightSum(2.0f);
	        pfield=new LinearLayout(m_context);
	    	LinearLayout.LayoutParams lllp2=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
	    	pfield.setLayoutParams(lllp2);
	    	pfield.setBackgroundColor(Color.TRANSPARENT);
	        //pfield.setBackgroundDrawable(scale_imi(R.drawable.player_field , getMetrics().widthPixels));
	        pfield.setOrientation(LinearLayout.VERTICAL);
	        pfield.setMinimumHeight((int) (144*fac));
	        pfield.setMinimumWidth((int) (336*fac));
	        tex=new TextView(m_context);
	        tex.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
	        tex.setTextColor(Color.WHITE);
	        tex.setTextSize(12);
	        Log.v(",.,.,. in setoverlay1","tex"+tex.getMatrix().toString()+" fac="+fac); 
	        //tex.setTextSize((float)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11, getResources().getDisplayMetrics()));
	        //tex.setTextSize((float)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));

	        tex.setPadding((int)(6*fac), (int)(15*fac), (int)(6*fac), (int)(10*fac));
	        
	        pfield.addView(tex);
	        LinearLayout lltxt=new LinearLayout(m_context);
	        lltxt.setOrientation(LinearLayout.HORIZONTAL);
	        lltxt.setLayoutParams(lllp);
	        tex2=new TextView(m_context);
	        tex2.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
	        tex2.setTextColor(Color.WHITE);
	        //tex2.setTextSize((float)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));	  
	        //tex2.setPadding((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getResources().getDisplayMetrics()), 0, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getResources().getDisplayMetrics()), 0);
	        tex2.setTextSize(12);
	        tex2.setLines(3);
	        tex2.setMaxWidth((int) (270*fac));
	        tex2.setPadding((int)(6*fac), 0, (int)(6*fac), 0);
	        lltxt.addView(tex2);
	        tex3=new TextView(m_context);
	        tex3.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
	        tex3.setTextColor(R.color.redgrey);
	        tex3.setTextSize(11);
	        tex3.setPadding((int)(6*fac), 0, (int)(6*fac), 0);
	        //tex3.setPadding((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()), 0, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5, getResources().getDisplayMetrics()), 0);
	        lltxt.addView(tex3);
	        pfield.addView(lltxt);
	        wrap.addView(pfield);
	        ll.addView(wrap);
	        pbut=new ImageButton(m_context);
	        pbut.setBackgroundDrawable(scale_imi(R.drawable.player_pause, getMetrics().widthPixels));
	        pbut.setMinimumWidth((int) (114*fac));
	        ll.addView(pbut);
	        parent.addView(ll);
	        ((LinearLayout) ag.findViewById(R.id.pbgll)).setVisibility(View.VISIBLE);
			((LinearLayout) ag.findViewById(R.id.pbgll)).invalidate();
        //inal LinearLayout ll=((LinearLayout)parent.findViewById(R.id.pwrap));
        if(bgdrawcol==0)wrap.setBackgroundDrawable(makeskin());
        else {
        	wrap.setBackgroundDrawable(null);
        	wrap.setBackgroundColor(bgdrawcol);//getResources().getDrawable(R.drawable.bg_glit_gold_p)
        }
        wrap.invalidate();
        if(!ssave)pimib2.setBackgroundDrawable(scale_imi(R.drawable.player_rec_on, getMetrics().widthPixels));
		else pimib2.setBackgroundDrawable(scale_imi(R.drawable.player_rec_off, getMetrics().widthPixels));
		pimib2.invalidate();
        pfield.setOnTouchListener(new OnTouchListener(){@Override
		public boolean onTouch(View v, MotionEvent event) {
        //Log.v(". . . . . . . . . . onTouch playerfield ","motion event"+event.getAction()+" rX="+event.getRawX()+" X="+event.getX()+" w="+pbut.getMeasuredWidth());
        	if(event.getAction()==0){           	        			
	        	if(popen){
	        		popen=false;
	        		//m_handler.postDelayed(m_remalert, 2000);
	       		Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.invplayerani);				  
	     			//alert2.show();
	     			wrap.startAnimation(lani);
	     			if(event.getY()>0 && event.getY()<pbut.getMeasuredWidth()){
		    			if(event.getX()>0 && event.getX()<pbut.getMeasuredWidth()){
		    				stopPlay();	
		    				try {
								if(supnp){new postStop().execute(avtrans_url.toString());}
							} catch (Exception e) {	Log.v(".... poststop ",""+e);}
		    			}else if(event.getX()>pbut.getMeasuredWidth() && event.getX()<(pbut.getMeasuredWidth()*2)){
		    				ssave=!ssave;
		    				if(!ssave)pimib2.setBackgroundDrawable(scale_imi(R.drawable.player_rec_on, getMetrics().widthPixels));
	        				else pimib2.setBackgroundDrawable(scale_imi(R.drawable.player_rec_off, getMetrics().widthPixels));
	        				pimib2.invalidate();
		    				refPlayBut();
		    				if(sstate==STATE_SET){try{((CheckBox)sets.findViewById(R.id.savecb)).setChecked(ssave);}catch(Exception e){}}
		    			}else if(event.getX()>(pbut.getMeasuredWidth()*2)){
		    				m_handler.post(m_runbuy);
		    			}
	    			}
	        	}else{
	        		Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.playerani);
	     			wrap.startAnimation(lani);m_handler.postDelayed(m_popen, 400);
	        	}
        	}			
				return false;
			}});
        pfield.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	//Log.v(". . . . . . . . . . onClick playerfield ","view"+v);
            }});
        pfield.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {//Log.v(". . x. . .x . . x. .x . onLongClick playerfield ","view"+v);
			Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.playerani);
 			wrap.startAnimation(lani);m_handler.postDelayed(m_popen, 400);
			return false;}
			});
	/*	final Button pimib1=(Button)parent.findViewById(R.id.pimib1);
		pimib1.setOnTouchListener(new OnTouchListener(){public boolean onTouch(View v, MotionEvent event) {
        	Log.v(". . . . . . . . . . onTouch pimib1 ","motion event"+event.getAction()+" "+event.getRawX()+""+event.getX());
        	Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.invplayerani);
			  
 			//alert2.show();
 			pfield.startAnimation(lani);
 			return false;
		}});
		final Button pimib2=(Button)parent.findViewById(R.id.pimib2);
		final Button pimib3=(Button)parent.findViewById(R.id.pimib3);
		*/
        try {
			//if(m_nagare_service!=null && m_nagare_service.state()==3){
				if(ssave)pbut.setBackgroundDrawable(scale_imi(R.drawable.player_pause_rec_g, getMetrics().widthPixels));
         		else pbut.setBackgroundDrawable(scale_imi(R.drawable.player_pause, getMetrics().widthPixels));; 
			//}
			//Log.v("setOverlay",""+m_nagare_service.position()+" / "+m_nagare_service.positionT());
			//m_nagare_service.seek(0);			
        } catch (Exception e) {e.printStackTrace();}   
		
        //Button pbut2=(Button)parent.findViewById(R.id.button2);
        if(sek){
        	//Log.v("setOverlay","sek"+sek);
        	pbut.setBackgroundDrawable(scale_imi(R.drawable.player_pause_rec_g, getMetrics().widthPixels));
	        skb.setOnSeekBarChangeListener(new OnSeekBarChangeListener (){
				@Override
				public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
					//Log.v("seekbar change","arg1"+arg1+" "+arg2);
					/*String meta=Xmlparser.getRecString(arg1);
			    	if(meta!=null){
			    		
			    		streamInfo=meta; event(this, Start.event_icy, meta);
			    	}*/
					if(arg2)seek(arg1);
					//m_handler.post(m_overlay_refresh);
					}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {}
			});
	        parent.addView(skb);
	    	skb.setMax(duration());
	        //m_handler.postDelayed(m_seek_refresh, 200);
        }
        try{        
        		//View tv = ll.findViewById(R.id.botn);
        		//refreshOverlay(tv);
        }catch(Exception err){//Log.v("start","err playbutton"+err);
        }
        

			
        pbut.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	//Log.v(". . . . . . . . . . onClick ","view "+v);
            	//Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.playerani);
     			//pfield.startAnimation(lani);m_handler.postDelayed(m_popen, 400);
            	if(selected){
            		getToastS(Start.getStart().getResources().getString(R.string.spause)).show();
                   	try {
						m_nagare_service.pause();selected=false;
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                   	//stopPlay();
             		Resources res = getResources();
             		
             		if(ssave)v.setBackgroundDrawable(scale_imi(R.drawable.player_play_rec_g, getMetrics().widthPixels));
             		else v.setBackgroundDrawable(scale_imi(R.drawable.player_play, getMetrics().widthPixels));
            	}else{
            		getToastS(Start.getStart().getResources().getString(R.string.sresume)).show();
            		//start.play(streamSelected, urlSelected);
                   	try {
						m_nagare_service.resume();selected=true;
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
             		Resources res = getResources();
             		if(ssave)v.setBackgroundDrawable(scale_imi(R.drawable.player_pause_rec_g, getMetrics().widthPixels));
             		else v.setBackgroundDrawable(scale_imi(R.drawable.player_pause, getMetrics().widthPixels));
            	}
            	//iv.setVisibility(View.GONE);            
        }});       
  /*      buton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Toast.makeText(getApplicationContext(), "Stopping..",Toast.LENGTH_SHORT).show();
            	
            	Start.stopPlay();
            	try {
					((ViewGroup)findViewById(R.id.vparent)).removeView(widgetview);
				} catch (Exception e1) {e1.printStackTrace();}
            }
        });  */
        iui_state=event_icy;
        refreshOverlay(ag);
    }
   
   private void delFile(String name, long pos){
	   try{
		   Log.v("Start delFile  ","delFile  "+ssave+" "+name);
		   if(!ssave || pos<8000){
			   Log.v("Start delFile  ","delFile  "+ssave+" "+name+" "+pos);
			   try{
				   File l2=new File(name+".bin");
				   l2.delete();
			   }catch(Exception ee){}
			   		File l=new File(name);
					l.delete();
		   }else{
			   if(pos<ShoutcastFile.inilen){
				   Log.v("Start delFile  ","let len"+ssave+" "+name+" "+pos);
				   RandomAccessFile l=new RandomAccessFile(name,"rwd");
				   l.setLength(pos);l.close();
			   }
		   }
	   }catch(Exception erx){Log.v("Start","delFile file err 2 "+erx);}
   }
   
   private void refPlayBut(){
		try {
			if(m_nagare_service!=null && pbut!=null){
				if(m_nagare_service.state()==3 ){							
					if(ssave)pbut.setBackgroundDrawable(scale_imi(R.drawable.player_play_rec_g, getMetrics().widthPixels));
					else pbut.setBackgroundDrawable(scale_imi(R.drawable.player_play, getMetrics().widthPixels));
				}else{
					if(ssave)pbut.setBackgroundDrawable(scale_imi(R.drawable.player_pause_rec_g, getMetrics().widthPixels));
					else pbut.setBackgroundDrawable(scale_imi(R.drawable.player_pause, getMetrics().widthPixels));						
				}
				pbut.invalidate();
			}
		} catch (Exception e) {}
		
}
   
   private final Runnable m_remalert = new Runnable()	{		@Override
public void run(){
	   if(alert2!=null)alert2.dismiss();
   }};
   public boolean getSaved(){
		 try {
			 //Log.v("<   <   <  start getSaved ","1");
			DataInputStream di= new DataInputStream( new FileInputStream ( Environment.getExternalStorageDirectory() + "/vradio/conf2.bin" ) );
	    	ssave=di.readBoolean();
	    	sshake=di.readBoolean();
	    	shttp=di.readBoolean();
	    	//sShake(sshake);
	    	slpos=di.readInt();
	    	sbgcol=di.readInt();
	    	uuid=di.readUTF();
	    	sstate=di.readInt();
	    	scache=di.readBoolean();
	    	//sshowagain=true;
	    	sshowagain=di.readBoolean();
	    	startURL=di.readUTF();
	    	supnp=di.readBoolean();
	    	supnpdev=di.readUTF();
	    	volumeSelected=di.readUTF();
	    	sostate=di.readInt();
	    	swarnmob=di.readBoolean();
	    	sad=di.readBoolean();
		 } catch (Exception e) {Log.v("<   <   <  start getSaved ","err"+e);return false;}
		 return true;
	 }

	 public void putSaved(){
		 Log.v(",.,.,. put saved",""+startURL);
		 try {
			DataOutputStream dout= new DataOutputStream( new FileOutputStream ( Environment.getExternalStorageDirectory() + "/vradio/conf2.bin" ) );
	    	dout.writeBoolean(ssave);
	    	dout.writeBoolean(sshake);
	    	dout.writeBoolean(shttp);
	    	dout.writeInt(slpos);
	    	dout.writeInt(sbgcol);
	    	if(uuid==null)uuid=UUID.randomUUID().toString();
	    	dout.writeUTF(uuid);
	    	dout.writeInt(sstate);
	    	dout.writeBoolean(scache);
	    	dout.writeBoolean(sshowagain);
	    	dout.writeUTF(startURL);
	    	dout.writeBoolean(supnp);
	    	dout.writeUTF(supnpdev);
	    	dout.writeUTF(volumeSelected);
	    	dout.writeInt(sostate);
	    	dout.writeBoolean(swarnmob);
	    	dout.writeBoolean(sad);
		 } catch (Exception e) {}
	 }

	private final Runnable m_overlay_refresh = new Runnable()
	{
		@Override
		public void run(){ 	try{refreshOverlay(main);}catch(Exception ex){}}
	};
	private final Runnable m_seek_refresh = new Runnable()
	{
		@Override
		public void run()
		{ 	try{
				//if(update_view!=null){
				updateSeekBar(main);
				if(sek)m_handler.postDelayed(m_seek_refresh,1000);
				
				//}
			}catch(Exception ex){Log.v("start","updateSeekBar 0"+ex );}			
		}
	};
    public void updateSeekBar(View ag){
    	
		try {
			if(m_nagare_service.positionT()<0)return;
			//Log.v("updateSeekBar","t=="+m_nagare_service.positionT());
		} catch (RemoteException e) {Log.v("start","updateSeekBar 1"+e);}
		
       	try{    	//skb.setSecondaryProgress((int)(m_nagare_service.position()));
    	skb.setProgress(m_nagare_service.positionT());
    	skb.invalidate();
    	String meta=Xmlparser.getRecString(m_nagare_service.positionT());
    	//Log.v(".... updateSeekBar 1",""+meta+" "+mmeta);
    	if(meta!=null && !meta.equals(mmeta)){
    		Log.v(".... updateSeekBar ",""+meta+" "+mmeta);
    		mmeta=meta;
    		streamInfo=meta;   
    		event(this, Start.event_icy, meta);
    	}
       	}catch(Exception ex){Log.v("start","updateSeekBar "+ex );
       	}
    }
    
    int waitanicounter=0;
	private final Runnable runwaitani = new Runnable()	{	@Override
	public void run()	{ 	try{
			if(waitanicounter>0)m_handler.postDelayed(this, 1000);
			else{
				findViewById(R.id.waitani).setVisibility(View.INVISIBLE);
    			frameAnimation.stop();
			}
			waitanicounter--;
			}catch(Exception ex){}}};
	public final Runnable runwaitanistop = new Runnable()	{	@Override
	public void run()	{ 	
		try{
			waitanicounter=0;
		}catch(Exception ex){}}};

    @Override
	public void event(Object source, int code, Object data) {   	
    	//Log.v("x x x x x x x start","event "+source+" "+data+" "+code);
    	
    		if(code==event_nocon){
    			m_handler.post(m_netsetdialog);
    		}
    		else if(code==event_memlo){
    			m_handler.post(m_standartdialog);
    		}else if(code==event_bitrate){
    			try {bitrate=data.toString();} catch (Exception e) {}
    		
    		}
    		try{
    			if(iui_state!=event_nocon && code!=event_buffer)iui_state=code;
    			if(data!=null && code!=event_bitrate)streamInfo=data.toString();
    		}catch(Exception ex){Log.v("event","err"+ex);}   	
    		if(code==event_buffer){
    			if(waitanicounter>0 && waitanicounter<2)waitanicounter++;
    			else{waitanicounter=2;
    			findViewById(R.id.waitani).setVisibility(View.VISIBLE);

    			int w=getResources().getDrawable(R.drawable.wait_00).getIntrinsicWidth()/2;
    			((LinearLayout)findViewById(R.id.waitaniparent)).setPadding(getMetrics().widthPixels/2-w , getMetrics().heightPixels/2-w, 0, 0);
    			frameAnimation.start();
    			m_handler.post(runwaitani);
    			return;
    			}
    		}else if(code==event_icy ){
    			waitanicounter=0;
    			if(data!=null)putMeta(data.toString());
    		}else if(code==event_nocon){
    			waitanicounter=0;
    		}
    		m_handler.post(m_overlay_refresh);
    }
    public void refreshOverlay(View ag){
    	//Log.v("vvvvvvvvvvvvvvvvvvvvvvvv start","refreshOverlay "+ag);
        try {
        		//TextView tex=(TextView) ag.findViewById(R.id.text_overlay);
				//TextView tex3=(TextView) ag.findViewById(R.id.text_bit);
				String grp=Xmlparser.getGroup(Start.streamSelected);
				try {
					if(grp.equals("") && !urlSelected.equals("")){grp=getStart().getResources().getString(R.string.custurltxt);}
				
				tex.setText( grp.equals("")?getStart().getResources().getString(R.string.recmp3)+" "+durationHM():Start.streamSelected+"\n"+getStart().getResources().getString(R.string.cate)+" "+grp);
				//ag.findViewById(R.id.botn).setBackgroundColor(Color.GRAY); 
				} catch (Exception e) {}
				//Log.v("vvvvvvvvvvvvvvvvvvvvvvvv start","ref2 "+2);
				//TextView tex2=(TextView) ag.findViewById(R.id.more_overlay);
				tex.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
				tex3.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
				tex2.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
				if(iui_state==event_icy || iui_state==event_bitrate){		
					//Log.v("start","refreshOverlay uistate"+iui_state);
										tex2.setText(streamInfo);
										tex3.setTextColor(Color.GREEN);
										if(Integer.parseInt(bitrate)>32)tex3.setTextColor(Color.MAGENTA|Color.LTGRAY);
										if(Integer.parseInt(bitrate)>64)tex3.setTextColor(Color.MAGENTA|Color.GRAY);
										if(Integer.parseInt(bitrate)>127)tex3.setTextColor(Color.RED|Color.GRAY);
										if(Integer.parseInt(bitrate)>128)tex3.setTextColor(Color.RED);
										tex3.setVisibility(View.VISIBLE);
										tex3.setText(""+bitrate+"KB");
										Animation hyperspaceJumpAnimation2 = AnimationUtils.loadAnimation(Start.getStart(), R.anim.ticker);
										tex2.startAnimation(hyperspaceJumpAnimation2);		
										iui_state=0;							
				}else if(iui_state==1){								
					//tex2.setText("Buffering...");
				
				}else if(iui_state==9){								
					tex2.setText(getStart().getResources().getString(R.string.memlo1));		
					tex2.setTextColor(Color.RED|Color.GRAY);
					tex2.setAnimation(null);
			}	
        
		} catch (Exception e) {
			Log.v("Start ref overlay","err: "+e);
			//e.printStackTrace();
		}
		//Button buton=(Button)ag.findViewById(R.id.button1);
		//if(sek){Start.getStart().m_handler.postDelayed(Start.getStart().m_seek_refresh,3000 );}
		wrap.invalidate();
	}   
	 
	public static int duration(){
    	try{
    		return m_nagare_service.duration();
    	}catch(Exception err){}
    	return -1;
    }
    
    public static String  durationHM(){
    	StringBuffer ret=new StringBuffer();
       	try{       
       		float fl=(duration())/60000.0f;
       		if(fl>1)ret.append(new Integer(duration()/60000).toString());
       		else ret.append("0");
       		ret.append(":");
       		getStart();
			int mini=(Start.duration()%60000)/1000;
       		if(mini<10)ret.append("0");
       		ret.append(new Integer(mini).toString());
    	}catch(Exception err){}
    	return ret.toString();
    }
    
    
    public boolean play(String name, String url){
    	Log.v("...play",name);
    	stopPlay();retryupnp=0;
    	try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if(!checkWiFi()){
        	DownloadThread.m_errors+=getResources().getString(R.string.mocon3);
        	m_handler.post(m_netsetdialog);
        	return false;
        }else{       
        	//hts=new Httpserver(Environment.getExternalStorageDirectory().getAbsolutePath());
        }
        if(hasMobileCon()){
        	
        	if(actualIpAddress.equals("0.0.0.0")){
        		mobname=name;moburl=url;
        		timeout=6000;
        		if(ses_swarnmob){
        			m_handler.post(m_mobdialog);
        			return false;
        		}     		
        	}else timeout=3000;
        }  
        makeMetaDB("");
        //stopPlay();
    	//Start.getStart().sShake(sshake);
    	Log.v("start","play "+url);
    	try{streamInfo=name;iui_state=0;
    	//getStart().setOverlay((ViewGroup) main);
    		if(url.indexOf(".pls")>=8){
    			String nurl=Xmlparser.parsePls(url);
    			Log.v("start","play pls "+nurl);
    			if(nurl!=null)url=nurl;
    		}else if(url.indexOf(".m3u")>=8){
    			String nurl=Xmlparser.parseM3u(url);
    	    	Log.v("start","play m3u "+nurl);
    			if(nurl!=null)url=nurl;
    		}
    		m_nagare_service.download(url);
    		streamSelected=name;
    		urlSelected=url;
    		selected=true;
    		return true;
    	}catch(Exception err){Log.v("start","play err "+err);}
    	return false;
    }
    
    public static boolean seek(int f){
    	//stopPlay();
    	try{
    		//int t=(int)((duration()/310)*f);
    		//Log.v("seeking",""+duration()+" "+f);
    		//m_nagare_service.scan();
    		m_nagare_service.seek(f);
    		return true;
    	}catch(Exception err){}
    	return false;
    }
    public boolean stopPlay(){
    	Log.v("vradioplayer","!!!!!!!!!!stopPlay");
    	selected=false;sek=false;
		streamInfo="";bitrate="32";iui_state=0;
		String fname=null;
		//if(hts!=null){hts.stop();hts=null;}
		//ShoutcastFile.clients=null;
		//RadioProxyProducer.playing=false;
		try{
			fname=Environment.getExternalStorageDirectory() + "/vradio/"+m_nagare_service.file_name();
			//if(m_nagare_service.file_name()==null)return false;
		}catch(Exception er){}
		Log.v("vradioplayer","!!!!!!!!!!stopPlay "+fname);
		try {
			if(m_nagare_service.state()==NagareService.STOPPED)return false;
		} catch (RemoteException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		try{
			((ViewGroup)findViewById(R.id.vparent)).removeAllViews();
		}catch(Exception ex){}		
		try {
			((LinearLayout) main.findViewById(R.id.pbgll)).setVisibility(View.INVISIBLE);
			((LinearLayout) main.findViewById(R.id.pbgll)).invalidate();
			sek=false;
		} catch (Exception e1) {e1.printStackTrace();}	
		try {
			Start.putMeta("VRSSStop");
		} catch (Exception e2) {}
		//update_handler=null;
		//update_view=null;
		if(sstate==STATE_REC){
			m_handler.post(m_rec);
		}
		long flen=0;
		try {
			flen=m_nagare_service.position();
		} catch (Exception e1) {}

		try {
			//((Service) m_nagare_service).stopSelf();
			if(m_nagare_service.file_name()!=null)delFile(fname,flen);
		} catch (Exception ee) {Log.v("S,,,,,,topplaY 2","err"+ee);}
 		try {
			m_nagare_service.stop();
			
		} catch (Exception e) {
			Log.v("S,,,,,,topplaY ","err"+e);
		}		
		try {
			//((Service) m_nagare_service).stopSelf();
		} catch (Exception ee) {Log.v("S,,,,,,stopPlay nagare stopself err 2","err"+ee);}
		Log.v("S,,,,,,topplaY ","done");
		return false;
    }
    
    public void stop(){
    	Log.v("vradioplayer","stop");
 		stopPlay();
          finish();
    }
    
	@Override
	public boolean onKeyDown(int Code, KeyEvent event) {
		Log.v("vradioplayer","start key click"+sostate);
		if ((Code == KeyEvent.KEYCODE_BACK) ) {
			if(popen){
				popen=false;
				Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.invplayerani);				  
     			wrap.startAnimation(lani);
     			return true;
			}
			if(sstate==STATE_EDF){
				m_handler.post(m_favs);
    	      	try{((LinearLayout)findViewById(R.id.listparent)).removeView(view);}catch(Exception e){}   
    	      	try{((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(main.getWindowToken(), 0);}catch(Exception erx){}
			}else if(sostate==sstate){
				m_handler.post(showQuitDialog);
			}else{
				//((LinearLayout)findViewById(R.id.listparent)).removeAllViews();
				//((LinearLayout)findViewById(R.id.listparent)).g
			    try{
			    	if(findViewById(R.id.nix)!=null){
			    		((LinearLayout)findViewById(R.id.listparent)).removeView(findViewById(R.id.nix));
			    	}
			    }catch(Exception e){}
			    
			    try{if(sstate==STATE_SEARCH){
						((LinearLayout)findViewById(R.id.listparent)).removeView(et);
						mRun=false;
					}else if(sstate==STATE_EDF){
						((LinearLayout)findViewById(R.id.listparent)).removeViewAt(0);
					}else if(sstate==STATE_SET){
						((LinearLayout)findViewById(R.id.listparent)).removeView(sets);sets=null;
					}
				}catch(Exception ee){Log.v("cat onclick err2",""+ee.toString());	}	
			    
					if(sostate==STATE_CAT){
						Start.groupSelected=gsel2;
						mHt= Xmlparser.getChannels(Start.groupSelected);
			    	   m_handler.post(m_ref_list);
					}else if(sostate==STATE_STATIONS){
						mHt= Xmlparser.getChannels(Start.groupSelected);
						m_handler.post(m_ref_stations);
					}else  if(sostate==STATE_REC){
			    	   m_handler.post(m_rec);
			       }else if(sostate==STATE_FAVS){
			    	   m_handler.post(m_favs);
			       }else if(sostate==STATE_SET){
			    	   m_handler.post(m_settings);
			       }else if(sostate==STATE_SEARCH){
			    	   m_handler.post(m_search) ; 
			       }else if(sostate==STATE_LIKES){
			    	   new loadLikes().execute(""); 
			       }					
			}
			/*if(sstate!=STATE_CAT){
				try{if(sstate==STATE_SEARCH)((LinearLayout)findViewById(R.id.listparent)).removeView(et);}catch(Exception e){}
				try{if(sstate==STATE_SET){((LinearLayout)findViewById(R.id.listparent)).removeView(sets);sets=null;}}catch(Exception e){} 
				sstate=STATE_CAT;mRun=false;
				Animation lani = AnimationUtils.loadAnimation(Start.getStart(), R.anim.listani_inv);
				m_handler.postDelayed(m_ref_list,400);
				getListView().startAnimation(lani);	
				cat.setSelected(true);recb.setSelected(false);my.setSelected(false);ser.setSelected(false);set.setSelected(false);
				if(findViewById(R.id.nix)!=null){
	        		((LinearLayout)findViewById(R.id.listparent)).removeView(findViewById(R.id.nix));
	        	}
				return true;
			}else{m_handler.post(showQuitDialog);*/
	    	    
	    		return true;}
	    //}
	    return super.onKeyDown(Code, event);
	}

   
    public static String[] searchS(){
	if(searchResults==null && shoutResults==null)return null;
	int i=0;
	try{
		if(searchResults!=null)i=searchResults.size();
		if(shoutResults!=null)i+=shoutResults.size();
	}catch(Exception e){}
	String[] st=new String[i];int cnt=0;
	try{    	    	
    	Enumeration<Channel> ec=searchResults.elements();
    	mHt=new Hashtable<String,Channel>();
    	while(ec.hasMoreElements()){
    		Channel canl=ec.nextElement();
    		
    		mHt.put(canl.getText(), canl);
    		st[cnt++]=canl.getText();}
    	}catch(Exception ex){
    		Log.v("err onPostEx","e:"+searchResults.size()+" "+ex);
    		//return s2;
    		}
  /*  	try{    	
        	
        	Enumeration<Channel> ec=shoutResults.elements();
        	while(ec.hasMoreElements()){
        		Channel canl=ec.nextElement();
        		if(mHt==null)mHt=new Hashtable<String,Channel>();
        		mHt.put(canl.getText(), canl);
        		st[cnt++]=canl.getText();}
        	}catch(Exception ex){
        		Log.v("err onPostEx","e:"+searchResults.size()+" "+ex);
        		} */
    return st;
}

 

@Override
protected void onPause() {
    super.onPause();
	/*if(mar!=null){
		if(sshake){mar.sm.unregisterListener(mar);mar=null;}
	}*/
}

public static String[] getFiles(){
	File exdir=null;
	Vector ret=null;
	try{
		exdir = new File(Environment.getExternalStorageDirectory() + "/vradio");
	}catch(Exception erx){if(debug)Log.v("ShoutcastFile","file err "+erx);}
	
	try{
		File[] l=exdir.listFiles();
		int cnt=l.length;
		ret=new Vector();
		if(debug)Log.v("ShoutcastFile","file gonna delete "+cnt);
		while(cnt>0){
			if(l[cnt-1].getName().startsWith("temp.mp3") || l[cnt-1].getName().endsWith(".bin")|| l[cnt-1].getName().startsWith("null")|| l[cnt-1].getName().startsWith("vr.xml")|| l[cnt-1].getName().startsWith("likes.tsv")){}
			else ret.addElement(l[cnt-1].getName());
			cnt--;
		}
	}catch(Exception erx){}	
	//Log.v("start","getFiles "+ret);
	try{
		String[] ts=new String[ret.size()];
		for(int i=0;i<ret.size();i++){ts[i]=ret.elementAt(i).toString();}
		return ts;
	}catch(Exception erx){}
	return null;
}
    
class DownloadFilesTask extends AsyncTask<String, Integer, String> {
    @Override
	protected String doInBackground(String...params ) {    	
        String str="..:";
        try{
	        if(scache && vronce)xmlReady=xmp.parseG(false);
	        else xmlReady=xmp.parseG(scache);
        }catch(Exception ex){
        	Log.v(". , . , . , parseG err","err="+ex);
        	if(ex instanceof java.io.FileNotFoundException){m_handler.post(m_standartdialog);}
        	else if(ex instanceof java.io.IOException || ex instanceof java.net.ConnectException || ex instanceof java.net.NoRouteToHostException || ex instanceof java.net.SocketException || ex instanceof java.net.ConnectException){m_handler.post(m_netsetdialog);}
        	
        }
        //likes=xmp.parseLikes();
        vronce=false;
        //if(xmlReady && startURL!=null &&! startURL.equals("") && !startonceFlag)m_handler.post(m_playext);
        //else 
        	if(xmlReady && (sstate==STATE_CAT))m_handler.post(m_ref_list);
        //Log.v("vradioplayer","in doInBackground xr="+xmlReady);
        startonceFlag=true;
        
        //Log.v("vradioplayer","in doInBackground xr="+xmlReady);
        return str;
    }

    @Override
	protected void onProgressUpdate(Integer... progress) {
        
    }

    @Override
	protected void onPostExecute(String result) {
    	parsed=true;
    }


}

PhoneStateListener mPhoneListener = new PhoneStateListener() { 
	@Override
	public void onCallStateChanged(int state, java.lang.String incomingNumber) {
		try {
			//Log.v(".,.,.,.,   onCallStateChanged","state"+state);
			if(state==TelephonyManager.CALL_STATE_RINGING || state==TelephonyManager.CALL_STATE_OFFHOOK || state==0){
			//Log.v(".,.,.,.,   onCallStateChanged","state2"+state);
			m_nagare_service.pause();		
			selected=false;		
			Button buton=(Button)findViewById(R.id.button1);		
			Resources res = getResources();
			refPlayBut();}
		}catch(Exception ex){}
}};



class SearchTask extends AsyncTask<String, Integer, String> {
	
    @Override
	protected String doInBackground(String...params ) {
    	//Log.v("search","in doInBackground ");
    	try{Thread.sleep(300);}catch(Exception err){Log.v("search","Thread sleep "+err);}
    	String str="..:";
    	try{
        //mRun=true;
        String temp=et.getText().toString();
        //while(temp.indexOf("\n")>=0)temp=temp.substring(0,temp.length()-2);
        Log.v("search","in doInBackground "+temp+" "+temptext);
        if(temp.length()<2 || temptext.equals(temp))return "no";
        temptext=temp;
        if(Start.searchTerm==null){
        	Start.searchTerm=temp;
        	Start.searchResults=Xmlparser.search(Start.searchTerm);
        }else if(Start.searchTerm.equals(temp)){
        	return "no";
        }else{
        	Start.searchTerm=temp;
        	Start.searchResults=Xmlparser.search(Start.searchTerm);
        }        
    	}catch(Exception err){}
        return str;
    }

    @Override
	protected void onProgressUpdate(Integer... progress) {
        
    }

    @Override
	protected void onPostExecute(String result) {
    	if(!result.equals("no"))
    	try{Log.v(".......... onPostEx","1 "+Start.searchResults);
	    	if(findViewById(R.id.nix)!=null){
	    		((LinearLayout)findViewById(R.id.listparent)).removeView(findViewById(R.id.nix));
	    	}
	    	setListAdapter(new MyAdapter(Start.this, R.layout.srow, Start.searchS()));
	    	et.requestFocus();
    	}catch(Exception ex){Log.v("err onPostEx","e: "+ex);}    	
    	if(mRun)new SearchTask().execute("");
    }
}
class postLikes extends AsyncTask<String, Integer, String> {
	
    @Override
	protected String doInBackground(String...params ) {
    	//Log.v("search","in doInBackground ");
    	try{
    			putSaved();
    			Log.v("vradioplayer","postlike url "+edurl);
    			HttpURLConnection hc = (HttpURLConnection) new URL("http://ninj.ch/vservices/addvote.jsp?id="+URLEncoder.encode(uuid)+"&url="+URLEncoder.encode(edurl)+"&t="+URLEncoder.encode(edname)).openConnection();
    			hc.setUseCaches(false);
    			hc.setDoInput(true);
    			hc.setRequestProperty("connection", "close");
    			InputStream is = hc.getInputStream();
    			StringBuffer sb=new StringBuffer();
    			int i=is.read();
    			while(i!=-1)i=is.read();      
    	}catch(Exception err){return "no";}
        return "ok";
    }

    @Override
	protected void onProgressUpdate(Integer... progress) {
        
    }

    @Override
	protected void onPostExecute(String result) {
//    	Log.v("loadlikes onPostEx","1 "+result);
        	
    }
}
class getLikes extends AsyncTask<String, Integer, String> {
	    @Override
		protected String doInBackground(String...params ) {
    	//Log.v("search","in doInBackground ");    	
    	try{
    		likes=Xmlparser.parseLikes(true);
            if(likes.isEmpty()){
            	m_handler.post(m_netsetdialog);
            }
    	}catch(Exception ex){
        	if(ex instanceof java.io.FileNotFoundException){m_handler.post(m_standartdialog);}
        	else if(ex instanceof java.io.IOException || ex instanceof java.net.ConnectException || ex instanceof java.net.NoRouteToHostException || ex instanceof java.net.SocketException || ex instanceof java.net.ConnectException){m_handler.post(m_netsetdialog);}

    		return "no";}
        return "ok";
    }
    @Override
	protected void onProgressUpdate(Integer... progress) {}
    @Override
	protected void onPostExecute(String result) {}
}
class loadLikes extends AsyncTask<String, Integer, String> {
	 
    @Override
	protected String doInBackground(String...params ) {
    	//Log.v("search","in doInBackground ");    	
    	try{
    		likes=Xmlparser.parseLikes(likeonce);
            if(likes.isEmpty()){
            	//m_handler.post(m_netsetdialog);
            }
    		likeonce=false;
    	}catch(Exception ex){
        	if(ex instanceof java.io.FileNotFoundException){m_handler.post(m_standartdialog);}
        	else if(ex instanceof java.io.IOException || ex instanceof java.net.ConnectException || ex instanceof java.net.NoRouteToHostException || ex instanceof java.net.SocketException || ex instanceof java.net.ConnectException){m_handler.post(m_netsetdialog);}

    		return "no";}
        return "ok";
    }

    @Override
	protected void onProgressUpdate(Integer... progress) {}

    @Override
	protected void onPostExecute(String result) {
//    	Log.v("loadlikes onPostEx","1 "+result);
    	Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.listani);
    	m_handler.post(m_ref_likes);
		getListView().startAnimation(lani);
   }
}
class searchShoutTask extends AsyncTask<String, Integer, String> {
	
    @Override
	protected String doInBackground(String...params ) {
    	//Log.v("search","in doInBackground ");
    	try{Thread.sleep(300);}catch(Exception err){Log.v("search","Thread sleep "+err);}
    	String str="..:";
    	try{
        //mRun=true;
        String temp=et.getText().toString();
        //while(temp.indexOf("\n")>=0)temp=temp.substring(0,temp.length()-2);
        Log.v("searchShoutTask","in doInBackground "+temp+" "+temptext2);
        if(temp.length()<2 || temptext2.equals(temp))return "no";
        temptext2=temp;
        if(Start.searchTerm2==null){
        	Start.searchTerm2=temp;
        	Start.shoutResults=Xmlparser.searchShout(Start.searchTerm2);
        }else if(Start.searchTerm2.equals(temp)){
        	return "no";
        }else{
        	Start.searchTerm2=temp;
        	Start.shoutResults=Xmlparser.searchShout(Start.searchTerm2);
        }        
    	}catch(Exception err){}
        return str;
    }

    @Override
	protected void onProgressUpdate(Integer... progress) {
        
    }

    @Override
	protected void onPostExecute(String result) {
    	if(!result.equals("no"))
    	try{Log.v(".......... searchShoutTask onPostEx","1 "+Start.shoutResults);
	    	if(findViewById(R.id.nix)!=null){
	    		((LinearLayout)findViewById(R.id.listparent)).removeView(findViewById(R.id.nix));
	    	}
	    	setListAdapter(new MyAdapter(Start.this, R.layout.srow, Start.searchS()));
	    	et.requestFocus();
    	}catch(Exception ex){Log.v("err onPostEx","e: "+ex);}    	
    	if(mRun)new searchShoutTask().execute("");
    }
}

private class MyAdapter extends ArrayAdapter <String>{
	String[] arr;
	public MyAdapter(Context c,int vgr, String[] s){		
		super(c,vgr,s);arr=s;
		//Log.v(".-.-.-MyAdapter","constructor  "+getMetrics().densityDpi+" "+getMetrics().widthPixels);
	}  
    @Override
    public View getView (int position, View convertView, ViewGroup parent){
    	//Log.v("MyAdapter","getView "+arr[position]+" "+position);
        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        LinearLayout lview=new LinearLayout(m_context);
        lview.setOrientation(LinearLayout.HORIZONTAL);
        android.widget.AbsListView.LayoutParams rl=new android.widget.AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.FILL_PARENT );
        lview.setLayoutParams(rl);
        lview.setBackgroundColor(R.color.transp);
        
        final ImageButton ib=new ImageButton(m_context);		
		ib.setBackgroundDrawable(buttons[10]);       
        //CheckBox cb=(CheckBox)lview.findViewById(R.id.scb);
        final ImageButton favs=new ImageButton(m_context);
        favs.setBackgroundDrawable(buttons[12]);
        
    	TextView tv=new TextView(m_context);
    	TextView tv2=new TextView(m_context);
    	tv.setTextColor(getResources().getColor(R.color.white));
    	tv.setId(R.id.litx);
    	tv.setTextSize(20.0f);
    	tv.setPadding(10, 0, 10, 4);
    	//tv.setWidth(getMetrics().widthPixels*4/5);
    	tv.setWidth(getMetrics().widthPixels-ib.getMeasuredWidth()-favs.getMeasuredWidth()-20);
    	tv.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
    	tv.setText(arr[position]);
		LinearLayout.LayoutParams llp=new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT, 1.0f);
		tv.setLayoutParams(llp);
		((LinearLayout.LayoutParams) tv.getLayoutParams()).gravity = Gravity.LEFT; 
		tv.setGravity(Gravity.LEFT);
		
    	tv2.setTextColor(getResources().getColor(R.color.white));
    	tv2.setTextSize(10.0f);
    	
    	tv2.setWidth(60);
    	tv2.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
    	
		LinearLayout.LayoutParams llpp=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		tv2.setLayoutParams(llpp);
		tv2.setText("");
		tv2.setGravity(Gravity.CENTER);		
		
		
		lview.addView(tv);  
		lview.addView(tv2);
        lview.addView(ib);		
        lview.addView(favs);
		        
        String s=null;
        try{
        	s=getMetaInf(arr[position]);
        	if(s!=null){favs.setBackgroundDrawable(buttons[13]);}        	
    	}catch(Exception err){//Log.v("MyAdapter","getView e2"+err);
    	}      
    	try{
    	if(likes.size()>0){
    		if(likes.get(arr[position])!=null){
    			ib.setBackgroundDrawable(buttons[11]);
    			tv2.setText(""+likes.get(arr[position]).getT());
    			//tv2.setText("123");
    		}else{ib.setBackgroundDrawable(buttons[10]);}
    	}
    	}catch(Exception err){}
        
        //tv.invalidate();
        tv.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {
				try{//Log.v("onlongClick","playing "+((TextView) v).getText().toString());
				//start.stopPlay();		
				//((TextView)v).setTextColor(getResources().getColor(R.color.white));
				String s=((TextView) v).getText().toString();
				if(s.endsWith(" (<=32kb)"))s=s.substring(0,s.indexOf(" (<=32kb)"));
				//Log.v("onlongClick","1");
				Channel url=null;
				try{url=mHt.get(s);}catch(Exception ex){}
				//Log.v("onlongClick","2 "+url);
				
				if(url==null)url=likes.get(s);
				startURL=s;
				Log.v("onlongClick....","playing url="+url.getUrl());
				//if(m_nagare_service!=null && m_nagare_service.state()>=1 && m_nagare_service.state()<=3)stopPlay();
					if(play(s, url.getUrl())){
						Start.this.setOverlay(main); 

					}	
						//Log.v("...onlongClick","playing "+((TextView) v).getText().toString());				
						//Log.v("...onlongClick","playing "+mHt.get(((TextView) v).getText()).getUrl());
					return true;
				}catch(Exception ex){Log.v("onLongClick","err "+ex);}
				return false;
			}});
        tv.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//Log.v("onToch","me="+event.getAction());
				try {
					if(event.getAction()>=0)((TextView)v).setTextColor(getResources().getColor(R.color.white));
					else ((TextView)v).setTextColor(getResources().getColor(R.color.rgray1));
				} catch (NotFoundException e) {e.printStackTrace();}
				return false;
			}});
   		/*cb.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				String name=((TextView)(((LinearLayout)arg0.getParent()).findViewById(R.id.srowt))).getText().toString();
				Log.v("stations","onCheckedChange"+name+" "+mHt.get(name).getUrl()+" "+arg1);
				arg0.setChecked(arg1);
				if(arg1){
					Db.getDb(Start.this).createFavorite(name, mHt.get(name).getUrl(), mHt.get(name).getParent());
					favht.put(name, new Channel(name,mHt.get(name).getUrl(),"Custom URL"));
					Toast.makeText(getApplicationContext(), Start.getStart().getResources().getString(R.string.addfavorites) , Toast.LENGTH_SHORT).show();
				}else{
					Db.getDb(Start.this).deleteFavorite(name);
					favht.remove(name);
					Toast.makeText(getApplicationContext(), Start.getStart().getResources().getString(R.string.remfavorites) , Toast.LENGTH_SHORT).show();
				}
			}} );
   		cb.invalidate();*/
   		favs.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				String name=((TextView)(((LinearLayout)v.getParent()).findViewById(R.id.litx))).getText().toString();
			boolean test=true;String br="1";
			if(name.endsWith(" (<=32kb)")){
				test=false;br="0";
				name=name.substring(0,name.indexOf(" (<=32kb)"));
			}
				//Log.v("onclick","name "+name);
				if(v.isSelected()){
					favs.setBackgroundDrawable(buttons[12]);
					v.invalidate();
					delStreamProp(name);
					favht.remove(name);
					getToastS(Start.getStart().getResources().getString(R.string.remfavorites)).show();
					//Toast.makeText(getApplicationContext(), Start.getStart().getResources().getString(R.string.remfavorites) , Toast.LENGTH_SHORT).show();
				}else{
					favs.setBackgroundDrawable(buttons[13]);v.invalidate();
					String curl=null;
					
					try{curl=mHt.get(name).getUrl();}catch(Exception e){}
					if(curl==null)curl=likes.get(name).getUrl();
					Channel ch=new Channel(name,curl,"Custom URL");
					ch.setBitrate(test);
					favht.put(name, ch);
					putStreamProp(name, "", curl, br);
					//Db.getDb(Start.this).createFavorite(name, mHt.get(name).getUrl(), mHt.get(name).getParent());
					//favht.put(name, new Channel(name,mHt.get(name).getUrl(),"Custom URL"));
					getToastS(Start.getStart().getResources().getString(R.string.addfavorites)).show();
					//Toast.makeText(getApplicationContext(), Start.getStart().getResources().getString(R.string.addfavorites) , Toast.LENGTH_SHORT).show();
				}
		}} );
   		favs.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//Log.v("ontoch","event "+event.getAction());
				return false;
			}});
   		lview.invalidate();
        return lview;
    }
}
Toast getToast(String s){
	try {
		if(ttoast!=null)ttoast.cancel();
	} catch (Exception e) {}
	LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    LinearLayout tview= (LinearLayout) li.inflate(R.layout.toast, null, false);
    TextView tvi=(TextView)tview.findViewById(R.id.totx);
    LinearLayout tll=(LinearLayout)tview.findViewById(R.id.totxparent);
    tvi.setText(s);
    Log.v("getToast", ""+(getMetrics().widthPixels)/2+" "+ (getMetrics().heightPixels)/2);
    tll.setPadding(getMetrics().widthPixels/4, getMetrics().heightPixels/4 , getMetrics().widthPixels/4, getMetrics().heightPixels/4);
    Toast ret=new Toast(m_context);
    ret.setDuration(Toast.LENGTH_LONG);
    ret.setView(tview);
    ttoast=ret;
    return ret;
}
Toast getToastS(String s){
	try {
		if(ttoast!=null)ttoast.cancel();
	} catch (Exception e) {}
	LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	LinearLayout tview= (LinearLayout) li.inflate(R.layout.toast, null, false);
    TextView tvi=(TextView)tview.findViewById(R.id.totx);
    LinearLayout tll=(LinearLayout)tview.findViewById(R.id.totxparent);
    tvi.setText(s);Log.v("getToastS", ""+(getMetrics().widthPixels-tvi.getWidth())/2+" "+ (getMetrics().heightPixels-tvi.getHeight())/2);
    tll.setPadding(getMetrics().widthPixels/4, getMetrics().heightPixels/4 , getMetrics().widthPixels/4, getMetrics().heightPixels/4);
    Toast ret=new Toast(m_context);
    ret.setDuration(Toast.LENGTH_SHORT);
    ret.setView(tview);
    ttoast=ret;
    return ret;
}

private class MyAdapter2 extends ArrayAdapter <String>{
	String[] arr;
	public MyAdapter2(Context c,int vgr, String[] s){
		super(c,vgr,s);arr=s;
	}  
    @Override
    public View getView (int position, View convertView, ViewGroup parent){
    	//Log.v("MyAdapter2","getView "+arr[position]);
        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout lview= (LinearLayout) li.inflate(R.layout.slist, parent, false);        
        TextView tv=(TextView)lview.findViewById(R.id.text1);
        tv.setText(arr[position]);
        tv.setShadowLayer(2, 2, 2, getResources().getColor(R.color.black));
        if(position==0){
        	
            tv.setOnLongClickListener(new OnLongClickListener(){
    			@Override
    			public boolean onLongClick(View v) {
    				try{//Log.v("onClick","playing "+((TextView) v).getText().toString());
    				//start.stopPlay();		
    				((TextView)v).setTextColor(getResources().getColor(R.color.white));
    				Start.groupSelected= ((TextView)v.findViewById(R.id.text1)).getText().toString();
    				Start.gsel2=Start.groupSelected;
    		    	Start.sstate=STATE_LIKES;setNaviButtons();
    		    	clview=((TextView)v.findViewById(R.id.text1));
    		    	clview.setTextColor(getResources().getColor(R.color.white));
    		    	cat.setSelected(false);
    				getToastS(getResources().getString(R.string.parsedat)).show();
    				likeonce=false;
    				new loadLikes().execute("");
    				slpos=getListView().getFirstVisiblePosition();
                	/*
                	m_handler.post(m_ref_likes);
        			getListView().startAnimation(lani);
        			slpos=getListView().getFirstVisiblePosition();
                	Start.groupSelected= ((TextView)v.findViewById(R.id.text1)).getText().toString();
                	Start.sstate=3;
                	clview=((TextView)v.findViewById(R.id.text1));
                	clview.setTextColor(getResources().getColor(R.color.white));
                	cat.setSelected(false);*/
                	//vib.vibrate(100);
                	 
       					
    					return true;
    				}catch(Exception ex){Log.v("onLongClick2","err "+ex);}
    				return false;
    			}});
        	
        }else{
        tv.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {
				try{Log.v("onClick","playing "+((TextView) v).getText().toString());
				//start.stopPlay();		
				((TextView)v).setTextColor(getResources().getColor(R.color.white));
            	Animation lani = AnimationUtils.loadAnimation(Start.this, R.anim.listani);
            	m_handler.postDelayed(m_ref_stations,300);
    			getListView().startAnimation(lani);
    			slpos=getListView().getFirstVisiblePosition();
            	Start.groupSelected= ((TextView)v.findViewById(R.id.text1)).getText().toString();
            	Start.gsel2=Start.groupSelected;
            	Start.sostate=Start.sstate;
            	Start.sstate=STATE_STATIONS;
            	//vib.vibrate(100);
            	clview=((TextView)v.findViewById(R.id.text1));
            	clview.setTextColor(getResources().getColor(R.color.white));
            	cat.setSelected(false);            	
            	mHt= Xmlparser.getChannels(Start.groupSelected);
						//Log.v("onClick","playing "+((TextView) v).getText().toString());								
					return true;
				}catch(Exception ex){Log.v("onLongClick3","err "+ex);}
				return false;
			}});}
        tv.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//Log.v("onToch","me="+event.getAction());
				try {
					if(event.getAction()>=0)((TextView)v).setTextColor(getResources().getColor(R.color.white));
					else ((TextView)v).setTextColor(getResources().getColor(R.color.rgray1));
				} catch (NotFoundException e) {e.printStackTrace();}
				return false;
			}});
        return lview;
    }
}


public static LinkedHashMap<String, String> metaDB=null;
//public static Properties metaDB=null;
public static void makeMetaDB(String name){
	metaDB=new LinkedHashMap<String, String>();
}
public static void storeMetaDB(){
	   try {
		   Log.v("storeMetaDB","");
			FileOutputStream fout= new FileOutputStream ( Environment.getExternalStorageDirectory() + "/vradio/"+m_nagare_service.file_name()+".bin" );
			for(Object s: metaDB.keySet().toArray()){
				fout.write((s+"="+metaDB.get(s)+"\n").getBytes());
			}
			  
		 } catch (Exception e) {Log.v("err  storeMetaDB",""+e);}
}
public static void delMetaDB(String name){
	try {
		File fil=new File(Environment.getExternalStorageDirectory() + "/vradio/"+name+".bin");
		fil.delete();
	} catch (Exception e) {}
}
public static void putMeta(String meta){
	if(metaDB==null)makeMetaDB("");
	metaDB.put(""+System.currentTimeMillis(), meta);	
	   try {
		   storeMetaDB();	  
		 } catch (Exception e) {}
}

public static Properties streamProps=null;
public void setProp(){
	   if(streamProps==null)streamProps=new Properties();
	   
	   try {
		   	File fi=new File(Environment.getExternalStorageDirectory() + "/vradio/db.bin");
			FileInputStream fin= new FileInputStream ( fi );
	    	if(fi.exists())sfile=true;
			streamProps.load(fin);
	  
		 } catch (Exception e) {}
}  

public static void getFHT(){
	//if(favht==null)
		favht=new Hashtable<String,Channel>();
		mHt=new Hashtable<String,Channel>();
	Enumeration en=streamProps.keys();
	while(en.hasMoreElements()){
		String s=en.nextElement().toString();
		if(s.charAt(s.length()-1)=='m'){
			if(streamProps.getProperty(s).startsWith("http://")){
				String s2=s.substring(0, s.length()-2);
				Channel chan=Xmlparser.searchC(s2);
				if(chan==null){
					chan=new Channel();
					chan.setText(s2);				
					String st=streamProps.getProperty(s);
					chan.setUrl(st);
				}
				try {
					if(getBitrate(s2).startsWith("1"))chan.setBitrate(true);
				} catch (Exception e) {}
				chan.setParent("fav");
				favht.put(s2,chan);
				
				mHt.put(s2,chan);
			}
		}
	}
}

public static String getBitrate(String sname){
	   try {
			return streamProps.getProperty(sname+".b");
	  
		 } catch (Exception e) {}
	   return null;
}
public static String getMetaInf(String sname){
	   try {
			return streamProps.getProperty(sname+".m");
	  
		 } catch (Exception e) {}
	   return null;
}
public static String getName(String sname){
	   try {
			return streamProps.getProperty(sname+".n");
	  
		 } catch (Exception e) {}
	   return null;
}
public static void putStreamProp(String name, String sname, String metainf, String bitrate){
	   if(streamProps==null)streamProps=new Properties();
	   streamProps.put(name+".m",metainf);
	   streamProps.put(name+".b",bitrate);
	   streamProps.put(name+".n",sname);
	   try {
			FileOutputStream fout= new FileOutputStream ( Environment.getExternalStorageDirectory() + "/vradio/db.bin" );
	    	streamProps.store(fout, "");	  
		 } catch (Exception e) {}
}
public static void delStreamProp(String name){
	   
	   if(streamProps==null)return;	   
	   
	   try {
		    streamProps.remove(name+".m");
		    streamProps.remove(name+".b");
		    streamProps.remove(name+".n");
		    
			FileOutputStream fout= new FileOutputStream ( Environment.getExternalStorageDirectory() + "/vradio/db.bin" );
	    	streamProps.store(fout, "");	  
		 } catch (Exception e) {}
}


}