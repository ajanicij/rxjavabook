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
import java.util.stream.*;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.*;

import com.ning.http.client.AsyncHandler;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

class RxJavaTest05
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
    // test017();
    // test018();
    // test019();
    test020();
  }

  public static boolean myWait(int ms)
  {
    try {
      Thread.sleep(ms);
      return true;
    }
    catch (InterruptedException e) {
      System.out.println("Thread.sleep interrupted");
      return false;
    }
  }

  // test001:
  // Simple threaded HTTP server
  public static void test001()
  {
    System.out.println("RxJavaTest05::test001");
    try {
      SingleThreadHttpd.testMain();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // test002:
  // HTTP server using Netty
  public static void test002()
  {
    System.out.println("RxJavaTest05::test002");
    try {
      HttpTcpNettyServer.testMain();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // test003:
  // HTTP server using RxNetty
  public static void test003()
  {
    System.out.println("RxJavaTest05::test003");
    System.out.println("Not implemented: RxNetty is not ready yet for RxJava 2");
  }
  
  // test004:
  // Simulates receiving receiving asynchronous notifications from PostgreSQL.
  public static void test004()
  {
    System.out.println("RxJavaTest05::test004");
    DbConnection conn = new DbConnection();
    conn.start();
    int i;
    for (i = 0; i < 10; i++) {
      myWait(1000);
      DbNotification[] notifications = conn.getNotifications();
      if (notifications == null) {
        System.out.println("Nothing");
      } else {
        int j;
        for (DbNotification n : notifications) {
          System.out.printf("Got notification: %s\n", n.getMessage());
        }
      }
    }
    conn.stop();
  }
  
  public static Observable<DbNotification> myPollForNotifications(
    long pollingPeriod,
    DbConnection conn)
  {
    return Observable
      .interval(0, pollingPeriod, TimeUnit.MILLISECONDS)
      .flatMap(x -> myTryGetNotification(conn))
      .filter(arr -> arr != null)
      .flatMapIterable(Arrays::asList);
  }
  
  public static Observable<DbNotification[]> myTryGetNotification(
    DbConnection conn)
  {
    try {
      return Observable.just(conn.getNotifications());
    } catch (Exception e) {
      // return Observable.error(e);
      return Observable.empty();
    }
  }
  
  public static Observable<DbNotification> myObserve(long pollingPeriod)
  {
    return Observable.<DbNotification>create(subscriber -> {
      try {
        DbConnection conn = new DbConnection();
        subscriber.setCancellable(() -> conn.stop()); // Upon unsubscribe, stop connection.
        myListenOn(conn);
        myPollForNotifications(pollingPeriod, conn)
          .subscribe(
            v -> {
              if (v != null) {
                subscriber.onNext(v);
              }
            },
            Throwable::printStackTrace,
            () -> System.out.println("Done")
          );
          conn.start();
      } catch (Exception e) {
        subscriber.onError(e);
      }
    }).share();
  }
  
  public static void myListenOn(DbConnection conn)
  {
    // In case of a real SQL client, we would execute a query here...
  }

  // test005:
  // Simulates receiving receiving asynchronous notifications from PostgreSQL
  // via Observable.
  public static void test005()
  {
    System.out.println("RxJavaTest05::test005");
    Disposable d = myObserve(800)
      .subscribe(
        n -> System.out.println(n.getMessage()),
        Throwable::printStackTrace,
        () -> System.out.println("Done")
        );
    myWait(10000);
    System.out.println("Disposing");
    d.dispose();
  }

  // test006:
  // Java concurrency API: ExecutorService
  // Taken from https://www.callicoder.com/java-executor-service-and-thread-pool-tutorial/
  public static void test006()
  {
    System.out.println("RxJavaTest05::test006");
    System.out.println("Inside : " + Thread.currentThread().getName());
    
    System.out.println("Creating Executor Service...");
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    System.out.println("Creating a Runnable...");
    Runnable runnable = () -> {
      System.out.println("Inside : " + Thread.currentThread().getName());
    };
    
    System.out.println("Submit the task to the executor service...");
    executorService.submit(runnable);
    // myWait(500);
    executorService.shutdown();
  }

  // test007:
  // Java concurrency API: ExecutorService
  // Taken from https://www.callicoder.com/java-executor-service-and-thread-pool-tutorial/
  // GitHub: https://github.com/callicoder/java-concurrency-examples
  public static void test007()
  {
    System.out.println("RxJavaTest05::test007");
    System.out.println("Inside : " + Thread.currentThread().getName());
    
    System.out.println("Create Executor service with a thread pool of size 2");
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    
    Runnable task1 = () -> {
      System.out.println("Executing Task1 inside : " + Thread.currentThread().getName());
      myWait(2000);
      System.out.println("Task1 exiting inside : " + Thread.currentThread().getName());
    };

    Runnable task2 = () -> {
      System.out.println("Executing Task2 inside : " + Thread.currentThread().getName());
      myWait(4000);
      System.out.println("Task2 exiting inside : " + Thread.currentThread().getName());
    };

    Runnable task3 = () -> {
      System.out.println("Executing Task3 inside : " + Thread.currentThread().getName());
      myWait(3000);
      System.out.println("Task3 exiting inside : " + Thread.currentThread().getName());
    };
    
    System.out.println("Submitting the tasks for execution...");
    executorService.submit(task1);
    executorService.submit(task2);
    executorService.submit(task3);
    
    executorService.shutdown();
  }

  // test008:
  // Java concurrency API: Callable
  // Taken from https://www.callicoder.com/java-executor-service-and-thread-pool-tutorial/
  // GitHub: https://github.com/callicoder/java-concurrency-examples
  public static void test008()
  {
    System.out.println("RxJavaTest05::test008");
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    
    Callable<String> callable = () -> {
      System.out.println("Entered Callable");
      myWait(2000);
      return "Hello from Callable";
    };
    
    System.out.println("Submitting Callable");
    Future<String> future = executorService.submit(callable);
    
    System.out.println("We submitted a Callable and got a Future");

    try {
      String result = future.get();
      System.out.println("Result : " + result);
    } catch (InterruptedException ex) {
      System.out.println("Caught InterruptedException");
    } catch (ExecutionException ex) {
      System.out.println("Caught ExecutionException");
    }
    
    executorService.shutdown();
  }

  // test009:
  // Java concurrency API: CompletableFuture
  // Taken from https://www.callicoder.com/java-8-completablefuture-tutorial/
  // GitHub: https://github.com/callicoder/java-concurrency-examples
  public static void test009()
  {
    System.out.println("RxJavaTest05::test009");
    CompletableFuture<String> whatsYourNameFuture = CompletableFuture.supplyAsync(() -> {
      myWait(1000);
      return "George";
    });
    
    CompletableFuture<String> greetingFuture = whatsYourNameFuture.thenApply(name -> {
      return "Hello " + name;
    });
    
    try {
      System.out.println(greetingFuture.get());
    } catch (Exception ex) {
      System.out.println("Caught Exception");
    }
  }

  // test010:
  // Java 8 streams
  // Taken from http://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html
  public static void test010()
  {
    System.out.println("RxJavaTest05::test010");
    List<Transaction> transactions = new ArrayList<Transaction>(Arrays.asList(
      new Transaction(Transaction.GROCERY, 1001),
      new Transaction(Transaction.BANKING, 1301),
      new Transaction(Transaction.GROCERY, 1401)
    ));
    List<Integer> ids = transactions.stream()
      .filter(t -> t.getType() == Transaction.GROCERY)
      .sorted(Comparator.comparing(Transaction::getValue).reversed())
      .map(Transaction::getId)
      .collect(Collectors.toList());
    for (Integer id : ids) {
      System.out.println("ID : " + id.toString());
    }
  }

  // test011:
  // Java 8 parallel streams
  // Taken from http://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html
  public static void test011()
  {
    System.out.println("RxJavaTest05::test011");
    List<Transaction> transactions = new ArrayList<Transaction>(Arrays.asList(
      new Transaction(Transaction.GROCERY, 1001),
      new Transaction(Transaction.BANKING, 1301),
      new Transaction(Transaction.GROCERY, 1401)
    ));
    List<Integer> ids = transactions.parallelStream()
      .filter(t -> t.getType() == Transaction.GROCERY)
      .sorted(Comparator.comparing(Transaction::getValue).reversed())
      .map(Transaction::getId)
      .collect(Collectors.toList());
    for (Integer id : ids) {
      System.out.println("ID : " + id.toString());
    }
  }

  // test012:
  // Java 8 streams and Optional
  // Taken from http://www.oracle.com/technetwork/articles/java/ma14-java-se-8-streams-2177646.html
  public static void test012()
  {
    System.out.println("RxJavaTest05::test012");
    List<Transaction> transactions = new ArrayList<Transaction>(Arrays.asList(
      new Transaction(Transaction.GROCERY, 1001),
      new Transaction(Transaction.BANKING, 1301),
      new Transaction(Transaction.GROCERY, 1401)
    ));
    Optional<Transaction> anyTransaction = transactions.stream()
      .filter(t -> t.getType() == Transaction.BANKING)
      .findAny();
    anyTransaction
      .ifPresent(t -> System.out.printf("Found : id=%d value=%d\n", t.getId(), t.getValue()));
  }

  private static final User user1 = new User(1, "John");
  private static final User user2 = new User(2, "George");
  
  public static User myFindById(long id)
  {
    System.out.println("Enter myFindById");
    myWait(1000);
    User user;
    if (id == 1) {
      user = user1;
    } else {
      user = user2;
    }
    System.out.println("Exit myFindById");
    return user2;
  }
  
  public static CompletableFuture<User> myFindByIdAsync(long id)
  {
    return CompletableFuture.supplyAsync(() -> myFindById(id));
  }
  
  public static GeoLocation myLocate(String flight)
  {
    System.out.println("Enter myLocate");
    myWait(1300);
    GeoLocation loc;
    if (flight.equals("Toronto")) {
      loc = new GeoLocation("Toronto");
    } else {
      loc = new GeoLocation("Montreal");
    }
    System.out.println("Exit myLocate");
    return loc;
  }
  
  public static CompletableFuture<GeoLocation> myLocateAsync(String flight)
  {
    return CompletableFuture.supplyAsync(() -> myLocate(flight));
  }
  
  public static CompletableFuture<String> myBookAsync(String flight)
  {
    return CompletableFuture.supplyAsync(() -> TravelAgency.myBook(flight));
  }
  
  // test013:
  // Java 8 streams and CompletableFuture
  public static void test013()
  {
    System.out.println("RxJavaTest05::test013");
    List<TravelAgency> agencies = Arrays.asList(new TravelAgency(), new TravelAgency());
    CompletableFuture<User> user = myFindByIdAsync(1);
    CompletableFuture<GeoLocation> location = myLocateAsync("flight-1");
    
    CompletableFuture<String> ticketFuture = user
      .thenCombine(location, (User us, GeoLocation loc) -> agencies
        .stream()
        .map(agency -> agency.mySearchAsync(us, loc))
        .reduce((f1, f2) ->
          f1.applyToEither(f2, Function.identity())
        )
        .get() // Needed because Stream.reduce returns Optional<String>.
      )
      .thenCompose(Function.identity())
      .thenCompose(RxJavaTest05::myBookAsync);
      
    ticketFuture
      .thenAccept(System.out::println);
    myWait(5000);
  }
  
  public static <T> Observable<T> observe(CompletableFuture<T> future)
  {
    return Observable.create(subscriber -> {
      future.whenComplete((value, exception) -> {
        if (exception != null) {
          subscriber.onError(exception);
        } else {
          subscriber.onNext(value);
          subscriber.onComplete();
        }
      });
    });
  }

  // findResource:
  // Used for constructing CompletableFuture in test014
  public static String findResource(long id) throws RuntimeException
  {
    System.out.println("Enter findResource");
    myWait(1000);
    if (id == 1) {
      System.out.println("Exit findResource");
      return "First resource";
    } else {
      System.out.println("findResource throws");
      throw new RuntimeException("No resource!");
    }
  }
  
  public static CompletableFuture<String> findResourceAsync(long id)
  {
    return CompletableFuture.supplyAsync(() -> findResource(id));
  }
  
  // test014:
  // Converting CompletableFuture to Observable
  public static void test014()
  {
    System.out.println("RxJavaTest05::test014");
    CompletableFuture<String> future1 = findResourceAsync(1);
    Observable<String> obs1 = observe(future1);
    obs1.subscribe(s -> System.out.println("obs1: got " + s));
    
    CompletableFuture<String> future2 = findResourceAsync(2);
    Observable<String> obs2 = observe(future2);
    obs2.subscribe(
      s -> System.out.println("obs2: got " + s),
      e -> e.printStackTrace(),
      () -> System.out.println("Done")
    );

    System.out.println("Waiting...");
    myWait(3000);
    System.out.println("Done waiting");
  }
  
  // toFuture
  // Convert Observable to CompletableFuture
  // Note: this is implemented as in the book (replacing single with singleOrError),
  // which throws an exception if observable emits more than one objects. For a
  // compile-time check, the parameter of toFuture should be declared as
  // Single<T> observable.
  public static <T> CompletableFuture<T> toFuture(Observable<T> observable)
  {
    CompletableFuture<T> promise = new CompletableFuture<>();
    observable
      .singleOrError()
      .subscribe(
        promise::complete,
        promise::completeExceptionally
      );
    return promise;
  }
  
  // toFutureList
  // Convert Observable<T> to CompletableFuture<List<T>>
  public static <T> CompletableFuture<List<T>> toFutureList(Observable<T> observable)
  {
    return toFuture(observable.toList().toObservable());
  }

  // test015:
  // Converting Observable to CompletableFuture
  public static void test015()
  {
    System.out.println("RxJavaTest05::test015");

    System.out.println("Creating Executor Service...");
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    Observable<String> observable = Observable
      .create(subscriber -> {
        executorService.submit(() -> {
          myWait(1000);
          subscriber.onNext("Hey!");
          subscriber.onComplete();
        });
      });
    
    CompletableFuture<String> promise = toFuture(observable);
    promise.thenAccept(s -> System.out.printf("Got string: %s\n", s));
    myWait(2000);
    executorService.shutdown();
  }

  // test016:
  // Converting Observable<T> to CompletableFuture<List<T>>
  public static void test016()
  {
    System.out.println("RxJavaTest05::test016");

    System.out.println("Creating Executor Service...");
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    Observable<String> observable = Observable
      .create(subscriber -> {
        executorService.submit(() -> {
          myWait(1000);
          subscriber.onNext("Hey!");
          myWait(1000);
          subscriber.onNext("You!");
          subscriber.onComplete();
        });
      });
    
    CompletableFuture<List<String>> promise = toFutureList(observable);
    promise.thenAccept(l -> {
      System.out.println("Got a list of string");
      for (String s : l) {
        System.out.println(s);
      }
    });
    myWait(2000);
    executorService.shutdown();
  }

  // test017:
  // Single
  public static void test017()
  {
    System.out.println("RxJavaTest05::test017");
    
    Single<String> single = Single.just("Hello, world!");
    single.subscribe(System.out::println);
    
    Single<Instant> error =
      Single.error(new RuntimeException("Oops!"));
    error
      .observeOn(Schedulers.io())
      .subscribe(
        System.out::println,
        (Throwable e) -> {
          System.out.println("Caught exception!");
          e.printStackTrace();
        }
      );
  }

  // test018:
  // AsyncHttpClient
  public static void test018()
  {
    System.out.println("RxJavaTest05::test018");
    AsyncHttpClient client = new AsyncHttpClient();
    Response response;
    try {
      response = client.prepareGet("https://www.yahoo.com")
        .execute()
        .get();
      System.out.printf("Response: %s\n", response.getResponseBody());
      String location = response.getHeader("Location");
      System.out.printf("Location: %s\n", location);
      int status = response.getStatusCode();
      System.out.printf("Status code: %d\n", status);
    } catch (Exception e) {
      System.out.println("Caught Exception");
      return;
    }
  }

  public static Single<Response> myFetch(String address)
  {
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    return Single.create(subscriber ->
      asyncHttpClient
        .prepareGet(address)
        .execute(myHandler(subscriber))
    );
  }
  
  public static AsyncHandler<Response> myHandler(SingleEmitter<? super Response> subscriber)
  {
    return new AsyncCompletionHandler<Response>() {
      public Response onCompleted(Response response)
      {
        try {
          //System.out.printf("onCompleted: response body=%s\n", response.getResponseBody());
          subscriber.onSuccess(response);
          return response;
        } catch (Exception e) {
          return null;
        }
      }
      
      public void onThrowable(Throwable t)
      {
        subscriber.onError(t);
      }
    };
  }
  
  // test019:
  // AsyncHttpClient
  public static void test019()
  {
    System.out.println("RxJavaTest05::test019");
    Single<String> result =
      myFetch("http://news.ycombinator.com")
        .map(response -> response.getResponseBody());
    result
      .subscribe(System.out::println);
    myWait(1000);
  }

  public static Single<String> myContent(int id)
  {
    return Single.fromCallable(() -> {
      System.out.println("myContent, before wait");
      myWait(1000);
      System.out.println("myContent, after wait");
      return "Hello";
    })
      .subscribeOn(Schedulers.io());
  }

  public static Single<Integer> myLikes(int id)
  {
    return Single.fromCallable(() -> {
      System.out.println("myLikes, before wait");
      myWait(800);
      System.out.println("myLikes, after wait");
      return 11;
    })
      .subscribeOn(Schedulers.io());
  }
  
  // myUpdateReadCount
  // Because we create Completable from Action, and it doesn't produce any value,
  // its subscriber is CompletableObserver, which implements onComplete().
  // Notice below how we convert this Completable to Single<Integer> by calling
  // toSingleDefault(0).
  // In the book, this function returns Single<Void>, but I couldn't get that to
  // work.
  public static Completable myUpdateReadCount()
  {
    return Completable.fromAction(() -> {
      System.out.println("myUpdateReadCount, before wait");
      myWait(800);
      System.out.println("myUpdateReadCount, after wait");
    })
      .subscribeOn(Schedulers.io());
  }

  // test020:
  // AsyncHttpClient
  public static void test020()
  {
    System.out.println("RxJavaTest05::test020");
    Single<String> doc = Single.zip(
      myContent(123),
      myLikes(123),
      myUpdateReadCount().toSingleDefault(0),
      (con, lks, vod) -> {
        return String.format("content for %s, likes: %d", con, lks);
      }
    );
    doc
      .subscribe((String s) -> System.out.printf("Got document %s\n", s));
    myWait(2000);
  }
}
