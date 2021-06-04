package com.vary.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.vary.ViewModels.CardsViewModel;

import java.util.Timer;
import java.util.TimerTask;

public class LocalService extends Service {
    private CardsViewModel viewModel;
    private int localTimer;
    private Timer timer;

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
        timer = new Timer();
        localTimer = time * 100;
        viewModel.setTimerCount(time);
        timer.schedule(new changeTimerInView(), 0, 10);
    }

    public void resumeTask() {
        if (timer != null) {
            int timeLeft = (localTimer % 100 > 50) ? (localTimer / 100 + 1) : (localTimer / 100);
            localTimer = timeLeft * 100;
            viewModel.setTimerCount(timeLeft);
            timer.schedule(new changeTimerInView(), 0, 10);
        }
    }


    public void pauseTask() {
        if (timer != null)
            timer.cancel();
        timer = new Timer();
    }

    public void stopTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private class changeTimerInView extends TimerTask {
        public void run() {
            if (localTimer > 0) {
                localTimer--;
                if (localTimer % 100 == 0) {
                    viewModel.setTimerCount(localTimer / 100);
                }
                if (localTimer == 0 && timer != null) {
                        timer.cancel();
                }
            }
        }
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