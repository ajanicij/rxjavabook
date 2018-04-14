package com.vogonsoft.rxjava;

import java.util.Random;

public class MessageSource implements Runnable
{
  private Thread thread;
  private MessageListener<String> listener;

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

  public MessageSource()
  {
    thread = new Thread(this);
  }
  
  @Override
  public void run()
  {
    String[] names = { "John", "George", "Paul", "Ringo" };
    String[] says = { "Hi", "Bye", "Hello", "Goodbye", "Yo!", "cya", "And yet", "How so?", "Ahem..." };
    Random rnd = new Random();

    while (true)
    {
      if (!myWait(500))
      {
        System.out.println("MessageSource thread exiting!");
        return;
      }
      if (listener != null)
      {
        String name = names[rnd.nextInt(names.length)];
        String saying = says[rnd.nextInt(says.length)];
        String message = String.format("%s says %s", name, saying);
        listener.onMessage(message);
      }
    }
  }
  
  public void setListener(MessageListener<String> listener)
  {
    this.listener = listener;
  }
  
  public void start()
  {
    thread.start();
  }
  
  public void stop()
  {
    thread.interrupt();
  }
}
