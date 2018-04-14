package com.vogonsoft.rxjava;

import java.util.Random;
import java.util.ArrayDeque;
import java.util.concurrent.*;

class DbConnection implements Runnable
{
  Thread thread;
  Random random;
  int count;
  ArrayDeque<DbNotification> queue;
  Semaphore mutex;
  
  public DbConnection()
  {
    random = new Random();
    count = 1;
    queue = new ArrayDeque<DbNotification>();
    mutex = new Semaphore(1);
  }
  
  public static boolean myWait(int ms)
  {
    try {
      Thread.sleep(ms);
      return true;
    }
    catch (InterruptedException e) {
      System.out.println("Thread.sleep interrupted");
      return false;
    }
  }

  public void start()
  {
    thread = new Thread(this);
    thread.start();
  }
  
  public void stop()
  {
    thread.interrupt();
  }
  
  @Override
  public void run()
  {
    while (true) {
      if (!myWait(1000)) {
        return;
      }
      int n = random.nextInt(4);
      int i;
      try {
        mutex.acquire();
        System.out.printf("Sending %d notifications\n", n);
        for (i = 0; i < n; i++)
        {
          String msg = String.format("message #%d", count);
          count++;
          // append message to the queue.
          DbNotification notification = new DbNotification(msg);
          queue.add(notification);
          System.out.printf("  - Sending notification %s\n", notification.getMessage());
        }
        mutex.release();
      } catch (InterruptedException ex) {
        // Ignore.
      }
    }
  }
  
  public DbNotification[] getNotifications()
  {
    try {
      mutex.acquire();
      if (queue.isEmpty()) {
        mutex.release();
        return null;
      }
      int size = queue.size();
      DbNotification[] notifications = new DbNotification[size];
      int i;
      for (i = 0; i < size; i++) {
        notifications[i] = queue.remove();
      }
      mutex.release();
      return notifications;
    } catch (InterruptedException ex) {
      // Ignore.
      return null;
    }

  }
}
