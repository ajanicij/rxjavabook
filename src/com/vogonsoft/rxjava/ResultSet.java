package com.vogonsoft.rxjava;

// import java.util.Iterator;
import java.util.*;

class ResultSet
{
  private Object[][] people;
  private Object[] current;
  int index;

  public ResultSet()
  {
    Object[][] people = {
      {"George Washington", 1732},
      {"John Adams",        1735},
      {"Thomas Jefferson",  1743},
      {"James Madison",     1751}
    };
    this.people = people;
    index = 0;
  }
  
  public boolean isLast()
  {
    return index == people.length;
  }
  
  public Object[] next()
  {
    current = people[index];
    index = index + 1;
    return current;
  }
  
  public Object[] toArray()
  {
    return current;
  }
  
  public void close()
  {
    System.out.println("ResultSet::close");
  }
}
