package com.vogonsoft.rxjava;

import io.reactivex.*;
import org.reactivestreams.*;
import io.reactivex.disposables.*;

// MyOperator<T>:
// Implements ObservableOperator<String, T>
// where String is downstream type and T is upstream type.
// This is a very basic operator that doesn't implement backpressure.
public final class MyOperator<T> implements ObservableOperator<String, T>
{
  
  @Override
  public Observer<? super T> apply(Observer<? super String> child)
  {
    return new Observer<T>()
    {
      final Observer<? super String> child_ = child;
      Disposable d;
      private boolean odd = true;
      
      @Override
      public void onComplete()
      {
        if (!d.isDisposed()) {
          d.dispose();
        }
      }
      
      @Override
      public void onError(Throwable e)
      {
        System.out.println("onError");
        if (!d.isDisposed()) {
          child_.onError(e);
        }
      }
      
      @Override
      public void onNext(T t)
      {
        if (odd && !d.isDisposed()) {
          child_.onNext(t.toString());
        }
        odd = !odd;
      }
      
      @Override
      public void onSubscribe(Disposable d)
      {
        this.d = d;
        child_.onSubscribe(d); // We call this so child can call dispose when it is
                               // unsubscribing.
      }
    };
  }
}
