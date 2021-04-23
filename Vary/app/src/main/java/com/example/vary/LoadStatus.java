package com.example.vary;

public class LoadStatus {
    Throwable error;

    public LoadStatus(Throwable e)
    {
        error = e;
    }

    public Throwable getError()
    {
        return error;
    }
}
