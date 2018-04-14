package com.vogonsoft.rxjava;

import java.util.concurrent.*;

public class TravelAgency
{
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

  public TravelAgency()
  {
  }

  public static String mySearch(User user, GeoLocation location)
  {
    myWait(1000);
    return "some-flight";
  }
  
  public static CompletableFuture<String> mySearchAsync(User user, GeoLocation location)
  {
    return CompletableFuture.supplyAsync(() -> mySearch(user, location));
  }
  
  public static String myBook(String flight)
  {
    myWait(1000);
    return "some-ticket";
  }
  
  public static CompletableFuture<String> myBookAsync(String flight)
  {
    return CompletableFuture.supplyAsync(() -> myBook(flight));
  }
}
