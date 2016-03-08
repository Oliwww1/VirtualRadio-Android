package com.giantrabbit.nagare;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedOutputStream;
import java.io.RandomAccessFile;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;

import org.vradio.R;
import org.vradio.io.IcyInputStream;
import org.vradio.io.RadioProxyProducer;
import org.vradio.phone.Start;


import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class ShoutcastFile {
	Context m_context;
	String m_shoutcast_name;
	String m_file_name;
	int m_bitrate, bufpos;
	// public static int inilen=524288, repeats; //1048576;
	public static int inilen = 8388608, repeats;
	long m_current_write_pos;
	public static long total_f1;
	long m_buffer_mark_pos = 0;
	boolean m_done = false;
	boolean m_notified_buffering_done = false, addbuffer;
	String m_errors = "";
	// RandomAccessFile m_file;
	RandomAccessFile m_file;
	//RandomAccessFile m_file_t;
	InputStream m_ist;
	File m_nagare_dir;
	final static int MEM_E = 5;
	final static boolean debug = false;
	int m_icyMetaInt = -1;
	public String m_br = null;
	public static Hashtable<String, PipedOutputStream> clients;
	public static int id=0;
	public int privateid;
	private static final String HDR_ICY_METAINT = "icy-metaint";
	private NagareService ns;


	public ShoutcastFile(Context context, URLConnection connection)
			throws Exception {
		m_context = context;
		m_shoutcast_name = connection.getHeaderField("icy-name");
		//privateid=++id;
		try {
			m_bitrate = Integer.parseInt(connection.getHeaderField("icy-br"));
		} catch (Exception e) {
		}
		build_file_name();
		try {
			m_icyMetaInt = Integer.parseInt(connection
					.getHeaderField(HDR_ICY_METAINT));
		} catch (Exception er) {
		}
		try {
			m_br = connection.getHeaderField("icy-br");
		} catch (Exception er) {
		}
		Start.putStreamProp(m_file_name, m_shoutcast_name, "" + m_icyMetaInt,
				"" + m_br);
		if (debug) {
			// Log.v("ShoutcastFile","m_icyMetaInt "+m_icyMetaInt);
			Iterator<String> i = connection.getHeaderFields().keySet()
					.iterator();
			while (i.hasNext()) {
				String temp = i.next();
				Log.v("ShoutcastFile",
						temp + " " + connection.getHeaderField(temp));
			}
		}
	}

	public ShoutcastFile(Context context, InputStream is, NagareService ns) throws Exception {
		this.ns=ns;
		m_ist=is;
		m_context = context;
		DownloadThread.m_errors="";
		String http=null;
		String location=null;
		boolean cnt = true;
		int ct=0;total_f1=0;
		char b=' ';char last=' ';
		clients = new Hashtable<String, PipedOutputStream>();
		InputStreamReader isr=new InputStreamReader(m_ist);
		StringBuffer sb=new StringBuffer();
		String line="";
		privateid=++id;
		while(cnt && (b=(char)m_ist.read() )!=-1){
			
			if(b=='\n'){
				line=sb.toString();
				if(line.trim().equals(""))cnt=false;
				sb=new StringBuffer();
				Log.v("....shout response:",line);
				
				if (line.toLowerCase().indexOf("location:") >= 0) {
					location=line.substring(10).trim();
					throw new Exception(location);
				} else if (line.indexOf("HTTP/1.") >= 0) {
					if(line.indexOf("30") >= 0){http=line.trim();}
				} else if (line.indexOf("icy-notice1") >= 0) {
					Log.v("icy-notice1", line.substring(12));
					//DownloadThread.m_errors+=line.substring(12);
				} else if (line.indexOf("icy-notice2") >= 0) {
					DownloadThread.m_errors+=line.substring(12);
					Log.v("icy-notice2", line.substring(12));
				} else if (line.indexOf("icy-name") >= 0) {
					m_shoutcast_name = line.substring(9).trim();
					Log.v("icy-name", line.substring(9));
				} else if (line.indexOf("icy-genre") >= 0) {
					Log.v("icy-genre", line.substring(10));
				} else if (line.indexOf("icy-url") >= 0) {
					Log.v("icy-url", line.substring(8));
				} else if (line.indexOf("content-type") >= 0) {
					Log.v("content-type", line.substring(13));
				} else if (line.indexOf("icy-pub") >= 0) {
					Log.v("icy-pub", line.substring(8));
				} else if (line.indexOf("icy-metaint") >= 0) {
					
					Log.v("icy-metaint", line.substring(12).trim());
					m_icyMetaInt = Integer.parseInt(line.substring(12).trim());
				} else if (line.indexOf("icy-br") >= 0) {
					Log.v("icy-br", line.substring(7).trim());
					try {
						m_bitrate = Integer.parseInt(line.substring(7).trim());
						m_br=line.substring(7).trim();
					} catch (Exception e) {
					
					}					
				}
			}else sb.append(b);			
		}
		
		//Log.v("ShoutcastFile","http="+http+" l="+location);
		/*if(location!=null){
			throw new Exception(location);
		}*/
		
		if(!(m_bitrate>0))throw new Exception("nocon");
		
		//m_ist.mark(1000);
		//Log.v("xxx....sb   ","befor buid file name"+m_shoutcast_name+" "+m_icyMetaInt);
		if(m_shoutcast_name==null || m_shoutcast_name.equals(""))m_shoutcast_name=Start.getStart().streamSelected;
		build_file_name();
		Start.putStreamProp(m_file_name, m_shoutcast_name, "" + m_icyMetaInt,
				"" + m_br);
		//if(Start.getStart().supnp)Start.getStart().postAV("VRADIOSTREAMER"+privateid+".mp3");
		Start.getStart().event(this, Start.event_bitrate, m_br);
		if (debug) {
			Log.v("ShoutcastFile","constructor end "+m_br);
			// Iterator<String>
			// i=connection.getHeaderFields().keySet().iterator();
			// while(i.hasNext()){
			// String temp=i.next();
			// Log.v("ShoutcastFile",temp+" "+connection.getHeaderField(temp));
			// }
		}
	}

	public static void addRadioListener(String s, RadioProxyProducer rpp) throws IOException{
		Log.v("...ShoutcastFile","addRadioListener "+s);
		PipedOutputStream pos=new PipedOutputStream();
		rpp.dock(pos);
			if(clients==null){
				clients = new Hashtable<String, PipedOutputStream>();
			}
			clients.put(s, pos);
	}

	public static void removeRadioListener(String s){
		Log.v("...ShoutcastFile","removeRadioListener "+s);
		if(clients==null)return;
		try {
			//clients.get(s).playing=false;
			clients.remove(s);
		} catch (Exception e) {}
		Log.v("...ShoutcastFile","removeRadioListener "+clients);
	}
	
	public static void send2RadioListeners(byte[] b, int l) {
		//Log.v("...ShoutcastFile","send2RadioListeners "+clients);
		if(clients==null){
			return;
		}
		if(clients.size()==0)return;
		for(String pos : clients.keySet()){
			try {
				clients.get(pos).write(b, 0, l);
			} catch (Exception e) {//Log.v("...ShoutcastFile","send2RadioListeners err "+e);
				//clients.remove(pos);
			}
		}
	}
	
	public void build_file_name() throws Exception {
		if (debug)
			Log.v("ShoutcastFile", "build_file_name ");
		repeats = 0;
		Calendar now = new GregorianCalendar();
		int ln = m_shoutcast_name.length();
		if (ln > 18)
			ln = 18;
		m_file_name = m_shoutcast_name.substring(0, ln);
		char[] ca = m_file_name.toCharArray();
		for (int i = 0; i < ca.length; i++) {
			if (ca[i] == ' ')
				ca[i] = '.';
			else if (ca[i] == '/')
				ca[i] = '.';
			else if (ca[i] == '?')
				ca[i] = '.';
			else if (ca[i] == '*')
				ca[i] = '.';
			else if (ca[i] == '#')
				ca[i] = '.';
			else if (ca[i] == ':')
				ca[i] = '.';
			else if (ca[i] == ';')
				ca[i] = '.';
			else if (ca[i] == '<')
				ca[i] = '.';
			else if (ca[i] == '>')
				ca[i] = '.';
			else if (ca[i] == ']')
				ca[i] = '.';
			else if (ca[i] == '[')
				ca[i] = '.';
			else if (ca[i] == '{')
				ca[i] = '.';
			else if (ca[i] == '}')
				ca[i] = '.';
			else if (ca[i] == '(')
				ca[i] = '.';
			else if (ca[i] == ')')
				ca[i] = '.';
			else if (ca[i] == '%')
				ca[i] = '.';
			else if (ca[i] == ' ')
				ca[i] = '.';
		}
		m_file_name = new String(ca)
				+ "."
				+ now.get(Calendar.YEAR)
				+ Start.getStart().getResources().getStringArray(R.array.mon)[now
						.get(Calendar.MONTH)] + now.get(Calendar.DAY_OF_MONTH)
				+ "." + now.get(Calendar.HOUR_OF_DAY) + "-"
				+ now.get(Calendar.MINUTE) + "-" + now.get(Calendar.SECOND)
				+ ".mp3";
		try {
			m_nagare_dir = new File(Environment.getExternalStorageDirectory()+ "/vradio");
		} catch (Exception erx) {
			if (debug)
				Log.v("ShoutcastFile", "file err " + erx);
		}
		if (debug)
			Log.v("ShoutcastFile", "build_file_name " + m_file_name);

		m_nagare_dir.mkdirs();
		try {
			File f = new File(m_nagare_dir.getAbsolutePath() + "/" + "temp.mp3");
			if (f.exists())
				f.delete();
		} catch (Exception e) {
		}
		// Log.v("ShoutcastFile","build_file_name 3"+m_nagare_dir.exists());
		try {

			m_file = new RandomAccessFile(m_nagare_dir.getAbsolutePath() + "/"
					+ m_file_name, "rwd");
			//m_file_t = new RandomAccessFile(m_nagare_dir.getAbsolutePath()+ "/" + "temp.mp3", "rwd");
		} catch (Exception e) {
			Log.v("ShoutcastFile", "random acc file err 1" + e);
			if (e instanceof java.io.FileNotFoundException) {
				Start.getStart().m_handler.post(Start.getStart().m_stop);
				if (e.toString().indexOf("(Invalid argument)") > 0) {
					throw e;
				} else {
					Start.getStart().event(this, Start.event_memlo, null);
					throw e;
				}

			}

		}
		// Log.v("ShoutcastFile","before make f");
		try {
			// m_file1.setLength(6710886);
			// m_file1.seek(6710886);
			m_file.setLength(inilen);
			m_file.seek(inilen);
			m_file.seek(0);
			// byte[] b=new byte[512*512];
			// for(int i=0;i<128;i++)m_file1.write(b);
			// m_file1.seek(0);
		} catch (Exception e) {
			Log.v("ShoutcastFile", "random acc file err 2" + e);
			Start.getStart().event(this, Start.event_memlo, null);
			return;
		}

		// Log.v("ShoutcastFile","afta make f");
		// Log.v("ShoutcastFile","build_file_name 4 "+m_nagare_dir.getAbsolutePath()+"  "+m_file_name+" "+m_file);
	}

	public void done() {
		m_done = true;
	}

	public String errors() {
		return m_errors;
	}

	public String file_path() {
		return m_nagare_dir.getAbsolutePath() + "/" + m_file_name;
	}

	public String file_path2() {
		return m_nagare_dir.getAbsolutePath() + "/" + m_file_name;
	}

	public void download(DownloadThread download_thread) throws Exception {
		try {
			Log.v("ShoutcastFile","stream="+m_ist );
			//m_ist.reset();
		
			IcyInputStream iis = new IcyInputStream(Start.getStart(), m_ist, m_icyMetaInt);
			RadioProxyProducer.prepare("p"+id);
			FileOutputStream output = new FileOutputStream(m_file.getFD());
			
			byte[] buffer = new byte[1024*32];//*8 
			int numRead;int tick = 0;
			if((numRead = iis.read(buffer)) == -1)throw new Exception("nocon0");
			ns.m_handler.post(ns.m_run_startplay);
			send2RadioListeners(buffer, numRead);
			output.write(buffer, 0, numRead);
			buffer = new byte[1024*8];
			while ((numRead = iis.read(buffer)) != -1 && !m_done) {
				//Log.v("........ShoutcastFile","numread"+numRead);
				
				send2RadioListeners(buffer, numRead);
				output.write(buffer, 0, numRead);
				
				m_current_write_pos += numRead;
				total_f1 += numRead;
				tick++;
				//if(tick<5)Log.v("........ShoutcastFile","tick"+tick);
				if(tick==4)Start.getStart().postAV("VRADIOSTREAMER"+privateid+".mp3");
				 //Log.v("ShoutcastFile","buf="+new String(buffer));
			}
			//removeRadioListener(""+privateid);
			//RadioProxyProducer.playing=false;
		} catch (Exception e) {
			Log.v("ShoutcastFile", "err write file " + e.toString());
			m_errors+=e.toString();
			removeRadioListener(""+privateid);
			done();
			throw new Exception("nocon2");
			// Start.getStart().event(this, Start.event_nocon, null);
			//RadioProxyProducer.playing=false;
			//removeRadioListener(""+privateid);
			/*
			 * try { BufferedOutputStream output = new BufferedOutputStream(new
			 * FileOutputStream(m_file)); byte[] buffer = new byte[1024]; int
			 * numRead; while ((numRead = input.read(buffer)) != -1 && !m_done)
			 * { output.write(buffer, 0, numRead); m_current_write_pos +=
			 * numRead; } } catch (Exception e)
			 */
		}
		removeRadioListener(""+privateid);
		done();
		m_errors=Start.getStart().getResources().getString(R.string.nocon);
		if(ns.state()==ns.PLAYING)Start.getStart().m_handler.post(Start.getStart().m_stop2);
		Log.v("ShoutcastFile Nagare ", "end download ");
	}

	public void rebuffer() {
		m_buffer_mark_pos = m_current_write_pos;
		m_notified_buffering_done = false;
	}
}
