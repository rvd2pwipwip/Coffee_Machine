package com.stingray.qello.firetv.android.async;

import java.util.concurrent.Callable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ObservableFactory {
    public <T> Observable<T> create(Callable<T> callable) {
        return Observable.fromCallable(callable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public <T> Observable<T> createDetached(Callable<T> callable) {
        return Observable.fromCallable(callable)
                .subscribeOn(Schedulers.io());
    }
}
