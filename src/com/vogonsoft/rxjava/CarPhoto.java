package com.vogonsoft.rxjava;

public class CarPhoto
{
  private final String name_;
  
  public CarPhoto(String name)
  {
    name_ = name;
  }
  
  @Override
  public String toString()
  {
    return name_;
  }
}
