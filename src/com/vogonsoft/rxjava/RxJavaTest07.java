package com.vogonsoft.rxjava;

import io.reactivex.*;
import java.util.Arrays;
import java.lang.Thread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.*;
import io.reactivex.functions.*;
import java.util.function.*;
import io.reactivex.schedulers.*;
import org.junit.Test;
import java.util.List;
import java.io.*;
import java.nio.file.*;
import io.reactivex.observers.*;
import io.reactivex.schedulers.*;
import io.reactivex.exceptions.*;
import io.reactivex.subscribers.*;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Assertions.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import java.time.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RxJavaTest07
{
  public static void test()
  {
    // test001();
    // test002();
    // test003();
    // test004();
    // test005();
    // test006();
    // test007();
    // test008();
    test009();
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

  public static void waitCompletableFuture(CompletableFuture<Boolean> future)
  {
    try {
      future.get();
    } catch (Exception e) {
      // Ignore.
    }
  }
  
  public static void test001()
  {
    System.out.println("RxJavaTest07::test001");
    Observable<Integer> obs = Observable
      .create(subscriber -> {
        try {
          subscriber.onNext(1 / 0);
        } catch (Exception e) {
          System.out.println("Caught exception: " + e);
          subscriber.onError(e);
        }
      });
    
    System.out.println("Now I subscribe");
    try {
      // BROKEN, missing onError()
      obs
        .subscribe(System.out::println);
      System.out.println("After subscribe");
    } catch (Exception e) {
      System.out.println("Ouch! " + e);
    }
    System.out.println("After try-catch");
  }
  
  public static Observable<Double> determineIncome(String person)
  {
    return Observable.create(subscriber -> {
      new Thread(() -> {
        if (person != "Gummo") {
          myWait(1000);
          subscriber.onNext(65000.0);
          subscriber.onComplete();
        } else {
          myWait(1500);
          subscriber.onError(new Exception("Who?"));
        }
      }).start();
    });
  }

  public static Observable<Double> guessIncome(String person)
  {
    return Observable.create(subscriber -> {
      new Thread(() -> {
        myWait(2000);
        subscriber.onNext(33000.0);
        subscriber.onComplete();
      }).start();
    });
  }
  
  // test002
  // Demostrate onErrorResumeNext
  public static void test002()
  {
    System.out.println("RxJavaTest07::test002");
    Observable<String> person = Observable.fromIterable(Arrays.asList(
      "Chico",
      "Harpo",
      "Groucho",
      "Gummo",
      "Zeppo"
      ));
    // person.subscribe(System.out::println);
    Observable<Double> income = person
      .flatMap(RxJavaTest07::determineIncome)
      .onErrorResumeNext(person.flatMap(RxJavaTest07::guessIncome));
    income
      .subscribe(System.out::println);
  }
  
  public static Observable<Confirmation> myConfirmation()
  {
    Observable<Confirmation> delayBeforCompletion =
      Observable
        .<Confirmation>empty()
        .delay(2000, TimeUnit.MILLISECONDS);
      return Observable
        .just(new Confirmation())
        .delay(1000, TimeUnit.MILLISECONDS)
        .concatWith(delayBeforCompletion);
  }

  // test003
  // Demostrate timeout operator.
  public static void test003()
  {
    System.out.println("RxJavaTest07::test003");
    CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
    Observable<Confirmation> observable = myConfirmation();

    observable
      .timeout(2100, TimeUnit.MILLISECONDS) // If timeout is <1000, we time out without receiving event;
                                            //               >1000, but <2000, we receive event but then
                                            //                                 time out before completion
                                            //               >2000, there is no time out
      .subscribe(
        (x) -> System.out.println("Got event"),
        th -> {
          if ((th instanceof TimeoutException)) {
            System.out.println("Too long");
          } else {
            th.printStackTrace();
          }
          future.complete(true);
        },
        () -> future.complete(true)
      );
/*
    observable
      .subscribe(
        (x) -> { System.out.println("Received event"); },
        Throwable::printStackTrace,
        () -> future.complete(true)
      );
*/
    waitCompletableFuture(future);
  }

  // test004
  // Demostrate retry operator.
  public static void test004()
  {
    System.out.println("RxJavaTest07::test004");
    Supplier<Observable<String>> risky = () -> {
      System.out.println("Risky");
      return Observable.fromCallable(() -> {
        double val = Math.random();
        System.out.println("val=" + val);
        if (val < 0.1) {
          myWait((int) (Math.random() * 2000.0));
          return "OK";
        } else {
          throw new RuntimeException("Transient");
        }
      });
    };
    risky.get()
      .timeout(1, TimeUnit.SECONDS)
      .doOnError(th -> { System.out.println("Will retry: " + th); })
      .retry()
      .subscribe(System.out::println);
  }

  // test005
  // Test scheduler
  public static void test005()
  {
    System.out.println("RxJavaTest07::test005");
    TestScheduler sched = new TestScheduler();
    Observable<String> fast = Observable
      .interval(10, TimeUnit.MILLISECONDS, sched)
      .map(x -> "F" + x)
      .take(3);
    Observable<String> slow = Observable
      .interval(50, TimeUnit.MILLISECONDS, sched)
      .map(x -> "S" + x);
    
    Observable<String> stream = Observable.concat(fast, slow);
    stream.subscribe(System.out::println);
    System.out.println("Subscribed");
    myWait(1000);
    System.out.println("After one second");
    sched.advanceTimeBy(25, TimeUnit.MILLISECONDS);
    
    myWait(1000);
    System.out.println("After one more second");
    sched.advanceTimeBy(75, TimeUnit.MILLISECONDS);

    myWait(1000);
    System.out.println("... and one more");
    sched.advanceTimeTo(200, TimeUnit.MILLISECONDS);
  }

  // test006
  // Test scheduler
  public static void test006()
  {
    System.out.println("RxJavaTest07::test006");
    try {
      Class cl = Class.forName("class org.gradle.plugin.management.internal.DefaultPluginRequests");
      if (cl == null) {
        System.out.println("Class not found");
        return;
      }
      String url = cl.getResource("DefaultPluginRequests.class").toString();
      System.out.println("Found: " + url);
    } catch (Exception ex) {
      System.out.println("Exception: " + ex.toString());
    }
  }

  // test007
  // mockito
  public static void test007()
  {
    System.out.println("RxJavaTest07::test007");
    List mockedList = mock(List.class);
    
    try {
      
      mockedList.add("one");
      mockedList.clear();
      
      verify(mockedList).add("one");
      verify(mockedList).clear();
    } catch (Exception e) {
      System.out.println("exception: " + e.toString());
    }
  }

  private MyServiceWithTimeout mockReturning(
    Observable<LocalDate> result,
    TestScheduler testScheduler)
  {
    MyService mock = mock(MyService.class);
    given(mock.externalCall()).willReturn(result);
    return new MyServiceWithTimeout(mock, testScheduler);
  }
  
  @Test
  public void timeoutWhenServiceCompletes() throws Exception
  {
    // fail("testing the test");
    TestScheduler testScheduler = new TestScheduler();
    MyService mock = mockReturning(Observable.never(), testScheduler);
    TestObserver<LocalDate> ts = new TestObserver<>();
    
    // when
    mock.externalCall().subscribe(ts);
    
    // then
    testScheduler.advanceTimeBy(950, TimeUnit.MILLISECONDS);
    ts.assertNotTerminated();
    testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
    ts.assertTerminated();
    ts.assertComplete();
    ts.assertNoValues();

  }

  @Test
  public void valueIsReturnedJustBeforeTimeout() throws Exception
  {
    // fail("testing the test");
    // given
    TestScheduler testScheduler = new TestScheduler();
    Observable<LocalDate> slow = Observable
      .timer(950, TimeUnit.MILLISECONDS, testScheduler)
      .map(x -> LocalDate.now());
    MyService myService = mockReturning(slow, testScheduler);
    TestObserver<LocalDate> ts = new TestObserver<>();

    // when
    myService.externalCall().subscribe(ts);
    
    // then
    testScheduler.advanceTimeBy(930, TimeUnit.MILLISECONDS);
    ts.assertNotTerminated();
    ts.assertNoValues();
    testScheduler.advanceTimeBy(50, TimeUnit.MILLISECONDS);
    ts.assertComplete();
    ts.assertValueCount(1);
  }
  
  public static Instant myDbQuery()
  {
    return Instant.now().plusSeconds(3600L);
  }
  
  // test008
  public static void test008()
  {
    System.out.println("RxJavaTest07::test008");
    CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
    Observable<Instant> timestamps = Observable
      .fromCallable(() -> myDbQuery())
      .doOnSubscribe((disposable) -> System.out.println("subscribe()"));
      
    timestamps
      .zipWith(timestamps.skip(1), Duration::between)
      .map(Object::toString)
      .subscribe(
        (x) -> System.out.println("Got event: " + x.toString()),
        (e) -> e.printStackTrace(),
        () -> {
          System.out.println("Complete");
          future.complete(true);
        }
      );
    waitCompletableFuture(future);
  }
  
  // sendNumbers
  // Sends a number of events; honours backpressure.
  public static Flowable<Integer> sendNumbers()
  {
    return Flowable.generate(
      () -> 0,
      (cur, emitter) -> {
        if (cur < 200) {
          emitter.onNext(cur);
          return cur + 1;
        }
        emitter.onComplete();
        return 0;
      }
    );
  }
  
  // test009
  public static void test009()
  {
    System.out.println("RxJavaTest07::test009");
    CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
    AtomicInteger i = new AtomicInteger(1000);
    Flowable<Pair<Integer, Instant>> timestamps = Flowable
      .fromCallable(() -> {
        return new Pair<>(i.getAndIncrement(), myDbQuery());
      })
      .doOnSubscribe((disposable) -> System.out.println("subscribe()"))
      .doOnRequest((l) -> System.out.println("Request... : " + l))
      .doOnNext((event) -> System.out.printf("Event: [i=%d, instant=%s]\n",
        event.getFirst(), event.getSecond()));
      
    timestamps
      .zipWith(timestamps.skip(1), (e1, e2) -> Duration.between(e1.getSecond(), e2.getSecond()))
      .map(Object::toString)
      .subscribe(
        (x) -> System.out.println("Got event: " + x.toString()),
        (e) -> e.printStackTrace(),
        () -> {
          System.out.println("Complete");
          future.complete(true);
        }
      );
    waitCompletableFuture(future);
  }
  
  // Unit tests
  
  @Test
  public void my01Test()
  {
    long x = 11L;
    assertEquals("x must be 11", 11L, x);
  }
  
  @Test
  public void my02_shouldApplyConcatMapInOrder_Test() throws Exception
  {
    List<String> list = Observable
      .range(1, 3)
      .concatMap(x -> Observable.just(x, -x))
      .map(Object::toString)
      .toList()
      .blockingGet();
    assertThat(list).containsExactly("1", "-1", "2", "-2", "3", "-3");
  }
  
  // failBecauseExceptionWasNotThrown
  @Test
  public void my03_FileNotFoundException_Test() throws Exception
  {
    // File file = new File("404.txt");
    Path path = FileSystems.getDefault().getPath(".", "404.txt");
    Observable<List<String>> fileContents = Observable
      .fromCallable(() -> Files.readAllLines(path, UTF_8));

    // System.out.println("pt 0");
    
    try {
      fileContents
        .toFuture()
        .get();
        
      System.out.println("pt 1");
      failBecauseExceptionWasNotThrown(java.nio.file.NoSuchFileException.class);
    } catch (Exception expected) {
      // System.out.println("pt 3");
      assertThat(expected)
        .hasCauseInstanceOf(NoSuchFileException.class);
    }
  }

  public enum Kind { KIND_OnNext, KIND_OnError, KIND_OnComplete };
  
  public static Kind getKind(Notification notification)
  {
    if (notification.isOnNext()){
      return Kind.KIND_OnNext;
    } else if (notification.isOnError()) {
      return Kind.KIND_OnError;
    } else {
      return Kind.KIND_OnComplete;
    }
  }
  
  // concatMapDelayError
  @Test
  public void my04_concatMapDelayError_Test() throws Exception
  {
    Observable<Notification<Integer>> notifications = Observable
      .just(3, 0, 2, 0, 1, 0)
      .concatMapDelayError(x -> Observable.fromCallable(() -> 100 / x))
      .materialize();
      
    List<Kind> kinds = notifications
      .map(RxJavaTest07::getKind)
      .toList()
      .blockingGet();
    
    assertThat(kinds).containsExactly(Kind.KIND_OnNext, Kind.KIND_OnNext, Kind.KIND_OnNext, Kind.KIND_OnError );
  }

  // TestObserver
  // In the book, they use TestSubscriber, but in RxJava 2, we use TestObserver.
  @Test
  public void my05_TestSubscriber_Test() throws Exception
  {
    Observable<Integer> obs = Observable
      .just(3, 0, 2, 0, 1, 0)
      .concatMapDelayError(x -> Observable.fromCallable(() -> 100 / x));
    
    TestObserver<Integer> ts = new TestObserver<>();
    obs.subscribe(ts);
    
    ts.assertValues(33, 50, 100);
    // ts.assertError(ArithmeticException.class);
    ts.assertError(CompositeException.class);
    List<Throwable> errors = ts.errors();
    assertThat(errors).hasSize(1);
    assertThat(errors).element(0).isInstanceOf(CompositeException.class);
    List<Throwable> exceptions = ((CompositeException)errors.get(0)).getExceptions();
    assertThat(exceptions).hasSize(3);
    assertThat(exceptions).element(0).isInstanceOf(ArithmeticException.class);
    assertThat(exceptions).allMatch((e) -> { return (e instanceof ArithmeticException); });
  }
  
  // naturals1
  // Generates a Flowable that does not honor backpressure.
  public static Flowable<Long> naturals1()
  {
    return Flowable.<Long>create(emitter -> {
        long i = 0;
        while (!emitter.isCancelled()) {
          // System.out.println("Dish: " + i);
          emitter.onNext(i++);
        }
        emitter.onComplete();
      },
      //BackpressureStrategy.BUFFER
      //BackpressureStrategy.ERROR
      BackpressureStrategy.MISSING // This value means "no backpressure."
      );
  }

  // naturals2
  // Generates a Flowable that honours backpressure.
  public static Flowable<Long> naturals2()
  {
    return Flowable.generate(
      () -> 0L,
      (cur, emitter) -> {
        emitter.onNext(cur);
        return cur + 1;
      }
    );
  }

  @Test
  // Test of a Flowable that should honour backpressure.
  public void my06_Backpressure_Test() throws Exception
  {
    // fail("ouch");
    // naturals1 does not honor backpressure. Uncommenting next line causes test to fail.
    // Flowable<Long> flowable = naturals1();
    // naturals2 honors backpressure. Uncommenting next line causes test to pass.
    Flowable<Long> flowable = naturals2();
    
    TestSubscriber<Long> ts = new TestSubscriber<Long>(0);
    flowable
      .take(10)
      .subscribe(ts);
    
    ts.assertNoValues();
    ts.requestMore(100);
    ts.assertValueCount(10);
    ts.assertComplete();
  }
}
