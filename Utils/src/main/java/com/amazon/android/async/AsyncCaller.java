package com.amazon.android.async;

import android.util.Log;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observables.BlockingObservable;
import rx.schedulers.Schedulers;

public class AsyncCaller {
    public <T> Observable<T> getOnSubscribe(Callable<T> callable) {
        return Observable.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public <T> Observable<T> getForBlocking(Callable<T> callable) {
        return Observable.fromCallable(callable)
                .subscribeOn(Schedulers.io());
    }
}
