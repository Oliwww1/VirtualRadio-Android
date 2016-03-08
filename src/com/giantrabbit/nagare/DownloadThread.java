package com.giantrabbit.nagare;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Process;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;

import org.vradio.R;
import org.vradio.io.Xmlparser;
import org.vradio.phone.Start;

public class DownloadThread extends Thread
{
	public Context m_context;
	public static String m_errors = "";
	public URL m_url;
	public ShoutcastFile m_shoutcast_file = null;
	final static boolean debug=false;
	public NagareService ns=null;
	checkTask ct;
	int loops=0;
	Socket soc;
	Socket soc2=null;
	public DownloadThread(Context context, URL url, NagareService ns)
	{
		m_context = context;
		m_url = url;
		this.ns=ns;
	}
	
	class checkTask extends AsyncTask<String, Integer, String> {		 
	    protected String doInBackground(String...params ) {
	    	Log.v(". ... . . check","in doInBackground ");  
	    	loops++;
	    	try{
	    		Thread.sleep(1000);
	    	}catch(Exception ex){}
	    	if(m_shoutcast_file!=null && m_shoutcast_file.total_f1>ns.BUFFER_BEFORE_PLAY){return "stopped";}	    		
	        return "stopped";
	    }

	    protected void onProgressUpdate(Integer... progress) {Log.v(".... .... .... checkTask onProgressUpdate","1 "+progress);}

	    protected void onPostExecute(String result) {
	    	Log.v(".... .... .... checkTask onPostEx","1 "+result);
	    	if(loops>100)return;
	    	if(m_shoutcast_file!=null && m_shoutcast_file.m_done)return;
	    	if(result.equals("ok")){
	    		ct=new checkTask();	    	
	    		ct.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
	    	}else {
	    		ns.startplay();
	    	}
	   }
	}
	
	public void done()
	{//Log.v("......Downloadthread done","done"+soc);
		//try {soc.shutdownInput();} catch (Exception e) {Log.v("......Downloadthread done","done1"+e);}
		//try {soc.shutdownOutput();} catch (Exception e) {Log.v("......Downloadthread done","done1b"+e);}
		//try {if(soc2!=null)soc2.shutdownInput();} catch (Exception e) {Log.v("......Downloadthread done","done2"+e);}
		//try {if(soc2!=null)soc2.shutdownOutput();} catch (Exception e) {Log.v("......Downloadthread done","done2b"+e);}
		if (m_shoutcast_file != null)
		{
			m_shoutcast_file.done();
		}
		//try {soc.close();} catch (Exception e) {Log.v("......Downloadthread done","done3"+e);}
		//try {if(soc2!=null)soc2.close();} catch (Exception e) {Log.v("......Downloadthread done4","done"+e);}
		
	}
	
	public String errors()
	{
		if (m_shoutcast_file != null)
		{
			return m_errors + m_shoutcast_file.errors();
		}
		return m_errors;
	}
	
	public void run()
	{
		//Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		try
		{
			if(debug)Log.v("DownloadThrad","run .. "+m_url+" "+m_url.getHost()+" "+" "+m_url.getPath()+" "+m_url.getPort());
			int po=m_url.getPort();
			if(po==-1)po=80;
			soc=new Socket();
			//Log.v("DownloadThrad","socket created "+soc);
			soc.connect(new InetSocketAddress(m_url.getHost(), po),Start.timeout);
			//soc=new Socket(m_url.getHost(), po);
			//Log.v("DownloadThrad","socket connected "+soc);
			OutputStream os=soc.getOutputStream();
			String user_agent = "VirtualRadio-android-vradio.org";
			String req="GET "+m_url.getPath()+" HTTP/1.0\r\nHost: "+m_url.getHost()+"\r\nUser-Agent: "+user_agent+"\r\nIcy-MetaData: 1\r\nAccept: */*\r\nConnection: keep-alive\r\n\r\n";
			Log.v("DownloadThread","r="+req);
			os.write(req.getBytes());
			InputStream is=soc.getInputStream();
			
			
			
			try {
				m_shoutcast_file = new ShoutcastFile(m_context, is, ns);
			} catch (Exception e) {
				Log.v("......Downloadthread location",""+e);
				if(e instanceof java.lang.NullPointerException){
					Start.getStart().m_handler.post(Start.getStart().m_stop);
					return;
				}
				
				if(e.getMessage().startsWith("http://")){
					//Log.v("......Downloadthread location2",""+e.getMessage());				
					URL nurl=new URL(e.getMessage());
					int port=nurl.getPort();
					if(port==-1)port=80;
					soc2=new Socket();
					soc2.connect(new InetSocketAddress(nurl.getHost(), port), Start.timeout);
					OutputStream os2=soc2.getOutputStream();
					String req2="GET "+nurl.getPath()+" HTTP/1.0\r\nHost: "+nurl.getHost()+"\r\nUser-Agent: "+user_agent+"\r\nIcy-MetaData: 1\r\nAccept: */*\r\nConnection: keep-alive\r\n\r\n";
					Log.v("DownloadThread","r="+req2);
					os2.write(req2.getBytes());
					InputStream is2=soc2.getInputStream();
					try {
						m_shoutcast_file = new ShoutcastFile(m_context, is2, ns);
					} catch (Exception ee) {
						//2.redirect..
						m_errors +=ee.toString() + "\n";
						Log.v("......Downloadthread err2","err="+e);
						Start.getStart().m_handler.post(Start.getStart().m_stop);//Start.getStart().event(this, Start.event_nocon, null);
						return;
					}
				}else{
					m_errors += e.toString() + "\n";
					Log.v("......Downloadthread err3","err="+e);
					Start.getStart().m_handler.post(Start.getStart().m_stop);
					//Start.getStart().event(this, Start.event_nocon, null);
					return;
				}
				

				
			}
			
			
			
			//ct=new checkTask();
			//ct.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
			//ct.execute("");
			
			m_shoutcast_file.download(this);
			
		}
		catch (Exception e)
		{   Log.v("......Downloadthread err","err="+e);
			//if(e instanceof NullPointerException ||e instanceof java.net.UnknownHostException || e instanceof java.net.ConnectException || e instanceof java.net.SocketException){
				
				Start.getStart().m_handler.post(Start.getStart().m_stop);//Start.getStart().event(this, Start.event_nocon, null);
			//}
			if(!e.getMessage().equals("nocon2"))m_errors += e.toString() + "\n";
		}
	}
}