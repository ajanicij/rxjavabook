package com.vogonsoft.rxjava;

public class LicencePlate
{
  private String licence_;
  
  public LicencePlate(String licence)
  {
    licence_ = licence;
  }
  
  @Override
  public String toString()
  {
    return licence_;
  }
}
