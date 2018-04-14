package com.vogonsoft.rxjava;

import java.util.*;
import java.util.concurrent.*;

public class TwitterStreamSimulator implements Runnable
{
  private Thread thread_;
  private List<TwitterStatusListener> listeners_;
  private Semaphore semaphore_;
  volatile boolean finished_;

  public static void myWait(int ms)
  {
    try {
      Thread.sleep(ms);
    }
    catch (InterruptedException e) {
      System.out.println("Thread.sleep interrupted");
    }
  }

  public TwitterStreamSimulator()
  {
    System.out.println("In TwitterStreamSimulator#TwitterStreamSimulator");
    listeners_ = new ArrayList<TwitterStatusListener>();
    semaphore_ = new Semaphore(1);
    finished_ = false;
  }
  
  public void addListener(TwitterStatusListener listener)
  {
    try {
      semaphore_.acquire();
      listeners_.add(listener);
    } catch (InterruptedException ex)
    {
      // Ignore.
    }
    finally {
      // mutex release.
      semaphore_.release();
    }
  }
  
  public void sample()
  {
    thread_ = new Thread(this);
    thread_.start();
  }
  
  public void shutdown()
  {
    finished_ = true;
  }
  
  public void run()
  {
    int i = 0;
    System.out.println("TwitterStreamSimulator thread starting");
    
    while (true) {
      myWait(500);
      if (finished_)
      {
        System.out.println("TwitterStreamSimulator thread interrupted, exiting");
        return;
      }
      i++;
      Status status = new Status(String.format("Message #%d", i));
      try {
        semaphore_.acquire();
        int j;
        for (TwitterStatusListener listener: listeners_) {
          listener.onStatus(status);
        }
      } catch (InterruptedException ex) {
        // Ignore.
      } finally {
        semaphore_.release();
      }
    }
  }
}
