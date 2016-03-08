package com.giantrabbit.nagare;

import java.net.URL;

import android.content.Context;
import android.util.Log;

public class CheckThread extends Thread{

	private NagareService ns;
	private boolean running=true;
	private DownloadThread dt;
	
	public CheckThread(NagareService ns, DownloadThread dt)
	{
		this.ns=ns;
		this.dt=dt;
		start();
	}
	public void run(){
		while(running){
	
		try{
    		Thread.sleep(1000);
    	}catch(Exception ex){}
		Log.v("....... in checkthread","total_f1 "+dt.m_shoutcast_file.total_f1);
    	if(dt.m_shoutcast_file!=null && dt.m_shoutcast_file.total_f1>ns.BUFFER_BEFORE_PLAY){
    		ns.startplay();
    		running=false;
    	}
    	}
	}
	
}
