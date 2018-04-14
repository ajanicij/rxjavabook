package com.vogonsoft.rxjava;

import org.reactivestreams.*;
import io.reactivex.functions.*;

class SubscriberUtil
{
  public static <T> Subscriber<T> generateSubscriber(
    Consumer<? super T> _onNext,
    Action _onComplete,
    int requestSize)
  {
    return new Subscriber<T>() {
        private int left;
        private Subscription s;

        {{
          left = 1;
        }}
        
        @Override
        public void onSubscribe(Subscription s)
        {
          s.request(requestSize);
          left = requestSize;
          this.s = s;
        }
        
        @Override
        public void onNext(T t)
        {
          try {
            _onNext.accept(t);
          } catch (Exception e) {
            // Ignore.
          }
          left--;
          if (left == 0) {
            System.out.println("SubscriberUtil::generateSubscriber: request(" + requestSize + ")");
            s.request(requestSize);
            left = requestSize;
          }
        }
        
        @Override
        public void onComplete()
        {
          try {
            _onComplete.run();
          } catch (Exception e) {
            // Ignore.
          }
        }
        
        @Override
        public void onError(Throwable t)
        {
          t.printStackTrace();
        }
    };
  }
}
