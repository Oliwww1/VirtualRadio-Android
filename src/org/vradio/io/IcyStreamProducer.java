package org.vradio.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

import org.apache.http.entity.ContentProducer;

import android.util.Log;

public class IcyStreamProducer implements ContentProducer{
	public String path;
	public int bitrate;
	public int offset;
	RandomAccessFile m_file1;
	public long m_stime,m_current_write_pos;
	public boolean m_done;
	
	public IcyStreamProducer(String path, String bitr, int offset){
		super();
		this.offset=offset;this.path=path;
		bitrate=(Integer.parseInt(bitr))/7;
		try {
			m_file1 = new RandomAccessFile(path,"rwd");
			m_file1.seek(offset);
		} catch (Exception e) {
			Log.v("ShoutcastFile","random acc file err 1"+e);
		}
	}
	public void writeTo(final OutputStream outstream) throws IOException {
		FileInputStream input = new FileInputStream(m_file1.getFD());
		m_stime=System.currentTimeMillis();
		m_current_write_pos=0;
		byte[] buffer = new byte[1024*4];
		int numRead=0,bRead=0;
		while ((bRead = input.read(buffer)) != -1 && !m_done) {
			outstream.write(buffer, 0, bRead);
			numRead++;m_current_write_pos++;
			if(numRead>=bitrate){
				long sleeptime=1000-(System.currentTimeMillis()-m_stime);
				//Log.v("IcyStreamProducer","before sleep:"+sleeptime+" nr:"+numRead);
				try {
					if(m_current_write_pos>3)Thread.sleep(sleeptime);
				} catch (Exception e) {}
				
				Log.v("IcyStreamProducer","after sleep:"+sleeptime+" nr:"+numRead+" "+m_current_write_pos);
				numRead=0;
				m_stime=System.currentTimeMillis();
			}
			//Log.v("ShoutcastFile","buf="+m_current_write_pos);
		}
		Log.v(".............IcyStreamProducer........","........end of file "+path);
    }

}
