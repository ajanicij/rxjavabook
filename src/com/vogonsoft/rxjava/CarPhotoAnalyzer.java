package com.vogonsoft.rxjava;

import io.reactivex.*;
import io.reactivex.subjects.*;

public class CarPhotoAnalyzer
{
  public static void myWait(int ms)
  {
    try {
      Thread.sleep(ms);
    }
    catch (InterruptedException e) {
      System.out.println("Thread.sleep interrupted");
    }
  }

  public static Observable<LicencePlate> recognize(CarPhoto photo)
  {
    PublishSubject<LicencePlate> subject = PublishSubject.create();
    Runnable r = () -> {
      run(photo, subject);
    };
    new Thread(r).start();
    return subject;
  }
  
  private static void run(CarPhoto photo, Subject<LicencePlate> subject)
  {
    LicencePlate licence;
    System.out.printf("Analyzing photo: %s\n", photo);
    myWait(2000);
    if (photo.toString() == "abc.jpg") {
      licence = new LicencePlate("SEXXY");
    } else {
      licence = new LicencePlate("MYKAAR");
    }
    subject.onNext(licence);
    myWait(500);
    subject.onComplete();
  }
}
