package com.example.testtraking;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

public class TrackingService extends IntentService{
	
	//actions
	public String ACTION_RUN_ISERVICE = "run";
	
	
	public TrackingService() {
		super("TrackingService");
	}
	
	@Override
    public void onDestroy() {
        
    }

    @Override
    public IBinder onBind(Intent intent) {
    	return null;
    }
    
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_RUN_ISERVICE.equals(action)) {
            	Tracking tracking = new Tracking(TrackingService.this, 15000, 0);
				tracking.runLocationTracking();
            }
        }
    }

}
