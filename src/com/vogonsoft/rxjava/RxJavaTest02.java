package com.vogonsoft.rxjava;

import io.reactivex.*;
import io.reactivex.disposables.*;
import io.reactivex.functions.*;
import java.util.concurrent.*;
import io.reactivex.subjects.*;
import io.reactivex.observables.*;

public class RxJavaTest02
{
  public static void test()
  {
    System.out.println("RxJavaTest02::test");
    // test001();
    // test002();
    // test003();
    // test004();
    // test005();
    // test006();
    // test007();
    // test008();
    // test009();
    // test010();
    // test011();
    // test012();
    // test013();
    // test014();
    // test015();
    // test016();
    test017();
  }
  
  public static void test001()
  {
    System.out.println("RxJavaTest::test001");
    Observable.create(s -> {
      s.onNext("Hello you");
      s.onComplete();
    }).subscribe(msg -> System.out.println(msg));
  }

  public static void test002()
  {
    System.out.println("RxJavaTest::test002");
    Observable.create(
      s -> {
        s.onNext("Hello you");
        s.onComplete();
    }).subscribe(
      System.out::println,
      Throwable::printStackTrace,
      () -> { System.out.println("Completed"); }
    );
  }
  
  public static void test003()
  {
    System.out.println("RxJavaTest::test003");
    Observable<String> observable = Observable.create(
      s -> {
        s.onNext("Hello you");
        s.onComplete();
    });
    
    Observer<String> observer = new Observer<String>() {
      @Override
      public void onSubscribe(Disposable d)
      {
        // Here we can save reference d for later use if we want
        // to dispose it from onNext.
      }

      @Override
      public void onNext(String msg)
      {
        System.out.println(msg);
      }
      
      @Override
      public void onError(Throwable e)
      {
        e.printStackTrace();
      }
      
      @Override
      public void onComplete()
      {
        System.out.println("in onComplete");
      }
    };
    observable.subscribe(observer);
  }

  // test004:
  // Observer interface now (in RxJava 2) has a method onSubscribe, so that we can
  // unsubscribe from within onNext. Now Subscriber is not needed any more.
  // subscribe method now returns Disposable (rather than Subscription), and
  // we unsubscribe by calling dispose (rather than unsubscribe).
  public static void test004()
  {
    System.out.println("RxJavaTest::test004");
    Observable<String> observable = Observable.create(
      s -> {
        s.onNext("Hello you");
        s.onNext("Do you know Java?");
        s.onNext("This will be lost...");
        s.onComplete();
    });
    
    Observer<String> observer = new Observer<String>() {
      private Disposable d_;

      @Override
      public void onSubscribe(Disposable d)
      {
        d_ = d;
      }

      @Override
      public void onNext(String msg)
      {
        System.out.println(msg);
        if (msg.contains("Java"))
        {
          System.out.printf("Received message %s, exiting\n", msg);
          d_.dispose();
        }
      }
      
      @Override
      public void onError(Throwable e)
      {
        e.printStackTrace();
      }
      
      @Override
      public void onComplete()
      {
        System.out.println("in onComplete");
      }
    };
    observable.subscribe(observer);
  }

  // test005:
  // Testing method range
  public static void test005()
  {
    System.out.println("RxJavaTest::test005");
    Observable
      .range(5, 3)
      .subscribe(
        i -> {
          System.out.printf("Got number: %d\n", i);
        },
        Throwable::printStackTrace,
        () -> { System.out.println("Completed"); }
      );
    System.out.println("After");
  }
  
  // myRange:
  // Implementing Observable::range in terms of Observable::create
  static Observable<Integer> myRange(int from, int count)
  {
    Observable<Integer> observable = Observable.create(s -> {
      int j;
      int i;
      for (i = 0, j = from; i < count; i++, j++) {
        s.onNext(j);
      }
      s.onComplete();
    });
    return observable;
  }
  
  // test006:
  // Using myRange in place of Observable::range.
  public static void test006()
  {
    Observable<Integer> observable = myRange(5, 3);
    observable
      .subscribe(
        i -> {
          System.out.printf("Got number: %d\n", i);
        },
        Throwable::printStackTrace,
        () -> { System.out.println("Completed"); }
      );
  }
  
  // test007:
  // cache operator makes an observable that caches all values sent to onNext.
  // The lambda in the create method is called only once, when subscribe method
  // called the first time.
  public static void test007()
  {
    System.out.println("RxJavaTest::test007");
    Observable<Integer> ints = Observable.<Integer>create(s -> {
      System.out.println("Created one Observable");
      s.onNext(42);
      s.onComplete();
    })
      .cache();

    ints.subscribe(i -> System.out.printf("Next: %d\n", i));
    ints.subscribe(i -> System.out.printf("Next: %d\n", i));
  }
  
  public static void myWait(int ms)
  {
    try {
      Thread.sleep(ms);
    }
    catch (InterruptedException e) {
      System.out.println("Thread.sleep interrupted");
    }
  }
  
  public static void test008()
  {
    Observable<Integer> ints = Observable.create(
      s -> {
        Runnable r = () -> {
          int i = 0;
          while (!s.isDisposed()) {
            // Wait for 500ms.
            myWait(500);
            s.onNext(i);
            i++;
          }
        };
        new Thread(r).start();
      }
    );
    Disposable d = ints
      .subscribe(
        i -> {
          System.out.println(i);
        }
      );
    // Wait for 5s.
    myWait(5000);
    System.out.println("Out of time, disposing the subscription");
    d.dispose();
    System.out.println("Exiting");
  }

  // test009:
  // Observable starts a thread that waits for 10s and then sends onNext and
  // onComplete. However, if the subscriber is disposed, we want the thread to
  // exit immediately rather than waiting. That is accomplished by calling
  // setCancellable. This is different from how it was done in RxJava 1.
  public static void test009()
  {
    Observable<Integer> ints = Observable.create(
      s -> {
        Runnable r = () -> {
          myWait(10000); // Sleep for 10s.
          if (!s.isDisposed()) {
            s.onNext(1001);
            s.onComplete();
          }
          System.out.println("Exiting thread");
        };
        final Thread thread = new Thread(r);
        thread.start();
        s.setCancellable(thread::interrupt);
      }
    );
    Disposable d = ints
      .subscribe(
        i -> {
          System.out.println(i);
        }
      );
    // Wait for 1s.
    myWait(1000);
    System.out.println("Out of time, disposing the subscription");
    d.dispose();
    
    System.out.println("Disposed, exiting");
  }

  // myLoad:
  // Simulates loading a resource that can throw an exception.
  // if id = 1, it throws. For any other value of id it returns a string
  // "Resource #n"
  private static String myLoad(int id)
    throws Exception
  {
    if (id == 1) {
      throw new Exception("Oops! myLoad failed");
    }
    return String.format("Resource #%d", id);
  }
  
  // myRxLoad:
  // Returns Observable to get a string resource by calling myLoad. If
  // myLoad returns a string, then the Observable calls onNext with that
  // string and then onComplete. If myLoad throws an exception, the Observable
  // call onError with that exception object.
  private static Observable<String> myRxLoad(int id)
  {
/*
    return Observable.create(
      subscriber -> {
        try {
          subscriber.onNext(myLoad(id));
          subscriber.onComplete();
        } catch (Exception e) {
          subscriber.onError(e);
        }
      }
    );
*/
    // Better way:
    return Observable.fromCallable(() -> myLoad(id));
  }
  
  // test010:
  // Calls myRxLoad to create observable, then subscribes to it.
  public static void test010()
  {
    Observable<String> observable = myRxLoad(1001); // Pass 1 to cause onError.
    observable
      .subscribe(
        v -> System.out.printf("Got string: %s\n", v),
        e -> System.out.printf("Got exception: %s\n", e),
        () -> System.out.println("Completed")
      );
  }
  
  // test011:
  // Test of timer method
  public static void test011()
  {
    System.out.println("RxJavaTest::test011");
    Observable
      .timer(1, TimeUnit.SECONDS)
      .subscribe((Long zero) -> System.out.println(zero));
    
    myWait(2000);
  }
  public static void test012()
  {
    System.out.println("RxJavaTest::test012");
    Observable
      .interval(1500, TimeUnit.MILLISECONDS)
      .subscribe(
        new Observer<Long>() {
          Disposable d_;

          @Override
          public void onSubscribe(Disposable d)
          {
            d_ = d;
          }
          
          @Override
          public void onNext(Long i)
          {
            System.out.println(i);
            if (i == 5) {
              System.out.println("Disposing");
              d_.dispose();
            }
          }
          
          @Override
          public void onError(Throwable e)
          {
            e.printStackTrace();
          }
          
          @Override
          public void onComplete()
          {
            System.out.println("Complete");
          }
        }
    );
    
    myWait(10000);
  }
  
  // test013:
  // Simulate twitter stream (from Twitter4j). TwitterStreamSimulator instance
  // will send notifications to all its listeners until shut down.
  public static void test013()
  {
    TwitterStreamSimulator stream = new TwitterStreamFactorySimulator().getInstance();
    stream.addListener(new TwitterStatusListener() {
      @Override
      public void onStatus(Status status)
      {
        System.out.printf("onStatus got %s\n", status);
      }
      
      @Override
      public void onException(Exception ex)
      {
        
      }

    });
    
    stream.sample();
    myWait(5000);
    stream.shutdown();
  }
  
  public static void myConsume(
        Consumer<Status> onStatus,
        Consumer<Exception> onException) {
    TwitterStreamSimulator stream = new TwitterStreamFactorySimulator().getInstance();
    stream.addListener(new TwitterStatusListener() {
      @Override
      public void onStatus(Status status)
      {
        try {
          onStatus.accept(status);
        } catch (Exception ex) {
          // Ignore.
        }
      }
      
      @Override
      public void onException(Exception ex)
      {
        try {
          onException.accept(ex);
        } catch (Exception ex2) {
          // Ignore.
        }
      }

    });
    stream.sample();
  }  
  
  public static void test014()
  {
    myConsume(
      status -> System.out.printf("Status: %s\n", status),
      ex     -> System.out.printf("Error callback: %s\n", ex)
    );
  }
  
  // myObserve:
  // Creates Observable that streams status objects from TwitterStreamSimulator
  // to subscribers.
  public static Observable<Status> myObserve()
  {
    return Observable.create(subscriber -> {
      TwitterStreamSimulator stream =
        new TwitterStreamFactorySimulator().getInstance();
      stream.addListener(new TwitterStatusListener() {
        @Override
        public void onStatus(Status status)
        {
          subscriber.onNext(status);
        }
        
        @Override
        public void onException(Exception ex)
        {
          subscriber.onError(ex);
        }
      });
      
      subscriber.setCancellable(stream::shutdown);
      stream.sample();
    });
  }
  
  // test015:
  // Gets observable from myObserve and subscribes to it. After 5s it unsubscribes.
  public static void test015()
  {
    System.out.println("RxJavaTest::test015");
    Disposable d = myObserve().subscribe(
      status -> System.out.printf("Status: %s\n", status),
      ex     -> System.out.printf("Error callback: %s\n", ex)
    );
    System.out.println("Here");
    myWait(5000);
    System.out.println("Disposing");
    d.dispose();
  }
  
  
  // test016:
  // Uses PublishSubject, which behaves like both Observer and Observable.
  // From the implementation of TwitterStatusListener, it behaves like an
  // Observable that receives onNext and onException. On the other hand,
  // we can call subscribe on it to receive notifications.
  public static void test016()
  {
    System.out.println("RxJavaTest::test016");
    PublishSubject<Status> subject = PublishSubject.create();
    TwitterStreamSimulator stream = new TwitterStreamFactorySimulator().getInstance();
    stream.addListener(new TwitterStatusListener() {
      @Override
      public void onStatus(Status status)
      {
        subject.onNext(status);
      }
      
      @Override
      public void onException(Exception ex)
      {
        subject.onError(ex);
      }
    });
    stream.sample();
    Disposable d = subject.subscribe(
      status -> System.out.printf("Status: %s\n", status),
      ex     -> System.out.printf("Error callback: %s\n", ex)
    );
    Disposable d2 = subject.subscribe(
      status -> System.out.printf("Status (2): %s\n", status),
      ex     -> System.out.printf("Error callback (2): %s\n", ex)
    );
    myWait(5000);
    System.out.println("Disposing d");
    d.dispose();
    myWait(2000);
    System.out.println("Disposing d2");
    d2.dispose();
    stream.shutdown();
 }
 
 public static void test017()
 {
    System.out.println("RxJavaTest::test017");
    Observable<Status> lazy = myObserve().publish().refCount();
    Disposable d = lazy.subscribe(
      status -> System.out.printf("Status: %s\n", status),
      ex     -> System.out.printf("Error callback: %s\n", ex)
    );
    Disposable d2 = lazy.subscribe(
      status -> System.out.printf("Status (2): %s\n", status),
      ex     -> System.out.printf("Error callback (2): %s\n", ex)
    );
    System.out.println("Here");
    myWait(5000);
    System.out.println("Disposing d");
    d.dispose();
    myWait(2000);
    System.out.println("Disposing d2");
    d2.dispose();
 }
}