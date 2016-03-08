package org.vradio.phone;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilterReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

import org.vradio.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.audiofx.Visualizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

public class cutter extends Activity{
	public final Handler m_handler = new Handler();
	MediaPlayer mp=null, ap=null;
	Visualizer mv=null;
	VisualizerView vv=null;
	public static LinkedHashMap<Long, Song> metaDB=null;
	public String icybr,icymetaint,icygenre,icyname;
	public static long starttime, endtime, duration;
	public static float sf=0.f, ef=0.f;
	Thread td=null;
	Song currentSong=null;
	public void getMetaDB(){
		metaDB=new LinkedHashMap<Long, Song> ();
		   try {
			   BufferedReader buf = new BufferedReader(new FileReader(Environment.getExternalStorageDirectory() + "/vradio/"+Start.streamInfo+".bin") );
				String i=buf.readLine();
				//Log.v("... getMetaDB","i="+i);
				Song lastsong=null;
				while(i!=null && !i.equals("")){
					String[] arr=getMetaData(i);
					Log.v("getMetaDB",""+arr[0]+" "+arr[1]+" "+arr[2]);
					if(arr[1].startsWith("icy-br")){
						starttime=Long.parseLong(arr[0]);
						icybr=arr[2];
					}else if(arr[1].startsWith("m_icyMetaInt"))icymetaint=arr[2];
					else if(arr[1].startsWith("icy-genre"))icygenre=arr[2];
					else if(arr[1].startsWith("m_shoutcast_name"))icyname=arr[2];
					else if(arr[1].startsWith("VRSSStop"))lastsong.end=Long.parseLong(arr[0])-starttime;
					else {
						
						Song song=new Song();
						song.Artist=arr[1];
						song.Title=arr[2];
						Long lon=Long.parseLong(arr[0])-starttime;
						song.start=lon;
						if(lastsong!=null)lastsong.end=song.start;
						metaDB.put(lon, song);
						Log.v("getMetaDB","putting song"+lon+" "+song.Title+" "+song.start+" "+song.end);
						try {
							Log.v("getMetaDB","putting lastsong"+lon+" "+lastsong.Title+" "+lastsong.start+" "+lastsong.end);
						} catch (Exception e) {}
						lastsong=song;
					}
					i=buf.readLine();
					Log.v("getMetaDB","after readln "+i);
				}
				
				  
			 } catch (Exception e) {Log.v("getMetaDB","err"+e);}
		//Log.v("getMetaDB","icymetaint"+icymetaint+" icygenre"+icygenre+" "+endtime);
		
	}
	public static String[] getMetaData(String s){
		String[] ret=new String[3];
		
		try {
			Log.v("getMetaData",""+s.indexOf("=")+" "+s.indexOf("\t"));
			ret[0]=s.substring(0,s.indexOf("="));//Log.v("getMetaData","1");
			if(s.indexOf("\t")>1){
				ret[1]=s.substring(s.indexOf("=")+1,s.indexOf("\t"));//Log.v("getMetaData","2");
			}else{
				ret[1]=s.substring(s.indexOf("=")+1);//Log.v("getMetaData","2b");
			}
			if(s.indexOf("\t")>=10)ret[2]=s.substring(s.indexOf("\t")+1);
		} catch (Exception e) {
			Log.v("getMetaData",""+e);
		}
		return ret;
	}

	public void cutMp3(final Song song, float s, float e){
		Log.v(".... cutMp3","s="+s+" e="+e+" ds"+(e-s));
	try {
		if(sf!=0.f)return;
		sf=s;ef=e;
		td=new Thread(){public void run(){
	           try{
	        		Log.v(".... cutMp3","song"+song+" ds="+(cutter.ef-cutter.sf)+" cd="+cutter.duration);
	        		RandomAccessFile ram=new RandomAccessFile(new File(Environment.getExternalStorageDirectory() + "/vradio/"+Start.streamInfo),"rwd");
	        		File outn=new File(Environment.getExternalStorageDirectory() + "/vradio/"+Start.streamInfo+"-test-.mp3");
	        		try{outn.delete();}catch(Exception er){}
	        		FileOutputStream output = new FileOutputStream(outn);
	        		float ds=cutter.ef-cutter.sf;
	        		int offs=(int)((ram.length()*1000.0f*sf)/cutter.duration);
	        		int cnt=(int)((ram.length()*1000.0f*ds)/cutter.duration);
	        		ram.seek(offs);
	        		Log.v(".... cutMp3","offs="+offs+" cnt="+cnt+" ram.l="+ram.length());
	        		
	        		byte[] buffer = new byte[1024*64];
	    			int numRead,heap=0;
	    			while ((numRead = ram.read(buffer)) != -1 && (heap<(cnt-1024*64))) {
	    				output.write(buffer, 0, numRead);
	    				heap+=numRead;
	    				Log.v("----------cutmp3","buf="+numRead);
	    			}
	    			numRead = ram.read(buffer);
	    			if(numRead!=-1)output.write(buffer, 0, cnt-heap);
	        		sf=0.f;ef=0.f;
	        		Log.v("............  makeFileTask fin","cnt="+cnt); 
	            }catch(Exception ex){
	            	Log.v("...........  makeFileTask err","err="+ex);   
	            	sf=0.f;ef=0.f;
	            }
		}};
		
		td.start();

	} catch (Exception ex) {
		// TODO Auto-generated catch block
		Log.v(".... cutMp3","ex"+ex);
	}
		
	}
	
	public void playFromTo(float start, float end){
		Log.v("playFromTo",""+start+" "+end);
		ef=end;
    	try {
			if(ap != null && ap.isPlaying())ap.stop();
			m_handler.removeCallbacks(m_stop);
		} catch (Exception e1) {}
    	
    		ap = new MediaPlayer();
    		ap.setOnCompletionListener(new OnCompletionListener(){public void onCompletion(MediaPlayer ap) {}});
    			
    	
    	try {
    		ap.setDataSource(Environment.getExternalStorageDirectory() + "/vradio/"+Start.streamInfo);
    		ap.prepare();
    		ap.seekTo((int)start*1000);
    		ap.start();
    		m_handler.postDelayed(m_stop, (int)(end-start)*1000);
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} 	
		
	}
	
    @Override 
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try {
          LayoutInflater li = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
  	      View mView= li.inflate(R.layout.cut, null, false);
  	      setContentView(mView);     
  	      m_handler.post(m_cut);
			getMetaDB();
		} catch (Exception e) {
			Log.v(". . . cutter activity","e1"+e);
		}
		Log.v("o. . . . cutter activity","onCreate DONE");
    }
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.v("... cutter","cutter key click"+keyCode);
		if ((keyCode == KeyEvent.KEYCODE_BACK) ) {
			try {
				mp.stop();
			} catch (Exception e) {}
			try {
				ap.stop();
			} catch (Exception e) {}
	    	Intent i = new Intent(cutter.this, Start.class);
	    	startActivityForResult(i, 0);	
	    		return true;}
	
	    return super.onKeyDown(keyCode, event);
	}
	
	void printSamples(MotionEvent ev) {     
		Log.v(".-.--.-.-.-.-.- printsamples",""+ev.getRawX()+" "+ev.getRawY());
		final int historySize = ev.getHistorySize();     
		final int pointerCount = ev.getPointerCount();     
		for (int h = 0; h < historySize; h++) {         
			//Log.v("At time ",""+ ev.getHistoricalEventTime(h));         
			for (int p = 0; p < pointerCount; p++) {             
				//Log.v("  historical pointer",""+ev.getPointerId(p)+" "+ev.getHistoricalX(p, h)+" "+ev.getHistoricalY(p, h));
		}     }    
		//Log.v("At time ", ""+ev.getEventTime());     
		for (int p = 0; p < pointerCount; p++) {         
			//Log.v("  present pointer ", ""+ev.getPointerId(p)+" "+ ev.getX(p)+" "+ev.getY(p));     			
		} 
		if(pointerCount>1){			
			if(ev.getY(0)<ev.getY(1)){
				playFromTo(ev.getY(0), ev.getY(1));
				vv.updateSelection(ev.getY(0), ev.getY(1));
				cutMp3(null,ev.getY(0), ev.getY(1));
			}
			else{
				playFromTo(ev.getY(1), ev.getY(0));
				cutMp3(null,ev.getY(1), ev.getY(0));
				vv.updateSelection(ev.getY(1), ev.getY(0));
			}
		}else if(pointerCount==1){
			long y=(long) ev.getY(0)*1000;
			for(Long l:metaDB.keySet().toArray(new Long[0])){
				Song s=metaDB.get(l);
				if(y>s.start && y<s.end){
					Log.v("....jupi song found",""+l+" "+y+" "+metaDB.get(l).Title);
					metaDB.get(l).selected=true;
					currentSong=metaDB.get(l);
				}
			}
			for(long i=y-1000;i<y+1000;i++){
				if(metaDB.containsKey(new Long(i))){
					Log.v("....jupi line found",""+i+" "+y+" "+metaDB.get(new Long(i)).Title);
					metaDB.get(new Long(i)).high=true;
				}
				
			}
		}
	}
	
	private final Runnable m_stop=new Runnable(){ 
		public void run() {Log.v("ap play stop","pos="+ap.getCurrentPosition());
	if(ap.getCurrentPosition()<ef)m_handler.postDelayed(m_stop, 10);
	else ap.stop();}};
	
    private final Runnable m_cut=new Runnable(){ 

    	int seekpos=0, cnt=0;
    	
    
    	public void run() {
    		Log.v("m_cut","before cut "+Start.streamInfo);
        // Create a VisualizerView (defined below), which will render the simplified audio
        // wave form to a Canvas.
    	LayoutInflater li = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        
    	if (mp == null)
    	{
    		mp = new MediaPlayer();
    		mp.setOnCompletionListener(new OnCompletionListener(){public void onCompletion(MediaPlayer mp) {
    			
    		}});
    	}
    	try {
    		mp.setDataSource(Environment.getExternalStorageDirectory() + "/vradio/"+Start.streamInfo);
    		mp.prepare();
    		mp.start();
    	} catch (IllegalStateException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}	
    	cutter.duration=mp.getDuration();
        int dur=(int)(cutter.duration/1000);
    	vv = new VisualizerView(Start.getStart());
        vv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,dur));
        ((LinearLayout)findViewById(R.id.ll)).addView(vv, 0);     
        
        vv.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.v(",,,,, cutta ontoch","event "+event.getAction()+" "+event.getX()+" "+event.getY());
				printSamples(event);
				vv.invalidate();
				return true;
			}});
        	
       
    	try {
    		mv=new Visualizer(mp.getAudioSessionId());
    		int[] ii=mv.getCaptureSizeRange ();
    		Log.v("................cutter","visualizer "+mv.getCaptureSize()+" "+mv.getMaxCaptureRate()+" rlo="+ii[0]+" rhi="+ii[1]);
    		mv.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
    	        public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
    	                int samplingRate) {
    	        	int sum=0;
    	        	for(byte b:bytes){
    	        		sum+=b;
    	        	}
    	        	seekpos+=1000;
    	            try {
    	            	if(mp==null || !mp.isPlaying())return;
						if(mp.getDuration()>seekpos){
							vv.updateVisualizer((byte)(sum/bytes.length));
							vv.setMinimumHeight(vv.getHeight()+1);
							mp.seekTo(seekpos);
						}else mp.stop();
					} catch (Exception e) {
						try {
							mp.stop();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

    	        }

    	        public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
    	        		Log.v("on data capture Fft",""+bytes.length);
    	        	}
    	    }, mv.getMaxCaptureRate(), true, false);
    		mv.setCaptureSize(ii[0]*2);
    		mv.setEnabled(true);
    	} catch (UnsupportedOperationException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (IllegalStateException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (RuntimeException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
        // Create the Visualizer object and attach it to our media player.
    }};
    public static String  durationHM(int l){
    	StringBuffer ret=new StringBuffer();
       	try{       
       		float fl=l/60.0f;
       		if(fl>1)ret.append(new Integer(l/60).toString());
       		else ret.append("0");
       		ret.append(":");
       		int mini=(l%60);
       		if(mini<10)ret.append("0");
       		ret.append(new Integer(mini).toString());
    	}catch(Exception err){}
    	return ret.toString();
    }
    
    class makeFileTask extends AsyncTask<Song, Integer, Long> {
        protected Long doInBackground(Song... song) {    	
            
            try{
        		Log.v(".... cutMp3","song"+song+" "+(cutter.ef-cutter.sf));
        		RandomAccessFile ram=new RandomAccessFile(new File(Environment.getExternalStorageDirectory() + "/vradio/"+Start.streamInfo),"rw");
        		File outn=new File(Environment.getExternalStorageDirectory() + "/vradio/.........tteesstt.mp3");
        		FileOutputStream output = new FileOutputStream(outn);
        		float ds=cutter.ef-cutter.sf;
        		int offs=(int)((ram.length()*1000.0f*sf)/cutter.duration);
        		int cnt=(int)((ram.length()*1000.0f*ds)/cutter.duration);
        		ram.seek(offs);
        		Log.v(".... cutMp3","offs="+offs+" cnt="+cnt+" ram.l="+ram.length());
        		for(int i=0;i<cnt;i++)output.write(ram.read());	 
        		sf=0.f;ef=0.f;
        		Log.v("............  makeFileTask fin",""); 
            }catch(Exception ex){
            	Log.v("...........  makeFileTask err","err="+ex);   
            	sf=0.f;ef=0.f;
            }
           
            return 0L;
        }

        protected void onProgressUpdate(Integer... progress) {
        	Log.v(". , . , . , makeFileTask update","prog="+progress);
        }

        protected void onPostExecute(String result) {
        	sf=0.f;ef=0.f;
        	Log.v(". , . , . , makeFileTask done","err="+result);  
        }
    }
    
    class Song extends Object{
    	long start, end;
    	String Artist, Title;
    	boolean high, selected;
    }

    class VisualizerView extends View {
    private byte[] mBytes;
    private float[] mPoints;
    private float top, bottom;
    private Rect mRect = new Rect();
    private Paint mForePaint = new Paint();
    private Paint linePaint = new Paint();
    private Paint selPaint = new Paint();
    private Paint hiPaint = new Paint();
    private Paint tp = new Paint();
    private Vector<Integer> vec=new Vector<Integer>();
    public VisualizerView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mBytes = null;
        mForePaint.setStrokeWidth(2f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(255, 255, 255));
        mForePaint.setTextSize(20.0f);
        linePaint.setColor(Color.rgb(255, 100, 155));
        selPaint.setColor(Color.rgb(155, 30, 55));
        linePaint.setTextSize(20.0f);
        tp.setTextSize(20.0f);
        tp.setColor(Color.rgb(255, 170, 205));
        hiPaint.setColor(Color.rgb(155, 155, 155));
        Log.v(".... in vv init","w"+getWidth()+" h"+getHeight());
    }

    public void updateVisualizer(byte byt) {
        vec.add(new Integer(byt));
        invalidate();
    }
   
    public void updateSelection(float f, float g) {
        this.top=f;
        this.bottom=g;
        invalidate();
    }   
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.v(".... in vv draw1","w"+getWidth()+" h"+getHeight());
        if (vec.size() == 0) {
            return;
        }

        if (mPoints == null || mPoints.length < vec.size() * 4) {
            mPoints = new float[vec.size() * 4];
        }
        //this.setMinimumHeight(vec.size());
        //this.setMeasuredDimension(getWidth(), getHeight()+1);
        mRect.set(0, 0, getWidth(), getHeight());
        int i=0;
        int u=0;
        if(top!=0 && bottom!=0){
        	canvas.drawRect(0.f, top, getWidth(), bottom, selPaint);
        	
        }
        while(i<vec.size()){
        	 
        	int v=((Integer)vec.elementAt(i)).intValue();
            mPoints[i * 4+1] = i;
            mPoints[i * 4] = u+128;
                 
            mPoints[i * 4 + 3] = i + 1;
            mPoints[i * 4 + 2] = v+128;
            u=v;i++;
        }
//Log.v(".... in vv draw2","vec.size"+vec.size()+" u "+u+" i"+i);
        canvas.drawLines(mPoints, mForePaint);
        
        if(currentSong!=null){
        	canvas.drawRect(0, currentSong.start/1000.0f, getWidth(), currentSong.end/1000.0f, hiPaint);
        	Log.v(".... in vv draw"," currentSong.start="+currentSong.start);
    	}
        
        Long[] kyz = cutter.metaDB.keySet().toArray(new Long[0]);
        int cnt=1;
        for(Long l: kyz){
        	try {
				//Log.v(".... in vv draw3","kyz.size"+l+" u "+kyz[cnt]+" "+l);
			} catch (Exception e) {}
        	Song cont=cutter.metaDB.get(l);     
        	
        	canvas.drawText(cont.Artist ,getWidth()/3,l/1000.0f+10.0f, tp);
        	canvas.drawText(cont.Title ,getWidth()/3,l/1000.0f+30.0f, tp);
        	if(cnt<kyz.length){
        		canvas.drawText("Duration "+cutter.durationHM((int)((kyz[cnt]-l)/1000)) ,getWidth()/3,l/1000.0f+50.0f, tp);
        		//canvas.drawText("Duration "+(int)((kyz[cnt].longValue())/1000.0f-l) ,getWidth()/3,l/1000.0f+50.0f, mForePaint);
        		cnt++;
        	}
        	else if(cnt==kyz.length)canvas.drawText("Duration "+cutter.durationHM((int)(getHeight()-(l/1000.0f))) ,getWidth()/3,l/1000.0f+50.0f, tp);
        	if(cont.high){canvas.drawLine(0, l/1000.0f , getWidth(), l/1000.0f, mForePaint); }else{
        	canvas.drawLine(0, l/1000.0f , getWidth(), l/1000.0f, linePaint);     }
        	
        	//Log.v(".... in vv draw3"," "+cont[0]+" "+cont[1]);
        }
    }
    }
    
}
