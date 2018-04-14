# RxJava Samples

Modified code samples from Reactive Programming with RxJava

This repository contains all the code through which I learned RxJava,
taken from the book [Reactive Programming with RxJava](http://shop.oreilly.com/product/0636920042228.do)
by Ben Christensen and Tomasz Nurkiewicz.

All the original samples in the book are writen for RxJava 1, but the
current latest version of RxJava is 2.

Note that I haven't typed in and translated all the samples from the book,
just most of the first 7 chapters.

## Build

I built and ran all the code with Java 8.

For building I used Ant, so to build and run everything, enter

  ant runrxjava

To clean up the build artifacts, enter

  ant clean

## Code structure

All code is in package com.vogonsoft.rxjava (vogonsoft.com is a domain
owned by me, so it is guaranteed to be unique).

Main class is Driver, which call test method in classes RxJavaTest02,
... to RxJavaTest07. Note that only the last code code that I ran is
uncommented. As I was going through the book, I had everything except
the current code commented-out, so when I run the code, I can observe
the behaviour I am learning at the moment.

## External Java packages used

Other than RxJava 2, I used:

* JUnit - unit-testing library
* AssertJ - library for fluent assertions (BDD)
* Mockito - mocking framework

## Changes for RxJava 2

I will list here some of the modifications for RxJava 2.

All the differences in version 2 are described in the page
[What's different in 2.0](https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0).

These are the most important changes I had to make to get the
book samples to work:

* onCompleted is now onComplete
* subscribe method now returns Disposable and in order to unsubscribe, we
  have to call dispose
* Setting a callback that will be called when the last subscriber
  unsubscribes is now done by calling setCancellable.
* Backpressure is now cleanly separated, so Observable knows nothing about it
  and Flowable is the class to use when we want to support backpressure.

One thing that I was not able to do is RxNetty, because at the time of my
reading RxNetty for RxJava 2 was still a work in progress. Hopefully soon
Netflix will have a usable version. I am writing this in April 2018, so by
the time you are reading this, it may well be out already.
