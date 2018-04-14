package com.vogonsoft.rxjava;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

class TestRunner
{
  public static void main(String[] args)
  {
    System.out.println("Hello TestRunner");
    Result result = JUnitCore.runClasses(RxJavaTest07.class);
    for (Failure failure : result.getFailures()) {
      System.out.println(failure.toString());
    }
  }
}
