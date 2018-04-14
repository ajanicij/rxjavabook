package com.vogonsoft.rxjava;

import java.time.LocalDate;
import io.reactivex.*;
import java.util.concurrent.TimeUnit;

public class MyServiceWithTimeout implements MyService
{
  private final MyService delegate;
  private final Scheduler scheduler;

  public MyServiceWithTimeout(MyService d, Scheduler s)
  {
    delegate = d;
    scheduler = s;
  }
  
  public Observable<LocalDate> externalCall()
  {
    return delegate
      .externalCall()
      .timeout(1L, TimeUnit.SECONDS,
        scheduler,
        Observable.empty());
  }
}
