package com.example.vary.Network;

public class LoadStatus {
    private Throwable error;
    private boolean success;

    public LoadStatus(Throwable e) {
        if (e == null) {
            success = true;
        }
        else {
            error = e;
            success = false;
        }
    }

    public Throwable getError()
    {
        return error;
    }
}
