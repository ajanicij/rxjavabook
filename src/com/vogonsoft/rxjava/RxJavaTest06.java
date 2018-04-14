package com.vogonsoft.rxjava;

import java.util.Random;
import java.util.stream.Collectors;
import io.reactivex.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.Arrays;
import io.reactivex.schedulers.*;
import io.reactivex.disposables.*;
import org.reactivestreams.*;
import io.reactivex.subjects.*;
import java.util.stream.*;
import io.reactivex.processors.*;
import java.util.Iterator;

class RxJavaTest06
{
  public static void test()
  {
    System.out.println("RxJavaTest05::test");
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
  
  public static double averageOfList(List<Double> list)
  {
    return list
             .stream()
             .collect(Collectors.averagingDouble(x -> x));
  }
  
  // test001:
  // Computing a moving average.
  public static void test001()
  {
    System.out.println("RxJavaTest06::test001");
    Random random = new Random();
    Observable
      .defer(() -> {
        System.out.println("Subscribed");
        return Observable.just(random.nextGaussian());
      })
      .doOnNext((x) -> System.out.printf("After defer: %f\n", x))
      .doOnComplete(() -> System.out.println("Complete"))
      .repeat(2)
      .doOnNext((x) -> System.out.printf("After repeat: %f\n", x))
      .buffer(5, 1)
      .doOnNext(l -> {
        String s = l
          .stream()
          .reduce("", (l1, r1) -> String.format("%s, %f", l1, r1), (l1, r1) -> l1 + r1);
        System.out.printf("After buffer: %s\n", s);
      })
      .map(RxJavaTest06::averageOfList)
      .subscribe(System.out::println);
  }

  // test002:
  // Exploring Observable::repeat operator.
  public static void test002()
  {
    System.out.println("RxJavaTest06::test002");
    Observable<Integer> obs = Observable
      .just(1, 2, 3)
      .repeat(2);
    obs
      .subscribe(System.out::println);
     
    System.out.println("-- Second observable");
    obs = Observable
      .create(subscriber -> {
        subscriber.onNext(11);
        subscriber.onComplete();
      });
    obs
      .repeat(3)
      .subscribe(System.out::println);
  }

  // genDelayed:
  // Generate delayed events from an iterable.
  // E.g. if the input Iterable contains numbers 1000, 2000 and 3000 and unit is
  // milliseconds, the output
  // Observable will emit events 0, 1000, 3000 and 6000 at 0ms, 1000ms, 3000ms and 6000ms.
  public static Observable<Long> genDelayed(Iterable<Long> times, TimeUnit unit)
  {
    return Observable
      .fromIterable(times)
      .<Long>scan(0L, (acc, item) -> acc + item)
      .<Long>flatMap(d -> Observable
                            .just(d)
                            .delay(d, unit));
  }

  // genDelayedFromIterable
  // Generate delayed events from an iterable. Similar to genDelayed, but also gets a
  // second iterable of items to emit as a parameter.
  public static <T> Observable<T> genDelayedFromIterable(
    Iterable<Long> times,
    TimeUnit unit,
    Iterable<T> items)
  {
    Observable<Long> obsTimed = Observable
      .fromIterable(times)
      .<Long>scan(0L, (acc, item) -> acc + item)
      .<Long>flatMap(d -> Observable
                            .just(d)
                            .delay(d, unit));
    Observable<T> obsItems = Observable
      .fromIterable(items);
    return obsTimed
      .zipWith(obsItems, (t, i) -> i);
  }
  
  // test003:
  // Generating timed events.
  public static void test003()
  {
    System.out.println("RxJavaTest06::test003");
    CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
    Observable<Long> obs = genDelayed(
      Arrays.asList(1000L, 2000L, 1000L, 4000L),
      TimeUnit.MILLISECONDS);
    obs
      .subscribe(
        System.out::println,
        e -> e.printStackTrace(),
        () -> future.complete(true)
        );
    
    // myWait(10000);
    waitCompletableFuture(future);
  }

  // test004:
  // debounce
  public static void test004()
  {
    System.out.println("RxJavaTest06::test004");
    CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
    Observable<Long> obs = genDelayed(
      Arrays.asList(100L, 100L, 100L, 1200L, 100L, 100L, 100L, 1300L),
      TimeUnit.MILLISECONDS);
    obs
      .debounce(1000, TimeUnit.MILLISECONDS)
      .subscribe(
        System.out::println,
        e -> e.printStackTrace(),
        () -> future.complete(true)
        );

    waitCompletableFuture(future);
  }

  // test005:
  // Generate delayed items by calling genDelayedFromIterable
  public static void test005()
  {
    System.out.println("RxJavaTest06::test005");
    CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
    Observable<String> obs = genDelayedFromIterable(
      Arrays.asList(1000L, 2000L, 1000L, 4000L),
      TimeUnit.MILLISECONDS,
      Arrays.<String>asList("one", "two", "three", "four", "five"));
    obs
      .subscribe(
        System.out::println,
        e -> e.printStackTrace(),
        () -> future.complete(true)
        );
    
    waitCompletableFuture(future);
  }

  // test006:
  // debounce of simulated stock price events
  // debounce is the overloaded function with variable timeout: if the price is over 150.0,
  // the timeout is 300ms, else the timeout is 800ms.
  public static void test006()
  {
    System.out.println("RxJavaTest06::test006");
    CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
    Observable<Double> obs = genDelayedFromIterable(
      Arrays.asList(100L, 100L, 100L, 1000L, 1000L, 500L, 500L, 500L),
      TimeUnit.MILLISECONDS,
      Arrays.<Double>asList(139.0, 140.0, 141.0, 142.0, 143.0, 151.0, 152.0, 153.0, 154.0))
      .debounce(x -> {
        boolean goodPrice = x > 150.0;
        return Observable
          .empty()
          .delay(goodPrice ? 300 : 800, TimeUnit.MILLISECONDS);
      });
    obs
      .subscribe(
        System.out::println,
        e -> e.printStackTrace(),
        () -> future.complete(true)
        );
    
    waitCompletableFuture(future);
  }

  // test007:
  // Emitting range events to an observer in a different thread
  // According to the book, Observable::range should implement backpressure.
  // Really not. In RxJava 2, Observable does not implement backpressure and
  // serves as a legacy class; when we do want to implement backpressure, we
  // should use Flowable instead of Observable.
  public static void test007()
  {
    System.out.println("RxJavaTest06::test007");
    Observable<Dish> dishes = Observable
      .range(1, 1_000_000_000)
      .map(Dish::new);
      
    dishes
      .observeOn(Schedulers.io())
      .subscribe(x -> {
        System.out.println("Washing: " + x);
        myWait(50);
      });
  }
  
  public static Observable<Integer> myRange(int from, int count)
  {
    return Observable.create(subscriber -> {
      int i = from;
      while (i < from + count) {
        if (!subscriber.isDisposed()) {
          // System.out.println("Dish: " + i);
          subscriber.onNext(i++);
          myWait(10);
        } else {
          return;
        }
      }
      subscriber.onComplete();
    });
  }

  // test008:
  // Like test007, but calls myRange instead of range.
  public static void test008()
  {
    System.out.println("RxJavaTest06::test008");
    CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
    Observable<Dish> dishes = myRange(1, 1_000_000_000)
      .map(Dish::new);
      
    dishes
      .observeOn(Schedulers.io())
      .subscribe(x -> {
        System.out.println("Washing: " + x);
        myWait(50);
      },
      Throwable::printStackTrace,
      () -> future.complete(true));
    waitCompletableFuture(future);
  }

  // test009
  // For backpressure, we use Flowable instead of Observable.
  // onSubscribe receives Subscription instead of Disposable.
  // We have to hang on to the subscription and call its request method at
  // appropriate time.
  public static void test009()
  {
    System.out.println("RxJavaTest06::test009");
    Flowable
      .range(1, 10)
      .subscribe(new Subscriber<Integer>() {
        private int left;
        private Subscription s;

        {{
          System.out.println("In constructor");
          left = 0;
        }}
        
        @Override
        public void onSubscribe(Subscription s)
        {
          System.out.println("In onSubscribe");
          s.request(3);
          left = 3;
          this.s = s;
        }
        
        @Override
        public void onNext(Integer n)
        {
          System.out.printf("In onNext(%d)\n", n);
          left--;
          if (left == 0) {
            s.request(3);
            left = 3;
          }
        }
        
        @Override
        public void onComplete()
        {
          System.out.println("In onComplete");
        }
        
        @Override
        public void onError(Throwable t)
        {
          t.printStackTrace();
        }
      });
  }

  // test010
  // Like test007 but using Flowable instead of Observable.
  // Flowable::range has backpressure built in.
  public static void test010()
  {
    System.out.println("RxJavaTest06::test010");
    CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();

    Flowable<Dish> dishes = Flowable
      .range(1, 1_000_000_000)
      .doOnNext(n -> System.out.printf("range sending %d\n", n))
      .map(n -> new Dish(n));
      
    dishes
      .observeOn(Schedulers.io())
      .subscribe(new Subscriber<Dish>() {
        private int left;
        private Subscription s;

        {{
          System.out.println("In constructor");
          left = 1;
        }}
        
        @Override
        public void onSubscribe(Subscription s)
        {
          System.out.println("In onSubscribe");
          s.request(3);
          left = 3;
          this.s = s;
        }
        
        @Override
        public void onNext(Dish d)
        {
          System.out.printf("In onNext(%s)\n", d);
          System.out.println("In onNext");
          System.out.println("Washing: " + d);
          myWait(50);
          left--;
          if (left == 0) {
            s.request(3);
            left = 3;
          }
        }
        
        @Override
        public void onComplete()
        {
          System.out.println("In onComplete");
          future.complete(true);
        }
        
        @Override
        public void onError(Throwable t)
        {
          t.printStackTrace();
        }
      });
    waitCompletableFuture(future);
  }

  // test011
  // Like test010 but using SubscriberUtil to make creating subscribers easier.
  // Here we generate a subscriber by calling SubscriberUtil.generateSubscriber
  // and passing Consumer<Dish>, Action to it for onNext, onComplete and number
  // of items we request from the Flowable by calling Subscription::request(long n).
  public static void test011()
  {
    System.out.println("RxJavaTest06::test011");
    CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();

    Flowable<Dish> dishes = Flowable
      .range(1, 1_000_000_000)
      .doOnNext(n -> System.out.printf("range sending %d\n", n))
      .map(n -> new Dish(n));
      
    dishes
      .observeOn(Schedulers.io())
      .subscribe(SubscriberUtil.generateSubscriber(
        d -> {
          System.out.printf("In onNext(%s)\n", d);
          System.out.println("In onNext");
          System.out.println("Washing: " + d);
          myWait(50);
        },
        () -> {
          System.out.println("In onComplete");
          future.complete(true);
        },
        3
      ));
    waitCompletableFuture(future);
  }

  public static Flowable<Integer> myFlowableRange(int from, int count)
  {
    return Flowable.<Integer>create(emitter -> {
        int i = from;
        while (i < from + count) {
          // System.out.println("Dish: " + i);
          emitter.onNext(i++);
          myWait(10);
        }
        emitter.onComplete();
      },
      //BackpressureStrategy.BUFFER
      //BackpressureStrategy.ERROR
      BackpressureStrategy.MISSING
      );
  }

  // test012
  // Like test010 but we use myFlowableRange instead of Flowable::range.
  public static void test012()
  {
    System.out.println("RxJavaTest06::test012");
    CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();

    Flowable<Dish> dishes = myFlowableRange(1, 1_000_000_000)
      .doOnNext(n -> System.out.printf("range sending %d\n", n))
      .map(n -> new Dish(n));
      
    dishes
      .observeOn(Schedulers.io())
      .subscribe(SubscriberUtil.generateSubscriber(
        d -> {
          System.out.printf("In onNext(%s)\n", d);
          System.out.println("In onNext");
          System.out.println("Washing: " + d);
          myWait(50);
        },
        () -> {
          System.out.println("In onComplete");
          future.complete(true);
        },
        3
      ));
    waitCompletableFuture(future);
  }
  
  public static void myCompute(int v)
  {
    System.out.println("myCompute(" + v + ")");
    myWait(1000);
  }

  // test013
  // Creating cold flowable using Flowable::generate. This flowable
  // supports backpressure.
  public static void test013()
  {
    System.out.println("RxJavaTest06::test013");
    CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
    Flowable<Dish> dishes = Flowable.generate(
      () -> 0,
      (n, emitter) -> {
        if (n < 1_000_000) {
          System.out.println("Emit Dish(" + n + ")");
          emitter.onNext(new Dish(n));
        } else {
          emitter.onComplete();
        }
        return n + 1;
      }
    );
    
    dishes
      .observeOn(Schedulers.io())
      .subscribe(SubscriberUtil.generateSubscriber(
        d -> {
          System.out.println("Washing: " + d);
          myWait(100);
        },
        () -> {
          System.out.println("In onComplete");
          future.complete(true);
        },
        3
      ));
    waitCompletableFuture(future);
  }

  // test014
  // Creating hot flowable using Flowable::create. We emit events fast from a separate
  // thread and in the subscriber implement backpressure strategy.
  public static void test014()
  {
    System.out.println("RxJavaTest06::test014");
    CompletableFuture<Boolean> future = new CompletableFuture<Boolean>();
    Flowable<Dish> dishes = Flowable.<Dish>create(emitter -> {
          new Thread(() -> {
            int i;
            for (i = 0; i < 1_000_000; i++) {
              System.out.println("Dish: " + i);
              emitter.onNext(new Dish(i));
              myWait(10);
            }
            emitter.onComplete();
          }).start();
        },
        // BackpressureStrategy.BUFFER
        // BackpressureStrategy.ERROR
        // BackpressureStrategy.MISSING
        BackpressureStrategy.LATEST
      );

    dishes
      .observeOn(Schedulers.io())
      .subscribe(SubscriberUtil.generateSubscriber(
        d -> {
          System.out.println("Washing: " + d);
          myWait(100);
        },
        () -> {
          System.out.println("In onComplete");
          future.complete(true);
        },
        3
      ));
    waitCompletableFuture(future);
  }

  // test015
  // Flowable::buffer
  public static void test015()
  {
    System.out.println("RxJavaTest06::test015");
    PublishProcessor<Integer> source = PublishProcessor.create();
    source
      .buffer(1000) // If this parameter is < 1000, we get MissingBackpressureException
      .observeOn(Schedulers.computation(), true, 1000)
      .subscribe(list -> {
        // int x = list.parallelStream().map(e -> e * e).reduce((result, element) -> result).get();
        // long x = list.parallelStream().count();
        int x = list.parallelStream().reduce((result, element) -> result).get();
        System.out.println("First: " + x);
      }, Throwable::printStackTrace);
    
    for (int i = 0; i < 1_000_000; i++) {
      source.onNext(i);
    }
    
    myWait(10000);
  }

  // test016
  // Flowable::onBackpressureBuffer
  public static void test016()
  {
    System.out.println("RxJavaTest06::test016");
    PublishProcessor<Integer> source = PublishProcessor.create();
    source
      .onBackpressureBuffer(
          128,
          () -> System.out.println("Overflown!"),
          BackpressureOverflowStrategy.DROP_LATEST
        )
      .observeOn(Schedulers.computation(), true, 1000)
      .subscribe(
        i -> {
          System.out.println("i=" + i);
        }, Throwable::printStackTrace);
    
    for (int i = 0; i < 10_000; i++) {
      source.onNext(i);
    }
    
    myWait(10000);
  }

  // test017
  // Getting ResultSet with backpressure.
  public static void test017()
  {
    System.out.println("RxJavaTest06::test017");
    ResultSet rs = new ResultSet();
    Flowable<Object[]> result =
      Flowable
        .fromIterable(ResultSetIterator.iterable(rs))
        .doAfterTerminate(() -> {
          try {
            rs.close();
          } catch (Exception ex) {
              // Ignore.
          }
        });
    result
      .subscribe(o -> {
        Object[] row = o;
        System.out.printf("Got %s,%d\n", row[0], row[1]);
      });
  }
}
