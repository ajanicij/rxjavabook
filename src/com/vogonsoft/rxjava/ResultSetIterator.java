package com.vogonsoft.rxjava;

import java.util.*;

class ResultSetIterator implements Iterator<Object[]>
{
  private final ResultSet rs;
  
  public ResultSetIterator(ResultSet rs)
  {
    this.rs = rs;
  }
  
  @Override
  public boolean hasNext()
  {
    return !rs.isLast();
  }
  
  @Override
  public Object[] next()
  {
    rs.next();
    return rs.toArray();
  }
  
  public static Iterable<Object[]> iterable(final ResultSet rs)
  {
    return new Iterable<Object[]>() {
      @Override
      public Iterator<Object[]> iterator()
      {
        return new ResultSetIterator(rs);
      }
    };
  }
}