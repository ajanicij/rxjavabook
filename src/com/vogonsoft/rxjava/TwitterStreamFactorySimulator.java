package com.vogonsoft.rxjava;

public class TwitterStreamFactorySimulator
{
  TwitterStreamFactorySimulator()
  {
    
  }
  
  TwitterStreamSimulator getInstance()
  {
    System.out.println("TwitterStreamSimulator::getInstance");
    return new TwitterStreamSimulator();
  }
}
