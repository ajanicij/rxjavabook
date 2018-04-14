package com.vogonsoft.rxjava;

public class Transaction
{
  private int type;
  private int value;
  private int id;
  
  private static int nextId = 1;
  
  public static final int GROCERY = 10;
  public static final int BANKING = 11;

  public Transaction(int type, int value)
  {
    this.type = type;
    this.value = value;
    this.id = nextId;
    nextId++;
  }
  
  public int getValue()
  {
    return value;
  }
  
  public int getType()
  {
    return type;
  }
  
  public int getId()
  {
    return id;
  }
}
