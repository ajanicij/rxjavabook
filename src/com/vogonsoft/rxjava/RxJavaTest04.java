package com.vogonsoft.rxjava;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import io.reactivex.*;
import io.reactivex.schedulers.*;
import java.time.*;
import java.util.concurrent.*;
import io.reactivex.disposables.*;
import com.google.common.util.concurrent.*;
import java.math.*;
import io.reactivex.observables.*;
import java.util.UUID;

public class RxJavaTest04
{
  public static void test()
  {
    System.out.println("RxJavaTest04::test");
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
    // test017();
    // test018();
    test019();
  }
  
  public static void log(Object label)
  {
    System.out.println(
      Driver.currentTime() - Driver.getStartTime() + "\t| " +
      Thread.currentThread().getName()   + "\t| " +
      label
    );
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
  
  public static void myGetId()
  {
    System.out.println("Current thread's ID: " + Thread.currentThread().getId());
  }

  public static /* List<String> */ Observable<String> myListPeople()
  {
    ArrayList<String> myList = new ArrayList<String>(Arrays.asList("John", "George", "Paul", "Ringo"));
    // return myList;
    return Observable.fromIterable(myList);
  }
  
  // test01:
  // Convert Observable to Iterable.
  // Single::blockingGet blocks until it receives event, and then returns the received
  // data.
  public static void test001()
  {
    System.out.println("RxJavaTest04::test001");
    Observable<String> peopleStream = myListPeople();
    Single<List<String>> peopleList = peopleStream.toList();
    List<String> people = peopleList.blockingGet();
    for (String person : people) {
      System.out.println(person);
    }
  }

  public static Observable<Book> myRecommend(String person)
  {
    if (person.equals("John"))
    {
      return Observable.just(new Book("War and Peace"));
    }
    return Observable.error(new Exception("ouch"));
  }
  
  public static Observable<Book> myBestSeller()
  {
    return Observable.just(new Book("Cujo"));
  }

  // test002:
  // Using onErrorResumeNext
  public static void test002()
  {
    System.out.println("RxJavaTest04::test002");
    Observable<Book> recommended = myRecommend("George");
    Observable<Book> bestSeller = myBestSeller();
    Observable<Book> book = recommended.onErrorResumeNext(bestSeller);
    book
      .map(Book::getTitle)
      .subscribe(
        System.out::println,
        (ex) -> {
          System.out.println("Exception caught! " + ex.toString());
          ex.printStackTrace();
        },
        () -> System.out.println("Done")
      );
  }
  
  // myListPeople2:
  // For input n returns a list of people (names).
  // This function is used in test003 to construct a lazy observable that would work
  // infinitely if a downstream operator was not takeWhile(list -> !list.isEmpty()).
  // myListPeople2 returns an empty list for n=3, which is when the process is stopped.
  public static List<String> myListPeople2(int n)
  {
    // ArrayList<String> myList = new ArrayList<String>();
    switch (n)
    {
      case 0:
        return new ArrayList<String>(Arrays.asList("Peter", "Magnus", "Bengt", "Lars-Olof", "Nina"));
      case 1:
        return new ArrayList<String>(Arrays.asList("John", "George", "Paul", "Ringo"));
      case 2:
        return new ArrayList<String>(Arrays.asList("Dolores", "Noel", "Mike", "Fergal"));
      case 3:
        return new ArrayList<String>();
      case 4:
        return new ArrayList<String>(Arrays.asList("Dolores", "Noel", "Mike", "Fergal"));
      default:
        return new ArrayList<String>(Arrays.asList("Mark", "Matthew", "John", "Luke"));
    }
  }
  
  // test003:
  // Lazy lists
  public static void test003()
  {
    System.out.println("RxJavaTest04::test003");
    Observable<List<String>> allPages = Observable
      .range(0, Integer.MAX_VALUE)
      .map((Integer n) -> myListPeople2(n))
      .takeWhile(list -> !list.isEmpty());
    allPages
      .subscribe((l) -> {
        System.out.println("Next list");
        for (String e : l)
        {
          System.out.println(e);
        }
      });
  }
  
  public static String myLookupFlight(String flightNo)
  {
    String flight;
    System.out.println("In myLookupFlight");
    myGetId();
    if (flightNo.equals("Vienna")) {
      myWait(500);
      flight = "XYZ101";
    }
    else
    {
      myWait(5000); // Simulate slow lookup.
      flight = "AF8467";
    }
    System.out.println("Exit myLookupFlight");
    return flight;
  }
  
  public static String myFindPassenger(long id)
  {
    System.out.println("In myFindPassenger");
    myGetId();
    myWait(5000); // Simulate lookup.
    System.out.println("Exit myFindPassenger");
    return "John Doe";
  }
  
  public static String myBookTicket(String flight, String passenger)
  {
    System.out.println("In myBookTicket");
    myGetId();
    String ticket = String.format("Ticket %s-%s", flight, passenger);
    System.out.println("Exit myBookTicket");
    return ticket;
  }
  
  public static Integer mySendEmail(String ticket)
  {
    System.out.printf("In mySendEmail: sending for %s\n", ticket);
    myWait(1000);
    System.out.printf("Exit mySendEmail: done sending %s\n", ticket);
    return 123;
  }
  
  public static Observable<String> myRxLookupFlight(String flightNo)
  {
    System.out.println("In myRxLookupFlight");
    Observable<String> observable = Observable.defer(() ->
      Observable.just(myLookupFlight(flightNo))
    );
    System.out.println("Exit myRxLookupFlight");
    return observable;
  }
  
  public static Observable<String> myRxFindPassenger(long id)
  {
    System.out.println("In myRxFindPassenger");
    Observable<String> observable = Observable.defer(() ->
      Observable.just(myFindPassenger(id))
    );
    System.out.println("Exit myRxFindPassenger");
    return observable;
  }
  
  public static Observable<Integer> myRxSendEmail(String ticket)
  {
    return Observable.create(s -> {
      if (!ticket.equals("two") && !ticket.equals("four"))
      {
        s.onNext(mySendEmail(ticket));
        s.onComplete();
      }
      else
      {
        mySendEmail(ticket);
        s.onError(new Exception("ouch!"));
      }
    });
  }
  
  // test004:
  // Simulates booking a flight synchronously.
  public static void test004()
  {
    System.out.println("RxJavaTest04::test004");
    String flight = myLookupFlight("Amsterdam");
    String passenger = myFindPassenger(1001);
    String ticket = myBookTicket(flight, passenger);
    int result = mySendEmail(ticket);
  }

  // test005:
  // Simulates booking a flight synchronously via Observers.
  public static void test005()
  {
    System.out.println("RxJavaTest04::test005");
    Observable<String> flight = myRxLookupFlight("Amsterdam");
    Observable<String> passenger = myRxFindPassenger(1001);
    Observable<String> ticket =
      flight.zipWith(passenger, (f, p) -> myBookTicket(f, p));
    ticket.subscribe(RxJavaTest04::mySendEmail);
  }

  // test006:
  // Simulates booking a flight ssynchronously via Observers.
  public static void test006()
  {
    System.out.println("RxJavaTest04::test006");
    Observable<String> flight = myRxLookupFlight("Amsterdam").subscribeOn(Schedulers.io());
    Observable<String> passenger = myRxFindPassenger(1001).subscribeOn(Schedulers.io());
    Observable<String> ticket =
      flight.zipWith(passenger, (f, p) -> myBookTicket(f, p));
    System.out.println("Observables created, subscribing");
    ticket.subscribe(RxJavaTest04::mySendEmail);
    myWait(10000);
  }

  // test007:
  // Demonstrates timeout operator.
  public static void test007()
  {
    System.out.println("RxJavaTest04::test007");
    Observable<String> flight = myRxLookupFlight("Vienna") // Change to "Amsterdam" for slow operation.
      .subscribeOn(Schedulers.io())
      .timeout(1000, TimeUnit.MILLISECONDS);
    flight.subscribe(
      (f) -> System.out.println("Flight: " + f),
        (ex) -> {
          System.out.println("Error!");
          ex.printStackTrace();
        },
        () -> System.out.println("Done")
    );
    myWait(5500);
  }
  
  // test008:
  // Simulate sequential sending of emails and catching of all failures.
  public static void test008()
  {
    System.out.println("RxJavaTest04::test008");
    List<String> tickets = new ArrayList<String>(Arrays.asList("one", "two", "three", "four", "five"));
    List<String> failures = Observable.fromIterable(tickets)
      .flatMap(ticket ->
        myRxSendEmail(ticket)
          .flatMap(response -> Observable.<String>empty())
          .doOnError(e -> System.out.printf("Failed to send %s: %s\n", ticket, e))
          .onErrorReturn(err -> ticket))
      .toList()
      .blockingGet();
    for (String failure : failures) {
      System.out.println(failure);
    }
  }

  // test009:
  // Simulate concurrent sending of emails and catching of all failures.
  // The only diference from sequential case (test008) is added subscribeOn(Schedulers.io()).
  public static void test009()
  {
    System.out.println("RxJavaTest04::test009");
    List<String> tickets = new ArrayList<String>(Arrays.asList("one", "two", "three", "four", "five"));
    List<String> failures = Observable.fromIterable(tickets)
      .flatMap(ticket ->
        myRxSendEmail(ticket)
          .flatMap(response -> Observable.<String>empty())
          .doOnError(e -> System.out.printf("Failed to send %s: %s\n", ticket, e))
          .onErrorReturn(err -> ticket)
          .subscribeOn(Schedulers.io())
      )
      .toList()
      .blockingGet();
    for (String failure : failures) {
      System.out.println(failure);
    }
  }

  // myObserve:
  // Returns observable that emits string events for messages
  // received from MessageSource via callback onMessage.
  public static Observable<String> myObserve()
  {
    return Observable.<String>create(subscriber -> {
      MessageSource source = new MessageSource();
      source.setListener(
        new MessageListener<String>() {
          public void onMessage(String message)
          {
            subscriber.onNext(message);
          }
        }
      );
      subscriber.setCancellable(source::stop);
      source.start();
    });
  }
  
  // test010:
  // Uses observable created by myObserve.
  public static void test010()
  {
    System.out.println("RxJavaTest04::test010");
    Observable<String> txtMessages =
      myObserve();
    Disposable disposable = txtMessages
      .subscribe(System.out::println);
    
    myWait(5000);
    disposable.dispose();
    myWait(2000);
  }

  public static void sleepOneSecond()
  {
    myWait(1000);
  }
  
  // test011:
  // Demonstrates use of trampoline scheduler.
  // Note that RxJava 2 does not have immediate scheduler any more, so we cannot
  // demonstrate the difference between immediate and trampoline schedulers.
  public static void test011()
  {
    System.out.println("RxJavaTest04::test011");
    Scheduler scheduler = Schedulers.trampoline();
    Scheduler.Worker worker = scheduler.createWorker();
    
    log("Main start");
    Disposable disposable;
    disposable = worker.schedule(() -> {
      log(" Outer start");
      sleepOneSecond();
      worker.schedule(() -> {
        log("  Inner start");
        sleepOneSecond();
        log("  Inner end");
      });
      log(" Outer end");
    });
    log("Main end");
    disposable.dispose();
  }

  public static Observable<String> mySimple()
  {
    return Observable.create(s -> {
      log("Subscribed");
      s.onNext("A");
      s.onNext("B");
      s.onComplete();
    });
  }
  
  public static ThreadFactory newThreadFactory(String pattern)
  {
    ThreadFactoryBuilder tfb = new ThreadFactoryBuilder();
    tfb.setNameFormat(pattern);
    return tfb.build();
  }

  // test012:
  // Using operator subscribeOn to set the scheduler
  public static void test012()
  {
    System.out.println("RxJavaTest04::test012");
    ExecutorService poolA = Executors.newFixedThreadPool(10, newThreadFactory("Sched-A-%d"));
    Scheduler schedulerA = Schedulers.from(poolA);
    log("Starting");
    Observable<String> obs = mySimple();
    log("Created");
    obs
      .subscribeOn(schedulerA)
      .subscribe(
        x -> log("Got " + x),
        Throwable::printStackTrace,
        () -> log("Completed")
      );
    log("After subscribe");
    myWait(2000);
    log("Shutting down");
    poolA.shutdown();
  }
  
  // test013:
  // Demonstrates which thread runs operators when we set a scheduler with subscribeOn.
  public static void test013()
  {
    System.out.println("RxJavaTest04::test013");
    log("Starting");
    final Observable<String> obs = mySimple();
    log("Created");

    ExecutorService poolA = Executors.newFixedThreadPool(10, newThreadFactory("Sched-A-%d"));
    Scheduler schedulerA = Schedulers.from(poolA);

    obs
      .doOnNext(RxJavaTest04::log)
      .map(x -> x + '1')
      .doOnNext(RxJavaTest04::log)
      .map(x -> x + '2')
      .subscribeOn(schedulerA)
      .doOnNext(RxJavaTest04::log)
      .subscribe(
        x -> log("Got " + x),
        Throwable::printStackTrace,
        () -> log("Completed")
      );
    poolA.shutdown();
  }

  // test014:
  // How not to do parallel computation. doPurchase is a slow function and because
  // all calls are on the same scheduler, they are made sequentially.
  public static void test014()
  {
    System.out.println("RxJavaTest04::test014");
    ExecutorService poolA = Executors.newFixedThreadPool(10, newThreadFactory("Sched-A-%d"));
    Scheduler schedulerA = Schedulers.from(poolA);
    RxGroceries rxGroceries = new RxGroceries();

    Maybe<BigDecimal> totalPrice = Observable
      .fromIterable(Arrays.asList("bread", "butter", "milk", "tomato", "cheese"))
      .subscribeOn(schedulerA)
      .map(prod -> rxGroceries.doPurchase(prod, 1))
      .reduce(BigDecimal::add);
    totalPrice
      .subscribe(System.out::println);
    poolA.shutdown();
  }

  // test015:
  // How not to do parallel computation. Change from test016: now we are using flatMap,
  // but it doesn't help because all calls are still sequential.
  public static void test015()
  {
    System.out.println("RxJavaTest04::test015");
    ExecutorService poolA = Executors.newFixedThreadPool(10, newThreadFactory("Sched-A-%d"));
    Scheduler schedulerA = Schedulers.from(poolA);
    RxGroceries rxGroceries = new RxGroceries();

    Maybe<BigDecimal> totalPrice = Observable
      .fromIterable(Arrays.asList("bread", "butter", "milk", "tomato", "cheese"))
      .subscribeOn(schedulerA)
      .flatMap(prod -> rxGroceries.purchase(prod, 1))
      .reduce(BigDecimal::add);
    totalPrice
      .subscribe(System.out::println);
    poolA.shutdown();
  }

  // test016:
  // How not to do parallel computation. doPurchase is a slow function and because
  // all calls are on the same scheduler, they are made sequentially.
  public static void test016()
  {
    System.out.println("RxJavaTest04::test016");
    ExecutorService poolA = Executors.newFixedThreadPool(10, newThreadFactory("Sched-A-%d"));
    Scheduler schedulerA = Schedulers.from(poolA);
    RxGroceries rxGroceries = new RxGroceries();

    Maybe<BigDecimal> totalPrice = Observable
      .fromIterable(Arrays.asList("bread", "butter", "milk", "tomato", "cheese"))
      .subscribeOn(schedulerA)
      .flatMap(prod -> rxGroceries
        .purchase(prod, 1)
        .subscribeOn(schedulerA)
      )
      .reduce(BigDecimal::add);
    totalPrice
      .subscribe(
        x -> log("Got " + x),
        Throwable::printStackTrace,
        () -> log("Completed")
      );
    myWait(2000); // We have to wait before shutting down the pool,
                  // otherwise it will throw exception 
                  //     [java] io.reactivex.exceptions.UndeliverableException:
                  //     java.util.concurrent.RejectedExecutionException:
                  //     Task java.util.concurrent.FutureTask@38d8f9d0 rejected
                  //     from java.util.concurrent.ThreadPoolExecutor@6a724a6c
                  //     [Shutting down, pool size = 1, active threads = 1, queued
                  //     tasks = 0, completed tasks = 0]
    poolA.shutdown();
  }

  // test017:
  // Usage of groupBy operator.
  // Difference for RxJava 2 (from the code in the book):
  //   - Operator count returns Single and not Observable. Because of this, the first
  //     flatMap had to become flatMapSingle.
  //   - reduce(BiFunction<T,T,T> reducer) returns Single, so the type of totalPrice
  //     had to become Maybe<BigDecimal>. (Maybe is a subclass of Observable, but has a
  //     different contract.)
  public static void test017()
  {
    System.out.println("RxJavaTest04::test017");
    ExecutorService poolA = Executors.newFixedThreadPool(10, newThreadFactory("Sched-A-%d"));
    Scheduler schedulerA = Schedulers.from(poolA);
    RxGroceries store = new RxGroceries();

    Maybe<BigDecimal> totalPrice = Observable
      .just("bread", "butter", "egg", "milk", "tomato",
            "cheese", "tomato", "egg", "egg")
      .groupBy(prod -> prod)
      .flatMapSingle(grouped -> grouped
          .count()
          .map(quantity -> {
            String productName = grouped.getKey();
            return Pair.of(productName, quantity);
          })
      )
      .flatMap(order -> store
        .purchase(order.getLeft(), (int)order.getRight().longValue())
        .subscribeOn(schedulerA)
      )
      .reduce(BigDecimal::add);

    totalPrice
      .subscribe(System.out::println);
    myWait(2000);
    poolA.shutdown();
  }

  // test018:
  // Use observeOn; demonstrates the difference from subscribeOn (test013).
  public static void test018()
  {
    System.out.println("RxJavaTest04::test018");
    log("Starting");
    final Observable<String> obs = mySimple();
    log("Created");

    ExecutorService poolA = Executors.newFixedThreadPool(10, newThreadFactory("Sched-A-%d"));
    Scheduler schedulerA = Schedulers.from(poolA);
    ExecutorService poolB = Executors.newFixedThreadPool(10, newThreadFactory("Sched-B-%d"));
    Scheduler schedulerB = Schedulers.from(poolB);

    obs
      .doOnNext(RxJavaTest04::log)
      .map(x -> x + '1')
      .observeOn(schedulerB)
      .doOnNext(RxJavaTest04::log)
      .map(x -> x + '2')
      .doOnNext(RxJavaTest04::log)
      .subscribeOn(schedulerA)
      .subscribe(
        x -> log("Got " + x),
        Throwable::printStackTrace,
        () -> log("Completed")
      );
    myWait(2000);
    poolA.shutdown();
    poolB.shutdown();
  }

  // myStore:
  // Used with test019.
  public static Observable<UUID> myStore(String s)
  {
    return Observable.create(subscriber -> {
      log("Storing " + s);
      // hard work
      myWait(1000);
      subscriber.onNext(UUID.randomUUID());
      subscriber.onComplete();
    });
  }
  
  // test019:
  // Demonstrate further using both subscribeOn and observeOn. subscribeOn sets
  // the scheduler to use in Observe.create when we we subscribe to an observable.
  // observeOn sets the scheduler to use in subscribe.
  public static void test019()
  {
    System.out.println("RxJavaTest04::test019");

    ExecutorService poolA = Executors.newFixedThreadPool(10, newThreadFactory("Sched-A-%d"));
    Scheduler schedulerA = Schedulers.from(poolA);
    ExecutorService poolB = Executors.newFixedThreadPool(10, newThreadFactory("Sched-B-%d"));
    Scheduler schedulerB = Schedulers.from(poolB);
    ExecutorService poolC = Executors.newFixedThreadPool(10, newThreadFactory("Sched-C-%d"));
    Scheduler schedulerC = Schedulers.from(poolC);

    log("Starting");
    Observable<String> obs = Observable.create(subscriber -> {
      log("Subscribed");
      subscriber.onNext("A");
      subscriber.onNext("B");
      subscriber.onNext("C");
      subscriber.onNext("D");
      subscriber.onComplete();
    });
    log("Created");
    obs
      .subscribeOn(schedulerA)
      .flatMap(record -> myStore(record).subscribeOn(schedulerB))
      .observeOn(schedulerC)
      .subscribe(
        x -> log("Got: " + x),
        Throwable::printStackTrace,
        () -> log("Completed")
      );
    log("Exiting");
    
    myWait(5000);
    poolA.shutdown();
    poolB.shutdown();
    poolC.shutdown();
  }
}
