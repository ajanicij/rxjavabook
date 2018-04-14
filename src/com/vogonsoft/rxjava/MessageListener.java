package com.vogonsoft.rxjava;

public interface MessageListener<T>
{
  void onMessage(T message);
}
