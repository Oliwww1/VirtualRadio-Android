package org.vradio.server;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.apache.http.util.EntityUtils;
import org.vradio.io.IcyStreamProducer;
import org.vradio.io.RadioProxyProducer;
import org.vradio.phone.Start;

import android.util.Log;

	/**
	 * Basic, yet fully functional and spec compliant, HTTP/1.1 file server.
	 * <p>
	 * Please note the purpose of this application is demonstrate the usage of HttpCore APIs.
	 * It is NOT intended to demonstrate the most efficient way of building an HTTP file server. 
	 * 
	 *
	 */
public class Httpserver {
public RequestListenerThread t=null;
public static Httpserver hts;
public static String docroot;	    
public static String port="8783";

	    public Httpserver(String path) {
	    	docroot=path;
			try {
				t = new RequestListenerThread(8783, path);	        
				t.setDaemon(true);
		        t.start();
			} catch (IOException e) {
				Log.v("httpserver","constructor "+e);
				stop();
			try {
				t = new RequestListenerThread(9347, path);	        
				t.setDaemon(true);
		        t.start();
		        port="9347";
			} catch (IOException ee) {Log.v("httpserver","constructor "+ee);}
			}
	    }
	    public void stop() {
			try {
		        t.interrupt();
		        t.serversocket.close();
		        t=null;
			} catch (Exception e) {Log.v("httpserver","stop"+e);}
	    }	    
	    static class HttpFileHandler implements HttpRequestHandler  {
	        
	        private final String docRoot;
	        
	        public HttpFileHandler(final String docRoot) {
	            super();
	            this.docRoot = docRoot;
	        }
	        
	        public void handle(
	                final HttpRequest request, 
	                final HttpResponse response,
	                final HttpContext context) throws HttpException, IOException {
	        	String target = request.getRequestLine().getUri();
	        			Log.v("httpserver","handle"+request.getRequestLine().toString());
	        			//Log.v("httpserver","handle "+request.getAllHeaders());
	        			//Log.v("httpserver","target "+target);
	        			//String range=null;
			            String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
			            
			            if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
			                throw new MethodNotSupportedException(method + " method not supported"); 
			            }
			            
		
			            if (request instanceof HttpEntityEnclosingRequest) {
			                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
			                byte[] entityContent = EntityUtils.toByteArray(entity);
			                Log.v("Incoming entity content (bytes):", " "+ entityContent.length+" "+new String(entityContent));
			            }else if(request instanceof BasicHttpRequest){
			            	/*if(((BasicHttpRequest)request).containsHeader("Range")){
			            		Header r[]=((BasicHttpRequest)request).getHeaders("Range");
			            		for(Header h:r){
			            		range=h.getValue();
			            	}
			            	//System.out.println("range="+range);
			            	Header s[]=((BasicHttpRequest)request).getAllHeaders();
			            	for(Header h:s){
			            		System.out.println("Incoming Basic Header: " + h.getName()+"="+h.getValue());
			            		
			            	}
			            	Header s[]=((BasicHttpRequest)request).getAllHeaders();
			            	for(Header h:s){
			            		System.out.println("Incoming Basic Header: " + h.getName()+"="+h.getValue());
			            		
			            	}*/
			            }
			            
			            final File file = new File(this.docRoot, URLDecoder.decode(target));
			            boolean test=false;
			            if(URLDecoder.decode(target).contains("VRADIOSTREAMER") )test=true;
			            //else if(!Start.shttp)return;
			            //Log.v("Httpserver","found file "+file.getAbsolutePath());
			            if (!file.exists() && !test) {
		
			                response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			                EntityTemplate body = new EntityTemplate(new ContentProducer() {
			                    
			                    public void writeTo(final OutputStream outstream) throws IOException {
			                        OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8"); 
			                        writer.write("<html><body><h1>");
			                        writer.write("File ");
			                        writer.write(file.getPath());
			                        writer.write(" not found");
			                        writer.write("</h1></body></html>");
			                        writer.flush();
			                    }
			                     
			                });
			                body.setContentType("text/html; charset=UTF-8");
			                response.setEntity(body);
			                //Log.v("httpserver","File " + file.getPath() + " not found");
			                
			            } else if (!test && !file.canRead() || file.isDirectory()) {
			                
			            	//Log.v("Httpserver","is directory "+file.getAbsolutePath());
			                if(file.isDirectory())response.setStatusCode(HttpStatus.SC_OK);
			                else response.setStatusCode(HttpStatus.SC_FORBIDDEN);
			                
			                EntityTemplate body = new EntityTemplate(new ContentProducer() {
			                    
			                    public void writeTo(final OutputStream outstream) throws IOException {
			                        OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8"); 
			                        writer.write("<html><body><p align='left'><image src=\"http://vradioorg.appspot.com/vr_icon_72.png\" alt=\"vstudio Fileserver\"/></p>");
			                        if(file.isDirectory()){
			                        	writer.write("<p>Local directory is: "+file.getAbsolutePath()+"<br/>Use Chrome or Firefox for best results</p>");
			                        	writer.write("<p><a href=\"http://"+getLocalIpAddress()+":"+port+file.getAbsolutePath().substring(docroot.length())+"/VRADIOSTREAMER"+System.currentTimeMillis()+".mp3\">Dock into currently playing stream: "+Start.streamSelected+"</a></p>");
			                        	for(String s:file.list()){
			                        		File fi=new File(file.getAbsolutePath()+"/"+s);
			                        		if(s.indexOf("temp.mp3")>=0 || (fi.length()==0 && fi.isFile())){}else{
				                        		if(fi.isDirectory())writer.write("<a href=\"http://"+getLocalIpAddress()+":"+port+file.getAbsolutePath().substring(docroot.length())+"/"+s+"\">"+s+"</a><br/>");
				                        		else if(s.endsWith(".mp4"))writer.write("<video src=\"http://"+getLocalIpAddress()+":"+port+file.getAbsolutePath().substring(docroot.length())+"/"+s+"\" poster=\"http://c-ninj2.ww2k.ch/ora/play.jpg?w=30\" onclick=\"this.play();\">"+s+"</video> Size:&nbsp;"+fi.length()+" Bytes <br/>");
				                        		else if(s.endsWith(".mp3"))writer.write("<p>"+s+" <a href=\"icy://"+getLocalIpAddress()+":"+port+file.getAbsolutePath().substring(docroot.length())+"/"+s+"\"><br/>Play in winamp </a>&nbsp;&nbsp;&nbsp; <a href=\"http://"+getLocalIpAddress()+":"+port+file.getAbsolutePath().substring(docroot.length())+"/"+s+"\"> In Browser/Download  </a> &nbsp;&nbsp;&nbsp; Size:&nbsp;"+fi.length()+" Bytes <br/><br/></p>");
				                        		else writer.write("<a href=\"http://"+getLocalIpAddress()+":"+port+file.getAbsolutePath().substring(docroot.length())+"/"+s+"\">"+s+"</a> Size:&nbsp;"+fi.length()+" Bytes <br/>");
			                        		}
			                        	}
			                        }else writer.write("<h1>Access denied</h1>");
			                        writer.write("</body></html>");
			                        writer.flush();
			                    }
			                    
			                });
			                body.setContentType("text/html; charset=UTF-8");
			                response.setEntity(body);
			                Log.v("httpserver","Cannot read file " + file.getPath());
			                
			            } else {
			            	Log.v("Httpserver","else.. "+ file.getPath());
			            		response.setStatusCode(HttpStatus.SC_OK);		
			            		if(method.equals("HEAD")){
			            			 if(((BasicHttpRequest)request).getHeaders("getcontentFeatures.dlna.org")!=null){
			            				 //response.setEntity(new ContentEntity(file, "audio/mpeg"));

			            			        BasicHttpEntity bod = new BasicHttpEntity(){
			    			                    
			    			                    public void writeTo(final OutputStream outstream) throws IOException {
			    			                        OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8"); 
			    			                        
			    			                        writer.write("");
			    			                        writer.flush();
			    			                    }
			    			                    
			    			                };
			    			                bod.setContentType("audio/mpeg");
			    			                //bod.setContentLength(10000000);
			    			                response.setEntity(bod); 
			            				 
			            			response.setHeader(new BasicHeader("Content-Type", "audio/mpeg"));	 
			            			//response.setHeader("Date", new Date().toGMTString());
			            			//response.setHeader("Last-Modified", new Date().toGMTString());
			            			//response.setHeader("Expires", new Date(new Date().getTime()+1000*60*60*24).toGMTString());
			            				 
			            			response.setHeader(new BasicHeader("Connection", "close"));
			            			response.setHeader(new BasicHeader("Accept-Ranges", "bytes"));
			            			//response.addHeader(new BasicHeader("Content-Length", "10000000"));
			            			//Log.v("Httpserver in HEAD ","cl="+response.getFirstHeader("Content-Length").getValue());
			            			response.setHeader(new BasicHeader("Cache-Control", "no-cache"));
			            			response.setHeader(new BasicHeader("EXT", ""));
			            			response.setHeader(new BasicHeader("contentFeatures.dlna.org", "DLNA.ORG_PN=MP3;DLNA.ORG_OP=01"));
			            			response.setHeader(new BasicHeader("transferMode.dlna.org", "Streaming"));
			            			response.setHeader(new BasicHeader("Server", "vstudio.org streaming http server v0.1"));
			            			return;
			            			 }
			            			/*
			            			Content-Type: video/x-mkv
			            			Date: Thu, 30 Jun 2011 18:07:10 GMT
			            			Last-Modified: Thu, 30 Jun 2011 18:07:10 GMT
			            			Expires: Sat, 30 Jul 2011 18:07:10 GMT
			            			Connection: close
			            			Accept-Ranges: bytes
			            			Content-Length: 16438246411
			            			EXT: 
			            			Cache-Control: no-cache
			            			contentFeatures.dlna.org: DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01500000000000000000000000000000
			            			transferMode.dlna.org: Streaming
			            			Server: Windows, UPnP/1.0 DLNADOC/1.50, Mezzmo Media Server/2.3.2.0*/
			            			 
			            		}
				                if(file.getPath().endsWith(".jpg"))response.setEntity(new FileEntity(file, "image/jpg"));
				                else if(file.getPath().endsWith(".png"))response.setEntity(new FileEntity(file, "image/png"));
				                else if(file.getPath().endsWith(".xml"))response.setEntity(new FileEntity(file, "text/xml"));
				                else if(file.getPath().endsWith(".bin"))response.setEntity(new FileEntity(file, "text/xml"));
				                else if(file.getPath().endsWith(".tsv"))response.setEntity(new FileEntity(file, "text/tab-separated-values"));
				                else if(file.getPath().endsWith(".txt"))response.setEntity(new FileEntity(file, "text/plain"));
				                else if(file.getPath().endsWith(".mp4"))response.setEntity(new FileEntity(file, "video/mp4"));
				                else if(file.getPath().endsWith(".mp5"))response.setEntity(new FileEntity(file, "audio/mpeg"));
				                else if(file.getPath().endsWith(".mp3")){
				                	//response.setEntity(new FileEntity(file, "audio/x-mp3"));
				                	//EntityTemplate body = new EntityTemplate(new IcyStreamProducer(file.getPath(),Start.getBitrate(file.getName()),0));
				                	AbstractHttpEntity body=null;
				                	
				                	try {
										if(test){
											int posfrom=file.getPath().indexOf("VRADIOSTREAMER")+14;
											int posto=file.getPath().indexOf(".mp3");
											body = new EntityTemplate(RadioProxyProducer.getPrepared(file.getPath().substring(posfrom,posto)));
										} else body=new FileEntity(file, "audio/mpeg");
										body.setContentType("audio/mpeg");
										//response.setHeader(new BasicHeader("icy-br",Start.getBitrate(file.getName())));
										//response.setHeader(new BasicHeader("icy-metaint",start.getMetaInf(file.getName())));
										response.setHeader(new BasicHeader("cache-control","no-cache"));
										//response.setHeader(new BasicHeader("MediaInfo.sec","SEC_Duration=2667000;"));
										//contentFeatures.dlna.org: DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=017000 00000000000000000000000000
										//response.setHeader(new BasicHeader("contentFeatures.dlna.org", "DLNA.ORG_PN=MP3;DLNA.ORG_CI=1;DLNA.ORG_FLAGS=01500000000000000000000000000000"));
										//response.setHeader(new BasicHeader("transferMode.dlna.org","Streaming"));
										//response.setHeader(new BasicHeader("icy-name",Start.getName(file.getName())));

										response.setHeader(new BasicHeader("Connection","keep-alive"));
										response.setHeader(new BasicHeader("Accept-Ranges","bytes"));
										response.setHeader(new BasicHeader("EXT",""));
										
										response.setHeader(new BasicHeader("realTimeInfo.dlna.org", "DLNA.ORG_TLAG=*"));
										response.setHeader(new BasicHeader("contentFeatures.dlna.org", "DLNA.ORG_PN=MP3;DLNA.ORG_OP=01"));
										response.setHeader(new BasicHeader("Server", "vstudio.org streaming http server v0.1"));
										response.setEntity(body);
									} catch (Exception e) {
										Log.v("Httpserver mp3","err="+e);
									}
					                
				                }
			                /*if(range!=null){
		                		response.setStatusCode(HttpStatus.SC_PARTIAL_CONTENT);
		                		response.setHeader("Content-Range","bytes 0-/"+file.length());
		                		response.setHeader("Content-Type","multipart/x-byterange");
			                }*/
			                //Log.v("httpserver","Serving file " + file.getPath());
			                
			            }
	        }
	        
	    }
	    
	    
	    
	    static class RequestListenerThread extends Thread {

	        private final ServerSocket serversocket;
	        private final HttpParams params; 
	        private final HttpService httpService;
	        
	        public RequestListenerThread(int port, final String docroot) throws IOException {
	            this.serversocket = new ServerSocket(port);
	            this.params = new BasicHttpParams();
	            this.params
	                .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 50000)
	                .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 16 * 1024)
	                .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
	                //.setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
	                .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/vstudio0.1");

	            // Set up the HTTP protocol processor
	            BasicHttpProcessor httpproc = new BasicHttpProcessor();
	            httpproc.addInterceptor(new ResponseDate());
	            httpproc.addInterceptor(new ResponseServer());
	            httpproc.addInterceptor(new ResponseContent());
	            //httpproc.addInterceptor(new ResponseConnControl());
	            
	            // Set up request handlers
	            HttpRequestHandlerRegistry reqistry = new HttpRequestHandlerRegistry();
	            reqistry.register("*", new HttpFileHandler(docroot));
	            
	            // Set up the HTTP service
	            this.httpService = new HttpService(
	                    httpproc, 
	                    new DefaultConnectionReuseStrategy(), 
	                    new DefaultHttpResponseFactory()
	            );
	            httpService.setHandlerResolver(reqistry);	           
	        }

	        public void run() {
	        	try {
					Log.v("httpserver","Listening on port " + this.serversocket.getLocalPort()+" "+serversocket.getInetAddress()+" "+getLocalIpAddress()+" "+InetAddress.getLocalHost().getHostAddress());
				} catch (UnknownHostException e1) {Log.v("Httpserver","unknown host"+e1);}
	            while (!Thread.interrupted()) {
	                try {
	                    // Set up HTTP connection
	                    Socket socket = this.serversocket.accept();
	                    DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
	                    Log.v("httpserver","Incoming connection from " + socket.getInetAddress());
	                    if(socket.getInetAddress().getHostAddress().equals("127.0.0.1") || socket.getInetAddress().getHostAddress().equals("/127.0.0.1") || Start.shttp || socket.getInetAddress().getHostAddress().equals(Start.getStart().avtrans_url.getHost())){
		                    conn.bind(socket, this.params);
		                    // Start worker thread
		                    Thread t = new WorkerThread(this.httpService, conn);
		                    t.setDaemon(true);
		                    t.start();
	                    }
	                } catch (InterruptedIOException ex) {
	                    break;
	                } catch (IOException e) {
	                	Log.v("httpserver","I/O error initialising connection thread: "+ e.getMessage());
	                    break;
	                }
	            }
	        }
	    }
	    
	    static class WorkerThread extends Thread {

	        private final HttpService httpservice;
	        private final HttpServerConnection conn;
	        
	        public WorkerThread(
	                final HttpService httpservice, 
	                final HttpServerConnection conn) {
	            super();
	            this.httpservice = httpservice;
	            this.conn = conn;
	        }
	        
	        public void run() {
	        	Log.v("httpserver","New connection thread");
	            HttpContext context = new BasicHttpContext(null);
	            try {
	                while (!Thread.interrupted() && this.conn.isOpen()) {
	                	Log.v("httpserver","gonna handle"+this.conn);
	                    this.httpservice.handleRequest(this.conn, context);
	                }
	            } catch (ConnectionClosedException ex) {
	                System.err.println("Client closed connection");
	            } catch (IOException ex) {
	                Log.v("Httpserver","I/O error: " + ex.getMessage()+" "+ex.getStackTrace().toString());
	               // ex.printStackTrace();
	            } catch (HttpException ex) {
	                System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage());
	            } finally {
	                try {
	                    this.conn.shutdown();
	                } catch (IOException ignore) {}
	            }
	        }
	    }
	    
	    public static String getLocalIpAddress() {
	        try {
	            return Start.actualIpAddress;
	        } catch (Exception ex) {
	            Log.e("httpserver", "getLocalIpAddress "+ex.toString());
	        }
	        return null;
	    }   
	}
