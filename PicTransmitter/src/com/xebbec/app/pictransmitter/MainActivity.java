package com.xebbec.app.pictransmitter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        final NetSender sender = NetSender.ins();
        sender.setContentResolver( getContentResolver() );
        
        Thread netThread = new Thread(sender);
        netThread.setDaemon(true);
        netThread.start();
        
        final Intent intent = getIntent();
        if( intent != null && intent.getAction().equals( Intent.ACTION_SEND) )
        {	
    		new AlertDialog.Builder(this)
    		.setIcon(android.R.drawable.ic_dialog_alert)
    		.setTitle(R.string.receive_title)
    		.setMessage(R.string.receive_message)
    		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
    				Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
	    		    if (imageUri != null) {
	    		    	Log.d("pt", imageUri.getPath());
	    		    	sender.addTask( imageUri );
	    		    	while( !sender.isTaskDone());
	    		    	MainActivity.this.finish();
	    		    }
    			}
    
    		})
    		.setNegativeButton(R.string.no, null)
    		.show();
        }
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
}
