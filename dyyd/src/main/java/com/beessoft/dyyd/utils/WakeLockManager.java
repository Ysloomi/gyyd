package com.beessoft.dyyd.utils;




import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;


public class WakeLockManager {
    private Context mContext;
    private WakeLock mWakeLock;
    private String mTag;
    private static final Object LOCK = new Object();
    private static WakeLockManager theManager;

    public WakeLockManager(Context context, String tag) {
        mTag = tag;
        mContext = context;
    }


    /**
     * Acquire a wake lock if we don't already have one.
     *
     * If we already have a lock, keeps the one we have.
     */
    public void acquireWakeLock() {
        if(mWakeLock == null) {
            PowerManager manager = (PowerManager) mContext.getSystemService(
                    Context.POWER_SERVICE);
            mWakeLock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, mTag);
            mWakeLock.acquire();
            Log.d(mTag, "Wake lock acquired");
        } else {
            Log.d(mTag, "Already have a wake lock -- keeping it");
        }
    }


    /**
     * If we have an active wake lock, release it.
     */
    public void releaseWakeLock() {
        if(mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
            Log.d(mTag, "Wake lock released");
        }
    }


    /**
      * Returns the wake lock manager.
      */
     public static WakeLockManager getInstance(Context context) {
       synchronized (LOCK) {
         if (theManager == null) {
           theManager = new WakeLockManager(context, null);
        }
          return theManager;
        }
      }
}

