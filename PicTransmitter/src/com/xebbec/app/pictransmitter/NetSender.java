package com.xebbec.app.pictransmitter;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.ContentResolver;
import android.net.Uri;

public class NetSender implements Runnable {
	private static NetSender instance = null;
	public static NetSender ins()
	{
		if( instance == null )
			instance = new NetSender();
		return instance;
	}
	
	private final String SERVER_IP = "192.168.1.98";
	private final int SERVER_PORT = 8412;
	
	private ConcurrentLinkedQueue<Uri> taskQueue;
	private ContentResolver contentResolver;
	private int pendingCount;
	
	private NetSender() {
		taskQueue = new ConcurrentLinkedQueue<Uri>();
		pendingCount = 0;
	}
	
	public void addTask( Uri uri ) {
		synchronized(this)
		{
			pendingCount += 1;
		}
		taskQueue.add( uri );
	}
	
	public boolean isTaskDone() {
		synchronized(this)
		{
			return pendingCount == 0;
		}
	}
	
	public void setContentResolver( ContentResolver cr ) {
		contentResolver = cr;
	}

	private void sendImage( Uri imageUri ) {
		try {
			InputStream input = contentResolver.openInputStream(imageUri);
			
			Socket s = new Socket(SERVER_IP, SERVER_PORT);
			
			OutputStream output = s.getOutputStream();
			
		    byte[] buffer = new byte[1024]; // Adjust if you want
		    int bytesRead;
		    while ((bytesRead = input.read(buffer)) != -1)
		    {
		        output.write(buffer, 0, bytesRead);
		    }
		    output.flush();

	        //close connection
	        s.close();
	        
			synchronized(this)
			{
				pendingCount -= 1;
			}
	    } catch (UnknownHostException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
	
	private void mainloop() {
		while(true) {
			Uri uri = taskQueue.poll();
			if( uri == null )
			{
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
			else sendImage( uri );
		}
	}

	public void run() {
		mainloop();
	}

}
