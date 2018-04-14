/*
Experiments with generics and wildcard types
*/
package com.vogonsoft.rxjava;

public class PairTest
{
  // printBuddies:
  // Wildcard type <? extends Employee> can be used to call p.getFirst() and p.getSecond()
  // and assign the returned value to a reference to Employee.
  public static void printBuddies(Pair<? extends Employee, ? extends Employee> p)
  {
    Employee first = p.getFirst();
    Employee second = p.getSecond();
    System.out.println(first.getName() + " and " + second.getName() + " are buddies.");
  }
  
  public static void test01()
  {
    Pair<Manager, Manager> pair1 = new Pair<>(new Manager("John"), new Manager("George"));
    printBuddies(pair1);
  }
  
  public static void setFirst(Pair<Employee, Employee> employeePair, Manager manager)
  {
    employeePair.setFirst(manager);
  }
}
