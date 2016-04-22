package com.beessoft.dyyd.utils;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;



public class DemoIntentService extends IntentService {
    
    public DemoIntentService (String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        WakeLockManager lock = WakeLockManager.getInstance(this);
        lock.releaseWakeLock();
        super.onDestroy();
    }
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    @SuppressLint("NewApi")
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return (START_REDELIVER_INTENT);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        onHandleIntent(intent);
    }
}
