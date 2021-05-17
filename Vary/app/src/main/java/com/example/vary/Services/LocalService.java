package com.example.vary.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class LocalService extends Service
{
    public void onCreate()
    {
        super.onCreate();
        startService();
    }

    private void startService()
    {
        System.out.println("service started");
    }

    public void runTask() {
        System.out.println("task running now");
        new Timer().schedule(new mainTask(), 10000);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            System.out.println("10 seconds after clicking the button have passed");
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
    }

    public class LocalBinder extends Binder {
        public LocalService getService() {
            // Return this instance of LocalService so clients can call public methods
            return LocalService.this;
        }
    }

    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}