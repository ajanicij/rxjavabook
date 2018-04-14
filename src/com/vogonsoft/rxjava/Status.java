package com.vogonsoft.rxjava;

public class Status
{
  private String msg_;

  Status(String msg)
  {
    msg_ = msg;
  }
  
  public String toString()
  {
    return msg_;
  }
}
