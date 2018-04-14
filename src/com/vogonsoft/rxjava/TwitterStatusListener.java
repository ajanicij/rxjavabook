package com.vogonsoft.rxjava;

public interface TwitterStatusListener
{
    void onStatus(Status status);
    void onException(Exception ex);
}
