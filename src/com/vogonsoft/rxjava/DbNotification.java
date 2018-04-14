package com.vogonsoft.rxjava;

class DbNotification
{
  private String message;
  
  public DbNotification(String message)
  {
    this.message = message;
  }
  
  public String getMessage()
  {
    return message;
  }
}
