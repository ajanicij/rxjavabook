package com.vogonsoft.rxjava;

import com.vogonsoft.rxjava.*;
import org.apache.log4j.Logger;

public class Driver
{
  static Logger logger = Logger.getLogger(Driver.class);
  static long startTime;

  public static void main(String[] args)
  {
    startTime = currentTime();
    System.out.println("Driver::main");
    // logger.info("Hello World");
    // RxJavaTest02.test();
    // RxJavaTest03.test();
    // RxJavaTest04.test();
    // RxJavaTest05.test();
    // RxJavaTest06.test();
    RxJavaTest07.test();
  }
  
  public static long currentTime()
  {
    return System.currentTimeMillis();
  }
  
  public static long getStartTime()
  {
    return startTime;
  }
}
