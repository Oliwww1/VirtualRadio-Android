package org.vradio.phone;
public class Song {

	
	private Long id= 113L;
	 
	
	private String artist;


	private Long t;
	

	private String title;
	

	private int bitrate;
	
	
	private String parent;	
	

	
	

	
    public Song(String artist, String title, String p, int br) {
		this.artist=artist;
		this.title=title;
		bitrate=br;
		t=System.currentTimeMillis();
		this.parent=p;
	}
    public Song() {
	}
	public Long getId() {
        return id;
    }
	
	public String getArtist(){return artist;}
	public String getTitle(){return title;}
	public int getBitrate(){return bitrate;}
	public String getParent(){return parent;}

	public Long getT(){return t;}
	
	
}



