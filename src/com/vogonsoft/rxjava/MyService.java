package com.vogonsoft.rxjava;

import java.time.LocalDate;
import io.reactivex.*;

public interface MyService
{
  Observable<LocalDate> externalCall();
}
