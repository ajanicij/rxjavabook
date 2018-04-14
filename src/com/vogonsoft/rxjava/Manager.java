package com.vogonsoft.rxjava;

public class Manager extends Employee
{
  public Manager(String name)
  {
    super(name);
  }
  
  // This method is an artificial method that exists purely so we have
  // a method implemented in Manager class but not in Employee class.
  public boolean manages(Employee e)
  {
    return false;
  }
}
