package com.stingray.qello.firetv.android.async;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AsyncCaller {
    public <T> Observable<T> getForSubscribe(Callable<T> callable) {
        return Observable.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public <T> Observable<T> getForBlocking(Callable<T> callable) {
        return Observable.fromCallable(callable)
                .subscribeOn(Schedulers.io());
    }
}
