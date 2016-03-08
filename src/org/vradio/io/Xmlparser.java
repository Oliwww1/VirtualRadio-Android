package org.vradio.io;





import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import org.vradio.phone.Channel;
import org.vradio.phone.Start;

import android.os.Environment;
import android.util.Log;



public class Xmlparser {
	
private static Xmlparser xmp;

	private static final String KEY_TITLE = "title";

	private static final String KEY_URL = "url";
	
	private static final String TOKEN_GROUP = "<channel_group";
	
	private static final String TOKEN_CHAN = "<channel";

	private String parent="";
	private boolean bitrate;
	private static LinkedHashMap<Long, String> m_reclib;
	private static Vector<Channel> vc;
	
	private static Hashtable<String,Hashtable> ht;
	
	
	public static Xmlparser getParser(){
		if(xmp==null)xmp=new Xmlparser();
		return xmp;
	}
	
	public static Vector<Channel> getGroup(){
		return vc;
	}
	
	public static void prepareRecLib(String name) throws FileNotFoundException{
		m_reclib=new LinkedHashMap<Long, String>();
		File toread=new File(Environment.getExternalStorageDirectory() + "/vradio/"+name);
		int cnt=1;
		int data=0;
		long t_0=0;
		BufferedInputStream buin=new BufferedInputStream(new FileInputStream ( toread));
		
		try {
			StringBuffer sb=new StringBuffer();
			while((data=buin.read())!=-1){
				if(data==10){
					String line=sb.toString();
					Log.v("preparereclib","line"+line);
					Long time=Long.parseLong(line.substring(0, line.indexOf("="))) ;
					if(t_0==0){t_0=time;time=0L;}
					else {time=time-t_0;}
					String song=line.substring(line.indexOf("=")+1).trim();
					if(!song.equals("VRSSStop"))m_reclib.put(time, song);
					Log.v("preparereclib","time"+time+" "+song);
					sb=new StringBuffer();
				}
				else sb.append((char)data);
				
			}
		} catch (IOException e) {

		}
	}
	public static String getRecString(long time){
		Long a=new Long(0l);
		if(m_reclib==null)return null;
		if(m_reclib.isEmpty())return null;
		for(Long l : m_reclib.keySet()){
			
			if(time>l){a=l;}
		}
		String ret=m_reclib.get(a);
		//Log.v("getRecString","ret"+ret);
		if(ret!=null)return ret;
		else return null;
	}
	
	public static Hashtable<String, Channel> searchShout(String s){
		Hashtable<String, Channel> ret=new Hashtable<String, Channel>();
		//String scurl="http://shoutcast.com/Internet-Radio/"+s;	
		String scurl="http://www.shoutcast.com/search-ajax/"+s;
		try {
			HttpURLConnection hc = (HttpURLConnection) new URL(scurl).openConnection();
			hc.setUseCaches(false);
			hc.setDoInput(true);
			hc.setRequestProperty("connection", "close");
			checkHttpResponse(hc.getResponseCode());
			InputStream is = hc.getInputStream();
			String line = readLine(is).trim();
			Log.v("xmlparser","parsePls parseshout"+line);
			while(true){				
				if(line.indexOf("<div class=\"stationcol")>=0){
					Log.v("xmlparser","parseshout line="+line);
					line = readLine(is).trim();
					if(line.startsWith("<a href=")){
						StringTokenizer st=new StringTokenizer(line,"\"");
						st.nextToken();
						String canurl=st.nextToken();
						st.nextToken();
						String canname=st.nextToken();
						Channel can=new Channel();
						can.setUrl(canurl);can.setText(canname);
						can.setIsGroup(false);
						can.setParent("shoutcast.com");
						ret.put(canname, can);}
					
				}
				line = readLine(is).trim();
			}
		} catch (Exception e) {Log.v("xmlparser","parseshout error "+e);}
		return ret;
	}
	
	public static Channel prev(String s){
		//Log.v("xmlparser","prev "+s);
		Hashtable<String, Channel> ret=new Hashtable<String, Channel>();
		Enumeration<Hashtable> en=ht.elements();Channel prev=null;
		while (en.hasMoreElements()){
			Hashtable<String,Channel> lht=en.nextElement();
			Enumeration<String> len=lht.keys();
			String ps=null;
			while(len.hasMoreElements()){
				String str=len.nextElement();
				//Log.v("xmlparser","search "+str);
				if(str.equals(s)){
					if(ps==null)return prev;
					return lht.get(ps);
				}
				ps=str;
				prev=lht.get(ps);
			}
		}
		//Log.v("xmlparser","search found "+ret);
		return null;
	}	

	public static Channel next(String s){
		//Log.v("xmlparser","next "+s);
		Hashtable<String, Channel> ret=new Hashtable<String, Channel>();
		Enumeration<Hashtable> en=ht.elements();Channel next=null;
		while (en.hasMoreElements()){
			Hashtable<String,Channel> lht=en.nextElement();
			Enumeration<String> len=lht.keys();
			String ps=null;
			while(len.hasMoreElements()){
				String str=len.nextElement();
				//Log.v("xmlparser","search "+str);
				if(str.equals(s)){
					if(len.hasMoreElements()){
						return lht.get(len.nextElement());
					}else if (en.hasMoreElements()){
						Hashtable<String,Channel> ltht=en.nextElement();
						Enumeration<String> lten=ltht.keys();
						if(lten.hasMoreElements())return ltht.get(lten.nextElement());
					}else{return null;}
					
					
				}
			}
		}
		//Log.v("xmlparser","search found "+ret);
		return null;
	}	
	
	public static Hashtable<String, Channel> search(String s){
		//Log.v("xmlparser","search "+s);
		Hashtable<String, Channel> ret=new Hashtable<String, Channel>();
		Enumeration<Hashtable> en=ht.elements();
		while (en.hasMoreElements()){
			Hashtable<String,Channel> lht=en.nextElement();
			Enumeration<String> len=lht.keys();
			while(len.hasMoreElements()){
				String str=len.nextElement();
				//Log.v("xmlparser","search "+str);
				if(str.toLowerCase().indexOf(s.trim().toLowerCase())>=0){
					ret.put(str, lht.get(str));
				}
			}
		}
		//Log.v("xmlparser","search found "+ret);
		return ret;
	}

	public static Channel searchC(String s){
		//Log.v("xmlparser","search "+s);
		Channel ret=null;
		Enumeration<Hashtable> en=ht.elements();
		while (en.hasMoreElements()){
			Hashtable<String,Channel> lht=en.nextElement();
			Enumeration<String> len=lht.keys();
			while(len.hasMoreElements()){
				String str=len.nextElement();
				//Log.v("xmlparser","search "+str);
				if(str.toLowerCase().equals(s.trim().toLowerCase())){
					ret=lht.get(str);
				}
			}
		}
		//Log.v("xmlparser","search found "+ret);
		return ret;
	}
	
	public static String getGroup(String s){
		//Log.v("xmlparser","getGroup "+s);
		Enumeration<String> en=ht.keys();
		while (en.hasMoreElements()){
			String group=en.nextElement();
			Hashtable<String,Channel> lht=ht.get(group);
			if(lht.get(s)!=null )return group;

		}
		return "";
	}	
	
	public static Hashtable<String, Channel> getChannels(String parent){
		return ht.get(parent);
	}

	public Xmlparser(){
		ht=new Hashtable<String, Hashtable>();
		vc=new Vector<Channel>();
	}

public static String parsePls(String url){
	String ret=null;
	try {
		HttpURLConnection hc = (HttpURLConnection) new URL(url).openConnection();
		hc.setUseCaches(false);
		hc.setDoInput(true);
		hc.setRequestProperty("connection", "close");
		checkHttpResponse(hc.getResponseCode());
		InputStream is = hc.getInputStream();
		String line = readHttpLine(is).trim();
		//Log.v("xmlparser","parsePls line"+line);
		while(!line.equals("")){
			
			if(line.indexOf("File1=http://")>=0){
				String rest=line.substring(line.indexOf("File1=http://")+6);
				StringTokenizer st=new StringTokenizer(rest,"\n");
				ret=st.nextToken();
				break;
			}
			Log.v("xmlparser","parsePls "+ret);
			line = readHttpLine(is).trim();
		}
		
	} catch (Exception e) {}
	return ret;
}

public static String parseM3u(String url){
	String ret=null;
	try {
		HttpURLConnection hc = (HttpURLConnection) new URL(url).openConnection();
		hc.setUseCaches(false);
		hc.setDoInput(true);
		hc.setRequestProperty("connection", "close");
		checkHttpResponse(hc.getResponseCode());
		InputStream is = hc.getInputStream();
		String line = readLine(is).trim();
		Log.v("xmlparser","parseM3u line"+line);
		while(!line.equals("")){
			
			if(line.startsWith("http://")){
				ret=line.trim();
				break;
			}
			Log.v("xmlparser","parseM3u "+ret);
			line = readLine(is).trim();
		}
		
	} catch (Exception e) {}
	return ret;
}

public static  LinkedHashMap<String, Channel> parseLikes(boolean cache) throws Exception{
	LinkedHashMap<String, Channel> hat=new LinkedHashMap<String, Channel>();
	File lik=new File(Environment.getExternalStorageDirectory() + "/vradio/likes.tsv");
	int cnt=1;
		
		if(lik.exists() && cache){	Log.v("- - - xmlparser","parselikes from cache........................");		
		BufferedInputStream buin=new BufferedInputStream(new FileInputStream ( Environment.getExternalStorageDirectory() + "/vradio/likes.tsv" ) );
		int i=buin.read();
		StringBuffer sb=new StringBuffer();
		while(i!=-1){				
			if(i==10){
				String line=sb.toString();sb=new StringBuffer();
				//Log.v("xmlparser","from likes  line="+line);
				Channel cn=new Channel();
				StringTokenizer st=new StringTokenizer(line,"\t");
				cn.setText(st.nextToken().toString());
				if(hat.get(cn.getText())!=null)cn.setText(cn.getText()+(cnt++));
				cn.setBitrate(true);
				cn.setUrl(st.nextToken().toString());
				cn.setT(Long.parseLong(st.nextToken().toString()));
				//Log.v("parselikes.........",""+cn.getText()+" "+cn.getUrl());
				hat.put(cn.getText(), cn);
				
			}else{
				sb.append((char)i);
			}
			i=buin.read();
		}
		Log.v("xmlparser","done like  ");
		}else{
		URL url = new URL("http://ninj.ch/vservices/highvotes.jsp");
		BufferedOutputStream buf=new BufferedOutputStream( new FileOutputStream ( Environment.getExternalStorageDirectory() + "/vradio/likes.tsv" ) );
		HttpURLConnection hc = (HttpURLConnection) url.openConnection();
		hc.setUseCaches(false);
		hc.setDoInput(true);
		hc.setRequestProperty("connection", "close");
		//StringBuffer sb=new StringBuffer();
		InputStream is = hc.getInputStream();
		String line=readLine(is);
		
		Log.v("- - - parselikes.........","from url "+line);
		while(!line.equals("")){
			buf.write(line.getBytes());
			buf.write(10);
			Channel cn=new Channel();
			StringTokenizer st=new StringTokenizer(line,"\t");
			cn.setText(st.nextToken().toString());
			if(hat.get(cn.getText())!=null)cn.setText(cn.getText()+(cnt++));
			cn.setBitrate(true);
			cn.setUrl(st.nextToken().toString());
			cn.setT(Long.parseLong(st.nextToken().toString()));
			//Log.v("parselikes.........",""+cn.getText()+" "+cn.getUrl());
			//hat.put(cn.getText(), cn);
			
			hat.put(cn.getText(), cn);
			
			line=readLine(is);
		}
		buf.flush();buf.close();
		}
	return hat;
}

public boolean parseG(boolean cache) throws Exception{
	
	ht=new Hashtable<String, Hashtable>();
	vc=new Vector<Channel>();
	//Log.v("xmlparser","parseG");
	File vr=new File(Environment.getExternalStorageDirectory() + "/vradio/vr.xml");
	
		if(vr.exists() && cache){	Log.v("- - - xmlparser","parseG from cache........................");		
			//BufferedInputStream buin=new BufferedInputStream(new FileInputStream ( Environment.getExternalStorageDirectory() + "/vradio/vr.xml" ) );
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream ( Environment.getExternalStorageDirectory() + "/vradio/vr.xml" ) ));
			
			String line=br.readLine();
			
			while(line!=null){				
					if(line.startsWith(TOKEN_GROUP)){
						Channel chan = extractChannel(line);							
							if(parent!=null && ht.get(parent)==null){
								ht.put(parent,new Hashtable<String, Channel>());
								vc.add(chan);
							}
					}else if (line.startsWith(TOKEN_CHAN)){
						//Log.v("xmlparser","line="+line);
						Channel chan = extractChannel(line);
						Hashtable<String, Channel> hat=ht.get(parent);
						hat.put(chan.getText(),chan);				
					}
					line=br.readLine();
			}
			Log.v("xmlparser","done vr  ");
		}else{
			Log.v("- . - . - xmlparser","parseG from url........................");	
		//URL url = new URL("http://www.ninj.com/vradio/vrj/vr.xml");
		try{
		URL url = new URL("http://www.ninj.com/vradio/vr_list.xml");

		HttpURLConnection hc = (HttpURLConnection) url.openConnection();
		hc.setUseCaches(false);
		hc.setDoInput(true);
		hc.setRequestProperty("connection", "close");
		
		int res = hc.getResponseCode();
		checkHttpResponse(res);
		
		//StringBuffer sb=new StringBuffer();
		InputStream is = hc.getInputStream();
		String line = readHttpLine(is).trim();
		try{
		File dir=new File(Environment.getExternalStorageDirectory() + "/vradio/" );
		dir.mkdirs();
		Log.v("... xmp direxists",""+dir.getAbsolutePath());
		}catch(Exception er){Log.v("... xmp err",""+er);}
		BufferedOutputStream buf=new BufferedOutputStream( new FileOutputStream ( Environment.getExternalStorageDirectory() + "/vradio/vr.xml" ) );
		if(line.startsWith("<vr ")){
			buf.write(line.getBytes());
			buf.write(10);
		}else{return false;}
		do {
			line = readHttpLine(is).trim();
			buf.write(line.getBytes());
			buf.write(10);
			//sb.append(line+"\n");
			//Log.v("xmlparser",".line="+line);
			if(line.startsWith(TOKEN_GROUP)){
				Channel chan = extractChannel(line);
				
					
					if(parent!=null && ht.get(parent)==null){
						ht.put(parent,new Hashtable<String, Channel>());
						vc.add(chan);
					}
				
				
				
			}else if (line.startsWith(TOKEN_CHAN)){
				//Log.v("xmlparser","line="+line);
				Channel chan = extractChannel(line);
				Hashtable<String, Channel> hat=ht.get(parent);
				hat.put(chan.getText(),chan);				
			}
			
		       
			
		} while (line != null && line.length() > 0 );
		buf.flush();buf.close();
		}catch(Exception x){
			if(vr.exists()){	
				Log.v("- - - xmlparser","parseG from cache after load failed");					
				//BufferedInputStream buin=new BufferedInputStream(new FileInputStream ( Environment.getExternalStorageDirectory() + "/vradio/vr.xml" ) );
				BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream ( Environment.getExternalStorageDirectory() + "/vradio/vr.xml" ) ));			
				String line=br.readLine();			
				while(line!=null){				
						if(line.startsWith(TOKEN_GROUP)){
							Channel chan = extractChannel(line);							
								if(parent!=null && ht.get(parent)==null){
									ht.put(parent,new Hashtable<String, Channel>());
									vc.add(chan);
								}
						}else if (line.startsWith(TOKEN_CHAN)){
							//Log.v("xmlparser","line="+line);
							Channel chan = extractChannel(line);
							Hashtable<String, Channel> hat=ht.get(parent);
							hat.put(chan.getText(),chan);				
						}
						line=br.readLine();
				}
				Start.xmlReady=true;
				Log.v("xmlparser","done vr  from cache");
			}
			throw x;		
	  }
	}
	//Log.v("xmlparser","done parseG");
	return true;
}

private Channel extractChannel(String line) {
	
		if (line == null || line.length() == 0)
			return null;
		int pos1 = 0;
		int pos2;	
		Channel ret=new Channel();
		ret.setT(System.currentTimeMillis());
		try{
		if(line.indexOf(TOKEN_GROUP)>=0){
			//Log.v("xmlparser","TOKEN_GROUP "+line);
			/*pos1 = line.indexOf(KEY_ID + "=\"", pos1);
			if (pos1 == -1)	return null;
			pos1 += KEY_ID.length() + 2;
			pos2 = line.indexOf("\" ", pos1);
			ret.setSid(line.substring(pos1,pos2));*/
  
			pos1 = line.indexOf(KEY_TITLE + "=\"", pos1);
			if (pos1 == -1)	return null;
			pos1 += KEY_TITLE.length() + 2;
			pos2 = line.indexOf("\"", pos1);
			
			ret.setText(line.substring(pos1,pos2));
			ret.setIsGroup(true);
			parent=ret.getText();
			if(parent.toUpperCase().indexOf("WI-FI/3G")>0){
				bitrate=true;
				if(parent.indexOf(" - ")>0){
				parent=parent.substring(0,parent.indexOf(" - "));
				}else parent=parent.substring(0,parent.toUpperCase().indexOf("WI-FI/3G"));
				ret.setText(parent);
				//Log.v("xmlparser","in extractChannel Wi-fi/3G "+ret.getText());
			}else bitrate=false;
			ret.setParent(null);
			//Log.v("xmlparser","in extractChannel "+ret.getText());
		
			
		}else{
/*			pos1 = line.indexOf(KEY_ID + "=\"", pos1);
			if (pos1 == -1)	return null;
			pos1 += KEY_ID.length() + 2;
			pos2 = line.indexOf("\" ", pos1);
			
			ret.setSid(line.substring(pos1,pos2));*/
			
			ret.setParent(parent);
			ret.setBitrate(bitrate);
			//Log.v("xmlparser","gonna parse <c "+parent);
			pos1 = line.indexOf(KEY_TITLE + "=\"", 0);
			if (pos1 == -1)	return null;
			pos1 += KEY_TITLE.length() + 2;
			pos2 = line.indexOf("\" ", pos1);
			ret.setText(line.substring(pos1,pos2));
			//Log.v("xmlparser","gonna parse <c "+pos1+" 2="+pos2);
			
			pos1 = line.indexOf(KEY_URL + "=\"", pos2);
			if (pos1 == -1)	return null;
			pos1 += KEY_URL.length() + 2;
			pos2 = line.indexOf("\"", pos1);
			//Log.v("xmlparser","gonna parse2 <c "+pos1+" 2="+pos2);
			ret.setUrl(line.substring(pos1,pos2));		
			
		}
		}catch(Exception x){//Log.v("xmlparser","err in extractChannel "+line+" "+x);ret.setText("err parse"+x);
		}	
	return ret;
	}

private static String readHLine(InputStream is) throws IOException {

	byte[] b=new byte[1000];
	int ch;
	char waitFor = '\r';
	int cnt=0;
	while ((ch = is.read()) != -1) {
		if ('\r' == (char) ch) {
			waitFor = '\n';
			continue;
		} else if (waitFor == (char) ch) {
			break;
		}
		b[cnt]=(byte)ch;cnt++;
	}
	byte[] r=new byte[cnt];
	System.arraycopy(b, 0, r, cnt-2, cnt);
	return r.toString();
}

	private static String readHttpLine(InputStream is) throws IOException {
		StringBuffer sb = new StringBuffer();
		int ch;
		char waitFor = '\r';

		while ((ch = is.read()) != -1) {
			if ('\r' == (char) ch) {
				waitFor = '\n';
				continue;
			} else if (waitFor == (char) ch) {
				break;
			}
			sb.append((char) ch);
		}
		return sb.toString();
	}
	private static String readLine(InputStream is) throws IOException {
		StringBuffer sb = new StringBuffer();
		int ch;
		char waitFor = '\n';

		while ((ch = is.read()) != -1) {
			if ('\r' == (char) ch) {
				waitFor = '\n';
				continue;
			} else if (waitFor == (char) ch) {
				break;
			}
			sb.append((char) ch);
		}
		if(ch==-1)throw new IOException("EOF");
		return sb.toString();
	}

	private static Hashtable rreadHttpHeaders(InputStream is) throws IOException {
		String key;
		String val;
		int idx = -1;
		Hashtable hash = new Hashtable(10);

		String line = readHLine(is);
		while (!"".equals(line)) {
			idx = line.indexOf(':');
			key = line.substring(0, idx).trim().toLowerCase();
			val = line.substring(idx + 1).trim();
			hash.put(key, val);
			line = readHttpLine(is);
		}

		return hash;
	}

	private static Vector readHttpResponseCode(InputStream is)
			throws IOException {
		Vector ret = new Vector();
		String line = readHttpLine(is);

		int pos0 = 0;
		int pos1 = line.indexOf(' ');

		while (pos1 != -1) {
			ret.addElement(line.substring(pos0, pos1));
			pos0 = pos1 + 1;
			pos1 = line.indexOf(' ', pos0);
		}

		return ret;
	}

	/**
	 * Checks the HTTP response code contained in res and throws an exception if
	 * it isn't either a page found or a redirect.
	 * 
	 * @param res
	 * @throws IOException
	 */
	private static void checkHttpResponse(int res) throws IOException {
		if (res != 200 && res != 302) {
			throw new IOException("server response: " + res);
		}
	}

	/**
	 * Checks the HTTP response code contained in res and throws an exception if
	 * it isn't either a page found or a redirect.
	 * 
	 * @param res
	 * @throws IOException
	 */
	private static void checkHttpResponse(String res) throws IOException {
		int resInt = Integer.parseInt(res);
		checkHttpResponse(resInt);
	}


}
