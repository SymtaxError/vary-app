package com.example.vary.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.example.vary.Models.CurrentGameModel;
import com.example.vary.ViewModels.CardsViewModel;

import java.util.Timer;
import java.util.TimerTask;

public class LocalService extends Service {
    private CardsViewModel viewModel;
    private int localTimer;
    private final Timer timer = new Timer();

    public void setViewModel(CardsViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void onCreate() {
        super.onCreate();
        startService();
    }

    private void startService() {
        System.out.println("service started");
    }

    public void runTask(int time) {
        System.out.println("task running now");
        localTimer = time;
        viewModel.setTimerCount(localTimer);
        timer.schedule(new changeTimerInView(), 0, 1000);
    }

    public void stopTast() {
        timer.cancel();
    }

    private class changeTimerInView extends TimerTask {
        public void run() {
            localTimer--;
            viewModel.setTimerCount(localTimer);
            if (localTimer == 0) {
                timer.cancel();
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
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