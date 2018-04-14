import oata.MyFunc;

public class HelloWorldTest extends junit.framework.TestCase
{
  public void testNothing()
  {
  }
  
  public void testWillAlwaysFail()
  {
    fail("An error message");
  }
  
  public void testMyFunc()
  {
    assertEquals(5, MyFunc.func(2, 3));
  }
}
