package com.vogonsoft.rxjava;

import io.reactivex.*;
import io.reactivex.disposables.*;
import io.reactivex.functions.*;
import java.util.concurrent.*;
import io.reactivex.subjects.*;
import io.reactivex.observables.*;
import java.util.concurrent.*;
import java.time.*;
import java.util.Arrays;
import java.math.*;

public class RxJavaTest03
{
  public static void test()
  {
    System.out.println("RxJavaTest03::test");
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

  // test001:
  // A simple demonstration of some operators
  public static void test001()
  {
    System.out.println("RxJavaTest03::test001");
    Observable
      .just(8, 9, 10)
      .doOnNext(i -> System.out.println("A: " + i))
      .filter(i -> i % 3 > 0)
      .doOnNext(i -> System.out.println("B: " + i))
      .map(i -> "#" + i * 10)
      .doOnNext(i -> System.out.println("C: " + i))
      .filter(s -> s.length() < 4)
      .subscribe(s -> System.out.println("D: " + s));
  }
  
  // test002:
  // Demo of flatMap
  public static void test002()
  {
    System.out.println("RxJavaTest03::test001");
    Observable
      .just(new CarPhoto("abc.jpg"), new CarPhoto("def.jpg"))
      .flatMap(CarPhotoAnalyzer::recognize)
      .subscribe(
        licence -> System.out.printf("Got licence: %s\n", licence),
        Throwable::printStackTrace,
        () -> System.out.println("Done")
      );
    
    // myWait(5000);
  }
  
  // test003:
  // Demonstrates that flatMap does not preserve the order of events.
  public static void test003()
  {
    Observable
      .just("Lorem", "ipsum", "dolor", "sit", "amet",
            "consectetur", "adipiscing", "elit")
      .delay(word -> Observable.timer(word.length(), TimeUnit.SECONDS))
      .subscribe(System.out::println);
    
    myWait(15000);
  }
  
  // myLoadRecordsFor:
  // @param: dow - day of week
  // @return: Observable<String>
  public static Observable<String> myLoadRecordsFor(DayOfWeek dow)
  {
    switch (dow) {
      case SUNDAY:
        return Observable
                 .interval(90, TimeUnit.MILLISECONDS)
                 .take(5)
                 .map(i -> "Sun-" + i);
      case MONDAY:
        return Observable
                 .interval(65, TimeUnit.MILLISECONDS)
                 .take(5)
                 .map(i -> "Mon-" + i);
      default:
        return Observable.empty();
    }
  }

  // test004:
  // Demonstrates that flatMap does not preserve the order of observables.
  public static void test004()
  {
    System.out.println("RxJavaTest03::test004");
    Observable
      .just(DayOfWeek.SUNDAY, DayOfWeek.MONDAY)
      .flatMap(RxJavaTest03::myLoadRecordsFor)
      .subscribe(System.out::println);
    
    myWait(2000);
  }
  
  // test005:
  // Using flatMap, print all squares of the chessboard.
  public static void test005()
  {
    System.out.println("RxJavaTest03::test005");
    Observable<Integer> oneToEight = Observable.range(1, 8);
    Observable<String> ranks = oneToEight
      .map(Object::toString);
    Observable<String> files = oneToEight
      .map(x -> 'a' + x - 1)
      .map(ascii -> (char)ascii.intValue())
      .map(ch -> Character.toString(ch));
    
    Observable<String> squares = files
      .flatMap(file -> ranks.map(rank -> file + rank));
    squares
      .subscribe(System.out::println);
  }
  
  public static Observable<String> myStream(int initialDelay, int interval, String name)
  {
    return Observable
             .interval(initialDelay, interval, TimeUnit.MILLISECONDS)
             .map(x -> name + x)
             .doOnSubscribe((Disposable d) ->
                System.out.println("Subscribe to " + name))
             .doOnDispose(() ->
                System.out.println("Unsubscribe from " + name));
  }
  
  public static void test006()
  {
    System.out.println("RxJavaTest03::test006");
    Observable.amb(Arrays.asList(
      myStream(100, 17, "S"),
      myStream(200, 10, "F"))
    ).subscribe(System.out::println);
    myWait(1000);
  }

  public static void test007()
  {
    System.out.println("RxJavaTest03::test007");
    Observable<Integer> progress = Observable.just(10, 14, 12, 13, 14, 16);
    progress
      .scan((total, chunk) -> total + chunk)
      .subscribe(System.out::println);
  }

  // test008:
  // scan operator
  public static void test008()
  {
    System.out.println("RxJavaTest03::test008");
    Observable<BigInteger> factorials = Observable
      .range(2, 100)
      .scan(BigInteger.ONE, (big, cur) ->
        big.multiply(BigInteger.valueOf(cur)));
    factorials
      .subscribe(System.out::println);
  }

  // test009:
  // reduce operator
  public static void test009()
  {
    System.out.println("RxJavaTest03::test009");
    Single<BigInteger> factorials = Observable
      .range(2, 100)
      .reduce(BigInteger.ONE, (big, cur) ->
        big.multiply(BigInteger.valueOf(cur)));
    factorials
      .subscribe(System.out::println);
  }

  // test010:
  // collect operator
  public static void test010()
  {
    System.out.println("RxJavaTest03::test010");
    Single<String> str = Observable
      .range(1, 10)
      .collect(
               StringBuilder::new,
               (sb, x) -> sb.append(x).append(", "))
      .map(StringBuilder::toString);
    str.subscribe(System.out::println);
  }

  // test011:
  // concat operator
  public static void test011()
  {
    System.out.println("RxJavaTest03::test011");
    ConnectableObservable<Integer> veryLong = Observable.<Integer>create(
      s -> {
        Runnable r = () -> {
          int i;
          for (i = 0; i < 20; i++) {
            myWait(200);
            System.out.printf("Sending %d\n", i);
            s.onNext(i);
          }
          System.out.println("Completing");
          s.onComplete();
        };
        new Thread(r).start();
      }
    )
      .publish();
    veryLong.connect();
    
    /*
    In this case, we use publish operator rather than share, because share doesn't
    do what we want: In concat below, it subscribes to the first observer (veryLong.take(5))
    and after it completes, disconnects and then subscribes to the second
    (veryLong.takeLast(5)). Because share() is the same as connect().refCount(), after
    connect unsubscribes from the first observer, the observable is disposed; on the
    second subscription, another thread is started, which starts counting from 0 again.
     */
    
    final Observable<Integer> ends = Observable.concat(
      veryLong.take(5),
      veryLong.takeLast(5)
    );
    ends.subscribe(System.out::println);
    // myWait(10000);
  }

  public static Observable<String> mySpeak(String quote, long millisPerChar)
  {
    String[] tokens = quote.replaceAll("[:,]", "").split(" ");
    Observable<String> words = Observable.fromArray(tokens);
    Observable<Long> absoluteDelay = words
      .map(String::length)
      .map(len -> len * millisPerChar)
      .scan((total, current) -> total + current);
    return words
      .zipWith(absoluteDelay.startWith(0L), Pair::of)
      .flatMap(pair -> Observable.just(pair.getLeft())
                         .delay(pair.getRight(), TimeUnit.MILLISECONDS));
  }

  // test012:
  // concat operator
  public static void test012()
  {
    System.out.println("RxJavaTest03::test012");
    Observable<String> alice = mySpeak(
      "To be, or not to be: that is the question", 110);
    Observable<String> bob = mySpeak(
      "Though this be madness, yet, there is method in't", 90);
    Observable<String> jane = mySpeak(
      "There are more things in Heaven and Earth, " +
      "Horatio, than are dreamt of in your philosophy", 100);

/*
    Observable
      .merge(
        alice.map(w -> "Alice: " + w),
        bob.map(w   -> "Bob:   " + w),
        jane.map(w  -> "Jane:  " + w)
      )
      .subscribe(System.out::println);
*/      
    Observable
      .concat(
        alice.map(w -> "Alice: " + w),
        bob.map(w   -> "Bob:   " + w),
        jane.map(w  -> "Jane:  " + w)
      )
      .subscribe(System.out::println);

    myWait(10000);
  }
  
  // myOdd:
  // Returns ObservableTransformer used for compose.
  // compose(myOdd()) transforms an upstream Observable by filtering through
  // only odd events (first, third, fifth etc.).
  // myOdd is used in test013.
  public static <T> ObservableTransformer<T, T> myOdd()
  {
    Observable<Boolean> trueFalse = Observable.just(true, false).repeat();
    return upstream -> upstream
      .zipWith(trueFalse, Pair::of)
      .filter(Pair::getRight)
      .map(Pair::getLeft);
  }

  // test013:
  // concat operator
  public static void test013()
  {
    System.out.println("RxJavaTest03::test013");
    // [A, B, C, D, E...]
    Observable<Character> alphabet =
      Observable
        .range(0, 'Z' - 'A' + 1)
        .map(c -> (char)('A' + c));
    // [A, C, E...]
    alphabet
      .compose(myOdd())
      .forEach(System.out::println);
  }

  // myOdd2:
  // Compatible with ObservableTransformer used for compose.
  // compose(RxJavaTest03::myOdd2) transforms an upstream Observable by filtering through
  // only odd events (first, third, fifth etc.).
  // myOdd2 is used in test014.
  // The difference from myOdd is that myOdd is a function that returns an
  // ObservableTransformer, while myOdd2 is a function that IS an ObservableTransformer.
  public static <T, U> Observable<? super U> myOdd2(Observable<? extends T> upstream)
  {
    Observable<Boolean> trueFalse = Observable.just(true, false).repeat();
    return upstream
      .zipWith(trueFalse, Pair::of)
      .filter(Pair::getRight)
      .map(Pair::getLeft);
  }

  // test014:
  // concat operator
  public static void test014()
  {
    System.out.println("RxJavaTest03::test013");
    // [A, B, C, D, E...]
    Observable<Character> alphabet =
      Observable
        .range(0, 'Z' - 'A' + 1)
        .map(c -> (char)('A' + c));
    // [A, C, E...]
    alphabet
      .compose(RxJavaTest03::myOdd2)
      .forEach(System.out::println);
  }
  
  // test015:
  // Experiments with generics and wildcard types
  public static void test015()
  {
    PairTest.test01();
  }
  
  // test016:
  // Test of a custom operator MyOperator.
  public static void test016()
  {
    System.out.println("RxJavaTest03::test016");
    Observable
      .range(1, 8)
      .lift(new MyOperator<Integer>())
      .map(Object::toString)
      .subscribe(System.out::println);
  }

  public static void test017()
  {
    System.out.println("RxJavaTest03::test017");
    Observable
      .range(1, 4)
      .repeat()
      .lift(new MyOperator<Integer>())
      .take(3)
      .subscribe(System.out::println);
  }
}
