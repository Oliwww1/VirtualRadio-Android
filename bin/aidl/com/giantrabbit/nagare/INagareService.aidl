package com.giantrabbit.nagare;

interface INagareService
{
	void download(String url);
	String errors();
	String file_name();
	long position();
	int positionT();
	int duration();
	void stop();
	void seek(int i);
	void pause();
	void resume();
	void play(String uri);
	int state();
}