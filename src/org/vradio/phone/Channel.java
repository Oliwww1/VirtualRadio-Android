package org.vradio.phone;






public class Channel {

	
	private Long id= 111L;
	 
	
	private String sid;


	private Long t;
	

	private String text;
	

	private String url;
	
	
	private String parent;	
	
	private boolean bitrate;
	
	
	private boolean isGroup=false;
	
    public Channel(String name, String url2, String t) {
		text=name;
		url=url2;
		parent=t;
	}
    public Channel() {
	}
	public Long getId() {
        return id;
    }
	
	public String getSid(){return sid;}
	public String getText(){return text;}
	public String getUrl(){return url;}
	public String getParent(){return parent;}
	public boolean getIsGroup(){return isGroup;}
	public Long getT(){return t;}
	public boolean getBitrate(){return bitrate;}
	
	public void setBitrate(boolean b){bitrate=b;}
	public void setSid(String s){sid=s;}
	public void setText(String s){text=s;}
	public void setUrl(String s){url=s;}
	public void setParent(String s){parent=s;}
	public void setIsGroup(boolean s){isGroup=s;}
	public void setT(Long t){this.t=t;}
}



