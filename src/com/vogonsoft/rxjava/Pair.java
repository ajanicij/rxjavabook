package com.vogonsoft.rxjava;

public class Pair<R, T>
{
  private R r_;
  private T t_;

  public static <R, T> Pair<R, T> of(R r, T t)
  {
    return new Pair<R, T>(r, t);
  }
  
  public Pair(R r, T t)
  {
    r_ = r;
    t_ = t;
  }
  
  public R getLeft()
  {
    return r_;
  }

  public R getFirst()
  {
    return r_;
  }
  
  public void setFirst(R first)
  {
    r_ = first;
  }
  
  public T getRight()
  {
    return t_;
  }
  
  public T getSecond()
  {
    return t_;
  }
  
  public void setSecond(T second)
  {
    t_ = second;
  }
}
