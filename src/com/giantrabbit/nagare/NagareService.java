package com.giantrabbit.nagare;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

import org.vradio.phone.Start;

import com.giantrabbit.nagare.DownloadThread.checkTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaScannerConnection;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

public class NagareService extends Service implements OnCompletionListener, OnBufferingUpdateListener, OnErrorListener
{
	public URL m_url = null;
	public DownloadThread m_download_thread = null;
	private CheckThread ct=null;
	public MediaPlayer m_media_player = null;
	//public MediaPlayer m_media_player2 = null;
	public boolean plswi=false, m_p1_playing=false, m_p2_playing=false, m_playoff=false;
	public Context m_context;
	public int m_current_position = 0,cdur;
	public String m_errors = "";
	public int m_state, dur_chunk, m_pause_t,bufferloops;
	public boolean m_scanned = false, paused, m_lock=true;
	public static final int STOPPED = 0;
	public static final int PLAYING = 1;
	public static final int BUFFERING = 2;
	public static final int PAUSED = 3;
	public static int repeats=0;
	public static long m_dur;
	public MediaScannerConnection m_scanner = null;
	public static final int BUFFER_BEFORE_PLAY = 8192; //32768; //65536;
	final static boolean debug=false;
	public static WifiManager.WifiLock _wifiLock = null;
	public static PowerManager _powerManagement = null;
	//public static PowerManager.WakeLock _wakeLock = null;
	Long cpos;
	public int filefac;
	

	final Handler m_handler = new Handler();
	//private volatile Handler m_handler2 = null;
	
	
	public NagareService() 
	{
		m_state = STOPPED;
	}
	Runnable m_run_startplay = new Runnable(){
		public void run(){startplay();Start.getStart().m_handler.post(Start.getStart().runwaitanistop);}
	};
	
	void startplay(){
		if(debug)Log.v("NagareService","startplay "+m_state);
		try {
			m_media_player.setDataSource("http://127.0.0.1:"+Start.getStart().hts.port+"/VRADIOSTREAMERp"+ShoutcastFile.id+".mp3");				
			m_media_player.prepareAsync(); //.prepare();
			//Start.getStart().event(this, Start.event_bitrate, m_download_thread.m_shoutcast_file.m_br);
			m_media_player.setOnPreparedListener(new OnPreparedListener() {
				public void onPrepared(MediaPlayer mp) {			            
		        	m_media_player.start();
		        	m_state = PLAYING;
					m_p1_playing=true;
					if(debug)Log.v("NagareService","start mp "+m_state+" "+m_current_position);
				}
			});	
		}catch (IllegalArgumentException e) {} 
		catch (IllegalStateException e) {} 
		catch (IOException e) {}
	}
	

	
	
	
	public void download(String url_string)
	{   if(debug)Log.v("................NagareService","download "+url_string+" "+m_state+" "+m_current_position);    
		m_errors = "";
		if(m_download_thread!=null){
			if(debug)Log.v("................NagareService","m_download_thread != null returning");
			return;
		}
		try
		{
			m_url = new URL(url_string);
		} 
		catch (MalformedURLException e)
		{
			m_errors += "Error parsing URL (" + url_string + "): " + e.toString() + "\n";
		}
		
		if (m_errors == "")
		{
			if(debug)Log.v("NagareService","errors null");
			m_context = getApplication().getApplicationContext();

			try {
				WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				  if (wifiManager != null) {
				   // _wifiLock = wifiManager.createWifiLock("wifi lock");
				    _wifiLock = wifiManager.createWifiLock(android.net.wifi.WifiManager.WIFI_MODE_FULL_HIGH_PERF,"wifi lock");
				    _wifiLock.acquire();
				  }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			m_download_thread = new DownloadThread(m_context, m_url, this);
			//m_handler2=new Handler(m_download_thread.getLooper());
			//ct=new CheckThread(this,m_download_thread);
			m_download_thread.start();
			m_current_position = 0;
			m_state = BUFFERING;
			m_scanned = false;
			//if(m_media_player==null)m_media_player = new MediaPlayer();
			m_media_player = new MediaPlayer();
			m_media_player.setOnCompletionListener(this);
			m_media_player.setOnErrorListener(this);
			m_media_player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			m_media_player.setOnBufferingUpdateListener(this);

			Start.getStart().event(this, Start.event_buffer, null);
		}
	}
        public boolean onError(MediaPlayer mp, int what, int extra) {
        	
        	Log.v(",..,.,.,., NagareService mp error listener",""+what+" "+extra);
        	//DownloadThread.m_errors+="Player err "+what+" "+extra;
			//m_media_player.reset();
        	//Start.getStart().m_handler.post(Start.getStart().m_stop2);
			//Start.getStart().event(this, Start.event_nocon, null);
        	
        	return true;}
        
	public String errors()
	{if(debug)Log.v("NagareService","errors () "+m_errors);
		if (m_download_thread != null)
		{//Log.v("NagareService","errors () dt!=null "+m_errors + m_download_thread.errors());
			return m_errors + m_download_thread.errors();		
		}
		return m_errors;
	}
	
	public String file_name()
	{
		if (m_download_thread == null)
		{
			return null;
		}
		
		if (m_download_thread.m_shoutcast_file == null)
		{
			return null;
		}
		
		return m_download_thread.m_shoutcast_file.m_file_name;
	}
	
	public IBinder onBind(Intent intent)
	{
		return m_binder;
	}
	

	
	public long position()
	{
		if (m_download_thread == null)
		{
			return -1;
		}
		
		if (m_download_thread.m_shoutcast_file == null)
		{
			return -1;
		}
		
		return m_download_thread.m_shoutcast_file.total_f1;
	}
	
	public int positionT()
	{
		if (m_media_player != null && m_state == PLAYING){return m_media_player.getCurrentPosition() ;}
		return -1;
	}	
	public int duration()
	{
		try {
			if (m_media_player != null && m_state == PLAYING){return m_media_player.getDuration() ;}
		} catch (Exception e) {}
		return -1;
	}		
	public void seek(int i){
		if (m_media_player != null && (m_state == PAUSED || m_state == PLAYING)){m_media_player.seekTo(i);m_state = PLAYING;}
	}
	
	
	public int state()
	{
		return m_state;
	}
	
	public void stop()
	{
		try {
			Log.v("....in NagareService stop",""+m_download_thread);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			Log.v("....in NagareService stop","err filelength "+e1);
			//Log.v("....in NagareService stop","downloadthread"+m_download_thread);
		}
		if (m_download_thread != null)
		{
			m_download_thread.done();
			m_download_thread = null;
		}
		if (m_media_player != null)
		{
			if (m_state == PLAYING)
			{
				try {
					m_media_player.stop();
				} catch (Exception e) {
					e.printStackTrace();
				}	
				try {
					m_media_player.release();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}			
			}
		}
		try {
			if(_wifiLock!=null){
				_wifiLock.release();
				_wifiLock=null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//if(_wakeLock!=null)_wakeLock.release();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m_state = STOPPED;m_playoff=false;
		
		//Start.getStart().event(this, Start.event_stop, null);
	}
	
	public void pause(){
		
		if (m_media_player != null && m_state == PLAYING){
		synchronized(this){	
			try {
				m_media_player.pause();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			m_state = PAUSED;
		}
		}
	}
	
	
	
	public void play(final String uri){
		Log.v(".............NagareService","play "+uri);
		File f=null;
		File f2=null;
		m_playoff=true;m_p1_playing=true;
		try{f = new File(uri);}catch (Exception e){}
		try{f2 = new File(uri+".bin");}catch (Exception e){}
		if (f.exists())
		{//Log.v("NagareService","f exists ");
			if(f2.exists()){
				
			}
			m_context = getApplication().getApplicationContext();			
			m_media_player = new MediaPlayer();
			m_media_player.setOnCompletionListener(this);
			
			try{
				//Log.v("NagareService","before play "+m_download_thread.m_shoutcast_file.file_path());
				m_media_player.setDataSource(uri);
				m_media_player.prepare();
				m_media_player.start();				
				m_state = PLAYING;
				
				//start.getStart().event(this, 7, m_media_player.getDuration());
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}		
	}	


	public void onCompletion(MediaPlayer mp){	
		//if(debug)
		Log.v("..............NagareService","on completion ");
		try {
			mp.release();
		} catch (Exception e) {
			Log.v("..............NagareService","err on completion "+e);e.printStackTrace();
		} 
		try {
			Start.getStart().m_handler.post(Start.getStart().m_stop2);
		} catch (Exception e) {
			Log.v("..............NagareService","err on completion 2"+e);e.printStackTrace();
		} 
	}


	public void resume(){
		if (m_media_player != null && m_state == PAUSED){
				m_media_player.start();	
			m_state = PLAYING;}
	}
	private final INagareService.Stub m_binder = new INagareService.Stub()
	{
		public void download(String url)
		{
			NagareService.this.download(url);
		}		
		public String errors()
		{
			return NagareService.this.errors();
		}		
		public String file_name()
		{
			return NagareService.this.file_name();
		}		
		public long position()
		{
			return NagareService.this.position();
		}
		public int positionT()
		{
			return NagareService.this.positionT();
		}		
		public void seek(int i)
		{
			NagareService.this.seek(i);
		}
		public int state()
		{
			return NagareService.this.state();
		}		
		public void stop()
		{
			
			NagareService.this.stop();
		}
		public int duration()
		{
			return NagareService.this.duration();
		}		
		public void pause()
		{
			NagareService.this.pause();
		}
		public void resume()
		{
			NagareService.this.resume();
		}
		public void play(String uri)
		{
			NagareService.this.play(uri);
		}
	};
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		//Log.v("...............NagarePlayer","onBufferingUpdate "+percent );	
		if(percent>0)Start.getStart().event(null, Start.event_buffer, null);
	}
}
