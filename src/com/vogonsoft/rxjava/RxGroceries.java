package com.vogonsoft.rxjava;

import java.math.*;
import io.reactivex.*;

public class RxGroceries
{
  public static void log(Object label)
  {
    System.out.println(
      Driver.currentTime() - Driver.getStartTime() + "\t| " +
      Thread.currentThread().getName()   + "\t| " +
      label
    );
  }

  public static void myWait(int ms)
  {
    try {
      Thread.sleep(ms);
    }
    catch (InterruptedException e) {
      System.out.println("Thread.sleep interrupted");
    }
  }

  Observable<BigDecimal> purchase(String productName, int quantity)
  {
    return Observable.fromCallable(() ->
      doPurchase(productName, quantity)
    );
  }
  
  BigDecimal doPurchase(String productName, int quantity)
  {
    log("Purchasing " + quantity + " " + productName);
    myWait(1000);
    BigDecimal priceForProduct = new BigDecimal(10.55);
    log("Done " + quantity + " " + productName);
    return priceForProduct;
  }
}
