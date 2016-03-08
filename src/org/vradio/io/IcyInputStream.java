package org.vradio.io;

	import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import org.vradio.phone.Start;
import org.vradio.phone.iUiListener;

import android.util.Log;



	public class IcyInputStream extends InputStream {

		private static final String STR_STREAM_TITLE = "StreamTitle='";

		private static final String STR_STREAM_URL = "StreamUrl='";

		private InputStream fInputStream;

		private int fIcyMetaInt = -1;

		private int fBytesToRead;

		private String fStreamTitle = "";

		protected String fStreamUrl = "";

		private String fArtist = "";

		private String fTitle = "";

		private iUiListener fListener;

		public IcyInputStream(iUiListener listener, InputStream is, int metaInt) {
			super();
			fListener = listener;
			fInputStream = is;
			fIcyMetaInt = metaInt;
			fBytesToRead = fIcyMetaInt;
			Log.v("IcyInputStream","-----start="+metaInt);
		}

		public String getArtist() {
			return fArtist;
		}

		public String getTitle() {
			return fTitle;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.InputStream#close()
		 */
		public void close() throws IOException {
			fInputStream.close();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.InputStream#read()
		 */
		public int read() throws IOException {
			if (fIcyMetaInt != -1) {
				if (fBytesToRead == 0) {
					readMetaData();
				} else {
					fBytesToRead--;
				}
			}
			return fInputStream.read();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.InputStream#read(byte[], int, int)
		 */
		public int read(byte[] buffer, int off, int len) throws IOException {
			int bytesRead = 0;

			while (bytesRead < len) {
				int readLen = len - bytesRead;
				if (fIcyMetaInt != -1) {
					if (fBytesToRead == 0) {
						readMetaData();
					}
					if (readLen > fBytesToRead)
						readLen = fBytesToRead;
				}
				int b = fInputStream.read(buffer, off + bytesRead, readLen);
				if (b == -1)
					throw new IOException("EOF reached");
				bytesRead += b;
				if (fIcyMetaInt != -1)
					fBytesToRead -= b;
			}

			return bytesRead;
		}

		private int readMetaData() throws IOException {
			int size = fInputStream.read() * 16;
			int bytesRead = 0;

			//Log.v("readMetaData","size=" + size);

			if (size > 0) {
				byte buf[] = new byte[size];
				while (bytesRead < size) {
					int b = fInputStream.read(buf, bytesRead, size - bytesRead);
					if (b == -1)
						throw new IOException("EOF reached");
					bytesRead += b;
				}
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < size; i++) {
					if (buf[i] == 0)
						break;
					char c = (char) buf[i];
					sb.append(c);
				}

				fStreamTitle = fStreamUrl = fArtist = fTitle = "";
				Log.v("icyinputstream","sb="+sb.toString());
				Vector fields = strToVector(sb.toString(), ";");
				for (int i = 0; i < fields.size(); i++) {
					String field = ((String) fields.elementAt(i)).trim();
					if (field.startsWith(STR_STREAM_TITLE)) {
						fStreamTitle = field.substring(STR_STREAM_TITLE.length(),
								field.length() - 1);
						Vector subFields = strToVector(fStreamTitle, "-");
						if (subFields.size() >= 2) {
							fArtist = (String) subFields.elementAt(0);
							fTitle = (String) subFields.elementAt(1);
						}else{
							fArtist = fStreamTitle;
							fTitle = "";
						}
					} else if (field.startsWith(STR_STREAM_URL)) {
						fStreamUrl = field.substring(STR_STREAM_URL.length(), field
								.length() - 1);
					}
				}
				if(!fArtist.equals("")){
					Log.v("VRadio Meta: "," " + size+ fArtist + " - Title: " + fTitle);
				
//	"fields:" + fields + 			System.err.println("Artist: " + fArtist + " - Title: " + fTitle);
				if (fListener != null)
					fListener.event(this, Start.event_icy, fArtist+" "+fTitle);
			}}
			fBytesToRead = fIcyMetaInt;
			return bytesRead + 1;
		}
		
		public static Vector strToVector(String str, String sep) {
			Vector v = new Vector();

			int pos = 0;
			int startPos = 0;
			int endPos = 0;

			while (pos != -1) {
				pos = str.indexOf(sep, startPos);
				if (pos == -1)
					endPos = str.length();
				else
					endPos = pos;

				if (endPos != startPos) {
					String token = str.substring(startPos, endPos);
					v.addElement(token.trim());
				}
				startPos = pos + sep.length();
			}

			return v;
		}
		
	}

