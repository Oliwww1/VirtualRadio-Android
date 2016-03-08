package org.vradio.io;



	import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.RandomAccessFile;
import java.util.Hashtable;
import java.util.Vector;

	import org.apache.http.entity.ContentProducer;

import android.util.Log;

	public class RadioProxyProducer  implements ContentProducer{
		public String path;
		public int bitrate;
		public int offset;
		RandomAccessFile m_file1;
		public long m_stime,m_current_write_pos;
		public boolean m_done;
		public String id;
		//public int mid=0;
		public boolean playing=false;

		public PipedInputStream pis=null;
		public static PipedInputStream ppis=null;
		public static Hashtable<String, RadioProxyProducer> ppht;
		static String prepared="";
		
		public static void prepare(String s){
			Log.v("RadioProxyProducer prepare ",s);
			if(ppht==null)ppht=new Hashtable<String, RadioProxyProducer>();
			RadioProxyProducer rpp=new RadioProxyProducer(s);
			ppht.put(s, rpp);
		}
		
		public static RadioProxyProducer getPrepared(String s){
			Log.v("RadioProxyProducer getprepared ",s);		
			RadioProxyProducer rpp=ppht.get(s);
			if(rpp==null){
				rpp=new RadioProxyProducer(s);
			}
			return rpp;
		}
		
		public RadioProxyProducer(String s){			
			super();
			Log.v(".............RadioProxyProducer id=",s);
			//mid=Integer.parseInt(s);
			id=s;
			playing=true;
			try {
				com.giantrabbit.nagare.ShoutcastFile.addRadioListener(s, this);
			} catch (IOException e) {
				playing=false;
			}
		}
		
		public void dock(PipedOutputStream pos) throws IOException{		
			Log.v(".............RadioProxyProducer dock=","");
			pis=new PipedInputStream(pos);			
		}
		public void undock() {		
			pis=null;			
		}		

		public void writeTo(final OutputStream outstream) throws IOException {
			//try {Thread.sleep(100);} catch (InterruptedException e1) {}
			if(playing==false)return;
			while(pis==null){
				try {Log.v(".............RadioProxyProducer........","........sleep1  ");
					Thread.sleep(50);
				} catch (InterruptedException e1) {}
			}
			byte[] buffer = new byte[1024];
			int numRead;
				
			try {
				while ((numRead = pis.read(buffer)) != -1) {
					//Log.v("........ShoutcastFile","numread"+numRead);
					outstream.write(buffer, 0,numRead );
				}
			} catch (Exception e) {
				if(e instanceof IOException)throw (IOException)e;
				return;
			}
				

			
			Log.v(".............RadioProxyProducer........","........end  ");
	    }
		
	}

